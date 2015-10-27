package de.uop.mics.bayerl.cube.similarity.matrix;

import de.uop.mics.bayerl.cube.model.Component;

/**
 * Created by sebastianbayerl on 30/07/15.
 */
public class EqualLabels extends ComputeComponentSimilarity {
    @Override
    public String getName() {
        return "EqualLabels";
    }

    @Override
    public double getSimilarity(Component co1, Component co2) {
        String l1 = co1.getLabel();
        String l2 = co2.getLabel();

        return l1.equalsIgnoreCase(l2) ? 1d : 0d;
    }
}
