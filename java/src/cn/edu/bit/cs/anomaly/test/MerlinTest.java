package cn.edu.bit.cs.anomaly.test;

import cn.edu.bit.cs.anomaly.Merlin;
import cn.edu.bit.cs.anomaly.NeighborProfile;
import cn.edu.bit.cs.anomaly.entity.TimeSeries;
import cn.edu.bit.cs.anomaly.util.FileHandler;

import java.util.HashMap;
import java.util.Map;

public class MerlinTest {
    public static int sub_len = 750;
    public static int max_sample = 8;
    public static int n_nnballs = 100;
    static int batchsize = 100;

    public static void main(String[] args) {
        TimeSeries T = null;
        FileHandler fh = new FileHandler();
        T = fh.readData("test/test_for_merlin.csv");
        System.out.println("Timeseries length: " + T.getLength());
        Map<String, Object> arg_list = new HashMap<String, Object>();
        arg_list.put("minL", 20);
        arg_list.put("maxL", 20);
        arg_list.put("top_k", 2);
        long startTime =  System.currentTimeMillis();
        Merlin merlin = new Merlin();
        merlin.init(arg_list, T);
        merlin.run();
        long endTime =  System.currentTimeMillis();
        long usedTime = (endTime-startTime);
        System.out.println(usedTime);
    }
}
