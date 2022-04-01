package cn.edu.bit.cs.anomaly.evaluate;

import cn.edu.bit.cs.anomaly.entity.TimePoint;
import cn.edu.bit.cs.anomaly.entity.TimePointMulDim;
import cn.edu.bit.cs.anomaly.entity.TimeSeries;
import cn.edu.bit.cs.anomaly.entity.TimeSeriesMulDim;
import cn.edu.bit.cs.anomaly.total.MetaData;
import cn.edu.bit.cs.anomaly.total.PointMetaData;
import cn.edu.bit.cs.anomaly.total.SubMetaData;
import cn.edu.bit.cs.anomaly.util.FileHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


public class DataSetAnalysis {

    private String Path = "result\\dataset\\";
    private boolean issig = true;
    static List<String> sigdimset = Arrays.asList();
    static List<String> muldimset = Arrays.asList("mul_0.1");
    private int split = 16;

    public void Run(String dsName ,boolean isMul) throws IOException {
        MetaData metaData = PointMetaData.getInstance();
        //MetaData metaData = SubMetaData.getInstance();
        Map<String, Object> dsMap = metaData.getDataset().get(dsName);
        String filePrefix = (String) dsMap.get("rawPrefix");
        String dir = (String) dsMap.get("dataDir");
        String rawPath = String.format("%s/%s.csv", dir, filePrefix);
        FileHandler fh = new FileHandler();
        if (!isMul) {
            TimeSeries ts = fh.readDataWithLabel(rawPath);
            Map<Double, Integer> rangeCount = new TreeMap<>();
            double Max = Double.MIN_VALUE,
                    Min = Double.MAX_VALUE,
                    Avg = 0;
            for (TimePoint tp : ts.getTimeseries()) {
                if(tp.getObsVal()>Double.MAX_VALUE)
                    continue;
                if (tp.getObsVal() < Min) {
                    Min = tp.getObsVal();
                }
                if (tp.getObsVal() > Max) {
                    Max = tp.getObsVal();
                }
                Avg += tp.getObsVal();
            }
            Avg = Avg / ts.getLength();

            System.out.println("MaxValue:" + Max);
            System.out.println("MinValue:" + Min);
            System.out.println("AvgValue:" + Avg);

            double range = (Max - Min) / split;
            for (int i = 0; i < split; i++) {
                rangeCount.put(Min + i * range, 0);
            }

            for (TimePoint tp : ts.getTimeseries()) {
                for (Entry<Double, Integer> ent : rangeCount.entrySet()) {
                    if (tp.getObsVal() < ent.getKey() + range) {
                        rangeCount.put(ent.getKey(), ent.getValue() + 1);
                        break;
                    }
                }
            }

            for (Entry<Double, Integer> ent : rangeCount.entrySet()) {
                System.out.println(ent.getKey() + "~:" + ent.getValue());
            }

            double MaxDis = Double.MIN_VALUE,
                    MinDis = Double.MAX_VALUE,
                    AvgDis = 0;

            for (int i = 0; i < ts.getLength(); i++) {
                for (int j = i + 1; j < ts.getLength(); j++) {
                    double a = ts.getTimePoint(i).getObsVal();
                    double b = ts.getTimePoint(j).getObsVal();
                    double dis = Math.sqrt(Math.abs(a * a - b * b));
                    if (dis > MaxDis) {
                        MaxDis = dis;
                    }
                    if (dis < MinDis) {
                        MinDis = dis;
                    }
                    AvgDis += (2 * dis);
                }
            }
            AvgDis = AvgDis / (ts.getLength() * (ts.getLength() - 1));

            System.out.println("MaxDistance" + MaxDis);
            System.out.println("MinDistance" + MinDis);
            System.out.println("AvgDistance" + AvgDis);

            String file = Path + dsName + ".txt";

            File f = new File(file);
            f.delete();
            f.createNewFile();

            PrintWriter pw = new PrintWriter(new FileWriter(file));
            pw.println(dsName);
            pw.println("MaxValue:" + Max);
            pw.println("MinValue:" + Min);
            pw.println("AvgValue:" + Avg);
            pw.println("MaxDistance" + MaxDis);
            pw.println("MinDistance" + MinDis);
            pw.println("AvgDistance" + AvgDis);
            for (Entry<Double, Integer> ent : rangeCount.entrySet()) {
                pw.println(ent.getKey() + "~:" + ent.getValue());
            }
            pw.flush();
            pw.close();
        } else {
            TimeSeriesMulDim<TimePointMulDim> ts = fh.readMulDataWithLabel(rawPath);
            int dim = ts.getDim();
            Map<Double[], Integer> rangeCount = new HashMap<>();
            double[] Max, Min, Avg;
            Max = new double[dim];
            Min = new double[dim];
            Avg = new double[dim];
            Arrays.fill(Max, Double.MIN_VALUE);
            Arrays.fill(Min, Double.MAX_VALUE);
            Arrays.fill(Avg, 0);
            for (TimePointMulDim tp : ts.getTimeseries()) {
                for (int i = 0; i < dim; i++) {
                    if (tp.getObsVal()[i] < Min[i]) {
                        Min[i] = tp.getObsVal()[i];
                    }
                    if (tp.getObsVal()[i] > Max[i]) {
                        Max[i] = tp.getObsVal()[i];
                    }
                    Avg[i] += tp.getObsVal()[i];
                }
            }
            for (int i = 0; i < dim; i++) {
                Avg[i] = Avg[i] / ts.getLength();
            }


            double MaxDis = Double.MIN_VALUE,
                    MinDis = Double.MAX_VALUE,
                    AvgDis = 0;

            for (int i = 0; i < ts.getLength(); i++) {
                for (int j = i + 1; j < ts.getLength(); j++) {
                    double[] a = ts.getTimePoint(i).getObsVal();
                    double[] b = ts.getTimePoint(j).getObsVal();
                    double dis = 0;
                    for (int k = 0; k < ts.getDim(); k++) {
                        dis += Math.abs((a[k] * a[k]) - (b[k] * b[k]));
                    }
                    dis = Math.sqrt(dis);
                    if (dis > MaxDis) {
                        MaxDis = dis;
                    }
                    if (dis < MinDis) {
                        MinDis = dis;
                    }
                    AvgDis += (2 * dis);
                }
            }
            AvgDis = AvgDis / (ts.getLength() * (ts.getLength() - 1));

            PrintWriter pw = new PrintWriter(new FileWriter(Path + "\\" + dsName+".txt"));
            pw.println(dsName);
            String Maxs ="",Mins="",Avgs = "";
            for (int i = 0; i < ts.getDim(); i++) {
                Maxs +=Max[i]+",";
                Mins +=Min[i]+",";
                Avgs +=Avg[i]+",";
            }
            pw.println("MaxValue:" + Maxs);
            pw.println("MinValue:" + Mins);
            pw.println("AvgValue:" + Avgs);
            pw.println("MaxDistance:" + MaxDis);
            pw.println("MinDistance:" + MinDis);
            pw.println("AvgDistance:" + AvgDis);

            pw.close();
        }
    }

    public static void main(String[] args) {
        try {
            for (String s : sigdimset)
                new DataSetAnalysis().Run(s,false);
            for (String s : muldimset)
                new DataSetAnalysis().Run(s,true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
