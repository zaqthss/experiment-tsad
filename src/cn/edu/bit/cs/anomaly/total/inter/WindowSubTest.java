package cn.edu.bit.cs.anomaly.total.inter;

import cn.edu.bit.cs.anomaly.SAND;
import cn.edu.bit.cs.anomaly.entity.Range;
import cn.edu.bit.cs.anomaly.entity.TimeSeries;
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
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class WindowSubTest {
  public static void main(String[] args) throws ArgumentParserException {
    ArgumentParser parser =
        ArgumentParsers.newFor("WindowPointTest")
            .build()
            .description("input ds, anomalyType, size, seed");
    parser.addArgument("ds").dest("ds").type(String.class);
    parser.addArgument("Ws").dest("Ws").type(String.class).nargs("+");

    MetaData meta = SubMetaData.getInstance();
    Namespace res = parser.parseArgs(args);
    String dsName = res.getString("ds");
    List<String> windowList = res.getList("Ws");
    Map<String, Object> dsMap = meta.getDataset().get(dsName);
    String dir = (String) dsMap.get("dataDir");
    String filePrefix = (String) dsMap.get("rawPrefix");
    String resultPrefix = "uni-sub-";

    List<Integer> seeds = new ArrayList<Integer>() {{ add(1); }};

    FileHandler fh = new FileHandler();

    String[] vars = windowList.toArray(new String[0]);
    boolean[] willOperate = {true};
    String[] algNames = {"SAND"};
    String[] metricNames = {"precision", "recall"};

    final int VARSIZE = vars.length;
    final int ALGNUM = algNames.length;
    final int METRICNUM = 2; // precision, recall

    long[][] algtime = new long[ALGNUM][2];
    long[][] totaltime = new long[VARSIZE][ALGNUM];
    double[][][] metrics = new double[VARSIZE][ALGNUM][METRICNUM];

    TimeSeries timeseries = null;
    int algIndex = 0;

    SAND sand = null;

    double alpha = 0;
    POS_BIAS bias = POS_BIAS.FLAT;
    ArrayList<Range> predictAnomaly = null;

    for (int index = 0; index < VARSIZE; ++index) {
      String rawPath = String.format("%s/%s", dir, filePrefix);
      System.out.println("test with window size " + vars[index] + " on " + rawPath + " begin");
      Map<Integer, ArrayList<Range>> realAnomalyMap = new HashMap<>();
      Map<Integer, TimeSeries> seriesMap = new HashMap<>();

      // SAND
      algIndex = 0;
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
          sand = new SAND();
          Map<String, Object> sandParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
          sandParams.put("batch_size", Integer.parseInt(vars[index]));
          sand.init(sandParams, timeseries);
          sand.run();
          algtime[algIndex][1] = System.currentTimeMillis();
          totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
          DataHandler.evaluate(
              alpha, bias, predictAnomaly, realAnomalyMap.get(seed), metrics[index][algIndex]);
        }
      }
      // write results
      fh.writeResults(
          "window", resultPrefix, vars, algNames, metricNames, totaltime, metrics, seeds.size());
    } // end of rIndex
  }
}