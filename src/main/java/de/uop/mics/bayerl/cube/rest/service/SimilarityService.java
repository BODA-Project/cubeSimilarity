package de.uop.mics.bayerl.cube.rest.service;

import de.uop.mics.bayerl.cube.model.Cube;
import de.uop.mics.bayerl.cube.rest.repository.CubeRepository;
import de.uop.mics.bayerl.cube.similarity.MatrixAggregation;
import de.uop.mics.bayerl.cube.similarity.Metric;
import de.uop.mics.bayerl.cube.similarity.RankingItem;
import de.uop.mics.bayerl.cube.similarity.SimilarityUtil;
import de.uop.mics.bayerl.cube.similarity.matrix.ComputeComponentSimilarity;
import de.uop.mics.bayerl.cube.similarity.matrix.SimilarityMatrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by sebastianbayerl on 03/09/15.
 */
@Service
public class SimilarityService {

    @Autowired
    private CubeRepository cubeRepository;

    public RankingItem computeSimilarity(String cube1, String cube2, String metric, String matrixAggregation) {
        RankingItem rankingItem = new RankingItem();
        rankingItem.setMetric(metric);
        rankingItem.setSourceId(cube1);
        rankingItem.setTargetId(cube2);

        Metric m = Metric.valueOf(metric);
        ComputeComponentSimilarity computeComponentSimilarity = SimilarityUtil.getAlgorithmForMetric(m);

        Cube c1 = cubeRepository.getCube(rankingItem.getSourceId());
        Cube c2 = cubeRepository.getCube(rankingItem.getTargetId());
        SimilarityMatrix matrix = computeComponentSimilarity.computeMatrix(c1, c2);

        MatrixAggregation matrixAggr = MatrixAggregation.valueOf(matrixAggregation);
        SimilarityMatrix resultMatrix = SimilarityUtil.doMatrixAggregation(matrixAggr, matrix);
        rankingItem.setSimilarityMatrix(resultMatrix);

        // Simplify result
        for (int i = 0; i < resultMatrix.getMatrix().length; i++) {
            for (int j = 0; j < resultMatrix.getMatrix()[0].length; j++) {
                resultMatrix.getMatrix()[i][j] = (int) (100 * resultMatrix.getMatrix()[i][j]);
            }
        }

        resultMatrix.setSimilarity((int) (100 * resultMatrix.getSimilarity()));

        return rankingItem;
    }

    public List<RankingItem> computeRanking(String sourceId, String metric, String matrixAggrigation) {
        List<Cube> cubes = cubeRepository.getCubes();
        List<RankingItem> ranking = new ArrayList<>();

        for (Cube cube : cubes) {
            ranking.add(computeSimilarity(sourceId, cube.getId(), metric, matrixAggrigation));
        }

        // sort ranking
        ranking.sort((r1, r2) -> Double.compare(r1.getSimilarityMatrix().getSimilarity(), r2.getSimilarityMatrix().getSimilarity()));
        Collections.reverse(ranking);

        return ranking;
    }


}
