package de.uop.mics.bayerl.cube.similarity.hierarchies.dbpedia.json;

/**
 * Created by sebastianbayerl on 26/10/15.
 */
public class Graph {

    private Node[] nodes;
    private Link[] links;

    public Node[] getNodes() {
        return nodes;
    }

    public void setNodes(Node[] nodes) {
        this.nodes = nodes;
    }

    public Link[] getLinks() {
        return links;
    }

    public void setLinks(Link[] links) {
        this.links = links;
    }
}
