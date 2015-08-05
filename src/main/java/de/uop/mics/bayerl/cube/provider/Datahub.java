package de.uop.mics.bayerl.cube.provider;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import de.uop.mics.bayerl.cube.model.Cube;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.*;
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

    private final static String ASK_DSD = "ASK { ?s ?p <http://purl.org/linked-data/cube#DataStructureDefinition> }";
    private final static String HAS_DSD = "SELECT ?s WHERE { ?s a <http://purl.org/linked-data/cube#DataStructureDefinition> }";
    private final static String SELECT_DSD = "SELECT ?d ?l1 ?dsd ?dm ?t ?l2 \n" +
            "\n" +
            "WHERE {\n" +
            "\n" +
            "?d <http://www.w3.org/2000/01/rdf-schema#label> ?l1 . \n" +
            "?d <http://purl.org/linked-data/cube#structure> ?dsd . \n" +
            "?dsd a <http://purl.org/linked-data/cube#DataStructureDefinition> . \n" +
            "?o <http://purl.org/linked-data/cube#component> ?bn . \n" +
            "?bn ?c ?dm . \n" +
            "?dm a ?t . \n" +
            "?dm <http://www.w3.org/2000/01/rdf-schema#label> ?l2 \n" +
            "MINUS {\n" +
            "      ?dm a <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> .\n" +
            "   }\n" +
            "}";


    public static void main(String[] args) {

        List<SparqlEndpoint> endpoints = read(FILE_ENDPOITNS_FILTERED);
        Set<String> set = new HashSet<>();
        for (SparqlEndpoint endpoint : endpoints) {
            set.add(endpoint.getEndpoint());
        }

        int count270a = 0;
        for (String s : set) {
            if (s.contains("270a.info/sparql")) {
                count270a++;
            }
        }



        System.out.println(endpoints.size());
        System.out.println(set.size());
        System.out.println(set.size() - count270a + 1);

        List<String> finalList = Lists.newLinkedList(set);
        Collections.sort(finalList);

        for (String s : finalList) {
            System.out.println(s);
        }

    }


    public static List<Cube> getDSDs(SparqlEndpoint endpoint) {
        ParameterizedSparqlString prepareQuery = new ParameterizedSparqlString(SELECT_DSD);
        QueryEngineHTTP qeHTTP = (QueryEngineHTTP) QueryExecutionFactory.sparqlService(endpoint.getEndpoint(), prepareQuery.toString());
        ResultSet results = qeHTTP.execSelect();

        List<Cube> cubes = new ArrayList<>();
        while (results.hasNext()) {
            QuerySolution r = results.next();

            String dataset = r.getResource("d").getURI();
            String label = r.getLiteral("l1").getLexicalForm();
            String component = r.getResource("dm").getURI();
            String compType = r.getResource("t").getURI();
            String compLabel = r.getLiteral("l2").getLexicalForm();

            // TODO validation check?

            // TODO filter
            if (compType.equals("http://purl.org/linked-data/cube#DimensionProperty") || compType.equals("http://purl.org/linked-data/cube#MeasureProperty")) {


                Cube cube = new Cube();
                cube.setSparqlEndpoint(endpoint);

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
        ParameterizedSparqlString prepareQuery = new ParameterizedSparqlString(HAS_DSD);
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
