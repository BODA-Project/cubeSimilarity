package de.uop.mics.bayerl.cube;

import de.uop.mics.bayerl.cube.similarity.hierarchies.dbpedia.DBPediaProperty;
import de.uop.mics.bayerl.cube.similarity.hierarchies.dbpedia.EdgeMode;
import de.uop.mics.bayerl.cube.similarity.string.DistanceAlgorithm;

/**
 * Created by sebastianbayerl on 29/07/15.
 */
public class Configuration {


    public static final boolean CACHE_SAME_AS = true;
    public static final DistanceAlgorithm STRING_DISTANCE_ALGORITHM = DistanceAlgorithm.LEVENSHTEIN;
    public static double similarity_base = 0.8;
    public static int COMMON_CONCEPT_MAX_DISTANCE = 5;
    public static int MAX_PATH_LENGTH_CATEGORY = 5;
    public static int MAX_PATH_LENGTH_PAGELINK = 3;
    public static EdgeMode EDGE_MODE = EdgeMode.BOTH;
    public static DBPediaProperty dbPediaProperty = DBPediaProperty.BROADER;
}
