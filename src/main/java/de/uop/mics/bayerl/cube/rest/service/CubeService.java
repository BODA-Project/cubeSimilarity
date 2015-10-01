package de.uop.mics.bayerl.cube.rest.service;

import de.uop.mics.bayerl.cube.model.Cube;
import de.uop.mics.bayerl.cube.rest.repository.CubeRepository;
import de.uop.mics.bayerl.cube.similarity.RankingItem;
import de.uop.mics.bayerl.cube.similarity.matrix.EqualConcepts;
import de.uop.mics.bayerl.cube.similarity.matrix.MatrixUtil;
import de.uop.mics.bayerl.cube.similarity.matrix.SimilarityMatrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sebastianbayerl on 03/09/15.
 */
@Service
public class CubeService {

    @Autowired
    private CubeRepository cubeRepository;

    public RankingItem computeSimilarity(String cube1, String cube2, String metric) {
        RankingItem rankingItem = new RankingItem();

        // TODO this is for testing
        rankingItem.setSourceId(cubeRepository.getFirst().getId());
        rankingItem.setTargetId(cubeRepository.getSecond().getId());
        rankingItem.setMetric(metric);

//        rankingItem.setSourceId(cube1);
//        rankingItem.setTargetId(cube2);


        EqualConcepts equalConcepts = new EqualConcepts();
        SimilarityMatrix matrix = equalConcepts.computeMatrix(
                cubeRepository.getCube(rankingItem.getSourceId()),
                cubeRepository.getCube(rankingItem.getTargetId()));
        double similarity = MatrixUtil.getSimilarity(matrix);
        rankingItem.setSimilarity(similarity);

        return rankingItem;
    }

    public List<RankingItem> computeRanking(String sourceId, String metric) {
        List<Cube> cubes = cubeRepository.getCubes();
        List<RankingItem> ranking = new ArrayList<>();

        // TODO for testing
        sourceId = cubeRepository.getFirst().getId();

        for (Cube cube : cubes) {
            ranking.add(computeSimilarity(sourceId, cube.getId(), metric));
        }

        // sort ranking
        ranking.sort((r1, r2) -> Double.compare(r1.getSimilarity(), r2.getSimilarity()));

        return ranking;
    }


}
