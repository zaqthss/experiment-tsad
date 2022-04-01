package cn.edu.bit.cs.anomaly.total.acc;

import cn.edu.bit.cs.anomaly.LRRDS;
import cn.edu.bit.cs.anomaly.NeighborProfile;
import cn.edu.bit.cs.anomaly.PBAD;
import cn.edu.bit.cs.anomaly.SAND;
import cn.edu.bit.cs.anomaly.entity.Range;
import cn.edu.bit.cs.anomaly.entity.TimeSeries;
import cn.edu.bit.cs.anomaly.entity.TimeSeriesMulDim;
import cn.edu.bit.cs.anomaly.total.MetaData;
import cn.edu.bit.cs.anomaly.total.SubMetaData;
import cn.edu.bit.cs.anomaly.util.Constants.POS_BIAS;
import cn.edu.bit.cs.anomaly.util.DataHandler;
import cn.edu.bit.cs.anomaly.util.FileHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/** Test accuracy on univariate subsequence anomaly */
public class UniSubTest {

  public static void main(String[] args) throws Exception {
    FileHandler fh = new FileHandler();

    String[] vars = {"sed"};
    boolean[] willOperate = {false, true, false, false};

    String[] algNames = {"PBAD", "LRRDS", "SAND", "NP"};
    String[] metricNames = {"precision", "recall"};

    final int VARSIZE = vars.length;
    final int ALGNUM = algNames.length;
    final int METRICNUM = 2; // precision, recall

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

    double alpha = 0;
    POS_BIAS bias = POS_BIAS.FLAT;
    ArrayList<Range> predictAnomaly = null;
    MetaData meta = SubMetaData.getInstance();
    for (int index = 0; index < VARSIZE; ++index) {
      System.out.println("test with " + vars[index] + " begin");
      Map<String, ArrayList<Range>> realAnomalyMap = new HashMap<>();
      Map<String, TimeSeries> seriesMap = new HashMap<>();
      Map<String, TimeSeriesMulDim> seriesMulMap = new HashMap<>();
      String dsName = vars[index];
      Map<String, Object> dsMap = meta.getDataset().get(dsName);
      String dir = (String) dsMap.get("dataDir");
      String filePrefix = (String) dsMap.get("rawPrefix");
      String rawPath = String.format("%s/%s.csv", dir, filePrefix);

      // PBAD
      algIndex = 0;
      if (willOperate[algIndex]) {
        System.out.println(algNames[algIndex] + " begin");
        if (!seriesMulMap.containsKey(dsName)) {
          timeSeriesMulDim = fh.readMulDataWithLabel(rawPath);
          seriesMulMap.put(dsName, timeSeriesMulDim);
          ArrayList<Range> realAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
          realAnomalyMap.put(dsName, realAnomaly);
        } else {
          timeSeriesMulDim = seriesMulMap.get(dsName);
        }
        algtime[algIndex][0] = System.currentTimeMillis();
        pbad = new PBAD();
        Map<String, Object> pbadParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
        pbad.init(pbadParams, timeSeriesMulDim);
        pbad.run();
        algtime[algIndex][1] = System.currentTimeMillis();
        predictAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
        DataHandler.evaluate(
            alpha, bias, predictAnomaly, realAnomalyMap.get(dsName), metrics[index][algIndex]);
      }

      // LRRDS
      algIndex++;
      if (willOperate[algIndex]) {
        System.out.println(algNames[algIndex] + " begin");
        if (!seriesMulMap.containsKey(dsName)) {
          timeSeriesMulDim = fh.readMulDataWithLabel(rawPath);
          seriesMulMap.put(dsName, timeSeriesMulDim);
          ArrayList<Range> realAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
          realAnomalyMap.put(dsName, realAnomaly);
        } else {
          timeSeriesMulDim = seriesMulMap.get(dsName);
        }
        algtime[algIndex][0] = System.currentTimeMillis();
        lrrds = new LRRDS();
        Map<String, Object> lrrdsParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
        lrrds.init(lrrdsParams, timeSeriesMulDim);
        lrrds.run();
        algtime[algIndex][1] = System.currentTimeMillis();
        predictAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
        DataHandler.evaluate(
            alpha, bias, predictAnomaly, realAnomalyMap.get(dsName), metrics[index][algIndex]);
      }

      // SAND
      algIndex++;
      if (willOperate[algIndex]) {
        System.out.println(algNames[algIndex] + " begin");
        if (!seriesMap.containsKey(dsName)) {
          timeseries = fh.readDataWithLabel(rawPath);
          seriesMap.put(dsName, timeseries);
          if (!realAnomalyMap.containsKey(dsName)) {
            ArrayList<Range> realAnomaly = DataHandler.findAnomalyRange(timeseries);
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
        predictAnomaly = DataHandler.findAnomalyRange(timeseries);
        DataHandler.evaluate(
            alpha, bias, predictAnomaly, realAnomalyMap.get(dsName), metrics[index][algIndex]);
      }

      // NP
      algIndex++;
      if (willOperate[algIndex]) {
        System.out.println(algNames[algIndex] + " begin");
        if (!seriesMap.containsKey(dsName)) {
          timeseries = fh.readDataWithLabel(rawPath);
          seriesMap.put(dsName, timeseries);
          if (!realAnomalyMap.containsKey(dsName)) {
            ArrayList<Range> realAnomaly = DataHandler.findAnomalyRange(timeseries);
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
        predictAnomaly = DataHandler.findAnomalyRange(timeseries);
        DataHandler.evaluate(
            alpha, bias, predictAnomaly, realAnomalyMap.get(dsName), metrics[index][algIndex]);
      }

      for (int algi = 0; algi < ALGNUM; ++algi) {
        totaltime[index][algi] += algtime[algi][1] - algtime[algi][0];
      }
      // write results
      fh.writeResults("acc", "uni-sub-", vars, algNames, metricNames, totaltime, metrics, 1);
    } // end of rIndex
  }
}
