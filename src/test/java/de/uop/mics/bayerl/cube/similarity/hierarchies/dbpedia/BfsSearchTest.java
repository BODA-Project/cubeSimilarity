package de.uop.mics.bayerl.cube.similarity.hierarchies.dbpedia;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by sebastianbayerl on 04/08/15.
 */
public class BfsSearchTest {

    @Test
    public void testFindPathsBroader() throws Exception {
        String c1 = "http://dbpedia.org/resource/Futurama";
        String c2 = "http://dbpedia.org/resource/Lucille_Lortel_Awards";

        List<List<String>> paths = BfsSearch.getInstance().findPathsBroader(c1, c2, 5, EdgeMode.BOTH, false);

        for (List<String> path : paths) {
            for (String s : path) {
                System.out.println(s);
            }

            System.out.println();
        }
    }

    @Test
    public void testFindPath() throws Exception {
//        String c1 = "http://dbpedia.org/resource/Futurama";
//        String c2 = "http://dbpedia.org/resource/Matt_Groening";
//        String c2 = "http://dbpedia.org/resource/Lucille_Lortel_Awards";
//        String c2 = "http://dbpedia.org/resource/Bender_(Futurama)";
//        String c2 = "http://dbpedia.org/resource/The_Simpsons";


        String c1 = "http://dbpedia.org/resource/Futurama";
        String c2 = "http://dbpedia.org/resource/Lucille_Lortel_Awards";

        List<String> path = BfsSearch.getInstance().findPath(c1, c2, 10, EdgeMode.BOTH, DBPediaProperty.BROADER);

        List<String> result = new ArrayList<>();
        result.add("http://dbpedia.org/resource/Futurama");
        result.add("http://dbpedia.org/resource/Category:Television_shows_set_in_New_York_City");
        result.add("http://dbpedia.org/resource/Category:New_York_City_in_fiction");
        result.add("http://dbpedia.org/resource/Category:Culture_of_New_York_City");
        result.add("http://dbpedia.org/resource/Lucille_Lortel_Awards");

        System.out.println(path);

        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i), path.get(i));
        }
    }

    @Test
    public void testFindPathWikiLink() throws Exception {
        String c1 = "http://dbpedia.org/resource/Futurama";
        String c2 = "http://dbpedia.org/resource/Lucille_Lortel_Awards";
        System.out.println(BfsSearch.getInstance().findDirectPath(c1, c2, 5));
    }

    @Test
    public void testGetPathContext() throws Exception {
        String c1 = "http://dbpedia.org/resource/Futurama";
//        String c2 = "http://dbpedia.org/resource/Low_comedy";
        String c2 = "http://dbpedia.org/resource/Screenwriting";

        List<String> path = BfsSearch.getInstance().findPath(c1, c2, 10, EdgeMode.BOTH, DBPediaProperty.BROADER);
        System.out.println("Path: ");
        System.out.println(path);
        BFSContext.getPathContext(path, false);

    }
}