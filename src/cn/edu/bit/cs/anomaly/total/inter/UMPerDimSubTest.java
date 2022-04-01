package cn.edu.bit.cs.anomaly.total.inter;

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
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Store all the middle results
 */
public class UMPerDimSubTest {

  public static void main(String[] args) {
    FileHandler fh = new FileHandler();

    // String[] vars = {"exercise", "exathlon", "systhetic w/o correlation"};
    //String[] vars = {"exercise", "exathlon"};
    String[] vars= {"exercise","mul_ncor_subg","mul_cor_subg"};
    int[] dims = {3,3,3};
    //int[] dims = {3,19};
    boolean[] willOperate = {true, true, true, true};

    String[] algNames = {"PBAD", "LRRDS", "SAND", "NP"};
    String[] metricNames = {"precision", "recall"};

    final int VARSIZE = vars.length;
    final int ALGNUM = algNames.length;
    final int METRICNUM = 2;  // precision, recall

    long[][] algtime = new long[ALGNUM][2];
    long[][] totaltime = new long[VARSIZE][ALGNUM];
    double[][][] metrics = new double[VARSIZE][ALGNUM][METRICNUM];

    TimeSeries[] tsArray = null;
    TimeSeriesMulDim timeSeriesMulDim = null;
    int algIndex = 0;

    PBAD pbad = null;
    LRRDS lrrds = null;
    SAND sand = null;
    NeighborProfile np = null;

    double alpha = 0;
    POS_BIAS bias = POS_BIAS.FLAT;
    ArrayList<Range> predictAnomaly = null;
    String dumpDir = "result/um_mid";
    File dumpF = new File(dumpDir);
    if (!dumpF.exists()) {
      dumpF.mkdirs();
    }
    MetaData meta = SubMetaData.getInstance();
    for (int index = 0; index < VARSIZE; ++index) {
      System.out.println("test with " + vars[index] + " begin");
      Map<String, ArrayList<Range>> realAnomalyMap = new HashMap<>();
      Map<String, TimeSeries[]> seriesMap = new HashMap<>();
      Map<String, TimeSeriesMulDim> seriesMulMap = new HashMap<>();
      String dsName = vars[index];
      Map<String, Object> dsMap = meta.getDataset().get(dsName);
      String dir = (String)dsMap.get("dataDir");
      String filePrefix = (String)dsMap.get("rawPrefix");
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
        String dumpPath = String.format(
            "%s/%s_%s_%s.csv", dumpDir, algNames[algIndex], filePrefix, "sub");
        fh.writeAnomalyRange(predictAnomaly, dumpPath);
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
        String dumpPath = String.format(
            "%s/%s_%s_%s.csv", dumpDir, algNames[algIndex], filePrefix, "sub");
        fh.writeAnomalyRange(predictAnomaly, dumpPath);
        DataHandler.evaluate(
            alpha, bias, predictAnomaly, realAnomalyMap.get(dsName), metrics[index][algIndex]);
      }
      // SAND
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
          ArrayList<Range> realAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
          realAnomalyMap.put(dsName, realAnomaly);
        }
        algtime[algIndex][0] = System.currentTimeMillis();
        sand = new SAND();
        Map<String, Object> sandParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
        for (TimeSeries ts : tsArray) {
          sand.init(sandParams, ts);
          sand.run();
        }
        algtime[algIndex][1] = System.currentTimeMillis();
        for (int dIndex = 0; dIndex < tsArray.length; ++dIndex) {
          ArrayList<Range> dimAnomaly = DataHandler.findAnomalyRange(tsArray[dIndex]);
          String dumpPath = String.format(
              "%s/%s_%s_dim_%s_%s.csv", dumpDir, algNames[algIndex],filePrefix,dIndex, "sub");
          fh.writeAnomalyRange(dimAnomaly, dumpPath);
        }
        DataHandler.evaluate(
            alpha, bias, tsArray, realAnomalyMap.get(dsName), metrics[index][algIndex]);
      }
      // NP
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
          ArrayList<Range> realAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
          realAnomalyMap.put(dsName, realAnomaly);
        }
        algtime[algIndex][0] = System.currentTimeMillis();
        np = new NeighborProfile();
        Map<String, Object> npParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
        for (TimeSeries ts : tsArray) {
          np.init(npParams, ts);
          np.run();
        }
        algtime[algIndex][1] = System.currentTimeMillis();
        for (int dIndex = 0; dIndex < tsArray.length; ++dIndex) {
          ArrayList<Range> dimAnomaly = DataHandler.findAnomalyRange(tsArray[dIndex]);
          String dumpPath = String.format(
              "%s/%s_%s_dim_%s_%s.csv", dumpDir, algNames[algIndex], filePrefix, dIndex, "sub");
          fh.writeAnomalyRange(dimAnomaly, dumpPath);
        }
        DataHandler.evaluate(
            alpha, bias, tsArray, realAnomalyMap.get(dsName), metrics[index][algIndex]);
      }

      for (int algi = 0; algi < ALGNUM; ++algi) {
        totaltime[index][algi] += algtime[algi][1] - algtime[algi][0];
      }
      // write results
      fh.writeResults("um", "dim-sub1", vars, algNames,
          metricNames, totaltime, metrics, 1);
    } // end of rIndex
  }

}
