package cn.edu.bit.cs.anomaly;

import cn.edu.bit.cs.anomaly.entity.TimeSeriesMulDim;
import cn.edu.bit.cs.anomaly.util.Constants.IS_ANOMALY;
import cn.edu.bit.cs.anomaly.util.Stare.Detector;
import cn.edu.bit.cs.anomaly.util.Stare.StareStreamGenerator;
import cn.edu.bit.cs.anomaly.util.Stare.Tuple;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.PriorityQueue;

public class Stare implements MultiDimAlgorithm {

  static String dataset = "YahooA1"; //dataset name
  static int W = 1421; //window size
  static int S = 71; //slide size
  static double R = 65; // size of a grid cell
  static int K = 50; // number of neighbors
  static int nW = 10000; // number of windows to process
  static int fixedN = 2; // fix N by a positive integer value
  static double skipThred = 0.1; // between 0 and 1. the default optimal value is 0.1
  static String printType = "Console"; // "Console" or "File"
  static boolean reportOutlierList = true; // true or false
  static BufferedWriter fw;

  TimeSeriesMulDim orign;
  StareStreamGenerator streamGen;

  @Override
  public void init(Map<String, Object> args, TimeSeriesMulDim timeseries) {
    timeseries.clear();
    R = (double)args.get("R");
    K = (int)args.get("K");
    S = (int)args.get("S");
    W = (int)args.get("W");
    fixedN = (int) Math.ceil(W * (double)args.get("rate"));
    nW = (int)args.get("nW");
    skipThred = (double)args.get("skipThred");

    orign = timeseries;
    streamGen = new StareStreamGenerator(timeseries);
  }

  @Override
  public void run() {
    try {
      int nS = W / S;
      int dim = streamGen.getMinValues().length;
      Detector detector = new Detector(dim, nS, R, K, S, streamGen.getMinValues(), skipThred);
      int numWindows = 0;
      for (int i = 0; i < nW + nS - 1; i++) {
        //Get the new slide
        ArrayList<Tuple> newTuples = streamGen.getTuples(i * S, (i + 1) * S);
        if (newTuples.size() < S) {
          break;
        }
        int newOutliers = 0;
        for (Tuple t : newTuples) {
          if (t.outlier) {
            newOutliers++;
          }
        }
        detector.slide(newTuples, i, newOutliers);
        //Check if a whole window can be prepared
        if (i >= nS - 1) {
          numWindows++;
          int N = (fixedN > 0 ? fixedN : detector.window.getNumOutliers()); // given N or the true N
          //Get Top-N outliers
          PriorityQueue<Tuple> topNOut = detector.getOutliers(N, numWindows);
          for(Tuple t :topNOut)
            orign.getTimePoint(t.id).setIs_anomaly(IS_ANOMALY.TRUE);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
