package de.uop.mics.bayerl.cube.provider.datahub;

import de.uop.mics.bayerl.cube.model.Cube;
import de.uop.mics.bayerl.cube.provider.SparqlEndpoint;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sebastianbayerl on 29/10/15.
 */
public class DatahubCubes {


    private final String GET_DIMS_WITH_LABLES = "SELECT ?c ?la ?pref WHERE {" +
            " ?dsd a <http://purl.org/linked-data/cube#DataStructureDefinition> ." +
            " ?dsd <http://purl.org/linked-data/cube#component> ?bn ." +
            " ?bn <http://purl.org/linked-data/cube#dimension> ?c ." +
            " OPTIONAL { ?c <http://www.w3.org/2000/01/rdf-schema#label> ?la }" +
            " OPTIONAL { ?c <http://www.w3.org/2004/02/skos/core#prefLabel> ?pref . FILTER (lang(?pref) = 'en') }" +
            " }";

    private final static String GET_MEAS_WITH_LABELS = " SELECT * WHERE { " +
            " ?dsd a <http://purl.org/linked-data/cube#DataStructureDefinition> ." +
            " ?dsd <http://purl.org/linked-data/cube#component> ?bn ." +
            " ?bn <http://purl.org/linked-data/cube#measure> ?c ." +
            " OPTIONAL { ?c <http://www.w3.org/2000/01/rdf-schema#label> ?la }" +
            " OPTIONAL { ?c <http://www.w3.org/2004/02/skos/core#prefLabel> ?pref . FILTER (lang(?pref) = 'en') }" +
            " }";


    public static void main(String[] args) {

        getAllCubes();

    }

    private static void getAllCubes() {
//        String fileIn = Datahub.FILE_ENDPOINTS_WITH_CUBE_CLEANED;
        String fileIn = "endpoints_cubes_partial.txt";


        try {
            Files.lines(Paths.get(fileIn)).forEach(l -> {
                JSONObject json = new JSONObject(l);
                JSONObject result = json.getJSONObject("result");
                String title = result.getString("title");
                String name = result.getString("name");

                JSONArray resources = json.getJSONObject("result").getJSONArray("resources");

                for (int i = 0; i < resources.length(); i++) {
                    JSONObject resource = resources.getJSONObject(i);
                    if (resource.getString("format").equals("api/sparql")) {
                        String id = resource.getString("id");
                        String url = resource.getString("url");

                        SparqlEndpoint sparqlEndpoint = new SparqlEndpoint();
                        sparqlEndpoint.setId(id);
                        sparqlEndpoint.setEndpoint(url);
                        sparqlEndpoint.setTitle(title);
                        sparqlEndpoint.setName(name);

                        List<Cube> cubes = getCubesFromEndpoint(sparqlEndpoint);
                    }
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * Retrieves cubes from an endpoint.
     *
     * @param sparqlEndpoint The endpoint.
     */
    private static List<Cube> getCubesFromEndpoint(SparqlEndpoint sparqlEndpoint) {
        List<Cube> cubes = new ArrayList<>();

        return cubes;
    }


    /*
    *
    * rework from here
    *
     */

//
//    private static void getAllCubes() {
//        List<SparqlEndpoint> endpoints = read(FILE_ENDPOITNS_FILTERED);
//        List<Cube> cubes = new ArrayList<>();
//        Set<String> done = new HashSet<>();
//        int i = 0;
//        for (SparqlEndpoint endpoint : endpoints) {
//            i++;
//            System.out.println();
//            if (!done.contains(endpoint.getEndpoint())) {
//                System.out.println("" + i + "/" + endpoints.size() + " Get cubes from: " + endpoint.getEndpoint());
//                done.add(endpoint.getEndpoint());
//                List<Cube> temp = getCubes(endpoint);
//                cubes.addAll(temp);
//                System.out.println("New cubes: " + temp.size());
//                System.out.println("Total cubes: " + cubes.size());
//            } else {
//                System.out.println("Already done: " + endpoint.getEndpoint());
//            }
//        }
//
//        writeCubes(cubes, false);
//    }

//    private static void addLabels() {
//        List<Cube> cubes = readCubes(false);
//        Set<String> skip = new HashSet<>();
//        for (Cube cube : cubes) {
//            if (!skip.contains(cube.getSparqlEndpoint().getEndpoint())) {
//                for (Component component : cube.getStructureDefinition().getComponents()) {
//                    ParameterizedSparqlString prepareQuery = new ParameterizedSparqlString(GET_LABEL);
//                    prepareQuery.setIri("s", component.getConcept());
//                    QueryEngineHTTP qeHTTP = (QueryEngineHTTP) QueryExecutionFactory.sparqlService(cube.getSparqlEndpoint().getEndpoint(), prepareQuery.toString());
//
//                    ResultSet results = qeHTTP.execSelect();
//
//                    if (results.hasNext()) {
//                        QuerySolution r = results.next();
//                        component.setLabel(r.getLiteral("l").getLexicalForm());
//                        System.out.println(component.getConcept());
//                        System.out.println(component.getLabel());
//                    } else {
//                        System.out.println("skip " + cube.getSparqlEndpoint().getEndpoint());
//                        skip.add(cube.getSparqlEndpoint().getEndpoint());
//                    }
//                }
//            }
//        }
//
//        writeCubes(cubes, true);
//    }
//
//
//    private static List<Cube> getCubes(SparqlEndpoint endpoint) {
//        ParameterizedSparqlString prepareQuery = new ParameterizedSparqlString(GET_DSD);
//        QueryEngineHTTP qeHTTP = (QueryEngineHTTP) QueryExecutionFactory.sparqlService(endpoint.getEndpoint(), prepareQuery.toString());
//        ResultSet results = qeHTTP.execSelect();
//
//        // get dataset and dsd concepts
//        List<Cube> cubes = new ArrayList<>();
//        while (results.hasNext()) {
//            QuerySolution r = results.next();
//
//            if (r.get("dsd") != null && r.get("dsd").isResource()) {
//                Cube cube = new Cube();
//                cubes.add(cube);
//                cube.setSparqlEndpoint(endpoint);
//                cube.getStructureDefinition().setConcept(r.getResource("dsd").getURI());
//            }
//        }
//
//        // get dimensions
//        for (Cube cube : cubes) {
//            prepareQuery = new ParameterizedSparqlString(GET_DIMS);
//            prepareQuery.setIri("dsd", cube.getStructureDefinition().getConcept());
//            qeHTTP = (QueryEngineHTTP) QueryExecutionFactory.sparqlService(endpoint.getEndpoint(), prepareQuery.toString());
//            results = qeHTTP.execSelect();
//
//            while (results.hasNext()) {
//                QuerySolution r = results.next();
//                Dimension d = new Dimension();
////                System.out.println(r.get("c"));
//
////                System.out.println(r.get("c").isResource() + "  " + r.get("c").isLiteral());
//                d.setConcept(r.getResource("c").getURI());
//                cube.getStructureDefinition().getDimensions().add(d);
//            }
//        }
//
//        // get measures
//        for (Cube cube : cubes) {
//            prepareQuery = new ParameterizedSparqlString(GET_MEAS);
//            prepareQuery.setIri("dsd", cube.getStructureDefinition().getConcept());
//            qeHTTP = (QueryEngineHTTP) QueryExecutionFactory.sparqlService(endpoint.getEndpoint(), prepareQuery.toString());
//            results = qeHTTP.execSelect();
//
//            while (results.hasNext()) {
//                QuerySolution r = results.next();
//                Measure m = new Measure();
////                System.out.println(r.get("c").isResource() + "  " + r.get("c").isLiteral());
//                m.setConcept(r.getResource("c").getURI());
//                cube.getStructureDefinition().getMeasures().add(m);
//            }
//        }
//
//        return cubes;
//    }
//
//
//
//
////
////    private static SparqlEndpoint getEndpoint(String dataset) {
////        HttpResponse<JsonNode> json = null;
////        System.out.println(DATAHUB_API + GET_DATASET + dataset);
////        try {
////            json = Unirest.get(DATAHUB_API + GET_DATASET + dataset).asJson();
////        } catch (UnirestException e) {
////            e.printStackTrace();
////        }
////
////        // TODO check for json == null
////        //System.out.println(json.getBody());
////
////
////        JSONObject result = json.getBody().getObject().getJSONObject("result");
////        JSONArray resources = result.getJSONArray("resources");
////
////        for (int i = 0; i < resources.length(); i++) {
////            JSONObject resource = resources.getJSONObject(i);
////            if (resource.getString("format").equals("api/sparql")) {
////                SparqlEndpoint sparqlEndpoint = new SparqlEndpoint();
////                sparqlEndpoint.setId(dataset);
////
////                if (!result.isNull("url")) {
////                    sparqlEndpoint.setUrl(result.getString("url"));
////                }
////
////                if (!resource.isNull("name")) {
////                    sparqlEndpoint.setName(resource.getString("name"));
////
////                }
////
////                if (!resource.isNull("url")) {
////                    sparqlEndpoint.setEndpoint(resource.getString("url"));
////                }
////
////                if (!result.isNull("title")) {
////                    sparqlEndpoint.setTitle(result.getString("title"));
////                }
////
////                return sparqlEndpoint;
////            }
////        }
////
////        return null;
////    }
//
//
//
//
//
//    private static String getLabelFromUrl(String concept) {
//
//        // get substring
//        int index = concept.lastIndexOf("#");
//
//        if (index == -1) {
//            index = concept.lastIndexOf("/");
//        }
//
//        String label = concept.substring(index + 1);
//
//        // remove unwanted characters
////        label = label.replaceAll("-", " ");
////        label = label.replaceAll("_", " ");
//
//        try {
//            label = URLDecoder.decode(label, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        label = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, label);
//        label = label.replaceAll("-", " ");
//
//        return label;
//    }
}