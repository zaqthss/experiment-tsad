package cn.edu.bit.cs.anomaly.total.tune;

import cn.edu.bit.cs.anomaly.total.MetaData;
import cn.edu.bit.cs.anomaly.total.PointMetaData;
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

import cn.edu.bit.cs.anomaly.LRRDS;
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

public class LRRDSTune {
	private final static Logger LOGGER = Logger.getLogger(PBADTune.class.getName());
	public static void main(String[] args) {
		Locale.setDefault(new Locale("en", "EN"));
	    ArgumentParser parser = ArgumentParsers.newFor("lrrds_tune").build().
	        description("input parameters needs to tune");
	    parser.addArgument("-c").dest("compressed_rate").type(Double.class);
	    parser.addArgument("-s").dest("slack").type(Integer.class);
	    parser.addArgument("-m").dest("sub_minlength").type(Integer.class);
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
	        double compressed_rate=res.getDouble("compressed_rate");
	        int slack=res.getInt("slack");
	        int sub_minlength=res.getInt("sub_minlength");
	        File dumpF = new File(dumpDir);
	        if (!dumpF.exists()) {
	          dumpF.mkdirs();
	        }
	        
	     // check result file
	        String[] resultToks = {filePrefix, "exp", "lrrds","compressed_rate", String.valueOf(compressed_rate), 
	        		"slack", String.valueOf(slack), "sub_minlength",String.valueOf(sub_minlength)};
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
	        LOGGER.info("run lrrds: "+compressed_rate+" "+slack+" "+sub_minlength+" ");
	        Map<String,Object> params = new HashMap<>();
	        params.put("compressed_rate", compressed_rate);
	        params.put("slack", slack);
	        params.put("sub_minlength", sub_minlength);
	        LRRDS lrrds = new LRRDS();
	        lrrds.init(params, ts);
	        lrrds.run();
	        
	        // write result
	        //LOGGER.info("dumping into " + resultPath);
	        pw = new PrintWriter(new FileWriter(resultPath));
	        if (meta.getSets().contains(dsName) || anomalyType.equals("point")) {
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
	        LOGGER.info("done");
	    }catch (ArgumentParserException e) {
	        parser.handleError(e);
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	}
	

}
