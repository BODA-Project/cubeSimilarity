package de.uop.mics.bayerl.cube.similarity.hierarchies.dbpedia;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sebastianbayerl on 02/11/15.
 */
public class DBPediaCategoryW2V {

    public static void main(String[] args) {
        writeMap();
    }


    public static void writeMap() {
        String queryString =  " SELECT DISTINCT ?s  WHERE { ?s " + BfsSearch.SKOS_BROADER + " ?o }";
        ParameterizedSparqlString prepareQuery = new ParameterizedSparqlString(queryString);

        DBPediaService.DATASET.begin(ReadWrite.READ);
        Model model = DBPediaService.DATASET.getDefaultModel();

        List<String> categories = new ArrayList<>();
        Map<String, List<String>> catBroader = new HashMap<>();
        Map<String, List<String>> catNarrower = new HashMap<>();
        try (QueryExecution queryExecution = QueryExecutionFactory.create(prepareQuery.toString(), model)) {
            ResultSet results = queryExecution.execSelect();
            long l = 0;
            while (results.hasNext()) {
                QuerySolution res = results.next();

                categories.add(res.getResource("s").toString());
                l++;

                if (l % 10000 == 0) {
                    System.out.println(l);
                }
            }
            System.out.println(l);
            System.out.println(categories.size());
        }

        DBPediaService.DATASET.end();

        int i = 0;
        for (String category : categories) {
            i++;

            if (i % 10000 == 0) {
                System.out.println(i);
            }
            List<String> localList = new ArrayList<>();
            localList.addAll(BfsSearch.queryTDB(category, BfsSearch.SKOS_BROADER, true));
            localList.addAll(BfsSearch.queryTDB(category, BfsSearch.SKOS_BROADER, false));
            catBroader.put(category, localList);
        }


        // TODO write to file


    }


}
