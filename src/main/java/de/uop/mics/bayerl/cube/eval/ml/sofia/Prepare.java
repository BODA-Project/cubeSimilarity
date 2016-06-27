package de.uop.mics.bayerl.cube.eval.ml.sofia;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Prepare {
    
    // TODO split into training and testset


    public static void main(String[] args) {
        prepare();
    }

    
    private static void prepare() {
        
        double trainingRatio = 0.6;
        
        List<String> lines = null;
        try {
            lines = Files.lines(Paths.get("sofia-ml/data/input-sofia-2.txt")).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.shuffle(lines);


        System.out.println((int) (lines.size() * trainingRatio));
        int index = (int) (lines.size() * trainingRatio);
        List<String> trainingSet = lines.subList(0, index);
        List<String> testSet = lines.subList(index, lines.size());

        System.out.println(lines.size());
        System.out.println(trainingSet.size());
        System.out.println(testSet.size());
        System.out.println(trainingSet.size() + testSet.size());
        


        try {
            Files.write(Paths.get("sofia-ml/data/training"), trainingSet, Charset.forName("UTF-8"), StandardOpenOption.CREATE);
            Files.write(Paths.get("sofia-ml/data/test"), testSet, Charset.forName("UTF-8"), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
