package cn.edu.bit.cs.anomaly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import cn.edu.bit.cs.anomaly.entity.TimeSeries;
import cn.edu.bit.cs.anomaly.util.DataHandler;
import cn.edu.bit.cs.anomaly.util.FileHandler;
import cn.edu.bit.cs.anomaly.util.SAX;
import weka.classifiers.trees.RandomForest;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.EM;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class CABD implements SingleDimAlgorithm {
	public TimeSeries timeseries;
	public ArrayList<Double> ts;
	public HashMap<Integer,ArrayList<Integer>> inn_list;
	public double[][] score_matrix;
	public SAX sax;
	public ArrayList<Integer> anomaly_index;
	
	public double compress_rate;
	public int alphasize;
	public int tree_num;
	
	public CABD() {
		
	}
	@Override
	public void run() {
		preprocess(timeseries);
		try {
			fit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init(Map<String, Object> args, TimeSeries timeseries) {
		this.timeseries=timeseries;
		this.compress_rate=(double) args.get("compress_rate");
		this.alphasize=(int) args.get("alphasize");
		this.tree_num=(int) args.get("tree_num");
	}
	public void preprocess(TimeSeries T) {
		double mean=DataHandler.calcMean(T);
		double std=DataHandler.calcStd(T);
		ts=new ArrayList<Double>();
		for(int i=0;i<T.getLength();i++) {
			ts.add((T.getTimePoint(i).getObsVal()-mean)/std);
		}
	}
	
	public void fit() throws Exception {
		ArrayList<Integer> candidate_list=seleCandidate();
		Collections.sort(candidate_list);
		ArrayList<Candidate> t_cand=new ArrayList<Candidate>();
		for(int i=0;i<candidate_list.size();i++) {
			double p=ts.get(candidate_list.get(i));
			Candidate c=new Candidate(candidate_list.get(i),p);
			t_cand.add(c);
		}

		calcINN(t_cand); 
		System.out.println("0"+inn_list.get(0));
		System.out.println("4"+inn_list.get(4));
		System.out.println("1214"+inn_list.get(1214));
		System.out.println("1216"+inn_list.get(1216));
		double[] ts1=new double[ts.size()];
		for(int i=0;i<ts.size();i++) {
			ts1[i]=ts.get(i);
		}
		sax=new SAX(ts1,compress_rate,alphasize);
		score_matrix=new double[t_cand.size()][3];
		
		long max_cs=-1;
		long min_cs=99999999999999999L;
		ArrayList<Long> cs=new ArrayList<Long>();
		ArrayList<Double> vs=new ArrayList<Double>();
		for(int i=0;i<t_cand.size();i++) {
			int index=t_cand.get(i).index;//index (in timeseries) i (in candidate)
			int ss_x=inn_list.get(index).size();
			double ms_x=(ss_x+0.0)/ts.size();
			long cs_x=calcCS(index);
			if(max_cs<cs_x) {
				max_cs=cs_x;
			}
			if(min_cs>cs_x) {
				min_cs=cs_x;
			}
			cs.add(cs_x);
			double vs_x=calcVS(index,ss_x);
			vs.add(vs_x);
			score_matrix[i][0]=ms_x;
			//score_matrix[i][1]=cs_x;
			score_matrix[i][2]=vs_x;
		}
		for(int i=0;i<t_cand.size();i++) {
			score_matrix[i][1]=(double)(cs.get(i)-min_cs)/(max_cs-min_cs);
		}
		
		//em cluster
		ArrayList<Attribute> attributes = new ArrayList<>();
		attributes.add(new Attribute("MS"));
		attributes.add(new Attribute("CS"));
		attributes.add(new Attribute("VS"));       
        Instances data = new Instances("scores",attributes,0);
        for(int i=0;i<score_matrix.length;i++) {
        	Instance instance = new DenseInstance(attributes.size());                   
        	for(int j=0;j<3;j++) {
        		 instance.setValue(j,score_matrix[i][j]);      
        	}  	
        	instance.setDataset(data); 
        	data.add(instance);
        }
        //System.out.println(data);
        int[] label=getLabel(data);
        
        //random forest
        ArrayList class_values = new ArrayList(2); 
        class_values.add("0");
        class_values.add("1"); 
        attributes.add(new Attribute("Class",class_values));
        Instances data1 = new Instances("scores",attributes,0);
        data1.setClassIndex(data.numAttributes() - 1);
        for(int i=0;i<score_matrix.length;i++) {
        	Instance instance = new DenseInstance(attributes.size());                   
        	for(int j=0;j<3;j++) {
        		 instance.setValue(j,score_matrix[i][j]);      
        	}
        	
        	instance.setDataset(data1); 
        	instance.setValue(3,label[i]);
        	data1.add(instance);
        }
		RandomForest rforest=new RandomForest();
		rforest.setNumIterations(tree_num);
		rforest.buildClassifier(data1);
		double error = rforest.measureOutOfBagError();
		anomaly_index= new ArrayList<Integer>();
		for (int i = 0; i < data1.size(); ++i) {
		        Instance inst = data1.get(i);
		        final double[] distributionForInstance = rforest.distributionForInstance(inst);	    
		        if(distributionForInstance[0]<distributionForInstance[1]) {
		        	anomaly_index.add(t_cand.get(i).index);
		        }
		}
		System.out.println();

	}
	private int[] getLabel(Instances data) throws Exception {
		EM em=new EM();
        em.setNumClusters(2);
        em.buildClusterer(data);
        ClusterEvaluation evaluation=new ClusterEvaluation();
        evaluation.setClusterer(em);
        evaluation.evaluateClusterer(new Instances(data));
        double[] cnum=evaluation.getClusterAssignments();
        System.out.println(evaluation.clusterResultsToString());
        String result=evaluation.clusterResultsToString();
        String ms_mean=result.split("\n")[13];
        String cs_mean=result.split("\n")[17];
        String vs_mean=result.split("\n")[21];
        ArrayList<Double> ms=new ArrayList<Double>();
        for(int i=0;i<2;i++) {
        	ms.add(Double.parseDouble(ms_mean.split("\\s+")[2+i]));
        }
        double msmax=Collections.max(ms);
        int normal1=ms.indexOf(msmax);
        ArrayList<Double> vs=new ArrayList<Double>();
        for(int i=0;i<2;i++) {
        	vs.add(Double.parseDouble(vs_mean.split("\\s+")[2+i]));
        }
        double vsmin=Collections.min(vs);
        int normal2=vs.indexOf(vsmin);
        if(normal1!=normal2) {
        	System.out.println("recheck cluster");
        }
        int[] label=new int[cnum.length];
        for(int i=0;i<cnum.length;i++) {
        	if(cnum[i]==normal1) {
        		label[i]=0;
        	}else {
        		label[i]=1;
        	}
        }
        return label;
	}
   
	private long calcCS(int index_in_timeseries) {
		ArrayList<Integer> inn=inn_list.get(index_in_timeseries);
		int[] INN=new int[inn.size()];
		for(int i=0;i<inn.size();i++) {
			INN[i]=inn.get(i);
		}
		String innstr=sax.toSax(INN);
		long cs=sax.countFrequency(sax.saxword,innstr);
		return cs;
	}
	

	public double calcVS(int index_in_timeseries,int ss) {
		if(ss==0) {
			return 1.0;
		}
		ArrayList<Integer> inn=inn_list.get(index_in_timeseries);
		ArrayList<Integer> spa=new ArrayList<Integer>();
		ArrayList<Double> adjacent=new ArrayList<Double>();
		int tmp=index_in_timeseries-1;
		int left=0;
		while(left<ss&&tmp>=0) {
			if(!inn.contains(tmp)){
				spa.add(tmp);
				adjacent.add(ts.get(tmp));
				left++;
			}
			tmp--;
		}
		tmp=index_in_timeseries+1;
		int right=0;
		while(right<ss&&tmp<ts.size()) {
			if(!inn.contains(tmp)){
				spa.add(tmp);
				adjacent.add(ts.get(tmp));
				right++;
			}
			tmp++;
		}
		/*for(int i=Math.max(0, index_in_timeseries-ss);i<Math.min(ts.size(),index_in_timeseries+ss+1);i++) {
			if(!inn.contains(i)){
				spa.add(i);
				adjacent.add(ts.get(i));
			}
		}*/
		double std1=calcstdforArray(adjacent); //srd(spa-inn)
		if(adjacent.size()==0) {
			std1=0.0;
		}
		spa.addAll(inn);
		ArrayList<Double> spa_value=new ArrayList<Double>();
		for(int i=0;i<spa.size();i++) {
			spa_value.add(ts.get(spa.get(i)));
		}
		double std2=calcstdforArray(spa_value);
		//double vs=(double)Math.abs(std1-std2)/std2;
		double vs=(double)(std2-std1)/std2;
		if(index_in_timeseries==0||index_in_timeseries==4||index_in_timeseries==8||index_in_timeseries==1214||index_in_timeseries==1215||index_in_timeseries==1216) {
			System.out.println(vs);
		}
		return vs;
	}
	
	public static double calcMeanforArray(ArrayList<Double> t) {
		  double sum=0;
		  for (int i = 0; i < t.size(); i++) {
	          sum += t.get(i);
	      }
		  double mean = sum/t.size();
		  return mean;
		  
	  }
    public double calcstdforArray(ArrayList<Double> t) {
    	 double total=0;
   	     double mean=calcMeanforArray(t);
   	     for (int i = 0; i<t.size(); i++) {
             total +=(t.get(i)-mean)*(t.get(i)-mean);
           }
   	     double std = Math.sqrt(total/(t.size()));
   	     return std;
    	
    }
	public ArrayList<Integer> seleCandidate() {
		ArrayList<Double> anomaly_score=calcASD();
		ArrayList<Integer> candidate_list=calcMAD(anomaly_score);
		return candidate_list;
	}
	public ArrayList<Double> calcASD() {
		ArrayList<Double> first_difference=new ArrayList<Double>();
		for(int i=0;i<ts.size();i++) {
			if(i!=0) {
				double fd=Math.abs(ts.get(i)-ts.get(i-1));
				first_difference.add(fd);	
			}else {
				double fd=Math.abs(ts.get(i)-ts.get(ts.size()-1));
				first_difference.add(fd);	
			}
			 
		}
		ArrayList<Double> second_difference=new ArrayList<Double>();
		for(int i=0;i<first_difference.size();i++) {
			if(i!=0) {
				double sd=Math.abs(first_difference.get(i)-first_difference.get(i-1));
				second_difference.add(sd);
			}else {
				double sd=Math.abs(first_difference.get(i)-first_difference.get(first_difference.size()-1));
				second_difference.add(sd);
			}
		}
		return second_difference;
	}
	public ArrayList<Integer> calcMAD(ArrayList<Double> anomaly_score) {
		double median_X=calcMedian(anomaly_score);
		ArrayList<Double> median_deviation=new ArrayList<Double>();
		for(int i=0;i<anomaly_score.size();i++) {
			double d=Math.abs(anomaly_score.get(i)-median_X);
			median_deviation.add(d);
		}
		double mad=calcMedian(median_deviation);
		ArrayList<Integer> candidate_list=new ArrayList<Integer>();
		for(int i=0;i<median_deviation.size();i++) {
			if(median_deviation.get(i)>mad) {
				candidate_list.add(i);
			}
		}
		return candidate_list;
	}
	public double calcMedian(ArrayList<Double> scorei) {
		double j = 0;
		ArrayList<Double> score=new ArrayList<Double>();
		score.addAll(scorei);
	    Collections.sort(score);
	    int size = score.size();
	    if(size % 2 == 1){
	    	j = score.get((size-1)/2);
	    }else {
	    	j = (score.get(size/2-1) + score.get(size/2) + 0.0)/2;
	    }
		return j;
	}
	
	public void calcINN(ArrayList<Candidate> t_cand) {
		//t_cand: index,observe
		ArrayList<ArrayList<Distance_X>> distance_matrix=calcdistanceall();
		inn_list= new HashMap<Integer,ArrayList<Integer>>();
		for(int i=0;i<t_cand.size();i++) {
			int flag=0;
			int r=1;
			ArrayList<Integer> inn_list_x=new ArrayList<Integer>();
			ArrayList<Distance_X> dx=distance_matrix.get(t_cand.get(i).index);
			int len_inn_x=0;
			while(flag==0) {
				ArrayList<Integer> knn_list_x=calcKNN(dx,r);
				for(int j=0;j<knn_list_x.size();j++) {
					ArrayList<Distance_X> dy=distance_matrix.get(knn_list_x.get(j));
					ArrayList<Integer> knn_list_y=calcKNN(dy,r);
					if(knn_list_y.contains(t_cand.get(i).index)&&!inn_list_x.contains(knn_list_x.get(j))) {
						inn_list_x.add(knn_list_x.get(j));
					}
				}
				if(inn_list_x.size()!=len_inn_x||inn_list_x.size()==0) {
					flag=0;
					len_inn_x=inn_list_x.size();
					r++;
				}else {
					flag=1;
				}
			}	
			Collections.sort(inn_list_x);
			inn_list.put(t_cand.get(i).index, inn_list_x);
		}
	}
	public ArrayList<Integer> calcKNN(ArrayList<Distance_X> dx ,int k) {
		ArrayList<Integer> knn_list=new ArrayList<Integer>();
		int count=0;
		/*for(int i=0;i<k;i++) {
			if(dx.get(i)==dx.get(i+1)) {
				knn_list.add(dx.get(i).index);
				knn_list.add(dx.get(i+1).index);
			}
			knn_list.add(dx.get(i).index);
		}*/
		int i=0;
		while(count<k&&i<dx.size()) {
			if(i>0&&dx.get(i)==dx.get(i-1)) {
				knn_list.add(dx.get(i).index);
				i++;
			}else {
				knn_list.add(dx.get(i).index);		
				i++;
				count++;
			}
		}
		return knn_list;
	}
	public ArrayList<ArrayList<Distance_X>> calcdistanceall() {
		ArrayList<ArrayList<Distance_X>> distance_matrix=new ArrayList<ArrayList<Distance_X>>();
		for(int i=0;i<ts.size();i++) {
			ArrayList<Distance_X> distance_x_list=new ArrayList<Distance_X>();
			for(int j=0;j<ts.size();j++) {
				if(j!=i) {
					double d=calcdistance(ts.get(i),ts.get(j));
					Distance_X dx=new Distance_X(j,d);
					distance_x_list.add(dx);
				}			
			}
			Collections.sort(distance_x_list);
			
			distance_matrix.add(distance_x_list);			
		}
		return distance_matrix;
	}
	
	public double calcdistance(double p1,double p2) {
		if((p1-p2)<0) {
			return p2-p1;
		}else {
			return p1-p2;
		}
	}
	public static void main(String[] args) {
		/*double[] data= {26.9,26.8,27.4,26.7,64.5, 65.1,62.1, 64.4, 62.2, 62.7, 27.1, 25.2, 25.4};
		TimeSeries t=new TimeSeries();
		for(int i=0;i<data.length;i++) {
			TimePoint p=new TimePoint(i,data[i]);
			t.addPoint(p);
		}
		CABD cabd=new CABD();
		cabd.preprocess(t);
		try {
			cabd.fit();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
		//[[1, 3, 10, 2, 12, 11], [0, 3, 10, 2, 12, 11], [], [], [7, 5], [], [8, 9], [4, 5, 9, 8, 6], [6, 9], [], [],[12],[11]]
		
		TimeSeries T = null;
	    FileHandler fh = new FileHandler();
	    T = fh.readData("test/real_1.csv");
	    System.out.println("Timeseries length: " + T.getLength());
	    CABD cabd=new CABD();
	    Map<String, Object> arg_list=new HashMap<String, Object>();
	    arg_list.put("compress_rate", 0.1);
	    arg_list.put("alphasize",10);
	    arg_list.put("tree_num",100);
	    cabd.init(arg_list, T);
	    cabd.run();
	   
	}

}

class Distance_X implements Comparable<Distance_X>{
	int index;
	double distance;
	
	public Distance_X(int index, double distance) {
		super();
		this.index = index;
		this.distance = distance;
	}

	@Override
	public int compareTo(Distance_X o) {
		if(this.distance>o.distance) {
			return 1;
		}else if(this.distance<o.distance) {
			return -1;
		}
		else {
			return 0;
		}
	}
}
class Candidate{
	int index;
	double observe;
	public Candidate(int index, double observe) {
		super();
		this.index = index;
		this.observe = observe;
	}
}
