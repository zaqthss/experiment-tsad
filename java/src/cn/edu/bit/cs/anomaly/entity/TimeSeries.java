package cn.edu.bit.cs.anomaly.entity;

import cn.edu.bit.cs.anomaly.util.ComparatorTimestamp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;

/** @author Aoqian */
public class TimeSeries {
  private ArrayList<TimePoint> timeseries;
  private TreeMap<Long, TimePoint> timeseriesMap;

  public TimeSeries(ArrayList<TimePoint> timeseries) {
    setTimeseries(timeseries);
  }

  public TimeSeries() {
    setTimeseries(new ArrayList<TimePoint>());
  }

  public ArrayList<TimePoint> getTimeseries() {
    return timeseries;
  }

  public void setTimeseries(ArrayList<TimePoint> timeseries) {
    this.timeseries = timeseries;
    this.timeseriesMap = new TreeMap<>();

    for (TimePoint tp : timeseries) {
      timeseriesMap.put(tp.getTimestamp(), tp);
    }
  }

  public TreeMap<Long, TimePoint> getTimeseriesMap() {
    return timeseriesMap;
  }

  public void addPoint(TimePoint tp) {
    this.timeseries.add(tp);

    this.timeseriesMap.put(tp.getTimestamp(), tp);
  }

  public void addPointToPos(TimePoint tp, int index) {
    this.timeseries.add(index, tp);
  }

  /**
   * T[a:b] = t_a,t_a+1,...,t_b
   *
   * @param beginIndex
   * @param endIndex
   * @return
   */
  public TimeSeries getSubPoints(int beginIndex, int endIndex) {
    ArrayList<TimePoint> window = new ArrayList<TimePoint>();
    int len = timeseries.size();

    for (int i = beginIndex; i <= endIndex; ++i) {
      if (i < len) window.add(timeseries.get(i));
      else break;
    }
    return (new TimeSeries(window));
  }

  public int getLength() {
    return timeseries.size();
  }

  /**
   * find the current position of timestamp
   *
   * @param timestamp
   * @return
   */
  public int findRealPos(long timestamp) {
    int targetIndex = -1;

    for (int index = 0; index < timeseries.size(); ++index) {
      if (timeseries.get(index).getTimestamp() == timestamp) return index;
    }

    return targetIndex;
  }

  /** clear all the modifications */
  public void clear() {
    double truth, observe;
    System.out.println("Data Cleared!");
    for (TimePoint tp : timeseries) {
      tp.clear();
    }
    Collections.sort(timeseries, new ComparatorTimestamp());
  }

  /** T[index] */
  public TimePoint getTimePoint(int index) {
    return timeseries.get(index);
  }

  public TimePoint getTimePoint(long timestamp) {
    return timeseriesMap.get(timestamp);
  }

  /** add a timeseries at the end */
  public ArrayList<TimePoint> addSubPointsToend(TimeSeries timeseries_to_add) {
    for (int i = 0; i < timeseries_to_add.getLength(); i++) {
      this.timeseries.add(timeseries_to_add.getTimePoint(i));
    }
    return this.timeseries;
  }
}
