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

import cn.edu.bit.cs.anomaly.NeighborProfile;
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

public class NPTune {
	private final static Logger LOGGER = Logger.getLogger(PBADTune.class.getName());
	public static void main(String[] args) {
		Locale.setDefault(new Locale("en", "EN"));
	    ArgumentParser parser = ArgumentParsers.newFor("np_tune").build().
	        description("input parameters needs to tune");
	    parser.addArgument("-b").dest("batchsize").type(Integer.class);
	    parser.addArgument("-m").dest("max_sample").type(Integer.class);
	    parser.addArgument("-s").dest("sub_len").type(Integer.class);
	    parser.addArgument("-c").dest("scale").type(String.class);
	    parser.addArgument("-k").dest("top_k").type(Integer.class);
	    parser.addArgument("ds").dest("ds").type(String.class);
	    parser.addArgument("dump_dir").dest("dumpDir").type(String.class);
	    parser.addArgument("anomaly_type").dest("anomalyType").type(String.class);
	    
	    Namespace res;
	    try { 
	    	// parse args and get parameter
					MetaData meta = SubMetaData.getInstance();
	        res = parser.parseArgs(args);
	        String dsName = res.getString("ds");
	        Map<String, Object> dsMap = meta.getDataset().get(dsName);
	        String dir = (String)dsMap.get("dataDir");
	        String filePrefix = (String)dsMap.get("rawPrefix");
	        String dumpDir = res.getString("dumpDir");
	        String anomalyType = res.getString("anomalyType");
	        int batchsize=res.getInt("batchsize");
	        int max_sample=res.getInt("max_sample");
	        int sub_len=res.getInt("sub_len");
	        String scale=res.getString("scale");
	        int top_k=res.getInt("top_k");
	        File dumpF = new File(dumpDir);
	        if (!dumpF.exists()) {
	          dumpF.mkdirs();
	        }
	        
	     // check result file
	        String[] resultToks = {filePrefix, "exp", "np","batchsize", String.valueOf(batchsize), 
	        		"max_sample", String.valueOf(max_sample), "sub_len",String.valueOf(sub_len),"scale",String.valueOf(scale),"top_k", String.valueOf(top_k)};
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
	        LOGGER.info("run np: "+batchsize+" "+max_sample+" "+sub_len+" "+scale+" "+top_k);
	        Map<String,Object> params = new HashMap<>();
	        params.put("batchsize", batchsize);
	        params.put("max_sample", max_sample);
	        params.put("sub_len", sub_len);
	        params.put("scale",scale);
	        params.put("top_k", top_k);
	        NeighborProfile np = new NeighborProfile();
	        np.init(params, ts);
	        np.run();
	        
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
