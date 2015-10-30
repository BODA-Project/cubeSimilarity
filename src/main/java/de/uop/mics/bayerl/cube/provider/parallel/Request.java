package de.uop.mics.bayerl.cube.provider.parallel;

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
        return null;
    }



}
