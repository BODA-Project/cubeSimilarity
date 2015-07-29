package de.uop.mics.bayerl.cube.similarity.structural;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by sebastianbayerl on 29/07/15.
 */
public class SameAsExtensionTest {

    @Test
    public void testIsSameAs() throws Exception {
        String c1 = "http://vocabulary.semantic-web.at/AustrianSkiTeam/121";
        String c2 = "http://rdf.freebase.com/ns/m/08zld9";
        assertTrue(SameAsExtension.isSameAs(c1, c2));
        assertTrue(SameAsExtension.isSameAs(c2, c1));
        assertTrue(SameAsExtension.isSameAs(c1, c1));
        assertFalse(SameAsExtension.isSameAs(c1, c1 + "x"));
    }

    @Test
    public void getSimilarity() throws Exception {
        String c1 = "http://vocabulary.semantic-web.at/AustrianSkiTeam/121";
        String c2 = "http://rdf.freebase.com/ns/m/08zld9";
        assertEquals(1d, SameAsExtension.getSimilarity(c1, c2), 0d);
        assertEquals(0d, SameAsExtension.getSimilarity(c1, c1 + "x"), 0d);
    }
}