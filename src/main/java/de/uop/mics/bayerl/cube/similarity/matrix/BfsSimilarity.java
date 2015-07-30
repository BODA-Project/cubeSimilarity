package de.uop.mics.bayerl.cube.similarity.matrix;

import de.uop.mics.bayerl.cube.model.Component;
import de.uop.mics.bayerl.cube.similarity.hierarchies.dbpedia.BfsSearch;

/**
 * Created by sebastianbayerl on 30/07/15.
 */
public class BfsSimilarity extends ComputeComponentSimilarity {
    @Override
    public String getName() {
        return "BfsSimilarity";
    }

    @Override
    double getSimilarity(Component co1, Component co2) {
        String c1 = co1.getUrl();
        String c2 = co2.getUrl();

        return BfsSearch.getSimilarity(c1, c2);
    }
}
