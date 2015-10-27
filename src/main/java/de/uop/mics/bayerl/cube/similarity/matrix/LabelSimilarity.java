package de.uop.mics.bayerl.cube.similarity.matrix;

import de.uop.mics.bayerl.cube.model.Component;
import de.uop.mics.bayerl.cube.similarity.string.ComputeStringDistance;
import de.uop.mics.bayerl.cube.similarity.string.DistanceAlgorithm;

/**
 * Created by sebastianbayerl on 30/07/15.
 */
public class LabelSimilarity extends ComputeComponentSimilarity {

    private DistanceAlgorithm distanceAlgorithm;


    public LabelSimilarity(DistanceAlgorithm distanceAlgorithm) {
        this.distanceAlgorithm = distanceAlgorithm;
    }
    
    @Override
    public String getName() {
        return "LabelSimilarity";
    }

    @Override
    public double getSimilarity(Component co1, Component co2) {
        String l1 = co1.getLabel();
        String l2 = co2.getLabel();

        return ComputeStringDistance.compute(l1, l2, distanceAlgorithm);
    }

}
