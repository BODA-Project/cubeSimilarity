package de.uop.mics.bayerl.cube.eval.spark;


import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.ml.tuning.CrossValidator;
import org.apache.spark.ml.tuning.CrossValidatorModel;
import org.apache.spark.ml.tuning.ParamGridBuilder;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;

public class SparkML {

    public static void main(String[] args) {
        evaluate();
    }

    private static void evaluate() {

        SparkConf conf = new SparkConf();
        conf.setAppName("Simple Application");
        conf.setMaster("local[*]");
        conf.set("spark.executor.memory", "1g");

        JavaSparkContext jsc = new JavaSparkContext(conf);
        SQLContext jsql = new SQLContext(jsc);
        DataFrame data = jsql.read().format("libsvm").load(SparkPrepare.TARGET);

        DataFrame training = data.sample(false, 0.6, 1l);
        DataFrame test = data.except(training);


        LogisticRegression lr = new LogisticRegression()
                .setMaxIter(10_000);

        ParamMap[] paramGrid = new ParamGridBuilder()
                //.addGrid(lr.regParam(), new double[]{0.1, 0.01})
                .build();

        Pipeline pipeline = new Pipeline()
                .setStages(new PipelineStage[] {lr});

        CrossValidator cv = new CrossValidator()
                .setEstimator(pipeline)
                .setEvaluator(new BinaryClassificationEvaluator())
                .setEstimatorParamMaps(paramGrid)
                .setNumFolds(10); // Use 3+ in practice

        CrossValidatorModel cvModel = cv.fit(training);

        DataFrame predictions = cvModel.transform(test);

        double evaluate = cvModel.getEvaluator().evaluate(predictions);

        System.out.println(cvModel.bestModel().explainParams());
        // TODO get best model and parameter set


        System.out.println("###");
        System.out.println("###");
        System.out.println(evaluate);
        System.out.println("###");
        System.out.println("###");
    }
}
