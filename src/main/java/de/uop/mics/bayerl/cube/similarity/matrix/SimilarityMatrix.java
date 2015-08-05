package de.uop.mics.bayerl.cube.similarity.matrix;

/**
 * Created by sebastianbayerl on 29/07/15.
 */
public class SimilarityMatrix {


    private double[][] matrix;


    public SimilarityMatrix(int rows, int cols) {
        matrix = new double[rows][cols];
    }

    public void setValue(int row, int col, double value) {
        matrix[row][col] = value;
    }

    public double getValue(int row, int col) {
        return matrix[row][col];
    }

    public void setRow(int row, double[] rowValues) {
        matrix[row] = rowValues;
    }

    public double[] getRow(int row) {
        return matrix[row];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < matrix.length; i++) {

            for (int j = 0; j < matrix[0].length; j++) {
                sb.append(matrix[i][j]);
                sb.append("   ");
            }

            sb.append("\n");
        }

        return sb.toString();
    }

    public double getSimilarity() {
        // TODO do this right
        double sim = 0;

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] > 0) {
                    sim += matrix[i][j];
                }
            }
        }



        return sim;
    }


}
