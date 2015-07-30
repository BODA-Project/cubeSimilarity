package de.uop.mics.bayerl.cube.similarity.hierarchies.dbpedia;

/**
 * Created by sebastianbayerl on 29/07/15.
 */
public class QueryWrapper {

    private int i;
    private int j;
    private String query;

    public QueryWrapper(int i, int j, String query) {
        this.i = i;
        this.j = j;
        this.query = query;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public int getJ() {
        return j;
    }

    public void setJ(int j) {
        this.j = j;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
