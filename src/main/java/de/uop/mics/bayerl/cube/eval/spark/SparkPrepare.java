package de.uop.mics.bayerl.cube.eval.spark;

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
    protected static final String TARGET = "spark/input_multi.txt";


    public static void main(String[] args) {

        prepare();


    }

    private static void prepare() {
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
                Map<String, Double> feature = new HashMap<>();
                features.put(currentFeature[0], feature);
                currentFeature[0]++;

                try {
                    Files.lines(file).forEach(line -> {
                        String[] splits = line.split(" ");
                        String id = splits[0] + splits[2];
                        availableIds.add(id);
                        feature.put(id, Double.parseDouble(splits[4]));
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        // write in suitable format
        List<String> lines = new ArrayList<>();
        availableIds.forEach(id -> {
            StringBuilder sb = new StringBuilder();
            sb.append(id.substring(1, id.indexOf("-")));

            for (int i = 0; i < finalFeatureSize; i++) {
                sb.append(" ");
                sb.append(i + 1);
                sb.append(":");
                sb.append(features.get(i).get(id));

                if (features.get(i).get(id) == null) {
                    System.out.println("id not found");
                }
            }
            lines.add(sb.toString());
        });


        try {
            Files.write(Paths.get(TARGET), lines, Charset.forName("UTF-8"), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}