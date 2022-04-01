package cn.edu.bit.cs.anomaly.util.clustering;


import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.math3.complex.Complex;

import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.jblas.ComplexDoubleMatrix;
import org.jblas.DoubleMatrix;
import org.jblas.Eigen;


public class KShape {
	public ArrayList<ArrayList<Double>> t_list;
	public int k;
	public ArrayList<ArrayList<Double>> centroids;
	public ArrayList<Integer> idx;
	public boolean check_empty_flag=false;
	
	
	public KShape(ArrayList<ArrayList<Double>> t_list,int k) {
		super();
		this.t_list=t_list;
		this.k = k;
	}
	
	public ArrayList<Double> zscore(ArrayList<Double> t,int ddof) {
		double mean=calcMean(t);
		double std=calcStd(t,ddof);
		for (int i = 0; i <t.size(); i++) {
	          t.set(i,(t.get(i)-mean)/std);
	        }
		return t;	
	}
	public static double calcMean(ArrayList<Double> t) {
		  double sum=0;
		  for (int i = 0; i < t.size(); i++) {
	          sum += t.get(i);
	      }
		  double mean = sum/t.size();
		  return mean;
		  
	  }
	  public static double calcStd(ArrayList<Double> t,int ddof) {
		  double total=0;
		  double mean=calcMean(t);
		  for (int i = 0; i<t.size(); i++) {
	          total +=
	              (t.get(i)-mean)
	                  * (t.get(i)-mean);
	        }
		  int dev=t.size();
		  if(ddof==1) {
			  dev=dev-1;
		  }
		  double std = Math.sqrt(total/dev);
 		  return std;
		  
	  }
	public Sbd calcSBDdistance(ArrayList<Double> x,ArrayList<Double>y) {	
		assert x.size()== y.size() : "length must be equal";
		Ncc NN=calcNCC(x,y);
		double ncc=NN.dist;
		int index=NN.index;
		double dist=1-ncc;
		
		int s=index-x.size();
		ArrayList<Double> y_shift=new ArrayList<Double>();
		if(s>0) {
			for(int i=0;i<s;i++) {
				y_shift.add(0.0);
			}
			for(int i=0;i<x.size()-s;i++) {
				y_shift.add(y.get(i));
			}
		}else if(s==0) {
			y_shift=y;
		}else {
			for(int i=-s;i<x.size();i++) {
				y_shift.add(y.get(i));
			}
			for(int i=0;i<-s;i++) {
				y_shift.add(0.0);
			}
		}
		Sbd sbd=new Sbd(dist,y_shift);
		return sbd;
	}

	private  Ncc calcNCC(ArrayList<Double>x, ArrayList<Double>y) {
		int len=x.size();
		double pow_x=0;
		double pow_y=0;
		int FFTlen =(int) Math.pow(2, Math.ceil(Math.log(2*len-1)/Math.log(2)));
		double[] xx=new double[FFTlen];
		double[] yy=new double[FFTlen];
		for(int i=0;i<len;i++) {
			pow_x+=Math.pow(x.get(i), 2);
			pow_y+=Math.pow(y.get(i), 2);
			xx[i]=x.get(i);
			yy[i]=y.get(i);
		}
		double dist_x=Math.pow(pow_x, 0.5);
		double dist_y=Math.pow(pow_y, 0.5);
		double dist_xy=dist_x*dist_y;

		for(int i=len;i<FFTlen;i++) {
			xx[i]=0.0;
			yy[i]=0.0;
		}
		FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
		Complex[] result_x = fft.transform(xx, TransformType.FORWARD);
		Complex[] result_y = fft.transform(yy, TransformType.FORWARD);
		Complex[] result_xy=new Complex[result_x.length];;
		for(int i=0;i<result_y.length;i++) {
			result_y[i]=result_y[i].conjugate();
			result_xy[i]=result_x[i].multiply(result_y[i]);
		}
		Complex[] cc=fft.transform(result_xy, TransformType.INVERSE);
		double[] dd=new double[len*2];
		int k=0;
		for(int i=cc.length-len+1;i<cc.length;i++) {
			dd[k++]=cc[i].getReal();
		}
		for(int i=0;i<len;i++) {
			dd[k++]=cc[i].getReal();
		}
        double max=-99999;
        int max_id=0;
        for(int i=0;i<dd.length;i++) {
        	if(dd[i]>max) {
        		max=dd[i];
        		max_id=i;
        	}
        }
        double dist=(double)max/dist_xy;
        Ncc ncc=new Ncc(max_id,dist);	
		return ncc;
	}
	
	private boolean check_empty() {
		ArrayList<Integer> idx_unique=new ArrayList<Integer>();
		for(int i=0;i<idx.size();i++) {
			if(!idx_unique.contains(idx.get(i))){
				idx_unique.add(idx.get(i));
			}
		}
		boolean flag=false;
		for(int i=0;i<k;i++) {
			if(!idx_unique.contains(i)) {
				flag=true;
				break;
			}
		}	
		return flag;
	}
	
	public Clusters kshape(){
		int max_attempts=10;
		int n_attempts=0;	
		Clusters cs=new Clusters();
		while(n_attempts<max_attempts) {
			check_empty_flag=false;
			n_attempts++;
			cs=_kshape(n_attempts);		
			if(!check_empty_flag) {
				System.out.println("kshape done");
				break;
			}
		}
		return cs;
	}
	private Clusters _kshape(int n_attempts) {
		int m=t_list.size();
		int n=t_list.get(0).size();
		idx=new ArrayList<Integer>();
		for(int i=0;i<m;i++) {
			idx.add(0);
		}
		centroids=new ArrayList<ArrayList<Double>> ();
		double old_inertia=999999;
		double new_inertia = 0;
		//random centroids
		Random rand=new Random(n_attempts);
		for(int i=0;i<k;i++) {
			ArrayList<Double> temp=new ArrayList<Double>();
			int r=rand.nextInt(m);
			temp.addAll(t_list.get(r));
			centroids.add(temp);
		}
		/*ArrayList<Double> temp=new ArrayList<Double>();
		temp.addAll(t_list.get(32));
		centroids.add(temp);
		temp=new ArrayList<Double>();
		temp.addAll(t_list.get(166));
		centroids.add(temp);
		temp=new ArrayList<Double>();
		temp.addAll(t_list.get(163));
		centroids.add(temp);
		temp=new ArrayList<Double>();
		temp.addAll(t_list.get(127));
		centroids.add(temp);
		temp=new ArrayList<Double>();
		temp.addAll(t_list.get(63));
		centroids.add(temp);
		temp=new ArrayList<Double>();
		temp.addAll(t_list.get(177));
		centroids.add(temp);*/
		//set init label
		double[][] dist_matrix=new double[m][k];
		for(int j=0;j<m;j++) {
			double mindist=999999;
			for(int t=0;t<k;t++) {
				Sbd sbd=calcSBDdistance(centroids.get(t),t_list.get(j));
				double dist=sbd.dist;
				dist_matrix[j][t]=dist;
				if(dist<mindist) {
					mindist=dist;
					idx.set(j, t);
				}			
			}
		}
		new_inertia = compute_inertia(dist_matrix);
		
		for(int i=0;i<100;i++) {
			//init old
			ArrayList<ArrayList<Double>> old_cluster_centers=new ArrayList<ArrayList<Double>> ();
			old_cluster_centers.addAll(centroids);
			ArrayList<Integer> old_idx=new ArrayList<Integer>();
			old_idx.addAll(idx);

			//Refinement step (update center)
			for(int j=0;j<k;j++) {
				ArrayList<Double> cur_center=extract_shape(j, centroids.get(j));
				centroids.set(j,cur_center);
			}
			//Assignment step (update label)
			dist_matrix=new double[m][k];
			for(int j=0;j<m;j++) {
				double mindist=999999;
				for(int t=0;t<k;t++) {
					Sbd sbd=calcSBDdistance(centroids.get(t),t_list.get(j));
					double dist=sbd.dist;
					dist_matrix[j][t]=dist;
					if(dist<mindist) {
						mindist=dist;
						idx.set(j, t);
					}			
				}
			}
			//Error handling (avoid empty clusters)
			check_empty_flag=check_empty();
			if(check_empty_flag) {
				break;
			}
			new_inertia = compute_inertia(dist_matrix);					
			if(Math.abs(old_inertia-new_inertia)<1e-6||(old_inertia-new_inertia<0)) {
				centroids=old_cluster_centers;
				idx=old_idx;
				break;
			}
			old_inertia=new_inertia ;
		}
		Clusters cs=new Clusters(idx,centroids);
		return cs;
	}


	private double compute_inertia(double[][] dist_matrix) {
		int n=dist_matrix.length;
		double sum=0;
		for(int i=0;i<n;i++) {
			sum+=Math.pow(dist_matrix[i][idx.get(i)], 2);
		}
		sum=(double)sum/n;
		return sum;
	}

	private ArrayList<Double> extract_shape(int jj,
			ArrayList<Double> cur_center) {
		ArrayList<ArrayList<Double>> a=new ArrayList<ArrayList<Double>>();
		for(int i=0;i<idx.size();i++) {
			ArrayList<Double> opt_x=new ArrayList<Double>();
			if(idx.get(i)==jj) {
				Sbd sbd=calcSBDdistance(cur_center,t_list.get(i));
				opt_x.addAll(sbd.y_shift);
				a.add(opt_x);
			}
			
		}
		if(a.size()==0) {
			ArrayList<Double> zero=new ArrayList<Double>();
			for(int i=0;i<t_list.get(0).size();i++) {
				zero.add(0.0);
			}
			return zero;
		}
		
		double[][] aa=new double[a.size()][a.get(0).size()];
		for(int i=0;i<a.size();i++) {
			for(int j=0;j<a.get(i).size();j++) {
				aa[i][j]=a.get(i).get(j);
			}
		}
		int columns=a.get(0).size();
        DoubleMatrix X=new DoubleMatrix(aa);
        DoubleMatrix Y=X.transpose();
        DoubleMatrix S=Y.mmul(X);
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
		return zscore(cent,0);
	}
	
}

class Ncc{
	int index;
	double dist;
	public Ncc(int index, double dist) {
		super();
		this.index = index;
		this.dist = dist;
	}
}

class Sbd{
	double dist;
	ArrayList<Double> y_shift;
	public Sbd(double dist, ArrayList<Double> y_shift) {
		super();
		this.dist = dist;
		this.y_shift = y_shift;
	}
	
}
