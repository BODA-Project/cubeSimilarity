package de.uop.mics.bayerl.cube.eval.cluster;

import de.uop.mics.bayerl.cube.model.Cube;
import de.uop.mics.bayerl.cube.provider.Datahub;
import de.uop.mics.bayerl.cube.provider.ReichstatisticsGroupedCubes;
import de.uop.mics.bayerl.cube.provider.datahub.DatahubCubes;
import de.uop.mics.bayerl.cube.similarity.MatrixAggregation;
import de.uop.mics.bayerl.cube.similarity.Metric;
import de.uop.mics.bayerl.cube.similarity.RankingItem;
import de.uop.mics.bayerl.cube.validation.ValidStructure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sebastianbayerl on 18/07/16.
 */
public class ClusterMain {

    public static void main(String[] args) {
        CompareAll compareAll = new CompareAll();
         List<Cube> cubesReich = ReichstatisticsGroupedCubes.loadCubes();

        List<Cube> cubes = DatahubCubes.readCubes();
        List<Cube> validCubes = new ArrayList<>();


        for (Cube cube : cubesReich) {
            if (!ValidStructure.validate(cube)) {
                System.out.println("reich has invalid cube");
            }
        }
        
        int invalidCount = 0;

        for (Cube cube : cubes) {
            if (ValidStructure.validate(cube)) {
                validCubes.add(cube);
            } else {
                invalidCount++;
//                System.out.println("LOD has invalid cube");
            }
        }

        System.out.println(cubes.size());
        System.out.println("valid: " + validCubes.size());
        System.out.println("INVALID: " + invalidCount);

        String fileOut = "cluster/labelSim-hung.txt";
        Datahub.saveCreateFile(fileOut);
        int i = 0;
        for (Cube sourceCube : validCubes) {
            System.out.println(i);
            i++;
            List<RankingItem> ranking = compareAll.getRanking(sourceCube, validCubes, Metric.LABEL_SIMILARITY, MatrixAggregation.HUNGARIAN_ALGORITHM);
            persistRanking(ranking, fileOut);


        }
    }
    
    
    private static void persistRanking(List<RankingItem> ranking, String fileOut) {
        StringBuilder sb = new StringBuilder();
        for (RankingItem rankingItem : ranking) {
            sb.append(rankingItem.getSimilarityMatrix().getSimilarity());
            sb.append(" ");
        }

        try {
            Files.write(Paths.get(fileOut), (sb.toString() + "\n").getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        
}
