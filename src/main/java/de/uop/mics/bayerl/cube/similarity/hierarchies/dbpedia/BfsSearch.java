package de.uop.mics.bayerl.cube.similarity.hierarchies.dbpedia;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import de.uop.mics.bayerl.cube.Configuration;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by sebastianbayerl on 29/07/15.
 */
public class BfsSearch {

    private final static Logger LOG = Logger.getLogger(BfsSearch.class);

    private static List<String> getBroader(String concept) {
        String queryString = " SELECT ?o WHERE { ?s <http://www.w3.org/2004/02/skos/core#broader> ?o }";
        ParameterizedSparqlString prepareQuery = new ParameterizedSparqlString(queryString);
        prepareQuery.setIri("s", concept);
        LOG.debug(prepareQuery.toString());

        DBPediaCategories.DATASET.begin(ReadWrite.READ);
        Model model = DBPediaCategories.DATASET.getDefaultModel();
        List<String> broaders = new ArrayList<>();

        try (QueryExecution queryExecution = QueryExecutionFactory.create(prepareQuery.toString(), model)) {
            ResultSet results = queryExecution.execSelect() ;

            while (results.hasNext()) {
                QuerySolution solution = results.next();
                broaders.add(solution.get("o").toString());
            }
        }

        DBPediaCategories.DATASET.end();
        return broaders;
    }

    private static List<String> getNarrower(String concept) {
        String queryString = " SELECT ?s WHERE { ?s <http://www.w3.org/2004/02/skos/core#broader> ?o }";
        ParameterizedSparqlString prepareQuery = new ParameterizedSparqlString(queryString);
        prepareQuery.setIri("o", concept);
        LOG.debug(prepareQuery.toString());

        DBPediaCategories.DATASET.begin(ReadWrite.READ);
        Model model = DBPediaCategories.DATASET.getDefaultModel();
        List<String> narrowers = new ArrayList<>();

        try (QueryExecution queryExecution = QueryExecutionFactory.create(prepareQuery.toString(), model)) {
            ResultSet results = queryExecution.execSelect() ;

            while (results.hasNext()) {
                QuerySolution solution = results.next();
                narrowers.add(solution.get("s").toString());
            }
        }

        DBPediaCategories.DATASET.end();
        return narrowers;
    }

    public static List<String> findPath(String c1, String c2, int maxDistance, BfsMode bfsMode) {

        if (c1.equals(c2)) {
            List<String> path = new LinkedList<>();
            path.add(c1);
            return path;
        }

        // do BFS search to find edges
        Queue<String> q = new LinkedList<>();
        Map<String, String> reachedFrom = new HashMap<>();
        Map<String, Integer> levels = new HashMap<>();
        levels.put(c1, 0);
        Set<String> allNodes = new HashSet<>();
        allNodes.add(c1);

        boolean work = true;
        boolean found = false;
        String current = c1;
        while (work) {
            if (levels.get(current) == maxDistance) {
                work = false;
            } else {
                List<String> nodes = new ArrayList<>();

                if (bfsMode == BfsMode.BROADER_AND_NARROWER) {
                    nodes.addAll(getBroader(current));
                    nodes.addAll(getNarrower(current));
                } else if (bfsMode == BfsMode.BROADER_ONLY) {
                    nodes.addAll(getBroader(current));
                } else {
                    nodes.addAll(getNarrower(current));
                }

                for (String node : nodes) {
                    if (!allNodes.contains(node)) {
                        q.add(node);
                        allNodes.add(node);

                        if (node.equals(c2)) {
                            work = false;
                            found = true;
                            break;
                        } else {
                            reachedFrom.put(node, current);
                            levels.put(node, levels.get(current) + 1);
                        }
                    }
                }

                if (work && q.size() > 0) {
                    current = q.remove();
                } else {
                    work = false;
                }
            }
        }

        List<String> path = new LinkedList<>();
        if (found) {
            path.add(c2);
            // compute path
            path.add(current);

            while (!current.equals(c1)) {
                current = reachedFrom.get(current);
                path.add(current);
            }

            Collections.reverse(path);
        }

//        for (String s : path) {
//            System.out.println(s);
//        }

        return path;

    }

    public static int getDistance(String c1, String c2, int maxDistance, BfsMode bfsMode) {
        return findPath(c1, c2, maxDistance, bfsMode).size() - 1;
    }

    public static double getSimilarity(String c1, String c2) {
        int maxDistance = Configuration.MAX_PATH_LENGTH;
        BfsMode bfsMode = Configuration.BFS_MODE;
        int distance = getDistance(c1, c2, maxDistance, bfsMode);

        if (distance < 0) {
            return 0.0;
        }

        return Math.pow(Configuration.similarity_base, distance);
    }
}
