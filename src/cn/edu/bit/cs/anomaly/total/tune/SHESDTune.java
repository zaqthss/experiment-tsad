package cn.edu.bit.cs.anomaly.total.tune;

import cn.edu.bit.cs.anomaly.SHESD;
import cn.edu.bit.cs.anomaly.SingleDimAlgorithm;
import cn.edu.bit.cs.anomaly.entity.Range;
import cn.edu.bit.cs.anomaly.entity.TimePoint;
import cn.edu.bit.cs.anomaly.entity.TimeSeries;
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

public class SHESDTune {

  private final static Logger LOGGER = Logger.getLogger(SHESDTune.class.getName());

  public static void main(String[] args) {
    Locale.setDefault(new Locale("en", "EN"));
    ArgumentParser parser = ArgumentParsers.newFor("shesd_tune").build().
        description("input parameters needs to tune");
    parser.addArgument("-s").dest("seasonality").type(Integer.class).setDefault(158);
    parser.addArgument("-m").dest("maxAnoms").type(Double.class).setDefault(0.1);
    parser.addArgument("-a").dest("alpha").type(Double.class).setDefault(0.05);
    parser.addArgument("-t").dest("anomsThreshold").type(Double.class).setDefault(1.05);
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
      int seasonality = res.getInt("seasonality");
      double maxAnoms = res.getDouble("maxAnoms");
      double alpha = res.getDouble("alpha");
      double anomsThreshold = res.getDouble("anomsThreshold");

      File dumpF = new File(dumpDir);
      if (!dumpF.exists()) {
        dumpF.mkdirs();
      }

      // check result file
      String[] resultToks = {filePrefix, "exp", "shesd",
          "seasonality", String.valueOf(seasonality), "maxAnoms", String.valueOf(maxAnoms), "alpha",
          String.valueOf(alpha), "anomsThreshold", String.valueOf(anomsThreshold)};
      String resultPrefix = String.join("_", resultToks);
      String resultPath = String.format("%s/%s_%s.csv",
          res.getString("dumpDir"), resultPrefix, anomalyType);
      PrintWriter pw = null;
     /* if (new File(resultPath).exists()) {
        System.out.printf("result %s exists already, skipping", resultPath);
        return;
      }*/

      // read file
      String rawPath = String.format("%s/%s.csv", dir, filePrefix);
      LOGGER.info("loading " + rawPath);
      FileHandler fh = new FileHandler();
      TimeSeries ts = fh.readDataWithLabel(rawPath);

      // run algorithm
      LOGGER.info("run SHESD");
      Map<String, Object> params = new HashMap<>();

      params.put("seasonality", seasonality);
      params.put("maxAnoms", maxAnoms);
      params.put("alpha", alpha);
      params.put("anomsThreshold", anomsThreshold);

      SingleDimAlgorithm alg = new SHESD();
      alg.init(params, ts);
      alg.run();

      // write result
      pw = new PrintWriter(new FileWriter(resultPath));
      if (meta.getSets().contains(dsName) || anomalyType.equals("sub")) {
        TreeMap<Long, TimePoint> predictAnomaly = DataHandler.findAnomalyPoint(ts);
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
      LOGGER.info("done");

    } catch (ArgumentParserException e) {
      parser.handleError(e);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
