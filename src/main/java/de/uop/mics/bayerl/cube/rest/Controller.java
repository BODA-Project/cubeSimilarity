package de.uop.mics.bayerl.cube.rest;


import de.uop.mics.bayerl.cube.model.Cube;
import de.uop.mics.bayerl.cube.rest.repository.CubeRepository;
import de.uop.mics.bayerl.cube.rest.service.SimilarityService;
import de.uop.mics.bayerl.cube.similarity.MatrixAggregation;
import de.uop.mics.bayerl.cube.similarity.Metric;
import de.uop.mics.bayerl.cube.similarity.RankingItem;
import org.apache.jena.ext.com.google.common.base.Stopwatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
public class Controller {

    @Autowired
    private SimilarityService similarityService;

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

    @RequestMapping(value = "/metrics", method = RequestMethod.GET)
    public Map<String, List<String>> getAvailableMeasures() {
        Map<String, List<String>> result = new HashMap<>();

        List<String> componentBased = new ArrayList<>();
        for (Metric metric : Metric.values()) {
            componentBased.add(metric.name());
        }

        result.put("componentBased", componentBased);

        List<String> datasetBased = new ArrayList<>();
        datasetBased.add("Label equality");
        datasetBased.add("Label similarity");
        result.put("datasetBased", datasetBased);

        return result;
    }

    @RequestMapping(value = "/cubes/{id}/compute-ranking", method = RequestMethod.GET)
    public List<RankingItem> getRanking(@PathVariable String id, @RequestParam String matrixAggr, @RequestParam(value="metric")  String metric) {
        Stopwatch sw = Stopwatch.createStarted();
        System.out.println("Start ranking: " + metric + " " + matrixAggr);
        List<RankingItem> rankingItems = similarityService.computeRanking(id, metric, matrixAggr);
        System.out.println("Done ranking: " + sw.elapsed(TimeUnit.SECONDS) + " [s]");

        return rankingItems;
    }

    @RequestMapping(value = "/cubes/{id}/compute-similarity", method = RequestMethod.GET)
    public RankingItem getSimilarity(@PathVariable String id, @RequestParam String secondCube,
                                             @RequestParam String metric,
                                             @RequestParam String matrixAggr,
                                             @RequestParam(required = false) String testset) {
        Stopwatch sw = Stopwatch.createStarted();
        System.out.println("Start similarity: " + metric + " " + matrixAggr);
        RankingItem rankingItem = similarityService.computeSimilarity(id, secondCube, metric, matrixAggr);
        System.out.println("Done similarity: " + sw.elapsed(TimeUnit.SECONDS) + " [s]");
        return rankingItem;
    }

    @RequestMapping(value = "/cubes/{id}", method = RequestMethod.GET)
    public Cube getCube(@PathVariable String id) {
        return cubeRepository.getCube(id);
    }


    @RequestMapping(value = "/matrix-aggregation", method = RequestMethod.GET)
    public List<String> getMatrixAggregations() {
        List<String> result = new ArrayList<>();
        for (MatrixAggregation matrixAggregation : MatrixAggregation.values()) {
            result.add(matrixAggregation.name());
        }

        return result;
    }

}