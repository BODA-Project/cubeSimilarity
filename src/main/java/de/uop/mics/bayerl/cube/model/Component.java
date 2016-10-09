package de.uop.mics.bayerl.cube.model;

import com.google.common.base.CaseFormat;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class Component implements Serializable {

    private String label;
    private String concept;
    private String subpropertyOf;

    public String getLabel() {
        if (label == null) {
            return getLabelFromUrl();
        }
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

    private String getLabelFromUrl() {

        // get substring
        int index = concept.lastIndexOf("#");

        if (index == -1) {
            index = concept.lastIndexOf("/");
        }

        String label = concept.substring(index + 1);

        // remove unwanted characters
//        label = label.replaceAll("-", " ");
//        label = label.replaceAll("_", " ");

        try {
            label = URLDecoder.decode(label, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        label = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, label);
        label = label.replaceAll("-", " ");

        return label;
    }
}
