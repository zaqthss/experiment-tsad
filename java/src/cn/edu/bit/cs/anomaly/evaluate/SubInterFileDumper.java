 package cn.edu.bit.cs.anomaly.evaluate;

import cn.edu.bit.cs.anomaly.total.MetaData;
import cn.edu.bit.cs.anomaly.total.PointMetaData;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import cn.edu.bit.cs.anomaly.entity.Range;
import cn.edu.bit.cs.anomaly.entity.TimePointMulDim;
import cn.edu.bit.cs.anomaly.entity.TimeSeriesMulDim;
import cn.edu.bit.cs.anomaly.total.SubMetaData;
import cn.edu.bit.cs.anomaly.util.DataHandler;
import cn.edu.bit.cs.anomaly.util.FileHandler;
import cn.edu.bit.cs.anomaly.util.Constants.POS_BIAS;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class SubInterFileDumper {
	public int pMetric = 7;
    public String[] pMetricNames = {"precision", "recall", "fmeasure", "accuracy",
            "errorRate", "sensitive", "specificity"};
    public int rMetric = 3;
    public String[] rMetricNames = {"precision", "recall","fmeasure"};
    
	public static void main(String[] args) throws ArgumentParserException, IOException {
		ArgumentParser parser = ArgumentParsers.newFor("FileDumper").build().
	            description("input the dir to dump");
	        parser.addArgument("dump_dir").dest("dumpDir").type(String.class);
	        Namespace res = parser.parseArgs(args);
	        SubInterFileDumper ifd = new SubInterFileDumper();
	        String dumpDir = res.getString("dumpDir");
	        double alpha = 0.;
	        POS_BIAS bias = POS_BIAS.FLAT;
	        ifd.dumpTuneResults(dumpDir, alpha, bias);
	}
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
        SubMetaData meta = SubMetaData.getInstance();
        // for each cluster of dataset
        // 0. form alg cluster; 1. extract parameters; 2. compute metrics; 3. combine results;
        // 4. write into file
        for (String rawName : fileMap.keySet()) {
            String dsName = meta.getRawToName().get(rawName);
            Map<String, Object> dsMap = meta.getDataset().get(dsName);
            String dir = (String) dsMap.get("dataDir");
            String filePrefix = (String) dsMap.get("rawPrefix");
            assert (filePrefix.equals(rawName));
            String rawPath = String.format("%s/test/%s.csv", dir, filePrefix);
            TimeSeriesMulDim ts = fh.readMulDataWithLabel(rawPath);
            seriesMap.put(rawName, ts);
            realAnomalyMapSub .put(rawName, DataHandler.findAnomalyRange(ts));
            dsType = "sub";
            // for each algorithm, alg -> results
            Map<String, List<String>> algResults = new HashMap<>();
            List<File> fileList = fileMap.get(rawName);
            for (File file : fileList) {
                String fileName = file.getName();
                String anomalyType = getAnoamlyType(fileName);
                assert anomalyType.equals(dsType);
                String algName = getAlgName(fileName);
                String params = extractParams(fileName);
                ArrayList<Range> predictAnomaly = fh.getPredictAnomalyFromFile(file);

                List<String> resList = null;
                if (!algResults.containsKey(algName)) {
                    List<String> headList = new ArrayList<>();
                    /*for (int i = 0; i < params.length; i = i + 2) {
                        headList.add(params[i]);
                    }*/
                    if(params!=null) {
                    	headList.add("dim");
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
                if(params!=null) {
                	paramList.add(params);
                }
                /*for (int i = 1; i < params.length; i = i + 2) {
                    paramList.add(params[i]);
                }*/
                
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
                    paramList.add(String.format("%.3f", rm.fmeasure));
                }
                resList.add(String.join(",", paramList));
            } // end of files
            // write this ds and all the algs
            for (String algName : algResults.keySet()) {
                String outPath = "result/um/" + algName + "_" + rawName + "_" + dsType + "_param.csv";
                PrintWriter pw = new PrintWriter(new FileWriter(outPath));
                for (String str : algResults.get(algName)) {
                    pw.println(str);
                }
                pw.close();
            }
        } // end of rawName
    }
	public String getDataName(String filename) {
        int pos = filename.lastIndexOf("sub");
		String p=filename.substring(0, pos - 1);
		String[] p1=filename.split("_");
		int pos2=p.indexOf(p1[0])+p1[0].length()+1;
		int pos3=p.indexOf("dim")-1;
		if(pos3==-2) {
			pos3=p.length();
		}
        return p.substring(pos2,pos3);
    }

    public String getAnoamlyType(String filename) {
        int startPos = filename.lastIndexOf("_");
        int endPos = filename.lastIndexOf(".");
        return filename.substring(startPos + 1, endPos);
    }

    public String getAlgName(String filename) {
    	int pos = filename.lastIndexOf("sub");
		String p=filename.substring(0, pos - 1);
		String[] p1=filename.split("_");
		return p1[0];
    }

    public String extractParams(String filename) {
        // NP_Exathlon_dim_13_sub ->
        // mul, 16, slide, 4, R, 1.0, K, 1
        int pos=filename.indexOf("dim");
        if(pos==-1) {
        	return null;
        }else {
        	int pos1=filename.lastIndexOf("sub");
        	String p=filename.substring(pos+4,pos1-1);
        	return p;
    		
        }
    }

    public TreeSet<Long> transRangeToPoint(ArrayList<Range> anomaly) {
        TreeSet<Long> anomalyPoints = new TreeSet<>();
        for (Range range : anomaly) {
            anomalyPoints.addAll(range);
        }
        return anomalyPoints;
    }

}
