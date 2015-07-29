package de.uop.mics.bayerl.cube;

import de.uop.mics.bayerl.cube.model.Cube;
import de.uop.mics.bayerl.cube.provider.CubeGenerator;
import de.uop.mics.bayerl.cube.similarity.SimilarityMatrix;
import de.uop.mics.bayerl.cube.similarity.SimilarityMeasure;
import de.uop.mics.bayerl.cube.similarity.algorithm.ComputeStringDistance;
import de.uop.mics.bayerl.cube.similarity.algorithm.DistanceAlgorithm;
import de.uop.mics.bayerl.cube.similarity.metadata.DescriptionBasedSimilarity;
import de.uop.mics.bayerl.cube.similarity.metadata.LabelBasedSimilarity;
import de.uop.mics.bayerl.cube.similarity.structural.EqualComponent;
import de.uop.mics.bayerl.cube.similarity.structural.EqualComponents;
import de.uop.mics.bayerl.cube.validation.ValidStructure;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sebastianbayerl on 23/07/15.
 */
public class ComputeSimilarity {

    private final static Logger LOG = Logger.getLogger(ComputeSimilarity.class);


    public static void main(String[] args) {
        List<Cube> cubes = CubeGenerator.createCubes(2);

        cubes.forEach(ValidStructure::validate);


        List<SimilarityMeasure> similarityMeasures = new ArrayList<>();
        similarityMeasures.add(new EqualComponents());
        similarityMeasures.add(new LabelBasedSimilarity());
        similarityMeasures.add(new DescriptionBasedSimilarity());


        for (SimilarityMeasure similarityMeasure : similarityMeasures) {
            LOG.info(similarityMeasure.getName() + ": " + similarityMeasure.computeSimilarity(cubes.get(0), cubes.get(1)));
        }

        System.out.println(ComputeStringDistance.compute("2", "2", DistanceAlgorithm.LEVENSHTEIN));


        SimilarityMatrix similarityMatrix = EqualComponent.computeSimilarity(cubes.get(0), cubes.get(1));

        System.out.println(similarityMatrix);



    }

}
