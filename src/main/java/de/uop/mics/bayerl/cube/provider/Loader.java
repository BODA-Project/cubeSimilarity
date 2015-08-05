package de.uop.mics.bayerl.cube.provider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by sebastianbayerl on 23/07/15.
 */
public class Loader {


    public static void main(String[] args) {
        findDSDs();
    }

//    private final static String PATH = "/Volumes/TOSHIBA EXT/CubeSimilarity/data/kai/dumps/";
    private final static String PATH = "/Users/sebastianbayerl/Desktop/data/dumps/";
    private final static String DSD = "http://purl.org/linked-data/cube#DataStructureDefinition";
    private final static String DS = "http://purl.org/linked-data/cube#DataSet";


    public static void findDSDs() {


        try {
            List<Path> paths = Files.walk(Paths.get(PATH)).filter(s -> s.toString().endsWith(".nt")).collect(Collectors.toList());

            for (Path path : paths) {
                //System.out.println("##### FILE #### " + path);
                Files.lines(path).filter(s -> s.contains(DSD)).forEach(System.out::println);

            }



        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
