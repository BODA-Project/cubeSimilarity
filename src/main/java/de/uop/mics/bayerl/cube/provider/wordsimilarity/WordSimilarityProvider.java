package de.uop.mics.bayerl.cube.provider.wordsimilarity;

import de.uop.mics.bayerl.cube.model.Cube;
import de.uop.mics.bayerl.cube.model.Dimension;
import de.uop.mics.bayerl.cube.provider.DBPediaExampleProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by sebastianbayerl on 28/10/15.
 */
public class WordSimilarityProvider {

    public static List<Cube> getWordSimCubes() {
        return getCubes(WordSimHelper.PATH_TARGET);
    }

    public static List<Cube> getMovieCubes() {
        return getCubes(WordSimHelper.PATH_TARGET_MOVIES);
    }

    private static List<Cube> getCubes(String file) {
        List<Cube> cubes = new ArrayList<>();
        Set<String> keys = new HashSet<>();
        try {
            Files.lines(Paths.get(file)).forEach(s -> {
                String[] splits = s.split(",");
                String l1 = splits[0];
                String l2 = splits[1];
                String c1 = splits[3];
                String c2 = splits[4];

                if (!keys.contains(l1)) {
                    keys.add(l1);
                    cubes.add(getCube(l1, c1));
                }

                if (!keys.contains(l2)) {
                    keys.add(l2);
                    cubes.add(getCube(l2, c2));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return cubes;
    }

    private static Cube getCube(String label, String concept) {
        Cube c = new Cube();
        c.setId(label);
        c.setLabel(label);
        c.setDescription(label + " - " + concept);

        Dimension d = new Dimension();
        c.getStructureDefinition().getDimensions().add(d);
        d.setLabel(label);
        d.setConcept(DBPediaExampleProvider.PREFIX + concept);

        return c;
    }

}
