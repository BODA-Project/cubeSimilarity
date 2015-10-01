package de.uop.mics.bayerl.cube.rest;


import de.uop.mics.bayerl.cube.model.Cube;
import de.uop.mics.bayerl.cube.rest.repository.CubeRepository;
import de.uop.mics.bayerl.cube.rest.service.CubeService;
import de.uop.mics.bayerl.cube.similarity.RankingItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class Controller {

    @Autowired
    private CubeService cubeService;

    @Autowired
    private CubeRepository cubeRepository;

    @RequestMapping(value = "/cubes", method = RequestMethod.GET)
    public List<Cube> getCubes(@RequestParam(defaultValue = "-1") int limit) {
//        List<Cube> cubes = Datahub.readCubes(false);
//        cubes = cubes.subList(0, 11);

        List<Cube> cubes = cubeRepository.getCubes();

        if (limit > -1 && limit < cubes.size()) {
            cubes = cubes.subList(0, limit);
        }

        return cubes;
    }

//    @RequestMapping(value = "/similarities", method = RequestMethod.GET)
//    public List<String> getSimilarities() {
//
//        List<String> similarities = new ArrayList<>();
//        similarities.add("Label - 1");
//        similarities.add("Label - 2");
//
//        return similarities;
//    }

    @RequestMapping(value = "/metrics", method = RequestMethod.GET)
    public Map<String, List<String>> getAvailableMeasures() {
        Map<String, List<String>> result = new HashMap<>();

        List<String> componentBased = new ArrayList<>();
        componentBased.add("Label equality");
        componentBased.add("Label similarity");
        componentBased.add("Concept equality");
        componentBased.add("Concept equality - sameAs extended");
        componentBased.add("Concept similarity - DBPedia Hierarchie BFS");
        result.put("componentBased", componentBased);

        List<String> datasetBased = new ArrayList<>();
        datasetBased.add("Label equality");
        datasetBased.add("Label similarity");
        result.put("datasetBased", datasetBased);

        return result;
    }

    @RequestMapping(value = "/cubes/{id}/compute-ranking", method = RequestMethod.GET)
    public List<RankingItem> getRanking(@PathVariable String id, @RequestParam(value="metric")  String metric) {
        return cubeService.computeRanking(id, metric);
    }

    @RequestMapping(value = "/cubes/{id}/compute-similarity", method = RequestMethod.GET)
    public RankingItem getComputedSimilarity(@PathVariable String id, @RequestParam String secondCube,
                                             @RequestParam String metric, @RequestParam(required = false) String testset) {
        return cubeService.computeSimilarity(id, secondCube, metric);
    }

    @RequestMapping(value = "/cubes/{id}", method = RequestMethod.GET)
    public Cube getComputedSimilarity(@PathVariable String id) {
        return cubeRepository.getCube(id);
    }

}