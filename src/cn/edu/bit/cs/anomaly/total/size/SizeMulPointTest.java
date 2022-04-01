package cn.edu.bit.cs.anomaly.total.size;

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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class SizeMulPointTest {

  public static void main(String[] args) throws ArgumentParserException {
    ArgumentParser parser =
        ArgumentParsers.newFor("SizeMulPointTest")
            .build()
            .description("input ds, anomalyType, size, seed");
    parser.addArgument("ds").dest("ds").type(String.class);
    parser.addArgument("anomalyType").dest("anomalyType").type(String.class)
        .choices("pointc", "pointg");
    parser.addArgument("anomalyRate").dest("anomalyRate").type(String.class);
    parser.addArgument("seed").dest("seed").type(Integer.class).nargs("+");

    Namespace res = parser.parseArgs(args);
    String dsName = res.getString("ds");
    String anomalyType = res.getString("anomalyType");
    String anomalyRate = res.getString("anomalyRate");
    List<Integer> seeds = res.getList("seed");
    MetaData meta = PointMetaData.getInstance();

    Map<String, Object> dsMap = meta.getDataset().get(dsName);
    String dir = (String) dsMap.get("dataDir");
    String filePrefix = (String) dsMap.get("rawPrefix");

    FileHandler fh = new FileHandler();

    // TODO: may change
    String[] vars = {"1000", "5000", "10000", "20000", "50000", "100000"};
    String[] algNames = {"CPOD", "NETS", "Stare", "Luminol",  "SHESD"};
    boolean[] willOperate = {true, true, true, false, false};
    String[] metricNames = {"precision", "recall", "fmeasure"};

    final int VARSIZE = vars.length;
    final int ALGNUM = algNames.length;
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
      String rawPath = String.format("%s/%s_%s_%s_%s_", dir, filePrefix, anomalyType, vars[index], anomalyRate);
      System.out.println("test with size " + vars[index] + " on " + rawPath + " begin");
      Map<Integer, TreeMap<Long, TimePointMulDim>> realAnomalyMulMap = new HashMap<>();
      Map<Integer, TimeSeries[]> seriesMap = new HashMap<>();
      Map<Integer, TimeSeriesMulDim> seriesMulMap = new HashMap<>();

      // CPOD
      algIndex = 0;
      if (willOperate[algIndex]) {
        for (int seed : seeds) {
          if (!seriesMulMap.containsKey(seed)) {
            timeSeriesMulDim = fh.readMulDataWithLabel(rawPath + seed + ".csv");
            seriesMulMap.put(seed, timeSeriesMulDim);
            TreeMap<Long, TimePointMulDim> realAnomaly = DataHandler.findAnomalyPoint(timeSeriesMulDim);
            realAnomalyMulMap.put(seed, realAnomaly);
          } else {
            timeSeriesMulDim = seriesMulMap.get(seed);
          }
          algtime[algIndex][0] = System.currentTimeMillis();
          cpod = new CPOD();
          Map<String, Object> cpodParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
          cpod.init(cpodParams, timeSeriesMulDim);
          cpod.run();
          algtime[algIndex][1] = System.currentTimeMillis();
          totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
          DataHandler.evaluate(timeSeriesMulDim, realAnomalyMulMap.get(seed), metrics[index][algIndex]);
        }
      }
      // NETS
      algIndex++;
      if (willOperate[algIndex]) {
        for (int seed : seeds) {
          if (!seriesMulMap.containsKey(seed)) {
            timeSeriesMulDim = fh.readMulDataWithLabel(rawPath + seed + ".csv");
            seriesMulMap.put(seed, timeSeriesMulDim);
            TreeMap<Long, TimePointMulDim> realAnomaly = DataHandler.findAnomalyPoint(timeSeriesMulDim);
            realAnomalyMulMap.put(seed, realAnomaly);
          } else {
            timeSeriesMulDim = seriesMulMap.get(seed);
          }
          algtime[algIndex][0] = System.currentTimeMillis();
          nets = new NETS();
          Map<String, Object> netsParams =
              meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
          nets.init(netsParams, timeSeriesMulDim);
          nets.run();
          algtime[algIndex][1] = System.currentTimeMillis();
          totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
          DataHandler.evaluate(
              timeSeriesMulDim, realAnomalyMulMap.get(seed), metrics[index][algIndex]);
        }
      }
      // Stare
      algIndex++;
      if (willOperate[algIndex]) {
        for (int seed : seeds) {
          System.out.println(algNames[algIndex] + " begin on seed " + seed);
          if (!seriesMulMap.containsKey(seed)) {
            timeSeriesMulDim = fh.readMulDataWithLabel(rawPath + seed + ".csv");
            seriesMulMap.put(seed, timeSeriesMulDim);
            TreeMap<Long, TimePointMulDim> realAnomaly =
                DataHandler.findAnomalyPoint(timeSeriesMulDim);
            realAnomalyMulMap.put(seed, realAnomaly);
          } else {
            timeSeriesMulDim = seriesMulMap.get(seed);
          }
          algtime[algIndex][0] = System.currentTimeMillis();
          stare = new Stare();
          Map<String, Object> stareParams =
              meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
          stare.init(stareParams, timeSeriesMulDim);
          stare.run();
          algtime[algIndex][1] = System.currentTimeMillis();
          totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
          DataHandler.evaluate(
              timeSeriesMulDim, realAnomalyMulMap.get(seed), metrics[index][algIndex]);
        }
      }
      // Luminol
      algIndex++;
      if (willOperate[algIndex]) {
        for (int seed : seeds) {
          System.out.println(algNames[algIndex] + " begin on seed " + seed);
          if (seriesMap.containsKey(seed)) {
            tsArray = seriesMap.get(seed);
          } else if (seriesMulMap.containsKey(seed)) {
            tsArray = seriesMulMap.get(seed).convert();
          } else {
            timeSeriesMulDim = fh.readMulDataWithLabel(rawPath + seed + ".csv");
            tsArray = timeSeriesMulDim.convert();
            seriesMulMap.put(seed, timeSeriesMulDim);
            TreeMap<Long, TimePointMulDim> realAnomaly = DataHandler.findAnomalyPoint(timeSeriesMulDim);
            realAnomalyMulMap.put(seed, realAnomaly);
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
          totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
          DataHandler.evaluate(tsArray, realAnomalyMulMap.get(seed), metrics[index][algIndex]);
        }
      }
      // SHESD
      algIndex++;
      if (willOperate[algIndex]) {
        for (int seed : seeds) {
          System.out.println(algNames[algIndex] + " begin on seed " + seed);
          if (seriesMap.containsKey(seed)) {
            tsArray = seriesMap.get(seed);
          } else if (seriesMulMap.containsKey(seed)) {
            tsArray = seriesMulMap.get(seed).convert();
          } else {
            timeSeriesMulDim = fh.readMulDataWithLabel(rawPath + seed + ".csv");
            tsArray = timeSeriesMulDim.convert();
            seriesMulMap.put(seed, timeSeriesMulDim);
            TreeMap<Long, TimePointMulDim> realAnomaly = DataHandler.findAnomalyPoint(timeSeriesMulDim);
            realAnomalyMulMap.put(seed, realAnomaly);
          }
          algtime[algIndex][0] = System.currentTimeMillis();
          shesd = new SHESD();
          Map<String, Object> shesdParams =
              meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
          shesdParams.put("seasonality",(int)Math.floor(Double.parseDouble(vars[index])*0.2));
          for (TimeSeries ts : tsArray) {
            try{
            shesd.init(shesdParams, ts);
            shesd.run();
            }
            catch (Exception e){
              e.printStackTrace();
            }
          }
          algtime[algIndex][1] = System.currentTimeMillis();
          totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
          DataHandler.evaluate(tsArray, realAnomalyMulMap.get(seed), metrics[index][algIndex]);
        }
      }
      // write results
      fh.writeResults("size", "mul-point-" + dsName, vars, algNames, metricNames, totaltime,
          metrics, seeds.size());
    } // end of rIndex
  }
}