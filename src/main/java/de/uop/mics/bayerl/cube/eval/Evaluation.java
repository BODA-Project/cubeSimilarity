package de.uop.mics.bayerl.cube.eval;

import de.uop.mics.bayerl.cube.model.Cube;
import de.uop.mics.bayerl.cube.provider.wordsimilarity.WordSimilarityProvider;
import de.uop.mics.bayerl.cube.similarity.MatrixAggregation;
import de.uop.mics.bayerl.cube.similarity.Metric;
import de.uop.mics.bayerl.cube.similarity.RankingItem;
import de.uop.mics.bayerl.cube.similarity.SimilarityUtil;
import de.uop.mics.bayerl.cube.similarity.hierarchies.dbpedia.BfsSearch;
import de.uop.mics.bayerl.cube.similarity.matrix.ComputeComponentSimilarity;
import de.uop.mics.bayerl.cube.similarity.matrix.SimilarityMatrix;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by sebastianbayerl on 03/11/15.
 */
@Service
public class Evaluation {

    public static void main(String[] args) {
        Evaluation.getInstance().evaluateMetrics();
    }

    private final static String TESTSET = "movies";
    public final static String FOLDER = "evaluation/raw/" + TESTSET + "/";
    private final static String FILE_PREFIX = "eval-matrices-" + TESTSET + "-";
    private final static String FILE_SUFFIX = ".csv";

    private static Evaluation instance;

    public static Evaluation getInstance() {
        if (instance == null) {
            instance = new Evaluation();
        }
        return instance;
    }

    public void evaluateMetrics() {
        List<Cube> cubes = WordSimilarityProvider.getMovieCubes();
        MatrixAggregation ma = MatrixAggregation.SIMPLE;
        for (Metric m : Metric.values()) {
//        Metric m = Metric.DBPEDIA_CATEGORY;
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

            BfsSearch.getInstance().resetCaches();
            persistResult(allRankings, m, ma);
        }
    }

    public String getCurrentFileName(String folder, Metric m, MatrixAggregation ma) {
        return folder + FILE_PREFIX +  m.name().toLowerCase() + "-" + ma.name().toLowerCase() + FILE_SUFFIX;
    }

    private void persistResult(List<RankingItem> rankings, Metric m, MatrixAggregation ma) {

        String currentFile = getCurrentFileName(FOLDER, m , ma);

        try {
            Files.deleteIfExists(Paths.get(currentFile));
            Files.createFile(Paths.get(currentFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(currentFile, true)))) {
            for (RankingItem ranking : rankings) {
                StringBuilder sb = new StringBuilder();
                sb.append(ranking.getSourceId());
                sb.append(",");
                sb.append(ranking.getTargetId());
                sb.append(",");
                sb.append(ranking.getSimilarityMatrix().getSimilarity());
                out.println(sb.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<RankingItem> getRanking(Cube c1, List<Cube> cubes, Metric m, MatrixAggregation ma) {
        return cubes.stream().map(c2 -> getSimilarity(c1, c2, m, ma)).collect(Collectors.toList());
    }


    public RankingItem getSimilarity(Cube c1, Cube c2, Metric m, MatrixAggregation ma) {
        // System.out.println("getSim " + c1.getId() +  "   " + c2.getId());
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
