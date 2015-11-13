package de.uop.mics.bayerl.cube.provider.wordsimilarity;

import de.uop.mics.bayerl.cube.similarity.hierarchies.dbpedia.DBPediaService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by sebastianbayerl on 02/11/15.
 */
public class WordSimHelper {

    public final static String FOLDER = "wordsim353/";
    private final static String PATH_INPUT = FOLDER + "combined.csv";
    private final static String PATH_DISAMBIGUATION = FOLDER + "wordSimConcepts.csv";
    public final static String PATH_TARGET = FOLDER + "wordSimEnriched.csv";

    public final static String FOLDER_MOVIES = "movies/";
    private final static String PATH_INPUT_MOVIES = FOLDER_MOVIES + "movies-truth.csv";
    private final static String PATH_DISAMBIGUATION_MOVIES = FOLDER_MOVIES + "movies-disambiguation.csv";
    public final static String PATH_TARGET_MOVIES = FOLDER_MOVIES + "moviesEnriched.csv";

    private final static String WIKI_PREFIX = "https://en.wikipedia.org/wiki/";
    public final static String DBPEDIA_PREFIX = "http://dbpedia.org/resource/";


    public static void main(String[] args) {
//        printAsHtml();
//        healthCheck();
        createDataset(PATH_DISAMBIGUATION_MOVIES, PATH_INPUT_MOVIES, PATH_TARGET_MOVIES);
    }


    private static void createDataset(String disambiguation, String input, String target) {
        // load concepts
        Map<String, String> concepts = new HashMap<>();
        try {
            Files.lines(Paths.get(disambiguation)).forEach(s -> {
                String[] splits = s.split(",");
                String c1 = splits[0].trim().replace(WIKI_PREFIX, "");
                if (splits.length == 2) {
                    String c2 = splits[1].trim().replace(WIKI_PREFIX, "");
                    concepts.put(c1.toLowerCase(), c2);
                } else {
                    concepts.put(c1.toLowerCase(), c1);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        // enrich dataset
        List<String> lines = new ArrayList<>();
        try {
            try (Stream<String> stream = Files.lines(Paths.get(input))) {
                stream.forEach(w -> {
                    String[] splits = w.split(",");
                    String w1 = splits[0];
                    String w2 = splits[1];
                    String c1 = concepts.getOrDefault(w1.toLowerCase(), w1);
                    String c2 = concepts.getOrDefault(w2.toLowerCase(), w2);
                    String line = w + "," + c1 + "," + c2 + "\n";
                    lines.add(line);
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // write dataset
        try {
            Files.deleteIfExists(Paths.get(target));
            Files.createFile(Paths.get(target));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String line : lines) {
            try {
                Files.write(Paths.get(target), line.getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void healthCheck(String disambiguation) {
        try {
            Files.lines(Paths.get(disambiguation)).forEach(s -> {
                String[] splits = s.split(",");

                String concept = splits[0];
                if (splits.length == 2) {
                    concept = splits[1];
                }

                concept = concept.trim();
                concept = concept.replace(WIKI_PREFIX, DBPEDIA_PREFIX);

                if (DBPediaService.getCategories(concept).size() == 0) {
                    System.out.println("no cat: " + concept);

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printAsHtml(String input) {
        Set<String> words = new HashSet<>();

        try {
            try (Stream<String> stream = Files.lines(Paths.get(input))) {
                stream.forEach(w -> {
                    String[] splits = w.split(",");
                    words.add(splits[0]);
                    words.add(splits[1]);
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> wordList = new ArrayList<>(words);
        Collections.sort(wordList);

        List<String> urls = new ArrayList<>();
        for (String s : wordList) {
            String first = String.valueOf(s.charAt(0));
            String upper = first.toUpperCase();
            urls.add(WIKI_PREFIX + upper + s.substring(1));
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html> <html><body><ol>");

        for (String url : urls) {
            sb.append("<li><a href=\"" + url + "\">" + url.replace(WIKI_PREFIX, "") + "</a></li>");
        }

        sb.append("</ol></body></html>");

        System.out.println(sb.toString());
    }


}
