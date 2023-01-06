package cn.edu.bit.cs.anomaly.util;

public class SubseqScore implements Comparable<SubseqScore> {
    int index;
    double score;

    public SubseqScore(int index, double score) {
        super();
        this.index = index;
        this.score = score;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public int compareTo(SubseqScore o) {
        if (this.score > o.score) {
            return 1;
        } else if (this.score < o.score) {
            return -1;
        } else {
            return 0;
        }
    }

}