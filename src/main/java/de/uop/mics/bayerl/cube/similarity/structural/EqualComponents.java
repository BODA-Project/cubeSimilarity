package de.uop.mics.bayerl.cube.similarity.structural;

import de.uop.mics.bayerl.cube.model.Cube;
import de.uop.mics.bayerl.cube.model.Dimension;
import de.uop.mics.bayerl.cube.model.Measure;
import de.uop.mics.bayerl.cube.model.StructureDefinition;
import de.uop.mics.bayerl.cube.similarity.SimilarityMeasure;

/**
 * Created by sebastianbayerl on 24/07/15.
 */
@Deprecated
public class EqualComponents implements SimilarityMeasure {

    @Override
    public String getName() {
        return "EqualComponents";
    }

    @Override
    public double computeSimilarity(Cube c1, Cube c2) {
        return getScore(c1.getStructureDefinition(), c2.getStructureDefinition(), CompareAlgorithm.FULL_STRUCTURE);
    }

    private double getScore(StructureDefinition sd1, StructureDefinition sd2, CompareAlgorithm compareAlgorithm) {
        int equalDims = 0;
        int equalMeas = 0;

        int totalDims1 = sd1.getDimensions().size();
        int totalDims2 = sd2.getDimensions().size();

        int totalMeas1 = sd1.getMeasures().size();
        int totalMeas2 = sd2.getMeasures().size();

        if (totalDims1 == 0 || totalDims2 == 0 || totalMeas1 == 0 || totalMeas2 == 0) {
            System.out.println("potentially invalid cube!");
            return 0.0;
        }

        for (Dimension dim1 : sd1.getDimensions()) {
            if (sd2.getDimensions().contains(dim1)) {
                equalDims++;
            }
        }

        for (Measure meas1 : sd1.getMeasures()) {
            if (sd2.getMeasures().contains(meas1)) {
                equalMeas++;
            }
        }

        double score = 0.0;

        if (compareAlgorithm == CompareAlgorithm.FULL_STRUCTURE) {
            score = computeScoreFullStructure(equalDims, equalMeas, totalDims1, totalDims2, totalMeas1, totalMeas2);
        } else if (compareAlgorithm == CompareAlgorithm.DIMENSIONS_ONLY) {
            score = computeScoreDimensionsOnly(equalDims, totalDims1, totalDims2);
        }

        return score;
    }


    private double computeScoreFullStructure(double equalDims, double equalMeas, double totalDims1, double totalDims2, double totalMeas1, double totalMeas2) {
        double result;
        result = equalDims / totalDims1 + equalDims / totalDims2 + equalMeas / totalMeas1 + equalMeas / totalMeas2;
        result = result / 4.0;

        return result;
    }

    private double computeScoreDimensionsOnly(double equalDims, double totalDims1, double totalDims2) {
        double result;

        result = equalDims / totalDims1 + equalDims / totalDims2;
        result = result / 2.0;

        return result;
    }

}
