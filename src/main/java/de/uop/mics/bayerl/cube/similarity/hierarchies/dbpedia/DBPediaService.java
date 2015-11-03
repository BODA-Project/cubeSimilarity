package de.uop.mics.bayerl.cube.similarity.hierarchies.dbpedia;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb.TDBFactory;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by sebastianbayerl on 24/07/15.
 */
public class DBPediaService {

    private final static Logger LOG = Logger.getLogger(DBPediaService.class);

    private final static String FOLDER = "/Users/sebastianbayerl/Desktop/desktop/sorted/Work/-1_CubeSimilarity/";
    private final static String TDB_FOLDER = "TDB/4/";

    private final static String TDB_FULL = "/Volumes/TOSHIBA_2TB/CubeSimilarity/data/TDB/5/";
    private final static String TDB_FULL_LOCAL = "/Users/sebastianbayerl/Desktop/TDB/5/";

    private final static String TDB_WIN = "C:\\Users\\Sebastian\\Desktop\\TDB\\5\\";
    public final static Dataset DATASET = TDBFactory.createDataset(TDB_FULL_LOCAL);



    public final static String DCT_SUBJECT = "http://purl.org/dc/terms/subject";

    // export JENAROOT=/Users/sebastianbayerl/Downloads/apache-jena-2.13.0/
    // tdbloader2 --loc=TDB/3 dbpadia-categories/*.nt


    public static boolean hasConcept(String concept) {
        String queryString =  " SELECT ?p ?o WHERE { ?s ?p ?o }";
        ParameterizedSparqlString prepareQuery = new ParameterizedSparqlString(queryString);
        prepareQuery.setIri("s", concept);
        LOG.info(prepareQuery.toString());

        DATASET.begin(ReadWrite.READ);
        Model model = DATASET.getDefaultModel();
        boolean hasConcept;

        try (QueryExecution queryExecution = QueryExecutionFactory.create(prepareQuery.toString(), model)) {
            ResultSet results = queryExecution.execSelect();
            hasConcept = results.hasNext();
        }

        DATASET.end();

        return hasConcept;
    }

    public static Set<String> getCategories(String c) {
        String queryString =  " SELECT ?o WHERE { ?s ?p ?o }";
        ParameterizedSparqlString prepareQuery = new ParameterizedSparqlString(queryString);
        prepareQuery.setIri("s", c);
        prepareQuery.setIri("p", DCT_SUBJECT);
        //LOG.info(prepareQuery.toString());

        DATASET.begin(ReadWrite.READ);
        Model model = DATASET.getDefaultModel();

        Set<String> categories = new HashSet<>();
        try (QueryExecution queryExecution = QueryExecutionFactory.create(prepareQuery.toString(), model)) {
            ResultSet results = queryExecution.execSelect();

            while (results.hasNext()) {
                QuerySolution res = results.next();
                categories.add(res.getResource("o").toString());
            }
        }

        DATASET.end();

        return categories;
    }

}
