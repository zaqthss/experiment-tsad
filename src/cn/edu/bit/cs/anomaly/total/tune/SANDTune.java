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

import cn.edu.bit.cs.anomaly.SAND;
import cn.edu.bit.cs.anomaly.entity.Range;
import cn.edu.bit.cs.anomaly.entity.TimePoint;
import cn.edu.bit.cs.anomaly.entity.TimeSeries;
import cn.edu.bit.cs.anomaly.total.SubMetaData;
import cn.edu.bit.cs.anomaly.util.DataHandler;
import cn.edu.bit.cs.anomaly.util.FileHandler;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class SANDTune {
	private final static Logger LOGGER = Logger.getLogger(SANDTune.class.getName());
	
	public static void main(String[] args) {
		Locale.setDefault(new Locale("en", "EN"));
	    ArgumentParser parser = ArgumentParsers.newFor("sand_tune").build().
	        description("input parameters needs to tune");
	    parser.addArgument("-k").dest("k").type(Integer.class);
	    parser.addArgument("-b").dest("batch_size").type(Integer.class);
	    parser.addArgument("-p").dest("pattern_length").type(Integer.class);
	    parser.addArgument("-t").dest("top_k").type(Integer.class);
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
	        int k = res.getInt("k");
	        int batch_size=res.getInt("batch_size");
	        int pattern_length=res.getInt("pattern_length");
	        int top_k=res.getInt("top_k");
	        File dumpF = new File(dumpDir);
	        if (!dumpF.exists()) {
	          dumpF.mkdirs();
	        }
	        
	     // check result file
	        String[] resultToks = {filePrefix, "exp", "sand","k", String.valueOf(k), 
	        		"batch_size", String.valueOf(batch_size), "pattern_length", String.valueOf(pattern_length),"top_k", String.valueOf(top_k)};
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
	        TimeSeries ts = fh.readDataWithLabel(rawPath);
	        
	     // run algorithm
	        LOGGER.info("run sand"+" "+k+" "+batch_size+" "+pattern_length+" "+top_k);
	        Map<String,Object> params = new HashMap<>();
	        params.put("k", k);
	        params.put("batch_size", batch_size);
	        params.put("pattern_length", pattern_length);
	        params.put("top_k", top_k);
	        SAND sand = new SAND();
	        sand.init(params, ts);
	        sand.run();
	        
	        // write result
	        //LOGGER.info("dumping into " + resultPath);
	        pw = new PrintWriter(new FileWriter(resultPath));
	        if (meta.getSets().contains(dsName) || anomalyType.equals("point")) {
	          TreeMap<Long, TimePoint> predictAnomaly = DataHandler.findAnomalyPoint(ts);
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
	        LOGGER.info("done");
	    }catch (ArgumentParserException e) {
	        parser.handleError(e);
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	}

}
