package de.uop.mics.bayerl.cube.evaluation;

import de.uop.mics.bayerl.cube.model.Cube;
import de.uop.mics.bayerl.cube.provider.wordsimilarity.WordSimilarityProvider;
import de.uop.mics.bayerl.cube.similarity.MatrixAggregation;
import de.uop.mics.bayerl.cube.similarity.Metric;
import de.uop.mics.bayerl.cube.similarity.RankingItem;
import de.uop.mics.bayerl.cube.similarity.SimilarityUtil;
import de.uop.mics.bayerl.cube.similarity.matrix.ComputeComponentSimilarity;
import de.uop.mics.bayerl.cube.similarity.matrix.SimilarityMatrix;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by sebastianbayerl on 03/11/15.
 */
@Service
public class Evaluation {

    public static void main(String[] args) {
        evaluateMetrics();
    }


    private final static String FOLDER = "evaluation/";
    private final static String FILE_PREFIX = "eval-matrics-wordsim-";
    private final static String FILE_SUFFIX = ".csv";

    public static void evaluateMetrics() {
        List<Cube> cubes = WordSimilarityProvider.getCubes();
        MatrixAggregation ma = MatrixAggregation.SIMPLE;
        for (Metric m : Metric.values()) {
            List<RankingItem> allRankings = new ArrayList<>();
            System.out.println("metric: " + m.name());
            int i = 0;
            for (Cube c1 : cubes) {
                i++;

                if (m == Metric.DBPEDIA_CATEGORY || m == Metric.DBPEDIA_ENTITY || m == Metric.WORD_2_VEC) {
                    System.out.println("ranking " + i + "/" + cubes.size() + ": " +  c1.getDescription());
                }

                List<RankingItem> ranking = getRanking(c1, cubes, m, ma);
                allRankings.addAll(ranking);
            }

            persistResult(allRankings, m, ma);
        }
    }

    private static void persistResult(List<RankingItem> rankings, Metric m, MatrixAggregation ma) {

        String currentFile = FOLDER + FILE_PREFIX +  m.name().toLowerCase() + "-" + ma.name().toLowerCase() + FILE_SUFFIX;

        try {
            Files.deleteIfExists(Paths.get(currentFile));
            Files.createFile(Paths.get(currentFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (RankingItem ranking : rankings) {
            String line = ranking.getSourceId() + "," + ranking.getTargetId() + "," + ranking.getSimilarityMatrix().getSimilarity() + "\n";
            try {
                Files.write(Paths.get(currentFile), line.getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static List<RankingItem> getRanking(Cube c1, List<Cube> cubes, Metric m, MatrixAggregation ma) {
        return cubes.stream().map(c2 -> getSimilarity(c1, c2, m, ma)).collect(Collectors.toList());
    }


    private static RankingItem getSimilarity(Cube c1, Cube c2, Metric m, MatrixAggregation ma) {
        ComputeComponentSimilarity computeComponentSimilarity = SimilarityUtil.getAlgorithmForMetric(m);
        SimilarityMatrix matrix = computeComponentSimilarity.computeMatrix(c1, c2);
        SimilarityMatrix resultMatrix = SimilarityUtil.doMatrixAggregation(ma, matrix);

        RankingItem ra = new RankingItem();
        ra.setMetric(m.name());
        ra.setSourceId(c1.getId());
        ra.setTargetId(c2.getId());
        ra.setSimilarityMatrix(resultMatrix);

        return ra;
    }

}
