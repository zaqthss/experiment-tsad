package cn.edu.bit.cs.anomaly.total.inter;

import cn.edu.bit.cs.anomaly.CPOD;
import cn.edu.bit.cs.anomaly.NETS;
import cn.edu.bit.cs.anomaly.Stare;
import cn.edu.bit.cs.anomaly.entity.TimePoint;
import cn.edu.bit.cs.anomaly.entity.TimePointMulDim;
import cn.edu.bit.cs.anomaly.entity.TimeSeries;
import cn.edu.bit.cs.anomaly.entity.TimeSeriesMulDim;
import cn.edu.bit.cs.anomaly.total.MetaData;
import cn.edu.bit.cs.anomaly.total.PointMetaData;
import cn.edu.bit.cs.anomaly.total.SubMetaData;
import cn.edu.bit.cs.anomaly.util.DataHandler;
import cn.edu.bit.cs.anomaly.util.FileHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class SlidePointTest {
  public static void main(String[] args) throws ArgumentParserException {
    ArgumentParser parser =
        ArgumentParsers.newFor("WindowPointTest")
            .build()
            .description("input ds, anomalyType, size, seed");
    parser.addArgument("ds").dest("ds").type(String.class);
    parser.addArgument("sList").dest("sList").type(String.class).nargs("+");
    parser.addArgument("--wSize").dest("wSize").type(Integer.class).setDefault(2000);
    parser.addArgument("--anomalyType").dest("anomalyType").type(String.class)
        .choices("pointc", "pointg");
    parser.addArgument("--size").dest("size").type(String.class).setDefault("").setDefault("");
    parser.addArgument("--anomalyRate").dest("anomalyType").type(String.class);
    parser.addArgument("--seed").dest("seed").type(Integer.class).nargs("+");
    MetaData meta = PointMetaData.getInstance();
    Namespace res = parser.parseArgs(args);
    String dsName = res.getString("ds");
    List<String> slideList = res.getList("sList");
    int windowSize = res.getInt("wSize");
    Map<String, Object> dsMap = meta.getDataset().get(dsName);
    String dir = (String) dsMap.get("dataDir");
    String filePrefix = (String) dsMap.get("rawPrefix");
    int dim = (int) dsMap.get("dim");
    String resultPrefix = "uni-point-";
    if (dim > 1) {
      resultPrefix = "mul-point-";
    }
    resultPrefix += dsName;

    String anomalyType = res.getString("anomalyType");
    String size = res.getString("size");
    String anomalyRate = res.getString("anomalyRate");
    List<Integer> seeds = res.getList("seed");

    if (!size.equals("")) {
      filePrefix += String.format("_%s_%s_%s_", anomalyType, size, anomalyRate);
    } else {
      seeds = new ArrayList<Integer>() {{ add(1); }};
    }

    FileHandler fh = new FileHandler();

    String[] vars = slideList.toArray(new String[0]);
    boolean[] willOperate = {true, true, true};
    String[] algNames = {"CPOD", "NETS", "Stare"};
    String[] metricNames = {"precision", "recall", "fmeasure"};

    final int VARSIZE = vars.length;
    final int ALGNUM = algNames.length;
    final int METRICNUM = 3; // precision, recall, f-measure

    long[][] algtime = new long[ALGNUM][2];
    long[][] totaltime = new long[VARSIZE][ALGNUM];
    double[][][] metrics = new double[VARSIZE][ALGNUM][METRICNUM];

    TimeSeriesMulDim timeSeriesMulDim = null;
    int algIndex = 0;

    CPOD cpod = null;
    NETS nets = null;
    Stare stare = null;

    for (int index = 0; index < VARSIZE; ++index) {
      String rawPath =
          String.format("%s/%s", dir, filePrefix);
      System.out.println("test with slide size " + vars[index] + " on " + rawPath + " begin");
      Map<Integer, TreeMap<Long, TimePoint>> realAnomalyMap = new HashMap<>();
      Map<Integer, TreeMap<Long, TimePointMulDim>> realAnomalyMulMap = new HashMap<>();
      Map<Integer, TimeSeries> seriesMap = new HashMap<>();
      Map<Integer, TimeSeriesMulDim> seriesMulMap = new HashMap<>();
      String realPath;

      // CPOD
      algIndex = 0;
      if (willOperate[algIndex]) {
        for (int seed : seeds) {
          System.out.println(algNames[algIndex] + " begin on seed " + seed);
          if (size.equals("")) {
            realPath = rawPath;
          } else {
            realPath = rawPath + "_" + seed;
          }
          if (!seriesMulMap.containsKey(seed)) {
            timeSeriesMulDim = fh.readMulDataWithLabel(realPath + ".csv");
            seriesMulMap.put(seed, timeSeriesMulDim);
            TreeMap<Long, TimePointMulDim> realAnomaly =
                DataHandler.findAnomalyPoint(timeSeriesMulDim);
            realAnomalyMulMap.put(seed, realAnomaly);
          } else {
            timeSeriesMulDim = seriesMulMap.get(seed);
          }
          algtime[algIndex][0] = System.currentTimeMillis();
          cpod = new CPOD();
          Map<String, Object> cpodParams =
              meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
          int mul = Math.max(windowSize / Integer.parseInt(vars[index]), 1);
          cpodParams.put("mul", mul);
          cpodParams.put("sSize", Integer.parseInt(vars[index]));
          cpod.init(cpodParams, timeSeriesMulDim);
          System.out.println(cpodParams);
          cpod.run();
          algtime[algIndex][1] = System.currentTimeMillis();
          totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
          DataHandler.evaluate(
              timeSeriesMulDim, realAnomalyMulMap.get(seed), metrics[index][algIndex]);
        }
      }
      // NETS
      algIndex++;
      if (willOperate[algIndex]) {
        for (int seed : seeds) {
          System.out.println(algNames[algIndex] + " begin on seed " + seed);
          if (size.equals("")) {
            realPath = rawPath;
          } else {
            realPath = rawPath + "_" + seed;
          }
          if (!seriesMulMap.containsKey(seed)) {
            timeSeriesMulDim = fh.readMulDataWithLabel(realPath + ".csv");
            seriesMulMap.put(seed, timeSeriesMulDim);
            TreeMap<Long, TimePointMulDim> realAnomaly =
                DataHandler.findAnomalyPoint(timeSeriesMulDim);
            realAnomalyMulMap.put(seed, realAnomaly);
          } else {
            timeSeriesMulDim = seriesMulMap.get(seed);
          }
          algtime[algIndex][0] = System.currentTimeMillis();
          nets = new NETS();
          Map<String, Object> netsParams =
              meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
          netsParams.put("W", windowSize);
          netsParams.put("S", Integer.parseInt(vars[index]));
          nets.init(netsParams, timeSeriesMulDim);
          System.out.println(netsParams);
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
          if (size.equals("")) {
            realPath = rawPath;
          } else {
            realPath = rawPath + "_" + seed;
          }
          if (!seriesMulMap.containsKey(seed)) {
            timeSeriesMulDim = fh.readMulDataWithLabel(realPath + ".csv");
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
          stareParams.put("W", windowSize);
          stareParams.put("S", Integer.parseInt(vars[index]));
          stare.init(stareParams, timeSeriesMulDim);
          System.out.println(stareParams);
          stare.run();
          algtime[algIndex][1] = System.currentTimeMillis();
          totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
          DataHandler.evaluate(
              timeSeriesMulDim, realAnomalyMulMap.get(seed), metrics[index][algIndex]);
        }
      }
      // write results
      fh.writeResults(
          "slide", resultPrefix, vars, algNames, metricNames, totaltime, metrics, seeds.size());
    } // end of rIndex
  }
}