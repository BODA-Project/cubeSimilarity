package de.uop.mics.bayerl.cube.model;

import de.uop.mics.bayerl.cube.provider.SparqlEndpoint;

import java.io.Serializable;

public class Cube implements Serializable {

    private String label;
    private String description;
    private String auth;
    private String id;
    private String graph;
    private StructureDefinition structureDefinition = new StructureDefinition();
    private SparqlEndpoint sparqlEndpoint;
    private String concept;

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

    public SparqlEndpoint getSparqlEndpoint() {
        return sparqlEndpoint;
    }

    public void setSparqlEndpoint(SparqlEndpoint sparqlEndpoint) {
        this.sparqlEndpoint = sparqlEndpoint;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }
}
