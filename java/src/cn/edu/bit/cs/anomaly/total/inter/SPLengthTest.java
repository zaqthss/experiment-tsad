package cn.edu.bit.cs.anomaly.total.inter;

import cn.edu.bit.cs.anomaly.CPOD;
import cn.edu.bit.cs.anomaly.GrammarViz;
import cn.edu.bit.cs.anomaly.Merlin;
import cn.edu.bit.cs.anomaly.NETS;
import cn.edu.bit.cs.anomaly.NeighborProfile;
import cn.edu.bit.cs.anomaly.PBAD;
import cn.edu.bit.cs.anomaly.SAND;
import cn.edu.bit.cs.anomaly.SHESD;
import cn.edu.bit.cs.anomaly.Stare;
import cn.edu.bit.cs.anomaly.entity.Range;
import cn.edu.bit.cs.anomaly.entity.TimePoint;
import cn.edu.bit.cs.anomaly.entity.TimePointMulDim;
import cn.edu.bit.cs.anomaly.entity.TimeSeries;
import cn.edu.bit.cs.anomaly.entity.TimeSeriesMulDim;
import cn.edu.bit.cs.anomaly.total.MetaData;
import cn.edu.bit.cs.anomaly.total.PointMetaData;
import cn.edu.bit.cs.anomaly.total.SubMetaData;
import cn.edu.bit.cs.anomaly.util.Constants.POS_BIAS;
import cn.edu.bit.cs.anomaly.util.DataHandler;
import cn.edu.bit.cs.anomaly.util.FileHandler;
import java.io.File;
import java.util.*;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Store all the middle results
 */
public class SPLengthTest {

  public static void main(String[] args) throws ArgumentParserException {
    ArgumentParser parser =
        ArgumentParsers.newFor("SPLengthTest")
            .build()
            .description("input ds");
    parser.addArgument("ds").dest("ds").type(String.class).choices("exathlon_sp", "uni_subg_sp", "uni_subs_sp","uni_subt_sp");
    parser.addArgument("seed").dest("seed").type(Integer.class).nargs("+");
    MetaData meta = SubMetaData.getInstance();
    Namespace res = parser.parseArgs(args);
    String dsName = res.getString("ds");
    Map<String, Object> dsMap = meta.getDataset().get(dsName);
    String dir = (String) dsMap.get("dataDir");
    String filePrefix = (String) dsMap.get("rawPrefix");
    List<Integer> seeds = res.getList("seed");
    FileHandler fh = new FileHandler();

    String[] vars = {"20","30","40","50"};
    boolean[] willOperate = {false, true, true, false,false, false, false, false};

    String[] algNames = {"PBAD", "SAND", "NP","MERLIN", "CPOD", "NETS", "Stare", "SHESD"};
    String[] metricNames = {"precision", "recall","fmeasure"};

    final int VARSIZE = vars.length;
    final int ALGNUM = algNames.length;
    final int METRICNUM = 3;  

    long[][] algtime = new long[ALGNUM][2];
    long[][] totaltime = new long[VARSIZE][ALGNUM];
    double[][][] metrics = new double[VARSIZE][ALGNUM][METRICNUM];

    TimeSeries timeseries = null;
    TimeSeriesMulDim timeSeriesMulDim = null;
    int algIndex = 0;

    PBAD pbad = null;
    SAND sand = null;
    NeighborProfile np = null;
    Merlin merlin=null;
    CPOD cpod = null;
    NETS nets = null;
    Stare stare = null;
    SHESD shesd = null;

    double alpha = 0;
    POS_BIAS bias = POS_BIAS.FLAT;
    ArrayList<Range> predictAnomaly = null;
    String dumpDir = "result/sp";
    File dumpF = new File(dumpDir);
    if (!dumpF.exists()) {
      dumpF.mkdirs();
    }

    for (int index = 0; index < VARSIZE; ++index) {
      System.out.println("test with " + vars[index] + " begin");
      Map<Integer, ArrayList<Range>> realAnomalyMap = new HashMap<>();
      Map<Integer, TimeSeries> seriesMap = new HashMap<>();
      Map<Integer, TimeSeriesMulDim> seriesMulMap = new HashMap<>();
      String rawPath = String.format("%s/test/%s_", dir, filePrefix);
      // PBAD
      algIndex = 0;
      if (willOperate[algIndex]) {
        for (int seed : seeds) {
          System.out.println(algNames[algIndex] + " begin on seed " + seed);
          //System.out.println(algNames[algIndex] + " begin");
          if (!seriesMulMap.containsKey(seed)) {
            timeSeriesMulDim = fh.readMulDataWithLabel(rawPath+ seed + ".csv");
            seriesMulMap.put(seed, timeSeriesMulDim);
            ArrayList<Range> realAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
            realAnomalyMap.put(seed, realAnomaly);
          } else {
            timeSeriesMulDim = seriesMulMap.get(seed);
          }
          algtime[algIndex][0] = System.currentTimeMillis();
          pbad = new PBAD();
          Map<String, Object> pbadParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
          pbadParams.put("window_size", Integer.parseInt(vars[index]));
          pbadParams.put("bin_size", Integer.parseInt(vars[index])/10);
          pbad.init(pbadParams, timeSeriesMulDim);
          pbad.run();
          algtime[algIndex][1] = System.currentTimeMillis();
          predictAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
          String dumpPath = String.format(
                  "%s/%s_%s_%s_l_%s.csv", dumpDir, algNames[algIndex], filePrefix, vars[index], "sub");
          fh.writeAnomalyRange(predictAnomaly, dumpPath);
          DataHandler.evaluate(
                  alpha, bias, predictAnomaly, realAnomalyMap.get(seed), metrics[index][algIndex]);
        }
      }
      // SAND
      algIndex++;
      if (willOperate[algIndex]) {
        for (int seed : seeds) {
          System.out.println(algNames[algIndex] + " begin on seed " + seed );
          //System.out.println(algNames[algIndex] + " begin");
          if (!seriesMap.containsKey(seed)) {
            timeseries = fh.readDataWithLabel(rawPath+ seed + ".csv");
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
          sandParams.put("pattern_length", Integer.parseInt(vars[index]));
          sand.init(sandParams, timeseries);
          sand.run();
          algtime[algIndex][1] = System.currentTimeMillis();
          predictAnomaly = DataHandler.findAnomalyRange(timeseries);
          String dumpPath = String.format(
                  "%s/%s_%s_%s_l_%s.csv", dumpDir, algNames[algIndex], filePrefix, vars[index], "sub");
          fh.writeAnomalyRange(predictAnomaly, dumpPath);
          DataHandler.evaluate(
                  alpha, bias, predictAnomaly, realAnomalyMap.get(seed), metrics[index][algIndex]);
        }
      }
      // NP
      algIndex++;
      if (willOperate[algIndex]) {
        for (int seed : seeds) {
          System.out.println(algNames[algIndex] + " begin on seed " + seed );
          //System.out.println(algNames[algIndex] + " begin");
          if (!seriesMap.containsKey(seed)) {
            timeseries = fh.readDataWithLabel(rawPath+seed + ".csv");
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
          npParams.put("sub_len", Integer.parseInt(vars[index]));
          np.init(npParams, timeseries);
          np.run();
          algtime[algIndex][1] = System.currentTimeMillis();
          predictAnomaly = DataHandler.findAnomalyRange(timeseries);
          String dumpPath = String.format(
                  "%s/%s_%s_%s_l_%s.csv", dumpDir, algNames[algIndex], filePrefix, vars[index], "sub");
          fh.writeAnomalyRange(predictAnomaly, dumpPath);
          DataHandler.evaluate(
                  alpha, bias, predictAnomaly, realAnomalyMap.get(seed), metrics[index][algIndex]);
        }
      }
   // merlin
      algIndex++;
      if (willOperate[algIndex]) {
        for (int seed : seeds) {
          System.out.println(algNames[algIndex] + " begin on seed " +seed);
          //System.out.println(algNames[algIndex] + " begin");
          if (!seriesMap.containsKey(seed)) {
            timeseries = fh.readDataWithLabel(rawPath +seed + ".csv");
            seriesMap.put(seed, timeseries);
            if (!realAnomalyMap.containsKey(seed)) {
              ArrayList<Range> realAnomaly = DataHandler.findAnomalyRange(timeseries);
              realAnomalyMap.put(seed, realAnomaly);
            }
          } else {
            timeseries = seriesMap.get(seed);
          }
          algtime[algIndex][0] = System.currentTimeMillis();
          merlin = new Merlin();
          Map<String, Object> merlinParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
          merlinParams.put("minL", Integer.parseInt(vars[index]));
          merlinParams.put("maxL", Integer.parseInt(vars[index]));
          merlin.init(merlinParams, timeseries);
          merlin.run();
          algtime[algIndex][1] = System.currentTimeMillis();
          predictAnomaly = DataHandler.findAnomalyRange(timeseries);
          String dumpPath = String.format(
                  "%s/%s_%s_%s_l_%s.csv", dumpDir, algNames[algIndex], filePrefix, vars[index], "sub");
          fh.writeAnomalyRange(predictAnomaly, dumpPath);
          DataHandler.evaluate(
                  alpha, bias, predictAnomaly, realAnomalyMap.get(seed), metrics[index][algIndex]);
        }
      }
      
      //meta = PointMetaData.getInstance();
      // CPOD
      /*algIndex++;
      if (willOperate[algIndex]) {
        // only need run one time
        // willOperate[algIndex] = false;
        System.out.println(algNames[algIndex] + " begin");
        if (!seriesMulMap.containsKey(dsName)) {
          timeSeriesMulDim = fh.readMulDataWithLabel(rawPath);
          seriesMulMap.put(dsName, timeSeriesMulDim);
          if (!realAnomalyMap.containsKey(dsName)) {
            ArrayList<Range> realAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
            realAnomalyMap.put(dsName, realAnomaly);
          }
        } else {
          timeSeriesMulDim = seriesMulMap.get(dsName);
        }
        algtime[algIndex][0] = System.currentTimeMillis();
        cpod = new CPOD();
        Map<String, Object> cpodParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
        cpod.init(cpodParams, timeSeriesMulDim);
        cpod.run();
        algtime[algIndex][1] = System.currentTimeMillis();
        TreeMap<Long, TimePointMulDim> predictPoint = DataHandler.findAnomalyPoint(timeSeriesMulDim);
        predictAnomaly = DataHandler.transPointToRange(new TreeSet<>(predictPoint.keySet()));
        String dumpPath = String.format(
            "%s/%s_%s_%s_l_%s.csv", dumpDir, algNames[algIndex], filePrefix, vars[index], "sub");
        fh.writeAnomalyRange(predictAnomaly, dumpPath);
        DataHandler.evaluate(
            alpha, bias, predictAnomaly, realAnomalyMap.get(dsName), metrics[index][algIndex]);
      }
      // NETS
      algIndex++;
      if (willOperate[algIndex]) {
        // only need run one time
        // willOperate[algIndex] = false;
        System.out.println(algNames[algIndex] + " begin");
        if (!seriesMulMap.containsKey(dsName)) {
          timeSeriesMulDim = fh.readMulDataWithLabel(rawPath);
          seriesMulMap.put(dsName, timeSeriesMulDim);
          if (!realAnomalyMap.containsKey(dsName)) {
            ArrayList<Range> realAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
            realAnomalyMap.put(dsName, realAnomaly);
          }
        } else {
          timeSeriesMulDim = seriesMulMap.get(dsName);
        }
        algtime[algIndex][0] = System.currentTimeMillis();
        nets = new NETS();
        Map<String, Object> netsParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
        nets.init(netsParams, timeSeriesMulDim);
        nets.run();
        algtime[algIndex][1] = System.currentTimeMillis();
        TreeMap<Long, TimePointMulDim> predictPoint = DataHandler.findAnomalyPoint(timeSeriesMulDim);
        predictAnomaly = DataHandler.transPointToRange(new TreeSet<>(predictPoint.keySet()));
        String dumpPath = String.format(
            "%s/%s_%s_%s_l_%s.csv", dumpDir, algNames[algIndex], filePrefix, vars[index], "sub");
        fh.writeAnomalyRange(predictAnomaly, dumpPath);
        DataHandler.evaluate(
            alpha, bias, predictAnomaly, realAnomalyMap.get(dsName), metrics[index][algIndex]);
      }
      // STARE
      algIndex++;
      if (willOperate[algIndex]) {
        // only need run one time
        // willOperate[algIndex] = false;
        System.out.println(algNames[algIndex] + " begin");
        if (!seriesMulMap.containsKey(dsName)) {
          timeSeriesMulDim = fh.readMulDataWithLabel(rawPath);
          seriesMulMap.put(dsName, timeSeriesMulDim);
          if (!realAnomalyMap.containsKey(dsName)) {
            ArrayList<Range> realAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
            realAnomalyMap.put(dsName, realAnomaly);
          }
        } else {
          timeSeriesMulDim = seriesMulMap.get(dsName);
        }
        algtime[algIndex][0] = System.currentTimeMillis();
        stare = new Stare();
        Map<String, Object> stareParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
        stare.init(stareParams, timeSeriesMulDim);
        stare.run();
        algtime[algIndex][1] = System.currentTimeMillis();
        TreeMap<Long, TimePointMulDim> predictPoint = DataHandler.findAnomalyPoint(timeSeriesMulDim);
        predictAnomaly = DataHandler.transPointToRange(new TreeSet<>(predictPoint.keySet()));
        String dumpPath = String.format(
            "%s/%s_%s_%s_l_%s.csv", dumpDir, algNames[algIndex], filePrefix, vars[index], "sub");
        fh.writeAnomalyRange(predictAnomaly, dumpPath);
        DataHandler.evaluate(
            alpha, bias, predictAnomaly, realAnomalyMap.get(dsName), metrics[index][algIndex]);
      }
      // SHESD
      algIndex++;
      if (willOperate[algIndex]) {
        // only need run one time
        // willOperate[algIndex] = false;
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
        shesd = new SHESD();
        Map<String, Object> shesdParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
        shesd.init(shesdParams, timeseries);
        shesd.run();
        algtime[algIndex][1] = System.currentTimeMillis();
        TreeMap<Long, TimePoint> predictPoint = DataHandler.findAnomalyPoint(timeseries);
        predictAnomaly = DataHandler.transPointToRange(new TreeSet<>(predictPoint.keySet()));
        String dumpPath = String.format(
            "%s/%s_%s_%s_l_%s.csv", dumpDir, algNames[algIndex], filePrefix, vars[index], "sub");
        fh.writeAnomalyRange(predictAnomaly, dumpPath);
        DataHandler.evaluate(
            alpha, bias, predictAnomaly, realAnomalyMap.get(dsName), metrics[index][algIndex]);
      }*/
      for (int algi = 0; algi < ALGNUM; ++algi) {
        totaltime[index][algi] += algtime[algi][1] - algtime[algi][0];
      }
      // write results
      fh.writeResults("length", "length1-sub-" + dsName, vars, algNames,
          metricNames, totaltime, metrics, seeds.size());
    } // end of rIndex
  }
}
