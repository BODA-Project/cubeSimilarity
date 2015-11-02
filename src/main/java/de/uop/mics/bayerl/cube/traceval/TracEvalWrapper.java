package de.uop.mics.bayerl.cube.traceval;

import java.io.File;
import java.io.IOException;

/**
 * Created by sebastianbayerl on 02/11/15.
 */
public class TracEvalWrapper {

    public static void main(String[] args) {
        File outputDir = null;
        final int exitCode;
        try {
            Process process = Runtime.getRuntime().exec("yourprogram", null, outputDir);
            exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException("program didnt exit with 0, but with " + exitCode);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
