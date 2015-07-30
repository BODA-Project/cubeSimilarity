package de.uop.mics.bayerl.cube;

import de.uop.mics.bayerl.cube.similarity.hierarchies.dbpedia.BfsMode;
import de.uop.mics.bayerl.cube.similarity.string.DistanceAlgorithm;

/**
 * Created by sebastianbayerl on 29/07/15.
 */
public class Configuration {


    public static final boolean CACHE_SAME_AS = true;
    public static final DistanceAlgorithm STRING_DISTANCE_ALGORITHM = DistanceAlgorithm.LEVENSHTEIN;
    public static double similarity_base = 0.8;
    public static int COMMON_CONCEPT_MAX_DISTANCE = 5;
    public static int MAX_PATH_LENGTH = 5;
    public static BfsMode BFS_MODE = BfsMode.BROADER_AND_NARROWER;
}
