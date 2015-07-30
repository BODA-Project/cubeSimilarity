package de.uop.mics.bayerl.cube.similarity.matrix;

import de.uop.mics.bayerl.cube.model.Component;
import de.uop.mics.bayerl.cube.similarity.hierarchies.dbpedia.FindTopConcept;

/**
 * Created by sebastianbayerl on 30/07/15.
 */
public class CommonTopConceptSimilarity extends ComputeComponentSimilarity {


    @Override
    public String getName() {
        return "CommonTopConceptSimilarity";
    }

    @Override
    double getSimilarity(Component co1, Component co2) {
        String c1 = co1.getUrl();
        String c2 = co2.getUrl();

        return FindTopConcept.getSimilarity(c1, c2);
    }
}
