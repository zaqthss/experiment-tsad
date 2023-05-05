package cn.edu.bit.cs.anomaly.test;

import cn.edu.bit.cs.anomaly.IDK;
import cn.edu.bit.cs.anomaly.Merlin;
import cn.edu.bit.cs.anomaly.entity.TimePoint;
import cn.edu.bit.cs.anomaly.entity.TimeSeries;
import cn.edu.bit.cs.anomaly.util.FileHandler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import cn.edu.bit.cs.anomaly.util.FileHandler;
public class IDKTest {
    public static void main(String[] args) {
        TimeSeries T = null;
        T = readData("/sub/sed/valN/sed.csv");
        System.out.println("Timeseries length: " + T.getLength());
        Map<String, Object> arg_list = new HashMap<String, Object>();
        arg_list.put("sample_num", 100);
        arg_list.put("sample_size1", 4);
        arg_list.put("sample_size2", 2);
        arg_list.put("window_size", 64);
        arg_list.put("top_k", 28);
        long startTime =  System.currentTimeMillis();
        IDK idk = new IDK();
        idk.init(arg_list, T);
        idk.run();
        ArrayList<Double> score=idk.getScore();
        FileHandler fh = new FileHandler();
        fh.writeScore(score,"../middle/idk_sed_score.csv");
    }
    public static String PATH = "../data/";
    public static TimeSeries readData(String filename) {
        TimeSeries timeSeries = new TimeSeries();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(PATH + filename));

            String line = null;
            long timestamp;
            double value;
            TimePoint tp = null;

            line = br.readLine(); // header

            int i = 0;
            while ((line = br.readLine()) != null) {
                i++;
                String[] vals = line.split(",");
                // timestamp = Long.parseLong(vals[0]);
                value = Double.parseDouble(vals[1]);

                tp = new TimePoint(0, value);
                timeSeries.addPoint(tp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return timeSeries;
    }
}
