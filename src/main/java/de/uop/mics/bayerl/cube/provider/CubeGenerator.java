package de.uop.mics.bayerl.cube.provider;

import de.uop.mics.bayerl.cube.model.Cube;
import de.uop.mics.bayerl.cube.model.Dimension;
import de.uop.mics.bayerl.cube.model.Measure;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by sebastianbayerl on 24/07/15.
 */
public class CubeGenerator {

    private final static int COUNT_DIMENSIONS = 10;
    private final static int COUNT_MEASURES = 2;

    public static List<Cube> createCubes(int nrOfCubes) {
        List<Cube> cubes = new ArrayList<>();
        for (int i = 0; i < nrOfCubes; i++) {
            cubes.add(createCube());
        }

        return cubes;
    }

    private static Cube createCube() {
        String id = UUID.randomUUID().toString();
        Cube cube = new Cube();
        cube.setId(id);
        cube.setLabel(id);
        cube.setDescription(id);

        for (int i = 0; i < COUNT_DIMENSIONS; i++) {
            cube.getStructureDefinition().getDimensions().add(createDimension());
        }

        for (int i = 0; i < COUNT_MEASURES; i++) {
            cube.getStructureDefinition().getMeasures().add(createMeasure());
        }

        return cube;
    }


    private static Dimension createDimension() {
        Dimension dimension = new Dimension();
        String id = UUID.randomUUID().toString();
        dimension.setLabel(id);
        dimension.setConcept(id);

        return dimension;
    }

    private static Measure createMeasure() {
        Measure measure = new Measure();
        String id = UUID.randomUUID().toString();
        measure.setLabel(id);
        measure.setConcept(id);

        return measure;
    }

}
