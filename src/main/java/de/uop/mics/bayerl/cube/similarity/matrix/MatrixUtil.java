package de.uop.mics.bayerl.cube.similarity.matrix;

/**
 * Created by sebastianbayerl on 30/07/15.
 */
public class MatrixUtil {

    private static double THRESHOLD = 0.7;
    private static double ALPHA = 0.5;

    // TODO implement: merge weighted matrices

    public static double[][] getMax(double[][] a, double[][] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("a.length != b.length");
        }

        if (a[0].length != b[0].length) {
            throw new IllegalArgumentException("a[0].length != b[0].length");
        }

        double[][] max = new double[a.length][a[0].length];

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                max[i][j] = Math.max(a[i][j], b[i][j]);
            }
        }

        return max;
    }

    public static String getAsString(double[][] matrix) {
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

    public static SimilarityMatrix useWeightedAlgorithm(SimilarityMatrix similarityMatrix) {
        // TODO add sims that are above a threshold?
        int count1 = 0;
        double addSim = 0.0;
//        double maxSim = 0.0;
        //int count0 = 0;

        for (int i = 0; i < similarityMatrix.getMatrix().length; i++) {
            for (int j = 0; j < similarityMatrix.getMatrix()[0].length; j++) {
                double current = similarityMatrix.getMatrix()[i][j];
                if (current == 1.0) {
                    count1++;
//                } else if (current == 0.0) {
//                    count0++;
                } else if (current >= THRESHOLD) {
                    addSim += ALPHA * current;
                }
            }
        }

        double similarity = count1 + addSim;
        similarityMatrix.setSimilarity(similarity);

        return similarityMatrix;
    }

    public static SimilarityMatrix useHungarianAlgorithm(SimilarityMatrix similarityMatrix) {

        double[][] temp = new double[similarityMatrix.getMatrix().length][similarityMatrix.getMatrix()[0].length];

        for (int i = 0; i < similarityMatrix.getMatrix().length; i++) {
            for (int j = 0; j < similarityMatrix.getMatrix()[0].length; j++) {
                temp[i][j] = 1 - similarityMatrix.getMatrix()[i][j];
            }
        }

        HungarianAlgorithm hungarianAlgorithm = new HungarianAlgorithm(temp);
        int[] mapping = hungarianAlgorithm.execute();

        double sum = 0;
        double mapped = 0;
        for (int i = 0; i < mapping.length; i++) {
            if (mapping[i] != -1) {
                sum += similarityMatrix.getMatrix()[i][mapping[i]];
                mapped++;
            }
        }

        double similarity = sum / mapped;

        similarityMatrix.setMapping(mapping);
        similarityMatrix.setSimilarity(similarity);

        return similarityMatrix;
    }

    public static SimilarityMatrix useSimpleSimilarity(SimilarityMatrix simMatrix, boolean normalize) {
        double[][] matrix = simMatrix.getMatrix();
        double sum = 0d;

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                sum += matrix[i][j];
            }
        }

        double similarity = sum;
        if (normalize) {
            similarity = sum / (double) (matrix.length * matrix[0].length);
        }

        simMatrix.setSimilarity(similarity);
        return simMatrix;
    }

    public static SimilarityMatrix useHeuristicSimilarity(SimilarityMatrix simMatrix) {
        double[][] matrix = simMatrix.getMatrix();
        double sim = 0d;
        double[][] temp = matrix.clone();
        double[] sims = new double[temp.length];

        // compute max similarities
        for (int run = 0; run < temp.length; run++) {
            int r = 0;
            int c = 0;
            double max = 0;

            for (int i = 0; i < temp.length; i++) {
                for (int j = 0; j < temp[0].length; j++) {
                    if (temp[i][j] > max) {
                        max = temp[i][j];
                        r = i;
                        c = j;
                    }
                }
            }

            // reset matrix rows and cols
            if (max == 0) {
                break;
            } else {
                sims[c] = max;
                temp = resetMatching(temp, r, c);
            }
        }

        // compute final similarity
        for (double s : sims) {
            sim += s;
        }

        sim /= temp[0].length;

        simMatrix.setSimilarity(sim);

        return simMatrix;
    }

    private static double[][] resetMatching(double[][] m, int r, int c) {
        m = resetRow(m, r);
        m = resetCol(m, c);
        return m;
    }

    private static double[][] resetRow(double[][] m, int r) {
        double[] row = new double[m[0].length];
        m[r] = row;
        return m;
    }

    private static double[][] resetCol(double[][] m, int c) {
        for (int i = 0; i < m.length; i++) {
            m[i][c] = 0d;
        }
        return m;
    }

}
