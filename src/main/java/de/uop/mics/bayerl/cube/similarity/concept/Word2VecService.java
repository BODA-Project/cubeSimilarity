package de.uop.mics.bayerl.cube.similarity.concept;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * Created by sebastianbayerl on 16/09/15.
 */
public class Word2VecService {

    private final static String ENDPOINT = "http://theseus.dimis.fim.uni-passau.de:80/Word2VecRest/w2vsim";
    private final static String BODY_1 = "{\"data\":[\"";
    private final static String BODY_2 = "\"]}";

    private final static String DBPEDIA_PREFIX = "http://dbpedia.org/resource/";

    public double getSimilarity(String s1, String s2, boolean isConcept) {

        if (isConcept) {
            if (!s1.startsWith(DBPEDIA_PREFIX)) {
                return 0;
            }

            if (!s2.startsWith(DBPEDIA_PREFIX)) {
                return 0;
            }

            s1 = s1.replace(DBPEDIA_PREFIX, "");
            s2 = s2.replace(DBPEDIA_PREFIX, "");
        }

        HttpResponse<JsonNode> result = null;

        try {
            result = Unirest.post(ENDPOINT).body(createQuery(s1, s2)).asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        double similarity = result.getBody().getObject().getJSONArray("data").getJSONObject(0).getDouble("sim");

        return similarity;
    }

    private String createQuery(String c1, String c2) {
        // TODO get entity name from concepts
        StringBuilder sb = new StringBuilder();
        sb.append(BODY_1);
        sb.append(c1);
        sb.append("|");
        sb.append(c2);
        sb.append(BODY_2);

        return sb.toString();
    }

    public static void main(String[] args) {
        Word2VecService service = new Word2VecService();

        System.out.println(service.getSimilarity("http://dbpedia.org/resource/Alan_Turing", "http://dbpedia.org/resource/Computer_science", true));
    }
}
