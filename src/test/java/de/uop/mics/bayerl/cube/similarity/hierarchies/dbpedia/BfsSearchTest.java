package de.uop.mics.bayerl.cube.similarity.hierarchies.dbpedia;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by sebastianbayerl on 04/08/15.
 */
public class BfsSearchTest {

    @Test
    public void testFindPath() throws Exception {
        String c1 = "http://dbpedia.org/resource/Suicide_booth";
//        String c2 = "http://dbpedia.org/resource/Matt_Groening";
        String c2 = "http://dbpedia.org/resource/SpongeBob_HeroPants";

        System.out.println(BfsSearch.findPath(c1, c2, 10, BfsMode.BROADER_AND_NARROWER));

    }
}