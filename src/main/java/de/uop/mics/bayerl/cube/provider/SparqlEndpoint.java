package de.uop.mics.bayerl.cube.provider;

import java.io.Serializable;

/**
 * Created by sebastianbayerl on 05/08/15.
 */
public class SparqlEndpoint implements Serializable {

    private String id;
    private String name;
    private String title;
    private String endpoint;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
