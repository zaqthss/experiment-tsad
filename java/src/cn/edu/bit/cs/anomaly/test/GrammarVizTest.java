package cn.edu.bit.cs.anomaly.test;

import cn.edu.bit.cs.anomaly.GrammarViz;
import cn.edu.bit.cs.anomaly.SHESD;
import cn.edu.bit.cs.anomaly.UniDimAlgorithm;
import cn.edu.bit.cs.anomaly.entity.TimeSeries;
import cn.edu.bit.cs.anomaly.util.FileHandler;
import java.util.HashMap;

public class GrammarVizTest {

  public static void main(String[] args) {

    //FileHandler.PATH ="D:\\";
    FileHandler fh = new FileHandler();
    TimeSeries ts = fh.readData("cui/uni/stock_pointg_10000_0.1_1.csv");

    int SAX_WINDOW_SIZE = 170;
    int SAX_PAA_SIZE = 4;
    int SAX_ALPHABET_SIZE = 4;
    int DISCORDS_NUM = 5;
    double SAX_NORM_THRESHOLD = 0.01;

    UniDimAlgorithm alg = new GrammarViz();
    HashMap<String, Object> arg = new HashMap<>();
    /*arg.put("SAX_WINDOW_SIZE",seasonality);
    arg.put("SAX_PAA_SIZE",maxAnoms);
    arg.put("SAX_ALPHABET_SIZE",alpha);
    arg.put("DISCORDS_NUM",anomsThreshold);
    arg.put("SAX_NORM_THRESHOLD",anomsThreshold);
*/
    alg.init(arg, ts);
    alg.run();
    System.out.println();
  }
}
