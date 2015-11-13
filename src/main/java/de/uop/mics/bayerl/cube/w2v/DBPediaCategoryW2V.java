package de.uop.mics.bayerl.cube.w2v;

import de.uop.mics.bayerl.cube.similarity.hierarchies.dbpedia.BfsSearch;
import de.uop.mics.bayerl.cube.similarity.hierarchies.dbpedia.DBPediaService;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by sebastianbayerl on 02/11/15.
 */
public class DBPediaCategoryW2V {
    private final static String FILE_MAP_BROADER = "catMap_broader";
    private final static String FILE_MAP_BOTH = "catMap_both";
    private final static String FILE_TARGET = "corpus_categories";
    private final static String PREFIX = "http://dbpedia.org/resource/Category:";

    private final static String CURRENT_MAP_FILE = FILE_MAP_BOTH;
    private final static int PATHS = 300_000;
    private final static int PATH_LENGTH = 100;

    public static void main(String[] args) {
//        writeMap();
//        healthCheck();
        generateDataset();
    }

    private static void healthCheck() {
        System.out.println("load map");
        Map<String, List<String>> catMap = loadMap();
        Set<String> broaders = new HashSet<>();
        Set<String> notop = new HashSet<>();
        for (String key : catMap.keySet()) {
            for (String s : catMap.get(key)) {
                if (!catMap.keySet().contains(s)) {
                    notop.add(s);
                }
            }
            broaders.addAll(catMap.get(key));
        }

        System.out.println(notop.size());
        System.out.println(catMap.size());
        System.out.println(broaders.size());
    }

    private static void generateDataset() {
        System.out.println("load map");
        Map<String, List<String>> catMap = loadMap();
        System.out.println("done loading");
        Map<String, Integer> visited = new HashMap<>();
        Set<String> all = new HashSet<>();
        all.addAll(catMap.keySet());

        for (String key : catMap.keySet()) {
            all.addAll(catMap.get(key));
        }

        List<String> keys = new ArrayList<>(catMap.keySet());
        System.out.println("key size: " + keys.size());
        System.out.println("all: " + all.size());

        for (String key : all) {
            visited.put(key, 0);
        }

        try {
            Files.deleteIfExists(Paths.get(FILE_TARGET));
            Files.createFile(Paths.get(FILE_TARGET));
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        int shortPath = 0;
        int temp = 0;
        for (int i = 0; i < PATHS; i++) {
            if (i % 100_000 == 0) {
                System.out.println("path:" + i);
            }

            List<String> path = new ArrayList<>();
            // TODO iterate every key and pick it as an starting point?
            String current = pickRandom(keys);
            visited.put(current, visited.get(current) + 1);
            path.add(current);
            for (int j = 0; j < PATH_LENGTH; j++) {

                if (catMap.containsKey(current)) {
                    current = pickRandom(catMap.get(current));
                    visited.put(current, visited.get(current) + 1);
                    path.add(current);
                } else {
                    shortPath++;
                    break;
                }
            }

            for (String p : path) {
                sb.append(p);
                sb.append(" ");
            }
            sb.append("\n");
            temp++;

            if (temp == 10_000) {
                System.out.println("write");
                temp = 0;
                try {
                    Files.write(Paths.get(FILE_TARGET), sb.toString().getBytes(), StandardOpenOption.APPEND);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sb = new StringBuilder();
            }
        }
        System.out.println("write remaining");
        try {
            Files.write(Paths.get(FILE_TARGET), sb.toString().getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int unvisited = 0;
        int often = 0;

        for (String key : all) {
            if (visited.get(key) == 0) {
                unvisited++;
            } else if (visited.get(key) > 50_000) {
                often++;
                //System.out.println(key + "  " +  visited.get(key));
            }
        }

        System.out.println("unvisited: " + unvisited);
        System.out.println("visited often: " + often);
        System.out.println("short paths: " + shortPath);
    }

    private static String pickRandom(List<String> concepts) {
        return concepts.get(ThreadLocalRandom.current().nextInt(0, concepts.size()));
    }

    private static Map<String, List<String>> loadMap() {
        Map<String, List<String>> catMap = null;
        try (FileInputStream streamIn = new FileInputStream(CURRENT_MAP_FILE)) {
            ObjectInputStream objectinputstream = new ObjectInputStream(streamIn);
            catMap = (Map<String, List<String>>) objectinputstream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

       return catMap;
    }

    public static void writeMap() {
        String queryString =  " SELECT ?s ?o  WHERE { ?s " + BfsSearch.SKOS_BROADER + " ?o }";
        ParameterizedSparqlString prepareQuery = new ParameterizedSparqlString(queryString);

        DBPediaService.DATASET.begin(ReadWrite.READ);
        Model model = DBPediaService.DATASET.getDefaultModel();

        //List<String> categories = new ArrayList<>();
        Map<String, List<String>> catMap = new HashMap<>();
        //Map<String, List<String>> catNarrower = new HashMap<>();
        try (QueryExecution queryExecution = QueryExecutionFactory.create(prepareQuery.toString(), model)) {
            ResultSet results = queryExecution.execSelect();
            long l = 0;
            while (results.hasNext()) {
                QuerySolution res = results.next();
                String c1 = res.getResource("s").toString();
                String c2 = res.getResource("o").toString();

                c1 = c1.replace(PREFIX, "");
                c2 = c2.replace(PREFIX, "");

                // add broader
                if (catMap.containsKey(c1)) {
                    catMap.get(c1).add(c2);
                } else {
                    List<String> cs = new ArrayList<>();
                    cs.add(c2);
                    catMap.put(c1, cs);
                }

                if (CURRENT_MAP_FILE == FILE_MAP_BOTH) {
                    // add narrower
                    if (catMap.containsKey(c2)) {
                        catMap.get(c2).add(c1);
                    } else {
                        List<String> cs = new ArrayList<>();
                        cs.add(c1);
                        catMap.put(c2, cs);
                    }
                }

                l++;

                if (l % 10000 == 0) {
                    System.out.println(l);
                }
            }
            System.out.println(l);
        }

        DBPediaService.DATASET.end();

        try {
            FileOutputStream fout = new FileOutputStream(CURRENT_MAP_FILE);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(catMap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
