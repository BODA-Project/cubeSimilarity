package de.uop.mics.bayerl.cube.similarity.matrix;

/**
 * Created by sebastianbayerl on 29/07/15.
 */
public class SimilarityMatrix {

    // TODO make sure that rows >= cols
    private double[][] matrix;
    private int[] mapping;
    private double similarity;

    public SimilarityMatrix(int rows, int cols) {
        matrix = new double[rows][cols];
    }

    public double[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(double[][] matrix) {
        this.matrix = matrix;
    }


    public int[] getMapping() {
        return mapping;
    }

    public void setMapping(int[] mapping) {
        this.mapping = mapping;
    }

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }
}
