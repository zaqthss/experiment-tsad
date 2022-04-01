package cn.edu.bit.cs.anomaly;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import cn.edu.bit.cs.anomaly.entity.TimePoint;
import cn.edu.bit.cs.anomaly.entity.TimePointMulDim;
import cn.edu.bit.cs.anomaly.entity.TimeSeries;
import cn.edu.bit.cs.anomaly.entity.TimeSeriesMulDim;
import cn.edu.bit.cs.anomaly.util.FileHandler;
import cn.edu.bit.cs.anomaly.util.SubseqScore;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import cn.edu.bit.cs.anomaly.util.Constants.IS_ANOMALY;

public class LRRDS implements MultiDimAlgorithm {
  private TimeSeriesMulDim MulT;
  public double com_rate = 0.1;
  public int emddim = 1;
  public int tao = 1;
  public int window_size = 2;
  public int slack = 30;
  public int sub_minlength = 10;
  public HashMap<Integer, ArrayList<Double>> paa_ts;
  public double[] w_entr;
  public ArrayList<Integer> cp_list;
  public ArrayList<Integer> cp_list_origin;
  public double[][] rec_plot;
  public double[][] cp_profile;
  public ArrayList<SubseqScore> all_score;
  private int top_k;
  private boolean success = true;
  public int anomaly_class=0;
  public ArrayList<Integer> anomaly_index;
  @Override
  public void run() {
    fit();
    if (success == true) {
      /*for (int i = 0; i < Math.min(top_k, all_score.size()); i++) {
        int index = all_score.get(i).getIndex();
        double[] record = cp_profile[index];
        System.out.println((int) record[2] + " " + (int) record[3]);
        for (int j = (int) record[2]; j <= (int) record[3]; j++) {
          TimePointMulDim tpm = (TimePointMulDim) MulT.getTimeseries().get(j);
          tpm.setIs_anomaly(IS_ANOMALY.TRUE);
        }
      }*/
    	System.out.println();
    	for(int i=0;i<anomaly_index.size();i++) {
    		double[] record = cp_profile[anomaly_index.get(i)];
    		for (int j = (int) record[2]; j <= Math.min((int) record[3],MulT.getLength()-1); j++) {
    	          TimePointMulDim tpm = (TimePointMulDim) MulT.getTimeseries().get(j);
    	          tpm.setIs_anomaly(IS_ANOMALY.TRUE);
    	        }
    	}
    }
  }

  @Override
  public void init(Map<String, Object> args, TimeSeriesMulDim timeseries) {
    this.MulT = timeseries;
    MulT.clear();
    this.com_rate = (double) args.get("compressed_rate");
    this.slack = (int) args.get("slack");
    this.sub_minlength = (int) args.get("sub_minlength");
    //this.top_k = (int) args.get("top_k");
    this.success=true;
  }

  public void fit() {
    preprocess();
    window_size = find_best_window();
    if (window_size == -1) {
      return;
    }
    Segment();
    outlier();
  }

  public void preprocess() {
    int lenT = MulT.getLength();
    ArrayList<TimeSeries> series_list = new ArrayList<TimeSeries>();
    ArrayList<TimePointMulDim> mult = MulT.getTimeseries();
    paa_ts = new HashMap<Integer, ArrayList<Double>>();
    for (int i = 0; i < MulT.getDim(); i++) {
      TimeSeries T = new TimeSeries();
      for (int j = 0; j < mult.size(); j++) {
        double value = mult.get(j).getObsVal()[i]; // time j dim i
        T.addPoint(new TimePoint(j, value));
      }
      ArrayList<Double> paa = PAA(T);
      double maxp = Collections.max(paa);
      double minp = Collections.min(paa);
      for (int j = 0; j < paa.size(); j++) {
    	  if(maxp==minp) {
    		  paa.set(j,2.0);
    	  }else {
    		  paa.set(j, 1 + (double) (paa.get(j) - minp) / (maxp - minp));
    	  }   
      }
      paa_ts.put(i, paa);
    }
    System.out.print("");
  }

  private void Segment() {
    rec_plot = MakeRP();
    LREC(rec_plot);
  }

  public int find_best_window() {
    ArrayList<Double> res = new ArrayList<Double>();
    for (int w = 2; w <= 10; w++) {
      window_size = w;
      Segment();
      int n = cp_list.size();
      if (n > 2) {
        Collections.sort(cp_list);
        ArrayList<Integer> cp_temp = new ArrayList<Integer>();
        ArrayList<Integer> cp_temp_origin = new ArrayList<Integer>();
        for (int i = 0; i < cp_list.size() - 1; i++) {
          if ((cp_list.get(i + 1) - cp_list.get(i)) > sub_minlength) {
            cp_temp.add(cp_list.get(i));
            cp_temp_origin.add(cp_list_origin.get(i));
          }
        }
        cp_list = cp_temp;
        cp_list_origin = cp_temp_origin;
        n = cp_list.size();
        if (n < 2) {
          res.add(9999.0);
          continue;
        }
        double[] cp_n = new double[n - 1];
        for (int i = 0; i < n - 1; i++) {
          cp_n[i] = cp_list.get(i + 1) - cp_list.get(i) + 1;
        }
        res.add(calcStd(cp_n, 1));
      } else {
        res.add(9999.0);
      }
    }
    Double w_best = Collections.min(res);
    if (w_best == 9999.0) {
      System.out.println("unable to find change point");
      success = false;
      return -1;
    }
    int w_best_in = res.indexOf(w_best) + 2;
    return w_best_in;
  }

  private void outlier() {
    Collections.sort(cp_list);
    // filtration of change points
    ArrayList<Integer> cp_temp = new ArrayList<Integer>();
    ArrayList<Integer> cp_temp_origin = new ArrayList<Integer>();
    for (int i = 0; i < cp_list.size() - 1; i++) {
      if ((cp_list.get(i + 1) - cp_list.get(i)) >= sub_minlength) {
        cp_temp.add(cp_list.get(i));
        cp_temp_origin.add(cp_list_origin.get(i));
      }
    }
    cp_list = cp_temp;
    cp_list_origin = cp_temp_origin;

    int n = cp_list.size();
    double[] cp_n = new double[n - 1];
    int[] cp_begin = new int[n - 1];
    int[] cp_end = new int[n - 1];
    for (int i = 0; i < n - 1; i++) {
      cp_n[i] = cp_list.get(i + 1) - cp_list.get(i) + 1;
      cp_begin[i] = (int) (cp_list.get(i) * (1 / com_rate));
      cp_end[i] = (int) (cp_list.get(i + 1) * (1 / com_rate));
    }

    cp_list = cp_list_origin;
    double[] cp_mean = new double[n - 1];
    for (int i = 0; i < n - 1; i++) {
      double sum = 0;
      for (int j = cp_list.get(i); j <= cp_list.get(i + 1); j++) {
        sum += w_entr[j - 1];
      }
      cp_mean[i] = (double) sum / (cp_list.get(i + 1) - cp_list.get(i) + 1);
    }
    cp_profile = new double[n - 1][4];
    for (int i = 0; i < n - 1; i++) {
      cp_profile[i][0] = (double) cp_n[i];
      cp_profile[i][1] = cp_mean[i];
      cp_profile[i][2] = (double) cp_begin[i];
      cp_profile[i][3] = (double) cp_end[i];
    }
    double[][] cp_metric = new double[2][n - 1];
    cp_metric[0] = cp_n;
    cp_metric[1] = cp_mean;
    double[][] cp_rp = MakeRP1(cp_metric);

    ArrayList<Double> deg = new ArrayList<Double>();
    for (int i = 0; i < cp_rp.length; i++) {
      int sum = 0;
      for (int j = 0; j < cp_rp.length; j++) {
        sum += cp_rp[i][j];
      }
      deg.add((double) sum / (n - 1));
    }
    
    
    ArrayList<Attribute> attributes = new ArrayList<>();
    attributes.add(new Attribute(Integer.toString(1)));
    ArrayList class_values = new ArrayList(2); 
    class_values.add("0");
    class_values.add("1"); 
    attributes.add(new Attribute("Class",class_values));
    Instances data = new Instances("scores",attributes,0);
    //data.setClassIndex(data.numAttributes() - 1);
    for(int i=0;i<deg.size();i++) {
    	Instance instance = new DenseInstance(attributes.size());                   
    	instance.setValue(0,deg.get(i));      
    	instance.setDataset(data); 
    	data.add(instance);
    }
    SimpleKMeans KM = new SimpleKMeans();
	try {
		KM.setPreserveInstancesOrder(true);
		KM.setNumClusters(2);
		KM.buildClusterer(data);
		System.out.println(KM.toString());
		Instances instances = KM.getClusterCentroids();
		double[] ins0=instances.get(0).toDoubleArray();
		double[] ins1=instances.get(1).toDoubleArray();
		if(ins0[0]<ins1[0]) {
			anomaly_class=1;
		}else {
			anomaly_class=0;
		}
		int assignments[] = KM.getAssignments();
		int x=0;
		anomaly_index=new ArrayList<Integer>();
	    for(int assignment : assignments) {
	        if(assignment==anomaly_class) {
	        	System.out.println(cp_profile[x][2]+" "+cp_profile[x][3]);
	        	anomaly_index.add(x);
	        }
	        x++;
	    }
	    System.out.println();
	} catch (Exception e) {
		e.printStackTrace();
	}
	
    
   /* all_score = new ArrayList<SubseqScore>();
    for (int i = 0; i < deg.size(); i++) {
      SubseqScore s = new SubseqScore(i, deg.get(i));
      all_score.add(s);
    }
    Collections.sort(all_score);
    Collections.reverse(all_score);*/

    System.out.print("");
  }

  private void LREC(double[][] x) {
    double[] dir = new double[x.length];
    for (int i = 0; i < x.length; i++) {
      dir[i] = i + 1;
    }
    double[][] w_m = phase_space(dir, window_size);
    double w_all = window_size * window_size;
    w_entr = new double[w_m.length];
    for (int i = 0; i < w_m.length; i++) {
      double sum = 0;
      for (int j = 0; j < w_m[i].length; j++) {
        for (int t = 0; t < w_m[i].length; t++) {
          sum += x[(int) w_m[i][j] - 1][(int) w_m[i][t] - 1];
        }
      }
      w_entr[i] = (double) sum / w_all;
    }
    double max_w = 0.0;
    double min_w = 99999;
    for (int i = 0; i < w_entr.length; i++) {
      if (w_entr[i] > max_w) {
        max_w = w_entr[i];
      }
      if (w_entr[i] < min_w) {
        min_w = w_entr[i];
      }
    }
    if (max_w == 0) {
      cp_list = new ArrayList<Integer>();
      return;
    }
    for (int i = 0; i < w_entr.length; i++) {
      w_entr[i] = (double) w_entr[i] / (max_w - min_w);
    }
    double[] ret = new double[w_m.length];
    for (int i = 0; i < w_entr.length; i++) {
      if (w_entr[i] > 0) {
        ret[i] = 1;
      } else {
        ret[i] = 0;
      }
    }
    double[][] entr_w = phase_space(ret, 2);
    double[] entr_new = new double[entr_w.length];
    double max_entr_new = 0;
    for (int i = 0; i < entr_w.length; i++) {
      entr_new[i] = calcStd(entr_w[i], 1);
      if (max_entr_new < entr_new[i]) {
        max_entr_new = entr_new[i];
      }
    }
    cp_list = new ArrayList<Integer>();
    for (int i = 0; i < entr_w.length; i++) {
      if (entr_new[i] == max_entr_new) {
        cp_list.add(i + 3);
      }
    }
    cp_list_origin = new ArrayList<Integer>();
    cp_list_origin.addAll(cp_list);
    for (int i = 0; i < cp_list.size(); i++) {
      if (ret[cp_list.get(i)] != 0) {
        cp_list.set(i, cp_list.get(i) + window_size);
      }
    }
  }

  public double calcStd(double[] t, int ddof) {
    double total = 0;
    double mean = calcMean(t);
    for (int i = 0; i < t.length; i++) {
      total += (t[i] - mean) * (t[i] - mean);
    }
    int dev = t.length;
    if (ddof == 1) {
      dev = dev - 1;
    }
    double std = Math.sqrt(total / dev);
    return std;
  }

  private double[][] phase_space(double[] data, int dim1) {
    int dim2 = data.length - (dim1 - 1) * tao;
    double[][] res = new double[dim2][dim1];
    for (int j = 0; j < dim2; j++) {
      for (int i = 1; i <= dim1; i++) {
        res[j][i - 1] = data[(i - 1) * tao + j];
      }
    }
    return res;
  }

  private double[][] MakeRP() {
    //HashMap<Integer,ArrayList<ArrayList<Double>>> res_list=phase_space_more();
    double[][] x = new double[paa_ts.get(0).size()][paa_ts.size()];
    for (int i = 0; i < paa_ts.get(0).size(); i++) {
      for (int j = 0; j < paa_ts.size(); j++) {
        x[i][j] = paa_ts.get(j).get(i);
      }
    }
    HashMap<String, Object> map = dist_m(x);
    //double theta1 = (double) map.get("d1max")*0.75;
    //double theta2 = (double) map.get("d2max")*0.75;
    double[][] d1 = (double[][]) map.get("d1");
    double[][] d2 = (double[][]) map.get("d2");
    ArrayList<Double> d1_list = (ArrayList<Double>) map.get("d1list");
    ArrayList<Double> d2_list = (ArrayList<Double>) map.get("d2list");
    Collections.sort(d1_list);
    Collections.sort(d2_list);
    double theta1=d1_list.get(d1_list.size()-1)*0.25;
    double theta2=d2_list.get(d2_list.size()-1)*0.25;
    for (int i = 0; i < d1.length; i++) {
      for (int j = 0; j < d1[0].length; j++) {
        if (d1[i][j] < theta1) {
          d1[i][j] = 0;
        } else {
          d1[i][j] = 1;
        }
        if (d2[i][j] < theta2) {
          d2[i][j] = 0;
        } else {
          d2[i][j] = 1;
        }
        d1[i][j] = d1[i][j] * d2[i][j];
      }
    }
    return d1;
  }

  // Function for generating full recurrence matrix
  private double[][] MakeRP1(double[][] cp_metric) {
    double[][] x = new double[cp_metric[0].length][cp_metric.length];
    for (int i = 0; i < cp_metric.length; i++) {
      double max = Arrays.stream(cp_metric[i]).max().getAsDouble();
      double min = Arrays.stream(cp_metric[i]).min().getAsDouble();
      for (int j = 0; j < cp_metric[0].length; j++) {
        cp_metric[i][j] = 1 + (double) (cp_metric[i][j] - min) / (max - min);
        x[j][i] = cp_metric[i][j];
      }
    }
    HashMap<String, Object> map = dist_m1(x);
    double[][] d1 = (double[][]) map.get("d1");
    double[][] d2 = (double[][]) map.get("d2");
    ArrayList<Double> d1_list = (ArrayList<Double>) map.get("d1list");
    ArrayList<Double> d2_list = (ArrayList<Double>) map.get("d2list");
    Collections.sort(d1_list);
    Collections.sort(d2_list);
    /*double index_d = (d1_list.size() - 1) * 0.75;
    int index = (int) Math.ceil((d1_list.size() - 1) * 0.75);

    double theta1 = d1_list.get(index - 1) + d1_list.get(index) * (index - index_d);
    double theta2 = d2_list.get(index - 1) + d2_list.get(index) * (index - index_d);*/
    double theta1=d1_list.get(d1_list.size()-1)*0.25;
    double theta2=d2_list.get(d2_list.size()-1)*0.25;

    for (int i = 0; i < d1.length; i++) {
      for (int j = 0; j < d1[0].length; j++) {
        if (d1[i][j] < theta1) {
          d1[i][j] = 0;
        } else {
          d1[i][j] = 1;
        }
        if (d2[i][j] < theta2) {
          d2[i][j] = 0;
        } else {
          d2[i][j] = 1;
        }
        d1[i][j] = 1 - (1 - d1[i][j]) * (1 - d2[i][j]);
      }
    }
    return d1;
  }

  private HashMap<String, Object> dist_m(double[][] x) {
    double[][] d1 = new double[x.length][x.length];
    double[][] d2 = new double[x.length][x.length];
    double d1max = 0.0;
    double d2max = 0.0;
    int window = window_size + slack;
    ArrayList<Double> d1_list = new ArrayList<Double>();
    ArrayList<Double> d2_list = new ArrayList<Double>();
    for (int i = 0; i < x.length - window; i++) {
      for (int j = i + 1; j <= i + window; j++) {
        d1[i][j] = d1[j][i] = Math.sqrt(calcsumpow(x[i], x[j]));
        d2[i][j] = d2[j][i] = B_dist(x[i], x[j]);
        d1max = d1[i][j];
        d2max = d2[i][j];
        d1_list.add(d1[i][j]);
        d2_list.add(d2[i][j]);
      }
    }
    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put("d1", d1);
    map.put("d2", d2);
    map.put("d1max", d1max);
    map.put("d2max", d2max);
    map.put("d1list", d1_list);
    map.put("d2list", d2_list);
    return map;
  }

  private HashMap<String, Object> dist_m1(double[][] x) {
    double[][] d1 = new double[x.length][x.length];
    double[][] d2 = new double[x.length][x.length];
    ArrayList<Double> d1_list = new ArrayList<Double>();
    ArrayList<Double> d2_list = new ArrayList<Double>();
    for (int i = 0; i < x.length; i++) {
      for (int j = i; j < x.length; j++) {
        d1[i][j] = d1[j][i] = Math.sqrt(calcsumpow(x[i], x[j]));
        d2[i][j] = d2[j][i] = B_dist(x[i], x[j]);
        d1_list.add(d1[i][j]);
        d2_list.add(d2[i][j]);
      }
    }
    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put("d1", d1);
    map.put("d2", d2);
    map.put("d1list", d1_list);
    map.put("d2list", d2_list);
    return map;
  }
  // Bhattacharyya distance
  private double B_dist(double[] x1, double[] x2) {
    double sum1 = calcsum(x1);
    double sum2 = calcsum(x2);
    double sumdown = Math.sqrt(sum1 * sum2);
    double sumup = 0.0;
    for (int i = 0; i < x1.length; i++) {
      sumup += Math.sqrt(x1[i] * x2[i]);
    }
    DecimalFormat df = new DecimalFormat("#.###");
    sumup = Double.parseDouble(df.format(sumup));
    sumdown = Double.parseDouble(df.format(sumdown));
    if (sumup == sumdown) {
      return 0;
    } else {
      double dist = Math.sqrt(1 - (double) sumup / sumdown);
      return dist;
    }
  }

  private double calcsumpow(double[] x1, double[] x2) {
    double sum = 0;
    for (int i = 0; i < x1.length; i++) {
      sum += Math.pow(x1[i] - x2[i], 2);
    }
    return sum;
  }

  private double calcsum(double[] x) {
    double sum = 0;
    for (int i = 0; i < x.length; i++) {
      sum += x[i];
    }
    return sum;
  }

  public ArrayList<Double> PAA(TimeSeries t) {
    ArrayList<Double> t_new = new ArrayList<Double>();
    int s = (int) (1 / com_rate);
    int n = t.getLength();
    for (int i = 1; i <= Math.floor((double) (n / s)); i++) {
      double sum = 0;
      int count = 0;
      for (int j = Math.max((i - 1) * s - 1, 0); j < Math.min(i * s, n); j++) {
        sum += t.getTimePoint(j).getObserve();
        count++;
      }
      sum = (double) sum / count;
      t_new.add(sum);
    }
    return t_new;
  }

  public static double calcMean(double[] t) {
    double sum = 0;
    for (int i = 0; i < t.length; i++) {
      sum += t[i];
    }
    double mean = sum / t.length;
    return mean;
  }
}
