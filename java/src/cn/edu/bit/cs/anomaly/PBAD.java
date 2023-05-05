package cn.edu.bit.cs.anomaly;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import cn.edu.bit.cs.anomaly.entity.TimePoint;
import cn.edu.bit.cs.anomaly.entity.TimePointMulDim;
import cn.edu.bit.cs.anomaly.entity.TimeSeries;
import cn.edu.bit.cs.anomaly.entity.TimeSeriesMulDim;
import cn.edu.bit.cs.anomaly.util.Constants;
import cn.edu.bit.cs.anomaly.util.DataHandler;
import cn.edu.bit.cs.anomaly.util.FileHandler;
import cn.edu.bit.cs.anomaly.util.SubseqScore;
import cn.edu.bit.cs.anomaly.util.Constants.IS_ANOMALY;
import weka.classifiers.trees.IsolationForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/** @author dsq */
public class PBAD implements MultiDimAlgorithm {
  public TimeSeriesMulDim MulT;
  public int window_size = 12;
  public int window_incr = 6;

  public ArrayList<Double> score;
  public int sequential_minlength = 2;
  public double distance_lambda = 2.0;
  public int distance_formula = 1;
  public double relative_minsup = 0.01;
  public double jaccard_threshold = 0.9;
  public int alphabet_size = 30;
  public int bin_size = 1;
  public boolean feature_flag=false;
  public int max_feature=50;

  public LinkedHashMap<Double, Integer> encode_dct;
  public LinkedHashMap<Integer, Double> decode_dct;
  public LinkedHashMap<String, ArrayList<Pattern>> pattern_dct;

  public ArrayList<ArrayList<TimeSeries>> preprocessed_series_D;
  public ArrayList<ArrayList<TimeSeries>> preprocessed_series_UD;
  public ArrayList<TimeSeries> series;
  public ArrayList<TimeSeries> series_D;
  public ArrayList<TimeSeries> series_sorted;

  public ArrayList<ArrayList<ArrayList<Double>>> PBAD_features;
  private double threshold;

  public PBAD() {}

  public PBAD(TimeSeriesMulDim MulT, int window_size, int window_incr) throws Exception {
    this.MulT = MulT;
    this.window_size = window_size / bin_size;
    this.window_incr = window_incr / bin_size;
    preprocess();
    fit();
  }

  @Override
  public void run() {
    preprocess();
    ArrayList<Double> score_or = new ArrayList<Double>();
    try {
      score_or = fit();
    } catch (Exception e) {
      e.printStackTrace();
    }
    this.score=new ArrayList<Double>();
    ArrayList<Double> binall=new ArrayList<Double>();
    int lenB=(int) Math.ceil((double) MulT.getLength() / bin_size);
    for(int i=0;i<lenB;i++){
      int end=i/this.window_incr;
      int start=(i-this.window_size)/this.window_incr;
      end=Math.min(score_or.size(), end);
      start=Math.max(0, start);
      double tempsum=0.0;
      int count=0;
      if(start>=end&&start==score_or.size()){
        binall.add(score_or.get(score_or.size()-1));
      }else if(start>=end&&start==0){
        binall.add(score_or.get(0));
      }else{
      for (int j = start; j < end; j++) {
          tempsum += score_or.get(j);
          count++;
        }
        binall.add((double) (tempsum / count));

      }
    }
    ArrayList<Double> joinall=new ArrayList<Double>();
    for(int i=0;i<MulT.getLength();i++){
      int end=(i+1)/this.bin_size;
      int start=i/this.bin_size;
      end=Math.min(binall.size(), end);
      start=Math.max(0, start);
      double tempsum=0.0;
      int count=0;
      if(start==end){
        joinall.add(binall.get(start));
      }else{
        for (int j = start; j < end; j++) {
          tempsum += binall.get(j);
          count++;
        }
        joinall.add((double) (tempsum / count));
      }
    }
    joinall=MaxMinScaler(joinall);
    this.score.addAll(joinall);
    ArrayList<SubseqScore> all_score = new ArrayList<SubseqScore>();
    for (int i = 0; i < score_or.size(); i++) {
      SubseqScore s = new SubseqScore(i * window_incr * bin_size, score_or.get(i));
      all_score.add(s);
    }
    Collections.sort(all_score);
    Collections.reverse(all_score);
    int top_k = (int) Math.ceil(threshold * all_score.size());
    for (int i = 0; i < Math.min(top_k, all_score.size()); i++) {
      int index = all_score.get(i).getIndex();
      int tt=window_size * bin_size + index;
      for (int j = index; j < Math.min(tt, MulT.getLength()); j++) {
        TimePointMulDim tpm = (TimePointMulDim) MulT.getTimeseries().get(j);
        tpm.setIs_anomaly(IS_ANOMALY.TRUE);
      }
    }
  }
  public void evaluate(double score_threshold) {
    MulT.clear();
    int th_num=(int)(score_threshold*score.size());
    if(th_num==0) return;
    ArrayList<SubseqScore> all_score = new ArrayList<SubseqScore>();
    for (int i = 0; i < score.size(); i++) {
      SubseqScore s = new SubseqScore(i, score.get(i));
      all_score.add(s);
    }
    Collections.sort(all_score);
    Collections.reverse(all_score);
    double th_value=all_score.get(th_num-1).getScore();
    for (int i = 0; i < all_score.size(); i++) {
      if(all_score.get(i).getScore()>=th_value){
        int index=all_score.get(i).getIndex();
        TimePointMulDim tpm = (TimePointMulDim) MulT.getTimeseries().get(index);
        tpm.setIs_anomaly(IS_ANOMALY.TRUE);
      }
    }
  }

  private ArrayList<PBADScore> drop_score(ArrayList<PBADScore> all_score, int index_list) {
    ArrayList<PBADScore> new_all_score = new ArrayList<PBADScore>();
    new_all_score.addAll(all_score);
    ArrayList<PBADScore> drop_score = new ArrayList<PBADScore>();
    for (int i = Math.max(index_list - window_size, 0);
         i < Math.min(index_list + window_size, all_score.size());
         i++) {
      drop_score.add(new_all_score.get(i));
    }
    new_all_score.removeAll(drop_score);
    return new_all_score;
  }
  @Override
  /** window_size: the subsequence length window_incr: windows overlaping rate */
  public void init(Map<String, Object> args, TimeSeriesMulDim timeseries) {
    this.MulT = timeseries;
    this.bin_size = (int) args.get("bin_size");
    this.window_size = (int) Math.ceil(((double) (int) args.get("window_size") / bin_size));
    this.window_incr = (int) Math.ceil(((double) (int) args.get("window_incr") / bin_size));
    this.max_feature=(int)args.get("max_feature");
    this.threshold = (double) args.get("threshold");
    MulT.clear();
  }

  public ArrayList<Double> getScore(){
    return this.score;
  }

  public double getMax(ArrayList<Double> a){
    double max=Collections.max(a);
    return max;
  }
  public double getMin(ArrayList<Double> a){
    double min=Collections.min(a);
    return min;
  }

  public ArrayList<Double> MaxMinScaler(ArrayList<Double> a){
    double max=getMax(a);
    double min=getMin(a);
    for(int i=0;i<a.size();i++){
      a.set(i,(double)((a.get(i)-min)/(max-min)));
    }
    return a;
  }
  public void preprocess() {
    int lenT = MulT.getLength();
    ArrayList<TimeSeries> series_list = new ArrayList<TimeSeries>();
    ArrayList<TimePointMulDim> mult = MulT.getTimeseries();
    for (int i = 0; i < MulT.getDim(); i++) {
      TimeSeries T = new TimeSeries();
      for (int j = 0; j < mult.size(); j++) {
        double value = mult.get(j).getObsVal()[i]; // time j dim i
        T.addPoint(new TimePoint(j, value));
      }
      series_list.add(T);
    }
    preprocessed_series_D = new ArrayList<ArrayList<TimeSeries>>();
    preprocessed_series_UD = new ArrayList<ArrayList<TimeSeries>>();
    for (int k = 0; k < MulT.getDim(); k++) {
      TimeSeries T = series_list.get(k);
     
      //min-max scaling
      double max=DataHandler.calcMax(T);
      double min=DataHandler.calcMin(T);
      for (int i = 0; i < lenT; i++) {
    	  if(max==min) {
    		  T.getTimePoint(i).setObsVal(max/max);
    	  }else {
    		  T.getTimePoint(i).setObsVal((double)(T.getTimePoint(i).getObsVal()-min)/(max-min));
    	  }
    	  //T.getTimePoint(i).setObsVal((double)(T.getTimePoint(i).getObsVal()-mean)/std);
      }
      // remove extreme values
      double mean = DataHandler.calcMean(T);
      double std = DataHandler.calcStd(T);
      for (int i = 0; i < lenT; i++) {
        if (T.getTimePoint(i).getObsVal() > (mean + 3 * std)) {
          T.getTimePoint(i).setObsVal(mean + 3 * std);
        } else if (T.getTimePoint(i).getObsVal() < (mean - 3 * std)) {
          T.getTimePoint(i).setObsVal(mean - 3 * std);
        }
      }
      // additional scaling  all<1
      /*for(int i=0;i<lenT;i++) {
      	double temp=Math.min(1, T.getTimePoint(i).getObsVal());
      	T.getTimePoint(i).setObsVal(temp);
      }*/
      // binning
      TimeSeries series_binned = new TimeSeries();
      if (bin_size > 1) {
        int nn = (int) Math.ceil((double) lenT / bin_size);
        for (int i = 0; i < nn; i++) {
          double sum = 0;
          if (bin_size * (i + 1) <= lenT) {
            for (int j = bin_size * i; j < bin_size * (i + 1); j++) {
              sum += T.getTimePoint(j).getObsVal();
            }
            sum = (double) (sum / bin_size);
          } else {
            for (int j = bin_size * i; j < lenT; j++) {
              sum += T.getTimePoint(j).getObsVal();
            }
            sum = (double) (sum / (lenT - bin_size * i));
          }

          TimePoint p = new TimePoint(i, sum);
          series_binned.addPoint(p);
        }
      } else {
        series_binned.addSubPointsToend(T);
      }

      // discretizing: binning with width
      int lenB = series_binned.getLength();
      double[] datas = new double[lenB];
      double[] sorted_datas = new double[lenB];
      for (int i = 0; i < lenB; i++) {
        datas[i] = series_binned.getTimePoint(i).getObsVal();
        sorted_datas[i] = series_binned.getTimePoint(i).getObsVal();
      }
      Arrays.sort(sorted_datas);
      double gap =
          (double) (sorted_datas[sorted_datas.length - 1] - sorted_datas[0]) / alphabet_size;
      double[] bin_edges = new double[alphabet_size + 1];
      double[] discretized_alphabet_values = new double[alphabet_size + 1];
      // segment
      for (int i = 0; i < alphabet_size; i++) {
        bin_edges[i] = sorted_datas[0] + i * gap;
      }
      bin_edges[alphabet_size] = sorted_datas[sorted_datas.length - 1];
      for (int i = 0; i < alphabet_size + 1; i++) {
        discretized_alphabet_values[i] = Double.valueOf(String.format("%.2f", bin_edges[i]));
        if (discretized_alphabet_values[i] == -0.0) {
          discretized_alphabet_values[i] = 0.0;
        }
      }
      // rebuild
      int[] value_ids = new int[lenB];
      for (int i = 0; i < lenB; i++) {
        for (int j = 0; j < alphabet_size + 1; j++) {
          if (j != alphabet_size && datas[i] >= bin_edges[j] && datas[i] < bin_edges[j + 1]) {
            value_ids[i] = j;
            break;
          }
          if (j == alphabet_size && datas[i] == bin_edges[j]) value_ids[i] = j;
        }
      }
      TimeSeries series_discrete = new TimeSeries();
      for (int i = 0; i < lenB; i++) {
        TimePoint p = new TimePoint(i, discretized_alphabet_values[value_ids[i]]);
        series_discrete.addPoint(p);
      }

      // window
      series_D = fast_divide_series_into_windows(series_discrete);
      series = fast_divide_series_into_windows(series_binned);

      preprocessed_series_D.add(series_D);
      preprocessed_series_UD.add(series);
    }
  }

  public ArrayList<Double> fit() throws Exception {

    PBAD_features = new ArrayList<ArrayList<ArrayList<Double>>>();
    for (int k = 0; k < preprocessed_series_D.size(); k++) {
      System.out.println("Dim:" + (k + 1));
      series_D = preprocessed_series_D.get(k);
      series = preprocessed_series_UD.get(k);

      int len = series_D.size();
      // pattern minning
      ArrayList<Pattern> IS_patterns = mine_non_redundant_itemsets();
      ArrayList<Pattern> SQ_patterns = mine_non_redundant_sequential_patterns();

      // remove sequential patterns that do not have the necessary min length

      if (sequential_minlength > 1 && SQ_patterns.size() > 0) {
        ArrayList<Pattern> New_SQ_patterns = new ArrayList<Pattern>();
        for (int i = 0; i < SQ_patterns.size(); i++) {
          if (SQ_patterns.get(i).decoded_points.length > sequential_minlength) {
            New_SQ_patterns.add(SQ_patterns.get(i));
          }
        }
        SQ_patterns = New_SQ_patterns;
      }

      pattern_dct = new LinkedHashMap<String, ArrayList<Pattern>>();
      pattern_dct.put("itemset", IS_patterns);
      pattern_dct.put("sequential", SQ_patterns);

      // construct the features
      series_sorted = new ArrayList<TimeSeries>();
      for (int i = 0; i < series.size(); i++) {
        TimeSeries t = new TimeSeries();
        for (int j = 0; j < series.get(i).getLength(); j++) {
          double temp = series.get(i).getTimePoint(j).getObsVal();
          long time = series.get(i).getTimePoint(j).getTimestamp();
          TimePoint p = new TimePoint(time, temp);
          t.addPoint(p);
        }
        series_sorted.add(t);
      }
      for (int i = 0; i < series_sorted.size(); i++) {
        Collections.sort(series_sorted.get(i).getTimeseries());
      }

      double[][] features = make_pattern_based_features(IS_patterns, "itemset");

      ArrayList<ArrayList<Double>> f = new ArrayList<ArrayList<Double>>();
      for (int i = 0; i < features.length; i++) {
        ArrayList<Double> d = new ArrayList<Double>();
        for (int j = 0; j < features[i].length; j++) {
          d.add(features[i][j]);
        }
        f.add(d);
      }
      PBAD_features.add(f);
      features = make_pattern_based_features(SQ_patterns, "sequential");
      f = new ArrayList<ArrayList<Double>>();
      for (int i = 0; i < features.length; i++) {
        ArrayList<Double> d = new ArrayList<Double>();
        for (int j = 0; j < features[0].length; j++) {
          d.add(features[i][j]);
        }
        f.add(d);
      }
      PBAD_features.add(f);
    }
    // features aggregation
    ArrayList<ArrayList<Double>> features_con = new ArrayList<ArrayList<Double>>();
    features_con.addAll(PBAD_features.get(0));
    for (int i = 1; i < PBAD_features.size(); i++) {
      for (int j = 0; j < PBAD_features.get(i).size(); j++) {
        features_con.get(j).addAll(PBAD_features.get(i).get(j));
      }
    }

    // remove zero feature
    ArrayList<Integer> ix_nonzero = new ArrayList<Integer>();
    for (int i = 0; i < features_con.get(0).size(); i++) {
      double sum = 0;
      for (int j = 0; j < series.size(); j++) {
        sum += features_con.get(j).get(i);
      }

      if (sum != 0) {
        ix_nonzero.add(i);
      }
    }
    double[][] features_con_nonzero = new double[series.size()][ix_nonzero.size()];
    for (int i = 0; i < ix_nonzero.size(); i++) {
      int j = ix_nonzero.get(i);
      ArrayList<Double> c = new ArrayList<Double>();
      for (int t = 0; t < series.size(); t++) {
        features_con_nonzero[t][i] = features_con.get(t).get(j);
      }
    }

    // train the classifier
    int nf = ix_nonzero.size();
    System.out.println();
    System.out.println("training classifier");
    System.out.println("size=" + features_con_nonzero.length + "," + nf);

    int attrnum=Math.min(max_feature, nf);
    ArrayList<Integer> randomattr=new ArrayList<Integer>();
    feature_flag=false;
    if(max_feature<nf) {
    	feature_flag=true;
    	randomattr=samplewithoutreplace(nf);
    }
    
    // build dataset for weka isolationforest
    ArrayList<Attribute> attributes = new ArrayList<>();
    for (int i = 0; i < attrnum; i++) {
      attributes.add(new Attribute(Integer.toString(i + 1)));
    }
    ArrayList class_values = new ArrayList(2);
    class_values.add("N");
    class_values.add("A");
    attributes.add(new Attribute("Class", class_values));

    Instances data = new Instances("features", attributes, 0);
    data.setClassIndex(data.numAttributes() - 1);
    if(feature_flag) {
    	for (int i = 0; i < features_con_nonzero.length; i++) {
    	      Instance instance = new DenseInstance(attributes.size());
    	      for (int j = 0; j <attrnum; j++) {
    	        instance.setValue(j, features_con_nonzero[i][randomattr.get(j)]);
    	      }
    	      instance.setDataset(data);
    	      data.add(instance);
    	    }
    }else {
    	for (int i = 0; i < features_con_nonzero.length; i++) {
    	      Instance instance = new DenseInstance(attributes.size());
    	      for (int j = 0; j < nf; j++) {
    	        instance.setValue(j, features_con_nonzero[i][j]);
    	      }
    	      instance.setDataset(data);
    	      data.add(instance);
    	    }
    }
    
    IsolationForest randomForest = new IsolationForest();
    randomForest.setNumTrees(500);
    randomForest.setSubsampleSize(1500);
    randomForest.buildClassifier(data);
    //String str="";
    ArrayList<Double> score = new ArrayList<Double>();
    for (int i = 0; i < data.size(); ++i) {
      Instance inst = data.get(i);
      final double[] distributionForInstance = randomForest.distributionForInstance(inst);
      score.add(distributionForInstance[0]);
      //System.out.print(distributionForInstance[0]+",");
      //str+=distributionForInstance[0]+",";
    }
    /*try {
        BufferedWriter out = new BufferedWriter(new FileWriter("D:/runoob.txt"));
        out.write(str);
        out.close();
    } catch (IOException e) {
    }*/
    // System.out.println();
    return score;
  }

  private ArrayList<Integer> samplewithoutreplace(int nf) {
	  Random rand = new Random(0);
	    ArrayList<Integer> collection = new ArrayList<>();
	    ArrayList<Integer> selected = new ArrayList<>();
	    for (int i = 0; i <max_feature; i++) {
	      int flag = 0;
	      int temp = 0;
	      while (flag == 0) {
	        temp = rand.nextInt(nf);
	        flag = 1;
	        for (int j = 0; j < selected.size(); j++) {
	          if (selected.get(j) == temp) {
	            flag = 0;
	          }
	        }
	      }
	      selected.add(temp);
	      collection.add(temp);
	    }
	    return collection;
}

// Make the pattern-based features
  private double[][] make_pattern_based_features(ArrayList<Pattern> patterns, String pattern_type) {
    double par_lambda = 2;
    int n = series.size();
    int npa = patterns.size();
    double[][] features = new double[n][npa];
    if (pattern_type.equals("itemset")) {
      // presort the patterns and the data
      if (distance_formula == 1) {
        for (int i = 0; i < n; i++) {
          for (int j = 0; j < npa; j++) {
            features[i][j] =
                compute_distance_weighted_similarity_formula1(
                    series_sorted.get(i).getTimeseries(), patterns.get(j), par_lambda);
          }
        }
      } else {
        for (int i = 0; i < n; i++) {
          for (int j = 0; j < npa; j++) {
            features[i][j] =
                compute_distance_weighted_similarity_formula2(
                    series_sorted.get(i).getTimeseries(), patterns.get(j), par_lambda);
          }
        }
      }

    } else {
      if (distance_formula == 1) {
        for (int i = 0; i < n; i++) {
          for (int j = 0; j < npa; j++) {
            features[i][j] =
                compute_distance_weighted_similarity_formula1(
                    series.get(i).getTimeseries(), patterns.get(j), par_lambda);
          }
        }
      } else {
        for (int i = 0; i < n; i++) {
          for (int j = 0; j < npa; j++) {
            features[i][j] =
                compute_distance_weighted_similarity_formula2(
                    series.get(i).getTimeseries(), patterns.get(j), par_lambda);
          }
        }
      }
    }
    return features;
  }

  private double compute_distance_weighted_similarity_formula1(
      ArrayList<TimePoint> data_window, Pattern pattern, double par_lambda) {
    int N = data_window.size();
    int M = pattern.decoded_points.length;
    double simil;
    // pattern with a single item
    double d_min = 999999;
    if (M == 1) {
      for (int i = 0; i < N; i++) {
        double d_new =
            Math.sqrt(
                Math.pow(
                    Math.abs(data_window.get(i).getObsVal() - pattern.decoded_points[0]),
                    par_lambda));
        if (d_new < d_min) {
          d_min = d_new;
        }
      }
      simil = Math.max(0.0, 1 - d_min);
      return simil;
    } else {
      int win = N - M + 1;
      double[][] matrix = new double[N + 1][M + 1];
      for (int i = 0; i < N + 1; i++) {
        for (int j = 0; j < M + 1; j++) {
          if (j > 0) {
            matrix[i][j] = 999999;
          } else {
            matrix[i][j] = 0;
          }
        }
      }
      // fill the matrix
      double d_best = 999999;
      for (int i = 0; i < M; i++) {
        int ii = i + 1;
        for (int j = i; j < i + win; j++) {
          int jj = j + 1;
          double d_new =
              Math.pow(
                  Math.abs(data_window.get(i).getObsVal() - pattern.decoded_points[i]), par_lambda);
          d_best = Math.min(matrix[j][ii], d_new + matrix[j][i]);
          matrix[jj][ii] = d_best;
        }
      }
      double dist = Math.sqrt(matrix[N][M]);
      simil = Math.max(0.0, 1.0 - dist / M);
      return simil;
    }
  }

  private double compute_distance_weighted_similarity_formula2(
      ArrayList<TimePoint> data_window, Pattern pattern, double par_lambda) {
    int N = data_window.size();
    int M = pattern.decoded_points.length;
    double simil;
    // pattern with a single item
    double d_min = 999999;
    if (M == 1) {
      for (int i = 0; i < N; i++) {
        double d_new =
            Math.pow(
                Math.abs(data_window.get(i).getObsVal() - pattern.decoded_points[0]),
                1.0 / par_lambda);
        if (d_new < d_min) {
          d_min = d_new;
        }
      }
      simil = Math.max(0.0, 1 - d_min);
      return simil;
    } else {
      int win = N - M + 1;
      double[][] matrix = new double[N + 1][M + 1];
      for (int i = 0; i < N + 1; i++) {
        for (int j = 0; j < M + 1; j++) {
          if (j > 0) {
            matrix[i][j] = 999999;
          } else {
            matrix[i][j] = 0;
          }
        }
      }
      // fill the matrix
      double d_best = 999999;
      for (int i = 0; i < M; i++) {
        int ii = i + 1;
        double d_new =
            Math.pow(
                Math.abs(data_window.get(i).getObsVal() - pattern.decoded_points[i]),
                1.0 / par_lambda);
        for (int j = i; j < i + win; j++) {
          int jj = j + 1;
          d_best = Math.min(matrix[j][ii], d_new + matrix[j][i]);
          matrix[jj][ii] = d_best;
        }
      }
      double dist = matrix[N][M];

      simil = Math.max(0.0, 1.0 - dist / M);
      return simil;
    }
  }

  public ArrayList<TimeSeries> fast_divide_series_into_windows(TimeSeries series) {
    int len = series.getLength();
    int nw = (int) (Math.ceil((double) (len - window_size) / window_incr) + 1);

    ArrayList<TimeSeries> windowed_series = new ArrayList<TimeSeries>();
    for (int i = 0; i < nw; i++) {
      int w = i * window_incr;
      if (series.getLength() < w + window_size) {
        TimeSeries temp = series.getSubPoints(w, series.getLength() - 1);
        for (int j = series.getLength(); j < w + window_size; j++) {
          TimePoint p = new TimePoint(j, 0);
          temp.addPoint(p);
        }
        windowed_series.add(temp);
      } else {
        windowed_series.add(series.getSubPoints(w, w + window_size - 1));
      }
    }
    return windowed_series;
  }

  public ArrayList<Pattern> mine_non_redundant_itemsets() {
    ArrayList<ArrayList<Integer>> encoded_data = data_encode_as_int();
    ArrayList<ArrayList<Integer>> encoded_data_unique = new ArrayList<ArrayList<Integer>>();
    for (int i = 0; i < encoded_data.size(); i++) {
      Collections.sort(encoded_data.get(i));
      int index = 0;
      int len = encoded_data.get(i).size();
      ArrayList<Integer> list = new ArrayList<Integer>();
      for (int j = 0; j < len; j++) {
        if (!list.contains(encoded_data.get(i).get(j))) {
          list.add(encoded_data.get(i).get(j));
        }
      }
      encoded_data_unique.add(list);
    }

    decode_dct = new LinkedHashMap<Integer, Double>();
    for (double key : encode_dct.keySet()) {
      decode_dct.put(encode_dct.get(key), key);
    }
    ArrayList<Pattern> pattern_list = mine_maximal_itemsets(encoded_data_unique);
    Collections.sort(pattern_list);
    Collections.reverse(pattern_list);
    // remove the overlapping patterns with Jaccard
    ArrayList<Integer> keep_ids =
        remove_overlapping_patterns_jaccard(pattern_list, encoded_data, "itemset");
    ArrayList<Pattern> non_redundant_pattern_list = new ArrayList<Pattern>();
    for (int i = 0; i < keep_ids.size(); i++) {
      non_redundant_pattern_list.add(pattern_list.get(keep_ids.get(i)));
    }
    System.out.println("length after remove:" + non_redundant_pattern_list.size());
    // decode the patterns
    ArrayList<Pattern> decoded_pattern_list =
        patterns_decode_from_int(non_redundant_pattern_list, decode_dct);
    return decoded_pattern_list;
  }

  private ArrayList<Pattern> mine_non_redundant_sequential_patterns() {
    // data = np.around(data, 10)
    ArrayList<ArrayList<Integer>> encoded_data = data_encode_as_int();
    decode_dct = new LinkedHashMap<Integer, Double>();
    for (double key : encode_dct.keySet()) {
      decode_dct.put(encode_dct.get(key), key);
    }
    ArrayList<Pattern> pattern_list = mine_maximal_sequential_patterns(encoded_data);
    Collections.sort(pattern_list);
    Collections.reverse(pattern_list);

    // remove the overlapping patterns with Jaccard
    ArrayList<Integer> keep_ids =
        remove_overlapping_patterns_jaccard(pattern_list, encoded_data, "sequential");
    ArrayList<Pattern> non_redundant_pattern_list = new ArrayList<Pattern>();
    for (int i = 0; i < keep_ids.size(); i++) {
      non_redundant_pattern_list.add(pattern_list.get(keep_ids.get(i)));
    }
    System.out.println("length after remove:" + non_redundant_pattern_list.size());
    ArrayList<Pattern> decoded_pattern_list =
        patterns_decode_from_int(non_redundant_pattern_list, decode_dct);
    return decoded_pattern_list;
  }

  private ArrayList<Pattern> patterns_decode_from_int(
      ArrayList<Pattern> pattern_list, HashMap<Integer, Double> decode_dct) {
    ArrayList<Pattern> decoded_pattern_list = new ArrayList<Pattern>();
    for (Pattern p : pattern_list) {
      int[] s = p.points;
      double[] new_s = new double[s.length];
      for (int i = 0; i < s.length; i++) {
        new_s[i] = decode_dct.get(s[i]);
      }
      p.decoded_points = new_s;
      decoded_pattern_list.add(p);
    }
    return decoded_pattern_list;
  }

  // Remove the overlapping patterns with Jaccard
  private ArrayList<Integer> remove_overlapping_patterns_jaccard(
      ArrayList<Pattern> pattern_list,
      ArrayList<ArrayList<Integer>> encoded_data,
      String pattern_type) {
    double[][] covers = compute_pattern_cover(pattern_list, encoded_data, pattern_type);
    // precompute the cover sets
    ArrayList<ArrayList<Integer>> cover_set = new ArrayList<ArrayList<Integer>>();
    for (int i = 0; i < covers.length; i++) {
      ArrayList<Integer> cover_ids = new ArrayList<Integer>();
      for (int j = 0; j < covers[i].length; j++) {
        if (covers[i][j] == 1) {
          cover_ids.add(j);
        }
      }
      if (cover_ids.size() != 0) {
        cover_set.add(cover_ids);
      }
    }
    // remove overlapping
    ArrayList<Integer> keep_ids = new ArrayList<Integer>();
    for (int i = 0; i < covers.length; i++) {
      boolean keep = true;
      for (int j = i + 1; j < covers.length; j++) {
        int intersect = 0;
        double js = 0;
        for (int t = 0; t < cover_set.get(j).size(); t++) {
          if (cover_set.get(i).contains(cover_set.get(j).get(t))) {
            intersect++;
          }
        }
        if (cover_set.get(i).size() + cover_set.get(j).size() == 0) {
          js = 0;
        } else {
          js = (double) intersect / (cover_set.get(i).size() + cover_set.get(j).size() - intersect);
        }
        if (js > jaccard_threshold) {
          keep = false;
          break;
        }
      }
      if (keep) keep_ids.add(i);
    }
    return keep_ids;
  }

  private double[][] compute_pattern_cover(
      ArrayList<Pattern> pattern_list,
      ArrayList<ArrayList<Integer>> encoded_data,
      String pattern_type) {
    int N = pattern_list.size();
    int M = encoded_data.size();
    double[][] covers = new double[N][M];
    if (pattern_type.equals("itemset")) {
      for (int j = 0; j < N; j++) {
        for (int i = 0; i < M; i++) {
          covers[j][i] = compute_cover_itemset(pattern_list.get(j), encoded_data.get(i));
        }
      }

    } else {
      for (int j = 0; j < N; j++) {
        for (int i = 0; i < M; i++) {
          covers[j][i] = compute_cover_sequential(pattern_list.get(j), encoded_data.get(i));
        }
      }
    }
    return covers;
  }

  private double compute_cover_itemset(Pattern pattern, ArrayList<Integer> datap) {
    for (int v : pattern.points) {
      int flag = 0;
      for (int w : datap) {
        if (w == v) flag = 1;
      }
      if (flag == 0) return 0.0;
    }
    return 1.0;
  }

  private double compute_cover_sequential(Pattern pattern, ArrayList<Integer> datap) {
    int i = 0, idx = 0;
    for (int v : pattern.points) {
      int flag = 0;
      for (i = idx; i < datap.size(); i++) {
        if (datap.get(i) == v) {
          flag = 1;
          idx = i + 1;
          break;
        }
      }
      if (flag == 0) return 0.0;
    }
    return 1.0;
  }

  private ArrayList<ArrayList<Integer>> data_encode_as_int() {
    // Encode the double/int data as ints
    TimeSeries l = new TimeSeries();
    ArrayList<Double> se = new ArrayList<Double>();
    ArrayList<ArrayList<Integer>> encoded_data = new ArrayList<ArrayList<Integer>>();
    for (int i = 0; i < series_D.size(); i++) {
      l.addSubPointsToend(series_D.get(i));
    }
    int i = 0;
    int flag = 0;
    double temp = 0;
    while (i < l.getLength()) {
      temp = l.getTimePoint(i).getObsVal();
      flag = 0;
      for (int j = 0; j < se.size(); j++) {
        if (se.get(j) == temp) {
          flag = 1;
          break;
        }
      }
      if (flag == 0) {
        if (temp == -0.0) {
          temp = 0.0;
        }
        se.add(temp);
      }
      i++;
    }
    Collections.sort(se);
    encode_dct = new LinkedHashMap<Double, Integer>();
    for (int j = 0; j < se.size(); j++) {
      encode_dct.put(se.get(j), j + 1);
    }
    int[] encoded_1 = new int[l.getLength()];
    for (int j = 0; j < l.getLength(); j++) {
      if (encode_dct.get(l.getTimePoint(j).getObsVal()) == null) {
        System.out.println();
      }
      int vis = encode_dct.get(l.getTimePoint(j).getObsVal());
      encoded_1[j] = vis;
    }

    for (int j = 0; j < series_D.size(); j++) {
      ArrayList<Integer> en = new ArrayList<Integer>();
      for (int t = 0; t < window_size; t++) {
        // encoded_data[j][t]=encoded_1[j*window_size+t];
        en.add(encoded_1[j * window_size + t]);
      }
      encoded_data.add(en);
    }
    return encoded_data;
  }

  public ArrayList<Pattern> mine_maximal_itemsets(
      ArrayList<ArrayList<Integer>> encoded_data_unique) {
    File input_file_spmf = data_to_spmf_file_format_transaction_db(encoded_data_unique);
    File output_file_spmf = null;
    try {
      output_file_spmf = File.createTempFile("spmf_charm_mfi_output", ".txt", new File("temp/"));
      output_file_spmf.deleteOnExit();
    } catch (IOException e) {
      e.printStackTrace();
    }
    run_spmf_algorithm("Charm_MFI", input_file_spmf.getName(), output_file_spmf.getName());
    ArrayList<Pattern> pattern_list =
        spmf_output_to_patterns(output_file_spmf.getName(), "itemset");
    input_file_spmf.delete();
    output_file_spmf.delete();
    return pattern_list;
  }

  private ArrayList<Pattern> mine_maximal_sequential_patterns(
      ArrayList<ArrayList<Integer>> encoded_data) {
    File input_file_spmf = data_to_spmf_file_format_sequence_db(encoded_data);
    File output_file_spmf = null;
    try {
      output_file_spmf = File.createTempFile("spmf_maxsp_output", ".txt", new File("temp/"));
      output_file_spmf.deleteOnExit();
    } catch (IOException e) {
      e.printStackTrace();
    }
    /* BufferedReader reader = null;
    ArrayList<String> spmf_lines=new ArrayList<String>();
    try {
        reader = new BufferedReader(new FileReader(input_file_spmf));
        String tempString = null;
        int line = 1;
        while ((tempString = reader.readLine()) != null) {
            spmf_lines.add(tempString);
            line++;
        }
        reader.close();
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e1) {
            }
        }
    }
    ArrayList<String> spmf_lines2=new ArrayList<String>();
    File file = new File("temp/tmpzgqh4togspmf_sdb.txt");
    try {
        reader = new BufferedReader(new FileReader(file));
        String tempString = null;
        int line = 1;
        while ((tempString = reader.readLine()) != null) {
            spmf_lines2.add(tempString);
            line++;
        }
        reader.close();
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e1) {
            }
        }
    }
    for(int i=0;i<spmf_lines.size();i++) {
    	if(!spmf_lines.get(i).equals(spmf_lines2.get(i))) {
    		System.out.println(i );
    		System.out.println(spmf_lines.get(i));
    		System.out.println(spmf_lines2.get(i));
    	}
    }*/
    run_spmf_algorithm("MaxSP", input_file_spmf.getName(), output_file_spmf.getName());
    ArrayList<Pattern> pattern_list =
        spmf_output_to_patterns(output_file_spmf.getName(), "sequential_pattern");
    input_file_spmf.delete();
    output_file_spmf.delete();
    return pattern_list;
  }

  // Store the data in a transaction database to be used by spmf
  public File data_to_spmf_file_format_transaction_db(
      ArrayList<ArrayList<Integer>> encoded_data_unique) {
    String context = "";
    File f = null;
    for (int i = 0; i < encoded_data_unique.size(); i++) {
      for (int j = 0; j < encoded_data_unique.get(i).size(); j++) {
        if (j != window_size - 1) context += encoded_data_unique.get(i).get(j) + " ";
        else context += encoded_data_unique.get(i).get(j);
      }
      context += "\n";
    }
    // FileHandler fh=new FileHandler();
    // String filename=fh.createTempFile("spmf_sdb",context);
    try {
      f = File.createTempFile("spmf_sdb", ".txt", new File("temp/"));
      System.out.println("File name: " + f.getName());
      BufferedWriter bw = new BufferedWriter(new FileWriter(f));
      bw.write(context);
      bw.close();
      f.deleteOnExit();

    } catch (Exception e) {
      e.printStackTrace();
    }
    return f;
  }

  private File data_to_spmf_file_format_sequence_db(ArrayList<ArrayList<Integer>> encoded_data) {
    String context = "";
    File f = null;
    for (int i = 0; i < encoded_data.size(); i++) {
      for (int j = 0; j < window_size; j++) {
        if (j != window_size - 1) context += encoded_data.get(i).get(j) + " -1 ";
        else context += encoded_data.get(i).get(j) + " ";
      }
      context += "-2\n";
    }
    // FileHandler fh=new FileHandler();
    // String filename=fh.createTempFile("spmf_sdb",context);
    try {
      f = File.createTempFile("spmf_sdb", ".txt", new File("temp/"));
      System.out.println("File name: " + f.getName());
      BufferedWriter bw = new BufferedWriter(new FileWriter(f));
      bw.write(context);
      bw.close();
      f.deleteOnExit();

    } catch (Exception e) {
      e.printStackTrace();
    }
    return f;
  }

  public ArrayList<Pattern> spmf_output_to_patterns(String output_name, String pattern_type) {
    /*Read the output of the SPMF file and return the found patterns

    SPMF itemset output:
    3 #SUP: 4
    1 3 #SUP: 3
    2 5 #SUP: 4
    2 3 5 #SUP: 3
    1 2 3 5 #SUP: 2

    SPMF sequential pattern output:
    4 -1 3 -1 2 -1 #SUP: 2
    5 -1 7 -1 3 -1 2 -1 #SUP: 2
    5 -1 1 -1 3 -1 2 -1 #SUP: 2
    where -1 is a separator
    */
    ArrayList<Pattern> pattern_list = new ArrayList<Pattern>();
    File file = new File("temp/" + output_name);
    BufferedReader reader = null;
    ArrayList<String> spmf_lines = new ArrayList<String>();
    try {
      reader = new BufferedReader(new FileReader(file));
      String tempString = null;
      int line = 1;
      while ((tempString = reader.readLine()) != null) {
        spmf_lines.add(tempString);
        line++;
      }
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e1) {
        }
      }
    }
    if (pattern_type.equals("itemset")) {
      pattern_list = parse_itemset_patterns(spmf_lines);
    } else {
      pattern_list = parse_sequential_patterns(spmf_lines);
    }
    return pattern_list;
  }

  // translate itemset output
  public ArrayList<Pattern> parse_itemset_patterns(ArrayList<String> spmf_lines) {
    ArrayList<Pattern> pattern_list = new ArrayList<Pattern>();
    for (int i = 0; i < spmf_lines.size(); i++) {
      int new_s =
          Integer.parseInt((spmf_lines.get(i).split(" "))[spmf_lines.get(i).split(" ").length - 1]);
      String[] new_pstr = spmf_lines.get(i).split("  ")[0].split(" ");
      int[] new_p = new int[new_pstr.length];
      for (int j = 0; j < new_pstr.length; j++) {
        new_p[j] = Integer.parseInt(new_pstr[j]);
      }
      Pattern p = new Pattern(new_p, new_s);
      pattern_list.add(p);
    }
    return pattern_list;
  }
  // translate sequential output
  private ArrayList<Pattern> parse_sequential_patterns(ArrayList<String> spmf_lines) {
    ArrayList<Pattern> pattern_list = new ArrayList<Pattern>();
    for (int i = 0; i < spmf_lines.size(); i++) {
      ;
      int new_s =
          Integer.parseInt((spmf_lines.get(i).split(" "))[spmf_lines.get(i).split(" ").length - 1]);
      String[] new_pstr =
          spmf_lines
              .get(i)
              .split("#")[0]
              .substring(0, spmf_lines.get(i).split("#")[0].length() - 1)
              .split("-1 ");
      int[] new_p = new int[new_pstr.length];
      for (int j = 0; j < new_pstr.length; j++) {
        new_p[j] = Integer.parseInt(new_pstr[j].substring(0, new_pstr[j].length() - 1));
      }
      Pattern p = new Pattern(new_p, new_s);
      pattern_list.add(p);
    }
    return pattern_list;
  }

  public void run_spmf_algorithm(String alg, String inputname, String outputname) {
    BufferedReader br = null;
    String inputfile = "temp/" + inputname;
    String outputfile = "temp/" + outputname;
    try {
      int min_sup = (int) (Constants.PBAD.RELATIVE_MINSUP * 100);
      String[] cmd = {
        "java",
        "-jar",
        "lib/spmf.jar",
        "run",
        alg,
        inputfile,
        outputfile,
        Integer.toString(min_sup) + '%'
      };

      for (int i = 0; i < cmd.length; i++) {}
      Process p = Runtime.getRuntime().exec(cmd);
      br = new BufferedReader(new InputStreamReader(p.getInputStream()));
      String line = null;
      StringBuilder sb = new StringBuilder();
      while ((line = br.readLine()) != null) {
        sb.append(line + "\n");
      }
      System.out.println(sb.toString());
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }
}

class Pattern implements Comparable<Pattern> {
  int[] points;
  double[] decoded_points;
  int support;

  public Pattern(int[] points, int support) {
    this.points = points;
    this.support = support;
  }

  public Pattern(double[] decoded_points, int support) {
    this.decoded_points = decoded_points;
    this.support = support;
  }

  @Override
  public int compareTo(Pattern o) {
    return this.support - o.support;
  }
}

class PBADScore implements Comparable<PBADScore> {
  int index;
  double score;

  public PBADScore(int index, double score) {
    super();
    this.index = index;
    this.score = score;
  }

  @Override
  public int compareTo(PBADScore o) {
    if (this.score > o.score) {
      return 1;
    } else if (this.score < o.score) {
      return -1;
    } else {
      return 0;
    }
  }
}

