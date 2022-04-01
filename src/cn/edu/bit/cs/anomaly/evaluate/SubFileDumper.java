package cn.edu.bit.cs.anomaly.evaluate;

import cn.edu.bit.cs.anomaly.entity.Range;
import cn.edu.bit.cs.anomaly.entity.TimePointMulDim;
import cn.edu.bit.cs.anomaly.entity.TimeSeriesMulDim;
import cn.edu.bit.cs.anomaly.total.MetaData;
import cn.edu.bit.cs.anomaly.total.PointMetaData;
import cn.edu.bit.cs.anomaly.total.SubMetaData;
import cn.edu.bit.cs.anomaly.util.Constants.POS_BIAS;
import cn.edu.bit.cs.anomaly.util.DataHandler;
import cn.edu.bit.cs.anomaly.util.FileHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class SubFileDumper {
    public int pMetric = 7;
    public String[] pMetricNames = {"precision", "recall", "fmeasure", "accuracy",
            "errorRate", "sensitive", "specificity"};
    public int rMetric = 2;
    public String[] rMetricNames = {"precision", "recall"};

    public void dumpTuneResults(String folderDir, double alpha, POS_BIAS bias) throws IOException {
        // cluster by filename
        Map<String, List<File>> fileMap = new HashMap<>();
        File folder = new File(folderDir);
        File[] files = folder.listFiles();
        for (File file : files) {
            String filename = file.getName();
            String dsName = getDataName(filename);
            List<File> fileList = fileMap.computeIfAbsent(dsName, s -> new ArrayList<>());
            fileList.add(file);
        }
        Map<String, TreeSet<Long>> realAnomalyMapPoint = new HashMap<>();
        Map<String, ArrayList<Range>> realAnomalyMapSub = new HashMap<>();
        Map<String, TimeSeriesMulDim> seriesMap = new HashMap<>();
        FileHandler fh = new FileHandler();
        String dsType = null;
        SingleMetric sm = new SingleMetric();
        RangeMetric rm = new RangeMetric();
        MetaData meta = SubMetaData.getInstance();
        // for each cluster of dataset
        // 0. form alg cluster; 1. extract parameters; 2. compute metrics; 3. combine results;
        // 4. write into file
        for (String rawName : fileMap.keySet()) {
            String dsName = meta.getRawToName().get(rawName);

            Map<String, Object> dsMap = meta.getDataset().get(dsName);
            String dir = (String) dsMap.get("dataDir");
            String filePrefix = (String) dsMap.get("rawPrefix");
            assert (filePrefix.equals(rawName));
            String rawPath = String.format("%s/%s.csv", dir, filePrefix);
            TimeSeriesMulDim ts = fh.readMulDataWithLabel(rawPath);
            seriesMap.put(rawName, ts);
            if (meta.getSets().contains(dsName)) {
                realAnomalyMapSub.put(rawName, DataHandler.findAnomalyRange(ts));
                dsType = "sub";
            }
            // for each algorithm, alg -> results
            Map<String, List<String>> algResults = new HashMap<>();
            List<File> fileList = fileMap.get(rawName);
            for (File file : fileList) {
                String fileName = file.getName();
                String anomalyType = getAnoamlyType(fileName);
                assert anomalyType.equals(dsType);
                String algName = getAlgName(fileName);
                String[] params = extractParams(fileName);
                ArrayList<Range> predictAnomaly = fh.getPredictAnomalyFromFile(file);

                List<String> resList = null;
                if (!algResults.containsKey(algName)) {
                    List<String> headList = new ArrayList<>();
                    for (int i = 0; i < params.length; i = i + 2) {
                        headList.add(params[i]);
                    }
                    if (anomalyType.equals("point")) {
                        headList.addAll(Arrays.asList(pMetricNames));
                    } else {
                        headList.addAll(Arrays.asList(rMetricNames));
                    }
                    resList = new ArrayList<>();
                    resList.add(String.join(",", headList));
                    algResults.put(algName, resList);
                } else {
                    resList = algResults.get(algName);
                }
                List<String> paramList = new ArrayList<>();
                for (int i = 1; i < params.length; i = i + 2) {
                    paramList.add(params[i]);
                }
                if (anomalyType.equals("point")) {
                    TreeSet<Long> preAnomaly = transRangeToPoint(predictAnomaly);
                    double[] metrics = sm.computeMetric(realAnomalyMapPoint.get(rawName), preAnomaly,
                            seriesMap.get(rawName).getTimeseriesMap().keySet());
                    for (double metric : metrics) {
                        paramList.add(String.format("%.3f", metric));
                    }
                } else {
                    rm.computeMetric(alpha, bias, realAnomalyMapSub.get(rawName), predictAnomaly);
                    paramList.add(String.format("%.3f", rm.precision));
                    paramList.add(String.format("%.3f", rm.recall));
                }
                resList.add(String.join(",", paramList));
            } // end of files
            // write this ds and all the algs
            for (String algName : algResults.keySet()) {
                String outPath = "result/tune/" + algName + "_" + rawName + "_" + dsType + "_param.csv";
                PrintWriter pw = new PrintWriter(new FileWriter(outPath));
                for (String str : algResults.get(algName)) {
                    pw.println(str);
                }
                pw.close();
            }
        } // end of rawName
    }

    public String getDataName(String filename) {
        int pos = filename.indexOf("exp");
        return filename.substring(0, pos - 1);
    }

    public String getAnoamlyType(String filename) {
        int startPos = filename.lastIndexOf("_");
        int endPos = filename.lastIndexOf(".");
        return filename.substring(startPos + 1, endPos);
    }

    public String getAlgName(String filename) {
        int startPos = filename.indexOf("exp");
        int endPos = filename.indexOf("_", startPos + "exp".length() + 1);
        return filename.substring(startPos + "exp".length() + 1, endPos);
    }

    public String[] extractParams(String filename) {
        // stock_pointc_10000_0.1_1_exp_cpod_mul_16_slide_4_R_1.0_K_1_point ->
        // mul, 16, slide, 4, R, 1.0, K, 1
        int expPos = filename.indexOf("exp");
        int startPos = filename.indexOf("_", expPos + "exp".length() + 1);
        int endPos = filename.lastIndexOf("_");
        String params = filename.substring(startPos + 1, endPos);
        return (params.split("_"));
    }

    public TreeSet<Long> transRangeToPoint(ArrayList<Range> anomaly) {
        TreeSet<Long> anomalyPoints = new TreeSet<>();
        for (Range range : anomaly) {
            anomalyPoints.addAll(range);
        }
        return anomalyPoints;
    }

    public static void main(String[] args) throws IOException, ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("FileDumper").build().
            description("input the dir to dump");
        parser.addArgument("dump_dir").dest("dumpDir").type(String.class);
        Namespace res = parser.parseArgs(args);
        SubFileDumper fd = new SubFileDumper();
        String dumpDir = res.getString("dumpDir");
        double alpha = 0.;
        POS_BIAS bias = POS_BIAS.FLAT;
        fd.dumpTuneResults(dumpDir, alpha, bias);
    }
}
