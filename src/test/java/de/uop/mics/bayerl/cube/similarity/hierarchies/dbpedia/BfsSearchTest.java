package de.uop.mics.bayerl.cube.similarity.hierarchies.dbpedia;

import org.junit.Test;

import java.util.List;

/**
 * Created by sebastianbayerl on 04/08/15.
 */
public class BfsSearchTest {

    @Test
    public void testFindPath() throws Exception {
//        String c1 = "http://dbpedia.org/resource/Futurama";
//        String c2 = "http://dbpedia.org/resource/Matt_Groening";
//        String c2 = "http://dbpedia.org/resource/Lucille_Lortel_Awards";
//        String c2 = "http://dbpedia.org/resource/Bender_(Futurama)";
//        String c2 = "http://dbpedia.org/resource/The_Simpsons";


        String c1 = "http://dbpedia.org/resource/Futurama";
        String c2 = "http://dbpedia.org/resource/Lucille_Lortel_Awards";

        System.out.println(BfsSearch.getInstance().findPath(c1, c2, 10, EdgeMode.BOTH, DBPediaProperty.BROADER));

    }

    @Test
    public void testFindPathWikiLink() throws Exception {
        String c1 = "http://dbpedia.org/resource/Futurama";
        String c2 = "http://dbpedia.org/resource/Lucille_Lortel_Awards";
        System.out.println(BfsSearch.findDirectPath(c1, c2, 5));
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