package cn.edu.bit.cs.anomaly.total.tune;

import cn.edu.bit.cs.anomaly.total.MetaData;
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

import cn.edu.bit.cs.anomaly.PBAD;
import cn.edu.bit.cs.anomaly.entity.Range;
import cn.edu.bit.cs.anomaly.entity.TimePointMulDim;
import cn.edu.bit.cs.anomaly.entity.TimeSeriesMulDim;
import cn.edu.bit.cs.anomaly.total.SubMetaData;
import cn.edu.bit.cs.anomaly.util.DataHandler;
import cn.edu.bit.cs.anomaly.util.FileHandler;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class PBADTune {
	private final static Logger LOGGER = Logger.getLogger(PBADTune.class.getName());
	public static void main(String[] args) {
		Locale.setDefault(new Locale("en", "EN"));
	    ArgumentParser parser = ArgumentParsers.newFor("pbad_tune").build().
	        description("input parameters needs to tune");
	    parser.addArgument("-s").dest("window_size").type(Integer.class);
	    parser.addArgument("-i").dest("window_incr").type(Integer.class);
	    parser.addArgument("-b").dest("bin_size").type(Integer.class);
	    parser.addArgument("-m").dest("max_feature").type(Integer.class);
	    parser.addArgument("-t").dest("threshold").type(Double.class);
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
	        String dir = (String)dsMap.get("dataDir");
	        String filePrefix = (String)dsMap.get("rawPrefix");
	        String dumpDir = res.getString("dumpDir");
	        String anomalyType = res.getString("anomalyType");
	        int window_size=res.getInt("window_size");
	        int window_incr=res.getInt("window_incr");
	        int bin_size=res.getInt("bin_size");
	        int max_feature=res.getInt("max_feature");
	        double threshold=res.getDouble("threshold");
	        File dumpF = new File(dumpDir);
	        if (!dumpF.exists()) {
	          dumpF.mkdirs();
	        }
	        
	     // check result file
	        String[] resultToks = {filePrefix, "exp", "pbad","window_size", String.valueOf(window_size), 
	        		"window_incr", String.valueOf(window_incr), "bin_size",String.valueOf(bin_size),"max_feature",String.valueOf(max_feature),"threshold", String.valueOf(threshold)};
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
	        TimeSeriesMulDim ts = fh.readMulDataWithLabel(rawPath);
	        
	     // run algorithm
	        System.out.println("run pbad: "+window_size+" "+window_incr+" "+bin_size+" "+max_feature+" "+threshold);
	        Map<String,Object> params = new HashMap<>();
	        params.put("window_size", window_size);
	        params.put("window_incr", window_incr);
	        params.put("bin_size", bin_size);
	        params.put("max_feature",max_feature);
	        params.put("threshold", threshold);
	        PBAD pbad = new PBAD();
	        pbad.init(params, ts);
	        pbad.run();
	        
	        // write result
	        //System.out.println("dumping into " + resultPath);
	        pw = new PrintWriter(new FileWriter(resultPath));
	        if (anomalyType.equals("point")) {
	          TreeMap<Long, TimePointMulDim> predictAnomaly = DataHandler.findAnomalyPoint(ts);
	          for (Long timestamp: predictAnomaly.keySet()) {
	            pw.println(timestamp);
	          }
	        } else {
	          ArrayList<Range> predictAnomaly = DataHandler.findAnomalyRange(ts);
	          for (Range range: predictAnomaly) {
	            List<String> tsList = range.stream().map(Object::toString).collect(Collectors.toList());
	            pw.println(String.join(",", tsList));
	          }
	        }
	        pw.close();
	        System.out.println("done");
	    }catch (ArgumentParserException e) {
	        parser.handleError(e);
	    } catch (IOException e) {
	      e.printStackTrace();
	    }

	}

}
