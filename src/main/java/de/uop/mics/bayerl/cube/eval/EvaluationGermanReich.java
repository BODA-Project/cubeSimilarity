package de.uop.mics.bayerl.cube.eval;

import com.google.common.base.Stopwatch;
import de.uop.mics.bayerl.cube.model.Cube;
import de.uop.mics.bayerl.cube.model.Dimension;
import de.uop.mics.bayerl.cube.model.Measure;
import de.uop.mics.bayerl.cube.provider.ReichstatisticsGroupedCubes;
import de.uop.mics.bayerl.cube.similarity.MatrixAggregation;
import de.uop.mics.bayerl.cube.similarity.Metric;
import de.uop.mics.bayerl.cube.similarity.RankingItem;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by sebastianbayerl on 15/12/15.
 */
public class EvaluationGermanReich {


    private static final String FOLDER = "eval/";
    private static final String FILE = "output.txt";


    public static void main(String[] args) {

        Stopwatch stopwatch = Stopwatch.createStarted();
        evaluate2();
        System.out.println(stopwatch.elapsed(TimeUnit.SECONDS) + "[s]");

       // sample();
    }


    private final static void print() {

        String id = "g1-c8";

        List<Cube> cubes = ReichstatisticsGroupedCubes.loadCubes();

        for (Cube cube : cubes) {
            if (cube.getId().equals(id)) {
                printCube(cube);

                break;
            }

        }

    }

    private final static void printCube(Cube cube) {
        String prefix = "http://dbpedia.org/resource/";
        //System.out.println(cube.getId() + "  " + cube.getSparqlEndpoint().getEndpoint());

        System.out.println(cube.getId() + "   " + cube.getDescription() + "    " + cube.getLabel());

        for (Dimension dimension : cube.getStructureDefinition().getDimensions()) {
           // System.out.println(dimension.getConcept() + "   " + dimension.getLabel());
            System.out.print(", " + dimension.getLabel());

        }
        System.out.println();
        for (Measure measure : cube.getStructureDefinition().getMeasures()) {
            //System.out.println(measure.getConcept() + "   " + measure.getLabel());
            System.out.print(", " + measure.getLabel());

        }
    }



    private static void evaluate2() {
        List<Cube> cubes = ReichstatisticsGroupedCubes.loadCubes();

//        List<Cube> queries = new ArrayList<>();
//        queries.add(cubes.get(0));
//        queries.add(cubes.get(1));
//        queries.add(cubes.get(2));
//        queries.add(cubes.get(3));
//        queries.add(cubes.get(4));



        // use all cubes as queries
        List<Cube> queries = cubes;

        List<Metric> metrics = new ArrayList<>();
//        metrics.add(Metric.CONCEPT_EQUALITY);
//        metrics.add(Metric.LABEL_SIMILARITY);
        metrics.add(Metric.DBPEDIA_CATEGORY);
//        metrics.add(Metric.DBPEDIA_ENTITY);
//        metrics.add(Metric.WORD_2_VEC);




        for (Metric metric : metrics) {

            for (MatrixAggregation matrixAggregation : MatrixAggregation.values()) {
//            Arrays.stream(MatrixAggregation.values()).parallel().forEach(matrixAggregation -> {
                for (Cube query : queries) {

                    System.out.println(query.getLabel());
                    List<RankingItem> ranking = Evaluation.getInstance().getRanking(query, cubes, metric, matrixAggregation);


                    // sort ranking
//                    ranking.sort((r1, r2) -> Double.compare(r1.getSimilarityMatrix().getSimilarity(), r2.getSimilarityMatrix().getSimilarity()));
//                    Collections.reverse(ranking);
//                    ranking.remove(0);


                    // filter self comparison
                    ranking.stream().filter(item -> !item.getSourceId().equals(item.getTargetId()));



                    List<String> lines = new ArrayList<>();
                    int i = 0;
                    for (RankingItem rankingItem : ranking) {
                        i++;
                        String line = query.getId() + " 0 " + rankingItem.getTargetId() + " " + i + " " + rankingItem.getSimilarityMatrix().getSimilarity() + " exp";
                        lines.add(line);
                        //System.out.println(line);
                    }

                    String file = FOLDER + metric.name().toLowerCase() + "___" + matrixAggregation.name().toLowerCase() + "_" + FILE;

                    if (!Files.exists(Paths.get(file))) {
                        try {
                            Files.createFile(Paths.get(file));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        Files.write(Paths.get(file), lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
//            });
            }

        }
    }


    private static void sample() {

        String id = "g1-c4";

        List<Cube> cubes = ReichstatisticsGroupedCubes.loadCubes();
        Cube query = null;
        for (Cube cube : cubes) {
            if (cube.getId().equals(id)) {
                query = cube;
                break;
            }

        }


        List<RankingItem> ranking = Evaluation.getInstance().getRanking(query, cubes, Metric.LABEL_SIMILARITY, MatrixAggregation.HUNGARIAN_ALGORITHM);
        // sort ranking
        ranking.sort((r1, r2) -> Double.compare(r1.getSimilarityMatrix().getSimilarity(), r2.getSimilarityMatrix().getSimilarity()));
        Collections.reverse(ranking);
//        ranking.remove(0);
        ranking = ranking.subList(0, 11);

        for (RankingItem rankingItem : ranking) {
            Cube target = null;
            for (Cube cube : cubes) {
                if (cube.getId().equals(rankingItem.getTargetId())) {
                    target = cube;
                    break;
                }

            }



            double sim = (int)(rankingItem.getSimilarityMatrix().getSimilarity() * 100) / 100d;
            System.out.println(rankingItem.getTargetId() + "   " + sim + "   " + target.getLabel());
        }


    }




    private static void evaluate() {
        List<Cube> cubes = ReichstatisticsGroupedCubes.loadCubes();

       // System.out.println(cubes.size());

        List<Cube> queries = new ArrayList<>();
        queries.add(cubes.get(0));
        queries.add(cubes.get(3));
        queries.add(cubes.get(7));
//        queries.add(cubes.get(9));

        List<Metric> metrics = new ArrayList<>();
        metrics.add(Metric.CONCEPT_EQUALITY);
//        metrics.add(Metric.LABEL_SIMILARITY);
//        metrics.add(Metric.DBPEDIA_CATEGORY);
//        metrics.add(Metric.DBPEDIA_ENTITY);
//        metrics.add(Metric.WORD_2_VEC);

        for (Cube query : queries) {
            System.out.println(query.getId());
            for (Metric metric : metrics) {
            //    System.out.println(metric.name() + " ");
                for (MatrixAggregation matrixAggregation : MatrixAggregation.values()) {
                    //System.out.print(matrixAggregation.name() + " ");
                    List<RankingItem> ranking = Evaluation.getInstance().getRanking(query, cubes, metric, matrixAggregation);
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
