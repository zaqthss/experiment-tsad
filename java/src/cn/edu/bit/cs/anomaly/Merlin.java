package cn.edu.bit.cs.anomaly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import cn.edu.bit.cs.anomaly.entity.TimeSeries;
import cn.edu.bit.cs.anomaly.util.Constants.IS_ANOMALY;

public class Merlin implements UniDimAlgorithm {
	public TimeSeries T;
	public ArrayList<Double> timeseries;
    public int minL;
    public int maxL;
    public int k;
    public int MIN=-999999;
    public double[][] distances;
    public int[][] indices;
    public int[] lengths;
	@Override
	public void run() {
		// TODO Auto-generated method stub
		MERLIN_topK(this.timeseries);
		for(int i=0;i<k;i++) {
			int index=indices[0][i];
			for(int j=index;j<Math.min(index+minL,T.getLength());j++) {
				T.getTimePoint(j).setIs_anomaly(IS_ANOMALY.TRUE);
			}		
		}
	}

	@Override
	public void init(Map<String, Object> args, TimeSeries T) {
		// TODO Auto-generated method stub
		this.T = T;
	    this.minL = (int) args.get("minL");
	    this.maxL = (int) args.get("maxL");
	    this.k = (int) args.get("top_k");
	    T.clear();
	    this.timeseries=new ArrayList<Double>();
	    for(int i=0;i<T.getLength();i++) {
	    	this.timeseries.add(T.getTimePoint(i).getObserve());
	    }
	}
	
	public void MERLIN_topK(ArrayList<Double> timeseries) {
		int numLengths=maxL-minL+1;
		distances=new double[numLengths][k];
		indices=new int[numLengths][k];
		lengths=new int[numLengths];
		for(int i=0;i<numLengths;i++) {
			for(int j=0;j<k;j++) {
				distances[i][j]=MIN;
				indices[i][j]=0;
			}
		}
		for(int i=minL;i<=maxL;i++) {
			lengths[i-minL]=i;
		}
		double kMultiplier = 1;
		double r=2*Math.sqrt(minL);
		ArrayList<Integer> exclusionIndices=new ArrayList<Integer>();
		for(int ki=0;ki<k;ki++) {
			while(distances[0][ki]<0) {
				HashMap<Integer,Object> find_re=DRAG_topK(timeseries, lengths[0], r*kMultiplier, exclusionIndices);
				distances[0][ki]=(double) find_re.get(1);
				indices[0][ki]=(int) find_re.get(2);
				if(ki==0) {
					r=r*0.5;
				}else {
					kMultiplier=kMultiplier*0.95;
				}
			}
			exclusionIndices.add(indices[0][ki]);
		}
		if(numLengths<2) return;
		for(int i=1;i<5;i++) {
			if(i>numLengths) return;
			exclusionIndices=new ArrayList<Integer>();
			kMultiplier = 1;
			r=distances[i-1][0]*0.99;
			for(int ki=0;ki<k;ki++) {
				while(distances[i][ki]<0) {
					HashMap<Integer,Object> find_re=DRAG_topK(timeseries, lengths[i], r*kMultiplier, exclusionIndices);
					distances[i][ki]=(double) find_re.get(1);
					indices[i][ki]=(int) find_re.get(2);
					if(ki==0) {
						r=r*0.99;
					}else {
						kMultiplier=kMultiplier*0.95;
					}
				}
				exclusionIndices.add(indices[i][ki]);
			}
		}
		
		if(numLengths<6) return;
		for(int i=5;i<numLengths;i++) {
			exclusionIndices=new ArrayList<Integer>();
		    kMultiplier = 1;
		    ArrayList<Double> disc=new ArrayList<Double>();
		    for(int j=i-5;j<i;j++) {
		    	disc.add(distances[j][0]);
		    }
		    double m=calcMean(disc);
		    double s=calcStd(disc,1);
		    r=m-2*s;
		    for(int ki=0;ki<k;ki++) {
		    	while(distances[i][ki]<0) {
		    		HashMap<Integer,Object> find_re=DRAG_topK(timeseries, lengths[i], r*kMultiplier, exclusionIndices);
					distances[i][ki]=(double) find_re.get(1);
					indices[i][ki]=(int) find_re.get(2);
					r=r*0.99;
					if(ki==0) {
						r=r*0.99;
					}else {
						kMultiplier=kMultiplier*0.95;
					}
		    	}
		    	exclusionIndices.add(indices[i][ki]);
		    }
		}
		
	}

	private HashMap<Integer, Object> DRAG_topK(ArrayList<Double> timeseries, int L, double r, ArrayList<Integer> exclusionIndices) {
		// TODO Auto-generated method stub
		HashMap<Integer,Object> find_re=new HashMap<Integer,Object>();
		if(Math.floor(timeseries.size()/2)<L||L<4) {
			 System.out.print("Subsequence length parameter must be in the range of 4 < L <= floor(length(TS)/2)");
			 return null;
		}
		int min_separation = L;
		int subseqcount=timeseries.size()-L+1;
		ArrayList<Double> mu=movmean(timeseries,L);
		ArrayList<Double> sig=movstd(timeseries,0,L);
	       
	    //Algorithm 2: Candidates Selection Phase
	    HashMap<Integer,ArrayList<Double>> C=new HashMap<Integer,ArrayList<Double>>();
	    
	    ArrayList<Double> cvalue=new ArrayList<Double>();
	    for(int i=0;i<L;i++) {
	    	cvalue.add((double)(timeseries.get(i)-mu.get(0))/sig.get(0));
	    }
	    C.put(0, cvalue);
	    Iterator<Map.Entry<Integer, ArrayList<Double>>> it = C.entrySet().iterator();
	    for(int i=1;i<subseqcount;i++) {
	    	ArrayList<Double> si=new ArrayList<Double>();
	    	for(int j=0;j<L;j++) {
		    	si.add((double)(timeseries.get(i+j)-mu.get(i))/sig.get(i));
		    }
	    	boolean isCand=true;
	    	it = C.entrySet().iterator();
	    	while(it.hasNext()) {
	    		Entry<Integer, ArrayList<Double>> ci = it.next();
	    		Integer cand=ci.getKey();
	    		if(Math.abs(cand-i)<min_separation) {
	    			continue;
	    		}
	    		double d=calcEuclideanDistance(si,C.get(cand));
	    		if(d<r) {
	    			it.remove();
	    			isCand=false;
	    			break;
	    		}
	    	}
	    	if(isCand) {
	    		C.put(i, si);
	    	}
	    }
	    Set<Integer> cands=C.keySet();
	    it = C.entrySet().iterator();
	    while(it.hasNext()){
	    	Entry<Integer, ArrayList<Double>> ci = it.next();
    		Integer cand=ci.getKey();
	    	for(Integer ex :exclusionIndices) {
	    		if(Math.abs(ex-cand)<L) {
	    			it.remove();
	    			break;
	    		}
	    	}
	    }
	    if(C.isEmpty()) {
	    	System.out.println("Failure: The r parameter of "+r+" was too large for the algorithm to work, try making it smaller");
	    	find_re.put(1, (double)MIN);
	    	find_re.put(2, 0);
	    	return find_re;
	    }
	    
	    //Algorithm 1: Discord Refinement Phase
	    HashMap<Integer,Double> cand_nndists2=new HashMap<Integer,Double>();
	    HashMap<Integer,Integer> cand_nn_pos=new HashMap<Integer,Integer>();
	    for(Integer cand : cands) {
	    	cand_nndists2.put(cand, (double) 999999);
	    	cand_nn_pos.put(cand, 0);
	    }

	    double r2=r*r;
	    for(int i=0;i<subseqcount;i++) {
	    	if(C.isEmpty()) {
	    		break;
	    	}
	    	cands=C.keySet();
	    	ArrayList<Double> si=new ArrayList<Double>();
	    	for(int j=0;j<L;j++) {
		    	si.add((double)(timeseries.get(i+j)-mu.get(i))/sig.get(i));
		    }
	    	it = C.entrySet().iterator();
	    	while(it.hasNext()){
	    		Entry<Integer, ArrayList<Double>> ci = it.next();
	    		Integer cand=ci.getKey();
	    		if(Math.abs(cand-i)<min_separation) {
	    			continue;
	    		}
	    		double dist2 = 0;
	    		ArrayList<Double> Candj=C.get(cand);
	    		double nndist2=cand_nndists2.get(cand);
	    		for(int t=0;t<L;t++) {
	    			dist2+=Math.pow(si.get(t)-Candj.get(t),2);
	    			if(dist2>nndist2) {
	    				break;
	    			}
	    		}
	    		if(dist2<r2) {	
	    			it.remove();
	    			cand_nndists2.remove(cand);
	    			cand_nn_pos.remove(cand);
	    		}else if(dist2 < cand_nndists2.get(cand)) {
	    			cand_nndists2.put(cand, dist2);
	    			cand_nn_pos.put(cand, i);
	    		}
	    	}    	
	    }
	    if(C.isEmpty()) {
	    	System.out.println("Failure: The r parameter of "+r+" was too large for the algorithm to work, try making it smaller");
	    	find_re.put(1, (double)MIN);
	    	find_re.put(2, 0);
	    	return find_re;
	    }else {
	    	double disc_dist2=0;
	    	int disc_loc=0;
	    	Set<Integer> keys=cand_nndists2.keySet();
	    	for(Integer key : keys) {
	    		if(cand_nndists2.get(key)>disc_dist2) {
	    			disc_dist2=cand_nndists2.get(key);
	    			disc_loc=key;
	    		}
	    	}
	    	disc_dist2=Math.sqrt(disc_dist2);
	    	int disc_nnloc=cand_nn_pos.get(disc_loc);
	    	System.out.println("The top discord of length "+L+" is at "+disc_loc);
	    	find_re.put(1, disc_dist2);
	    	find_re.put(2, disc_loc);
	    }
		return find_re;
	}
	
	public ArrayList<Double> movmean(ArrayList<Double> t,int L){
		ArrayList<Double> mu=new ArrayList<Double>();
		for(int i=0;i<t.size()-L+1;i++) {
			ArrayList<Double> s=new ArrayList<>(t.subList(i, i+L));
			mu.add(calcMean(s));
		}
		return mu;
		
	}
	public ArrayList<Double> movstd(ArrayList<Double> t,int ddof,int L){
		ArrayList<Double> sig=new ArrayList<Double>();
		for(int i=0;i<t.size()-L+1;i++) {
			ArrayList<Double> s=new ArrayList<>(t.subList(i, i+L));
			sig.add(calcStd(s,ddof));
		}
		return sig;
		
	}
	public double calcMean(ArrayList<Double> t) {
		    double sum = 0;
		    for (int i = 0; i < t.size(); i++) {
		      sum += t.get(i);
		    }
		    double mean = sum / t.size();
		    return mean;
		  }

	 public double calcStd(ArrayList<Double> t, int ddof) {
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
	 public static double calcEuclideanDistance(ArrayList<Double> t1, ArrayList<Double> t2) {
		    double dis = 0;
		    assert t1.size() == t2.size() : "length must be equal";
		    int len = t1.size();
		    for(int i = 0; i < len; i++) {
		      double temp = t1.get(i) - t2.get(i) ;
		      dis += Math.pow(temp, 2);
		    }
		    return Math.pow(dis, 0.5);
	}
}
