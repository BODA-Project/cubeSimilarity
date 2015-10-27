package de.uop.mics.bayerl.cube.similarity;

import de.uop.mics.bayerl.cube.similarity.matrix.SimilarityMatrix;

/**
 * Created by sebastianbayerl on 03/09/15.
 */
public class RankingItem {

    private String sourceId;
    private String targetId;
    private String metric;
    private SimilarityMatrix similarityMatrix;

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public SimilarityMatrix getSimilarityMatrix() {
        return similarityMatrix;
    }

    public void setSimilarityMatrix(SimilarityMatrix similarityMatrix) {
        this.similarityMatrix = similarityMatrix;
    }
}
