package de.uop.mics.bayerl.cube.similarity.matrix;

import de.uop.mics.bayerl.cube.model.Component;
import de.uop.mics.bayerl.cube.similarity.hierarchies.dbpedia.BfsSearch;
import de.uop.mics.bayerl.cube.similarity.hierarchies.dbpedia.DBPediaProperty;

/**
 * Created by sebastianbayerl on 30/07/15.
 */
public class BfsSimilarity extends ComputeComponentSimilarity {

    DBPediaProperty dbPediaProperty;

    public BfsSimilarity(DBPediaProperty dbPediaProperty) {
        this.dbPediaProperty = dbPediaProperty;
    }

    @Override
    public String getName() {
        return "BfsSimilarity";
    }

    @Override
    public double getSimilarity(Component co1, Component co2) {
        String c1 = co1.getConcept();
        String c2 = co2.getConcept();

        return BfsSearch.getInstance().getSimilarity(c1, c2, dbPediaProperty);
    }
}
