package cn.edu.bit.cs.anomaly.total.vus;
import cn.edu.bit.cs.anomaly.GrammarViz;
import cn.edu.bit.cs.anomaly.LRRDS;
import cn.edu.bit.cs.anomaly.Merlin;
import cn.edu.bit.cs.anomaly.NeighborProfile;
import cn.edu.bit.cs.anomaly.PBAD;
import cn.edu.bit.cs.anomaly.SAND;
import cn.edu.bit.cs.anomaly.entity.Range;
import cn.edu.bit.cs.anomaly.entity.TimeSeries;
import cn.edu.bit.cs.anomaly.entity.TimeSeriesMulDim;
import cn.edu.bit.cs.anomaly.evaluate.VUSMetric;
import cn.edu.bit.cs.anomaly.total.MetaData;
import cn.edu.bit.cs.anomaly.total.SubMetaData;
import cn.edu.bit.cs.anomaly.util.Constants;
import cn.edu.bit.cs.anomaly.util.Constants.POS_BIAS;
import cn.edu.bit.cs.anomaly.util.DataHandler;
import cn.edu.bit.cs.anomaly.util.FileHandler;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Test accuracy on univariate subsequence anomaly
 */
public class VusTest {

    public static void main(String[] args) throws Exception {
        ArgumentParser parser =
                ArgumentParsers.newFor("TypeUniSubTest")
                        .build()
                        .description("input ds, anomalyType, size, seed");
        parser.addArgument("ds").dest("ds").type(String.class);
        parser.addArgument("anomalyRate").dest("anomalyRate").type(String.class);
        parser.addArgument("anomalyLength").dest("anomalyLength").type(String.class);
        parser.addArgument("size").dest("size").type(String.class);
        parser.addArgument("seed").dest("seed").type(Integer.class).nargs("+");

        Namespace res = parser.parseArgs(args);
        String dsName = res.getString("ds");
        String anomalyRate = res.getString("anomalyRate");
        String anomalyLength = res.getString("anomalyLength");
        String size = res.getString("size");
        List<Integer> seeds = res.getList("seed");
        MetaData meta = SubMetaData.getInstance();
        String dir="syn/sub/uni";
        String filePrefix="uni";
        FileHandler fh = new FileHandler();

        String[] vars = {"subg","subs","subt"};
        boolean[] willOperate = {true,true, true, true};

        String[] algNames = {"PBAD", "LRRDS", "SAND", "NP"};
        String[] metricNames = {"precision", "recall","fmeasure","r_auc_roc","r_auc_pr","vus_roc","vus_pr"};

        final int VARSIZE = vars.length;
        final int ALGNUM = algNames.length;
        final int METRICNUM = 7;  // precision, recall

        long[][] algtime = new long[ALGNUM][2];
        long[][] totaltime = new long[VARSIZE][ALGNUM];
        double[][][] metrics = new double[VARSIZE][ALGNUM][METRICNUM];

        TimeSeries timeseries = null;
        TimeSeriesMulDim timeSeriesMulDim = null;
        ArrayList<Integer> labels=new ArrayList<>();
        int algIndex = 0;
        int slidingWindow=50;

        PBAD pbad = null;
        LRRDS lrrds = null;
        SAND sand = null;
        NeighborProfile np = null;
        Merlin merlin=null;
        GrammarViz grammarviz=null;

        double alpha = 0;
        Constants.POS_BIAS bias = Constants.POS_BIAS.FLAT;
        ArrayList<Range> predictAnomaly = null;

        for (int index = 0; index < VARSIZE; ++index) {
            String rawPath =
                    String.format("%s_%s_rate/test/%s_%s_len_%s_%s_%s_", dir, vars[index],filePrefix, vars[index], anomalyLength, size,
                            anomalyRate);
            System.out.println("test with " + vars[index] + " on " + rawPath + " begin");
            Map<Integer, ArrayList<Range>> realAnomalyMap = new HashMap<>();
            Map<Integer, TimeSeries> seriesMap = new HashMap<>();
            Map<Integer, TimeSeriesMulDim> seriesMulMap = new HashMap<>();
            Map<Integer,ArrayList<Integer>> labelMap=new HashMap<>();
            dsName="uni_"+vars[index]+"_sp";
            // PBAD
            algIndex = 0;
            if (willOperate[algIndex]) {
                for (int seed : seeds) {
                    System.out.println(algNames[algIndex] + " begin on seed " + seed);
                    if (!seriesMulMap.containsKey(seed)) {
                        timeSeriesMulDim = fh.readMulDataWithLabel(rawPath + seed + ".csv");
                        labels=fh.readLabel(rawPath + seed + ".csv");
                        seriesMulMap.put(seed, timeSeriesMulDim);
                        labelMap.put(seed,labels);
                        ArrayList<Range> realAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
                        realAnomalyMap.put(seed, realAnomaly);
                    } else {
                        timeSeriesMulDim = seriesMulMap.get(seed);
                        labels=labelMap.get(seed);
                    }
                    algtime[algIndex][0] = System.currentTimeMillis();
                    pbad = new PBAD();
                    Map<String,Object> pbadParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
                    pbad.init(pbadParams, timeSeriesMulDim);
                    pbad.run();
                    ArrayList<Double> score=pbad.getScore();
                    algtime[algIndex][1] = System.currentTimeMillis();
                    totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
                    predictAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
                    String dumpPath = String.format(
                            "%s/%s_%s.csv", "../middle/acc", algNames[algIndex], dsName);
                    fh.writeAnomalyRange(predictAnomaly, dumpPath);
                    fh.writeScore(score,String.format(
                            "%s/%s_%s_score.csv", "../middle/acc", algNames[algIndex], dsName));
                    DataHandler.evaluate(alpha, bias, predictAnomaly, realAnomalyMap.get(seed),
                            metrics[index][algIndex]);
                    VUSMetric vus=new VUSMetric();
                    HashMap<Integer,Double> auc= vus.RangeAUC(labels,score,slidingWindow);
                    metrics[index][algIndex][3]+=auc.get(1);
                    metrics[index][algIndex][4]+=auc.get(2);
                    HashMap<Integer,Double> volume=vus.RangeAUC_volume(labels,score,2*slidingWindow);
                    metrics[index][algIndex][5]+=volume.get(1);
                    metrics[index][algIndex][6]+=volume.get(2);
                }
            }
            // LRRDS
            algIndex++;
            if (willOperate[algIndex]) {
                for (int seed : seeds) {
                    System.out.println(algNames[algIndex] + " begin on seed " + seed);
                    if (!seriesMulMap.containsKey(seed)) {
                        timeSeriesMulDim = fh.readMulDataWithLabel(rawPath + seed + ".csv");
                        seriesMulMap.put(seed, timeSeriesMulDim);
                        labels=fh.readLabel(rawPath + seed + ".csv");
                        labelMap.put(seed,labels);
                        ArrayList<Range> realAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
                        realAnomalyMap.put(seed, realAnomaly);
                    } else {
                        timeSeriesMulDim = seriesMulMap.get(seed);
                        labels=labelMap.get(seed);
                    }
                    algtime[algIndex][0] = System.currentTimeMillis();
                    lrrds = new LRRDS();
                    Map<String,Object> lrrdsParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
                    if(vars[index].equals("subt")) {
                        lrrdsParams.put("compressed_rate", 0.5);
                        lrrdsParams.put("sub_minlength", 1);
                    }
                    lrrds.init(lrrdsParams, timeSeriesMulDim);
                    lrrds.run();
                    ArrayList<Double> score=lrrds.getScore();
                    algtime[algIndex][1] = System.currentTimeMillis();
                    totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
                    predictAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
                    String dumpPath = String.format(
                            "%s/%s_%s.csv", "../middle/acc", algNames[algIndex], dsName);
                    fh.writeAnomalyRange(predictAnomaly, dumpPath);
                    fh.writeScore(score,String.format(
                            "%s/%s_%s_score.csv", "../middle/acc", algNames[algIndex], dsName));
                    DataHandler.evaluate(alpha, bias, predictAnomaly, realAnomalyMap.get(seed),
                            metrics[index][algIndex]);
                    VUSMetric vus=new VUSMetric();
                    HashMap<Integer,Double> auc= vus.RangeAUC(labels,score,slidingWindow);
                    metrics[index][algIndex][3]+=auc.get(1);
                    metrics[index][algIndex][4]+=auc.get(2);
                    HashMap<Integer,Double> volume=vus.RangeAUC_volume(labels,score,2*slidingWindow);
                    metrics[index][algIndex][5]+=volume.get(1);
                    metrics[index][algIndex][6]+=volume.get(2);
                }
            }
            // SAND
            algIndex++;
            if (willOperate[algIndex]) {
                for (int seed : seeds) {
                    System.out.println(algNames[algIndex] + " begin on seed " + seed);
                    if (!seriesMap.containsKey(seed)) {
                        timeseries = fh.readDataWithLabel(rawPath + seed + ".csv");
                        seriesMap.put(seed, timeseries);
                        labels=fh.readLabel(rawPath + seed + ".csv");
                        labelMap.put(seed,labels);
                        if (!realAnomalyMap.containsKey(seed)) {
                            ArrayList<Range> realAnomaly = DataHandler.findAnomalyRange(timeseries);
                            realAnomalyMap.put(seed, realAnomaly);
                        }
                    } else {
                        timeseries = seriesMap.get(seed);
                        labels=labelMap.get(seed);
                    }
                    algtime[algIndex][0] = System.currentTimeMillis();
                    sand = new SAND();
                    Map<String, Object> sandParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
                    sand.init(sandParams, timeseries);
                    sand.run();
                    ArrayList<Double> score=sand.getScore();
                    algtime[algIndex][1] = System.currentTimeMillis();
                    totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
                    predictAnomaly = DataHandler.findAnomalyRange(timeseries);
                    String dumpPath = String.format(
                            "%s/%s_%s.csv", "../middle/acc", algNames[algIndex], dsName);
                    fh.writeAnomalyRange(predictAnomaly, dumpPath);
                    fh.writeScore(score,String.format(
                            "%s/%s_%s_score.csv", "../middle/acc", algNames[algIndex], dsName));
                    DataHandler.evaluate(
                            alpha, bias, predictAnomaly, realAnomalyMap.get(seed), metrics[index][algIndex]);
                    VUSMetric vus=new VUSMetric();
                    HashMap<Integer,Double> auc= vus.RangeAUC(labels,score,slidingWindow);
                    metrics[index][algIndex][3]+=auc.get(1);
                    metrics[index][algIndex][4]+=auc.get(2);
                    HashMap<Integer,Double> volume=vus.RangeAUC_volume(labels,score,2*slidingWindow);
                    metrics[index][algIndex][5]+=volume.get(1);
                    metrics[index][algIndex][6]+=volume.get(2);
                }
            }
            // NP
            algIndex++;
            if (willOperate[algIndex]) {
                for (int seed : seeds) {
                    System.out.println(algNames[algIndex] + " begin on seed " + seed);
                    if (!seriesMap.containsKey(seed)) {
                        timeseries = fh.readDataWithLabel(rawPath + seed + ".csv");
                        seriesMap.put(seed, timeseries);
                        labels=fh.readLabel(rawPath + seed + ".csv");
                        labelMap.put(seed,labels);
                        if (!realAnomalyMap.containsKey(seed)) {
                            ArrayList<Range> realAnomaly = DataHandler.findAnomalyRange(timeseries);
                            realAnomalyMap.put(seed, realAnomaly);
                        }
                    } else {
                        timeseries = seriesMap.get(seed);
                        labels=labelMap.get(seed);
                    }
                    algtime[algIndex][0] = System.currentTimeMillis();
                    np = new NeighborProfile();
                    Map<String, Object> npParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
                    np.init(npParams, timeseries);
                    np.run();
                    ArrayList<Double> score=np.getScore();
                    algtime[algIndex][1] = System.currentTimeMillis();
                    totaltime[index][algIndex] += algtime[algIndex][1] - algtime[algIndex][0];
                    predictAnomaly = DataHandler.findAnomalyRange(timeseries);
                    String dumpPath = String.format(
                            "%s/%s_%s.csv", "../middle/acc", algNames[algIndex], dsName);
                    fh.writeAnomalyRange(predictAnomaly, dumpPath);
                    fh.writeScore(score,String.format(
                            "%s/%s_%s_score.csv", "../middle/acc", algNames[algIndex], dsName));
                    DataHandler.evaluate(
                            alpha, bias, predictAnomaly, realAnomalyMap.get(seed), metrics[index][algIndex]);
                    VUSMetric vus=new VUSMetric();
                    HashMap<Integer,Double> auc= vus.RangeAUC(labels,score,slidingWindow);
                    metrics[index][algIndex][3]+=auc.get(1);
                    metrics[index][algIndex][4]+=auc.get(2);
                    HashMap<Integer,Double> volume=vus.RangeAUC_volume(labels,score,2*slidingWindow);
                    metrics[index][algIndex][5]+=volume.get(1);
                    metrics[index][algIndex][6]+=volume.get(2);
                }
            }
            try {
                PrintWriter pw = new PrintWriter(new FileWriter("result/vus/"+dsName+"_existence.csv"));
                String head = "acc" + "," + String.join(",", algNames);
                pw.println(head);
                for(int i=0;i<metricNames.length;i++){
                    pw.print(metricNames[i]+",");
                    for(int j=0;j<algNames.length-1;j++){
                        pw.print(metrics[index][j][i]/seeds.size()+",");
                    }
                    pw.println(metrics[index][algNames.length-1][i]/seeds.size());
                }
                pw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } // end of rIndex
    }
}
