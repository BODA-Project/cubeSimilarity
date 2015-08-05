package de.uop.mics.bayerl.cube.similarity.hierarchies.dbpedia;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import de.uop.mics.bayerl.cube.Configuration;
import de.uop.mics.bayerl.cube.similarity.concept.SameAsService;
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

        DBPediaService.DATASET.begin(ReadWrite.READ);
        Model model = DBPediaService.DATASET.getDefaultModel();
        List<String> broaders = new ArrayList<>();

        try (QueryExecution queryExecution = QueryExecutionFactory.create(prepareQuery.toString(), model)) {
            ResultSet results = queryExecution.execSelect() ;

            while (results.hasNext()) {
                QuerySolution solution = results.next();
                broaders.add(solution.get("o").toString());
            }
        }

        DBPediaService.DATASET.end();
        return broaders;
    }

    private static List<String> getNarrower(String concept) {
        String queryString = " SELECT ?s WHERE { ?s <http://www.w3.org/2004/02/skos/core#broader> ?o }";
        ParameterizedSparqlString prepareQuery = new ParameterizedSparqlString(queryString);
        prepareQuery.setIri("o", concept);
        LOG.debug(prepareQuery.toString());

        DBPediaService.DATASET.begin(ReadWrite.READ);
        Model model = DBPediaService.DATASET.getDefaultModel();
        List<String> narrowers = new ArrayList<>();

        try (QueryExecution queryExecution = QueryExecutionFactory.create(prepareQuery.toString(), model)) {
            ResultSet results = queryExecution.execSelect() ;

            while (results.hasNext()) {
                QuerySolution solution = results.next();
                narrowers.add(solution.get("s").toString());
            }
        }

        DBPediaService.DATASET.end();
        return narrowers;
    }

    public static List<String> findPath(String c1, String c2, int maxDistance, BfsMode bfsMode) {

        // Get DBPedia concept using the SameAs service
        String dbpC1 = SameAsService.getInstance().getDBPediaSameAs(c1);
        String dbpC2 = SameAsService.getInstance().getDBPediaSameAs(c2);

        if (dbpC1 == null || dbpC2 == null) {
            LOG.info("No suitable DBPedia concept found");
            return new LinkedList<>();
        }

        // Get the categories for the DBPedia concepts
        Set<String> catC1 = DBPediaService.getCategories(dbpC1);
        Set<String> catC2 = DBPediaService.getCategories(dbpC2);

        if (catC1.isEmpty() || catC2.isEmpty()) {
            LOG.info("No DBPedia categories found");
            return new LinkedList<>();
        }

        // TODO is this check necessary?
        if (c1.equals(c2)) {
            List<String> path = new LinkedList<>();
            path.add(c1);
            return path;
        }

        // check if common category is already found
        for (String s : catC1) {
            if (catC2.contains(s)) {
                List<String> path = new LinkedList<>();
                path.add(c1);
                path.add(s);
                path.add(c2);
                return path;
            }
        }

        // do BFS search to find edges
        Queue<String> bfsQueue = new LinkedList<>();

        // stores all already reached nodes and from which node they were reached
        Map<String, String> reachedFrom = new HashMap<>();

        // stores the level of the node (distance to source node)
        Map<String, Integer> levels = new HashMap<>();
        levels.put(c1, 0);

        for (String s : catC1) {
            levels.put(s, 1);
            bfsQueue.add(s);
            reachedFrom.put(s, c1);
        }

        boolean work = true;
        boolean found = false;

        String current = bfsQueue.remove();

        while (work) {
            if (levels.get(current) == maxDistance) {
                work = false;
            } else {
                List<String> nodes = getNextNodes(bfsMode, current);

                for (String node : nodes) {
                    if (!reachedFrom.containsKey(node)) {
                        bfsQueue.add(node);
                        reachedFrom.put(node, current);
                        levels.put(node, levels.get(current) + 1);

                        if (catC2.contains(node)) {
                            current = node;
                            work = false;
                            found = true;
                            break;
                        }
                    }
                }

                if (work && bfsQueue.size() > 0) {
                    current = bfsQueue.remove();
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

//        path.forEach(System.out::println);

        return path;
    }

    private static List<String> getNextNodes(BfsMode bfsMode, String current) {
        List<String> nodes = new ArrayList<>();
        if (bfsMode == BfsMode.BROADER_AND_NARROWER) {
            nodes.addAll(getBroader(current));
            nodes.addAll(getNarrower(current));
        } else if (bfsMode == BfsMode.BROADER_ONLY) {
            nodes.addAll(getBroader(current));
        } else {
            nodes.addAll(getNarrower(current));
        }

        return nodes;
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
