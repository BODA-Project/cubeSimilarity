package de.uop.mics.bayerl.cube.eval;

import de.uop.mics.bayerl.cube.model.Cube;
import de.uop.mics.bayerl.cube.provider.ReichstatisticsGroupedCubes;
import de.uop.mics.bayerl.cube.similarity.MatrixAggregation;
import de.uop.mics.bayerl.cube.similarity.Metric;
import de.uop.mics.bayerl.cube.similarity.RankingItem;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by sebastianbayerl on 15/12/15.
 */
public class EvaluationGermanReich {


    private static final String FILE = "output.txt";


    public static void main(String[] args) {
        evaluate();
    }



    private static void evaluate2() {
        List<Cube> cubes = ReichstatisticsGroupedCubes.loadCubes();

        List<Cube> queries = new ArrayList<>();
        queries.add(cubes.get(0));
        queries.add(cubes.get(1));
        queries.add(cubes.get(2));


        for (MatrixAggregation matrixAggregation : MatrixAggregation.values()) {
            for (Cube query : queries) {
                List<RankingItem> ranking = Evaluation.getRanking(query, cubes, Metric.CONCEPT_EQUALITY, MatrixAggregation.HUNGARIAN_ALGORITHM);
                // sort ranking
                ranking.sort((r1, r2) -> Double.compare(r1.getSimilarityMatrix().getSimilarity(), r2.getSimilarityMatrix().getSimilarity()));
                Collections.reverse(ranking);
                ranking.remove(0);

                List<String> lines = new ArrayList<>();
                int i = 0;
                for (RankingItem rankingItem : ranking) {
                    i++;
                    String line = query.getId() + " 0 " + rankingItem.getTargetId() + " " + i + " " + rankingItem.getSimilarityMatrix().getSimilarity() + " exp";
                    lines.add(line);
                    //System.out.println(line);
                }

                String file = FILE + "_" + query.getId() + "_" + matrixAggregation.name();
                try {
                    Files.write(Paths.get(file), lines, Charset.forName("UTF-8"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }




    private static void evaluate() {
        List<Cube> cubes = ReichstatisticsGroupedCubes.loadCubes();

       // System.out.println(cubes.size());


        List<Cube> queries = new ArrayList<>();
        queries.add(cubes.get(0));
        queries.add(cubes.get(3));
        queries.add(cubes.get(7));
        queries.add(cubes.get(9));


        List<Metric> metrics = new ArrayList<>();
        metrics.add(Metric.CONCEPT_EQUALITY);
        metrics.add(Metric.LABEL_SIMILARITY);
        metrics.add(Metric.DBPEDIA_CATEGORY);
        metrics.add(Metric.DBPEDIA_ENTITY);
//        metrics.add(Metric.WORD_2_VEC);


        for (Cube query : queries) {
            System.out.println(query.getId());
            for (Metric metric : metrics) {
            //    System.out.println(metric.name() + " ");
                for (MatrixAggregation matrixAggregation : MatrixAggregation.values()) {
                    //System.out.print(matrixAggregation.name() + " ");
                    List<RankingItem> ranking = Evaluation.getRanking(query, cubes, metric, matrixAggregation);
                    // sort ranking
                    ranking.sort((r1, r2) -> Double.compare(r1.getSimilarityMatrix().getSimilarity(), r2.getSimilarityMatrix().getSimilarity()));
                    Collections.reverse(ranking);
                    ranking.remove(0);

                    getValues(ranking);
                }

                System.out.println();
            }

        }


    }


    private static void getValues(List<RankingItem> ranking) {

        String group = ranking.get(0).getSourceId().split("-")[0];


        double correct = 0;
        for (int i = 1; i < 11; i++) {

            if (ranking.get(i - 1).getTargetId().split("-")[0].equals(group)) {
                correct++;
            }


            if (i == 1) {
                System.out.print(" & " + (int) correct);
            }

            if (i == 5 || i == 10) {
                System.out.print(" & " + ((int)(correct * 100 / (double) i)) / 100d);
            }


        }





    }

}
