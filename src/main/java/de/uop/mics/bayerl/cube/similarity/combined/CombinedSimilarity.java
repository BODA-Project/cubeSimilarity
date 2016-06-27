package de.uop.mics.bayerl.cube.similarity.combined;

import de.uop.mics.bayerl.cube.model.Component;
import de.uop.mics.bayerl.cube.similarity.hierarchies.dbpedia.DBPediaProperty;
import de.uop.mics.bayerl.cube.similarity.matrix.*;
import de.uop.mics.bayerl.cube.similarity.string.DistanceAlgorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sebastianbayerl on 27/06/16.
 */
public class CombinedSimilarity extends ComputeComponentSimilarity {
    @Override
    public String getName() {
        return "Combined Similarity";
    }

    @Override
    public double getSimilarity(Component co1, Component co2) {
        List<Double> similarities = new ArrayList<>();
        similarities.add(new LabelSimilarity(DistanceAlgorithm.JARO_WINKLER).getSimilarity(co1, co2));
        similarities.add(new EqualLabels().getSimilarity(co1, co2));
        similarities.add(new SameAsExtended().getSimilarity(co1, co2));
        similarities.add(new BfsSimilarity(DBPediaProperty.BROADER).getSimilarity(co1, co2));
        similarities.add(new BfsSimilarity(DBPediaProperty.PAGE_LINK).getSimilarity(co1, co2));
        similarities.add(new Word2Vec().getSimilarity(co1, co2));
        
        return similarities.stream().max(Double::compare).get();
    }
}
