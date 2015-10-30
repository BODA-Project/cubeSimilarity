package de.uop.mics.bayerl.cube.rest.service;

import de.uop.mics.bayerl.cube.model.Cube;
import de.uop.mics.bayerl.cube.rest.repository.CubeRepository;
import de.uop.mics.bayerl.cube.similarity.MatrixAggregation;
import de.uop.mics.bayerl.cube.similarity.Metric;
import de.uop.mics.bayerl.cube.similarity.RankingItem;
import de.uop.mics.bayerl.cube.similarity.hierarchies.dbpedia.DBPediaProperty;
import de.uop.mics.bayerl.cube.similarity.matrix.*;
import de.uop.mics.bayerl.cube.similarity.string.DistanceAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by sebastianbayerl on 03/09/15.
 */
@Service
public class CubeService {

    @Autowired
    private CubeRepository cubeRepository;

    private Metric getMetric(String metric) {
        return Metric.valueOf(metric);


    }

    public RankingItem computeSimilarity(String cube1, String cube2, String metric, String matrixAggregation) {
        RankingItem rankingItem = new RankingItem();
        rankingItem.setMetric(metric);
        rankingItem.setSourceId(cube1);
        rankingItem.setTargetId(cube2);

        Metric m = Metric.valueOf(metric);
        ComputeComponentSimilarity computeComponentSimilarity = null;

        if (m == Metric.CONCEPT_EQUALITY) {
            computeComponentSimilarity = new EqualConcepts();
        } else if (m == Metric.LABEL_SIMILARITY) {
            computeComponentSimilarity = new LabelSimilarity(DistanceAlgorithm.JARO_WINKLER);
        } else if (m == Metric.LABEL_EQUALITY) {
            computeComponentSimilarity = new EqualLabels();
        } else if (m == Metric.CONCEPT_EQUALITY_SAMEAS) {
            computeComponentSimilarity = new SameAsExtended();
        } else if (m == Metric.DBPEDIA_CATEGORY) {
            computeComponentSimilarity = new BfsSimilarity(DBPediaProperty.BROADER);
        } else if (m == Metric.DBPEDIA_ENTITY) {
            computeComponentSimilarity = new BfsSimilarity(DBPediaProperty.PAGE_LINK);
        } else if (m == Metric.WORD_2_VEC) {
            computeComponentSimilarity = new Word2Vec();
        }

        Cube c1 = cubeRepository.getCube(rankingItem.getSourceId());
        Cube c2 = cubeRepository.getCube(rankingItem.getTargetId());
        SimilarityMatrix matrix = computeComponentSimilarity.computeMatrix(c1, c2);


        MatrixAggregation matrixAggr = MatrixAggregation.valueOf(matrixAggregation);
        SimilarityMatrix resultMatrix = null;
        if (matrixAggr == MatrixAggregation.SIMPLE) {
            resultMatrix = MatrixUtil.useSimpleSimilarity(matrix, false);
        } else if (matrixAggr == MatrixAggregation.SIMPLE_NORMALIZE) {
            resultMatrix = MatrixUtil.useSimpleSimilarity(matrix, true);
        } else if (matrixAggr == MatrixAggregation.HEURISTIC) {
            resultMatrix = MatrixUtil.useHeuristicSimilarity(matrix);
        } else if (matrixAggr == MatrixAggregation.HUNGARIAN_ALGORITHM) {
            resultMatrix = MatrixUtil.useHungarianAlgorithm(matrix);
        } else if (matrixAggr == MatrixAggregation.WEIGHTED) {
            resultMatrix = MatrixUtil.useWeightedAlgorithm(matrix);
        }

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
