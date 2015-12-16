package de.uop.mics.bayerl.cube.provider;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by sebastianbayerl on 05/08/15.
 */
public class Datahub {

    // TODO get label and concept for cube?
    //SELECT * WHERE { <http://abs.270a.info/dataset/ABS_CENSUS2011_T32> <http://purl.org/dc/terms/title> ?b  . FILTER ( lang(?b) = "en" ) }

    private static final String DATAHUB_API = "http://datahub.io/api/3/action/";
    private static final String GET_PACKAGES = "package_list";
    private static final String GET_DATASET = "package_show?id=";

    private static final String FILE_ALL_DATASETS = "0-all-datasets.txt";
    private static final String FILE_DATASET_WITH_SPARQL = "1-dataset-sparql.txt";
    private static final String FILE_ENDPOINTS_WITH_CUBE = "2-endpoints-cubes.txt";
    public static final String FILE_ENDPOINTS_WITH_CUBE_CLEANED = "3-endpoints-cubes-cleaned.txt";

    public final static String GET_DSD = " SELECT * WHERE { ?dsd a <http://purl.org/linked-data/cube#DataStructureDefinition> }";


    public static void main(String[] args) {
//        getAllDatasets();
//        getDatasetsWithSparqlEndpoint();
//        getEndpointsWithCubes();
        getCleanedEndpoints();
    }

    private static void getCleanedEndpoints() {
        String fileIn = FILE_ENDPOINTS_WITH_CUBE;
        String fileOut = FILE_ENDPOINTS_WITH_CUBE_CLEANED;

        saveCreateFile(fileOut);

        try {
            Set<String> endpoints = new HashSet<>();
            Files.lines(Paths.get(fileIn)).forEach(l -> {
                JSONObject json = new JSONObject(l);
                JSONArray resources = json.getJSONObject("result").getJSONArray("resources");

                for (int i = 0; i < resources.length(); i++) {
                    JSONObject resource = resources.getJSONObject(i);
                    if (resource.getString("format").equals("api/sparql")) {

                        String url = resource.getString("url");
                        if (!endpoints.contains(url)) {
                            System.out.println(resource.getString("url"));
                            try {
                                Files.write(Paths.get(fileOut), (l + "\n").getBytes(), StandardOpenOption.APPEND);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            endpoints.add(url);
                        }

                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static int exceptionCount = 0;

    /**
     * Check if the given endpoint contains at least one DSD.
     *
     * @param endpoint The endpoint.
     * @return True if there is a DSD available.
     */
    private static boolean hasDSD(String endpoint) {
        ParameterizedSparqlString prepareQuery = new ParameterizedSparqlString(GET_DSD);
        QueryEngineHTTP qeHTTP = (QueryEngineHTTP) QueryExecutionFactory.sparqlService(endpoint, prepareQuery.toString());
        boolean result = false;
        qeHTTP.setTimeout(2000, 2000);

        try {
            ResultSet resultSet = qeHTTP.execSelect();
            result = resultSet.hasNext();
        } catch (Exception e) {
            exceptionCount++;
            e.printStackTrace();
        }
        qeHTTP.close();

        return result;
    }

    /**
     * Filter the sparql endpoints. Check if there is a cube available.
     */
    public static void getEndpointsWithCubes() {
        String fileIn = FILE_DATASET_WITH_SPARQL;
        String fileOut = FILE_ENDPOINTS_WITH_CUBE;
        exceptionCount = 0;

        saveCreateFile(fileOut);

        try {
            Files.lines(Paths.get(fileIn)).forEach(l -> {
                JSONObject json = new JSONObject(l);
                JSONArray resources = json.getJSONObject("result").getJSONArray("resources");

                for (int i = 0; i < resources.length(); i++) {
                    JSONObject resource = resources.getJSONObject(i);
                    if (resource.getString("format").equals("api/sparql")) {
                        if (hasDSD(resource.getString("url"))) {
                            try {
                                Files.write(Paths.get(fileOut), (l + "\n").getBytes(), StandardOpenOption.APPEND);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("exceptions: " + exceptionCount);
    }

    /**
     * Search for datasets that have a sparql endpoint.
     */
    public static void getDatasetsWithSparqlEndpoint() {
        String fileIn = FILE_ALL_DATASETS;
        String fileOut = FILE_DATASET_WITH_SPARQL;

        saveCreateFile(fileOut);


        try {

            System.out.println("with sparql endpoint: " + Files.lines(Paths.get(fileIn)).filter(s -> s.contains("\"format\": \"api/sparql\"")).count());

            Files.lines(Paths.get(fileIn)).filter(s -> s.contains("\"format\": \"api/sparql\"")).forEach(l -> {
                try {
                    Files.write(Paths.get(fileOut), (l + "\n").getBytes(), StandardOpenOption.APPEND);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves all Datasets from datahub.io and stores the json responses in a file.
     */
    private static void getAllDatasets() {
        List<String> datasets = getDatasets();
        System.out.println("Dataset count: " + datasets.size());

        String file = FILE_ALL_DATASETS;
        saveCreateFile(file);

        int i = 0;
        for (String dataset : datasets) {
            System.out.println("" + i + "/" + datasets.size());
            i++;

            HttpResponse<String> response = null;
            try {
                response = Unirest.get(DATAHUB_API + GET_DATASET + dataset).asString();
            } catch (UnirestException e) {
                e.printStackTrace();
            }

            if (response != null) {
                try {
                    Files.write(Paths.get(file), (response.getBody() + "\n").getBytes(), StandardOpenOption.APPEND);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Retrieves a list of all available datasets from datahub.io.
     *
     * @return A list of dataset ids.
     */
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

    /**
     * Save way to create a file.
     *
     * @param file The name of the file to create.
     */
    private static void saveCreateFile(String file) {
        // reset file
        try {
            Files.delete(Paths.get(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Files.createFile(Paths.get(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
