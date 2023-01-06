package cn.edu.bit.cs.anomaly.test;

import cn.edu.bit.cs.anomaly.NeighborProfile;
import cn.edu.bit.cs.anomaly.entity.TimeSeries;
import cn.edu.bit.cs.anomaly.util.FileHandler;

import java.util.HashMap;
import java.util.Map;

public class NPTest {
    public static int sub_len = 750;
    public static int max_sample = 8;
    public static int n_nnballs = 100;
    static int batchsize = 100;

    public static void main(String[] args) {
        TimeSeries T = null;
        FileHandler fh = new FileHandler();
        T = fh.readData("sub/taxi.csv");
        System.out.println("Timeseries length: " + T.getLength());
        //ArrayList<Double> nprofile;
        //NeighborProfile np = new NeighborProfile(T, n_nnballs, batchsize,sub_len, max_sample);
        //nprofile = np.nprofile;
        Map<String, Object> arg_list = new HashMap<String, Object>();
//    arg_list.put("batchsize",10000);
        arg_list.put("batchsize", 5000);
        arg_list.put("max_sample", 64);
        arg_list.put("sub_len", 48);
        arg_list.put("scale","zscore");
        arg_list.put("top_k", 5);
        long startTime =  System.currentTimeMillis();
        NeighborProfile np = new NeighborProfile();
        np.init(arg_list, T);
        np.run();
        long endTime =  System.currentTimeMillis();
        long usedTime = (endTime-startTime)/1000;
        System.out.println(usedTime);
    }
}
