package de.uop.mics.bayerl.cube.provider;

import de.uop.mics.bayerl.cube.model.Cube;
import de.uop.mics.bayerl.cube.model.Dimension;
import de.uop.mics.bayerl.cube.model.Measure;
import de.uop.mics.bayerl.cube.provider.wordsimilarity.WordSimHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by sebastianbayerl on 15/12/15.
 */
public class ReichstatisticsGroupedCubes {

    private final static String FOLDER = "reichstatisticsInput" + File.separator;
    private final static String FILE = FOLDER + "reichstatisticsSampleDSD.txt";
    private static final String FILE_CONCEPTS = FOLDER + "reichstatisticsDisambiguation.txt";

    private final static Map<String, String> CONCEPTS = new HashMap<>();

    private final static Set<String> labels = new HashSet<>();

    static {
        try {
            Files.lines(Paths.get(FILE_CONCEPTS)).forEach(s -> {
                String[] splits = s.split(",");
                CONCEPTS.put(splits[0].trim(), WordSimHelper.DBPEDIA_PREFIX + splits[1].trim());
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(CONCEPTS.size());
        
        
        Set<String> cs = new HashSet<>();
        cs.addAll(CONCEPTS.values());
        System.out.println(cs.size());
    }

    public static void main(String[] args) {
        List<Cube> cubes = loadCubes();
        System.out.println(cubes.size());

        Set<String> ids = new HashSet<>();
        for (Cube cube : cubes) {
            ids.add(cube.getId());
        }

        if (cubes.size() != ids.size()) {
            System.out.println("ERROR ids are not unique");
        }
    }


    public static List<Cube> loadCubes() {
        Set<String> errorSet = new HashSet<>();
        List<Cube> cubes = new ArrayList<>();
        List<String> lines = new ArrayList<>();
        try {
            lines = Files.lines(Paths.get(FILE)).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        int i = 0;
        Cube cube = null;
        for (String line : lines) {
            line = line.trim();
            if (line.equals("")) {
                cube = new Cube();
                cubes.add(cube);
                i = 0;
            } else if (i == 1) {
                cube.setId(line);
            } else if (i == 2) {
                cube.setLabel(line);
            } else if (i == 3) {
                cube.setDescription(line);
            } else if (i == 4) {
                String[] splits = line.split(",");
                for (String split : splits) {
                    split = split.trim();
                    Dimension d = new Dimension();
                    cube.getStructureDefinition().getDimensions().add(d);
                    d.setLabel(split);

                    if (CONCEPTS.get(split) == null) {
                        System.out.println("error " + split);
                        errorSet.add(split);
                    }

                    d.setConcept(CONCEPTS.get(split));
                    labels.add(split);
                }
            } else if (i == 5) {
                String[] splits = line.split(",");
                for (String split : splits) {
                    split = split.trim();
                    Measure m = new Measure();
                    cube.getStructureDefinition().getMeasures().add(m);
                    m.setLabel(split);

                    if (CONCEPTS.get(split) == null) {
                        System.out.println("error " + split);
                        errorSet.add(split);
                    }

                    m.setConcept(CONCEPTS.get(split));
                    labels.add(split);
                }
            }

            i++;
        }

        errorSet.forEach(System.out::println);

        return cubes;
    }
}
