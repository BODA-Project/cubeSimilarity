package de.uop.mics.bayerl.cube.provider;

import de.uop.mics.bayerl.cube.model.Cube;
import de.uop.mics.bayerl.cube.model.Dimension;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sebastianbayerl on 27/10/15.
 */
public class DBPediaExampleProvider {

    public static final String PREFIX = "http://dbpedia.org/resource/";


    public static List<Cube> generateCubes() {
        List<Cube> cubes = new ArrayList<>();


        // create futurama cube
        Cube c1 = new Cube();
        c1.setId("dbpedia-example-0");
        c1.setLabel("A cube about Futurama");
        c1.setDescription("Interesting stuff about Futurama?");

        Dimension d1 = new Dimension();
        d1.setLabel("Futurama");
        d1.setConcept(PREFIX + "Futurama");

        Dimension d2 = new Dimension();
        d2.setLabel("Matt Groening");
        d2.setConcept(PREFIX + "Matt_Groening");

        c1.getStructureDefinition().getDimensions().add(d1);
        c1.getStructureDefinition().getDimensions().add(d2);


        // create Simpsons cube
        Cube c2 = new Cube();
        c2.setId("dbpedia-example-1");
        c2.setLabel("A cube about The Simpsons");
        c2.setDescription("Interesting stuff about The Simpsons?");

        d1 = new Dimension();
        d1.setLabel("The Simpsons");
        d1.setConcept(PREFIX + "The_Simpsons");

        d2 = new Dimension();
        d2.setLabel("Matt Groening");
        d2.setConcept(PREFIX + "Matt_Groening");

        Dimension d3 = new Dimension();
        d3.setLabel("John Frink");
        d3.setConcept(PREFIX + "John_Frink");

        c2.getStructureDefinition().getDimensions().add(d1);
        c2.getStructureDefinition().getDimensions().add(d2);
        c2.getStructureDefinition().getDimensions().add(d3);

        // create Family Guy cube
        Cube c3 = new Cube();
        c3.setId("dbpedia-example-2");
        c3.setLabel("A cube about The Simpsons");
        c3.setDescription("Interesting stuff about The Simpsons?");

        d1 = new Dimension();
        d1.setLabel("Family_Guy");
        d1.setConcept(PREFIX + "Family_Guy");

        d2 = new Dimension();
        d2.setLabel("Seth MacFarlane");
        d2.setConcept(PREFIX + "Seth_MacFarlane");

        d3 = new Dimension();
        d3.setLabel("720p");
        d3.setConcept(PREFIX + "720p");

        c3.getStructureDefinition().getDimensions().add(d1);
        c3.getStructureDefinition().getDimensions().add(d2);
        c3.getStructureDefinition().getDimensions().add(d3);

        cubes.add(c1);
        cubes.add(c2);
        cubes.add(c3);

        return cubes;
    }


}
