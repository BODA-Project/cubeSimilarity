package de.uop.mics.bayerl.cube.provider.wordsimilarity;

/**
 * Created by sebastianbayerl on 28/10/15.
 */
public class WordSim {

    private String w1;
    private String w2;
    private double sim;

    public WordSim(String w1, String w2, double sim) {
        this.w1 = w1;
        this.w2 = w2;
        this.sim = sim;
    }

    public String getW1() {
        return w1;
    }

    public void setW1(String w1) {
        this.w1 = w1;
    }

    public String getW2() {
        return w2;
    }

    public void setW2(String w2) {
        this.w2 = w2;
    }

    public double getSim() {
        return sim;
    }

    public void setSim(double sim) {
        this.sim = sim;
    }
}
