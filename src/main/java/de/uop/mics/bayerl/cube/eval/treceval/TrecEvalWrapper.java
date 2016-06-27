package de.uop.mics.bayerl.cube.eval.treceval;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by sebastianbayerl on 02/11/15.
 */
public class TrecEvalWrapper {


    public static void main(String[] args) {

        String trecEval = "./trec-eval/trec_eval.9.0/trec_eval";
        String measures = " -m P.1,5,10,15 -m map -m recip_rank ";
        String groundTruth = " qrel.cube ";
        String folder = "eval";

        try {
            Files.list(Paths.get(folder)).forEach(f -> {
                System.out.print(f.getFileName().toString().replace("_output.txt", "").replaceAll("_", "-").replace("---", " & "));
                String run = trecEval + measures + groundTruth + f;

                Process tr = null;
                try {
                    tr = Runtime.getRuntime().exec(run);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                BufferedReader rd = new BufferedReader(new InputStreamReader(tr.getInputStream()));
                BufferedReader error = new BufferedReader(new InputStreamReader(tr.getErrorStream()));

                rd.lines().forEach(s -> {
                    String[] splits = s.split("all");
                    System.out.print(" & " + splits[1].trim()); // splits[0].trim() +
                });

                System.out.print(" \\\\");
                System.out.println();

                error.lines().forEach(s -> {
                    System.out.println(s);
                });
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}