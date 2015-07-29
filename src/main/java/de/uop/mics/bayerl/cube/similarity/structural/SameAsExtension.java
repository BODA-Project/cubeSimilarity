package de.uop.mics.bayerl.cube.similarity.structural;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by sebastianbayerl on 29/07/15.
 */
public class SameAsExtension {

    private final static String SERVICE = "http://zaire.dimis.fim.uni-passau.de:8080/balloon/sameas";

    public static boolean isSameAs(String c1, String c2) {
        if (c1.equals(c2)) {
            return true;
        }

        return getCluster(c1).contains(c2);
    }

    public static double getSimilarity(String c1, String c2) {
        return isSameAs(c1, c2) ? 1 : 0;
    }

    private static Set<String> getCluster(String c) {
        HttpResponse<JsonNode> json = null;
        try {
            json = Unirest.post(SERVICE).queryString("url", c).asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        Set<String> sameAsCluster = new HashSet<>();

        if (json.getBody().getObject().getInt("status") == 200) {
            JSONArray array = json.getBody().getObject().getJSONArray("sameAs");

            for (int i = 0; i < array.length(); i++) {
                String same = array.getString(i);
                sameAsCluster.add(same);
            }
        }

        return sameAsCluster;
    }



}
