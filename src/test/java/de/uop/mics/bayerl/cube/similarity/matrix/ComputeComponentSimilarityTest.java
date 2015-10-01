package de.uop.mics.bayerl.cube.similarity.matrix;

import de.uop.mics.bayerl.cube.model.Cube;
import de.uop.mics.bayerl.cube.provider.CubeGenerator;
import de.uop.mics.bayerl.cube.validation.ValidStructure;
import org.junit.BeforeClass;

import java.util.List;

/**
 * Created by sebastianbayerl on 30/07/15.
 */
public class ComputeComponentSimilarityTest {

    private static List<Cube> cubes;

    @BeforeClass
    public static void beforeClass() {
        cubes = CubeGenerator.createCubes(2);
        cubes.forEach(ValidStructure::validate);
    }

//    @Test
//    public void testEqualComponents() throws Exception {
//        ComputeComponentSimilarity similarity = new EqualConcepts();
//        System.out.println(similarity.computeMatrix(cubes.get(0), cubes.get(1)).getSimilarity());
//    }
//
//    @Test
//    public void testEqualLabels() throws Exception {
//        ComputeComponentSimilarity similarity = new EqualLabels();
//        System.out.println(similarity.computeMatrix(cubes.get(0), cubes.get(1)).getSimilarity());
//    }
//
//    @Test
//    public void testLabelSimilarity() throws Exception {
//        ComputeComponentSimilarity similarity = new LabelSimilarity();
//        System.out.println(similarity.computeMatrix(cubes.get(0), cubes.get(1)).getSimilarity());
//    }
//
//    @Test
//    public void testSameAsExtended() throws Exception {
//        ComputeComponentSimilarity similarity = new SameAsExtended();
//        System.out.println(similarity.computeMatrix(cubes.get(0), cubes.get(1)).getSimilarity());
//    }
//
//    @Test
//    public void testCommonTopConcept() throws Exception {
//        ComputeComponentSimilarity similarity = new CommonTopConceptSimilarity();
//        System.out.println(similarity.computeMatrix(cubes.get(0), cubes.get(1)).getSimilarity());
//    }
//
//    @Test
//    public void testBfsSimilarity() throws Exception {
//        ComputeComponentSimilarity similarity = new BfsSimilarity();
//        System.out.println(similarity.computeMatrix(cubes.get(0), cubes.get(1)).getSimilarity());
//    }


}