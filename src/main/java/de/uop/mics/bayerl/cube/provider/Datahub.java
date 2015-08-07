package de.uop.mics.bayerl.cube.provider;

import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import de.uop.mics.bayerl.cube.model.Cube;
import de.uop.mics.bayerl.cube.model.Dimension;
import de.uop.mics.bayerl.cube.model.Measure;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by sebastianbayerl on 05/08/15.
 */
public class Datahub {

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


    public static void main(String[] args) {

        getAllCubes();

    }

    public static void getAllCubes() {
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

        writeCbues(cubes);
    }


    public static List<Cube> getCubes(SparqlEndpoint endpoint) {
        ParameterizedSparqlString prepareQuery = new ParameterizedSparqlString(GET_DSD);
        QueryEngineHTTP qeHTTP = (QueryEngineHTTP) QueryExecutionFactory.sparqlService(endpoint.getEndpoint(), prepareQuery.toString());
        ResultSet results = qeHTTP.execSelect();

        // get dataset and dsd concepts
        List<Cube> cubes = new ArrayList<>();
        while (results.hasNext()) {
            QuerySolution r = results.next();
            if (r.getResource("dsd") != null) {
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
                m.setConcept(r.getResource("c").getURI());
                cube.getStructureDefinition().getMeasures().add(m);
            }
        }

        return cubes;
    }


    public static void persistFilteredEndpoints() {
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

    public static void persistEndpoints() {
        List<String> datasets = getDatasets();

//        getEndpoint("2000-us-census-rdf");
        System.out.println(datasets.size());


        int i = 0;
        List<SparqlEndpoint> endpoints = new ArrayList<>();
        for (String dataset : datasets) {
            System.out.println("Endpoints: " + endpoints.size() +  "   " + i + "/" + datasets.size() + "      Current dataset: " + dataset);
            SparqlEndpoint sparqlEndpoint = getEndpoint(dataset);

            if (sparqlEndpoint != null) {
                endpoints.add(sparqlEndpoint);
            }

            i++;

//            if (i > 100) {
//                break;
//            }
        }

        System.out.println("datasets:  " + datasets.size());
        System.out.println("endpoints: " + endpoints.size());

        write(endpoints, FILE_ENDPOINTS);
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

    private static SparqlEndpoint getEndpoint(String dataset) {

        HttpResponse<JsonNode> json = null;
        try {
            json = Unirest.get(DATAHUB_API + GET_DATASET + dataset).asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }


        JSONObject result = json.getBody().getObject().getJSONObject("result");
        JSONArray resources = result.getJSONArray("resources");

        for (int i = 0; i < resources.length(); i++) {
            JSONObject resource = resources.getJSONObject(i);
            if (resource.getString("format").equals("api/sparql")) {
                SparqlEndpoint sparqlEndpoint = new SparqlEndpoint();
                sparqlEndpoint.setId(dataset);

                if (!result.isNull("url")) {
                    sparqlEndpoint.setUrl(result.getString("url"));
                }

                if (!resource.isNull("name")) {
                    sparqlEndpoint.setName(resource.getString("name"));

                }

                if (!resource.isNull("url")) {
                    sparqlEndpoint.setEndpoint(resource.getString("url"));
                }

                if (!result.isNull("title")) {
                    sparqlEndpoint.setTitle(result.getString("title"));
                }

                return sparqlEndpoint;
            }
        }

        return null;
    }

    private static void writeCbues(List<Cube> cubes) {
        try {
            FileOutputStream fout = new FileOutputStream(CUBE_FILE);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(cubes);
            oos.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Cube> readCubes() {
        List<Cube> cubes = new ArrayList<>();
        try {
            FileInputStream streamIn = new FileInputStream(CUBE_FILE);
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

}
