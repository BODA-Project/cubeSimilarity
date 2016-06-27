

Evaluation German Reich Statistics.

1. All cubes are in reichstatisticsInput (also info and disambiguation)
2. ReichstatisticsGroupedCubes provider generates cubes from this file
3. EvaluationGermanReich generates actual similarity evaluation -> files in eval folder
4. QRelGenerator generates the ground truth for the trec-eval evaluation -> qrel.cube
5. TrecEvalWrapper generates measures based on 3. and 4. -> console print

6. SparkPrepare brings the similarities from 3. into a spark readable format -> spark/input.txt
7. SparkEvalGui -> Train a SVM and show results
8. SparkML -> Logistic Regression with cross-validation

TODO

9. Sofia-ml: prepare training and test set 