package de.uop.mics.bayerl.cube.similarity.matrix;

import de.uop.mics.bayerl.cube.model.Component;
import de.uop.mics.bayerl.cube.similarity.concept.SameAsService;

/**
 * Created by sebastianbayerl on 30/07/15.
 */
public class SameAsExtended extends ComputeComponentSimilarity {
    @Override
    public String getName() {
        return "SameAsExtended";
    }

    @Override
    double getSimilarity(Component co1, Component co2) {
        String c1 = co1.getUrl();
        String c2 = co2.getUrl();
        SameAsService sameAsService = SameAsService.getInstance();

        return sameAsService.getSimilarity(c1, c2);
    }
}
