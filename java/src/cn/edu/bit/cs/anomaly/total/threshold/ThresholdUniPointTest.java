package cn.edu.bit.cs.anomaly.total.threshold;

import cn.edu.bit.cs.anomaly.CPOD;
import cn.edu.bit.cs.anomaly.GrammarViz;
import cn.edu.bit.cs.anomaly.LRRDS;
import cn.edu.bit.cs.anomaly.Luminol;
import cn.edu.bit.cs.anomaly.Merlin;
import cn.edu.bit.cs.anomaly.NETS;
import cn.edu.bit.cs.anomaly.NeighborProfile;
import cn.edu.bit.cs.anomaly.PBAD;
import cn.edu.bit.cs.anomaly.SAND;
import cn.edu.bit.cs.anomaly.SHESD;
import cn.edu.bit.cs.anomaly.Stare;
import cn.edu.bit.cs.anomaly.entity.Range;
import cn.edu.bit.cs.anomaly.entity.TimePointMulDim;
import cn.edu.bit.cs.anomaly.entity.TimeSeries;
import cn.edu.bit.cs.anomaly.entity.TimeSeriesMulDim;
import cn.edu.bit.cs.anomaly.total.MetaData;
import cn.edu.bit.cs.anomaly.total.PointMetaData;
import cn.edu.bit.cs.anomaly.total.SubMetaData;
import cn.edu.bit.cs.anomaly.util.Constants;
import cn.edu.bit.cs.anomaly.util.DataHandler;
import cn.edu.bit.cs.anomaly.util.FileHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class ThresholdUniPointTest {
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
                        .description("input ds, anomalyRate, anomalyType, size, seed");
        parser.addArgument("ds").dest("ds").type(String.class);
        parser.addArgument("anomalyRate").dest("anomalyRate").type(String.class);
        parser.addArgument("anomalyType").dest("anomalyType").type(String.class)
                .choices("pointc","pointg");
        parser.addArgument("size").dest("size").type(String.class);
        parser.addArgument("seed").dest("seed").type(Integer.class).nargs("+");

        Namespace res = parser.parseArgs(args);
        String dsName = res.getString("ds");
        String anomalyRate = res.getString("anomalyRate");
        String anomalyType = res.getString("anomalyType");
        String size = res.getString("size");
        List<Integer> seeds = res.getList("seed");
        MetaData meta = PointMetaData.getInstance();
        Map<String, Object> dsMap = meta.getDataset().get(dsName);
        String dir = (String) dsMap.get("dataDir");
        String filePrefix = (String) dsMap.get("rawPrefix");
        FileHandler fh = new FileHandler();

        double min_threshold=0.0;
        double max_threshold=1.0;
        ArrayList<Double> vars=creatLinspace(min_threshold,max_threshold,200);
        //String[] vars = {"0.02","0.04","0.06","0.08","0.1","0.12","0.14","0.16","0.18"};
        String[] algNames = { "Stare"};
        boolean[] willOperate = {true};
        String[] metricNames = {"precision", "recall", "fmeasure"};

        final int VARSIZE = vars.size();
        final int ALGNUM = algNames.length;
        final int METRICNUM = 3; // precision, recall, f-measure
        double[][][] metrics = new double[ALGNUM][VARSIZE+1][METRICNUM];
        double[][] aucmetrics=new double[ALGNUM][1];

        TimeSeries timeseries = null;
        TimeSeriesMulDim timeSeriesMulDim = null;
        int algIndex = 0;

        Stare stare = null;


        double alpha = 0;
        Constants.POS_BIAS bias = Constants.POS_BIAS.FLAT;
        ArrayList<Range> predictAnomaly = null;

        for(algIndex=0;algIndex<algNames.length;algIndex++){
            String rawPath = String.format("%s/%s/%s_%s_%s_%s_", dir, "test", filePrefix, anomalyType,
                size, anomalyRate);
            //System.out.println("test with threshold " + vars.get(index) + " on " + rawPath + " begin");
            Map<Integer, TreeMap<Long, TimePointMulDim>> realAnomalyMulMap = new HashMap<>();
            Map<Integer, TimeSeries[]> seriesMap = new HashMap<>();
            Map<Integer, TimeSeriesMulDim> seriesMulMap = new HashMap<>();
            // CPOD
            if(algIndex==0){
                if (willOperate[algIndex]) {
                    for (int seed : seeds) {
                        System.out.println(algNames[algIndex] + " begin on seed " + seed);
                        if (!seriesMulMap.containsKey(seed)) {
                            timeSeriesMulDim = fh.readMulDataWithLabel(rawPath + seed + ".csv");
                            seriesMulMap.put(seed, timeSeriesMulDim);
                            TreeMap<Long, TimePointMulDim> realAnomaly = DataHandler.findAnomalyPoint(
                                timeSeriesMulDim);
                            realAnomalyMulMap.put(seed, realAnomaly);
                        } else {
                            timeSeriesMulDim = seriesMulMap.get(seed);
                        }
                        stare = new Stare();
                        Map<String, Object> stareParams =
                            meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
                        stareParams.put("rate",0.1);
                        stare.init(stareParams, timeSeriesMulDim);
                        stare.run();
/*
                        ArrayList<Double> score=pbad.getScore();
                        for (int index = 0; index < VARSIZE; ++index) {
                            double threshold=vars.get(index);
                            pbad.evaluate(threshold);
                            predictAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
                            DataHandler.evaluate(
                               alpha, bias, predictAnomaly, realAnomalyMap.get(seed), metrics[algIndex][index]);
                        }

 */
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
        fh.writeAUCResults("threshold", "" + dsName,algNames, "AUC_PR",aucmetrics, seeds.size());
        fh.writeThresholdResults("threshold", "" + dsName, vars, algNames, metricNames,metrics, seeds.size());
        System.out.println();
    }
}
