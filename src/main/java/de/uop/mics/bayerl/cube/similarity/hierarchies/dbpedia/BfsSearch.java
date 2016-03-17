package de.uop.mics.bayerl.cube.similarity.hierarchies.dbpedia;

import de.uop.mics.bayerl.cube.Configuration;
import de.uop.mics.bayerl.cube.similarity.concept.SameAsService;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by sebastianbayerl on 29/07/15.
 */
public class BfsSearch {

    private boolean doCache = true;
    private Map<String, List<String>> cacheBroader;
    private Map<String, List<List<String>>> cachePage;
    private Map<String, Integer> cacheDistance;
    private static BfsSearch instance;

    private final static String PAGE_LINK_PROPERTY = "<http://dbpedia.org/ontology/wikiPageWikiLink>";
    public final static String SKOS_BROADER = "<http://www.w3.org/2004/02/skos/core#broader>";
    private final static Logger LOG = Logger.getLogger(BfsSearch.class);

    private BfsSearch() {
        cacheBroader = new HashMap<>();
        cachePage = new HashMap<>();
        cacheDistance = new HashMap<>();
    }

    public static BfsSearch getInstance() {
        if (instance == null) {
            instance = new BfsSearch();
        }

        return instance;
    }

    public void resetCaches() {
        cacheBroader = new HashMap<>();
        cachePage = new HashMap<>();
    }

    public List<String> getNextNodes(String concept, EdgeMode edgeMode, String property) {
        List<String> nodes = new ArrayList<>();

        if (edgeMode == EdgeMode.BOTH) {
            nodes.addAll(queryTDB(concept, property, true));
            nodes.addAll(queryTDB(concept, property, false));
        } else if (edgeMode == EdgeMode.INCOMING) {
            nodes.addAll(queryTDB(concept, property, true));
        } else {
            nodes.addAll(queryTDB(concept, property, false));
        }

        return nodes;
    }


    public List<String> queryTDB(String concept, String property, boolean incoming) {

        String key = concept + property + incoming;
        if (doCache) {
            if (cacheBroader.containsKey(key)) {
                return cacheBroader.get(key);
            }
        }

        List<String> concepts = new ArrayList<>();
        String queryString = " SELECT ";

        if (incoming) {
            queryString += "?s ";
        } else {
            queryString += "?o ";
        }

        queryString += "WHERE { ?s " + property + " ?o }";

        ParameterizedSparqlString prepareQuery = new ParameterizedSparqlString(queryString);

        if (incoming) {
            prepareQuery.setIri("o", concept);
        } else {
            prepareQuery.setIri("s", concept);
        }

        //LOG.info("query: " + prepareQuery.toString());

        DBPediaService.DATASET.begin(ReadWrite.READ);
        Model model = DBPediaService.DATASET.getDefaultModel();

        try (QueryExecution queryExecution = QueryExecutionFactory.create(prepareQuery.toString(), model)) {
            ResultSet results = queryExecution.execSelect() ;

            while (results.hasNext()) {
                QuerySolution solution = results.next();

                if (incoming) {
                    concepts.add(solution.get("s").toString());
                } else {
                    concepts.add(solution.get("o").toString());
                }
            }
        }

        DBPediaService.DATASET.end();

        if (doCache) {
            cacheBroader.put(key, concepts);
        }

        return concepts;
    }

    public List<List<String>> findPathsBroader(String c1, String c2, int maxDistance, EdgeMode edgeMode, boolean onlyShortestPaths) {
        List<List<String>> paths = new ArrayList<>();

        if (c1.equals(c2)) {
            List<String> path = new ArrayList<>();
            path.add(c1);
            paths.add(path);
            return paths;
        }

        // do BFS search to find edges
        Queue<String> bfsQueue = new LinkedList<>();

        // stores all already reached nodes and from which node they were reached
        Map<String, String> reachedFrom = new HashMap<>();

        // stores the level of the node (distance to source node)
        Map<String, Integer> levels = new HashMap<>();
        levels.put(c1, 0);


        // Get DBPedia concept using the SameAs service
        String dbpC1 = SameAsService.getInstance().getDBPediaSameAs(c1);
        String dbpC2 = SameAsService.getInstance().getDBPediaSameAs(c2);

        if (dbpC1 == null || dbpC2 == null) {
            System.out.println("No suitable DBPedia concept found");
            return new ArrayList<>();
        }

        // Get the categories for the DBPedia concepts
        Set<String> sources = DBPediaService.getCategories(dbpC1);
        Set<String> targets = DBPediaService.getCategories(dbpC2);

        if (sources.isEmpty() ) {
            System.out.println("No DBPedia categories found for source: " + dbpC1);
            return new ArrayList<>();
        }

        if (targets.isEmpty()) {
            System.out.println("No DBPedia categories found for target: " + dbpC2);
            return new ArrayList<>();
        }

        // check if common categories are already found
        for (String s : sources) {
            if (targets.contains(s)) {
                List<String> path = new ArrayList<>();
                path.add(c1);
                path.add(s);
                path.add(c2);
                paths.add(path);
            }
        }

        if (paths.size() > 0) {
            return paths;
        }

        // initialize data structures
        for (String s : sources) {
            levels.put(s, 1);
            bfsQueue.add(s);
            reachedFrom.put(s, c1);
        }

        boolean work = true;
        String current = bfsQueue.remove();
        int oldLevel = 1;

        while (work) {
            if (levels.get(current) == maxDistance) {
                work = false;

            } else if (onlyShortestPaths && oldLevel < levels.get(current) && paths.size() > 0) {
                work = false;
            } else {
                List<String>  nodes = BfsSearch.getInstance().getNextNodes(current, edgeMode, BfsSearch.SKOS_BROADER);

                for (String node : nodes) {
                    if (!reachedFrom.containsKey(node)) {
                        bfsQueue.add(node);
                        reachedFrom.put(node, current);
                        levels.put(node, levels.get(current) + 1);

                        if (targets.contains(node)) {
                            current = node;
                            paths.add(extractPath(c1, c2, current, reachedFrom));
                        }
                    }
                }

                if (bfsQueue.size() > 0) {
                    current = bfsQueue.remove();
                } else {
                    work = false;
                }
            }
        }

//        path.forEach(System.out::println);

        return paths;
    }

    private List<String> extractPath(String c1, String c2, String current, Map<String, String> reachedFrom) {
        String c = current;
        List<String> path = new ArrayList<>();

        path.add(c2);

        // compute path
        path.add(c);

        while (!c.equals(c1)) {
            c = reachedFrom.get(c);
            path.add(c);
        }

        Collections.reverse(path);

        return path;
    }

    public List<List<String>> findPathsWikiLinks(String source, String target, int length) {
        if (length >= 3) {
            System.out.println(length);
        }
        String key = source + target + length;

        if (doCache) {
            if (cachePage.containsKey(key)) {
                return cachePage.get(key);
            }
        }

        List<List<String>> paths = new ArrayList<>();
        DBPediaService.DATASET.begin(ReadWrite.READ);
        Model model = DBPediaService.DATASET.getDefaultModel();

        StringBuilder sb = new StringBuilder();
        sb.append("select * where {");
        sb.append(" <" + source + "> " + PAGE_LINK_PROPERTY);

        for (int i = 0; i < length; i++) {
            String current = "?c" + i;
            sb.append(" " + current + " . " + current + " " + PAGE_LINK_PROPERTY);
        }

        sb.append(" <" + target + "> }");

        String query = sb.toString();

//        System.out.println(query);
        try (QueryExecution queryExecution = QueryExecutionFactory.create(query, model)) {
            ResultSet results = queryExecution.execSelect();

            while (results.hasNext()) {
                QuerySolution solution = results.next();
                List<String> path = new ArrayList<>();
                path.add(source);
                for (int j = 0; j < length; j++) {
                    path.add(solution.getResource("c" + j).toString());
                }
                path.add(target);

                paths.add(path);
            }

//            System.out.println(paths.size());
//            queryExecution.close();
        }

        DBPediaService.DATASET.end();

        if (doCache) {
            cachePage.put(key, paths);
        }

        return paths;
    }

    public List<String> findDirectPath(String source, String target, int maxLength) {

        for (int i = 0; i < maxLength; i++) {
            List<List<String>> paths = findPathsWikiLinks(source, target, i);

            if (paths.size() > 0) {
                return paths.get(0);
            }
        }

        return new ArrayList<>();
    }


    public List<String> findPath(String c1, String c2, int maxDistance, EdgeMode edgeMode, DBPediaProperty useProperty) {
        Set<String> sources;
        Set<String> targets = new HashSet<>();
        // do BFS search to find edges
        Queue<String> bfsQueue = new LinkedList<>();

        // stores all already reached nodes and from which node they were reached
        Map<String, String> reachedFrom = new HashMap<>();

        // stores the level of the node (distance to source node)
        Map<String, Integer> levels = new HashMap<>();
        levels.put(c1, 0);


        if (DBPediaProperty.BROADER == useProperty) {
            // Get DBPedia concept using the SameAs service
            String dbpC1 = SameAsService.getInstance().getDBPediaSameAs(c1);
            String dbpC2 = SameAsService.getInstance().getDBPediaSameAs(c2);

            if (dbpC1 == null || dbpC2 == null) {
                LOG.info("No suitable DBPedia concept found");
                return new LinkedList<>();
            }

            // Get the categories for the DBPedia concepts
            sources = DBPediaService.getCategories(dbpC1);
            targets = DBPediaService.getCategories(dbpC2);

            if (sources.isEmpty() ) {
                LOG.info("No DBPedia categories found for source: " + dbpC1);
                return new LinkedList<>();
            }

            if (targets.isEmpty()) {
                LOG.info("No DBPedia categories found for target: " + dbpC2);
                return new LinkedList<>();
            }

            // TODO is this check necessary?
            if (c1.equals(c2)) {
                List<String> path = new LinkedList<>();
                path.add(c1);
                return path;
            }

            // check if common category is already found
            for (String s : sources) {
                if (targets.contains(s)) {
                    List<String> path = new LinkedList<>();
                    path.add(c1);
                    path.add(s);
                    path.add(c2);
                    return path;
                }
            }

            for (String s : sources) {
                levels.put(s, 1);
                bfsQueue.add(s);
                reachedFrom.put(s, c1);
            }
        } else {
            //sources.add(c1);
            bfsQueue.add(c1);
            targets.add(c2);
        }

        boolean work = true;
        boolean found = false;

        String current = bfsQueue.remove();

        while (work) {
            if (levels.get(current) == maxDistance) {
                work = false;
            } else {
                List<String> nodes;
                if (useProperty == DBPediaProperty.BROADER) {
                    nodes = getNextNodes(current, edgeMode, SKOS_BROADER);
                } else {
                    nodes = getNextNodes(current, edgeMode, PAGE_LINK_PROPERTY);
                }

                for (String node : nodes) {
                    if (!reachedFrom.containsKey(node)) {
                        bfsQueue.add(node);
                        reachedFrom.put(node, current);
                        levels.put(node, levels.get(current) + 1);

                        if (targets.contains(node)) {
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
            if (useProperty == DBPediaProperty.BROADER) {
                path.add(c2);
            }

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

    public int getDistance(String c1, String c2, int maxDistance, EdgeMode edgeMode, DBPediaProperty dbPediaProperty) {
        String key = c1 + "<>" + c2;

        if (!cacheDistance.containsKey(key)) {
            cacheDistance.put(key, findPath(c1, c2, maxDistance, edgeMode, dbPediaProperty).size() - 1);
        }

        return cacheDistance.get(key);
    }

    public double getSimilarity(String c1, String c2, DBPediaProperty dbPediaProperty) {
        int distance = 0;
        if (dbPediaProperty == DBPediaProperty.BROADER) {
            distance = getDistance(c1, c2, Configuration.MAX_PATH_LENGTH_CATEGORY, Configuration.EDGE_MODE, DBPediaProperty.BROADER);
        } else if (dbPediaProperty == DBPediaProperty.PAGE_LINK) {
            distance = findDirectPath(c1, c2, Configuration.MAX_PATH_LENGTH_PAGELINK).size() - 1;
        }

        if (distance < 0) {
            return 0.0;
        }


        return Math.pow(Configuration.similarity_base, distance);
    }

}
