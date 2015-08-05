package de.uop.mics.bayerl.cube.similarity.concept;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import de.uop.mics.bayerl.cube.Configuration;
import org.json.JSONArray;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by sebastianbayerl on 29/07/15.
 */
public class SameAsService {

    private static final String DBPEDIA_PREFIX = "http://dbpedia.org/";
    private static SameAsService sameAsService = null;

    private SameAsService() {}

    public static SameAsService getInstance() {
        if (sameAsService == null) {
            sameAsService = new SameAsService();
        }

        return sameAsService;
    }

    private final static String SERVICE = "http://zaire.dimis.fim.uni-passau.de:8080/balloon/sameas";
    private Map<String, Set<String>> cache = new HashMap<>();
    private boolean useCache = Configuration.CACHE_SAME_AS;

    private boolean isSameAs(String c1, String c2) {
        if (c1.equals(c2)) {
            return true;
        }

        return getCluster(c1).contains(c2);
    }

    public double getSimilarity(String c1, String c2) {
        return isSameAs(c1, c2) ? 1 : 0;
    }

    public String getDBPediaSameAs(String c) {
        if (c.startsWith(DBPEDIA_PREFIX)) {
            return c;
        }

        Set<String> cluster = getCluster(c);
        for (String item : cluster) {
            if (item.startsWith(DBPEDIA_PREFIX)) {
                return item;
            }
        }

        return null;
    }

    private Set<String> getCluster(String c) {
        Set<String> cluster;

        if (useCache && cache.containsKey(c)) {
            cluster = cache.get(c);
        } else {
            cluster = requestCluster(c);
            cache.put(c, cluster);
        }

        return cluster;
    }

    private Set<String> requestCluster(String c) {
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
