package cn.edu.bit.cs.anomaly.total.tune;

import cn.edu.bit.cs.anomaly.IDK;
import cn.edu.bit.cs.anomaly.Merlin;
import cn.edu.bit.cs.anomaly.entity.Range;
import cn.edu.bit.cs.anomaly.entity.TimePoint;
import cn.edu.bit.cs.anomaly.entity.TimeSeries;
import cn.edu.bit.cs.anomaly.total.MetaData;
import cn.edu.bit.cs.anomaly.total.SubMetaData;
import cn.edu.bit.cs.anomaly.util.DataHandler;
import cn.edu.bit.cs.anomaly.util.FileHandler;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class IDKTune {
    private final static Logger LOGGER = Logger.getLogger(IDKTune.class.getName());

    public static void main(String[] args) {
        Locale.setDefault(new Locale("en", "EN"));
        ArgumentParser parser = ArgumentParsers.newFor("merlin_tune").build().
                description("input parameters needs to tune");
        parser.addArgument("-n").dest("sample_num").type(Integer.class).setDefault(5);
        parser.addArgument("-s").dest("sample_size1").type(Integer.class).setDefault(10);
        parser.addArgument("-i").dest("sample_size2").type(Integer.class).setDefault(1);
        parser.addArgument("-w").dest("window_size").type(Integer.class).setDefault(1);
        parser.addArgument("-t").dest("top_k").type(Integer.class).setDefault(1);
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
            String dir = (String) dsMap.get("dataDir");
            String filePrefix = (String) dsMap.get("rawPrefix");
            Boolean IsSyn = (Boolean) dsMap.get("syn");
            String dumpDir = res.getString("dumpDir");
            String anomalyType = res.getString("anomalyType");
            int sample_num = res.getInt("sample_num");
            int sample_size1 = res.getInt("sample_size1");
            int sample_size2 = res.getInt("sample_size2");
            int window_size = res.getInt("window_size");
            int K = res.getInt("top_k");
            File dumpF = new File(dumpDir);
            if (!dumpF.exists()) {
                dumpF.mkdirs();
            }

            // check result file
            String[] resultToks = {filePrefix, "exp", "idk", "t", String.valueOf(sample_num),
                    "psi1", String.valueOf(sample_size1), "psi2", String.valueOf(sample_size2),"w",
                    String.valueOf(window_size), "k", String.valueOf(K)};
            String resultPrefix = String.join("_", resultToks);
            String resultPath = String.format("%s/%s_%s.csv",
                    res.getString("dumpDir"), resultPrefix, anomalyType);
            PrintWriter pw = null;
            if (new File(resultPath).exists()) {
                System.out.printf("result %s exists already, skipping", resultPath);
                return;
            }

            // read file
            String rawPath=String.format("%s/valN/%s.csv", dir, filePrefix);
            System.out.println("loading " + rawPath);
            FileHandler fh = new FileHandler();
            TimeSeries ts = fh.readDataWithLabel(rawPath);

            // run algorithm
            System.out.println("run idk" + " " + sample_num + " " + sample_size1+" "+sample_size2+" "+window_size + " " + K);
            Map<String, Object> params = new HashMap<>();
            params.put("sample_num", sample_num);
            params.put("sample_size1", sample_size1);
            params.put("sample_size2", sample_size2);
            params.put("window_size", window_size);
            params.put("top_k", K);

            IDK idk = new IDK();
            idk.init(params, ts);
            idk.run();

            // write result
            System.out.println("dumping into " + resultPath);
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
