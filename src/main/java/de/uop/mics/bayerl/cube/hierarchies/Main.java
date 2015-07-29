package de.uop.mics.bayerl.cube.hierarchies;

import de.uop.mics.bayerl.cube.hierarchies.dbpedia.BfsMode;
import de.uop.mics.bayerl.cube.hierarchies.dbpedia.BfsSearch;
import de.uop.mics.bayerl.cube.hierarchies.dbpedia.FindTopConcept;

/**
 * Created by sebastianbayerl on 24/07/15.
 */
public class Main {

    public static void main(String[] args) {

        String c1 = "http://dbpedia.org/resource/Category:Futurama";
//        String c2 = "http://dbpedia.org/resource/Category:Humour"; // x x x y

        String c2 = "http://dbpedia.org/resource/Category:Laughter"; // x x x t y


        System.out.println(BfsSearch.getSimilarity(c1, c2, 5, BfsMode.NARROWER_ONLY));
        System.out.println(BfsSearch.getSimilarity(c1, c2, 5, BfsMode.BROADER_ONLY));
        System.out.println(BfsSearch.getSimilarity(c1, c2, 5, BfsMode.BROADER_AND_NARROWER));
        System.out.println(FindTopConcept.getSimilarity(c1, c2, 5));

    }
}
