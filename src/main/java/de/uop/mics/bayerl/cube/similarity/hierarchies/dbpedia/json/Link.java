package de.uop.mics.bayerl.cube.similarity.hierarchies.dbpedia.json;

/**
 * Created by sebastianbayerl on 26/10/15.
 */
public class Link {

    private int source;
    private int target;
    private int value;

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
