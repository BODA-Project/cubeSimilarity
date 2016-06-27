package de.uop.mics.bayerl.cube.eval.ml.spark;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.classification.SVMModel;
import org.apache.spark.mllib.classification.SVMWithSGD;
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.util.MLUtils;
import scala.Tuple2;

import java.io.Serializable;
import java.util.List;

public class SparkEvalGui extends Application implements Serializable {

    private int numberOfInputs = 0;
    private int numbeofPositiveInputs = 0;
    private double trainingdata = 0.6;
    private int numIterations =  25_000;

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("CubeMergability Machine Learning");

        String file = SparkPrepare.TARGET;
        BinaryClassificationMetrics metrics = createModel(file);
        printSVMMetricResults(metrics, stage);

    }

    private BinaryClassificationMetrics createModel(String file) {
        SparkConf conf = new SparkConf();
        conf.setAppName("Simple Application");
        conf.setMaster("local[4]");
        conf.set("spark.executor.memory", "1g");

        SparkContext sc = new SparkContext(conf);


        // LibSVMFormat
        // <label> <index1>:<value1> <index2>:<value2>
        // classification , feature vector
        // 0 1:23 2:323 3:343 ...
        JavaRDD<LabeledPoint> data = MLUtils.loadLibSVMFile(sc, file).toJavaRDD();
        List<LabeledPoint> inputdata = data.collect();
        numberOfInputs = inputdata.size();

        for (LabeledPoint labeledPoint : inputdata) {
            if (labeledPoint.label() == 1.0){
                numbeofPositiveInputs++;
            }
        }

        // Split initial RDD into two... [60% training data, 40% testing data].
        JavaRDD<LabeledPoint> training = data.sample(false, trainingdata, 1L);


        // Persist this RDD with the default storage level (`MEMORY_ONLY`).
        training.cache();

        JavaRDD<LabeledPoint> test = data.subtract(training);

        // Train a Support Vector Machine (SVM) using Stochastic Gradient Descent
        final SVMModel model = SVMWithSGD.train(training.rdd(), numIterations);


//        SVMWithSGD svmAlg = new SVMWithSGD();
//        svmAlg.optimizer()
//                .setNumIterations(25_000)
//                .setRegParam(0.1)
//                .setUpdater(new SquaredL2Updater());
//        final SVMModel model = svmAlg.run(training.rdd());

        // Clears the threshold so that predict will output raw prediction scores.
        model.clearThreshold();

        // Compute raw scores on the test set
        JavaRDD<Tuple2<Object, Object>> scoreAndLabels = test.map(
                new Function<LabeledPoint, Tuple2<Object, Object>>() {
                    public Tuple2<Object, Object> call(LabeledPoint p) {
                        Double score = model.predict(p.features());
                        return new Tuple2<Object, Object>(score, p.label());
                    }
                }
        );

//        model.
        
        // Get evaluation metrics.
        BinaryClassificationMetrics metrics = new BinaryClassificationMetrics(JavaRDD.toRDD(scoreAndLabels));


        return metrics;

        // Save and load model
        // model.save(sc, "/Users/schlegel/Desktop/mllib/mymodel");
        // SVMModel sameModel = SVMModel.load(sc, "/Users/schlegel/Desktop/mllib/mymodel");
    }

    public static void main(String[] args) {
        launch(args);
    }


    private void printSVMMetricResults(BinaryClassificationMetrics metrics, Stage stage) {
        /// ------
        // PR/ROC Chart
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("recall / false positive rate");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("precision / true positive rate");

        LineChart<Number,Number> prRocChart = new LineChart<>(xAxis,yAxis);
        prRocChart.setTitle("PR / ROC Curve");

        // pr-curve
        List<Tuple2<Object, Object>> prc = metrics.pr().toJavaRDD().collect();
        XYChart.Series prcSeries = new XYChart.Series();
        prcSeries.setName("Precicion-Recall (PR)");
        for(Tuple2<Object, Object> item : prc) {
            prcSeries.getData().add(new XYChart.Data(item._1(), item._2()));
        }
        prRocChart.getData().add(prcSeries);

        // roc-curve
        List<Tuple2<Object, Object>> roc = metrics.roc().toJavaRDD().collect();
        XYChart.Series rocSeries = new XYChart.Series();
        rocSeries.setName("Receiver Operating Characteristic (ROC)");
        for(Tuple2<Object, Object> item : roc) {
            rocSeries.getData().add(new XYChart.Data(item._1(), item._2()));
        }
        prRocChart.getData().add(rocSeries);

        // -------

        // threshold metrics
        NumberAxis xThresholdAxis = new NumberAxis();
        xThresholdAxis.setLabel("threshold");
        NumberAxis yThresholdAxis = new NumberAxis();
        yThresholdAxis.setLabel("recall/precision/f-measure");
        LineChart<Number,Number> tprChart = new LineChart<>(xThresholdAxis,yThresholdAxis);
        tprChart.setTitle("Threshold Metrics");

        // t-recall
        List<Tuple2<Object, Object>> recall = metrics.recallByThreshold().toJavaRDD().collect();
        XYChart.Series trSeries = new XYChart.Series();
        trSeries.setName("Recall");
        for(Tuple2<Object, Object> item : recall) {
            trSeries.getData().add(new XYChart.Data(item._1(), item._2()));
        }
        tprChart.getData().add(trSeries);

        // t-precision
        List<Tuple2<Object, Object>> precision = metrics.precisionByThreshold().toJavaRDD().collect();
        XYChart.Series tpSeries = new XYChart.Series();
        tpSeries.setName("Precision");
        for(Tuple2<Object, Object> item : precision) {
            tpSeries.getData().add(new XYChart.Data(item._1(), item._2()));
        }
        tprChart.getData().add(tpSeries);

        // t-f1
        List<Tuple2<Object, Object>> f1Score = metrics.fMeasureByThreshold().toJavaRDD().collect();
        XYChart.Series f1Series = new XYChart.Series();
        f1Series.setName("F1 Score");
        for(Tuple2<Object, Object> item : f1Score) {
            f1Series.getData().add(new XYChart.Data(item._1(), item._2()));
        }
        tprChart.getData().add(f1Series);

        // t-f2
        List<Tuple2<Object, Object>> f2Score = metrics.fMeasureByThreshold(2.0).toJavaRDD().collect();
        XYChart.Series f2Series = new XYChart.Series();
        f2Series.setName("F2 Score");
        for(Tuple2<Object, Object> item : f2Score) {
            f2Series.getData().add(new XYChart.Data(item._1(), item._2()));
        }
        tprChart.getData().add(f2Series);


        // Single Metrics
        VBox vBox = new VBox(8);
        vBox.getChildren().add(new Label("Area under PR curve"));
        vBox.getChildren().add(new Label("" + metrics.areaUnderPR()));
        vBox.getChildren().add(new Separator());
        vBox.getChildren().add(new Label("Area under ROC curve"));
        vBox.getChildren().add(new Label("" + metrics.areaUnderROC()));
        vBox.getChildren().add(new Separator());
        vBox.getChildren().add(new Separator());
        vBox.getChildren().add(new Label("Input Data Elements"));
        vBox.getChildren().add(new Label("Pos: " + numbeofPositiveInputs + " Neg: " + (numberOfInputs-numbeofPositiveInputs) + " Sum: " + numberOfInputs));
        vBox.getChildren().add(new Label("Pos: " + (int)(((double)numbeofPositiveInputs/(double)numberOfInputs)*100) + "% Neg: " + (int)(((double)(numberOfInputs-numbeofPositiveInputs)/(double)numberOfInputs)*100) + "%"));
        vBox.getChildren().add(new Separator());
        vBox.getChildren().add(new Label("Training/Test Ratio"));
        vBox.getChildren().add(new Label("" + trainingdata + "/" + (1.0-trainingdata)));

        HBox hbox = new HBox(8); // spacing = 8
        hbox.getChildren().add(tprChart);
        hbox.getChildren().add(prRocChart);
        hbox.getChildren().add(vBox);

        Scene scene  = new Scene(hbox,1200,600);

        stage.setScene(scene);
        stage.show();
    }

}
