package de.uop.mics.bayerl.cube.similarity.string;

import org.apache.lucene.search.spell.JaroWinklerDistance;
import org.apache.lucene.search.spell.LevensteinDistance;
import org.apache.lucene.search.spell.LuceneLevenshteinDistance;
import org.apache.lucene.search.spell.StringDistance;

/**
 * Created by sebastianbayerl on 23/07/15.
 */
public class ComputeStringDistance {

    public static double compute(String source, String target, DistanceAlgorithm distanceAlgorithm) {
        StringDistance stringDistance;

        if (distanceAlgorithm == DistanceAlgorithm.LEVENSHTEIN) {
            stringDistance = new LevensteinDistance();
        } else if (distanceAlgorithm == DistanceAlgorithm.DAMERAU_LEVENSHTEIN) {
            stringDistance = new LuceneLevenshteinDistance();
        } else if (distanceAlgorithm == DistanceAlgorithm.JARO_WINKLER) {
            stringDistance = new JaroWinklerDistance();
        } else {
            throw new IllegalStateException();
        }

        return stringDistance.getDistance(source, target);
    }
}
