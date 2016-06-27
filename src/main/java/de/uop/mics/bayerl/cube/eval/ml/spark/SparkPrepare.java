package de.uop.mics.bayerl.cube.eval.ml.spark;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * Created by sebastianbayerl on 08/03/16.
 */
public class SparkPrepare {

    private static final String INPUT = "eval";
    protected static final String TARGET = "spark/input.txt";


    public static void main(String[] args) {
        prepare();
    }

    private static void prepare() {

        if (Files.exists(Paths.get(TARGET))) {
            try {
                Files.delete(Paths.get(TARGET));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Files.createFile(Paths.get(TARGET));
        } catch (IOException e) {
            e.printStackTrace();
        }

        long featureSize = 0;
        try {
            featureSize = Files.list(Paths.get(INPUT)).count();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final int finalFeatureSize = (int) featureSize;

        // load all feature data
        Map<Integer, Map<String, Double>> features = new HashMap<>();
        Set<String> availableIds = new HashSet<>();

        final int[] currentFeature = {0};
        try {
            Files.list(Paths.get(INPUT)).forEach(file -> {
                System.out.println("FILE: " + file.getFileName());
                Map<String, Double> feature = new HashMap<>();
                features.put(currentFeature[0], feature);
                currentFeature[0]++;

                try {
                    Files.lines(file).forEach(line -> {
                        if (!line.equals("")) {
                            String[] splits = line.split(" ");
                            String id = splits[0] + "#" + splits[2];
                            availableIds.add(id);
                            feature.put(id, Double.parseDouble(splits[4]));
                        } else {
                            System.out.println("empty line");
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Map<String, Integer> qidMap = new HashMap<>(); 

        // write in suitable format
        List<String> lines = new ArrayList<>();
        availableIds.forEach(id -> {
            StringBuilder sb = new StringBuilder();
            String[] idSplits = id.split("#");
            
            // compute qid
            String query = idSplits[0];
            
            if (!qidMap.containsKey(query)) {
                qidMap.put(query, qidMap.size() + 1);
            }
            

            String group1 = idSplits[0].substring(1, id.indexOf("-"));
            String group2 = idSplits[1].substring(1, id.indexOf("-"));

            if (group1.equals(group2)) {
                // 1 for spark and sofia
                // 2 for ranklib
                sb.append(2);
            } else {
                // -1 for sofia-ml
                // 0 for spark
                // 1 for ranklib
                sb.append(1);
            }
            
            sb.append(" qid:");
            sb.append(qidMap.get(query));

            for (int i = 0; i < finalFeatureSize; i++) {
                sb.append(" ");
                sb.append(i + 1);
                sb.append(":");
                sb.append(features.get(i).get(id));

                if (features.get(i).get(id) == null) {
                    System.out.println("id not found");
                }
            }
            sb.append(" ");
            lines.add(sb.toString());
        });

        try {
            Files.write(Paths.get(TARGET), lines, Charset.forName("UTF-8"), StandardOpenOption.WRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
