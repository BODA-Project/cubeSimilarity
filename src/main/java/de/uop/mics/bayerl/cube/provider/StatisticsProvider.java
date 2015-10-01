package de.uop.mics.bayerl.cube.provider;

import de.uop.mics.bayerl.cube.model.Cube;
import de.uop.mics.bayerl.cube.model.Dimension;
import de.uop.mics.bayerl.cube.model.Measure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by sebastianbayerl on 29/09/15.
 */
public class StatisticsProvider {

    private final static String FILE = "/Users/sebastianbayerl/Desktop/test - Sheet1.csv";
    private final static Map<String, String> CONCEPTS = new HashMap<>();


    private static final String FILE_CONCEPTS = "/Users/sebastianbayerl/Desktop/test - concepts.csv";

    static {
        try {
            Files.lines(Paths.get(FILE_CONCEPTS)).forEach(s -> {
                String[] splits = s.split(",");
                CONCEPTS.put(splits[0].trim(), splits[1].trim());
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(CONCEPTS.size());

    }

    public static void main(String[] args) {
        loadCubes();
    }


    public static List<Cube> loadCubes() {
        List<Cube> cubes = new ArrayList<>();
        List<String> lines = new ArrayList<>();
        try {
            lines = Files.lines(Paths.get(FILE)).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Cube cube = new Cube();
        for (String line : lines) {
            System.out.println(line);
            String[] splits = line.split(",");

            if (splits.length > 0) {
                String key = splits[0];

                if (key.equals("Label")) {
                    cube = new Cube();
                    cubes.add(cube);
                    cube.setLabel(splits[1]);
                } else if (key.equals("Description")) {
                    cube.setDescription(splits[1]);
                } else if (key.equals("Number")) {
                    cube.setLabel(splits[1] + "-" + cube.getLabel());
                } else if (key.equals("Measures")) {
                    for (int i = 1; i < splits.length; i++) {
                        Measure measure = new Measure();
                        cube.getStructureDefinition().getMeasures().add(measure);
                        measure.setLabel(splits[i]);
                        measure.setConcept(getConcept(splits[i]));
                    }
                } else if (key.equals("Dimensions")) {
                    for (int i = 1; i < splits.length; i++) {
                        Dimension dimension = new Dimension();
                        cube.getStructureDefinition().getDimensions().add(dimension);
                        dimension.setLabel(splits[i]);
                        dimension.setConcept(getConcept(splits[i]));
                    }
                } else {

                }
            }
        }

        System.out.println(cubes.size());


        return cubes;
    }

    private static String getConcept(String label) {
        String concept;
        if (CONCEPTS.containsKey(label)) {
            concept = CONCEPTS.get(label);
        } else {
            concept = "dummy";
        }

        return concept;
    }


}
