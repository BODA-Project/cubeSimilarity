package de.uop.mics.bayerl.cube.similarity.structural;

import de.uop.mics.bayerl.cube.model.Component;
import de.uop.mics.bayerl.cube.model.Cube;
import de.uop.mics.bayerl.cube.similarity.SimilarityMatrix;

import java.util.List;

/**
 * Created by sebastianbayerl on 29/07/15.
 */
public class EqualComponent {


    public static SimilarityMatrix computeSimilarity(Cube c1, Cube c2) {
        List<Component> comp1 = c1.getStructureDefinition().getComponents();
        List<Component> comp2 = c2.getStructureDefinition().getComponents();
        SimilarityMatrix similarityMatrix = new SimilarityMatrix(comp1.size(), comp2.size());

        for (int i = 0; i < comp1.size(); i++) {
            for (int j = 0; j < comp2.size(); j++) {
                similarityMatrix.setValue(i, j, getSimilarity(comp1.get(i).getUrl(), comp2.get(j).getUrl()));
            }
        }

        return similarityMatrix;
    }


    private static double getSimilarity(String c1, String c2) {
        return c1.equals(c2) ? 1d : 0d;
    }
}
