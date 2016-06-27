package de.uop.mics.bayerl.cube.eval.ml.spark;


import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.classification.LogisticRegressionModel;
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


        LogisticRegression lr = new LogisticRegression();

        ParamMap[] paramGrid = new ParamGridBuilder()
                .addGrid(lr.maxIter(), new int[]{1_000, 5_000, 10_000})
               // .addGrid(lr.regParam(), new double[]{0.0, 0.01, 0.1})
                .addGrid(lr.elasticNetParam(), new double[]{0.0, 0.4, 0.8, 1})
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

        PipelineModel pipelineModel = (PipelineModel) cvModel.bestModel();
        LogisticRegressionModel logisticRegressionModel = (LogisticRegressionModel) pipelineModel.stages()[0];


        System.out.println("###");
        System.out.println(logisticRegressionModel.explainParams());
        System.out.println("###");
        System.out.println(evaluate);
        System.out.println("###");
    }
}
