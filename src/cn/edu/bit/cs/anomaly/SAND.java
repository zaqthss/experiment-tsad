package cn.edu.bit.cs.anomaly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.jblas.ComplexDoubleMatrix;
import org.jblas.DoubleMatrix;
import org.jblas.Eigen;

import cn.edu.bit.cs.anomaly.entity.TimeSeries;
import cn.edu.bit.cs.anomaly.util.Constants.IS_ANOMALY;
import cn.edu.bit.cs.anomaly.util.clustering.Clusters;
import cn.edu.bit.cs.anomaly.util.clustering.KShape;
import cn.edu.bit.cs.anomaly.util.stamp.Mp;

public class SAND implements UniDimAlgorithm {
  public TimeSeries timeseries;
  public ArrayList<Double> ts;
  public int k = 6;
  public int init_length;
  public int batch_size;
  public int subsequence_length;
  public int pattern_length;
  public int current_time = 0;
  public double alpha = 0.5;

  public int overlaping_rate = 10;
  public int top_k = 10;

  public ArrayList<Scluster_in_Memory> sclusters;
  //public ArrayList<Scluster_in_Memory> new_sclusters_to_merge;
  public ArrayList<Double> new_clusters_dist;
  public ArrayList<Double> weights;
  public ArrayList<Double> nm_current_weight;
  public ArrayList<Double> time_decay;
  public ArrayList<DoubleMatrix> SS;

  public HashMap<Integer, ArrayList<Scluster>> to_add;
  public ArrayList<Scluster> new_c;

  public double mean = -1;
  public double std = -1;

  public SAND() {}

  public SAND(TimeSeries T, int init_length, int batch_size, int pattern_length) {
    super();
    this.timeseries = T;
    this.init_length = init_length;
    this.batch_size = batch_size;
    this.pattern_length = pattern_length;
    this.subsequence_length = 4 * pattern_length;
  }

  public void mainSAND() {
    initial();
    // SCORE ON THE INITIAL BATCH
    ArrayList<Double> score = compute_score();
    ArrayList<Double> all_score = new ArrayList<Double>();
    all_score.addAll(score);
    int batch = 0;
    while (current_time < ts.size()) {
      batch++;
      // System.out.println("batch:"+batch);
      run_next_batch();
      all_score.addAll(score);
      // System.out.println(score);
    }
  }

  @Override
  /**
   * init_length: size for init batch_size: size for a batch pattern_length: the subsequence length
   * top_k: threshold for evaluation
   */
  public void init(Map<String, Object> args, TimeSeries timeseries) {
    this.timeseries = timeseries;
    timeseries.clear();
    this.k = (int) args.get("k");
    this.batch_size = (int) args.get("batch_size");
    this.init_length = (int) args.get("batch_size");
    this.pattern_length = (int) args.get("pattern_length");
    this.top_k = (int) args.get("top_k");
    this.subsequence_length = 4 * this.pattern_length;
    this.current_time=0;
    this.mean=-1;
    this.std=-1;
  }

  @Override
  public void run() {
    initial();
    // SCORE ON THE INITIAL BATCH
    ArrayList<Double> score = compute_score();
    ArrayList<Score> all_score = new ArrayList<Score>();

    for (int i = 0; i < score.size(); i++) {
      Score s = new Score(i, score.get(i));
      all_score.add(s);
    }
    int batch = 0;
    while (current_time < ts.size()&&current_time<Math.min(current_time + batch_size, ts.size() - subsequence_length)) {
      batch++;
      System.out.println("batch:"+batch);
      
      run_next_batch();
      score = compute_score();
      // all_score.addAll(score);
      int begin = current_time - batch_size;
      for (int i = begin; i < score.size() + begin; i++) {
        Score s = new Score(i, score.get(i - begin));
        all_score.add(s);
      }
    }
    Score max_score = Collections.max(all_score);
    int max_index_begin = max_score.index;
    int index_list = all_score.indexOf(max_score);
    for (int i = 0; i < Math.min(top_k, all_score.size()); i++) {
      for (int j = max_index_begin;
          j < Math.min(pattern_length + max_index_begin, timeseries.getLength());
          j++) {
        timeseries.getTimePoint(j).setIs_anomaly(IS_ANOMALY.TRUE);
      }
      all_score = drop_score(all_score, index_list);
      if(all_score.size()==0) {
    	  break;
      }
      // next max
      max_score = Collections.max(all_score);
      max_index_begin = max_score.index;
      index_list = all_score.indexOf(max_score);
    }
  }

  private ArrayList<Score> drop_score(ArrayList<Score> all_score, int index_list) {
    ArrayList<Score> new_all_score = new ArrayList<Score>();
    new_all_score.addAll(all_score);
    ArrayList<Score> drop_score = new ArrayList<Score>();
    for (int i = Math.max(index_list - pattern_length, 0);
        i < Math.min(index_list + pattern_length, all_score.size());
        i++) {
      drop_score.add(new_all_score.get(i));
    }
    new_all_score.removeAll(drop_score);
    return new_all_score;
  }

  public void initial() {
    ts = new ArrayList<Double>();
    for (int i = 0; i < timeseries.getLength(); i++) {
      double temp = timeseries.getTimePoint(i).getObsVal();
      ts.add(temp);
    }
    HashMap<Integer, Scluster> scs = Kshape_subsequence(true);
    SS = new ArrayList<DoubleMatrix>();
    new_clusters_dist = new ArrayList<Double>();
    time_decay = new ArrayList<Double>();
    sclusters = new ArrayList<Scluster_in_Memory>();
    for (int i:scs.keySet()) {
      Scluster sc = scs.get(i);
      sclusters.add(new Scluster_in_Memory(sc.centroids, sc.idxs));
      new_clusters_dist.add(compute_mean_dist(sc.centroids, sc.idxs));
      set_initial_S(sc.cluster_subseq, i, sc.centroids);
    }

    current_time = init_length;
    set_normal_model();
    nm_current_weight = new ArrayList<Double>();
  }

  public void run_next_batch() {
    // Run K-Shape algorithm on the subsequences of the current batch
    HashMap<Integer, Scluster> scs = Kshape_subsequence(false);
    /*new_sclusters_to_merge = new ArrayList<Scluster_in_Memory>();
    for (int i = 0; i < scs.size(); i++) { 
      if(scs.containsKey(i)) {
    	  Scluster sc = scs.get(i);
    	  new_sclusters_to_merge.add(new Scluster_in_Memory(sc.centroids, sc.idxs));
      }    
    }*/

    to_add = new HashMap<Integer, ArrayList<Scluster>>();
    new_c = new ArrayList<Scluster>();
    for(int i:scs.keySet()){
      double min_dist = 999999;
      int tmp_index = -1;
      for (int j = 0; j < sclusters.size(); j++) {
        ArrayList<Double> origin_centroids = sclusters.get(j).centroids;
        double new_dist = calcSBDdistance(origin_centroids, scs.get(i).centroids).dist;
            if (min_dist > new_dist) {
              min_dist = new_dist;
              tmp_index = j;
            }   
      }
      if (tmp_index != -1) {
        if (min_dist < new_clusters_dist.get(tmp_index)) {
          if (!to_add.containsKey(tmp_index)) {
            ArrayList<Scluster> temp = new ArrayList<Scluster>();
            temp.add(scs.get(i));
            to_add.put(tmp_index, temp);
          } else {
            ArrayList<Scluster> temp = to_add.get(tmp_index);
            temp.add(scs.get(i));
            to_add.put(tmp_index, temp);
          }
        } else {
          new_c.add(scs.get(i));
        }
      }
    }
    ArrayList<Scluster_in_Memory> new_clusters = new ArrayList<Scluster_in_Memory>();
    ArrayList<Double> all_mean_dist = new ArrayList<Double>();
    for (int i = 0; i < sclusters.size(); i++) {
      Scluster_in_Memory cur_c = sclusters.get(i);
      if (to_add.get(i) != null) {
        ArrayList<Scluster> t_a = to_add.get(i);
        ArrayList<Integer> all_index = cur_c.idxs;
        ArrayList<ArrayList<Double>> all_sub_to_add = new ArrayList<ArrayList<Double>>();
        for (Scluster t_a_s : t_a) {
          all_index.addAll(t_a_s.idxs);
          all_sub_to_add.addAll(t_a_s.cluster_subseq);
        }
        // Updating the centroid shape
        ArrayList<Double> new_centroid =
            extract_shape_stream(all_sub_to_add, i, cur_c.centroids, false);
        new_clusters.add(new Scluster_in_Memory(new_centroid, all_index));
        // Updating the intra cluster distance
        double dist_to_add = compute_mean_dist(cur_c.centroids, all_index);
        double ratio = (double) cur_c.idxs.size() / (cur_c.idxs.size() + all_index.size());
        all_mean_dist.add(ratio * new_clusters_dist.get(i) + (1.0 - ratio) * dist_to_add);
      } else {
        new_clusters.add(cur_c);
        all_mean_dist.add(new_clusters_dist.get(i));
      }
    }
    // Adding new clusters
    for (int i = 0; i < new_c.size(); i++) {
      set_initial_S(new_c.get(i).cluster_subseq, sclusters.size() + i, new_c.get(i).centroids);
      new_clusters.add(new Scluster_in_Memory(new_c.get(i).centroids, new_c.get(i).idxs));
      all_mean_dist.add(compute_mean_dist(new_c.get(i).centroids, new_c.get(i).idxs));
    }
    sclusters = new_clusters;
    new_clusters_dist = all_mean_dist;
    current_time += batch_size;

    set_normal_model();
  }

  public ArrayList<Double> compute_score() {
    int size_ts = Math.min(ts.size(), current_time) - (current_time - batch_size);
    ArrayList<Double> ts_current = new ArrayList<Double>();
    for (int i = current_time - batch_size; i < Math.min(ts.size(), current_time); i++) {
      ts_current.add(ts.get(i));
    }
    if (nm_current_weight.size() != weights.size()) {
      for (int i = nm_current_weight.size(); i < weights.size(); i++) {
        nm_current_weight.add(weights.get(i));
      }
    }
    // calc score
    ArrayList<ArrayList<Double>> all_join = new ArrayList<ArrayList<Double>>();
    for (int i = 0; i < sclusters.size(); i++) {
      Mp mp = new Mp();
      ArrayList<Double> join_s = mp.stamp(sclusters.get(i).centroids, ts_current, pattern_length);
      all_join.add(join_s);
    }

    // update w
    ArrayList<Double> join = new ArrayList<Double>();
    ArrayList<Double> all_activated_weighted = new ArrayList<Double>();
    for (int i = 0; i < all_join.get(0).size(); i++) {
      join.add(0.0);
    }
    for (int i = 0; i < all_join.size(); i++) {
      double new_w = (double) weights.get(i) / (1 + Math.max(0, time_decay.get(i) - batch_size));
      double update_w = (1 - alpha) * nm_current_weight.get(i) + alpha * new_w;
      for (int j = 0; j < all_join.get(i).size(); j++) {
        double temp = join.get(j) + all_join.get(i).get(j) * update_w;
        join.set(j, temp);
      }
      all_activated_weighted.add(update_w);
    }

    // join
    /*double sum=calcsum(all_activated_weighted);
    double c=join.get(join.size()-1);
    for(int i=0;i<pattern_length;i++) {
    	join.add(c);
    }
    for(int i=0;i<join.size();i++) {
    	join.set(i, (double)join.get(i)/sum);
    }
    join = running_mean(join,pattern_length);
    double c1=join.get(0);
    Collections.reverse(join);
    for(int i=0;i<pattern_length-1;i++) {
    	join.add(c1);
    }
    Collections.reverse(join);*/

    nm_current_weight = all_activated_weighted;
    if (mean == -1) {
      mean = calcMean(join);
      std = calcStd(join, 0);
    } else {
      mean = (1 - alpha) * mean + alpha * calcMean(join);
      std = (1 - alpha) * std + alpha * calcStd(join, 0);
    }

    for (int i = 0; i < join.size(); i++) {
      join.set(i, (double) (join.get(i) - mean) / std);
    }
    return join;
  }

  private ArrayList<Double> running_mean(ArrayList<Double> x, int l) {
    ArrayList<Double> s = new ArrayList<Double>();
    s.add(0.0);
    s.addAll(x);
    s = cumsum(s);
    ArrayList<Double> seqSum = new ArrayList<Double>();
    for (int i = 0; i < s.size() - l; i++) {
      int j = i + l;
      seqSum.add((double) (s.get(j) - s.get(i)) / l);
    }
    return seqSum;
  }

  public ArrayList<Double> cumsum(ArrayList<Double> x) {
    ArrayList<Double> y = new ArrayList<Double>();
    for (int i = 0; i < x.size(); i++) {
      if (i == 0) {
        y.add(0.0);
      } else {
        y.add(y.get(i - 1));
      }
      y.set(i, y.get(i) + x.get(i));
    }
    return y;
  }

  private double calcsum(ArrayList<Double> x) {
    double sum = 0;
    for (int i = 0; i < x.size(); i++) {
      sum += x.get(i);
    }
    return sum;
  }

  private ArrayList<Double> extract_shape_stream(
      ArrayList<ArrayList<Double>> X, int idx, ArrayList<Double> cluster_centers, boolean initial) {
    int columns = X.get(0).size();
    ArrayList<ArrayList<Double>> a = new ArrayList<ArrayList<Double>>();
    for (int i = 0; i < X.size(); i++) {
      ArrayList<Double> opt_x = new ArrayList<Double>();
      Sbd sbd = calcSBDdistance(cluster_centers, X.get(i));
      a.add(sbd.y_shift);
    }
    double[][] aa = new double[a.size()][a.get(0).size()];
    for (int i = 0; i < a.size(); i++) {
      for (int j = 0; j < a.get(i).size(); j++) {
        aa[i][j] = a.get(i).get(j);
      }
    }
    DoubleMatrix Xp=new DoubleMatrix(aa);
    DoubleMatrix Y=Xp.transpose();
    DoubleMatrix S=Y.mmul(Xp);
    S=S.add(SS.get(idx));
    SS.set(idx, S);
    double[][] o=new double[columns][columns];
	double[][] ii=new double[columns][columns];
	for(int i=0;i<columns;i++) {
		for(int j=0;j<columns;j++) {
			o[i][j]=(double)1/columns;
			if(i==j)ii[i][j]=1;
		}
	}
	double[][] q=new double[columns][columns];
	for(int i=0;i<columns;i++) {
		for(int j=0;j<columns;j++) {
			q[i][j]=ii[i][j]-o[i][j];
		}
	}
	DoubleMatrix Q=new DoubleMatrix(q);
	DoubleMatrix QT=Q.transpose();
	DoubleMatrix M=QT.mmul(S).mmul(Q);
    //ComplexDoubleMatrix E = Eigen.eigenvalues(A);
    ComplexDoubleMatrix[] EV = Eigen.eigenvectors(M);
    ArrayList<Double> cent=new ArrayList<Double>();
    for(int i=0;i<aa[0].length;i++) {
		cent.add(EV[0].getReal(i));
	}
	//
	double finddistance1=0;
	double finddistance2=0;		
	for(int i=0;i<a.get(0).size();i++) {
		finddistance1+=Math.pow(a.get(0).get(i)-cent.get(i), 2);
		finddistance2+=Math.pow(a.get(0).get(i)+cent.get(i), 2);
	}
	if(finddistance1>=finddistance2) {
		for(int i=0;i<cent.size();i++) {
			cent.set(i, cent.get(i)*-1);
		}
	}
	return zscore(cent,1);
  }

  public ArrayList<Double> zscore(ArrayList<Double> t, int ddof) {
    double mean = calcMean(t);
    double std = calcStd(t, ddof);
    for (int i = 0; i < t.size(); i++) {
      t.set(i, (t.get(i) - mean) / std);
    }
    return t;
  }

  public static double calcMean(ArrayList<Double> t) {
    double sum = 0;
    for (int i = 0; i < t.size(); i++) {
      sum += t.get(i);
    }
    double mean = sum / t.size();
    return mean;
  }

  public static double calcStd(ArrayList<Double> t, int ddof) {
    double total = 0;
    double mean = calcMean(t);
    for (int i = 0; i < t.size(); i++) {
      total += (t.get(i) - mean) * (t.get(i) - mean);
    }
    int dev = t.size();
    if (ddof == 1) {
      dev = dev - 1;
    }
    double std = Math.sqrt(total / dev);
    return std;
  }

  public Sbd calcSBDdistance(ArrayList<Double> x, ArrayList<Double> y) {
    assert x.size() == y.size() : "length must be equal";
    Ncc NN = calcNCC(x, y);
    double ncc = NN.dist;
    int index = NN.index;
    double dist = 1 - ncc;

    int s = index - x.size();
    ArrayList<Double> y_shift = new ArrayList<Double>();
    if (s > 0) {
      for (int i = 0; i < s; i++) {
        y_shift.add(0.0);
      }
      for (int i = 0; i < x.size() - s; i++) {
        y_shift.add(y.get(i));
      }
    } else if (s == 0) {
      y_shift = y;
    } else {
      for (int i = -s; i < x.size(); i++) {
        y_shift.add(y.get(i));
      }
      for (int i = 0; i < -s; i++) {
        y_shift.add(0.0);
      }
    }
    Sbd sbd = new Sbd(dist, y_shift);
    return sbd;
  }

  private Ncc calcNCC(ArrayList<Double> x, ArrayList<Double> y) {
    int len = x.size();
    double pow_x = 0;
    double pow_y = 0;
    int FFTlen = (int) Math.pow(2, Math.ceil(Math.log(2 * len - 1) / Math.log(2)));
    double[] xx = new double[FFTlen];
    double[] yy = new double[FFTlen];
    for (int i = 0; i < len; i++) {
      pow_x += Math.pow(x.get(i), 2);
      pow_y += Math.pow(y.get(i), 2);
      xx[i] = x.get(i);
      yy[i] = y.get(i);
    }
    double dist_x = Math.pow(pow_x, 0.5);
    double dist_y = Math.pow(pow_y, 0.5);
    double dist_xy = dist_x * dist_y;

    for (int i = len; i < FFTlen; i++) {
      xx[i] = 0.0;
      yy[i] = 0.0;
    }
    FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
    Complex[] result_x = fft.transform(xx, TransformType.FORWARD);
    Complex[] result_y = fft.transform(yy, TransformType.FORWARD);
    Complex[] result_xy = new Complex[result_x.length];
    ;
    for (int i = 0; i < result_y.length; i++) {
      result_y[i] = result_y[i].conjugate();
      result_xy[i] = result_x[i].multiply(result_y[i]);
    }
    Complex[] cc = fft.transform(result_xy, TransformType.INVERSE);
    double[] dd = new double[len * 2];
    int k = 0;
    for (int i = cc.length - len + 1; i < cc.length; i++) {
      dd[k++] = cc[i].getReal();
    }
    for (int i = 0; i < len; i++) {
      dd[k++] = cc[i].getReal();
    }
    double max = -99999;
    int max_id = 0;
    for (int i = 0; i < dd.length; i++) {
      if (dd[i] > max) {
        max = dd[i];
        max_id = i;
      }
    }
    double dist = (double) max / dist_xy;
    Ncc ncc = new Ncc(max_id, dist);
    return ncc;
  }

  public HashMap<Integer, Scluster> Kshape_subsequence(boolean initialization) {
    int nb_subsequence = 0;
    if (initialization) {
      nb_subsequence = init_length;
    } else {
      nb_subsequence = batch_size;
    }

    ArrayList<Integer> idxs = new ArrayList<Integer>();
    ArrayList<ArrayList<Double>> t_list = new ArrayList<ArrayList<Double>>();

    int range = Math.min(current_time + nb_subsequence, ts.size() - subsequence_length);
    for (int i = current_time; i < range; i += overlaping_rate) {
      ArrayList<Double> temp = new ArrayList<Double>();
      for (int j = i; j < i + subsequence_length; j++) {
        temp.add(ts.get(j));
      }
      t_list.add(temp);
      idxs.add(i);
    }
    KShape ks = new KShape(t_list, k);
    Clusters cs= ks.kshape();
    ArrayList<Integer> list_label = cs.getIdx();

    HashMap<Integer, ArrayList<Integer>> cluster_idx = new HashMap<Integer, ArrayList<Integer>>();
    HashMap<Integer, ArrayList<ArrayList<Double>>> cluster_subseq =
        new HashMap<Integer, ArrayList<ArrayList<Double>>>();
    for (int i = 0; i < list_label.size(); i++) {
      int lbl = list_label.get(i);
      int idx = idxs.get(i);
      if (!cluster_idx.containsKey(lbl)) {
        ArrayList<Integer> temp_id = new ArrayList<Integer>();
        temp_id.add(idx);
        cluster_idx.put(lbl, temp_id);
        ArrayList<ArrayList<Double>> temp_seq = new ArrayList<ArrayList<Double>>();
        idx = idx - current_time;
        temp_seq.add(t_list.get(idx / overlaping_rate));
        cluster_subseq.put(lbl, temp_seq);
      } else {
        ArrayList<Integer> temp_id = cluster_idx.get(lbl);
        temp_id.add(idx);
        cluster_idx.put(lbl, temp_id);
        ArrayList<ArrayList<Double>> temp_seq = cluster_subseq.get(lbl);
        idx = idx - current_time;
        temp_seq.add(t_list.get(idx / overlaping_rate));
        cluster_subseq.put(lbl, temp_seq);
      }
    }
    HashMap<Integer, Scluster> new_Sclusters = new HashMap<Integer, Scluster>();
    for (int i = 0; i < k; i++) {
    	if(cluster_subseq.containsKey(i)) {
    		Scluster s =
    		          new Scluster(cs.getCentroids().get(i), cluster_idx.get(i), cluster_subseq.get(i));
    		      new_Sclusters.put(i, s);
    	}
      
    }
    return new_Sclusters;
  }

  // weightUPDATE UNIT
  public void set_normal_model() {
    ArrayList<Double> frequency = new ArrayList<Double>();
    ArrayList<Double> centrality = new ArrayList<Double>();
    ArrayList<Double> Time_decay = new ArrayList<Double>();
    for (int i = 0; i < sclusters.size(); i++) {
      frequency.add((double) sclusters.get(i).idxs.size());
      int len = sclusters.get(i).idxs.size();
      Time_decay.add((double) current_time - (double) sclusters.get(i).idxs.get(len - 1));
      double dist_nms = 0;
      for (int j = 0; j < sclusters.size(); j++) {
        if (j != i) {
          dist_nms += calcSBDdistance(sclusters.get(i).centroids, sclusters.get(j).centroids).dist;
        }
      }
      centrality.add(dist_nms);
    }
    double maxf = Collections.max(frequency);
    double minf = Collections.min(frequency);
    double maxc = Collections.max(centrality);
    double minc = Collections.min(centrality);
    for (int i = 0; i < sclusters.size(); i++) {
      frequency.set(i, ((double) frequency.get(i) - minf) / (maxf - minf) + 1);
      centrality.set(i, ((double) centrality.get(i) - minc) / (maxc - minc) + 1);
    }
    weights = new ArrayList<Double>();
    for (int i = 0; i < sclusters.size(); i++) {
      weights.add((double) Math.pow(frequency.get(i), 2) / centrality.get(i));
    }
    time_decay = Time_decay;
  }

  // SETTING IN MEMORY THE MATRICES S
  public void set_initial_S(
      ArrayList<ArrayList<Double>> X, int idx, ArrayList<Double> cluster_centers) {
    int sz = X.size();
    ArrayList<ArrayList<Double>> a = new ArrayList<ArrayList<Double>>();
    for (int i = 0; i < X.size(); i++) {
      ArrayList<Double> opt_x = new ArrayList<Double>();
      Sbd sbd = calcSBDdistance(cluster_centers, X.get(i));
      a.add(sbd.y_shift);
    }
    double[][] aa = new double[a.size()][a.get(0).size()];
    for (int i = 0; i < a.size(); i++) {
      for (int j = 0; j < a.get(i).size(); j++) {
        aa[i][j] = a.get(i).get(j);
      }
    }
    DoubleMatrix Xp=new DoubleMatrix(aa);
    DoubleMatrix Y=Xp.transpose();
    DoubleMatrix S=Y.mmul(Xp);
    SS.add(S);
  }

  public double compute_mean_dist(ArrayList<Double> centroids, ArrayList<Integer> idxs) {
    ArrayList<Double> dist_all = new ArrayList<Double>();
    for (Integer idx : idxs) {
      dist_all.add(calcSBDdistance(centroids, subArrayList(ts, idx)).dist);
    }
    return calcMean(dist_all);
  }

  public ArrayList<Double> subArrayList(ArrayList<Double> a, int i) {
    ArrayList<Double> temp = new ArrayList<Double>();
    for (int j = i; j < i + subsequence_length; j++) {
      temp.add(a.get(j));
    }
    return temp;
  }
}

class Ncc {
  int index;
  double dist;

  public Ncc(int index, double dist) {
    super();
    this.index = index;
    this.dist = dist;
  }
}

class Sbd {
  double dist;
  ArrayList<Double> y_shift;

  public Sbd(double dist, ArrayList<Double> y_shift) {
    super();
    this.dist = dist;
    this.y_shift = y_shift;
  }
}

class Scluster {
  ArrayList<Double> centroids;
  ArrayList<Integer> idxs;
  ArrayList<ArrayList<Double>> cluster_subseq;

  public Scluster(
      ArrayList<Double> centroids,
      ArrayList<Integer> idxs,
      ArrayList<ArrayList<Double>> cluster_subseq) {
    super();
    this.centroids = centroids;
    this.idxs = idxs;
    this.cluster_subseq = cluster_subseq;
  }
}

class Scluster_in_Memory {
  ArrayList<Double> centroids;
  ArrayList<Integer> idxs;

  public Scluster_in_Memory(ArrayList<Double> centroids, ArrayList<Integer> idxs) {
    super();
    this.centroids = centroids;
    this.idxs = idxs;
  }
}

class Score implements Comparable<Score> {
  int index;
  double score;

  public Score(int index, double score) {
    super();
    this.index = index;
    this.score = score;
  }

  @Override
  public int compareTo(Score o) {
    if (this.score > o.score) {
      return 1;
    } else if (this.score < o.score) {
      return -1;
    } else {
      return 0;
    }
  }
}
