package de.uop.mics.bayerl.cube.eval.spark;


import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.tree.RandomForest;
import org.apache.spark.mllib.tree.model.RandomForestModel;
import org.apache.spark.mllib.util.MLUtils;

import java.util.HashMap;
import java.util.Map;

public class SparkEval {


    public static void main(String[] args) {

        evaluate();

    }



    private static void evaluate() {



        SparkConf sparkConf = new SparkConf().setAppName("JavaDecisionTreeClassificationExample");
        sparkConf.setMaster("local[*]");
        JavaSparkContext jsc = new JavaSparkContext(sparkConf);
        JavaRDD<LabeledPoint> data = MLUtils.loadLibSVMFile(jsc.sc(), SparkPrepare.TARGET, true).toJavaRDD();

//        // Split initial RDD into two... [60% training data, 40% testing data].
//        JavaRDD<LabeledPoint> training = data.sample(false, 0.6, 11L);
//        training.cache();
//        JavaRDD<LabeledPoint> test = data.subtract(training);
//
//        // Run training algorithm to build the model.
//        int numIterations = 100;



        JavaRDD<LabeledPoint>[] splits = data.randomSplit(new double[]{0.7, 0.3});
        JavaRDD<LabeledPoint> trainingData = splits[0];
        JavaRDD<LabeledPoint> testData = splits[1];

        // Set parameters.
        //  Empty categoricalFeaturesInfo indicates all features are continuous.
        Integer numClasses = 6;
        Map<Integer, Integer> categoricalFeaturesInfo = new HashMap<>();
        String impurity = "gini";
        Integer maxDepth = 5;
        Integer maxBins = 32;

//        // Train a DecisionTree model for classification.
//        final DecisionTreeModel model = DecisionTree.trainClassifier(trainingData, numClasses,
//                categoricalFeaturesInfo, impurity, maxDepth, maxBins);

        Integer numTrees = 3; // Use more in practice.
        String featureSubsetStrategy = "auto"; // Let the algorithm choose.
        Integer seed = 12345;
        final RandomForestModel model2 = RandomForest.trainClassifier(trainingData, numClasses,
                categoricalFeaturesInfo, numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins,
                seed);


        System.out.println("###");
        System.out.println(data.toDebugString());
        System.out.println("###");
        System.out.println(model2.toDebugString());
        System.out.println("###");

    }





}
