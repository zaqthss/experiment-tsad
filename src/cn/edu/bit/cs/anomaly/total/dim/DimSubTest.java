package cn.edu.bit.cs.anomaly.total.dim;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class DimSubTest {

  public static void main(String[] args) throws ArgumentParserException {
    ArgumentParser parser =
        ArgumentParsers.newFor("DimSubTest")
            .build()
            .description("input ds, anomalyType, size, seed");
    parser.addArgument("ds").dest("ds").type(String.class);
    parser.addArgument("dims").dest("dims").type(Integer.class).nargs("+");
    parser.addArgument("--anomalyType").dest("anomalyType").type(String.class)
        .choices("subg", "subs", "subt");
    parser.addArgument("--size").dest("size").type(String.class).setDefault("");
    parser.addArgument("--anomalyRate").dest("anomalyType").type(String.class);
    parser.addArgument("--seed").dest("seed").type(Integer.class).nargs("+").setDefault(1);
    MetaData meta  = SubMetaData.getInstance();
    Namespace res = parser.parseArgs(args);
    String dsName = res.getString("ds");
    List<Integer> dims = res.getList("dims");
    Map<String, Object> dsMap = meta.getDataset().get(dsName);
    String dir = (String) dsMap.get("dataDir");
    String filePrefix = (String) dsMap.get("rawPrefix");
    int maxDim = (int) dsMap.get("dim");

    String anomalyType = res.getString("anomalyType");
    String size = res.getString("size");
    String anomalyRate = res.getString("anomalyRate");
    //List<Integer> seeds = res.getList("seed");
    List<Integer> seeds=new ArrayList<Integer>();
    seeds.add(res.getInt("seed"));
    if (!size.equals("")) {
      filePrefix += String.format("_%s_%s_%s_", anomalyType, size, anomalyRate);
    } else {
      seeds = new ArrayList<Integer>() {{ add(1); }};
    }

    FileHandler fh = new FileHandler();

    String[] vars = DataHandler.transToDims(dims, maxDim);
    boolean[] willOperate = {true, true, false, false};
    String[] algNames = {"PBAD", "LRRDS", "SAND", "NP"};
    String[] metricNames = {"precision", "recall"};

    final int VARSIZE = vars.length;
    final int ALGNUM = algNames.length;
    final int METRICNUM = 3; // precision, recall, f-measure

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

    // real anomaly range is not affected by dimension, since the label is across all dims
    // the file is read only once, but will be extracted according to the tested dimensions
    for (int index = 0; index < VARSIZE; ++index) {
      String rawPath = String.format("%s/%s", dir, filePrefix);
      System.out.println("test with dim " + vars[index] + " on " + rawPath + " begin");
      Map<Integer, ArrayList<Range>> realAnomalyMap = new HashMap<>();
      Map<Integer, TimeSeries[]> seriesMap = new HashMap<>();
      Map<Integer, TimeSeriesMulDim> seriesMulMap = new HashMap<>();
      String realPath;
      int currentDim = Integer.parseInt(vars[index]);

      // PBAD
      algIndex = 0;
      if (willOperate[algIndex]) {
        for (int seed : seeds) {
          System.out.println(algNames[algIndex] + " begin on seed " + seed);
          if (size.equals("")) {
            realPath = rawPath;
          } else {
            realPath = rawPath + seed;
          }
          if (!seriesMulMap.containsKey(seed)) {
            timeSeriesMulDim = fh.readMulDataWithLabel(realPath + ".csv");
            seriesMulMap.put(seed, timeSeriesMulDim);
            ArrayList<Range> realAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
            realAnomalyMap.put(seed, realAnomaly);
          } else {
            timeSeriesMulDim = seriesMulMap.get(seed);
          }
          algtime[algIndex][0] = System.currentTimeMillis();
          pbad = new PBAD();
          Map<String,Object> pbadParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
          TimeSeriesMulDim timeSeriesDim = timeSeriesMulDim.getSubDims(currentDim);
          pbad.init(pbadParams, timeSeriesDim);
          pbad.run();
          algtime[algIndex][1] = System.currentTimeMillis();
          totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
          predictAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
          String dumpPath = String.format(
                  "%s/%s_%s_%s_l_%s.csv", "result/dim/ids", algNames[algIndex], filePrefix, vars[index], "sub");
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
          if (size.equals("")) {
            realPath = rawPath;
          } else {
            realPath = rawPath + seed;
          }
          if (!seriesMulMap.containsKey(seed)) {
            timeSeriesMulDim = fh.readMulDataWithLabel(realPath + ".csv");
            seriesMulMap.put(seed, timeSeriesMulDim);
            ArrayList<Range> realAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
            realAnomalyMap.put(seed, realAnomaly);
          } else {
            timeSeriesMulDim = seriesMulMap.get(seed);
          }
          algtime[algIndex][0] = System.currentTimeMillis();
          lrrds = new LRRDS();
          Map<String,Object> lrrdsParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
          TimeSeriesMulDim timeSeriesDim = timeSeriesMulDim.getSubDims(currentDim);
          lrrds.init(lrrdsParams, timeSeriesDim);
          lrrds.run();
          String dumpPath = String.format(
                  "%s/%s_%s_%s_l_%s.csv", "result/dim/ids", algNames[algIndex], filePrefix, vars[index], "sub");
          fh.writeAnomalyRange(predictAnomaly, dumpPath);
          algtime[algIndex][1] = System.currentTimeMillis();
          totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
          predictAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
          DataHandler.evaluate(alpha, bias, predictAnomaly, realAnomalyMap.get(seed),
              metrics[index][algIndex]);
        }
      }
      // SAND
      algIndex++;
      if (willOperate[algIndex]) {
        for (int seed : seeds) {
          System.out.println(algNames[algIndex] + " begin on seed " + seed);
          if (size.equals("")) {
            realPath = rawPath;
          } else {
            realPath = rawPath + seed;
          }
          if (seriesMap.containsKey(seed)) {
            tsArray = seriesMap.get(seed);
          } else if (seriesMulMap.containsKey(seed)) {
            tsArray = seriesMulMap.get(seed).convert();
            seriesMap.put(seed, tsArray);
          } else {
            timeSeriesMulDim = fh.readMulDataWithLabel(realPath + ".csv");
            tsArray = timeSeriesMulDim.convert();
            seriesMap.put(seed, tsArray);
            seriesMulMap.put(seed, timeSeriesMulDim);
            ArrayList<Range> realAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
            realAnomalyMap.put(seed, realAnomaly);
          }
          algtime[algIndex][0] = System.currentTimeMillis();
          sand = new SAND();
          Map<String, Object> sandParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
          for (int dimIndex = 0; dimIndex < currentDim; ++dimIndex) {
            sand.init(sandParams, tsArray[dimIndex]);
            sand.run();
          }
          algtime[algIndex][1] = System.currentTimeMillis();
          totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
          DataHandler.evaluate(
              alpha, bias, Arrays.copyOfRange(tsArray, 0, currentDim),
              realAnomalyMap.get(seed), metrics[index][algIndex]);
        }
      }
      // NP
      algIndex++;
      if (willOperate[algIndex]) {
        for (int seed : seeds) {
          System.out.println(algNames[algIndex] + " begin on seed " + seed);
          if (size.equals("")) {
            realPath = rawPath;
          } else {
            realPath = rawPath + seed;
          }
          if (seriesMap.containsKey(seed)) {
            tsArray = seriesMap.get(seed);
          } else if (seriesMulMap.containsKey(seed)) {
            tsArray = seriesMulMap.get(seed).convert();
            seriesMap.put(seed, tsArray);
          } else {
            timeSeriesMulDim = fh.readMulDataWithLabel(realPath + ".csv");
            tsArray = timeSeriesMulDim.convert();
            seriesMap.put(seed, tsArray);
            seriesMulMap.put(seed, timeSeriesMulDim);
            ArrayList<Range> realAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
            realAnomalyMap.put(seed, realAnomaly);
          }
          algtime[algIndex][0] = System.currentTimeMillis();
          np = new NeighborProfile();
          Map<String, Object> npParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
          for (int dimIndex = 0; dimIndex < currentDim; ++dimIndex) {
            np.init(npParams, tsArray[dimIndex]);
            np.run();
          }
          algtime[algIndex][1] = System.currentTimeMillis();
          totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
          DataHandler.evaluate(
              alpha, bias, Arrays.copyOfRange(tsArray, 0, currentDim),
              realAnomalyMap.get(seed), metrics[index][algIndex]);
        }
      }
      // write results
      /*fh.writeResults(
          "dim", "mul-sub2-" + dsName, vars, algNames, metricNames, totaltime, metrics, seeds.size());*/
    } // end of rIndex
  }
}
