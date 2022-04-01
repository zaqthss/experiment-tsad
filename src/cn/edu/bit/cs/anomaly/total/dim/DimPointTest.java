package cn.edu.bit.cs.anomaly.total.dim;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class DimPointTest {

  public static void main(String[] args) throws ArgumentParserException {
    ArgumentParser parser =
        ArgumentParsers.newFor("DimPointTest")
            .build()
            .description("input ds, anomalyType, size, seed");
    parser.addArgument("ds").dest("ds").type(String.class);
    parser.addArgument("dims").dest("dims").type(Integer.class).nargs("+");
    parser.addArgument("--anomalyType").dest("anomalyType").type(String.class)
        .choices("pointc", "pointg");
    parser.addArgument("--size").dest("size").type(String.class).setDefault("");
    parser.addArgument("--anomalyRate").dest("anomalyRate").type(String.class);
    parser.addArgument("--seed").dest("seed").type(Integer.class).nargs("+");
    MetaData meta = PointMetaData.getInstance();
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
    List<Integer> seeds = res.getList("seed");
    if (!size.equals("")) {
      filePrefix += String.format("_%s_%s_%s", anomalyType, size, anomalyRate);
    } else {
      seeds = new ArrayList<Integer>() {{ add(1); }};
    }

    FileHandler fh = new FileHandler();

    // String[] vars = {"1", "5", "10", "15", "20", "25"};
    String[] vars = DataHandler.transToDims(dims, maxDim);
    String[] algNames = {"CPOD", "NETS", "Stare", "Luminol", "SHESD"};
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

    // real anomaly range is not affected by dimension, since the label is across all dims
    // the file is read only once, but will be extracted according to the tested dimensions
    for (int index = 0; index < VARSIZE; ++index) {
      String rawPath = String.format("%s/%s", dir, filePrefix);
      System.out.println("test with dim " + vars[index] + " on " + rawPath + " begin");
      Map<Integer, TreeMap<Long, TimePointMulDim>> realAnomalyMulMap = new HashMap<>();
      Map<Integer, TimeSeries[]> seriesMap = new HashMap<>();
      Map<Integer, TimeSeriesMulDim> seriesMulMap = new HashMap<>();
      String realPath;
      int currentDim = Integer.parseInt(vars[index]);

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
            TreeMap<Long, TimePointMulDim> realAnomaly = DataHandler.findAnomalyPoint(timeSeriesMulDim);
            realAnomalyMulMap.put(seed, realAnomaly);
          } else {
            timeSeriesMulDim = seriesMulMap.get(seed);
          }
          algtime[algIndex][0] = System.currentTimeMillis();
          cpod = new CPOD();
          Map<String, Object> cpodParams =new HashMap<>(meta.getDataAlgParam().get(dsName).get(algNames[algIndex]));
          TimeSeriesMulDim<TimePointMulDim> timeSeriesDim = timeSeriesMulDim.getSubDims(currentDim);
          cpodParams.put("R",(double)cpodParams.get("R")*StrictMath.pow(((double)currentDim/timeSeriesMulDim.getDim()),0.7));
          System.out.println("Current R is "+cpodParams.get("R")+" with currDim "+ timeSeriesDim.getTimeseries().get(0).getObsVal().length+"  and all Dim "+timeSeriesMulDim.getDim());
          cpod.init(cpodParams, timeSeriesDim);
          cpod.run();
          algtime[algIndex][1] = System.currentTimeMillis();
          totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
          DataHandler.evaluate(
              timeSeriesDim, realAnomalyMulMap.get(seed), metrics[index][algIndex]);
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
            TreeMap<Long, TimePointMulDim> realAnomaly = DataHandler.findAnomalyPoint(timeSeriesMulDim);
            realAnomalyMulMap.put(seed, realAnomaly);
          } else {
            timeSeriesMulDim = seriesMulMap.get(seed);
          }
          algtime[algIndex][0] = System.currentTimeMillis();
          nets = new NETS();
          Map<String, Object> netsParams =
              new HashMap<>(meta.getDataAlgParam().get(dsName).get(algNames[algIndex]));
          TimeSeriesMulDim timeSeriesDim = timeSeriesMulDim.getSubDims(currentDim);
          netsParams.put("R",(double)netsParams.get("R")*StrictMath.pow(((double)currentDim/timeSeriesMulDim.getDim()),0.7));
          nets.init(netsParams, timeSeriesDim);
          nets.run();
          algtime[algIndex][1] = System.currentTimeMillis();
          totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
          DataHandler.evaluate(
              timeSeriesDim, realAnomalyMulMap.get(seed), metrics[index][algIndex]);
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
            timeSeriesMulDim =
                fh.readMulDataWithLabel(realPath + ".csv");
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
              new HashMap<>(meta.getDataAlgParam().get(dsName).get(algNames[algIndex]));
          TimeSeriesMulDim timeSeriesDim = timeSeriesMulDim.getSubDims(currentDim);
          stareParams.put("R",(double)stareParams.get("R")*StrictMath.pow(((double)currentDim/timeSeriesMulDim.getDim()),0.7));
          stare.init(stareParams, timeSeriesDim);
          stare.run();
          algtime[algIndex][1] = System.currentTimeMillis();
          totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
          DataHandler.evaluate(
              timeSeriesDim, realAnomalyMulMap.get(seed), metrics[index][algIndex]);
        }
      }
      // Luminol
      algIndex++;
      if (willOperate[algIndex]) {
        for (int seed : seeds) {
          System.out.println(algNames[algIndex] + " begin on seed " + seed);
          if (size.equals("")) {
            realPath = rawPath;
          } else {
            realPath = rawPath + "_" + seed;
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
            TreeMap<Long, TimePointMulDim> realAnomaly = DataHandler.findAnomalyPoint(timeSeriesMulDim);
            realAnomalyMulMap.put(seed, realAnomaly);
          }
          algtime[algIndex][0] = System.currentTimeMillis();
          luminol = new Luminol();
          Map<String, Object> luminolParams =
              meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
          luminolParams.put("lag_window_size", tsArray[0].getLength() / 80);
          luminolParams.put("future_window_size", tsArray[0].getLength() / 80);
          for (int dimIndex = 0; dimIndex < currentDim; ++dimIndex) {
            luminol.init(luminolParams, tsArray[dimIndex]);
            luminol.run();
          }
          algtime[algIndex][1] = System.currentTimeMillis();
          totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
          DataHandler.evaluate(Arrays.copyOfRange(tsArray, 0, currentDim),
              realAnomalyMulMap.get(seed), metrics[index][algIndex]);
        }
      }
      // SHESD
      algIndex++;
      if (willOperate[algIndex]) {
        for (int seed : seeds) {
          System.out.println(algNames[algIndex] + " begin on seed " + seed);
          if (size.equals("")) {
            realPath = rawPath;
          } else {
            realPath = rawPath + "_" + seed;
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
            TreeMap<Long, TimePointMulDim> realAnomaly = DataHandler.findAnomalyPoint(
                timeSeriesMulDim);
            realAnomalyMulMap.put(seed, realAnomaly);
          }
          algtime[algIndex][0] = System.currentTimeMillis();
          shesd = new SHESD();
          Map<String, Object> shesdParams =
              meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
          for (int dimIndex = 0; dimIndex < currentDim; ++dimIndex) {
            shesd.init(shesdParams, tsArray[dimIndex]);
            shesd.run();
          }
          algtime[algIndex][1] = System.currentTimeMillis();
          totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
          DataHandler.evaluate(Arrays.copyOfRange(tsArray, 0, currentDim),
              realAnomalyMulMap.get(seed), metrics[index][algIndex]);
        }
      }
      // write results
      fh.writeResults("dim", "mul-point-" + dsName, vars, algNames, metricNames, totaltime,
          metrics, seeds.size());
    } // end of rIndex
  }
}