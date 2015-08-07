package de.uop.mics.bayerl.cube.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class StructureDefinition implements Serializable {

    private List<Dimension> dimensions = new LinkedList<>();
    private List<Measure> measures = new LinkedList<>();
    private String concept;


    public List<Dimension> getDimensions() {
        return dimensions;
    }

    public void setDimensions(List<Dimension> dimensions) {
        this.dimensions = dimensions;
    }

    public List<Measure> getMeasures() {
        return measures;
    }

    public void setMeasures(List<Measure> measures) {
        this.measures = measures;
    }

    public List<Component> getComponents() {
        List<Component> components = new ArrayList<>();
        components.addAll(dimensions);
        components.addAll(measures);
        return components;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }
}
