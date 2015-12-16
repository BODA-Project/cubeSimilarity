package de.uop.mics.bayerl.cube.similarity.matrix;

import de.uop.mics.bayerl.cube.model.Component;
import de.uop.mics.bayerl.cube.similarity.concept.Word2VecService;

/**
 * Created by sebastianbayerl on 27/10/15.
 */
public class Word2Vec extends ComputeComponentSimilarity {

    @Override
    public String getName() {
        return "Word2Vec";
    }

    @Override
    public double getSimilarity(Component co1, Component co2) {
        String c1 = co1.getConcept();
        String c2 = co2.getConcept();

        Word2VecService word2VecService = Word2VecService.getInstance();

        return word2VecService.getSimilarity(c1, c2, true);
    }
}
