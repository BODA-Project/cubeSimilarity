package de.uop.mics.bayerl.cube.similarity.hierarchies.dbpedia;

import org.junit.Test;

import java.util.List;

/**
 * Created by sebastianbayerl on 05/11/15.
 */
public class BFSContextTest {

    @Test
    public void testGetPathGraph() throws Exception {
        String c1 = "http://dbpedia.org/resource/Futurama";
        String c2 = "http://dbpedia.org/resource/Screenwriting";

        List<List<String>> paths = BfsSearch.getInstance().findPathsBroader(c1, c2, 7, EdgeMode.BOTH, false);

        BFSContext.getPathGraph(paths);
    }
}