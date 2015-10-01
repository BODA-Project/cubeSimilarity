package de.uop.mics.bayerl.cube.provider.parallel;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import de.uop.mics.bayerl.cube.provider.SparqlEndpoint;

import java.util.concurrent.Callable;

/**
 * Created by sebastianbayerl on 02/09/15.
 */
public class Request implements Callable<SparqlEndpoint> {

    private static final String DATAHUB_API = "http://datahub.io/api/3/action/";
    private static final String GET_DATASET = "package_show?id=";

    private final String dataset;

    public Request(String dataset) {
        this.dataset = dataset;
    }

    @Override
    public SparqlEndpoint call() throws Exception {
        return getEndpoint();
    }


    public SparqlEndpoint getEndpoint() {
        HttpResponse<String> json = null;
        System.out.println(DATAHUB_API + GET_DATASET + dataset);
        try {
            json = Unirest.get(DATAHUB_API + GET_DATASET + dataset).asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        System.out.println(json.getBody());

//
//        JSONObject result = json.getBody().getJSONObject("result");
//        JSONArray resources = result.getJSONArray("resources");

//        for (int i = 0; i < resources.length(); i++) {
//            JSONObject resource = resources.getJSONObject(i);
//            if (resource.getString("format").equals("api/sparql")) {
//                SparqlEndpoint sparqlEndpoint = new SparqlEndpoint();
//                sparqlEndpoint.setId(dataset);
//
//                if (!result.isNull("url")) {
//                    sparqlEndpoint.setUrl(result.getString("url"));
//                }
//
//                if (!resource.isNull("name")) {
//                    sparqlEndpoint.setName(resource.getString("name"));
//
//                }
//
//                if (!resource.isNull("url")) {
//                    sparqlEndpoint.setEndpoint(resource.getString("url"));
//                }
//
//                if (!result.isNull("title")) {
//                    sparqlEndpoint.setTitle(result.getString("title"));
//                }
//
//                return sparqlEndpoint;
//            }
//        }

        return null;
    }
}
