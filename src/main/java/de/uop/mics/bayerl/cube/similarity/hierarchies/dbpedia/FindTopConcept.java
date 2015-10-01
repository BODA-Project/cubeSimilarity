package de.uop.mics.bayerl.cube.similarity.hierarchies.dbpedia;

import de.uop.mics.bayerl.cube.Configuration;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by sebastianbayerl on 29/07/15.
 */
public class FindTopConcept {

    private final static Logger LOG = Logger.getLogger(FindTopConcept.class);

    public static QueryWrapper findCommonTopConcept(String c1, String c2, int maxDistance) {
//         # queries not dependent on number of broader relationships but und number of maxDistance
        List<QueryWrapper> queryWrappers = new LinkedList<>();
        String broaderProperty = "<http://www.w3.org/2004/02/skos/core#broader>";
//        broaderProperty = "<b>";

        // c2 is TOP concept of c1
        for (int i = 0; i < maxDistance; i++) {
            int j = 0;
            StringBuilder sb = new StringBuilder();
            sb.append(" ASK {");

            for (int k = 0; k < i; k++) {
                sb.append(" ?x" + k + " " + broaderProperty + " ?x" + (k + 1) + " .");
            }

            sb.append(" ?x" + i + " " + broaderProperty + " ?y0 }");
            queryWrappers.add(new QueryWrapper(i + 1, j, sb.toString()));
        }

        // c1 is TOP concept of c2
        for (int j = 0; j < maxDistance; j++) {
            int i = 0;
            StringBuilder sb = new StringBuilder();
            sb.append(" ASK {");

            for (int k = 0; k < j; k++) {
                sb.append(" ?y" + k + " " + broaderProperty + " ?y" + (k + 1) + " .");

            }

            sb.append(" ?y" + j + " " + broaderProperty + " ?x0 }");
            queryWrappers.add(new QueryWrapper(i, j + 1, sb.toString()));
        }

        // Common TOP concept
        for (int i = 0; i < maxDistance - 1; i++) {
            for (int j = 0; j < maxDistance - 1; j++) {

                StringBuilder sb = new StringBuilder();
                sb.append(" ASK {");

                for (int k = 0; k < i; k++) {
                    sb.append(" ?x" + k + " " + broaderProperty + " ?x" + (k + 1) + " .");
                }

                for (int k = 0; k < j; k++) {
                    sb.append(" ?y" + k + " " + broaderProperty + " ?y" + (k + 1) + " .");
                }

                sb.append(" ?x" + i + " " + broaderProperty + " ?t .");
                sb.append(" ?y" + j + " " + broaderProperty + " ?t } ");
                queryWrappers.add(new QueryWrapper(i + 1, j + 1, sb.toString()));
            }
        }

        return findDistance(c1, c2, queryWrappers);
    }

    private static QueryWrapper findDistance(String c1, String c2, List<QueryWrapper> queryWrappers) {
        DBPediaService.DATASET.begin(ReadWrite.READ);
        Model model = DBPediaService.DATASET.getDefaultModel();

        for (QueryWrapper queryWrapper : queryWrappers) {
            LOG.debug(queryWrapper.getQuery());
            ParameterizedSparqlString prepareQuery = new ParameterizedSparqlString(queryWrapper.getQuery());
            prepareQuery.setIri("x0", c1);
            prepareQuery.setIri("y0", c2);
            //LOG.info(prepareQuery.toString());

            try (QueryExecution queryExecution = QueryExecutionFactory.create(prepareQuery.toString(), model)) {
                if (queryExecution.execAsk()) {
                    return queryWrapper;
                }
            }
        }
        DBPediaService.DATASET.end();
        return null;
    }

    public static double getSimilarity(String c1, String c2) {
        int maxDistance = Configuration.COMMON_CONCEPT_MAX_DISTANCE;
        QueryWrapper queryWrapper = findCommonTopConcept(c1, c2, maxDistance);

        if (queryWrapper == null) {
            return 0.0;
        }

        // TODO compute similarity better?
        int distance = queryWrapper.getI() + queryWrapper.getJ();

        // boost if one of the concepts is the top concept
//        if (queryWrapper.getI() == 0 || queryWrapper.getJ() == 0) {
//            distance -= 1;
//        }

        return Math.pow(Configuration.similarity_base, distance);
    }
}
