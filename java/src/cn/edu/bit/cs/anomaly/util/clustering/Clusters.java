package cn.edu.bit.cs.anomaly.util.clustering;

import java.util.ArrayList;

public class Clusters {
    private ArrayList<Integer> idx;
    ArrayList<ArrayList<Double>> centroids;

    public Clusters(ArrayList<Integer> idx, ArrayList<ArrayList<Double>> centroids) {
        super();
        this.setIdx(idx);
        this.centroids = centroids;
    }

    public Clusters() {

    }

    public void Print() {
        for (int i = 0; i < centroids.size(); i++) {
            System.out.println(centroids.get(i));
        }
        System.out.println(getIdx());
    }

    public ArrayList<Integer> getIdx() {
        return idx;
    }

    public void setIdx(ArrayList<Integer> idx) {
        this.idx = idx;
    }

    public ArrayList<ArrayList<Double>> getCentroids() {
        return centroids;
    }

    public void setCentroids(ArrayList<ArrayList<Double>> centroids) {
        this.centroids = centroids;
    }

}
