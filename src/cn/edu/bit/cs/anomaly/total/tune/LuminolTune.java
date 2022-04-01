package cn.edu.bit.cs.anomaly.total.tune;

import cn.edu.bit.cs.anomaly.Luminol;
import cn.edu.bit.cs.anomaly.SingleDimAlgorithm;
import cn.edu.bit.cs.anomaly.entity.Range;
import cn.edu.bit.cs.anomaly.entity.TimePoint;
import cn.edu.bit.cs.anomaly.entity.TimeSeries;
import cn.edu.bit.cs.anomaly.total.MetaData;
import cn.edu.bit.cs.anomaly.total.PointMetaData;
import cn.edu.bit.cs.anomaly.total.SubMetaData;
import cn.edu.bit.cs.anomaly.util.Constants;
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

public class LuminolTune {

  private final static Logger LOGGER = Logger.getLogger(LuminolTune.class.getName());

  public static void main(String[] args) {
    Locale.setDefault(new Locale("en", "EN"));
    ArgumentParser parser = ArgumentParsers.newFor("shesd_tune").build().
        description("input parameters needs to tune");
    parser.addArgument("-p").dest("precision").type(Integer.class).setDefault(Constants.Luminol.DEFAULT_BITMAP_PRECISION);
    parser.addArgument("-c").dest("chunk-size").type(Integer.class).setDefault(Constants.Luminol.DEFAULT_BITMAP_CHUNK_SIZE);
    parser.addArgument("-f").dest("future-window-size").type(Integer.class).setDefault(Constants.Luminol.DEFAULT_BITMAP_LEADING_WINDOW_SIZE);
    parser.addArgument("-l").dest("lag-window-size").type(Integer.class).setDefault(Constants.Luminol.DEFAULT_BITMAP_LAGGING_WINDOW_SIZE);
    parser.addArgument("-t").dest("anomsThreshold").type(Double.class).setDefault(100d);
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
      int precision = res.getInt("precision");
      int chunk_size = res.getInt("chunk-size");
      int future_window_size = res.getInt("future-window-size");
      int lag_window_size = res.getInt("lag-window-size");
      double threshold = res.getDouble("anomsThreshold");

      File dumpF = new File(dumpDir);
      if (!dumpF.exists()) {
        dumpF.mkdirs();
      }

      // check result file
      String[] resultToks = {filePrefix, "exp", "Luminol",
          "precision", String.valueOf(precision), "chunk-size", String.valueOf(chunk_size),
          "future-window-size",
          String.valueOf(future_window_size), "lag-window-size", String.valueOf(lag_window_size),
          "threshold", String.valueOf(threshold)};
      String resultPrefix = String.join("_", resultToks);
      String resultPath = String.format("%s/%s_%s.csv",
          res.getString("dumpDir"), resultPrefix, anomalyType);
      PrintWriter pw = null;
      //if (new File(resultPath).exists()) {
       // System.out.printf("result %s exists already, skipping", resultPath);
        //return;
      //}

      // read file
      String rawPath = String.format("%s/%s.csv", dir, filePrefix);
      LOGGER.info("loading " + rawPath);
      FileHandler fh = new FileHandler();
      TimeSeries ts = fh.readDataWithLabel(rawPath);

      // run algorithm
      LOGGER.info("run Luminol");
      Map<String, Object> params = new HashMap<>();

      params.put("precision", precision);
      params.put("chunk_size", chunk_size);
      params.put("lag_window_size", lag_window_size);
      params.put("future_window_size",future_window_size);
      params.put("threshold", threshold);
      SingleDimAlgorithm alg = new Luminol();
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
