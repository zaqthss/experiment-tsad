package cn.edu.bit.cs.anomaly.total.threshold;

import cn.edu.bit.cs.anomaly.*;
import cn.edu.bit.cs.anomaly.entity.Range;
import cn.edu.bit.cs.anomaly.entity.TimeSeries;
import cn.edu.bit.cs.anomaly.entity.TimeSeriesMulDim;
import cn.edu.bit.cs.anomaly.total.MetaData;
import cn.edu.bit.cs.anomaly.total.SubMetaData;
import cn.edu.bit.cs.anomaly.util.Constants;
import cn.edu.bit.cs.anomaly.util.DataHandler;
import cn.edu.bit.cs.anomaly.util.FileHandler;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThresholdUniSubTest {
    private static ArrayList<Double> creatLinspace(double start, double end, int num){
        double diff=(double) (end-start)/(num-1);
        ArrayList<Double> lin=new ArrayList<Double>();
        lin.add(start);
        for(int i=1;i<=num-1;i++){
            lin.add(start+i*diff);
        }
        return lin;
    }
    public static void main(String[] args) throws ArgumentParserException {
        ArgumentParser parser =
                ArgumentParsers.newFor("ThresholdUniSubTest")
                        .build()
                        .description("input ds, anomalyType, size, seed");
        parser.addArgument("ds").dest("ds").type(String.class);
        parser.addArgument("anomalyRate").dest("anomalyRate").type(String.class);
        parser.addArgument("anomalyType").dest("anomalyType").type(String.class)
                .choices("subg", "subs", "subt");
        parser.addArgument("anomalyLength").dest("anomalyLength").type(String.class);
        parser.addArgument("size").dest("size").type(String.class);
        parser.addArgument("seed").dest("seed").type(Integer.class).nargs("+");

        Namespace res = parser.parseArgs(args);
        String dsName = res.getString("ds");
        String anomalyRate = res.getString("anomalyRate");
        String anomalyType = res.getString("anomalyType");
        String anomalyLength = res.getString("anomalyLength");
        String size = res.getString("size");
        List<Integer> seeds = res.getList("seed");
        MetaData meta = SubMetaData.getInstance();
        Map<String, Object> dsMap = meta.getDataset().get(dsName);
        String dir = (String) dsMap.get("dataDir");
        String filePrefix = (String) dsMap.get("rawPrefix");
        FileHandler fh = new FileHandler();

        double min_threshold=0.0;
        double max_threshold=1.0;
        ArrayList<Double> vars=creatLinspace(min_threshold,max_threshold,200);
        String[] algNames = {"PBAD", "SAND", "NP","MERLIN","GrammarViz","IDK"};
        boolean[] willOperate = {false, false, false,false,false,true};
        String[] metricNames = {"precision", "recall","fmeasure"};

        final int VARSIZE = vars.size();
        final int ALGNUM = algNames.length;
        final int METRICNUM = 3; // precision, recall, f-measure
        double[][][] metrics = new double[ALGNUM][VARSIZE+1][METRICNUM];
        double[][] aucmetrics=new double[ALGNUM][1];

        TimeSeries timeseries = null;
        TimeSeriesMulDim timeSeriesMulDim = null;
        int algIndex = 0;

        PBAD pbad = null;
        LRRDS lrrds = null;
        SAND sand = null;
        NeighborProfile np = null;
        Merlin merlin=null;
        GrammarViz grammarviz=null;
        IDK idk=null;

        double alpha = 0;
        Constants.POS_BIAS bias = Constants.POS_BIAS.FLAT;
        ArrayList<Range> predictAnomaly = null;

        for(algIndex=0;algIndex<algNames.length;algIndex++){
            String rawPath = String.format("%s/test/%s_", dir, filePrefix, dir);
            //System.out.println("test with threshold " + vars.get(index) + " on " + rawPath + " begin");
            Map<Integer, ArrayList<Range>> realAnomalyMap = new HashMap<>();
            Map<Integer, TimeSeries> seriesMap = new HashMap<>();
            Map<Integer, TimeSeriesMulDim> seriesMulMap = new HashMap<>();
            //dsName="uni_"+vars[index]+"_sp";
            // PBAD
            if(algIndex==0){
                if (willOperate[algIndex]) {
                    for (int seed : seeds) {
                        System.out.println(algNames[algIndex] + " begin on seed " + seed);
                        if (!seriesMulMap.containsKey(seed)) {
                            timeSeriesMulDim = fh.readMulDataWithLabel(rawPath + seed + ".csv");
                            seriesMulMap.put(seed, timeSeriesMulDim);
                            ArrayList<Range> realAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
                            realAnomalyMap.put(seed, realAnomaly);
                        } else {
                            timeSeriesMulDim = seriesMulMap.get(seed);
                        }
                        pbad = new PBAD();
                        Map<String, Object> pbadParams = meta.getDataAlgParam().get(dsName)
                                .get(algNames[algIndex]);
                        pbad.init(pbadParams, timeSeriesMulDim);
                        pbad.run();
                        ArrayList<Double> score=pbad.getScore();
                        for (int index = 0; index < VARSIZE; ++index) {
                            double threshold=vars.get(index);
                            pbad.evaluate(threshold);
                            predictAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
                            DataHandler.evaluate(
                               alpha, bias, predictAnomaly, realAnomalyMap.get(seed), metrics[algIndex][index]);
                        }
                        metrics[algIndex][0][0]=1.0;
                        metrics[algIndex][0][1]=0.0;
                        metrics[algIndex][VARSIZE-1][0]=0.0;
                        metrics[algIndex][VARSIZE-1][1]=1.0;
                    }
                }
            }
            //SAND
            if(algIndex==1){
                if (willOperate[algIndex]) {
                    for (int seed : seeds) {
                        System.out.println(algNames[algIndex] + " begin on seed " + seed);
                        if (!seriesMap.containsKey(seed)) {
                            timeseries = fh.readDataWithLabel(rawPath + seed + ".csv");
                            seriesMap.put(seed, timeseries);
                            if (!realAnomalyMap.containsKey(seed)) {
                                ArrayList<Range> realAnomaly = DataHandler.findAnomalyRange(timeseries);
                                realAnomalyMap.put(seed, realAnomaly);
                            }
                        } else {
                            timeseries = seriesMap.get(seed);
                        }
                        sand = new SAND();
                        Map<String, Object> sandParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
                        sand.init(sandParams, timeseries);
                        sand.run();
                        for (int index = 0; index < VARSIZE; ++index) {
                            double threshold=vars.get(index);
                            sand.evaluate(threshold);
                            predictAnomaly = DataHandler.findAnomalyRange(timeseries);
                            DataHandler.evaluate(
                                    alpha, bias, predictAnomaly, realAnomalyMap.get(seed), metrics[algIndex][index]);
                        }
                        metrics[algIndex][0][0]=1.0;
                        metrics[algIndex][0][1]=0.0;
                        metrics[algIndex][VARSIZE][0]=0.0;
                        metrics[algIndex][VARSIZE][1]=1.0;
                    }
                }
            }
            //NP
            if(algIndex==2){
                if (willOperate[algIndex]) {
                    for (int seed : seeds) {
                        System.out.println(algNames[algIndex] + " begin on seed " + seed);
                        if (!seriesMap.containsKey(seed)) {
                            timeseries = fh.readDataWithLabel(rawPath + seed + ".csv");
                            seriesMap.put(seed, timeseries);
                            if (!realAnomalyMap.containsKey(seed)) {
                                ArrayList<Range> realAnomaly = DataHandler.findAnomalyRange(timeseries);
                                realAnomalyMap.put(seed, realAnomaly);
                            }
                        } else {
                            timeseries = seriesMap.get(seed);
                        }
                        np = new NeighborProfile();
                        Map<String, Object> npParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
                        np.init(npParams, timeseries);
                        np.run();
                        for (int index = 0; index < VARSIZE; ++index) {
                            double threshold=vars.get(index);
                            np.evaluate(threshold);
                            predictAnomaly = DataHandler.findAnomalyRange(timeseries);
                            DataHandler.evaluate(
                                    alpha, bias, predictAnomaly, realAnomalyMap.get(seed), metrics[algIndex][index]);
                        }
                        metrics[algIndex][0][0]=1.0;
                        metrics[algIndex][0][1]=0.0;
                        metrics[algIndex][VARSIZE-1][0]=0.0;
                        metrics[algIndex][VARSIZE-1][1]=1.0;
                    }
                }
            }
            //merlin
            if(algIndex==3){
                if (willOperate[algIndex]) {
                    for (int seed : seeds) {
                        System.out.println(algNames[algIndex] + " begin on seed " +seed);
                        //System.out.println(algNames[algIndex] + " begin");
                        if (!seriesMap.containsKey(seed)) {
                            timeseries = fh.readDataWithLabel(rawPath +seed + ".csv");
                            seriesMap.put(seed, timeseries);
                            if (!realAnomalyMap.containsKey(seed)) {
                                ArrayList<Range> realAnomaly = DataHandler.findAnomalyRange(timeseries);
                                realAnomalyMap.put(seed, realAnomaly);
                            }
                        } else {
                            timeseries = seriesMap.get(seed);
                        }
                        merlin = new Merlin();
                        Map<String, Object> merlinParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
                        int k=Integer.parseInt(size)/50;
                        System.out.println(k);
                        merlinParams.put("top_k", k);
                        merlin.init(merlinParams, timeseries);
                        merlin.run();
                        for (int index = 0; index < VARSIZE; ++index) {
                            merlin.evaluate(vars.get(index));
                            predictAnomaly = DataHandler.findAnomalyRange(timeseries);
                            DataHandler.evaluate(
                                    alpha, bias, predictAnomaly, realAnomalyMap.get(seed), metrics[algIndex][index]);
                        }
                        metrics[algIndex][0][0]=1.0;
                        metrics[algIndex][0][1]=0.0;
                        metrics[algIndex][VARSIZE-1][0]=0.0;
                        metrics[algIndex][VARSIZE-1][1]=1.0;
                    }
                }
            }
            //grammarviz
            if(algIndex==4){
                if (willOperate[algIndex]) {
                    for (int seed : seeds) {
                        System.out.println(algNames[algIndex] + " begin on seed " + seed);
                        if (!seriesMap.containsKey(seed)) {
                            timeseries = fh.readDataWithLabel(rawPath + seed + ".csv");
                            seriesMap.put(seed, timeseries);
                            if (!realAnomalyMap.containsKey(seed)) {
                                ArrayList<Range> realAnomaly = DataHandler.findAnomalyRange(timeseries);
                                realAnomalyMap.put(seed, realAnomaly);
                            }
                        } else {
                            timeseries = seriesMap.get(seed);
                        }
                        grammarviz = new GrammarViz();
                        Map<String, Object> grammarvizParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
                        for (int index = 0; index < VARSIZE; ++index) {
                            int k=(int) (vars.get(index)*Integer.parseInt(size))/50;
                            System.out.println(k);
                            grammarvizParams.put("DISCORDS_NUM", k);
                            grammarviz.init(grammarvizParams, timeseries);
                            grammarviz.run();
                            predictAnomaly = DataHandler.findAnomalyRange(timeseries);
                            DataHandler.evaluate(
                                    alpha, bias, predictAnomaly, realAnomalyMap.get(seed), metrics[algIndex][index]);
                        }
                        metrics[algIndex][0][0]=1.0;
                        metrics[algIndex][0][1]=0.0;
                        metrics[algIndex][VARSIZE-1][0]=0.0;
                        metrics[algIndex][VARSIZE-1][1]=1.0;

                    }
                }
            }
            //IDK
            if(algIndex==5){
                if (willOperate[algIndex]) {
                    for (int seed : seeds) {
                        System.out.println(algNames[algIndex] + " begin on seed " + seed);
                        if (!seriesMap.containsKey(seed)) {
                            timeseries = fh.readDataWithLabel(rawPath + seed + ".csv");
                            seriesMap.put(seed, timeseries);
                            if (!realAnomalyMap.containsKey(seed)) {
                                ArrayList<Range> realAnomaly = DataHandler.findAnomalyRange(timeseries);
                                realAnomalyMap.put(seed, realAnomaly);
                            }
                        } else {
                            timeseries = seriesMap.get(seed);
                        }
                        idk = new IDK();
                        Map<String, Object> idkParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
                        for (int index = 0; index < VARSIZE; ++index) {
                            int k=(int) (vars.get(index)*Integer.parseInt(size))/50;
                            System.out.println(k);
                            idkParams.put("top_k", k);
                            idk.init(idkParams, timeseries);
                            idk.run();
                            predictAnomaly = DataHandler.findAnomalyRange(timeseries);
                            DataHandler.evaluate(
                                    alpha, bias, predictAnomaly, realAnomalyMap.get(seed), metrics[algIndex][index]);
                        }
                        metrics[algIndex][0][0]=1.0;
                        metrics[algIndex][0][1]=0.0;
                        metrics[algIndex][VARSIZE-1][0]=0.0;
                        metrics[algIndex][VARSIZE-1][1]=1.0;

                    }
                }
            }

            ArrayList<Double> Recall_list =new ArrayList<Double>();
            ArrayList<Double> Precision_list =new ArrayList<Double>();
            for(int i=0;i< vars.size();i++){
                Recall_list.add(metrics[algIndex][i][1]);
                Precision_list.add(metrics[algIndex][i][0]);
            }
            double AP_range=0.0;
            ArrayList<Double> width_PR=new ArrayList<Double>();
            ArrayList<Double> height_PR=new ArrayList<Double>();
            for (int i=0;i<Recall_list.size()-1;i++){
                width_PR.add(Recall_list.get(i+1)-Recall_list.get(i));
                height_PR.add((double)((Precision_list.get(i)+Precision_list.get(i+1))/2));

            }
            for (int i=0;i<width_PR.size();i++){
                AP_range+=width_PR.get(i)*height_PR.get(i);
            }
            aucmetrics[algIndex][0]=AP_range;
        }
        fh.writeAUCResults("threshold", "" + dsName,algNames, "AUC_PR1",aucmetrics, seeds.size());
        fh.writeThresholdResults("threshold", "" + dsName, vars, algNames, metricNames,metrics, seeds.size());
        System.out.println();
    }
}
