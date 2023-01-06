package cn.edu.bit.cs.anomaly.entity;

import java.util.*;

@SuppressWarnings("hiding")
public class TimeSeriesMulDim<TPMD extends TimePointMulDim> implements Iterable<TPMD> {
  protected int dim;
  protected ArrayList<TPMD> timeseries;
  protected TreeMap<Long, TPMD> timeseriesMap;

  public TimeSeriesMulDim(ArrayList<TPMD> timeseries) {
    setTimeseries(timeseries);
    if (timeseries.size() > 0) {
      dim = timeseries.get(0).getDim();
    } else dim = 0;
  }

  public boolean isEmpty() {
    return timeseries.isEmpty() && timeseriesMap.isEmpty();
  }

  public TimeSeriesMulDim() {
    setTimeseries(new ArrayList<TPMD>());
    this.dim = 0;
  }

  public int getDim() {
    return dim;
  }

  public void setDim(int dim) {
    this.dim = dim;
  }

  public ArrayList<TPMD> getTimeseries() {
    return timeseries;
  }

  public void setTimeseries(ArrayList<TPMD> timeseries) {
    this.timeseries = timeseries;
    this.timeseriesMap = new TreeMap<>();

    for (TPMD tp : timeseries) {
      timeseriesMap.put(tp.getTimestamp(), tp);
    }
  }

  public TreeMap<Long, TPMD> getTimeseriesMap() {
    return timeseriesMap;
  }

  public void addPoint(TPMD tp) {
    this.timeseries.add(tp);

    this.timeseriesMap.put(tp.getTimestamp(), tp);
  }

  public void addPointToPos(TPMD tp, int index) {
    this.timeseries.add(index, tp);
  }

  /**
   * T[a:b] = t_a,t_a+1,...,t_b
   *
   * @param beginIndex
   * @param endIndex
   * @return
   */
  public TimeSeriesMulDim<TPMD> getSubPoints(int beginIndex, int endIndex) {
    ArrayList<TPMD> window = new ArrayList<TPMD>();
    int len = timeseries.size();

    for (int i = beginIndex; i <= endIndex; ++i) {
      if (i < len) window.add(timeseries.get(i));
      else break;
    }
    return (new TimeSeriesMulDim<TPMD>(window));
  }

  public TimeSeries[] convert() {
    if (timeseries == null) return null;
    TimeSeries[] ts = new TimeSeries[this.dim];
    for (int i = 0; i < this.dim; i++) ts[i] = new TimeSeries();
    for (TimePointMulDim tp : this.timeseries) {
      for (int i = 0; i < this.dim; i++) {
        ts[i].addPoint(new TimePoint(tp.getTimestamp(), tp.getTruth()[i], tp.getObserve()[i]));
      }
    }
    return ts;
  }
  
  public TimeSeriesMulDim[] converttoMul() {
	    if (timeseries == null) return null;
	    TimeSeriesMulDim[] ts = new TimeSeriesMulDim[this.dim];
	    for (int i = 0; i < this.dim; i++) ts[i] = new TimeSeriesMulDim();
	    for (TimePointMulDim tp : this.timeseries) {
	      for (int i = 0; i < this.dim; i++) {
	    	double[] truth=new double[1];
	    	double[] observe=new double[1];
	    	truth[0]=tp.getTruth()[i];
	    	observe[0]=tp.getObserve()[i];
	        ts[i].addPoint(new TimePointMulDim(tp.getTimestamp(), truth, observe));
	        ts[i].setDim(1);
	      }
	    }
	    return ts;
	  }

  /**
   *
   * @param firstDims
   * @return
   */
  public TimeSeriesMulDim getSubDims(int firstDims) {
    ArrayList<TimePointMulDim> timeseries = new ArrayList<>();

    for (TimePointMulDim tp : this.timeseries) {
      double[] truth = new double[firstDims];
      double[] observe = new double[firstDims];
      for (int i = 0; i < firstDims; i++) {
        truth[i] = tp.getTruth()[i];
        observe[i] = tp.getObserve()[i];
      }
      timeseries.add(new TimePointMulDim(tp.getTimestamp(), truth, observe));
    }

    return (new TimeSeriesMulDim(timeseries));
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
    double[] truth, observe;
    System.out.println("Data Cleared!");
    for (TPMD tp : timeseries) {
      tp.clear();
    }

    Collections.sort(timeseries, Comparator.comparing(TPMD::getTimestamp));
  }

  /** T[index] */
  public TPMD getTimePoint(int index) {
    return timeseries.get(index);
  }

  public TPMD getTimePoint(long timestamp) {
    return timeseriesMap.get(timestamp);
  }

  @Override
  public Iterator<TPMD> iterator() {
    return this.timeseries.iterator();
  }
}
