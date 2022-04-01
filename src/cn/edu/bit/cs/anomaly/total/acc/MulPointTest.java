package cn.edu.bit.cs.anomaly.total.acc;

import cn.edu.bit.cs.anomaly.CPOD;
import cn.edu.bit.cs.anomaly.Luminol;
import cn.edu.bit.cs.anomaly.NETS;
import cn.edu.bit.cs.anomaly.SHESD;
import cn.edu.bit.cs.anomaly.Stare;
import cn.edu.bit.cs.anomaly.entity.TimePointMulDim;
import cn.edu.bit.cs.anomaly.entity.TimeSeries;
import cn.edu.bit.cs.anomaly.entity.TimeSeriesMulDim;
import cn.edu.bit.cs.anomaly.total.MetaData;
import cn.edu.bit.cs.anomaly.total.PointMetaData;
import cn.edu.bit.cs.anomaly.total.SubMetaData;
import cn.edu.bit.cs.anomaly.util.DataHandler;
import cn.edu.bit.cs.anomaly.util.FileHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Test accuracy on univariate point anomaly TODO: change vars, algNames, add algorithms, add
 * univariate algorithm
 */
public class MulPointTest {

  public static void main(String[] args) {
    FileHandler fh = new FileHandler();
    String[] vars = {"ecg", "dlr", "smtp"};

    String[] algNames = {"CPOD", "NETS", "Stare", "Luminol", "SHESD"};
    boolean[] willOperate = {true, true, true, true, true};
    String[] metricNames = {"precision", "recall", "fmeasure"};

    final int VARSIZE = vars.length;
    final int ALGNUM = algNames.length; // NETS ...
    final int METRICNUM = 3; // precision, recall, f-measure

    long[][] algtime = new long[ALGNUM][2];
    long[][] totaltime = new long[VARSIZE][ALGNUM];
    double[][][] metrics = new double[VARSIZE][ALGNUM][METRICNUM];

    TimeSeries[] tsArray = null;
    TimeSeriesMulDim timeSeriesMulDim = null;
    int algIndex = 0;

    CPOD cpod = null;
    NETS nets = null;
    Stare stare = null;
    Luminol luminol = null;
    SHESD shesd = null;

    for (int index = 0; index < VARSIZE; ++index) {
      System.out.println("test with " + vars[index] + " begin");
      Map<String, TreeMap<Long, TimePointMulDim>> realAnomalyMulMap = new HashMap<>();
      Map<String, TimeSeries[]> seriesMap = new HashMap<>();
      Map<String, TimeSeriesMulDim> seriesMulMap = new HashMap<>();
      String dsName = vars[index];
      MetaData meta = PointMetaData.getInstance();
      Map<String, Object> dsMap = meta.getDataset().get(dsName);
      String dir = (String) dsMap.get("dataDir");
      String filePrefix = (String) dsMap.get("rawPrefix");
      String rawPath = String.format("%s/%s.csv", dir, filePrefix);

      // CPOD
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
        cpod = new CPOD();
        Map<String, Object> cpodParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
        cpod.init(cpodParams, timeSeriesMulDim);
        cpod.run();
        algtime[algIndex][1] = System.currentTimeMillis();
        DataHandler.evaluate(
            timeSeriesMulDim, realAnomalyMulMap.get(dsName), metrics[index][algIndex]);
      }
      // NETS
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
        nets = new NETS();
        Map<String, Object> netsParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
        nets.init(netsParams, timeSeriesMulDim);
        nets.run();
        algtime[algIndex][1] = System.currentTimeMillis();
        DataHandler.evaluate(
            timeSeriesMulDim, realAnomalyMulMap.get(dsName), metrics[index][algIndex]);
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
        DataHandler.evaluate(
            timeSeriesMulDim, realAnomalyMulMap.get(dsName), metrics[index][algIndex]);
      }
      // Luminol
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
        luminol = new Luminol();
        Map<String, Object> luminolParams =
            meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
        for (TimeSeries ts : tsArray) {
          luminol.init(luminolParams, ts);
          luminol.run();
        }
        algtime[algIndex][1] = System.currentTimeMillis();
        // union each dim
        DataHandler.evaluate(tsArray, realAnomalyMulMap.get(dsName), metrics[index][algIndex]);
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
        // union each dim
        DataHandler.evaluate(tsArray, realAnomalyMulMap.get(dsName), metrics[index][algIndex]);
      }

      for (int algi = 0; algi < ALGNUM; ++algi) {
        totaltime[index][algi] += algtime[algi][1] - algtime[algi][0];
      }
      // write results
      fh.writeResults("acc", "mul-point", vars, algNames, metricNames, totaltime, metrics, 1);
    } // end of rIndex
  }
}
