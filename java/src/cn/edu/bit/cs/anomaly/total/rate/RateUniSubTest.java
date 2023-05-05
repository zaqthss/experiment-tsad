package cn.edu.bit.cs.anomaly.total.rate;

import cn.edu.bit.cs.anomaly.*;
import cn.edu.bit.cs.anomaly.entity.Range;
import cn.edu.bit.cs.anomaly.entity.TimeSeries;
import cn.edu.bit.cs.anomaly.entity.TimeSeriesMulDim;
import cn.edu.bit.cs.anomaly.total.MetaData;
import cn.edu.bit.cs.anomaly.total.PointMetaData;
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
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class RateUniSubTest {

  public static void main(String[] args) throws ArgumentParserException {
    ArgumentParser parser =
        ArgumentParsers.newFor("RateUniSubTest")
            .build()
            .description("input ds, anomalyType, size, seed");
    parser.addArgument("ds").dest("ds").type(String.class);
    parser
        .addArgument("anomalyType")
        .dest("anomalyType")
        .type(String.class)
        .choices("subg", "subs", "subt");
    parser.addArgument("anomalyLength").dest("anomalyLength").type(String.class);
    parser.addArgument("size").dest("size").type(String.class);
    parser.addArgument("seed").dest("seed").type(Integer.class).nargs("+");

    Namespace res = parser.parseArgs(args);
    String dsName = res.getString("ds");
    String anomalyType = res.getString("anomalyType");
    String anomalyLength = res.getString("anomalyLength");
    String size = res.getString("size");
    List<Integer> seeds = res.getList("seed");
    MetaData meta = SubMetaData.getInstance();

    Map<String, Object> dsMap = meta.getDataset().get(dsName);
    String dir = (String) dsMap.get("dataDir");
    String filePrefix = (String) dsMap.get("rawPrefix");

    FileHandler fh = new FileHandler();

    String[] vars = {"0.05","0.075","0.1","0.125","0.15","0.175","0.2","0.225","0.25"};
    String[] algNames = {"PBAD", "LRRDS", "SAND", "NP","MERLIN","GrammarViz","IDK"};
    boolean[] willOperate = {false,false, false, false,false,false,true};
    String[] metricNames = {"precision", "recall","fmeasure"};

    final int VARSIZE = vars.length;
    final int ALGNUM = algNames.length;
    final int METRICNUM = 3; // precision, recall, f-measure

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
    Merlin merlin=null;
    GrammarViz grammarviz=null;
    IDK idk=null;
    
    double alpha = 0;
    POS_BIAS bias = POS_BIAS.FLAT;
    ArrayList<Range> predictAnomaly = null;

    for (int index = 0; index < VARSIZE; ++index) {
      String rawPath =
          String.format("%s/test/%s_%s_len_%s_%s_%s_", dir, filePrefix, anomalyType, anomalyLength, size,
              vars[index]);
      System.out.println("test with anomaly rate " + vars[index] + " on " + rawPath + " begin");
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
          Map<String, Object> pbadParams = meta.getDataAlgParam().get(dsName)
              .get(algNames[algIndex]);
          pbadParams.put("threshold",Double.parseDouble(vars[index])*0.9);
          pbad.init(pbadParams, timeSeriesMulDim);
          pbad.run();
          algtime[algIndex][1] = System.currentTimeMillis();
          totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
          predictAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
          String dumpPath = String.format(
                  "%s/%s_%s_%s_%s.csv", "../middle/rate", algNames[algIndex], dsName, vars[index],seed);
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
          Map<String, Object> lrrdsParams = meta.getDataAlgParam().get(dsName)
              .get(algNames[algIndex]);
          lrrds.init(lrrdsParams, timeSeriesMulDim);
          lrrds.run();
          algtime[algIndex][1] = System.currentTimeMillis();
          totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
          predictAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
          String dumpPath = String.format(
                  "%s/%s_%s_%s_%s.csv", "../middle/rate", algNames[algIndex], dsName, vars[index],seed);
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
          Map<String, Object> sandParams = meta.getDataAlgParam().get(dsName)
              .get(algNames[algIndex]);
          //int k=(int) (Integer.parseInt(size)*Double.parseDouble(vars[index]))/50;
          int k=(int) (Integer.parseInt(size)*Double.parseDouble(vars[index]))/Integer.parseInt(anomalyLength);
          sandParams.put("top_k", k);
          sand.init(sandParams, timeseries);
          sand.run();
          algtime[algIndex][1] = System.currentTimeMillis();
          totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
          predictAnomaly = DataHandler.findAnomalyRange(timeseries);
          String dumpPath = String.format(
                  "%s/%s_%s_%s_%s.csv", "../middle/rate", algNames[algIndex], dsName, vars[index],seed);
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
          //int k=(int) (Integer.parseInt(size)*Double.parseDouble(vars[index]))/50;
          int k=(int) (Integer.parseInt(size)*Double.parseDouble(vars[index]))/Integer.parseInt(anomalyLength);
          npParams.put("top_k", k);
          np.init(npParams, timeseries);
          np.run();
          algtime[algIndex][1] = System.currentTimeMillis();
          totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
          predictAnomaly = DataHandler.findAnomalyRange(timeseries);
          String dumpPath = String.format(
                  "%s/%s_%s_%s_%s.csv", "../middle/rate", algNames[algIndex], dsName, vars[index],seed);
          fh.writeAnomalyRange(predictAnomaly, dumpPath);
          DataHandler.evaluate(
              alpha, bias, predictAnomaly, realAnomalyMap.get(seed), metrics[index][algIndex]);
        }
      }
      
   // MERLIN
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
          merlin = new Merlin();
          Map<String, Object> merlinParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
          //int k=(int) (Integer.parseInt(size)*Double.parseDouble(vars[index]))/50;
          int k=(int) (Integer.parseInt(size)*Double.parseDouble(vars[index]))/Integer.parseInt(anomalyLength);
          merlinParams.put("top_k", k);
          merlin.init(merlinParams, timeseries);
          merlin.run();
          algtime[algIndex][1] = System.currentTimeMillis();
          totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
          predictAnomaly = DataHandler.findAnomalyRange(timeseries);
          String dumpPath = String.format(
                  "%s/%s_%s_%s_%s.csv", "../middle/rate", algNames[algIndex], dsName, vars[index],seed);
          fh.writeAnomalyRange(predictAnomaly, dumpPath);
          DataHandler.evaluate(
              alpha, bias, predictAnomaly, realAnomalyMap.get(seed), metrics[index][algIndex]);
        }
      }
      
      // grammar
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
             grammarviz = new GrammarViz();
             Map<String, Object> grammarvizParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
             //int k=(int) (Integer.parseInt(size)*Double.parseDouble(vars[index]))/50;
             int k=(int) (Integer.parseInt(size)*Double.parseDouble(vars[index]))/Integer.parseInt(anomalyLength);
             grammarvizParams.put("DISCORDS_NUM", k);
             grammarviz.init(grammarvizParams, timeseries);
             grammarviz.run();
             algtime[algIndex][1] = System.currentTimeMillis();
             totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
             predictAnomaly = DataHandler.findAnomalyRange(timeseries);
             String dumpPath = String.format(
                     "%s/%s_%s_%s_%s.csv", "../middle/rate", algNames[algIndex], dsName, vars[index],seed);
             fh.writeAnomalyRange(predictAnomaly, dumpPath);
             DataHandler.evaluate(
                 alpha, bias, predictAnomaly, realAnomalyMap.get(seed), metrics[index][algIndex]);
           }
         }

    // IDK
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
          idk = new IDK();
          Map<String, Object> idkParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
          int k=(int) (Integer.parseInt(size)*Double.parseDouble(vars[index]))/50;
          idkParams.put("top_k", k);
          idk.init(idkParams, timeseries);
          idk.run();
          algtime[algIndex][1] = System.currentTimeMillis();
          totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
          predictAnomaly = DataHandler.findAnomalyRange(timeseries);
          String dumpPath = String.format(
                  "%s/%s_%s_%s_%s.csv", "../middle/rate", algNames[algIndex], dsName, vars[index],seed);
          fh.writeAnomalyRange(predictAnomaly, dumpPath);
          DataHandler.evaluate(
                  alpha, bias, predictAnomaly, realAnomalyMap.get(seed), metrics[index][algIndex]);
        }
      }
      // write results
      fh.writeResults(
          "rate", "uni-sub1-" + anomalyType, vars, algNames, metricNames, totaltime, metrics,
          seeds.size());
    } // end of rIndex
  }
}
