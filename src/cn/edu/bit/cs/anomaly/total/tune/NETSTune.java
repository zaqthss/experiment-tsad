package cn.edu.bit.cs.anomaly.total.tune;

import cn.edu.bit.cs.anomaly.MultiDimAlgorithm;
import cn.edu.bit.cs.anomaly.NETS;
import cn.edu.bit.cs.anomaly.entity.Range;
import cn.edu.bit.cs.anomaly.entity.TimePointMulDim;
import cn.edu.bit.cs.anomaly.entity.TimeSeriesMulDim;
import cn.edu.bit.cs.anomaly.total.MetaData;
import cn.edu.bit.cs.anomaly.total.PointMetaData;
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

public class NETSTune {
    private final static Logger LOGGER = Logger.getLogger(NETSTune.class.getName());

    public static void main(String[] args) {
        Locale.setDefault(new Locale("en", "EN"));
        ArgumentParser parser = ArgumentParsers.newFor("nets_tune").build().
                description("input parameters needs to tune");
        parser.addArgument("-sub").dest("subDim").type(Integer.class).setDefault(1);
        parser.addArgument("-r").dest("R").type(Double.class).setDefault(115d);
        parser.addArgument("-k").dest("K").type(Integer.class).setDefault(50);
        parser.addArgument("-s").dest("S").type(Integer.class).setDefault(500);
        parser.addArgument("-w").dest("W").type(Integer.class).setDefault(10000);
        parser.addArgument("-nW").dest("nW").type(Integer.class).setDefault(10000);
        parser.addArgument("-rsd").dest("randSubDim").type(Integer.class).setDefault(0);
        parser.addArgument("ds").dest("ds").type(String.class);
        parser.addArgument("dump_dir").dest("dumpDir").type(String.class);
        parser.addArgument("anomaly_type").dest("anomalyType").type(String.class).choices("point", "sub");

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
            int dim = (int) dsMap.get("dim");
            int subDim = res.getInt("subDim");
            double R = res.getDouble("R");
            int K = res.getInt("K");
            int S = res.getInt("S");
            int W = res.getInt("W");
            int nW = res.getInt("nW");
            int randSubDim = res.getInt("randSubDim");

            File dumpF = new File(dumpDir);
            if (!dumpF.exists()) {
                dumpF.mkdirs();
            }

            // check result file
            String[] resultToks = {filePrefix, "exp", "nets", "subDim", String.valueOf(subDim),
                    "S", String.valueOf(S), "R", String.valueOf(R), "K", String.valueOf(K),
                    "W", String.valueOf(W), "nW", String.valueOf(nW), "randSubDim", String.valueOf(randSubDim)};
            String resultPrefix = String.join("_", resultToks);
            String resultPath = String.format("%s/%s_%s.csv",
                    res.getString("dumpDir"), resultPrefix, anomalyType);
            PrintWriter pw = null;
            if (new File(resultPath).exists()) {
                System.out.printf("result %s exists already, skipping", resultPath);
                return;
            }

            // read file
            String rawPath = String.format("%s/%s.csv", dir, filePrefix);
            LOGGER.info("loading " + rawPath);
            FileHandler fh = new FileHandler();
            TimeSeriesMulDim ts = fh.readMulDataWithLabel(rawPath);

            // run algorithm
            LOGGER.info("run NETS");
            Map<String, Object> params = new HashMap<>();
            params.put("dim", dim);
            params.put("subDim", subDim);
            params.put("R", R);
            params.put("K", K);
            params.put("S", S);
            params.put("W", W);
            params.put("nW", nW);
            params.put("randSubDim", randSubDim);

            MultiDimAlgorithm alg = new NETS();
            alg.init(params, ts);
            alg.run();

            // write result
            LOGGER.info("dumping into " + resultPath);
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
            LOGGER.info("done");

        } catch (ArgumentParserException e) {
            parser.handleError(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
