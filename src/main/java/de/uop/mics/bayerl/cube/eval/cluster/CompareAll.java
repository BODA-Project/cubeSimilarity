package de.uop.mics.bayerl.cube.eval.cluster;

import de.uop.mics.bayerl.cube.model.Cube;
import de.uop.mics.bayerl.cube.similarity.MatrixAggregation;
import de.uop.mics.bayerl.cube.similarity.Metric;
import de.uop.mics.bayerl.cube.similarity.RankingItem;
import de.uop.mics.bayerl.cube.similarity.SimilarityUtil;
import de.uop.mics.bayerl.cube.similarity.matrix.ComputeComponentSimilarity;
import de.uop.mics.bayerl.cube.similarity.matrix.SimilarityMatrix;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by sebastianbayerl on 18/07/16.
 */
public class CompareAll {

    public List<RankingItem> getRanking(Cube c1, List<Cube> cubes, Metric m, MatrixAggregation ma) {
        // parallel stream + Collectors.toList() preserves the order
        return cubes.parallelStream().map(c2 -> getSimilarity(c1, c2, m, ma)).collect(Collectors.toList());
    }


    @SuppressWarnings("Duplicates")
    private RankingItem getSimilarity(Cube c1, Cube c2, Metric m, MatrixAggregation ma) {
        ComputeComponentSimilarity computeComponentSimilarity = SimilarityUtil.getAlgorithmForMetric(m);

        SimilarityMatrix matrix = computeComponentSimilarity.computeMatrix(c1, c2);
        SimilarityMatrix resultMatrix = SimilarityUtil.doMatrixAggregation(ma, matrix);

        RankingItem ra = new RankingItem();
        ra.setMetric(m.name());
        ra.setSourceId(c1.getId());
        ra.setTargetId(c2.getId());
        ra.setSimilarityMatrix(resultMatrix);
        
        if (ra.getSimilarityMatrix().getSimilarity() > 0.9) {
            if (!ra.getSourceId().equals(ra.getTargetId())) {
//                System.out.println("### " + ra.getSimilarityMatrix().getSimilarity());
            }
            
        }

        return ra;
    }
    
    
    
}
