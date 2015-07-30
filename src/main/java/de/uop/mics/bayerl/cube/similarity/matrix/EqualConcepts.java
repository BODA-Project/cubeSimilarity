package de.uop.mics.bayerl.cube.similarity.matrix;

import de.uop.mics.bayerl.cube.model.Component;

/**
 * Created by sebastianbayerl on 29/07/15.
 */
public class EqualConcepts extends ComputeComponentSimilarity {

    @Override
    public String getName() {
        return EqualConcepts.class.getName();
    }

    @Override
    double getSimilarity(Component co1, Component co2) {
        String c1 = co1.getUrl();
        String c2 = co2.getUrl();

        return c1.equals(c2) ? 1d : 0d;
    }
}
