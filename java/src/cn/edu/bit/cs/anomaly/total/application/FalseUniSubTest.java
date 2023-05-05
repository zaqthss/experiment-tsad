package cn.edu.bit.cs.anomaly.total.application;

import cn.edu.bit.cs.anomaly.*;
import cn.edu.bit.cs.anomaly.entity.*;
import cn.edu.bit.cs.anomaly.total.MetaData;
import cn.edu.bit.cs.anomaly.total.SubMetaData;
import cn.edu.bit.cs.anomaly.util.Constants.POS_BIAS;
import cn.edu.bit.cs.anomaly.util.DataHandler;
import cn.edu.bit.cs.anomaly.util.FileHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/** Test accuracy on univariate subsequence anomaly */
public class FalseUniSubTest {

  public static void main(String[] args) throws Exception {
    FileHandler fh = new FileHandler();

    String[] vars = {"taxi","sed","taxi","machine"};
    boolean[] willOperate = {true, true, true, true,true,true,true};

    String[] algNames = {"PBAD", "LRRDS", "SAND", "NP","MERLIN","GrammarViz","IDK"};
    String[] metricNames = {"fpr","fnr"};

    final int VARSIZE = vars.length;
    final int ALGNUM = algNames.length;
    final int METRICNUM = 2; // precision, recall,fmeasure

    long[][] algtime = new long[ALGNUM][2];
    long[][] totaltime = new long[VARSIZE][ALGNUM];
    double[][][] metrics = new double[VARSIZE][ALGNUM][METRICNUM];

    TimeSeries timeseries = null;
    TimeSeriesMulDim timeSeriesMulDim = null;
    int algIndex = 0;

    PBAD pbad = null;
    LRRDS lrrds = null;
    SAND sand = null;
    NeighborProfile np = null;
    Merlin merlin=null;
    GrammarViz grammarviz=null;
    IDK idk=null;

    double alpha = 0;
    POS_BIAS bias = POS_BIAS.FLAT;
    MetaData meta = SubMetaData.getInstance();
    for (int index = 0; index < VARSIZE; ++index) {
      System.out.println("test with " + vars[index] + " begin");
      Map<String, TreeMap<Long, TimePoint>> realAnomalyMap = new HashMap<>();
      Map<String, TreeMap<Long, TimePointMulDim>> realAnomalyMulMap = new HashMap<>();
      Map<String, TimeSeries> seriesMap = new HashMap<>();
      Map<String, TimeSeriesMulDim> seriesMulMap = new HashMap<>();
      String dsName = vars[index];
      Map<String, Object> dsMap = meta.getDataset().get(dsName);
      String dir = (String) dsMap.get("dataDir");
      String filePrefix = (String) dsMap.get("rawPrefix");
      String rawPath = String.format("%s/test/%s.csv", dir, filePrefix);

      // PBAD
      algIndex = 0;
      if (willOperate[algIndex]) {
        System.out.println(algNames[algIndex] + " begin");
        if (!seriesMulMap.containsKey(dsName)) {
          timeSeriesMulDim = fh.readMulDataWithLabel(rawPath);
          seriesMulMap.put(dsName, timeSeriesMulDim);
          TreeMap<Long, TimePointMulDim> realAnomaly =
                  DataHandler.findAnomalyPoint(timeSeriesMulDim);
          realAnomalyMulMap.put(dsName, realAnomaly);
        } else {
          timeSeriesMulDim = seriesMulMap.get(dsName);
        }
        algtime[algIndex][0] = System.currentTimeMillis();
        pbad = new PBAD();
        Map<String, Object> pbadParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
        pbad.init(pbadParams, timeSeriesMulDim);
        pbad.run();
        algtime[algIndex][1] = System.currentTimeMillis();
        DataHandler.evaluate_false(timeSeriesMulDim, realAnomalyMulMap.get(dsName), metrics[index][algIndex]);
      }

      // LRRDS
      algIndex++;
      if (willOperate[algIndex]) {
        System.out.println(algNames[algIndex] + " begin");
        if (!seriesMulMap.containsKey(dsName)) {
          timeSeriesMulDim = fh.readMulDataWithLabel(rawPath);
          seriesMulMap.put(dsName, timeSeriesMulDim);
          TreeMap<Long, TimePointMulDim> realAnomaly =
                  DataHandler.findAnomalyPoint(timeSeriesMulDim);
          realAnomalyMulMap.put(dsName, realAnomaly);
        } else {
          timeSeriesMulDim = seriesMulMap.get(dsName);
        }
        algtime[algIndex][0] = System.currentTimeMillis();
        lrrds = new LRRDS();
        Map<String, Object> lrrdsParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
        lrrds.init(lrrdsParams, timeSeriesMulDim);
        lrrds.run();
        algtime[algIndex][1] = System.currentTimeMillis();
        DataHandler.evaluate_false(timeSeriesMulDim, realAnomalyMulMap.get(dsName), metrics[index][algIndex]);
      }

      // SAND
      algIndex++;
      if (willOperate[algIndex]) {
        System.out.println(algNames[algIndex] + " begin");
        if (!seriesMap.containsKey(dsName)) {
          timeseries = fh.readDataWithLabel(rawPath);
          seriesMap.put(dsName, timeseries);
          if (!realAnomalyMap.containsKey(dsName)) {
            TreeMap<Long, TimePoint> realAnomaly = DataHandler.findAnomalyPoint(timeseries);
            realAnomalyMap.put(dsName, realAnomaly);
          }
        } else {
          timeseries = seriesMap.get(dsName);
        }
        algtime[algIndex][0] = System.currentTimeMillis();
        sand = new SAND();
        Map<String, Object> sandParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
        sand.init(sandParams, timeseries);
        sand.run();
        algtime[algIndex][1] = System.currentTimeMillis();
        DataHandler.evaluate_false(timeseries, realAnomalyMap.get(dsName), metrics[index][algIndex]);
      }

      // NP
      algIndex++;
      if (willOperate[algIndex]) {
        System.out.println(algNames[algIndex] + " begin");
        if (!seriesMap.containsKey(dsName)) {
          timeseries = fh.readDataWithLabel(rawPath);
          seriesMap.put(dsName, timeseries);
          if (!realAnomalyMap.containsKey(dsName)) {
            TreeMap<Long, TimePoint> realAnomaly = DataHandler.findAnomalyPoint(timeseries);
            realAnomalyMap.put(dsName, realAnomaly);
          }
        } else {
          timeseries = seriesMap.get(dsName);
        }
        algtime[algIndex][0] = System.currentTimeMillis();
        np = new NeighborProfile();
        Map<String, Object> npParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
        np.init(npParams, timeseries);
        np.run();
        algtime[algIndex][1] = System.currentTimeMillis();
        DataHandler.evaluate_false(timeseries, realAnomalyMap.get(dsName), metrics[index][algIndex]);
      }
      
   // MERLIN
      algIndex++;
      if (willOperate[algIndex]) {
        System.out.println(algNames[algIndex] + " begin");
        if (!seriesMap.containsKey(dsName)) {
          timeseries = fh.readDataWithLabel(rawPath);
          seriesMap.put(dsName, timeseries);
          if (!realAnomalyMap.containsKey(dsName)) {
            TreeMap<Long, TimePoint> realAnomaly = DataHandler.findAnomalyPoint(timeseries);
            realAnomalyMap.put(dsName, realAnomaly);
          }
        } else {
          timeseries = seriesMap.get(dsName);
        }
        algtime[algIndex][0] = System.currentTimeMillis();
        merlin = new Merlin();
        Map<String, Object> merlinParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
        merlin.init(merlinParams, timeseries);
        merlin.run();
        algtime[algIndex][1] = System.currentTimeMillis();
        DataHandler.evaluate_false(timeseries, realAnomalyMap.get(dsName), metrics[index][algIndex]);
      }
      
   // grammar
      algIndex++;
      if (willOperate[algIndex]) {
        System.out.println(algNames[algIndex] + " begin");
        if (!seriesMap.containsKey(dsName)) {
          timeseries = fh.readDataWithLabel(rawPath);
          seriesMap.put(dsName, timeseries);
          if (!realAnomalyMap.containsKey(dsName)) {
            TreeMap<Long, TimePoint> realAnomaly = DataHandler.findAnomalyPoint(timeseries);
            realAnomalyMap.put(dsName, realAnomaly);
          }
        } else {
          timeseries = seriesMap.get(dsName);
        }
        algtime[algIndex][0] = System.currentTimeMillis();
        grammarviz = new GrammarViz();
        Map<String, Object> grammarvizParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
        grammarviz.init(grammarvizParams, timeseries);
        grammarviz.run();
        algtime[algIndex][1] = System.currentTimeMillis();
        DataHandler.evaluate_false(timeseries, realAnomalyMap.get(dsName), metrics[index][algIndex]);
      }
      // IDK
      algIndex++;
      if (willOperate[algIndex]) {
        System.out.println(algNames[algIndex] + " begin");
        if (!seriesMap.containsKey(dsName)) {
          timeseries = fh.readDataWithLabel(rawPath);
          seriesMap.put(dsName, timeseries);
          if (!realAnomalyMap.containsKey(dsName)) {
            TreeMap<Long, TimePoint> realAnomaly = DataHandler.findAnomalyPoint(timeseries);
            realAnomalyMap.put(dsName, realAnomaly);
          }
        } else {
          timeseries = seriesMap.get(dsName);
        }
        algtime[algIndex][0] = System.currentTimeMillis();
        idk = new IDK();
        Map<String, Object> idkParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
        idk.init(idkParams, timeseries);
        idk.run();
        algtime[algIndex][1] = System.currentTimeMillis();
        DataHandler.evaluate_false(timeseries, realAnomalyMap.get(dsName), metrics[index][algIndex]);
      }
      for (int algi = 0; algi < ALGNUM; ++algi) {
        totaltime[index][algi] += algtime[algi][1] - algtime[algi][0];
      }
      // write results
      fh.writeResults("application", "uni-sub", vars, algNames, metricNames, totaltime, metrics, 1);
    } // end of rIndex
  }
}
