package cn.edu.bit.cs.anomaly.util.clustering;

/*
 * Programmed by Shephalika Shekhar
 * Class for Kmeans Clustering implemetation
 */

import cn.edu.bit.cs.anomaly.entity.TimePointMulDim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class K_Cluster {

    private int dim;
    private int max_iterations = 500;
    private List<? extends TimePointMulDim> features;

    public static class K_MeansReturn {
        public Map<Integer, Integer> clusters;
        public Map<Integer, double[]> centroids;

        public K_MeansReturn(Map<Integer, Integer> clusters, Map<Integer, double[]> centroids) {
            this.clusters = clusters;
            this.centroids = centroids;
        }
    }

    public K_MeansReturn getKmeans(List<? extends TimePointMulDim> f, int k) {
        this.features = f;
        dim = features.get(0).getDim();
        // Hashmap to store centroids with index
        Map<Integer, double[]> centroids = new HashMap<>();
        // calculating initial centroids
        double[] x1 = new double[dim];
        int r = 0;
        for (int i = 0; i < k; i++) {
            x1 = features.get(r++).getObsVal();
            centroids.put(i, x1);
        }

        // Hashmap for finding cluster indexes
        Map<Integer, Integer> clusters = new HashMap<>();
        clusters = kmeans(centroids, k);

        double db[] = new double[dim];
        // reassigning to new clusters
        for (int i = 0; i < max_iterations; i++) {
            for (int j = 0; j < k; j++) {
                List<Integer> list = new ArrayList<>();
                for (int key : clusters.keySet()) {
                    if (key == j) {
                        list.add(key);
                    }
                }
                db = centroidCalculator(list);
                centroids.put(j, db);
            }
            clusters.clear();
            clusters = kmeans(centroids, k);
        }

        return new K_MeansReturn(clusters, centroids);
    }

    // method to calculate centroids
    private double[] centroidCalculator(List<Integer> a) {

        int count = 0;
        // double x[] = new double[ReadDataset.dim];
        double sum = 0.0;
        double[] centroids = new double[dim];
        for (int i = 0; i < dim; i++) {
            sum = 0.0;
            count = 0;
            for (int x : a) {
                count++;
                sum = sum + features.get(x).getObsVal()[i];
            }
            centroids[i] = sum / count;
        }
        return centroids;
    }

    // method for putting features to clusters and reassignment of clusters.
    private Map<Integer, Integer> kmeans(Map<Integer, double[]> centroids, int k) {
        Map<Integer, Integer> clusters = new HashMap<>();
        int k1 = 0;
        double dist = 0.0;
        for (int i = 0; i < features.size(); i++) {
            double minimum = 999999.0;
            for (int j = 0; j < k; j++) {
                dist = eucledianDistance(centroids.get(j), features.get(i).getObsVal());
                if (dist < minimum) {
                    minimum = dist;
                    k1 = j;
                }
            }
            clusters.put(i, k1);
        }
        return clusters;
    }

    public static double eucledianDistance(double[] point1, double[] point2) {
        double sum = 0.0;
        for (int i = 0; i < point1.length; i++) {
            // System.out.println(point1[i]+" "+point2[i]);
            sum += ((point1[i] - point2[i]) * (point1[i] - point2[i]));
        }
        return Math.sqrt(sum);
    }

}
