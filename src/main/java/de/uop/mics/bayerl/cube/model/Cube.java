package de.uop.mics.bayerl.cube.model;

public class Cube {

    private String label;
    private String description;
    private String auth;
    private String id;
    private String graph;
    private StructureDefinition structureDefinition = new StructureDefinition();

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGraph() {
        return graph;
    }

    public void setGraph(String graph) {
        this.graph = graph;
    }

    public StructureDefinition getStructureDefinition() {
        return structureDefinition;
    }

    public void setStructureDefinition(StructureDefinition structureDefinition) {
        this.structureDefinition = structureDefinition;
    }
}
