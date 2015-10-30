package de.uop.mics.bayerl.cube.provider;

import de.uop.mics.bayerl.cube.model.Cube;
import de.uop.mics.bayerl.cube.model.Dimension;

import java.util.*;

/**
 * Created by sebastianbayerl on 28/10/15.
 */
public class WordSimilarityProvider {

    public static List<Cube> getCubes() {

        List<WordSim> sims = new ArrayList<>();
        sims.add(new WordSim("love", "sex", 6.77));
        sims.add(new WordSim("tiger", "cat", 7.35));
        sims.add(new WordSim("tiger", "tiger", 10.00));
        sims.add(new WordSim("book", "paper", 7.46));
        sims.add(new WordSim("computer", "keyboard", 7.62));
        sims.add(new WordSim("computer", "internet", 7.58));
        sims.add(new WordSim("plane", "car", 5.77));
        sims.add(new WordSim("train", "car", 6.31));
        sims.add(new WordSim("telephone", "communication", 7.50));
        sims.add(new WordSim("television", "radio", 6.77));


        Map<String, String> disambiguation = new HashMap<>();

        disambiguation.put("love", "Love");
        disambiguation.put("tiger", "Tiger");
        disambiguation.put("book", "Book");
        disambiguation.put("computer", "Computer");
        disambiguation.put("plane", "Airplane");
        disambiguation.put("train", "Train");
        disambiguation.put("telephone", "Telephone");
        disambiguation.put("television", "Television");
        disambiguation.put("sex", "Sexual_intercourse");
        disambiguation.put("cat", "Cat");
        disambiguation.put("paper", "Paper");
        disambiguation.put("keyboard", "Computer_keyboard");
        disambiguation.put("internet", "Internet");
        disambiguation.put("car", "Car");
        disambiguation.put("communication", "Communication");
        disambiguation.put("radio", "Radio"); //_(receiver)


        disambiguation.put("jaguar", "Jaguar");
        disambiguation.put("feline", "Felidae");
        disambiguation.put("carnivore", "Carnivore");
        disambiguation.put("mammal", "Mammal");
        disambiguation.put("animal", "Animal");
        disambiguation.put("organism", "Organism");
        disambiguation.put("fauna", "Fauna");


        List<Cube> cubes = new ArrayList<>();

        for (String key : disambiguation.keySet()) {
            Cube c = new Cube();
            cubes.add(c);
            c.setId(key);
            c.setLabel(key);
            c.setDescription(key);

            Dimension d = new Dimension();
            c.getStructureDefinition().getDimensions().add(d);
            d.setLabel(key);
            d.setConcept(DBPediaExampleProvider.PREFIX + disambiguation.get(key));
        }

        return cubes;
    }




}
