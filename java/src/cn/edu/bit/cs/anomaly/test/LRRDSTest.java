package cn.edu.bit.cs.anomaly.test;

import java.util.HashMap;
import java.util.Map;

import cn.edu.bit.cs.anomaly.LRRDS;
import cn.edu.bit.cs.anomaly.entity.TimeSeriesMulDim;
import cn.edu.bit.cs.anomaly.util.FileHandler;

public class LRRDSTest {

	public static void main(String[] args) {
	    TimeSeriesMulDim MulT = null;
	    FileHandler fh = new FileHandler();
	    MulT = fh.readMulData("syn/sub/uni_subg_size/test/uni_subg_len_50_2000_0.1_7.csv",1);
	    System.out.println("Timeseries length: " + MulT.getLength());
	    Map<String, Object> arg_list = new HashMap<String, Object>();
	    arg_list.put("compressed_rate", 0.5);
	    arg_list.put("slack", 70);
	    arg_list.put("sub_minlength", 10);
	    long startTime =  System.currentTimeMillis();
	    LRRDS lrrds = new LRRDS();
	    lrrds.init(arg_list, MulT);
	    lrrds.run();
	    long endTime =  System.currentTimeMillis();
        long usedTime = (endTime-startTime)/1000;
        System.out.println(usedTime);
	  }

}
