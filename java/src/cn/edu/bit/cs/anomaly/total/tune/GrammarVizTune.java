package cn.edu.bit.cs.anomaly.total.tune;

import cn.edu.bit.cs.anomaly.GrammarViz;
import cn.edu.bit.cs.anomaly.SAND;
import cn.edu.bit.cs.anomaly.entity.Range;
import cn.edu.bit.cs.anomaly.entity.TimePoint;
import cn.edu.bit.cs.anomaly.entity.TimeSeries;
import cn.edu.bit.cs.anomaly.total.MetaData;
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

public class GrammarVizTune {

  private final static Logger LOGGER = Logger.getLogger(GrammarVizTune.class.getName());

  public static void main(String[] args) {
    Locale.setDefault(new Locale("en", "EN"));
    ArgumentParser parser = ArgumentParsers.newFor("sand_tune").build().
        description("input parameters needs to tune");
    parser.addArgument("-w").dest("SAX_WINDOW_SIZE").type(Integer.class);
    parser.addArgument("-p").dest("SAX_PAA_SIZE").type(Integer.class);
    parser.addArgument("-a").dest("SAX_ALPHABET_SIZE").type(Integer.class);
    parser.addArgument("-k").dest("DISCORDS_NUM").type(Integer.class);
    parser.addArgument("-n").dest("SAX_NORM_THRESHOLD").type(Double.class);
    parser.addArgument("ds").dest("ds").type(String.class);
    parser.addArgument("dump_dir").dest("dumpDir").type(String.class);
    parser.addArgument("anomaly_type").dest("anomalyType").type(String.class);

    Namespace res;
    try {
      // parse args and get parameter
      res = parser.parseArgs(args);
      String dsName = res.getString("ds");
      MetaData meta = SubMetaData.getInstance();
      Map<String, Object> dsMap = meta.getDataset().get(dsName);
      System.out.println(dsName);
      System.out.println(dsMap);
      String dir = (String) dsMap.get("dataDir");
      String filePrefix = (String) dsMap.get("rawPrefix");
      String dumpDir = res.getString("dumpDir");
      String anomalyType = res.getString("anomalyType");
      int SAX_WINDOW_SIZE = res.getInt("SAX_WINDOW_SIZE");
      int SAX_PAA_SIZE = res.getInt("SAX_PAA_SIZE");
      int SAX_ALPHABET_SIZE = res.getInt("SAX_ALPHABET_SIZE");
      int DISCORDS_NUM = res.getInt("DISCORDS_NUM");
      Double SAX_NORM_THRESHOLD = res.getDouble("SAX_NORM_THRESHOLD");
      File dumpF = new File(dumpDir);
      if (!dumpF.exists()) {
        dumpF.mkdirs();
      }

      // check result file
      String[] resultToks = {filePrefix, "exp", "GrammarViz", "SAX_WINDOW_SIZE",
          String.valueOf(SAX_WINDOW_SIZE),
          "SAX_PAA_SIZE", String.valueOf(SAX_PAA_SIZE), "SAX_ALPHABET_SIZE",
          String.valueOf(SAX_ALPHABET_SIZE), "DISCORDS_NUM", String.valueOf(DISCORDS_NUM),
          "SAX_NORM_THRESHOLD", String.valueOf(SAX_NORM_THRESHOLD)};
      String resultPrefix = String.join("_", resultToks);
      String resultPath = String.format("%s/%s_%s.csv",
          res.getString("dumpDir"), resultPrefix, anomalyType);
      PrintWriter pw = null;
      if (new File(resultPath).exists()) {
        System.out.printf("result %s exists already, skipping", resultPath);
        return;
      }

      // read file
      String rawPath = String.format("%s/valN/%s.csv", dir, filePrefix);
      System.out.println("loading " + rawPath);
      FileHandler fh = new FileHandler();
      TimeSeries ts = fh.readDataWithLabel(rawPath);

      // run algorithm
      System.out.println(
          "run GrammarViz" + " " + SAX_WINDOW_SIZE + " " + SAX_PAA_SIZE + " " + SAX_ALPHABET_SIZE
              + " " + DISCORDS_NUM + " " + SAX_NORM_THRESHOLD);
      Map<String, Object> params = new HashMap<>();
      params.put("SAX_WINDOW_SIZE", SAX_WINDOW_SIZE);
      params.put("SAX_PAA_SIZE", SAX_PAA_SIZE);
      params.put("SAX_ALPHABET_SIZE", SAX_ALPHABET_SIZE);
      params.put("DISCORDS_NUM", DISCORDS_NUM);
      params.put("SAX_NORM_THRESHOLD", SAX_NORM_THRESHOLD);
      GrammarViz sand = new GrammarViz();
      sand.init(params, ts);
      sand.run();

      // write result
      //System.out.println("dumping into " + resultPath);
      pw = new PrintWriter(new FileWriter(resultPath));
      if (anomalyType.equals("point")) {
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
      System.out.println("done");
    } catch (ArgumentParserException e) {
      parser.handleError(e);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
