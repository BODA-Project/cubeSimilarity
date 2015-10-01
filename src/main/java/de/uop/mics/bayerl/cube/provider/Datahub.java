package de.uop.mics.bayerl.cube.provider;

import com.google.common.base.CaseFormat;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import de.uop.mics.bayerl.cube.model.Component;
import de.uop.mics.bayerl.cube.model.Cube;
import de.uop.mics.bayerl.cube.model.Dimension;
import de.uop.mics.bayerl.cube.model.Measure;
import de.uop.mics.bayerl.cube.provider.parallel.Request;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.json.JSONArray;

import java.io.*;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Created by sebastianbayerl on 05/08/15.
 */
public class Datahub {

    // TODO get label and concept for cube?
    //SELECT * WHERE { <http://abs.270a.info/dataset/ABS_CENSUS2011_T32> <http://purl.org/dc/terms/title> ?b  . FILTER ( lang(?b) = "en" ) }

    private static final String DATAHUB_API = "http://datahub.io/api/3/action/";
    private static final String GET_PACKAGES = "package_list";
    private static final String GET_DATASET = "package_show?id=";
    private static final String FILE_ENDPOINTS = "endpoints";
    private static final String FILE_ENDPOITNS_FILTERED = "endpointsFiltered";
    private static final String CUBE_FILE = "cubes";


    private final static String GET_DSD = " SELECT * WHERE { ?dsd a <http://purl.org/linked-data/cube#DataStructureDefinition> }";

    private final static String GET_DIMS = " SELECT * WHERE { " +
            " ?dsd a <http://purl.org/linked-data/cube#DataStructureDefinition> ." +
            " ?dsd <http://purl.org/linked-data/cube#component> ?bn ." +
            " ?bn <http://purl.org/linked-data/cube#dimension> ?c ." +
            " }";

    private final static String GET_MEAS = " SELECT * WHERE { " +
            " ?dsd a <http://purl.org/linked-data/cube#DataStructureDefinition> ." +
            " ?dsd <http://purl.org/linked-data/cube#component> ?bn ." +
            " ?bn <http://purl.org/linked-data/cube#measure> ?c ." +
            " }";

    private final static String GET_LABEL = "select ?l where { ?s <http://www.w3.org/2000/01/rdf-schema#label> ?l }";


    public static void main(String[] args) {

        //List<String> datasets = getDatasets();
        //getEndpointsParallel(datasets);

//        System.out.println("##### Get endpoints");
//        persistEndpoints();
//        System.out.println("##### Filter Endpoints");
//        persistFilteredEndpoints();


//        System.out.println("##### Get cubes");
//        getAllCubes();
//        System.out.println("##### Get labels");
//        addLabels();


//        SparqlEndpoint se = new SparqlEndpoint();
//        se.setEndpoint("http://lod.openlinksw.com/sparql");
//
//        getCubes(se);

    }

    private static void getAllCubes() {
        List<SparqlEndpoint> endpoints = read(FILE_ENDPOITNS_FILTERED);
        List<Cube> cubes = new ArrayList<>();
        Set<String> done = new HashSet<>();
        int i = 0;
        for (SparqlEndpoint endpoint : endpoints) {
            i++;
            System.out.println();
            if (!done.contains(endpoint.getEndpoint())) {
                System.out.println("" + i + "/" + endpoints.size() + " Get cubes from: " + endpoint.getEndpoint());
                done.add(endpoint.getEndpoint());
                List<Cube> temp = getCubes(endpoint);
                cubes.addAll(temp);
                System.out.println("New cubes: " + temp.size());
                System.out.println("Total cubes: " + cubes.size());
            } else {
                System.out.println("Already done: " + endpoint.getEndpoint());
            }
        }

        writeCubes(cubes, false);
    }

    private static void addLabels() {
        List<Cube> cubes = readCubes(false);
        Set<String> skip = new HashSet<>();
        for (Cube cube : cubes) {
            if (!skip.contains(cube.getSparqlEndpoint().getEndpoint())) {
                for (Component component : cube.getStructureDefinition().getComponents()) {
                    ParameterizedSparqlString prepareQuery = new ParameterizedSparqlString(GET_LABEL);
                    prepareQuery.setIri("s", component.getConcept());
                    QueryEngineHTTP qeHTTP = (QueryEngineHTTP) QueryExecutionFactory.sparqlService(cube.getSparqlEndpoint().getEndpoint(), prepareQuery.toString());

                    ResultSet results = qeHTTP.execSelect();

                    if (results.hasNext()) {
                        QuerySolution r = results.next();
                        component.setLabel(r.getLiteral("l").getLexicalForm());
                        System.out.println(component.getConcept());
                        System.out.println(component.getLabel());
                    } else {
                        System.out.println("skip " + cube.getSparqlEndpoint().getEndpoint());
                        skip.add(cube.getSparqlEndpoint().getEndpoint());
                    }
                }
            }
        }

        writeCubes(cubes, true);
    }


    private static List<Cube> getCubes(SparqlEndpoint endpoint) {
        ParameterizedSparqlString prepareQuery = new ParameterizedSparqlString(GET_DSD);
        QueryEngineHTTP qeHTTP = (QueryEngineHTTP) QueryExecutionFactory.sparqlService(endpoint.getEndpoint(), prepareQuery.toString());
        ResultSet results = qeHTTP.execSelect();

        // get dataset and dsd concepts
        List<Cube> cubes = new ArrayList<>();
        while (results.hasNext()) {
            QuerySolution r = results.next();

            if (r.get("dsd") != null && r.get("dsd").isResource()) {
                Cube cube = new Cube();
                cubes.add(cube);
                cube.setSparqlEndpoint(endpoint);
                cube.getStructureDefinition().setConcept(r.getResource("dsd").getURI());
            }
        }

        // get dimensions
        for (Cube cube : cubes) {
            prepareQuery = new ParameterizedSparqlString(GET_DIMS);
            prepareQuery.setIri("dsd", cube.getStructureDefinition().getConcept());
            qeHTTP = (QueryEngineHTTP) QueryExecutionFactory.sparqlService(endpoint.getEndpoint(), prepareQuery.toString());
            results = qeHTTP.execSelect();

            while (results.hasNext()) {
                QuerySolution r = results.next();
                Dimension d = new Dimension();
//                System.out.println(r.get("c"));

//                System.out.println(r.get("c").isResource() + "  " + r.get("c").isLiteral());
                d.setConcept(r.getResource("c").getURI());
                cube.getStructureDefinition().getDimensions().add(d);
            }
        }

        // get measures
        for (Cube cube : cubes) {
            prepareQuery = new ParameterizedSparqlString(GET_MEAS);
            prepareQuery.setIri("dsd", cube.getStructureDefinition().getConcept());
            qeHTTP = (QueryEngineHTTP) QueryExecutionFactory.sparqlService(endpoint.getEndpoint(), prepareQuery.toString());
            results = qeHTTP.execSelect();

            while (results.hasNext()) {
                QuerySolution r = results.next();
                Measure m = new Measure();
//                System.out.println(r.get("c").isResource() + "  " + r.get("c").isLiteral());
                m.setConcept(r.getResource("c").getURI());
                cube.getStructureDefinition().getMeasures().add(m);
            }
        }

        return cubes;
    }


    private static void persistFilteredEndpoints() {
        List<SparqlEndpoint> endpoints = read(FILE_ENDPOINTS);
        List<SparqlEndpoint> filtered = filterDSD(endpoints);

        write(filtered, FILE_ENDPOITNS_FILTERED);
    }

    private static List<SparqlEndpoint> filterDSD(List<SparqlEndpoint> endpoints) {
        return endpoints.stream().filter(endpoint -> hasDSD(endpoint)).collect(Collectors.toList());
    }

    private static boolean hasDSD(SparqlEndpoint endpoint) {
        ParameterizedSparqlString prepareQuery = new ParameterizedSparqlString(GET_DSD);
        QueryEngineHTTP qeHTTP = (QueryEngineHTTP) QueryExecutionFactory.sparqlService(endpoint.getEndpoint(), prepareQuery.toString());
        boolean result = false;

        qeHTTP.setTimeout(2000, 2000);

        System.out.println(endpoint.getId() + "    " + endpoint.getEndpoint());

        try {
            ResultSet resultSet = qeHTTP.execSelect();
            result = resultSet.hasNext();
        } catch (Exception e) {
            e.printStackTrace();
        }
        qeHTTP.close();

        // TODO count unreachable, no DSD and the ones with DSD?

        return result;
    }

    private static void persistEndpoints() {
        List<String> datasets = getDatasets();

//        getEndpoint("2000-us-census-rdf");
        System.out.println(datasets.size());


        int i = 0;
        List<SparqlEndpoint> endpoints = new ArrayList<>();
        for (String dataset : datasets) {
            System.out.println("Endpoints: " + endpoints.size() +  "   " + i + "/" + datasets.size() + "      Current dataset: " + dataset);
            Request r = new Request(dataset);
            SparqlEndpoint sparqlEndpoint = r.getEndpoint();

            if (sparqlEndpoint != null) {
                endpoints.add(sparqlEndpoint);
            }

            i++;

            if (i > 100) {
                break;
            }
        }

        System.out.println("datasets:  " + datasets.size());
        System.out.println("endpoints: " + endpoints.size());

        //write(endpoints, FILE_ENDPOINTS);
    }

    private static List<String> getDatasets() {
        HttpResponse<JsonNode> json = null;
        try {
            json = Unirest.get(DATAHUB_API + GET_PACKAGES).asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        JSONArray packageList = json.getBody().getObject().getJSONArray("result");
        List<String> datasets = new ArrayList<>();
        for (int i = 0; i < packageList.length(); i++) {
            datasets.add(packageList.getString(i));
        }

        return datasets;
    }

    private static List<SparqlEndpoint> getEndpointsParallel(List<String> datasets) {
        ExecutorService executor = Executors.newFixedThreadPool(50);

        datasets = datasets.subList(0, 100);

        List<Future<SparqlEndpoint>> futures = new ArrayList<>();
        for (String dataset : datasets) {
            Future<SparqlEndpoint> future = executor.submit(new Request(dataset));
            futures.add(future);
        }

        List<SparqlEndpoint> endpoints = new ArrayList<>();

        for (Future<SparqlEndpoint> future : futures) {
            try {
                SparqlEndpoint sparqlEndpoint = future.get();

                if (sparqlEndpoint != null) {
                    endpoints.add(sparqlEndpoint);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        System.out.println("endpoints: " + endpoints.size());

        return endpoints;
    }


    private static void writeCubes(List<Cube> cubes, boolean withLabels) {
        try {
            String file = CUBE_FILE;
            if (withLabels) {
                file += "_l";
            }
            FileOutputStream fout = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(cubes);
            oos.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Cube> readCubes(boolean withLabels) {
        List<Cube> cubes = new ArrayList<>();
        try {
            String file = CUBE_FILE;
            if (withLabels) {
                file += "_l";
            }
            FileInputStream streamIn = new FileInputStream(file);
            ObjectInputStream objectinputstream = new ObjectInputStream(streamIn);
            cubes = (List<Cube>) objectinputstream.readObject();
            objectinputstream.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return cubes;
    }

    private static void write(List<SparqlEndpoint> endpoints, String file) {
        try {
            FileOutputStream fout = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(endpoints);
            oos.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    private static List<SparqlEndpoint> read(String file) {
        List<SparqlEndpoint> endpoints = new ArrayList<>();
        try {
            FileInputStream streamIn = new FileInputStream(file);
            ObjectInputStream objectinputstream = new ObjectInputStream(streamIn);
            endpoints = (List<SparqlEndpoint>) objectinputstream.readObject();
            objectinputstream.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return endpoints;
    }

    private static String getLabelFromUrl(String concept) {

        // get substring
        int index = concept.lastIndexOf("#");

        if (index == -1) {
            index = concept.lastIndexOf("/");
        }

        String label = concept.substring(index + 1);

        // remove unwanted characters
//        label = label.replaceAll("-", " ");
//        label = label.replaceAll("_", " ");

        try {
            label = URLDecoder.decode(label, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        label = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, label);
        label = label.replaceAll("-", " ");

        return label;
    }

}
