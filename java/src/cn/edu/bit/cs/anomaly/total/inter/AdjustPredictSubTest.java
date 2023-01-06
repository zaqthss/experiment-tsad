package cn.edu.bit.cs.anomaly.total.inter;

import cn.edu.bit.cs.anomaly.CPOD;
import cn.edu.bit.cs.anomaly.Luminol;
import cn.edu.bit.cs.anomaly.NETS;
import cn.edu.bit.cs.anomaly.SHESD;
import cn.edu.bit.cs.anomaly.Stare;
import cn.edu.bit.cs.anomaly.entity.Range;
import cn.edu.bit.cs.anomaly.entity.TimePointMulDim;
import cn.edu.bit.cs.anomaly.entity.TimeSeries;
import cn.edu.bit.cs.anomaly.entity.TimeSeriesMulDim;
import cn.edu.bit.cs.anomaly.total.MetaData;
import cn.edu.bit.cs.anomaly.total.PointMetaData;
import cn.edu.bit.cs.anomaly.util.Constants.IS_ANOMALY;
import cn.edu.bit.cs.anomaly.util.Constants.POS_BIAS;
import cn.edu.bit.cs.anomaly.util.DataHandler;
import cn.edu.bit.cs.anomaly.util.FileHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Test accuracy on univariate point anomaly TODO: change vars, algNames, add algorithms, add
 * univariate algorithm
 */
public class AdjustPredictSubTest {

  public static void main(String[] args) {
    FileHandler fh = new FileHandler();
    String[] vars = {"yahoo", "twitter", "dlr", "ecg", "smtp", "exathlon_sp_pos", "uni_subg_sp_pos","uni_subs_sp_pos", "uni_subt_sp_pos"}; //{ "exathlon_sp_pos", "uni_subg_sp_pos","uni_subs_sp_pos", "uni_subt_sp_pos"};

    String[] algNames = {"CPOD", "NETS", "Stare", "Luminol", "SHESD"};
    boolean[] willOperate = {true, true, true, false, true};
    String[] metricNames = {"precision", "recall", "fmeasure"};

    final int VARSIZE = vars.length;
    final int ALGNUM = algNames.length; // NETS ...
    final int METRICNUM = 3; // precision, recall, f-measure

    long[][] algtime = new long[ALGNUM * 2][2];
    long[][] totaltime = new long[VARSIZE][ALGNUM * 2];
    double[][][] metrics = new double[VARSIZE][ALGNUM * 2][METRICNUM];

    TimeSeries[] tsArray = null;
    TimeSeriesMulDim timeSeriesMulDim = null;
    int algIndex = 0;

    CPOD cpod = null;
    NETS nets = null;
    Stare stare = null;
    Luminol luminol = null;
    SHESD shesd = null;

    double alpha = 0;
    POS_BIAS bias = POS_BIAS.FLAT;
    ArrayList<Range> predictAnomaly = null;

    for (int index = 0; index < VARSIZE; ++index) {
      System.out.println("test with " + vars[index] + " begin");
      Map<String, TreeMap<Long, TimePointMulDim>> realAnomalyMulMap = new HashMap<>();
      Map<String, ArrayList<Range>> realAnomalyMap = new HashMap<>();
      Map<String, TimeSeries[]> seriesMap = new HashMap<>();
      Map<String, TimeSeriesMulDim> seriesMulMap = new HashMap<>();
      String dsName = vars[index];
      MetaData meta = PointMetaData.getInstance();
      Map<String, Object> dsMap = meta.getDataset().get(dsName);
      String dir = (String) dsMap.get("dataDir");
      String filePrefix = (String) dsMap.get("rawPrefix");
      String rawPath = String.format("%s/%s/%s.csv", dir, "test", filePrefix);

      // CPOD
      algIndex = 0;
      if (willOperate[algIndex]) {
        System.out.println(algNames[algIndex] + " begin");
        if (!seriesMulMap.containsKey(dsName)) {
          timeSeriesMulDim = fh.readMulDataWithLabel(rawPath);
          seriesMulMap.put(dsName, timeSeriesMulDim);
          TreeMap<Long, TimePointMulDim> realAnomaly = DataHandler.findAnomalyPoint(
              timeSeriesMulDim);
          ArrayList<Range> realsubAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
          realAnomalyMulMap.put(dsName, realAnomaly);
          realAnomalyMap.put(dsName, realsubAnomaly);
        } else {
          timeSeriesMulDim = seriesMulMap.get(dsName);
        }
        algtime[algIndex][0] = System.currentTimeMillis();
        cpod = new CPOD();
        Map<String, Object> cpodParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
        cpod.init(cpodParams, timeSeriesMulDim);
        cpod.run();
        algtime[algIndex][1] = System.currentTimeMillis();
        predictAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
        DataHandler.evaluate(
            alpha, bias, predictAnomaly, realAnomalyMap.get(dsName), metrics[index][algIndex * 2]);
        timeSeriesMulDim = adjustPredict(realAnomalyMulMap.get(dsName), timeSeriesMulDim);
        predictAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
        DataHandler.evaluate(
            alpha, bias, predictAnomaly, realAnomalyMap.get(dsName),
            metrics[index][algIndex * 2 + 1]);
      }

      // NETS
      algIndex++;
      if (willOperate[algIndex]) {
        System.out.println(algNames[algIndex] + " begin");
        if (!seriesMulMap.containsKey(dsName)) {
          timeSeriesMulDim = fh.readMulDataWithLabel(rawPath);
          seriesMulMap.put(dsName, timeSeriesMulDim);
          TreeMap<Long, TimePointMulDim> realAnomaly =
              DataHandler.findAnomalyPoint(timeSeriesMulDim);
          realAnomalyMulMap.put(dsName, realAnomaly);
        } else {
          timeSeriesMulDim = seriesMulMap.get(dsName);
        }
        algtime[algIndex][0] = System.currentTimeMillis();
        nets = new NETS();
        Map<String, Object> netsParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
        nets.init(netsParams, timeSeriesMulDim);
        nets.run();
        algtime[algIndex][1] = System.currentTimeMillis();
        predictAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
        DataHandler.evaluate(
            alpha, bias, predictAnomaly, realAnomalyMap.get(dsName), metrics[index][algIndex * 2]);
        timeSeriesMulDim = adjustPredict(realAnomalyMulMap.get(dsName), timeSeriesMulDim);
        predictAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
        DataHandler.evaluate(
            alpha, bias, predictAnomaly, realAnomalyMap.get(dsName),
            metrics[index][algIndex * 2 + 1]);
      }
      // STARE
      algIndex++;
      if (willOperate[algIndex]) {
        System.out.println(algNames[algIndex] + " begin");
        if (!seriesMulMap.containsKey(dsName)) {
          timeSeriesMulDim = fh.readMulDataWithLabel(rawPath);
          seriesMulMap.put(dsName, timeSeriesMulDim);
          TreeMap<Long, TimePointMulDim> realAnomaly =
              DataHandler.findAnomalyPoint(timeSeriesMulDim);
          realAnomalyMulMap.put(dsName, realAnomaly);
        } else {
          timeSeriesMulDim = seriesMulMap.get(dsName);
        }
        algtime[algIndex][0] = System.currentTimeMillis();
        stare = new Stare();
        Map<String, Object> stareParams = meta.getDataAlgParam().get(dsName)
            .get(algNames[algIndex]);
        stare.init(stareParams, timeSeriesMulDim);
        stare.run();
        algtime[algIndex][1] = System.currentTimeMillis();
        predictAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
        DataHandler.evaluate(
            alpha, bias, predictAnomaly, realAnomalyMap.get(dsName), metrics[index][algIndex * 2]);
        timeSeriesMulDim = adjustPredict(realAnomalyMulMap.get(dsName), timeSeriesMulDim);
        predictAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
        DataHandler.evaluate(
            alpha, bias, predictAnomaly, realAnomalyMap.get(dsName),
            metrics[index][algIndex * 2 + 1]);
      }
      // Luminol
      algIndex++;
      if (willOperate[algIndex]) {
        System.out.println(algNames[algIndex] + " begin");
        if (seriesMap.containsKey(dsName)) {
          tsArray = seriesMap.get(dsName);
        } else if (seriesMulMap.containsKey(dsName)) {
          tsArray = seriesMulMap.get(dsName).convert();
        } else {
          timeSeriesMulDim = fh.readMulDataWithLabel(rawPath);
          tsArray = timeSeriesMulDim.convert();
          seriesMulMap.put(dsName, timeSeriesMulDim);
          TreeMap<Long, TimePointMulDim> realAnomaly =
              DataHandler.findAnomalyPoint(timeSeriesMulDim);
          realAnomalyMulMap.put(dsName, realAnomaly);
        }
        algtime[algIndex][0] = System.currentTimeMillis();
        luminol = new Luminol();
        Map<String, Object> luminolParams =
            meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
        for (TimeSeries ts : tsArray) {
          luminol.init(luminolParams, ts);
          luminol.run();
        }
        algtime[algIndex][1] = System.currentTimeMillis();
        // union each dim
        DataHandler.evaluate(tsArray, realAnomalyMulMap.get(dsName), metrics[index][algIndex * 2]);
      }
      // SHESD
      algIndex++;
      if (willOperate[algIndex]) {
        System.out.println(algNames[algIndex] + " begin");
        if (seriesMap.containsKey(dsName)) {
          tsArray = seriesMap.get(dsName);
        } else if (seriesMulMap.containsKey(dsName)) {
          tsArray = seriesMulMap.get(dsName).convert();
        } else {
          timeSeriesMulDim = fh.readMulDataWithLabel(rawPath);
          tsArray = timeSeriesMulDim.convert();
          seriesMulMap.put(dsName, timeSeriesMulDim);
          TreeMap<Long, TimePointMulDim> realAnomaly =
              DataHandler.findAnomalyPoint(timeSeriesMulDim);
          realAnomalyMulMap.put(dsName, realAnomaly);
        }
        algtime[algIndex][0] = System.currentTimeMillis();
        shesd = new SHESD();
        Map<String, Object> shesdParams =
            meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
        for (TimeSeries ts : tsArray) {
          shesd.init(shesdParams, ts);
          shesd.run();
        }
        algtime[algIndex][1] = System.currentTimeMillis();
        // union each dim
        combineTSArray(tsArray, timeSeriesMulDim);
        predictAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
        DataHandler.evaluate(
            alpha, bias, predictAnomaly, realAnomalyMap.get(dsName), metrics[index][algIndex * 2]);
        timeSeriesMulDim = adjustPredict(realAnomalyMulMap.get(dsName), timeSeriesMulDim);
        predictAnomaly = DataHandler.findAnomalyRange(timeSeriesMulDim);
        DataHandler.evaluate(
            alpha, bias, predictAnomaly, realAnomalyMap.get(dsName),
            metrics[index][algIndex * 2 + 1]);
      }

      for (int algi = 0; algi < ALGNUM * 2; ++algi) {
        totaltime[index][algi] += algtime[algi][1] - algtime[algi][0];
      }
      // write results
      fh.writeResults("ap", "mul-sub", vars, adjustNames(algNames), metricNames, totaltime,
          metrics, 1);
    } // end of rIndex
  }

  private static String[] adjustNames(String[] Names) {
    ArrayList<String> temp = new ArrayList<>();
    for (String s : Names) {
      temp.add(s);
      temp.add(s + "_ap");
    }
    String[] str = new String[temp.size()];
    temp.toArray(str);
    return str;
  }

  private static TimeSeriesMulDim adjustPredict(TreeMap<Long, TimePointMulDim> series,
      TimeSeriesMulDim rseries) {
    Set<Long> actual = series.keySet();
    TreeMap<Long, TimePointMulDim> predict = rseries.getTimeseriesMap();
    boolean anomaly_state = false;
    int anomaly_count = 0;
    int latency = 0;
    ArrayList<Long> keyset = new ArrayList<>(predict.keySet());
    for (int i = 0; i < keyset.size(); i++) {
      if (actual.contains(keyset.get(i))&& predict.get(keyset.get(i)).getIs_anomaly() == IS_ANOMALY.TRUE && !anomaly_state) {
        anomaly_state = true;
        anomaly_count += 1;
        for (int j = i; j > -1; j--) {
          if (!actual.contains(keyset.get(j))) {
            break;
          } else {
            if (predict.get(keyset.get(j)).getIs_anomaly() == IS_ANOMALY.FALSE) {
              predict.get(keyset.get(j)).setIs_anomaly(IS_ANOMALY.TRUE);
              latency += 1;
            }
          }
        }
      } else if (!actual.contains(keyset.get(i))) {
        anomaly_state = false;
      }
      if (anomaly_state) {
        predict.get(keyset.get(i)).setIs_anomaly(IS_ANOMALY.TRUE);
      }
    }
    rseries.setTimeseries(new ArrayList<>(predict.values()));
    return rseries;
  }

  private static void combineTSArray(TimeSeries[] tsArray,
      TimeSeriesMulDim<TimePointMulDim> series) {
    series.clear();
    ArrayList<Long> keyset = new ArrayList<>(series.getTimeseriesMap().keySet());
    for (int i = 0; i < keyset.size(); i++) {
      boolean tmp = false;
      for (int j = 0; j < tsArray.length; j++) {
        tmp = (tsArray[j].getTimeseriesMap().get(keyset.get(i)).getIs_anomaly() == IS_ANOMALY.TRUE)
            || tmp;
      }
      if (tmp) {
        series.getTimeseriesMap().get(keyset.get(i)).setIs_anomaly(IS_ANOMALY.TRUE);
      } else {
        series.getTimeseriesMap().get(keyset.get(i)).setIs_anomaly(IS_ANOMALY.FALSE);
      }
    }
  }
}
