package de.uop.mics.bayerl.cube.provider.wordsimilarity;

import de.uop.mics.bayerl.cube.similarity.hierarchies.dbpedia.DBPediaService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by sebastianbayerl on 02/11/15.
 */
public class WordSimHelper {

    private final static String PATH = "/Users/sebastianbayerl/Desktop/work/data/wordsim353/wordsim353/combined.csv";
    private final static String WIKI_PREFIX = "https://en.wikipedia.org/wiki/";


    public static void main(String[] args) {


        help();

    }



    private static void help() {

        Set<String> words = new HashSet<>();

        try {

            try (Stream<String> stream = Files.lines(Paths.get(PATH))) {
                stream.skip(1).forEach(w -> {
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


//        System.out.println(wordList.size());
//        wordList.forEach(System.out::println);

        List<String> urls = new ArrayList<>();
        for (String s : wordList) {
            String first = String.valueOf(s.charAt(0));

            String upper = first.toUpperCase();

            urls.add(WIKI_PREFIX + upper + s.substring(1));


        }

//        StringBuilder sb = new StringBuilder();
//        sb.append("<!DOCTYPE html> <html><body><ol>");
//
//        for (String url : urls) {
//            sb.append("<li><a href=\"" + url + "\">" + url.replace(WIKI_PREFIX, "") + "</a></li>");
//        }
//
//        sb.append("</ol></body></html>");
//
//        System.out.println(sb.toString());


        System.out.println(DBPediaService.getCategories("http://dbpedia.org/resource/American"));

    }


}
