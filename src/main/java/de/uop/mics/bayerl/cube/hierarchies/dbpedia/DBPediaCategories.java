package de.uop.mics.bayerl.cube.hierarchies.dbpedia;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDBFactory;
import de.uop.mics.bayerl.cube.Configuration;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by sebastianbayerl on 24/07/15.
 */
public class DBPediaCategories {

    private final static Logger LOG = Logger.getLogger(DBPediaCategories.class);

    private final static String PREFIXES = "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> " +
                                           "PREFIX dbp:  <http://dbpedia.org/resource/> ";

    private final static String FOLDER = "/Users/sebastianbayerl/Desktop/desktop/sorted/Work/-1_CubeSimilarity/";
//    private final static String INPUT = "dbpadia-categories/";
//    private final static String FILE_ARTICLES = "article_categories_en.nt";
//    private final static String FILE_LABELS = "category_labels_en.nt";
//    private final static String FILE_SKOS = "skos_categories_en.nt";
    private final static String TDB_FOLDER = "TDB/3/";

    public final static Dataset DATASET = TDBFactory.createDataset(FOLDER + TDB_FOLDER);

    // export JENAROOT=/Users/sebastianbayerl/Downloads/apache-jena-2.13.0/
    // ./tdbloader2 --loc=/Users/sebastianbayerl/Desktop/desktop/sorted/Work/-1_CubeSimilarity/TDB/3 /Users/sebastianbayerl/Desktop/desktop/sorted/Work/-1_CubeSimilarity/dbpadia-categories/*.nt


//    public static boolean hasConcept(String concept) {
//        String queryString =  " SELECT ?p ?o WHERE { ?s ?p ?o }";
//        ParameterizedSparqlString prepareQuery = new ParameterizedSparqlString(queryString);
//        prepareQuery.setIri("s", concept);
//        LOG.info(prepareQuery.toString());
//
//        dataset.begin(ReadWrite.READ);
//        Model model = dataset.getDefaultModel();
//        boolean hasConcept;
//
//        try (QueryExecution queryExecution = QueryExecutionFactory.create(prepareQuery.toString(), model)) {
//            ResultSet results = queryExecution.execSelect();
//            hasConcept = results.hasNext();
//        }
//
//        dataset.end();
//
//        return hasConcept;
//    }










}
