package de.uop.mics.bayerl.cube.similarity.hierarchies.dbpedia;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by sebastianbayerl on 04/08/15.
 */
public class DBPediaServiceTest {

    private final static String DBPEDIA_CONCEPT = "http://dbpedia.org/resource/Thomas_Morgenstern";

    @Test
    public void testHasConcept() throws Exception {
        assertTrue(DBPediaService.hasConcept(DBPEDIA_CONCEPT));
    }

    @Test
    public void testGetCategories() throws Exception {
        assertEquals(16, DBPediaService.getCategories(DBPEDIA_CONCEPT).size());
    }
}