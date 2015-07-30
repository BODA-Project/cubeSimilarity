package de.uop.mics.bayerl.cube.similarity.structural;

import de.uop.mics.bayerl.cube.similarity.concept.SameAsService;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by sebastianbayerl on 29/07/15.
 */
public class SameAsServiceTest {

    @Test
    public void getSimilarity() throws Exception {
        SameAsService sameAsService = SameAsService.getInstance();
        String c1 = "http://vocabulary.semantic-web.at/AustrianSkiTeam/121";
        String c2 = "http://rdf.freebase.com/ns/m/08zld9";
        assertEquals(1d, sameAsService.getSimilarity(c1, c2), 0d);
        assertEquals(1d, sameAsService.getSimilarity(c2, c1), 0d);
        assertEquals(1d, sameAsService.getSimilarity(c1, c1), 0d);
        assertEquals(0d, sameAsService.getSimilarity(c1, c1 + "x"), 0d);
    }
}