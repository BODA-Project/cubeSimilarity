package de.uop.mics.bayerl.cube.eval;

import de.uop.mics.bayerl.cube.model.Cube;
import de.uop.mics.bayerl.cube.model.Dimension;
import de.uop.mics.bayerl.cube.model.Measure;
import de.uop.mics.bayerl.cube.provider.ReichstatisticsGroupedCubes;
import de.uop.mics.bayerl.cube.provider.datahub.DatahubCubes;
import de.uop.mics.bayerl.cube.similarity.MatrixAggregation;
import de.uop.mics.bayerl.cube.similarity.Metric;
import de.uop.mics.bayerl.cube.similarity.RankingItem;

import java.util.Collections;
import java.util.List;

/**
 * Created by sebastianbayerl on 13/12/15.
 */
public class EvaluateDatahub {

    public static void main(String[] args) {
        evaluate();
    }


    private static void evaluateVsGerman() {
        String id = "g1-c8";

        List<Cube> cubes = ReichstatisticsGroupedCubes.loadCubes();
        Cube query = null;
        for (Cube cube : cubes) {
            if (cube.getId().equals(id)) {
                query = cube;
                break;
            }

        }


        List<Cube> targets = DatahubCubes.readCubes();
        List<RankingItem> ranking = Evaluation.getInstance().getRanking(query, targets, Metric.LABEL_SIMILARITY, MatrixAggregation.SIMPLE);

        ranking.sort((r1, r2) -> Double.compare(r1.getSimilarityMatrix().getSimilarity(), r2.getSimilarityMatrix().getSimilarity()));
        Collections.reverse(ranking);

//        for (RankingItem rankingItem : ranking) {
//            System.out.println(rankingItem.getSimilarityMatrix().getSimilarity() + "    " + rankingItem.getSourceId() + "   " + rankingItem.getTargetId());
//        }


        System.out.println(ranking.size());
        ranking = ranking.subList(0, 10);

        for (RankingItem rankingItem : ranking) {
            for (Cube cube : targets) {
                if (cube.getId().equals(rankingItem.getTargetId())) {
                    System.out.println(rankingItem.getSimilarityMatrix().getSimilarity() + "    " + rankingItem.getSourceId() + "   " + rankingItem.getTargetId());
                    printCube(cube);
                    System.out.println("-------------------");
                }
            }
        }

    }


    private static void evaluate() {
        List<Cube> cubes = DatahubCubes.readCubes();


//        for (Cube cube : cubes) {
//            if (!cube.getSparqlEndpoint().getEndpoint().startsWith("http://oecd.270a.info/sparql")) {
//                System.out.println(cube.getId() + "  " + cube.getSparqlEndpoint().getEndpoint());
//
//                for (Dimension dimension : cube.getStructureDefinition().getDimensions()) {
//                    System.out.println(dimension.getConcept() + "   " + dimension.getLabel());
//                }
//
//            }
//
//
//        }

        Cube query = null;
        for (Cube cube : cubes) {
            if (cube.getId().equals("bd312b15-bae2-4913-afad-c44758d95943")) {
                query = cube;
                break;
            }
        }

        String endpoint = query.getSparqlEndpoint().getEndpoint();


        List<RankingItem> ranking = Evaluation.getInstance().getRanking(query, cubes, Metric.CONCEPT_EQUALITY, MatrixAggregation.WEIGHTED);

        // sort ranking
        ranking.sort((r1, r2) -> Double.compare(r1.getSimilarityMatrix().getSimilarity(), r2.getSimilarityMatrix().getSimilarity()));
        Collections.reverse(ranking);

//        for (RankingItem rankingItem : ranking) {
//            System.out.println(rankingItem.getSimilarityMatrix().getSimilarity() + "    " + rankingItem.getSourceId() + "   " + rankingItem.getTargetId());
//        }

        ranking = ranking.subList(0, 10);


        for (RankingItem rankingItem : ranking) {
            for (Cube cube : cubes) {
                if (cube.getId().equals(rankingItem.getTargetId())) {
                    System.out.println(rankingItem.getSimilarityMatrix().getSimilarity() + "    " + rankingItem.getSourceId() + "   " + rankingItem.getTargetId());
                    printCube(cube);
                    System.out.println("-------------------");
                }
            }
        }


    }


    private final static void printCube(Cube cube) {
        System.out.println(cube.getId() + "  " + cube.getSparqlEndpoint().getEndpoint() + "  " + cube.getStructureDefinition().getConcept());

        for (Dimension dimension : cube.getStructureDefinition().getDimensions()) {
            System.out.println(dimension.getConcept() + "   " + dimension.getLabel());
        }
        System.out.println("-");
        for (Measure measure : cube.getStructureDefinition().getMeasures()) {
            System.out.println(measure.getConcept() + "   " + measure.getLabel());
        }
    }


}
