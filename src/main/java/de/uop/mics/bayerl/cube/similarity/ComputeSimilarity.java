package de.uop.mics.bayerl.cube.similarity;

import de.uop.mics.bayerl.cube.model.Cube;

/**
 * Created by sebastianbayerl on 23/07/15.
 */
public interface ComputeSimilarity {

    String getName();
    double computeSimilarity(Cube c1, Cube c2);

}
