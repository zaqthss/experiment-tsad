package cn.edu.bit.cs.anomaly.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.edu.bit.cs.anomaly.PBAD;
import cn.edu.bit.cs.anomaly.entity.TimeSeries;
import cn.edu.bit.cs.anomaly.entity.TimeSeriesMulDim;
import cn.edu.bit.cs.anomaly.util.FileHandler;

public class PBADTest {
  public static int window_size = 10;
  public static int window_incr = 1;
  public static int sequential_minlength = 2;
  public static int bin_size = 10;

  public static void main(String[] args) throws Exception {
    TimeSeriesMulDim MulT = null;
    FileHandler fh = new FileHandler();
    //MulT = fh.readMulData("test/taxi.csv", 1);
    MulT = fh.readMulData("test/test_for_pbad.csv",1);
    System.out.println("Timeseries length: " + MulT.getLength());
    Map<String, Object> arg_list = new HashMap<String, Object>();
    arg_list.put("window_size",12);
    arg_list.put("window_incr",6);
    arg_list.put("bin_size",1);
    arg_list.put("max_feature",50);
    arg_list.put("threshold", 0.1);
    long startTime =  System.currentTimeMillis();
    PBAD pbad = new PBAD();
    pbad.init(arg_list, MulT);
    pbad.run();
    ArrayList<Double> score=pbad.getScore();
    fh.writeScore(score,String.format(
            "%s/%s_%s_score.csv", "../middle/acc", "PBAD", "uni_subg_sp"));
    long endTime =  System.currentTimeMillis();
    long usedTime = (endTime-startTime)/1000;
    System.out.println(usedTime);
  }
  // python:0.952574311822321
  // java:0.9418766932107381
}
