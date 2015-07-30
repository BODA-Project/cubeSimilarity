package de.uop.mics.bayerl.cube.similarity.string;

import de.uop.mics.bayerl.cube.model.Cube;
import de.uop.mics.bayerl.cube.similarity.ComputeSimilarity;

/**
 * Created by sebastianbayerl on 23/07/15.
 */
public class LabelBasedSimilarity implements ComputeSimilarity {

    @Override
    public String getName() {
        return "LabelBasedSimilarity";
    }

    @Override
    public double computeSimilarity(Cube c1, Cube c2) {
        return ComputeStringDistance.compute(c1.getLabel(), c2.getLabel(), DistanceAlgorithm.LEVENSHTEIN);
    }
}
