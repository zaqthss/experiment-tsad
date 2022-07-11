package cn.edu.bit.cs.anomaly.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cn.edu.bit.cs.anomaly.SAND;
import cn.edu.bit.cs.anomaly.entity.TimePoint;
import cn.edu.bit.cs.anomaly.entity.TimeSeries;
import cn.edu.bit.cs.anomaly.util.FileHandler;

public class SANDTest {
  public static int k = 6;
  public static int init_length = 2000;
  public static int batch_size = 2000;
  public static int subsequence_length = 120;
  public static int pattern_length = 30;

  public static void main(String[] args) {
    TimeSeries T = null;
    //FileHandler fh = new FileHandler();
    T = readData("sub/taxi.csv");
    System.out.println("Timeseries length: " + T.getLength());
    // SAND sand=new SAND(T,init_length,batch_size,pattern_length);
    Map<String, Object> arg_list = new HashMap<String, Object>();
    arg_list.put("k", 6);
    arg_list.put("batch_size", 5000);
    arg_list.put("pattern_length", 150);
    arg_list.put("top_k", 9);
    long startTime =  System.currentTimeMillis();
    SAND sand = new SAND();
    sand.init(arg_list, T);
    sand.run();
    long endTime =  System.currentTimeMillis();
    long usedTime = (endTime-startTime)/1000;
    System.out.println(usedTime);
  }
  public static String PATH = "data/";
  /**
   * Two basic attributes: timestamp, value
   *
   * @param filename
   * @return
   */
  public static TimeSeries readData(String filename) {
    TimeSeries timeSeries = new TimeSeries();
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(PATH + filename));

      String line = null;
      long timestamp;
      double value;
      TimePoint tp = null;

      line = br.readLine(); // header

      int i = 0;
      while ((line = br.readLine()) != null) {
        i++;
        String[] vals = line.split(",");
        // timestamp = Long.parseLong(vals[0]);
        value = Double.parseDouble(vals[1]);

        tp = new TimePoint(0, value);
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
}
