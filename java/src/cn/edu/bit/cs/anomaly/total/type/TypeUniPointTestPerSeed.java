package cn.edu.bit.cs.anomaly.total.type;

import cn.edu.bit.cs.anomaly.CPOD;
import cn.edu.bit.cs.anomaly.Luminol;
import cn.edu.bit.cs.anomaly.NETS;
import cn.edu.bit.cs.anomaly.SHESD;
import cn.edu.bit.cs.anomaly.Stare;
import cn.edu.bit.cs.anomaly.entity.TimePoint;
import cn.edu.bit.cs.anomaly.entity.TimePointMulDim;
import cn.edu.bit.cs.anomaly.entity.TimeSeries;
import cn.edu.bit.cs.anomaly.entity.TimeSeriesMulDim;
import cn.edu.bit.cs.anomaly.evaluate.SingleMetric;
import cn.edu.bit.cs.anomaly.total.MetaData;
import cn.edu.bit.cs.anomaly.total.PointMetaData;
import cn.edu.bit.cs.anomaly.util.DataHandler;
import cn.edu.bit.cs.anomaly.util.FileHandler;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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

public class TypeUniPointTestPerSeed {
  public static Map<String,String []> resultPerSeed = new HashMap<String,String []>();
  public static List<Integer> seeds = null;
  public static String[] vars = {"pointc"};
  public static String dsName = null;
  public static void main(String[] args) throws ArgumentParserException {
    ArgumentParser parser =
        ArgumentParsers.newFor("TypeUniPointTest")
            .build()
            .description("input ds, anomalyType, size, seed");
    parser.addArgument("ds").dest("ds").type(String.class);
    parser.addArgument("rate").dest("rate").type(String.class);
    parser.addArgument("size").dest("size").type(String.class);
    parser.addArgument("seed").dest("seed").type(Integer.class).nargs("+");

    Namespace res = parser.parseArgs(args);
    dsName = res.getString("ds");
    String rate = res.getString("rate");
    String size = res.getString("size");
    seeds = res.getList("seed");
    MetaData meta = PointMetaData.getInstance();

    Map<String, Object> dsMap = meta.getDataset().get(dsName);
    String dir = (String) dsMap.get("dataDir");
    String filePrefix = (String) dsMap.get("rawPrefix");

    FileHandler fh = new FileHandler();


    String[] algNames = {"CPOD", "NETS", "Stare", "Luminol",  "SHESD"};
    boolean[] willOperate = {true, true, true, false, true};
    String[] metricNames = { "fmeasure"};

    final int VARSIZE = vars.length;
    final int ALGNUM = algNames.length;
    final int METRICNUM = 1; // precision, recall, f-measure

    long[][] algtime = new long[ALGNUM][2];
    long[][] totaltime = new long[VARSIZE][ALGNUM];
    double[][][] metrics = new double[VARSIZE][ALGNUM][METRICNUM];

    TimeSeries timeseries = null;
    TimeSeriesMulDim timeSeriesMulDim = null;
    int algIndex = 0;
    CPOD cpod = null;
    NETS nets = null;
    Stare stare = null;
    Luminol luminol = null;
    SHESD shesd = null;


    for (int index = 0; index < VARSIZE; ++index) {
      String rawPath = String.format("%s/%s/%s_%s_%s_%s_", dir,"test", filePrefix,vars[index], size,rate);
      System.out.println("test with anomaly rate " + vars[index] + " on " + rawPath + " begin");
      Map<Integer, TreeMap<Long, TimePoint>> realAnomalyMap = new HashMap<>();
      Map<Integer, TreeMap<Long, TimePointMulDim>> realAnomalyMulMap = new HashMap<>();
      Map<Integer, TimeSeries> seriesMap = new HashMap<>();
      Map<Integer, TimeSeriesMulDim> seriesMulMap = new HashMap<>();

      // CPOD
      algIndex = 0;
      if (willOperate[algIndex]) {
        for (int seed : seeds) {
          System.out.println(algNames[algIndex] + " begin on seed " + seed);
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
          addseedresult(timeSeriesMulDim, realAnomalyMulMap.get(seed),algNames[algIndex],seed);

        }
      }
      // NETS
      algIndex++;
      if (willOperate[algIndex]) {
        for (int seed : seeds) {
          System.out.println(algNames[algIndex] + " begin on seed " + seed);
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
          addseedresult(timeSeriesMulDim, realAnomalyMulMap.get(seed),algNames[algIndex],seed);
        }
      }
      // Stare
      algIndex++;
      if (willOperate[algIndex]) {
        for (int seed : seeds) {
          System.out.println(algNames[algIndex] + " begin on seed " + seed);
          if (!seriesMulMap.containsKey(seed)) {
            timeSeriesMulDim = fh.readMulDataWithLabel(rawPath);
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
          stareParams.put("rate",Double.valueOf(rate));
          stare.init(stareParams, timeSeriesMulDim);
          stare.run();
          algtime[algIndex][1] = System.currentTimeMillis();
          totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
          addseedresult(timeSeriesMulDim, realAnomalyMulMap.get(seed),algNames[algIndex],seed);
        }
      }
      // Luminol
      algIndex++;
      if (willOperate[algIndex]) {
        for (int seed : seeds) {
          System.out.println(algNames[algIndex] + " begin on seed " + seed);
          if (!seriesMap.containsKey(seed)) {
            timeseries = fh.readDataWithLabel(rawPath + seed + ".csv");
            seriesMap.put(seed, timeseries);
            TreeMap<Long, TimePoint> realAnomaly = DataHandler.findAnomalyPoint(timeseries);
            realAnomalyMap.put(seed, realAnomaly);
          } else {
            timeseries = seriesMap.get(seed);
          }
          algtime[algIndex][0] = System.currentTimeMillis();
          luminol = new Luminol();
          Map<String, Object> luminolParams =
              meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
          luminolParams.put("threshold", Double.valueOf(rate));
          luminol.init(luminolParams, timeseries);
          luminol.run();
          algtime[algIndex][1] = System.currentTimeMillis();
          totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
          addseedresult(timeSeriesMulDim, realAnomalyMulMap.get(seed),algNames[algIndex],seed);
        }
      }
      // SHESD
      algIndex++;
      if (willOperate[algIndex]) {
        for (int seed : seeds) {
          System.out.println(algNames[algIndex] + " begin on seed " + seed);
          if (!seriesMap.containsKey(seed)) {
            timeseries = fh.readDataWithLabel(rawPath + seed + ".csv");
            seriesMap.put(seed, timeseries);
            TreeMap<Long, TimePoint> realAnomaly = DataHandler.findAnomalyPoint(timeseries);
            realAnomalyMap.put(seed, realAnomaly);
          } else {
            timeseries = seriesMap.get(seed);
          }
          algtime[algIndex][0] = System.currentTimeMillis();
          shesd = new SHESD();
          Map<String, Object> shesdParams =
              meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
          shesdParams.put("maxAnoms", Double.valueOf(rate));
          shesdParams.put("alpha", Double.valueOf(rate));
          shesd.init(shesdParams, timeseries);
          shesd.run();
          algtime[algIndex][1] = System.currentTimeMillis();
          totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
          addseedresult(timeSeriesMulDim, realAnomalyMulMap.get(seed),algNames[algIndex],seed);
        }
      }
      // write results
      try {
        writeResultperSeed();
      } catch (IOException e) {
        e.printStackTrace();
      }
    } // end of rIndex
  }

  public static void addseedresult(TimeSeriesMulDim timeseries, TreeMap<Long, TimePointMulDim> realAnomaly,
      String algname,int seed) {
    TreeMap<Long, TimePointMulDim> predictAnomaly = DataHandler.findAnomalyPoint(timeseries);
    SingleMetric sm = new SingleMetric();
    sm.computeMetric(realAnomaly, predictAnomaly, timeseries);
    if(resultPerSeed.containsKey(algname)){
      resultPerSeed.get(algname)[seed] = sm.fmeasure+"";
    }
    else{
      String [] s = new String[seeds.size()];
      s[seed] = sm.fmeasure+"";
      resultPerSeed.put(algname,s);
    }
  }

  public static void writeResultperSeed() throws IOException {
    PrintWriter datpw = null;
    List<String> l = new ArrayList(resultPerSeed.keySet());
    l.add(0,"seed");
    String head = String.join(",",l);
    // metric
    String sep=",";
      datpw =
          new PrintWriter(
              new FileWriter(
                  "result/" + "type" + "/" + "type-Perseed" + "-" + vars[0] +"-"+dsName+ ".csv"));
      datpw.println(head);
      // each line
      for (int i = 0; i < seeds.size(); ++i) {
        datpw.print(seeds.get(i) + sep);
        for (String s [] : resultPerSeed.values()) {
          datpw.print(s[i] + sep);
        }
        datpw.println();
      }
      datpw.flush();
      datpw.close();
    }

  public static void writeResults(
      String type,
      String prefix,
      String[] vars,
      String[] algNames,
      String[] metricNames,
      long[][] totaltime,
      double[][][] totalcost,
      int repeatNumber) {
    // generate all .dat files
    String sep = ",";
    try {
      PrintWriter datpw = null;
      String head = type + sep + String.join(sep, algNames);

      // metric
      for (int mi = 0; mi < metricNames.length; ++mi) {
        datpw =
            new PrintWriter(
                new FileWriter(
                    "result/" + type + "/" + prefix + "-" + metricNames[mi] + "-" + type + ".csv"));
        datpw.println(head);
        // each line
        for (int i = 0; i < vars.length; ++i) {
          datpw.print(vars[i] + sep);
          for (int algi = 0; algi < algNames.length - 1; ++algi) {
            datpw.print(totalcost[i][algi][mi] / repeatNumber + sep);
          }
          datpw.println(totalcost[i][algNames.length - 1][mi] / repeatNumber);
        }
        datpw.flush();
        datpw.close();
      }
      // time
      datpw =
          new PrintWriter(
              new FileWriter("result/" + type + "/" + prefix + "-time-" + type + ".csv"));
      datpw.println(head);
      for (int i = 0; i < vars.length; ++i) {
        datpw.print(vars[i] + sep);
        for (int algi = 0; algi < algNames.length - 1; ++algi) {
          datpw.print((double) (totaltime[i][algi]) / repeatNumber + sep);
        }
        datpw.println((double) (totaltime[i][algNames.length - 1]) / repeatNumber);
      }
      datpw.flush();
      datpw.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
