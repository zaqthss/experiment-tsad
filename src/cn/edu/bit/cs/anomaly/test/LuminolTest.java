package cn.edu.bit.cs.anomaly.test;

import cn.edu.bit.cs.anomaly.Luminol;
import cn.edu.bit.cs.anomaly.entity.TimeSeries;
import cn.edu.bit.cs.anomaly.total.MetaData;
import cn.edu.bit.cs.anomaly.total.PointMetaData;
import cn.edu.bit.cs.anomaly.total.SubMetaData;
import cn.edu.bit.cs.anomaly.util.FileHandler;

import java.util.HashMap;
import java.util.Map;



public class LuminolTest {

    public static int  precision = 4;
    public static int  chunk_size = 2;
    public static int  lag_window_size ;
    public static int  future_window_size;
    public static double threshold = 100;
    public static void main(String [] args){
        MetaData meta = PointMetaData.getInstance();
        Map<String, Object> dsMap = meta.getDataset().get("yahoo");
        String dir = (String) dsMap.get("dataDir");
        String filePrefix = (String) dsMap.get("rawPrefix");
        String rawPath = String.format("%s/%s.csv", dir, filePrefix);
        FileHandler.PATH ="E:\\Run\\data\\";
        FileHandler fh = new FileHandler();

        TimeSeries ts = fh.readData(rawPath);

        future_window_size =lag_window_size = ts.getLength()/80;

        Map<String, Object> arg = new HashMap<>();
        arg.put("precision", precision);
        arg.put("chunk_size", chunk_size);
        arg.put("lag_window_size", lag_window_size);
        arg.put("future_window_size", future_window_size);
        arg.put("threshold", threshold);

        Luminol luminol = new Luminol();
        luminol.init(arg, ts);
        luminol.run();


    }
}
