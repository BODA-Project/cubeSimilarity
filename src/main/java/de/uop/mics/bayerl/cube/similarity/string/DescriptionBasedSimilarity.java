package de.uop.mics.bayerl.cube.similarity.string;

import de.uop.mics.bayerl.cube.model.Cube;
import de.uop.mics.bayerl.cube.similarity.ComputeSimilarity;

/**
 * Created by sebastianbayerl on 24/07/15.
 */
public class DescriptionBasedSimilarity implements ComputeSimilarity {

    @Override
    public String getName() {
        return "DescriptionBasedSimilarity";
    }

    @Override
    public double computeSimilarity(Cube c1, Cube c2) {
        return ComputeStringDistance.compute(c1.getDescription(), c2.getDescription(), DistanceAlgorithm.LEVENSHTEIN);
    }
}
