package cn.edu.bit.cs.anomaly.util;

import cn.edu.bit.cs.anomaly.entity.*;
import cn.edu.bit.cs.anomaly.util.Constants.IS_ANOMALY;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Read files
 *
 * @author Aoqian
 */
public class FileHandler {

  public static String PATH = "../data/";

  /**
   * Two basic attributes: timestamp, value
   *
   * @param filename
   * @return
   */
  public TimeSeries readData(String filename) {
    TimeSeries timeSeries = new TimeSeries();
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(PATH + filename));

      String line = null;
      long timestamp;
      double value;
      TimePoint tp = null;

      line = br.readLine(); // header

      while ((line = br.readLine()) != null) {
        String[] vals = line.split(",");
        timestamp = Long.parseLong(vals[0]);
        value = Double.parseDouble(vals[1]);

        tp = new TimePoint(timestamp, value);
        timeSeries.addPoint(tp);
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

    return timeSeries;
  }

  /**
   * Two basic attributes: timestamp, value, label
   *
   * @param filename
   * @return
   */
  public TimeSeries readDataWithLabel(String filename) {
    TimeSeries timeSeries = new TimeSeries();
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(PATH + filename));

      String line = null;
      long timestamp;
      double value;
      int label;
      TimePoint tp = null;

      line = br.readLine(); // header

      while ((line = br.readLine()) != null) {
        String[] vals = line.split(",");
        timestamp = new Double(Double.parseDouble(vals[0])).longValue();
        value = Double.parseDouble(vals[1]);
        label = Math.round(Float.parseFloat(vals[2]));

        tp = new TimePoint(timestamp, value);
        if (label == 1) {
          tp.setIs_anomaly(IS_ANOMALY.TRUE);
        } else {
          tp.setIs_anomaly(IS_ANOMALY.FALSE);
        }
        timeSeries.addPoint(tp);
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

    return timeSeries;
  }

  /**
   * Two basic attributes: timestamp, truth, observe
   *
   * @param filename
   * @return
   */
  public TimeSeries readDataTruthObs(String filename) {
    TimeSeries timeSeries = new TimeSeries();
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(PATH + filename));

      String line = null;
      long timestamp;
      double truth, observe;
      TimePoint tp = null;

      line = br.readLine(); // header

      while ((line = br.readLine()) != null) {
        String[] vals = line.split(",");
        timestamp = Long.parseLong(vals[0]);
        truth = Double.parseDouble(vals[1]);
        observe = Double.parseDouble(vals[2]);

        tp = new TimePoint(timestamp, truth, observe);
        timeSeries.addPoint(tp);
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

    return timeSeries;
  }

  /**
   * basic attributes: timestamp, values
   *
   * @param filename,dim
   * @return
   */
  public TimeSeriesMulDim readMulData(String filename, int dim) {
    TimeSeriesMulDim multimeSeries = null;
    ArrayList<TimePointMulDim> timeseries = new ArrayList<TimePointMulDim>();
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(PATH + filename));

      String line = null;
      long timestamp;
      double[] value = null;
      TimePointMulDim tp = null;

      line = br.readLine(); // header

      while ((line = br.readLine()) != null) {
        String[] vals = line.split(",");
        timestamp = Long.parseLong(vals[0]);
        value = new double[dim];
        for (int i = 0; i < dim; i++) {
          value[i] = Double.parseDouble(vals[i + 1]);
        }

        tp = new TimePointMulDim(timestamp, value, dim);
        timeseries.add(tp);
      }
      multimeSeries = new TimeSeriesMulDim(timeseries);
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

    return multimeSeries;
  }

  public ArrayList<Integer> readLabel(String filename) {
    ArrayList<Integer> label = new ArrayList<Integer>();
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(PATH + filename));

      String line = null;
      long timestamp;
      int value = 0;
      TimePointMulDim tp = null;

      line = br.readLine(); // header

      while ((line = br.readLine()) != null) {
        String[] vals = line.split(",");
        value= Integer.parseInt(vals[vals.length-1]);
        label.add(value);
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

    return label;
  }

  /**
   * basic attributes: timestamp, values if do not specify the dim,
   *
   * @param filename
   * @return
   */
  public TimeSeriesMulDim readMulDataWithLabel(String filename) {
    TimeSeriesMulDim multimeSeries = null;
    ArrayList<TimePointMulDim> timeseries = new ArrayList<>();
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(PATH + filename));
      String line = br.readLine(); // header
      int dim = line.split(",").length - 2;
      multimeSeries = readMulDataWithLabel(filename, dim);
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
    return multimeSeries;
  }

  /**
   * Only read first dim of data, e.g., the dataset contains 20 dims but we only read the first 5
   *
   * @param filename
   * @param dim
   * @return
   */
  public TimeSeriesMulDim readMulDataWithLabel(String filename, int dim) {
    TimeSeriesMulDim multimeSeries = null;
    ArrayList<TimePointMulDim> timeseries = new ArrayList<>();
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(PATH + filename));
      String line = null;
      long timestamp;
      double[] value = null;
      TimePointMulDim tp = null;

      line = br.readLine(); // header
      while ((line = br.readLine()) != null) {
        String[] vals = line.split(",");
        timestamp = new Double(Double.parseDouble(vals[0])).longValue();
        value = new double[dim];
        for (int i = 0; i < dim; i++) {
          value[i] = Double.parseDouble(vals[i + 1]);
        }
        int label =Math.round(Float.parseFloat(vals[vals.length - 1]));

        tp = new TimePointMulDim(timestamp, value, dim);
        if (label == 1) {
          tp.setIs_anomaly(IS_ANOMALY.TRUE);
        } else {
          tp.setIs_anomaly(IS_ANOMALY.FALSE);
        }
        timeseries.add(tp);
      }
      multimeSeries = new TimeSeriesMulDim(timeseries);
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
    return multimeSeries;
  }

  public ArrayList<Range> getPredictAnomalyFromFile(String filename) {
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

  public ArrayList<Range> getPredictAnomalyFromFile(File file) {
    BufferedReader br = null;
    ArrayList<Range> predictAnomaly = new ArrayList<>();
    try {
      br = new BufferedReader(new FileReader(file));
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

  public void writeAnomalyRange(ArrayList<Range> rangeList, String filename) {
    try {
      PrintWriter pw = new PrintWriter(new FileWriter(filename));
      for (Range range : rangeList) {
        List<String> tsList = range.stream().map(Object::toString).collect(Collectors.toList());
        pw.println(String.join(",", tsList));
      }
      pw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void writeScore(ArrayList<Double> score,String filename) {
    try {
      PrintWriter pw = new PrintWriter(new FileWriter(filename));
      for (double s : score) {
        pw.println(s);
      }
      pw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void writeMiddleResult(String path,TimeSeriesMulDim<TimePointMulDim> ts){
    ArrayList<String> result = new ArrayList<>();
    path+=".csv";
    for(TimePointMulDim tp :ts){
      if(tp.getIs_anomaly()==IS_ANOMALY.TRUE){
        result.add(tp.getTimestamp()+"");
      }
    }
    PrintWriter pw = null;
    try {
      Files.deleteIfExists(Paths.get(path));
      pw = new PrintWriter(path);
      for(String s :result){
        pw.println(s);
      }
      pw.flush();
      pw.close();
    } catch (IOException e) {
      e.printStackTrace();
      if(pw!=null)
        pw.close();
    }
  }

  public void writeMiddleResult(String path,TimeSeries ts){
    ArrayList<String> result = new ArrayList<>();
    path+=".csv";
    for(TimePoint tp :ts.getTimeseries()){
      if(tp.getIs_anomaly()==IS_ANOMALY.TRUE){
        result.add(tp.getTimestamp()+"");
      }
    }
    PrintWriter pw = null;
    try {
      Files.deleteIfExists(Paths.get(path));
      pw = new PrintWriter(path);
      for(String s :result){
        pw.println(s);
      }
      pw.flush();
      pw.close();
    } catch (IOException e) {
      e.printStackTrace();
      if(pw!=null)
        pw.close();
    }
  }

  public void writeResults(
      String type,
      String prefix,
      String[] vars,
      String[] algNames,
      String[] metricNames,
      long[][] totaltime,
      double[][][] totalcost,
      int repeatNumber) {
    // generate all .dat files
    String sep = ",";
    try {
      PrintWriter datpw = null;
      String head = type + sep + String.join(sep, algNames);

      // metric
      for (int mi = 0; mi < metricNames.length; ++mi) {
        datpw =
            new PrintWriter(
                new FileWriter(
                    "result/" + type + "/" + prefix + "-" + metricNames[mi] + "-" + type + ".csv"));
        datpw.println(head);
        // each line
        for (int i = 0; i < vars.length; ++i) {
          datpw.print(vars[i] + sep);
          for (int algi = 0; algi < algNames.length - 1; ++algi) {
            datpw.print(totalcost[i][algi][mi] / repeatNumber + sep);
          }
          datpw.println(totalcost[i][algNames.length - 1][mi] / repeatNumber);
        }
        datpw.flush();
        datpw.close();
      }
      if(totaltime!=null){
        // time
        datpw =
                new PrintWriter(
                        new FileWriter("result/" + type + "/" + prefix + "-time-" + type + ".csv"));
        datpw.println(head);
        for (int i = 0; i < vars.length; ++i) {
          datpw.print(vars[i] + sep);
          for (int algi = 0; algi < algNames.length - 1; ++algi) {
            datpw.print((double) (totaltime[i][algi]) / repeatNumber + sep);
          }
          datpw.println((double) (totaltime[i][algNames.length - 1]) / repeatNumber);
        }
        datpw.flush();
        datpw.close();
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void writeThresholdResults(
          String type,
          String prefix,
          ArrayList<Double> vars,
          String[] algNames,
          String[] metricNames,
          double[][][] totalcost,
          int repeatNumber) {
    // generate all .dat files
    String sep = ",";
    try {
      PrintWriter datpw = null;
      String head = type + sep + String.join(sep, metricNames);

      //alg
      for(int i=0;i<algNames.length;i++){
        datpw =
                new PrintWriter(
                        new FileWriter(
                                "result/" + type + "/" + prefix + "-" + algNames[i] + "-" + type + ".csv"));
        datpw.println(head);
        for (int j = 0; j < vars.size(); j++) {
          datpw.print(vars.get(j).toString() + sep);
          for (int mi = 0; mi < metricNames.length - 1; ++mi) {
            datpw.print(totalcost[i][j][mi] / repeatNumber + sep);
          }
          datpw.println(totalcost[i][j][metricNames.length - 1] / repeatNumber);
        }
        datpw.flush();
        datpw.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  public void writeAUCResults(
          String type,
          String prefix,
          String[] algNames,
          String metricNames,
          double[][] totalcost,
          int repeatNumber) {
    // generate all .dat files
    String sep = ",";
    try {
      PrintWriter datpw = null;
      String head = type + sep + String.join(sep, metricNames);
      datpw =
              new PrintWriter(
                      new FileWriter(
                              "result/" + type + "/" + prefix + "-"+ metricNames + ".csv"));
      datpw.println(head);
      for (int i = 0; i < algNames.length; i++) {
        datpw.print(algNames[i] + sep);
          //datpw.print(totalcost[i][0] / repeatNumber + sep);
        datpw.println(totalcost[i][0] / repeatNumber);
      }
      datpw.flush();
      datpw.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  public static void main(String[] args) {
    System.out.println("file handler...");

    FileHandler fh = new FileHandler();

    System.out.println("end");
  }
}
