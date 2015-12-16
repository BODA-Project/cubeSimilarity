package de.uop.mics.bayerl.cube.similarity;

import de.uop.mics.bayerl.cube.similarity.hierarchies.dbpedia.DBPediaProperty;
import de.uop.mics.bayerl.cube.similarity.matrix.*;
import de.uop.mics.bayerl.cube.similarity.string.DistanceAlgorithm;

/**
 * Created by sebastianbayerl on 03/11/15.
 */
public class SimilarityUtil {

    public static ComputeComponentSimilarity getAlgorithmForMetric(Metric m) {
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

        return computeComponentSimilarity;
    }


    public static SimilarityMatrix doMatrixAggregation(MatrixAggregation matrixAggr, SimilarityMatrix matrix) {
        SimilarityMatrix resultMatrix = null;
        if (matrixAggr == MatrixAggregation.SIMPLE) {
            resultMatrix = MatrixUtil.useSimpleSimilarity(matrix, false);
        } else if (matrixAggr == MatrixAggregation.SIMPLE_NORMALIZE) {
            resultMatrix = MatrixUtil.useSimpleSimilarity(matrix, true);
//        } else if (matrixAggr == MatrixAggregation.HEURISTIC) {
//            resultMatrix = MatrixUtil.useHeuristicSimilarity(matrix);
        } else if (matrixAggr == MatrixAggregation.HUNGARIAN_ALGORITHM) {
            resultMatrix = MatrixUtil.useHungarianAlgorithm(matrix);
        } else if (matrixAggr == MatrixAggregation.WEIGHTED) {
            resultMatrix = MatrixUtil.useWeightedAlgorithm(matrix);
        }

        return resultMatrix;
    }


}
