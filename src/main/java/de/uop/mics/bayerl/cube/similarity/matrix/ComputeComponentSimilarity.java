package de.uop.mics.bayerl.cube.similarity.matrix;

import de.uop.mics.bayerl.cube.model.Component;
import de.uop.mics.bayerl.cube.model.Cube;

import java.util.List;

/**
 * Created by sebastianbayerl on 30/07/15.
 */
public abstract class ComputeComponentSimilarity {

    public abstract String getName();

    public abstract double getSimilarity(Component co1, Component co2);

    public SimilarityMatrix computeMatrix(Cube c1, Cube c2) {
        List<Component> comp1 = c1.getStructureDefinition().getComponents();
        List<Component> comp2 = c2.getStructureDefinition().getComponents();
        SimilarityMatrix similarityMatrix = new SimilarityMatrix(comp1.size(), comp2.size());

        for (int i = 0; i < comp1.size(); i++) {
            for (int j = 0; j < comp2.size(); j++) {
                similarityMatrix.getMatrix()[i][j] = getSimilarity(comp1.get(i), comp2.get(j));
            }
        }

        return similarityMatrix;
    }

}
