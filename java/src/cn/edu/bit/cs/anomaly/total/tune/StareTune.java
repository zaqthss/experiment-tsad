package cn.edu.bit.cs.anomaly.total.tune;

import cn.edu.bit.cs.anomaly.MultiDimAlgorithm;
import cn.edu.bit.cs.anomaly.Stare;
import cn.edu.bit.cs.anomaly.entity.Range;
import cn.edu.bit.cs.anomaly.entity.TimePointMulDim;
import cn.edu.bit.cs.anomaly.entity.TimeSeriesMulDim;
import cn.edu.bit.cs.anomaly.total.MetaData;
import cn.edu.bit.cs.anomaly.total.PointMetaData;
import cn.edu.bit.cs.anomaly.total.SubMetaData;
import cn.edu.bit.cs.anomaly.util.DataHandler;
import cn.edu.bit.cs.anomaly.util.FileHandler;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class StareTune {

  private final static Logger LOGGER = Logger.getLogger(StareTune.class.getName());

  public static void main(String[] args) {
    Locale.setDefault(new Locale("en", "EN"));
    ArgumentParser parser = ArgumentParsers.newFor("nets_tune").build().
        description("input parameters needs to tune");
    parser.addArgument("-w").dest("W").type(Integer.class).setDefault(1415);
    parser.addArgument("-s").dest("S").type(Integer.class).setDefault(71);
    parser.addArgument("-r").dest("R").type(Double.class).setDefault(60d);
    parser.addArgument("-k").dest("K").type(Integer.class).setDefault(140);
    parser.addArgument("-nW").dest("nW").type(Integer.class).setDefault(10000);
    parser.addArgument("-ra").dest("rate").type(Double.class).setDefault(0.1);
    parser.addArgument("-sT").dest("skipThred").type(Double.class).setDefault(0.1d);
    parser.addArgument("ds").dest("ds").type(String.class);
    parser.addArgument("dump_dir").dest("dumpDir").type(String.class);
    parser.addArgument("anomaly_type").dest("anomalyType").type(String.class)
        .choices("point", "sub");

    Namespace res;
    try {
      // parse args and get parameter
      res = parser.parseArgs(args);
      String dsName = res.getString("ds");
      MetaData meta = PointMetaData.getInstance();
      Map<String, Object> dsMap = meta.getDataset().get(dsName);
      String dir = (String) dsMap.get("dataDir");
      String filePrefix = (String) dsMap.get("rawPrefix");
      String dumpDir = res.getString("dumpDir");
      String anomalyType = res.getString("anomalyType");

      //Alg parameter
      double R = res.getDouble("R");
      int K = res.getInt("K");
      int S = res.getInt("S");
      int W = res.getInt("W");
      int nW = res.getInt("nW");
      double rate = res.getDouble("rate");
      double skipThred = res.getDouble("skipThred");

      File dumpF = new File(dumpDir);
      if (!dumpF.exists()) {
        dumpF.mkdirs();
      }

      // check result file
      String[] resultToks = {filePrefix, "exp", "stare",
          "S", String.valueOf(S), "R", String.valueOf(R), "K", String.valueOf(K),
          "W", String.valueOf(W), "nW", String.valueOf(nW), "rate", String.valueOf(rate), "skipThred",
          String.valueOf(skipThred)};
      String resultPrefix = String.join("_", resultToks);
      String resultPath = String.format("%s/%s_%s.csv",
          res.getString("dumpDir"), resultPrefix, anomalyType);
      PrintWriter pw = null;
      if (new File(resultPath).exists()) {
        System.out.printf("result %s exists already, skipping", resultPath);
        return;
      }

      // read file
      String rawPath = String.format("%s/%s/%s.csv", dir,"test", filePrefix);
      System.out.println("loading " + rawPath);
      FileHandler fh = new FileHandler();
      TimeSeriesMulDim ts = fh.readMulDataWithLabel(rawPath);

      // run algorithm
      System.out.println("run Stare");
      Map<String, Object> params = new HashMap<>();

      params.put("R", R);
      params.put("K", K);
      params.put("S", S);
      params.put("W", W);
      params.put("rate", rate);
      params.put("nW", nW);
      params.put("skipThred", skipThred);

      MultiDimAlgorithm alg = new Stare();
      alg.init(params, ts);
      alg.run();

      // write result
      System.out.println("dumping into " + resultPath);
      pw = new PrintWriter(new FileWriter(resultPath));
      if (meta.getSets().contains(dsName) || anomalyType.equals("point")) {
        TreeMap<Long, TimePointMulDim> predictAnomaly = DataHandler.findAnomalyPoint(ts);
        for (Long timestamp : predictAnomaly.keySet()) {
          pw.println(timestamp);
        }
      } else {
        ArrayList<Range> predictAnomaly = DataHandler.findAnomalyRange(ts);
        for (Range range : predictAnomaly) {
          List<String> tsList = range.stream().map(Object::toString).collect(Collectors.toList());
          pw.println(String.join(",", tsList));
        }
      }
      pw.close();
      System.out.println("done");

    } catch (ArgumentParserException e) {
      parser.handleError(e);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
