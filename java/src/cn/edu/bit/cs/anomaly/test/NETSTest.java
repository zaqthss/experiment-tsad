package cn.edu.bit.cs.anomaly.test;

import cn.edu.bit.cs.anomaly.NETS;
import cn.edu.bit.cs.anomaly.entity.TimeSeriesMulDim;
import cn.edu.bit.cs.anomaly.total.MetaData;
import cn.edu.bit.cs.anomaly.total.PointMetaData;
import cn.edu.bit.cs.anomaly.total.SubMetaData;
import cn.edu.bit.cs.anomaly.util.FileHandler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class NETSTest {
    public static String dataset = "TAO";
    public static String method = "NETS";
    public static double R = 1.9; // distance threshold, default=6.5(HPC), 115(EM), 1.9(TAO), 0.45(STK), 0.028(GAU), 525(FC), 2.75(GAS)
    public static int K = 50; // neighborhood threshold, default = 50
    public static int dim = 3; // dimension, default = 7(HPC), 16(EM), 55(FC), 3(TAO)
    public static int subDim = 1; // sub-dimension selected by
    public static int randSubDim = 0; //0: false, 1:true
    public static int S = 500; // sliding size, default = 500(FC, TAO), 5000(Otherwise)
    public static int W = 10000; // sliding size, default = 10000(FC, TAO), 100000(Otherwise)
    public static int nS = W / S;
    public static int nW = 1;
    public static BufferedWriter fw;
    public static String printType = "Console";

    public static double allTimeSum = 0;
    public static double peakMemory = 0;

    public static void main(String[] args) throws IOException {
        NETS nets = new NETS();
        String dsName = "yahoo";
        MetaData meta = PointMetaData.getInstance();
        Map<String, Object> dsMap = meta.getDataset().get(dsName);
        String dir = (String) dsMap.get("dataDir");
        String filePrefix = (String) dsMap.get("rawPrefix");
        String rawPath = String.format("%s/%s.csv", dir, filePrefix);
        FileHandler.PATH ="E:\\Run\\data\\";
        FileHandler fh = new FileHandler();

        TimeSeriesMulDim ts = fh.readMulData(rawPath,1);

        Map<String, Object> arg = new HashMap<>();
        arg.put("dim", dim);
        arg.put("subDim", subDim);
        arg.put("R", R);
        arg.put("K", K);
        arg.put("S", S);
        arg.put("W", W);
        arg.put("nW", nW);
        arg.put("randSubDim", randSubDim);
        nets.init(arg, ts);
        nets.run();

   }


    public static void loadArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].indexOf("--") == 0) {
                switch (args[i]) {
                    case "--R":
                        R = Double.valueOf(args[i + 1]);
                        break;
                    case "--D":
                        dim = Integer.valueOf(args[i + 1]);
                        break;
                    case "--sD":
                        subDim = Integer.valueOf(args[i + 1]);
                        break;
                    case "--rand":
                        randSubDim = Integer.valueOf(args[i + 1]);
                        break;
                    case "--K":
                        K = Integer.valueOf(args[i + 1]);
                        break;
                    case "--W":
                        W = Integer.valueOf(args[i + 1]);
                        break;
                    case "--S":
                        S = Integer.valueOf(args[i + 1]);
                        break;
                    case "--nW":
                        nW = Integer.valueOf(args[i + 1]);
                        break;
                    case "--dataset":
                        dataset = args[i + 1];
                        break;
                    case "--method":
                        method = args[i + 1];
                        break;
                }
                nS = W / S;
            }
        }
    }

    public static void printInfo(int itr, String dataset, double[] dimLength, double[] subDimLength, String type) throws IOException {
        /* Print Information */
        if (type == "Console") {
            System.out.println("# Dataset: " + dataset);
            System.out.println("Method: " + method);
            System.out.println("Dim: " + dim);
            System.out.println("subDim: " + subDim);
            System.out.println("R/K/W/S: " + R + "/" + K + "/" + W + "/" + S);
            System.out.println("# of windows: " + (itr - nS + 1));
            System.out.println("Avg CPU time(s) \t Peak memory(MB)");
            System.out.println(allTimeSum / (itr - nS + 1) + "\t" + peakMemory);
        } else if (type == "File") {
            fw.write("# Dataset: " + dataset + "\n");
            fw.write("Method: " + method + "\n");
            fw.write("Dim: " + dim + "\n");
            fw.write("subDim: " + subDim + "\n");
            fw.write("R/K/W/S: " + R + "/" + K + "/" + W + "/" + S + "\n");
            fw.write("# of windows: " + (itr - nS + 1) + "\n");
            fw.write("Avg CPU time(s) \t Peak memory(MB)" + "\n");
            fw.write(allTimeSum / (itr - nS + 1) + "\t" + peakMemory + "\n");
            fw.flush();
            fw.close();
        }
    }
}
