package cn.edu.bit.cs.anomaly.total.size;

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
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SizeSWaTTest {

  public static void main(String[] args) throws ArgumentParserException {
    ArgumentParser parser =
        ArgumentParsers.newFor("SizeUniSubTest")
            .build()
            .description("input ds, anomalyType, size, seed");
    parser.addArgument("ds").dest("ds").type(String.class);
    parser.addArgument("--anomalyType").dest("anomalyType").type(String.class)
        .choices("subg", "subs", "subt");
    parser.addArgument("--anomalyRate").dest("anomalyRate").type(String.class).setDefault(0);
    parser.addArgument("--anomalyLength").dest("anomalyLength").type(String.class).setDefault(50);
    parser.addArgument("--seed").dest("seed").type(Integer.class).nargs("+").setDefault(1);

    Namespace res = parser.parseArgs(args);
    String dsName = res.getString("ds");
    String anomalyType = res.getString("anomalyType");
    String anomalyRate = res.getString("anomalyRate");
    String anomalyLength = res.getString("anomalyLength");
    List<Integer> seeds=new ArrayList<Integer>();
    seeds.add(res.getInt("seed"));
    MetaData meta = SubMetaData.getInstance();
    Map<String, Object> dsMap = meta.getDataset().get(dsName);
    String dir = (String) dsMap.get("dataDir");
    String filePrefix = (String) dsMap.get("rawPrefix");

    FileHandler fh = new FileHandler();

    String[] vars = {"5000","10000","20000","30000","40000","500000"};
    //String[] vars = {"10000","20000","40000","80000","100000"};
    String[] arate= {"0.278","0.223","0.149","0.112","0.292","0.089"};
    //String[] arate={"0.038","0.048","0.037","0.033","0.031"};
    String[] algNames = {"PBAD", "LRRDS", "SAND", "NP"};
    boolean[] willOperate = {true, true, true,true};
    String[] metricNames = {"precision", "recall"};

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

    double alpha = 0;
    POS_BIAS bias = POS_BIAS.FLAT;
    ArrayList<Range> predictAnomaly = null;

    for (int index = 0; index < VARSIZE; ++index) {
      String rawPath =
          String.format("%s/%s_%s_%s", dir, filePrefix, vars[index],arate[index]);
      System.out.println("test with size " + vars[index] + " on " + rawPath + " begin");
      Map<Integer, ArrayList<Range>> realAnomalyMap = new HashMap<>();
      Map<Integer, TimeSeries> seriesMap = new HashMap<>();
      Map<Integer, TimeSeriesMulDim> seriesMulMap = new HashMap<>();

      // PBAD
      algIndex = 0;
      if (willOperate[algIndex]) {
        for (int seed : seeds) {
          System.out.println(algNames[algIndex] + " begin on seed " + seed);
          if (!seriesMulMap.containsKey(seed)) {
            timeSeriesMulDim = fh.readMulDataWithLabel(rawPath + ".csv");
            seriesMulMap.put(seed, timeSeriesMulDim);
            ArrayList<Range> realAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
            realAnomalyMap.put(seed, realAnomaly);
          } else {
            timeSeriesMulDim = seriesMulMap.get(seed);
          }
          algtime[algIndex][0] = System.currentTimeMillis();
          pbad = new PBAD();
          Map<String,Object> pbadParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
          pbadParams.put("threshold", Double.parseDouble(arate[index]));
          pbad.init(pbadParams, timeSeriesMulDim);
          pbad.run();
          algtime[algIndex][1] = System.currentTimeMillis();
          totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
          predictAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
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
            timeSeriesMulDim = fh.readMulDataWithLabel(rawPath + ".csv");
            seriesMulMap.put(seed, timeSeriesMulDim);
            ArrayList<Range> realAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
            realAnomalyMap.put(seed, realAnomaly);
          } else {
            timeSeriesMulDim = seriesMulMap.get(seed);
          }
          algtime[algIndex][0] = System.currentTimeMillis();
          lrrds = new LRRDS();
          Map<String,Object> lrrdsParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
          if(vars[index].equals("5000")){
            lrrdsParams.put("compressed_rate",0.5);
          }
          lrrds.init(lrrdsParams, timeSeriesMulDim);
          lrrds.run();
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
          if (!seriesMap.containsKey(seed)) {
            timeseries = fh.readDataWithLabel(rawPath  + ".csv");
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
          int batchsize;
          if(Integer.parseInt(vars[index])<=5000) {
        	  batchsize=1000;
          }else {
        	  batchsize=5000;
          }
          int k=(int) (Integer.parseInt(vars[index])*Double.parseDouble(arate[index]))/64;
          sandParams.put("batch_size",batchsize);
          sandParams.put("top-k", k);
          sand.init(sandParams, timeseries);
          sand.run();
          algtime[algIndex][1] = System.currentTimeMillis();
          totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
          predictAnomaly = DataHandler.findAnomalyRange(timeseries);
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
            timeseries = fh.readDataWithLabel(rawPath + ".csv");
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
          int k=(int) (Integer.parseInt(vars[index])*Double.parseDouble(arate[index]))/64;
          npParams.put("top-k", k);
          np.init(npParams, timeseries);
          np.run();
          algtime[algIndex][1] = System.currentTimeMillis();
          totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
          predictAnomaly = DataHandler.findAnomalyRange(timeseries);
          DataHandler.evaluate(
              alpha, bias, predictAnomaly, realAnomalyMap.get(seed), metrics[index][algIndex]);
        }
      }

      // write results
      fh.writeResults(
          "size", "" + dsName,vars, algNames, metricNames, totaltime, metrics, seeds.size());
    } // end of rIndex
  }
}
