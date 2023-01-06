package cn.edu.bit.cs.anomaly.test;

import cn.edu.bit.cs.anomaly.util.FileHandler;
import java.util.HashMap;
import cn.edu.bit.cs.anomaly.entity.TimePointMulDim;
import cn.edu.bit.cs.anomaly.entity.TimeSeriesMulDim;
//import cn.edu.bit.cs.anomaly.Stare;


public class StareTest {
    static String dataset = "YahooA2"; //dataset name
    static int W = 1421; //window size
    static int S = 71; //slide size
    static double R = 65; // size of a grid cell
    static int K = 50; // number of neighbors
    static int nW = 10000; // number of windows to process
    static int fixedN = 2; // fix N by a positive integer value
    static double skipThred = 0.1; // between 0 and 1. the default optimal value is 0.1


    public static void main(String[] args)  {

/*

        FileHandler.PATH ="D:\\";
        FileHandler fh = new FileHandler();
        TimeSeriesMulDim<TimePointMulDim> series =fh.readMulData("YahooA1.csv",1);

        Stare s = new Stare();
        HashMap<String, Object> arg = new HashMap<>();
        arg.put("W", W);
        arg.put("S", S);
        arg.put("R", R);
        arg.put("K", K);
        arg.put("skipThred", skipThred);
        arg.put("nW",nW);
        arg.put("fN",fixedN);
        s.init(arg, series);
        s.run();
*/
    }

    //The default paraemter values for each data set
    public static void loadDefaultArgs(String dataset) {
        switch (dataset) {
            case "YahooA1":
                W = 1415;
                S = 71;
                R = 60;
                K = 140;
                break;
            case "YahooA2":
                W = 1421;
                S = 71;
                R = 65;
                K = 50;
                break;
            case "HTTP":
                W = 6000;
                S = 300;
                R = 24;
                K = 5;
                break;
            case "DLR":
                W = 1000;
                S = 50;
                R = 18.8;
                K = 2;
                break;
            case "ECG":
                W = 2337;
                S = 117;
                R = 13.57;
                K = 2;
                break;
        }
    }
}
