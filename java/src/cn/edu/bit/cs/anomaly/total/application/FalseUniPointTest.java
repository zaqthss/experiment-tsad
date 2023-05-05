package cn.edu.bit.cs.anomaly.total.application;

import cn.edu.bit.cs.anomaly.*;
import cn.edu.bit.cs.anomaly.entity.*;
import cn.edu.bit.cs.anomaly.total.MetaData;
import cn.edu.bit.cs.anomaly.total.PointMetaData;
import cn.edu.bit.cs.anomaly.total.SubMetaData;
import cn.edu.bit.cs.anomaly.util.Constants.POS_BIAS;
import cn.edu.bit.cs.anomaly.util.DataHandler;
import cn.edu.bit.cs.anomaly.util.FileHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/** Test accuracy on univariate subsequence anomaly */
public class FalseUniPointTest {

  public static void main(String[] args) throws Exception {
    FileHandler fh = new FileHandler();

    String[] vars = {"ecg","smtp","dlr"};
    boolean[] willOperate = {true, true, false};

    String[] algNames = {"NETS", "Stare", "SHESD"};
    String[] metricNames = {"fpr","fnr"};

    final int VARSIZE = vars.length;
    final int ALGNUM = algNames.length;
    final int METRICNUM = 2; // precision, recall,fmeasure

    long[][] algtime = new long[ALGNUM][2];
    long[][] totaltime = new long[VARSIZE][ALGNUM];
    double[][][] metrics = new double[VARSIZE][ALGNUM][METRICNUM];


    TimeSeries timeseries = null;
    TimeSeries[] tsArray = null;
    TimeSeriesMulDim timeSeriesMulDim = null;
    int algIndex = 0;

    NETS nets = null;
    Stare stare = null;
    SHESD shesd = null;

    double alpha = 0;
    POS_BIAS bias = POS_BIAS.FLAT;
    MetaData meta = PointMetaData.getInstance();
    for (int index = 0; index < VARSIZE; ++index) {
      System.out.println("test with " + vars[index] + " begin");
      Map<String, TreeMap<Long, TimePoint>> realAnomalyMap = new HashMap<>();
      Map<String, TreeMap<Long, TimePointMulDim>> realAnomalyMulMap = new HashMap<>();
      Map<String, TimeSeries[]> seriesMap = new HashMap<>();
      Map<String, TimeSeriesMulDim> seriesMulMap = new HashMap<>();
      String dsName = vars[index];
      Map<String, Object> dsMap = meta.getDataset().get(dsName);
      String dir = (String) dsMap.get("dataDir");
      String filePrefix = (String) dsMap.get("rawPrefix");
      String rawPath = String.format("%s/test/%s.csv", dir, filePrefix);

      // NETS
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
        nets = new NETS();
        Map<String, Object> netsParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
        nets.init(netsParams, timeSeriesMulDim);
        nets.run();
        algtime[algIndex][1] = System.currentTimeMillis();
        DataHandler.evaluate_false(timeSeriesMulDim, realAnomalyMulMap.get(dsName), metrics[index][algIndex]);
      }

      // STARE
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
        stare = new Stare();
        Map<String, Object> stareParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
        stare.init(stareParams, timeSeriesMulDim);
        stare.run();
        algtime[algIndex][1] = System.currentTimeMillis();
        DataHandler.evaluate_false(timeSeriesMulDim, realAnomalyMulMap.get(dsName), metrics[index][algIndex]);
      }

      // SHESD
      algIndex++;
      if (willOperate[algIndex]) {
        System.out.println(algNames[algIndex] + " begin");
        if (seriesMap.containsKey(dsName)) {
          tsArray = seriesMap.get(dsName);
        } else if (seriesMulMap.containsKey(dsName)) {
          tsArray = seriesMulMap.get(dsName).convert();
        } else {
          timeSeriesMulDim = fh.readMulDataWithLabel(rawPath);
          tsArray = timeSeriesMulDim.convert();
          seriesMulMap.put(dsName, timeSeriesMulDim);
          TreeMap<Long, TimePointMulDim> realAnomaly =
                  DataHandler.findAnomalyPoint(timeSeriesMulDim);
          realAnomalyMulMap.put(dsName, realAnomaly);
        }
        algtime[algIndex][0] = System.currentTimeMillis();
        shesd = new SHESD();
        Map<String, Object> shesdParams =
                meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
        for (TimeSeries ts : tsArray) {
          shesd.init(shesdParams, ts);
          shesd.run();
        }
        algtime[algIndex][1] = System.currentTimeMillis();
        DataHandler.evaluate_false(timeseries, realAnomalyMap.get(dsName), metrics[index][algIndex]);
      }

      for (int algi = 0; algi < ALGNUM; ++algi) {
        totaltime[index][algi] += algtime[algi][1] - algtime[algi][0];
      }
      // write results
      fh.writeResults("application", "uni-point", vars, algNames, metricNames, totaltime, metrics, 1);
    } // end of rIndex
  }
}
