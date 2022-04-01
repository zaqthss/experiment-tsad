package cn.edu.bit.cs.anomaly.test;

import cn.edu.bit.cs.anomaly.SHESD;
import cn.edu.bit.cs.anomaly.SingleDimAlgorithm;
import cn.edu.bit.cs.anomaly.entity.TimeSeries;
import cn.edu.bit.cs.anomaly.util.FileHandler;
import java.util.HashMap;

public class SHESDTest {
  public static  void main(String [] args ){

    FileHandler.PATH ="D:\\";
    FileHandler fh = new FileHandler();
    TimeSeries ts = fh.readData("YahooA1.csv");

    int seasonality = 316;
    double maxAnoms = 0.49;
    double alpha = 0.05;
    double anomsThreshold = 1.0;

    SingleDimAlgorithm alg = new SHESD();
    HashMap<String,Object> arg = new HashMap<>();
    arg.put("seasonality",seasonality);
    arg.put("maxAnoms",maxAnoms);
    arg.put("alpha",alpha);
    arg.put("anomsThreshold",anomsThreshold);

    alg.init(arg,ts);
    alg.run();
  }
}
