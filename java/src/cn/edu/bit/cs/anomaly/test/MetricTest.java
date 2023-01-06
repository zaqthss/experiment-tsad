package cn.edu.bit.cs.anomaly.test;

import cn.edu.bit.cs.anomaly.entity.Range;
import cn.edu.bit.cs.anomaly.entity.TimePoint;
import cn.edu.bit.cs.anomaly.entity.TimeSeries;
import cn.edu.bit.cs.anomaly.evaluate.RangeMetric;
import cn.edu.bit.cs.anomaly.evaluate.SingleMetric;
import cn.edu.bit.cs.anomaly.util.Constants;
import cn.edu.bit.cs.anomaly.util.Constants.POS_BIAS;
import cn.edu.bit.cs.anomaly.util.DataHandler;
import cn.edu.bit.cs.anomaly.util.FileHandler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Test metrics
 *
 * @author Aoqian
 */
public class MetricTest {
    private long INTERVAL = 1;

    public static void main(String[] args) {
        System.out.println("Metric Test");
        MetricTest mt = new MetricTest();
        FileHandler fh = new FileHandler();
        DataHandler dh = new DataHandler();

        System.out.println("Single test");
        String singleFile = "example/single.csv";
        // real anomaly 3, 5, 10, 14, 18
        TimeSeries seriesSingle = fh.readDataTruthObs(singleFile);
        // predict anomaly 3, 5, 10, 15, 20
        long[] predicts = {3, 5, 10, 8, 15, 20};
        TreeSet<Long> predictSingle = new TreeSet<>();
        for (long ts : predicts) {
            seriesSingle.getTimeseriesMap().get(ts).setPredictVal(0);
            predictSingle.add(ts);
        }
        ArrayList<Range> realSingleList = mt.findRealAnomaly(seriesSingle);
        // divide into a single TreeSet
        TreeSet<Long> realSingle = new TreeSet<>();
        for (Range range : realSingleList) {
            realSingle.addAll(range);
        }
        SingleMetric sm = new SingleMetric();
        sm.computeMetric(realSingle, predictSingle, seriesSingle.getTimeseriesMap().keySet());
        System.out.println("precision " + sm.precision);
        System.out.println("recall " + sm.recall);

        System.out.println("Range test");
        String rangeFile = "example/range.csv";
        // 6-10, 16-20, 26-30, 36-40, 46-50
        TimeSeries seriesRange = fh.readDataTruthObs(rangeFile);
        // predict anomaly: 9-13, 16-21, 28-38, 45-47, 49-50
        ArrayList<Range> realRange = mt.findRealAnomaly(seriesRange);
        ArrayList<Range> predictRange = mt.getPredictAnomalyFromFile("example/predict_range.txt");
        RangeMetric rm = new RangeMetric();
        double alpha = 0;
        POS_BIAS bias = POS_BIAS.FLAT;
        // POS_BIAS bias = POS_BIAS.FRONT_END;
        rm.computeMetric(alpha, bias, realRange, predictRange);
        System.out.println("precision " + rm.precision);
        System.out.println("recall " + rm.recall);

        System.out.println("Subsume test, should be the same as Single test result");
        alpha = 0;
        bias = POS_BIAS.FLAT;
        rm = new RangeMetric();
        ArrayList<Range> predictSingleList = new ArrayList<>();
        for (long ts : predicts) {
            Range range = new Range();
            range.add(ts);
            predictSingleList.add(range);
        }
        rm.computeMetric(alpha, bias, realSingleList, predictSingleList);
        System.out.println("precision " + rm.precision);
        System.out.println("recall " + rm.recall);

        //        System.out.println("Equal test, should be the same with RangeMetricQuick");
        //        SeriesResult<TimePoint> R = mt.transToSeriesResult(realRange, seriesRange);
        //        SeriesResult<TimePoint> P = mt.transToSeriesResult(predictRange, seriesRange);
        //        RangeMetricQuick rmq = new RangeMetricQuick();
        //        rmq.Calculate(R, P, null);
        //        System.out.println("precision " + rmq.Precision);
        //        System.out.println("recall " + rmq.Recall);

        // Range label test
        System.out.println("Range label test");
        String rangeLabelFile = "example/range_label.csv";
        // 6-10, 16-20, 26-30, 36-40, 46-50
        TimeSeries seriesLabelRange = fh.readDataWithLabel(rangeLabelFile);
        // predict anomaly: 9-13, 16-21, 28-38, 45-47, 49-50
        ArrayList<Range> realLabelRange = dh.findAnomalyRange(seriesRange);
        ArrayList<Range> predictLabelRange = mt.getPredictAnomalyFromFile("example/predict_range.txt");
        RangeMetric rmLabel = new RangeMetric();
        double alphaLabel = 0;
        POS_BIAS biasLabel = POS_BIAS.FLAT;
        // POS_BIAS bias = POS_BIAS.FRONT_END;
        rmLabel.computeMetric(alphaLabel, biasLabel, realLabelRange, predictLabelRange);
        System.out.println("precision " + rm.precision);
        System.out.println("recall " + rm.recall);
    }

    //    private SeriesResult<TimePoint> transToSeriesResult(ArrayList<Range> rangeList,
    //        TimeSeries timeseries) {
    //        SeriesResult<TimePoint> seriesResult = new SeriesResult<>();
    //        for (Range range: rangeList) {
    //            SinglePointResult<TimePoint> spr = new SinglePointResult<>();
    //            Iterator<Long> it = range.iterator();
    //            while(it.hasNext()){
    //                Long ts = it.next();
    //                spr.put(ts, timeseries.getTimeseriesMap().get(ts));
    //            }
    //            seriesResult.add(spr);
    //        }
    //        return seriesResult;
    //    }

    private ArrayList<Range> findRealAnomaly(TimeSeries timeseries) {
        ArrayList<Range> realAnomaly = new ArrayList<>();
        long curTs = -1;
        Range curRange = new Range();
        for (TimePoint tp : timeseries.getTimeseries()) {
            long ts = tp.getTimestamp();
            double truth = tp.getTruth();
            double observe = tp.getObserve();
            // error
            if (Math.abs(truth - observe) > Constants.EPSILON) {
                if (curRange.size() > 0 && (ts - curTs != INTERVAL)) {
                    // new range
                    realAnomaly.add(curRange);
                    curRange = new Range();
                }
                curRange.add(ts);
                curTs = ts;
            }
        }
        if (curRange.size() > 0) {
            realAnomaly.add(curRange);
        }
        return realAnomaly;
    }

    private ArrayList<Range> getPredictAnomalyFromFile(String filename) {
        BufferedReader br = null;
        ArrayList<Range> predictAnomaly = new ArrayList<>();
        try {
            br = new BufferedReader(new FileReader("data/" + filename));
            String line = null;
            while ((line = br.readLine()) != null) {
                Range range = new Range();
                String[] vals = line.split(",");
                for (String val : vals) {
                    range.add(Long.parseLong(val));
                }
                predictAnomaly.add(range);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return predictAnomaly;
    }
}
