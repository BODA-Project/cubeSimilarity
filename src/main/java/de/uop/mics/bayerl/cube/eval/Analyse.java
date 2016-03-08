package de.uop.mics.bayerl.cube.eval;

import de.uop.mics.bayerl.cube.provider.wordsimilarity.WordSimHelper;
import de.uop.mics.bayerl.cube.similarity.MatrixAggregation;
import de.uop.mics.bayerl.cube.similarity.Metric;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by sebastianbayerl on 04/11/15.
 */
public class Analyse {

    private static final String FOLDER = "evaluation/processed/";

    public static void main(String[] args) {

        getData();

    }

    private static void getData() {
        Metric m = Metric.WORD_2_VEC;
        MatrixAggregation ma = MatrixAggregation.SIMPLE;

        String truthFilename = WordSimHelper.PATH_TARGET_MOVIES;
        String currentFilename =  Evaluation.getInstance().getCurrentFileName(Evaluation.FOLDER, m , ma);


        Map<String, Double> truth = extractMap(truthFilename);
        Map<String, Double> current = extractMap(currentFilename);

//        persist(m, ma, truth, current);

        List<String> keys = new ArrayList<>(truth.keySet());
        Collections.sort(keys);

        for (String key : keys) {
            System.out.println(current.get(key));
        }
    }

    private static void persist(Metric m, MatrixAggregation ma, Map<String, Double> truth, Map<String, Double> current) {
        String targetFile = Evaluation.getInstance().getCurrentFileName(FOLDER, m, ma);
        List<String> keys = new ArrayList<>(truth.keySet());
        Collections.sort(keys);

        try {
            Files.deleteIfExists(Paths.get(targetFile));
            Files.createFile(Paths.get(targetFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(targetFile, true)))) {
            for (String key : keys) {
                out.println(key + "," + current.get(key));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Double> extractMap(String file) {
        Map<String, Double> result = new HashMap<>();
        try {
            Files.lines(Paths.get(file)).forEach(s -> {
                String[] splits = s.split(",");
                result.put(splits[0] + "," + splits[1], Double.parseDouble(splits[2]));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

}
