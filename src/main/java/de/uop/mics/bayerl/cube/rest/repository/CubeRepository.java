package de.uop.mics.bayerl.cube.rest.repository;

import de.uop.mics.bayerl.cube.model.Cube;
import de.uop.mics.bayerl.cube.provider.StatisticsProvider;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by sebastianbayerl on 03/09/15.
 */
@Repository
public class CubeRepository {

//    private final List<Cube> cubes = CubeGenerator.createCubes(10);
//    private final List<Cube> cubes = WordSimilarityProvider.getMovieCubes();
//    private final List<Cube> cubes = DBPediaExampleProvider.generateCubes();
//    private final List<Cube> cubes = Datahub.provideCubes();
    private final List<Cube> cubes = StatisticsProvider.loadCubes();


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
