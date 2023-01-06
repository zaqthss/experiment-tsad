package cn.edu.bit.cs.anomaly.util;

import cn.edu.bit.cs.anomaly.entity.Range;
import cn.edu.bit.cs.anomaly.entity.TimePointMulDim;
import cn.edu.bit.cs.anomaly.entity.TimeSeriesMulDim;
import cn.edu.bit.cs.anomaly.evaluate.RangeMetric;
import cn.edu.bit.cs.anomaly.evaluate.SingleMetric;
import cn.edu.bit.cs.anomaly.util.Constants.IS_ANOMALY;
import cn.edu.bit.cs.anomaly.util.Constants.POS_BIAS;
import java.util.ArrayList;

import cn.edu.bit.cs.anomaly.entity.TimePoint;
import cn.edu.bit.cs.anomaly.entity.TimeSeries;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class DataHandler {
  public static long INTERVAL = 1;
  /**
   * construct subsequences with seq_begin_position and length
   *
   * @param timeseries, startTsList, length
   * @return subsequences
   */
  public static ArrayList<TimeSeries> build_subsequence(
      TimeSeries timeseries, ArrayList<Integer> startTsList, int length) {
    ArrayList<TimeSeries> subsequences = new ArrayList<TimeSeries>();
    int startTs;
    for (int i = 0; i < startTsList.size(); i++) {
      startTs = startTsList.get(i);
      subsequences.add(timeseries.getSubPoints(startTs, startTs + length - 1));
    }
    return subsequences;
  }
  
  /**
   * calculate average
   *
   * @param t of type TimeSeries
   * @return average
   */
  public static double calcMean(TimeSeries t) {
	  double sum=0;
	  for (int i = 0; i < t.getLength(); i++) {
          sum += t.getTimePoint(i).getObsVal();
      }
	  double mean = sum/t.getLength();
	  return mean;
  }
  
  /**
   * calculate std
   *
   * @param t of type TimeSeries
   * @return std
   */
  public static double calcStd(TimeSeries t) {
	  double total=0;
	  double mean=calcMean(t);
	  for (int i = 0; i<t.getLength(); i++) {
          total +=
              (t.getTimePoint(i).getObsVal()-mean)
                  * (t.getTimePoint(i).getObsVal()-mean);
        }
	  double std = Math.sqrt(total/(t.getLength()));
	  return std;
  }
  
  /**
   * calculate max
   *
   * @param t of type TimeSeries
   * @return max
   */
  public static double calcMax(TimeSeries t) {
	  double max=-999999;
	  for (int i = 0; i<t.getLength(); i++) {
		  if(t.getTimePoint(i).getObsVal()>max) {
			  max=t.getTimePoint(i).getObsVal();
		  }
        }
	  return max;
  }
  
  /**
   * calculate min
   *
   * @param t of type TimeSeries
   * @return min
   */
  public static double calcMin(TimeSeries t) {
	  double min=999999;
	  for (int i = 0; i<t.getLength(); i++) {
		  if(t.getTimePoint(i).getObsVal()<min) {
			  min=t.getTimePoint(i).getObsVal();
		  }
        }
	  return min;
  }
  
  /**
   * normalize timeseries with demean or zscore
   *
   * @param t_list,scale
   * @return
   */
  public static ArrayList<TimeSeries> Scale(ArrayList<TimeSeries> t_list, String scale) {
    ArrayList<TimeSeries> t_list_normalized = new ArrayList<TimeSeries>();
    if (scale.equals("demean")) {
    	TimeSeries e = null;
        for (int t = 0; t < t_list.size(); t++) {
          e = new TimeSeries();
          double mean=calcMean(t_list.get(t));
          for (int i = 0; i < t_list.get(t).getLength(); i++) {
            double temp = t_list.get(t).getTimePoint(i).getObsVal();
            long time = t_list.get(t).getTimePoint(i).getTimestamp();
            TimePoint t1 = new TimePoint(time,temp - mean);
            e.addPoint(t1);
          }
          t_list_normalized.add(e);
        }
        return t_list_normalized;
    } else if (scale.equals("zscore")) {
      TimeSeries e = null;
      for (int t = 0; t < t_list.size(); t++) {
        e = new TimeSeries();
        double mean=calcMean(t_list.get(t));
        double std=calcStd(t_list.get(t));
        for (int i = 0; i < t_list.get(t).getLength(); i++) {
          double temp = t_list.get(t).getTimePoint(i).getObsVal();
          long time = t_list.get(t).getTimePoint(i).getTimestamp();
          TimePoint t1 = new TimePoint(time, (temp - mean) / std);
          e.addPoint(t1);
        }
        t_list_normalized.add(e);
      }
      return t_list_normalized;
    } else {
      return t_list;
    }
  }

  public static double calcEuclideanDistance(TimeSeries t1, TimeSeries t2) {
    double dis = 0;
    assert t1.getLength() == t2.getLength() : "length must be equal";
    int len = t1.getLength();
    for(int i = 0; i < len; i++) {
      double temp = t1.getTimePoint(i).getObsVal() - t2.getTimePoint(i).getObsVal();
      dis += Math.pow(temp, 2);
    }
    return Math.pow(dis, 0.5);
  }

  // rely on labels in the dataset
  public static TreeMap<Long, TimePoint> findAnomalyPoint(TimeSeries timeseries) {
    TreeMap<Long, TimePoint> anomaly = new TreeMap<>();

    for (TimePoint tp : timeseries.getTimeseries()) {
      if (tp.getIs_anomaly() == IS_ANOMALY.TRUE) {
        anomaly.put(tp.getTimestamp(), tp);
      }
    }
    return anomaly;
  }

  public static TreeMap<Long, TimePointMulDim> findAnomalyPoint(TimeSeriesMulDim timeseries) {
    TreeMap<Long, TimePointMulDim> anomaly = new TreeMap<>();

    for (Object object : timeseries.getTimeseries()) {
      TimePointMulDim tp = (TimePointMulDim) object;
      if (tp.getIs_anomaly() == IS_ANOMALY.TRUE) {
        anomaly.put(tp.getTimestamp(), tp);
      }
    }
    return anomaly;
  }

  // time interval equals 1 for all the dataset
  // TODO: will change later to adapt more cases
  public static ArrayList<Range> findAnomalyRange(TimeSeries timeseries) {
    ArrayList<Range> anomaly = new ArrayList<>();
    IS_ANOMALY preAnomaly = IS_ANOMALY.FALSE;
    Range curRange = new Range();
    int size = timeseries.getLength();

    for (int index = 0; index < size; ++index) {
      TimePoint tp = timeseries.getTimePoint(index);
      long ts = tp.getTimestamp();
      IS_ANOMALY curAnomaly = tp.getIs_anomaly();
      if (curAnomaly == IS_ANOMALY.TRUE) {
        if (curRange.size() > 0 && (preAnomaly == IS_ANOMALY.FALSE)) {
          // new range
          anomaly.add(curRange);
          curRange = new Range();
        }
        curRange.add(ts);
      }
      preAnomaly = curAnomaly;
    }
    if (curRange.size() > 0) {
      anomaly.add(curRange);
    }
    return anomaly;
  }

  public static ArrayList<Range> findAnomalyRange(TimeSeriesMulDim timeseries) {
    ArrayList<Range> anomaly = new ArrayList<>();
    IS_ANOMALY preAnomaly = IS_ANOMALY.FALSE;
    Range curRange = new Range();
    int size = timeseries.getLength();

    for (int index = 0; index < size; ++index) {
      TimePointMulDim tp = timeseries.getTimePoint(index);
      long ts = tp.getTimestamp();
      IS_ANOMALY curAnomaly = tp.getIs_anomaly();
      if (curAnomaly == IS_ANOMALY.TRUE) {
        if (curRange.size() > 0 && (preAnomaly == IS_ANOMALY.FALSE)) {
          // new range
          anomaly.add(curRange);
          curRange = new Range();
        }
        curRange.add(ts);
      }
      preAnomaly = curAnomaly;
    }
    if (curRange.size() > 0) {
      anomaly.add(curRange);
    }
    return anomaly;
  }

  public static ArrayList<Range> transPointToRange(TreeSet<Long> pointAnomaly) {
    ArrayList<Range> rangeAnomaly = new ArrayList<>();

    long curTs = -1;
    Range curRange = new Range();
    for (long ts: pointAnomaly) {
      if (curRange.size() > 0 && (ts - curTs != INTERVAL)) {
        // new range
        rangeAnomaly.add(curRange);
        curRange = new Range();
      }
      curRange.add(ts);
      curTs = ts;
    }
    if (curRange.size() > 0) {
      rangeAnomaly.add(curRange);
    }
    return rangeAnomaly;
  }

  // uni point to uni alg
  public static void evaluate(TimeSeries timeseries, TreeMap<Long, TimePoint> realAnomaly,
      double[] metrics) {
    TreeMap<Long, TimePoint> predictAnomaly = DataHandler.findAnomalyPoint(timeseries);
    SingleMetric sm = new SingleMetric();
    sm.computeMetric(realAnomaly, predictAnomaly, timeseries);
    metrics[0] += sm.precision;
    metrics[1] += sm.recall;
    metrics[2] += sm.fmeasure;
  }

  // uni/mul point to mul alg
  public static void evaluate(TimeSeriesMulDim timeseries, TreeMap<Long, TimePointMulDim> realAnomaly,
      double[] metrics) {
    TreeMap<Long, TimePointMulDim> predictAnomaly = DataHandler.findAnomalyPoint(timeseries);
    SingleMetric sm = new SingleMetric();
    sm.computeMetric(realAnomaly, predictAnomaly, timeseries);
    metrics[0] += sm.precision;
    metrics[1] += sm.recall;
    metrics[2] += sm.fmeasure;
  }

  // mul point to uni alg
  public static void evaluate(TimeSeries[] predictTsArray, TreeMap<Long, TimePointMulDim> realAnomaly,
      double[] metrics) {
    TreeSet<Long> realAnomalyTime = new TreeSet<>(realAnomaly.keySet());
    TreeSet<Long> predictAnomalyCombine = new TreeSet<>();

    for (TimeSeries ts: predictTsArray) {
      TreeSet<Long> predictAnomaly = new TreeSet<>(DataHandler.findAnomalyPoint(ts).keySet());
      predictAnomalyCombine.addAll(predictAnomaly);
    }

    Set<Long> timeStamps = new TreeSet<>(predictTsArray[0].getTimeseriesMap().keySet());

    SingleMetric sm = new SingleMetric();
    sm.computeMetric(realAnomalyTime, predictAnomalyCombine, timeStamps);
    metrics[0] += sm.precision;
    metrics[1] += sm.recall;
    metrics[2] += sm.fmeasure;
  }

  // uni range to uni/mul alg, mul range to mul alg
  public static void evaluate(double alpha, POS_BIAS bias, ArrayList<Range> predictAnomaly,
      ArrayList<Range> realAnomaly, double[] metrics) {
    RangeMetric rm = new RangeMetric();
    rm.computeMetric(alpha, bias, realAnomaly, predictAnomaly);
    metrics[0] += rm.precision;
    metrics[1] += rm.recall;
    metrics[2] += rm.fmeasure;
  }

  // mul range to uni alg
  public static void evaluate(double alpha, POS_BIAS bias, TimeSeries[] predictTsArray,
      ArrayList<Range> realAnomaly, double[] metrics) {
    TreeSet<Long> predictAnomalyCombine = new TreeSet<>();
    for (TimeSeries ts: predictTsArray) {
      predictAnomalyCombine.addAll(DataHandler.findAnomalyPoint(ts).keySet());
    }
    // from point to range
    ArrayList<Range> predictAnomalyRangeCombine = transPointToRange(predictAnomalyCombine);

    RangeMetric rm = new RangeMetric();
    rm.computeMetric(alpha, bias, realAnomaly, predictAnomalyRangeCombine);
    metrics[0] += rm.precision;
    metrics[1] += rm.recall;
    metrics[2] += rm.fmeasure;
  }
  
  //mul range to mul alg
  public static void evaluate(double alpha, POS_BIAS bias, TimeSeriesMulDim[] predictTsArray,
	      ArrayList<Range> realAnomaly, double[] metrics) {
	    TreeSet<Long> predictAnomalyCombine = new TreeSet<>();
	    for (TimeSeriesMulDim ts: predictTsArray) {
	      predictAnomalyCombine.addAll(DataHandler.findAnomalyPoint(ts).keySet());
	    }
	    // from point to range
	    ArrayList<Range> predictAnomalyRangeCombine = transPointToRange(predictAnomalyCombine);

	    RangeMetric rm = new RangeMetric();
	    rm.computeMetric(alpha, bias, realAnomaly, predictAnomalyRangeCombine);
	    metrics[0] += rm.precision;
	    metrics[1] += rm.recall;
	    metrics[2] += rm.fmeasure;
  }
  
  public static String[] transToDims(List<Integer> dimList, int maxDim) {
    List<Integer> filterList = new ArrayList<>();
    for (int dim: dimList) {
      if (dim <= maxDim) {
        filterList.add(dim);
      }
    }
    Collections.sort(filterList);
    String[] dims = new String[filterList.size()];
    for (int i = 0; i < filterList.size(); ++i) {
      dims[i] = String.valueOf(filterList.get(i));
    }
    return dims;
  }

  public static void main(String[] args) {
    double[] as = {1, 2, 3, 4, 5};
    double[] bs = Arrays.stream(as).map(i -> i / 3).toArray();
    for (double b : bs) {
      System.out.println(b);
    }
  }
}
