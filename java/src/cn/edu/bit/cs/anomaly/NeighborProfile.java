package cn.edu.bit.cs.anomaly;

import cn.edu.bit.cs.anomaly.entity.TimeSeries;
import cn.edu.bit.cs.anomaly.util.Constants;
import cn.edu.bit.cs.anomaly.util.Constants.IS_ANOMALY;
import cn.edu.bit.cs.anomaly.util.DataHandler;
import cn.edu.bit.cs.anomaly.util.SubseqScore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Random;

/** @author dsq */
public class NeighborProfile implements UniDimAlgorithm {
  public TimeSeries timeseries;
  public ArrayList<TimeSeries> T_List;
  public ArrayList<TimeSeries> Subsample_List;
  public ArrayList<TimeSeries> Subsequnce_List;
  public ArrayList<Double> nprofile;
  public ArrayList<Double> score;
  public int n_nnballs = 100;
  public int batchsize;
  public int sub_len;
  public int max_sample;
  public int MIN;
  public ArrayList<NN_ball> list_of_nn_ball;
  public int top_k;

  public String scale="zscore";
  
  public NeighborProfile() {}

  public NeighborProfile(TimeSeries T, int batchsize, int sub_len, int max_sample) {
    this.batchsize = batchsize;
    this.sub_len = sub_len;
    this.max_sample = max_sample;
    this.MIN = Constants.NeighborProfile.MIN;
    fit();
    estimate_for_time_series();
  }

  @Override
  public void run() {
    fit();
    estimate_for_time_series();
    ArrayList<SubseqScore> all_score = new ArrayList<SubseqScore>();
    ArrayList<Double> joinall=new ArrayList<Double>();
    joinall.addAll(nprofile);
    for(int i=nprofile.size();i<timeseries.getLength();i++){
      joinall.add(nprofile.get(nprofile.size()-1));
    }
    joinall=MaxMinScaler(joinall);
    this.score=joinall;
    for (int i = 0; i < nprofile.size(); i++) {
      SubseqScore s = new SubseqScore(i, nprofile.get(i));
      all_score.add(s);
    }
    SubseqScore max_score = Collections.max(all_score);
    int max_index_begin = max_score.getIndex();
    int index_list = all_score.indexOf(max_score);
    for (int i = 0; i < Math.min(top_k, all_score.size()); i++) {
      for (int j = max_index_begin;
          j < Math.min(sub_len + max_index_begin, timeseries.getLength());
          j++) {
        timeseries.getTimePoint(j).setIs_anomaly(IS_ANOMALY.TRUE);
      }
      all_score = drop_score(all_score, index_list);
      // next max
      if (all_score.isEmpty()) {
        return;
      }
      max_score = Collections.max(all_score);
      max_index_begin = max_score.getIndex();
      index_list = all_score.indexOf(max_score);
    }
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

  @Override
  /**
   * max_sample: the size of subsample s {8,16,32,64} chose one sub_len: the subsequence length
   * batchsize: usually 10000 top_k: threshold for evaluation
   */
  public void init(Map<String, Object> args, TimeSeries timeseries) {
    this.timeseries = timeseries;
    this.batchsize = (int) args.get("batchsize");
    this.sub_len = (int) args.get("sub_len");
    this.max_sample = (int) args.get("max_sample");
    this.scale=(String)args.get("scale");
    this.top_k = (int) args.get("top_k");
    this.MIN = Constants.NeighborProfile.MIN;
    timeseries.clear();
  }

  public ArrayList<SubseqScore> drop_score(ArrayList<SubseqScore> all_score, int index_list) {
    ArrayList<SubseqScore> new_all_score = new ArrayList<SubseqScore>();
    new_all_score.addAll(all_score);
    ArrayList<SubseqScore> drop_score = new ArrayList<SubseqScore>();
    for (int i = Math.max(index_list - sub_len, 0);
        i < Math.min(index_list + sub_len, all_score.size());
        i++) {
      drop_score.add(new_all_score.get(i));
    }
    new_all_score.removeAll(drop_score);
    return new_all_score;
  }

  /** construct subsamples Q1... Qn calculate B(x; Qi) */
  public ArrayList<NN_ball> fit() {

    int lenT = timeseries.getLength();
    list_of_nn_ball = new ArrayList<NN_ball>();
    ArrayList<Integer> seq_idx = null;

    for (int i = 0; i < n_nnballs; i++) {
      seq_idx = sample_without_replacement(max_sample, lenT,i);
      // System.out.println(seq_idx);
      Subsample_List = DataHandler.build_subsequence(timeseries, seq_idx, sub_len);
      Subsample_List = DataHandler.Scale(Subsample_List, scale);
      double[][] distance_matrix = new double[seq_idx.size()][seq_idx.size()];
      for (int j = 0; j < seq_idx.size(); j++) {
        for (int t = 1; t < seq_idx.size() - j; t++) {
          distance_matrix[j][j + t] =
              DataHandler.calcEuclideanDistance(Subsample_List.get(j), Subsample_List.get(j + t));
          distance_matrix[j + t][j] = distance_matrix[j][j + t];
        }
      }
      ArrayList<Double> nn_distance = new ArrayList<Double>();
      for (int t = 0; t < seq_idx.size(); t++) {
        double min = MIN;
        distance_matrix[t][t] = min;
        for (int j = 0; j < seq_idx.size(); j++) {
          if (distance_matrix[t][j] < min) {
            min = distance_matrix[t][j];
          }
        }
        nn_distance.add(min);
      }

      NN_ball nnball = new NN_ball(Subsample_List, nn_distance);
      list_of_nn_ball.add(nnball);
    }
    return list_of_nn_ball;
  }

  /** construct Ti,m with batchsize */
  public ArrayList<Double> estimate_for_time_series() {
    ArrayList<Integer> seq_idy = null;
    nprofile = new ArrayList<Double>();
    for (int i = 0; i < timeseries.getLength() - sub_len; i += batchsize) {
      seq_idy = range(i, i + batchsize, timeseries.getLength() - sub_len);
      Subsequnce_List = DataHandler.build_subsequence(timeseries, seq_idy, sub_len);
      nprofile.addAll(estimate_for_subsequences(i, Subsequnce_List));
    }
    return nprofile;
  }

  /** calculate neighbor profile npi */
  private ArrayList<Double> estimate_for_subsequences(int num, ArrayList<TimeSeries> Y) {
    Y = DataHandler.Scale(Y, "zscore");
    System.out.println("Batch:" + num + " " + (Y.size() + num));

    ArrayList<TimeSeries> nnball_c;
    ArrayList<Double> nnball_r;
    NN_ball nn_ball = null;
    ArrayList<Double> profile = new ArrayList<Double>();
    double[] r_list = new double[Y.size()];
    double[][] cdist = new double[Y.size()][max_sample];
    for (int i = 0; i < Y.size(); i++) {
      TimeSeries y = Y.get(i);
      double[] nn_r = new double[list_of_nn_ball.size()]; // r(y,Qj)
      int[] nn_d_idx = new int[list_of_nn_ball.size()]; // nn(y,Qj)
      double[] nn_d = new double[list_of_nn_ball.size()]; // nnd(y,Qj)
      for (int j = 0; j < list_of_nn_ball.size(); j++) {
        nn_ball = list_of_nn_ball.get(j);
        nnball_c = nn_ball.Q;
        nnball_r = nn_ball.nn_distance;
        TimeSeries x = null;
        double min = MIN;
        int min_id = 0;
        int flag = 0;
        for (int t = 0; t < max_sample; t++) {
          x = nnball_c.get(t);
          cdist[i][t] = DataHandler.calcEuclideanDistance(y, x);
          if (cdist[i][t] < min) {
            min = cdist[i][t];
            min_id = t;
          }
        }
        nn_d[j] = min;
        nn_d_idx[j] = min_id;
        if (nnball_r.get(nn_d_idx[j]) < nn_d[j]) { // nnd(nn(y,Qj); Qj \ {nn(y,Qj)})
          nn_r[j] = nn_d[j];
        } else {
          nn_r[j] = nnball_r.get(nn_d_idx[j]);
        }
        r_list[i] += Math.log(nn_r[j]);
      }
      profile.add(r_list[i] / n_nnballs);
    }
    return profile;
  }

  public ArrayList<Integer> sample_without_replacement(int max_sample, int len,int it) {
    Random rand = new Random(it);
    int t = len - sub_len + 1;
    ArrayList<Integer> collection = new ArrayList<>();
    ArrayList<Integer> selected = new ArrayList<>();
    for (int i = 0; i < max_sample; i++) {
      int flag = 0;
      int temp = 0;
      while (flag == 0) {
        temp = rand.nextInt(t);
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

  public static ArrayList<Integer> range(int left, int right1, int right2) {
    ArrayList<Integer> id = new ArrayList<Integer>();
    int right = Math.min(right1, right2);
    for (int i = left; i < right; i++) {
      id.add(i);
    }
    return id;
  }
}

class NN_ball {
  public ArrayList<TimeSeries> Q;
  public ArrayList<Double> nn_distance;

  public NN_ball(ArrayList<TimeSeries> Q, ArrayList<Double> nn_distance) {
    this.Q = Q;
    this.nn_distance = nn_distance;
  }
}
