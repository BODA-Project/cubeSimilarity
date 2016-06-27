package de.uop.mics.bayerl.cube.eval;

import de.uop.mics.bayerl.cube.model.Component;
import de.uop.mics.bayerl.cube.model.Cube;
import de.uop.mics.bayerl.cube.provider.ReichstatisticsGroupedCubes;

import java.util.*;

/**
 * Created by sebastianbayerl on 05/04/16.
 */
public class GermanReichOverview {


    public static void main(String[] args) {
//        getOverview();
        //componentOverview();
        componentDetails();
    }

    private static void getOverview() {
        List<Cube> cubes = ReichstatisticsGroupedCubes.loadCubes();
        
        
        Map<String, List<Integer>> components = new HashMap<>();


        for (Cube cube : cubes) {
            String id = cube.getId().split("-")[0];
            
            if (!components.containsKey(id)) {
                components.put(id, new ArrayList<>());
            }
            components.get(id).add(cube.getStructureDefinition().getDimensions().size());
        }

        for (String key : components.keySet()) {
            
            double avg = 0;
            for (Integer integer : components.get(key)) {
                avg += integer;
            }
            avg /= components.get(key).size();

            System.out.println(key + "    " + components.get(key).size() + "     "  + avg);
        }
    }
    
    
    private static void componentOverview() {
        List<Cube> cubes = ReichstatisticsGroupedCubes.loadCubes();
        Map<String, Integer> compStats = new HashMap<>();

        for (Cube cube : cubes) {

            for (Component component : cube.getStructureDefinition().getDimensions()) {
                String concept = component.getConcept().replace("http://dbpedia.org/resource/", "");
                
                
                if (!compStats.containsKey(concept)) {
                    compStats.put(concept, 0);
                }
                
                compStats.put(concept, compStats.get(concept) + 1);
            }
        }


        for (String key : compStats.keySet()) {
            if (compStats.get(key) > 15) {
                int p = (int) (((double) compStats.get(key) / 110d) * 1000);
                double percent = p / 1000d ; 
                
                System.out.println(key + " & " + compStats.get(key) + " & " + percent + " \\\\");
            }
            
        }
    }
    
    
    private static void componentDetails() {
        List<Cube> cubes = ReichstatisticsGroupedCubes.loadCubes();
        Map<String, Integer> compStats = new HashMap<>();
        Map<String, Set<String>> compGroups = new HashMap<>();

        for (Cube cube : cubes) {
            String groupId = cube.getId().split("-")[0];
            for (Component component : cube.getStructureDefinition().getDimensions()) {
                String concept = component.getConcept().replace("http://dbpedia.org/resource/", "");
                if (!compStats.containsKey(concept)) {
                    compStats.put(concept, 0);
                }

                if (!compGroups.containsKey(concept)) {
                    compGroups.put(concept, new HashSet<>());
                }
                
                
                compStats.put(concept, compStats.get(concept) + 1);
                compGroups.get(concept).add(groupId);
            }
        }

//        for (String key : compStats.keySet()) {
//            System.out.println(key + "   " + compStats.get(key));
//        }
//
//        System.out.println("######");
//
//        for (String key : compGroups.keySet()) {
//            System.out.println(key + "   " + compGroups.get(key).size());
//        }
        
//        compStats.keySet().stream().sorted((a, b) -> compStats.get(b).compareTo(compStats.get(a))).forEach(s -> System.out.println(s + " \t  " + compStats.get(s)));
        compGroups.keySet().stream().sorted((a, b) -> Integer.compare(compGroups.get(b).size(), compGroups.get(a).size())).forEach(s -> System.out.println(s + " \t " + compGroups.get(s).size()));
        
    }
}
