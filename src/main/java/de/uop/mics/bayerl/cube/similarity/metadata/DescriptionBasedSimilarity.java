package de.uop.mics.bayerl.cube.similarity.metadata;

import de.uop.mics.bayerl.cube.model.Cube;
import de.uop.mics.bayerl.cube.similarity.SimilarityMeasure;
import de.uop.mics.bayerl.cube.similarity.algorithm.ComputeStringDistance;
import de.uop.mics.bayerl.cube.similarity.algorithm.DistanceAlgorithm;

/**
 * Created by sebastianbayerl on 24/07/15.
 */
public class DescriptionBasedSimilarity implements SimilarityMeasure {

    @Override
    public String getName() {
        return "DescriptionBasedSimilarity";
    }

    @Override
    public double computeSimilarity(Cube c1, Cube c2) {
        return ComputeStringDistance.compute(c1.getDescription(), c2.getDescription(), DistanceAlgorithm.LEVENSHTEIN);
    }
}
