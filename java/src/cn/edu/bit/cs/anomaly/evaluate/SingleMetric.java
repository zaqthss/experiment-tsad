package cn.edu.bit.cs.anomaly.evaluate;

import cn.edu.bit.cs.anomaly.entity.TimePoint;
import cn.edu.bit.cs.anomaly.entity.TimePointMulDim;
import cn.edu.bit.cs.anomaly.entity.TimeSeries;
import cn.edu.bit.cs.anomaly.entity.TimeSeriesMulDim;

import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * The classic metric for binary classification https://en.wikipedia.org/wiki/Precision_and_recall
 *
 * @author Antinomies
 * @author Aoqian
 */
public class SingleMetric {
    public double precision;
    public double recall;
    public double accuracy;
    public double errorRate;
    public double sensitive;
    public double specificity;
    public double fmeasure;
    public double fpr;
    public double fnr;

    public double[] computeMetric(
            TreeMap<Long, TimePoint> realAnomaly,
            TreeMap<Long, TimePoint> predictAnomaly,
            TimeSeries timeseries) {
        TreeSet<Long> realSet = new TreeSet<>(realAnomaly.keySet());
        TreeSet<Long> predictSet = new TreeSet<>(predictAnomaly.keySet());
        Set<Long> timeStamps = timeseries.getTimeseriesMap().keySet();
        return computeMetric(realSet, predictSet, timeStamps);
    }

    public double[] computeMetric(
            TreeMap<Long, TimePointMulDim> realAnomaly,
            TreeMap<Long, TimePointMulDim> predictAnomaly,
            TimeSeriesMulDim timeseries) {
        TreeSet<Long> realSet = new TreeSet<>(realAnomaly.keySet());
        TreeSet<Long> predictSet = new TreeSet<>(predictAnomaly.keySet());
        Set<Long> timeStamps = timeseries.getTimeseriesMap().keySet();
        return computeMetric(realSet, predictSet, timeStamps);
    }

    public double[] computeMetric(
            TreeSet<Long> realAnomaly, TreeSet<Long> predictAnomaly, Set<Long> timeStamps) {
        int TP = 0, TN = 0, FP = 0, FN = 0;

        for (long ts : timeStamps) {
            if (realAnomaly.contains(ts) && predictAnomaly.contains(ts)) TP++;
            else if (realAnomaly.contains(ts) && !predictAnomaly.contains(ts)) FN++;
            else if (!realAnomaly.contains(ts) && predictAnomaly.contains(ts)) FP++;
            else {
                TN++;
            }
        }

        accuracy = 1.0 * (TP + TN) / (TP + TN + FP + FN);
        sensitive = 1.0 * TP / (TP + FP);
        specificity = 1.0 * TN / (TN + FN);
        precision = 1.0 * TP / (TP + FP);
        recall = 1.0 * TP / (TP + FN);
        errorRate = 1.0 * (FP + FN) / (TP + TN + FP + FN);
        fmeasure = 2 * precision * recall / (precision + recall);
        fpr=1.0 * FP/(FP+TN);
        fnr=1.0 * FN/(FN+TP);

        double[] metrics = new double[9];
        metrics[0] = precision;
        metrics[1] = recall;
        metrics[2] = fmeasure;
        metrics[3] = accuracy;
        metrics[4] = errorRate;
        metrics[5] = sensitive;
        metrics[6] = specificity;
        metrics[7] =fpr;
        metrics[8]=fnr;

        return metrics;
    }
}
