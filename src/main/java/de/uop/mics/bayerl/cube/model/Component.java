package de.uop.mics.bayerl.cube.model;

import java.io.Serializable;

public class Component implements Serializable {

    private String label;
    private String concept;
    private String subpropertyOf;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public String getSubpropertyOf() {
        return subpropertyOf;
    }

    public void setSubpropertyOf(String subpropertyOf) {
        this.subpropertyOf = subpropertyOf;
    }
}
