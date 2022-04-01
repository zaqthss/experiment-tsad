package cn.edu.bit.cs.anomaly.total.type;

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
import java.util.List;
import java.util.Map;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Test accuracy on univariate subsequence anomaly
 */
public class TypeUniSubTest {

  public static void main(String[] args) throws Exception {
    ArgumentParser parser =
        ArgumentParsers.newFor("TypeUniSubTest")
            .build()
            .description("input ds, anomalyType, size, seed");
    parser.addArgument("ds").dest("ds").type(String.class);
    parser.addArgument("anomalyRate").dest("anomalyRate").type(String.class);
    parser.addArgument("anomalyLength").dest("anomalyLength").type(String.class);
    parser.addArgument("size").dest("size").type(String.class);
    parser.addArgument("seed").dest("seed").type(Integer.class).nargs("+");

    Namespace res = parser.parseArgs(args);
    String dsName = res.getString("ds");
    String anomalyRate = res.getString("anomalyRate");
    String anomalyLength = res.getString("anomalyLength");
    String size = res.getString("size");
    List<Integer> seeds = res.getList("seed");
    MetaData meta = SubMetaData.getInstance();
    Map<String, Object> dsMap = meta.getDataset().get(dsName);
    String dir = (String) dsMap.get("dataDir");
    String filePrefix = (String) dsMap.get("rawPrefix");

    FileHandler fh = new FileHandler();

    String[] vars = {"subg","subs","subt"};
    boolean[] willOperate = {true,true, true, true};

    String[] algNames = {"PBAD", "LRRDS", "SAND", "NP"};
    String[] metricNames = {"precision", "recall"};

    final int VARSIZE = vars.length;
    final int ALGNUM = algNames.length;
    final int METRICNUM = 2;  // precision, recall

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

    for (int index = 0; index < VARSIZE; ++index) {
      String rawPath =
          String.format("%s/%s_%s_len_%s_%s_%s_", dir, filePrefix, vars[index], anomalyLength, size,
              anomalyRate);
      System.out.println("test with " + vars[index] + " on " + rawPath + " begin");
      Map<Integer, ArrayList<Range>> realAnomalyMap = new HashMap<>();
      Map<Integer, TimeSeries> seriesMap = new HashMap<>();
      Map<Integer, TimeSeriesMulDim> seriesMulMap = new HashMap<>();

      // PBAD
      algIndex = 0;
      if (willOperate[algIndex]) {
        for (int seed : seeds) {
          System.out.println(algNames[algIndex] + " begin on seed " + seed);
          if (!seriesMulMap.containsKey(seed)) {
            timeSeriesMulDim = fh.readMulDataWithLabel(rawPath + seed + ".csv");
            seriesMulMap.put(seed, timeSeriesMulDim);
            ArrayList<Range> realAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
            realAnomalyMap.put(seed, realAnomaly);
          } else {
            timeSeriesMulDim = seriesMulMap.get(seed);
          }
          algtime[algIndex][0] = System.currentTimeMillis();
          pbad = new PBAD();
          Map<String,Object> pbadParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
          pbad.init(pbadParams, timeSeriesMulDim);
          pbad.run();
          algtime[algIndex][1] = System.currentTimeMillis();
          totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
          predictAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
          String dumpPath = String.format(
                  "%s/%s_%s_%s_l_%s.csv", "result/type/ids", algNames[algIndex], filePrefix, vars[index], "sub");
          fh.writeAnomalyRange(predictAnomaly, dumpPath);
          DataHandler.evaluate(alpha, bias, predictAnomaly, realAnomalyMap.get(seed),
              metrics[index][algIndex]);
        }
      }
      // LRRDS
      algIndex++;
      if (willOperate[algIndex]) {
        for (int seed : seeds) {
          System.out.println(algNames[algIndex] + " begin on seed " + seed);
          if (!seriesMulMap.containsKey(seed)) {
            timeSeriesMulDim = fh.readMulDataWithLabel(rawPath + seed + ".csv");
            seriesMulMap.put(seed, timeSeriesMulDim);
            ArrayList<Range> realAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
            realAnomalyMap.put(seed, realAnomaly);
          } else {
            timeSeriesMulDim = seriesMulMap.get(seed);
          }
          algtime[algIndex][0] = System.currentTimeMillis();
          lrrds = new LRRDS();
          Map<String,Object> lrrdsParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
          if(vars[index].equals("subt")) {
        	  lrrdsParams.put("compressed_rate", 0.5);
        	  lrrdsParams.put("sub_minlength", 1);
          }
          lrrds.init(lrrdsParams, timeSeriesMulDim);
          lrrds.run();
          algtime[algIndex][1] = System.currentTimeMillis();
          totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
          predictAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
          String dumpPath = String.format(
                  "%s/%s_%s_%s_l_%s.csv", "result/type/ids", algNames[algIndex], filePrefix, vars[index], "sub");
          fh.writeAnomalyRange(predictAnomaly, dumpPath);
          DataHandler.evaluate(alpha, bias, predictAnomaly, realAnomalyMap.get(seed),
              metrics[index][algIndex]);
        }
      }
      // SAND
      algIndex++;
      if (willOperate[algIndex]) {
        for (int seed : seeds) {
          System.out.println(algNames[algIndex] + " begin on seed " + seed);
          if (!seriesMap.containsKey(seed)) {
            timeseries = fh.readDataWithLabel(rawPath + seed + ".csv");
            seriesMap.put(seed, timeseries);
            if (!realAnomalyMap.containsKey(seed)) {
              ArrayList<Range> realAnomaly = DataHandler.findAnomalyRange(timeseries);
              realAnomalyMap.put(seed, realAnomaly);
            }
          } else {
            timeseries = seriesMap.get(seed);
          }
          algtime[algIndex][0] = System.currentTimeMillis();
          sand = new SAND();
          Map<String, Object> sandParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
          sand.init(sandParams, timeseries);
          sand.run();
          algtime[algIndex][1] = System.currentTimeMillis();
          totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
          predictAnomaly = DataHandler.findAnomalyRange(timeseries);
          String dumpPath = String.format(
                  "%s/%s_%s_%s_l_%s.csv", "result/type/ids", algNames[algIndex], filePrefix, vars[index], "sub");
              fh.writeAnomalyRange(predictAnomaly, dumpPath);
          DataHandler.evaluate(
              alpha, bias, predictAnomaly, realAnomalyMap.get(seed), metrics[index][algIndex]);
        }
      }
      // NP
      algIndex++;
      if (willOperate[algIndex]) {
        for (int seed : seeds) {
          System.out.println(algNames[algIndex] + " begin on seed " + seed);
          if (!seriesMap.containsKey(seed)) {
            timeseries = fh.readDataWithLabel(rawPath + seed + ".csv");
            seriesMap.put(seed, timeseries);
            if (!realAnomalyMap.containsKey(seed)) {
              ArrayList<Range> realAnomaly = DataHandler.findAnomalyRange(timeseries);
              realAnomalyMap.put(seed, realAnomaly);
            }
          } else {
            timeseries = seriesMap.get(seed);
          }
          algtime[algIndex][0] = System.currentTimeMillis();
          np = new NeighborProfile();
          Map<String, Object> npParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
          np.init(npParams, timeseries);
          np.run();
          algtime[algIndex][1] = System.currentTimeMillis();
          totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
          predictAnomaly = DataHandler.findAnomalyRange(timeseries);
          String dumpPath = String.format(
                  "%s/%s_%s_%s_l_%s.csv", "result/type/ids", algNames[algIndex], filePrefix, vars[index], "sub");
          fh.writeAnomalyRange(predictAnomaly, dumpPath);
          DataHandler.evaluate(
              alpha, bias, predictAnomaly, realAnomalyMap.get(seed), metrics[index][algIndex]);
        }
      }

      // write results
      fh.writeResults(
          "type", "" + dsName, vars, algNames, metricNames, totaltime, metrics, seeds.size());
    } // end of rIndex
  }
}
