package de.uop.mics.bayerl.cube.rest.repository;

import de.uop.mics.bayerl.cube.model.Cube;
import de.uop.mics.bayerl.cube.provider.DBPediaExampleProvider;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by sebastianbayerl on 03/09/15.
 */
@Repository
public class CubeRepository {

    private final List<Cube> cubes = DBPediaExampleProvider.generateCubes();//CubeGenerator.createCubes(10);

    public Cube getFirst() {
        return cubes.get(0);
    }

    public Cube getSecond() {
        return cubes.get(1);
    }

    public List<Cube> getCubes() {
        return cubes;
    }

    public Cube getCube(String cubeId) {

        for (Cube cube : cubes) {
            if (cube.getId().equals(cubeId)) {
                return cube;
            }
        }

        return null;
    }
}
