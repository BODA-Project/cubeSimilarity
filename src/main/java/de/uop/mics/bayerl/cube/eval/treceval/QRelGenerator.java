package de.uop.mics.bayerl.cube.eval.treceval;

import de.uop.mics.bayerl.cube.model.Cube;
import de.uop.mics.bayerl.cube.provider.ReichstatisticsGroupedCubes;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sebastianbayerl on 15/12/15.
 */
public class QRelGenerator {


    private static final String FILE = "qrel.cube";

    public static void main(String[] args) {
        generate();
    }


    private static void generate() {
        List<Cube> cubes = ReichstatisticsGroupedCubes.loadCubes();

        List<String> lines = new ArrayList<>();
        for (Cube cube : cubes) {
            for (Cube cubeTarget : cubes) {
                String line = cube.getId() + " 0 " + cubeTarget.getId() + " ";

                if (cubeTarget.getId().split("-")[0].equals(cube.getId().split("-")[0])) {
                    line += 1;
                } else {
                    line += 0;
                }


                lines.add(line);

                System.out.println(line);
            }

        }

        try {
            Files.write(Paths.get(FILE), lines, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
