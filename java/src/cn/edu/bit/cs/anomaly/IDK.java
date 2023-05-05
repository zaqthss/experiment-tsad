package cn.edu.bit.cs.anomaly;

import cn.edu.bit.cs.anomaly.entity.TimeSeries;
import cn.edu.bit.cs.anomaly.entity.TimeSeriesMulDim;
import cn.edu.bit.cs.anomaly.util.Constants;

import java.util.*;

public class IDK implements UniDimAlgorithm{

    public TimeSeries timeseries;
    public double[][] ts;
    public int t=100;
    public int psi1=16;
    public int psi2=2;
    public int width=100;
    public int top_k=0;
    public ArrayList<Double> score=new ArrayList<Double>();
    @Override
    public void run() {
        double[][] featuremap_count=IDK_T();
        double[] similarity_score=IK_inne_fm(featuremap_count);
        ArrayList<Double> anomaly_score=new ArrayList<Double>();
        for(int i=0;i<similarity_score.length;i++){
            anomaly_score.add(-similarity_score[i]);
        }
        anomaly_score=MaxMinScaler(anomaly_score);
        ArrayList<Double> joinall=new ArrayList<Double>();
        for(int i=0;i<anomaly_score.size()-1;i++){
            for(int j=0;j<width;j++){
                joinall.add(anomaly_score.get(i));
            }
        }
        int left=timeseries.getLength()-joinall.size();
        for(int j=0;j<left;j++){
            joinall.add(anomaly_score.get(anomaly_score.size()-1));
        }
        score.addAll(joinall);
        for (int i = 0; i < Math.min(this.top_k, anomaly_score.size()); i++) {
            double max_score=Collections.max(anomaly_score);
            int max_index_begin = anomaly_score.indexOf(max_score);
            for (int j = max_index_begin*width;
                 j < Math.min(width + max_index_begin*width, timeseries.getLength());
                 j++) {
                timeseries.getTimePoint(j).setIs_anomaly(Constants.IS_ANOMALY.TRUE);
            }
            anomaly_score.set(max_index_begin,-999999.0);
        }
    }
    public ArrayList<Double> getScore(){
        return score;
    }

    @Override
    public void init(Map<String, Object> args, TimeSeries timeseries) {
        this.timeseries = timeseries;
        timeseries.clear();
        this.t = (int) args.get("sample_num");
        this.psi1 = (int) args.get("sample_size1");
        this.psi2 = (int) args.get("sample_size2");
        this.width = (int) args.get("window_size");
        this.top_k = (int) args.get("top_k");
        ts = new double[timeseries.getLength()][1];
        for (int i = 0; i < timeseries.getLength(); i++) {
            double temp = timeseries.getTimePoint(i).getObsVal();
            ts[i][0]=temp;
        }
    }

    public double getMax(ArrayList<Double> a){
        double max= Collections.max(a);
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
    private double [][] IDK_T(){
        int window_num=(int)Math.ceil(ts.length/(this.width*1.0));
        assert window_num <psi1 : "Sample size1 is too big";
        double [][]featuremap_count=new double[window_num][t*psi1];
        int [][]onepoint_matrix=new int[ts.length][t];

        for(int i=0;i<onepoint_matrix.length;i++){
            for(int j=0;j<onepoint_matrix[0].length;j++)
                onepoint_matrix[i][j]=-1;
        }

        for(int time=0;time<t;time++){
            int sample_num=psi1;
            ArrayList<Integer> sample_list=random_sample(sample_num,ts.length,time);
           // ArrayList<Integer> sample_list=new ArrayList<Integer>();
            //sample_list.add(20844);
           // sample_list.add(30207);
           // sample_list.add(21977);
            double[][] sample=new double[sample_num][1];
            for(int i=0;i<sample_num;i++){
                sample[i][0]=ts[sample_list.get(i)][0];
            }

            double[][] square_ts=new double[ts.length][1];
            for(int i=0;i<ts.length;i++){
                for(int j=0;j<1;j++){
                    square_ts[i][j]=Math.pow(ts[i][0],2);
                }
            }
            double[][] square_ones=new double[1][sample_num];
            for(int i=0;i<1;i++){
                for(int j=0;j<sample_num;j++){
                    square_ones[i][j]=1;
                }
            }
            double[][] term1=dot(square_ts,square_ones);

            double[][] ts_ones=new double[ts.length][1];
            for(int i=0;i<ts.length;i++){
                for(int j=0;j<1;j++){
                    ts_ones[i][j]=1;
                }
            }
            double[][] square_sample=new double[1][sample_num];
            for(int i=0;i<1;i++){
                for(int j=0;j<sample_num;j++){
                    square_sample[i][j]=Math.pow(sample[j][0],2);
                }
            }

            double[][] term2=dot(ts_ones,square_sample);
            double[][] ts_dot_sample=dot(ts,transform(sample));
            double[][] point2sample=new double[ts.length][sample_num];
            for(int i=0;i<ts.length;i++){
                for(int j=0;j<sample_num;j++){
                    point2sample[i][j]=term1[i][j]+term2[i][j]-2*ts_dot_sample[i][j];
                }
            }
            double[][] sample2sample=new double[sample_num][sample_num];
            for(int i=0;i<sample2sample.length;i++){
                for(int j=0;j<sample2sample[i].length;j++){
                    if(i==j){
                        sample2sample[i][j]=999999;
                    }else{
                        sample2sample[i][j]=point2sample[sample_list.get(i)][j];
                    }
                }
            }
            HashMap<Integer,Object> map1=findmin(sample2sample);
            double[] radius_list= (double[]) map1.get(1);
            HashMap<Integer,Object> map2=findmin(point2sample);
            int[] min_dist_point2sample= (int[]) map2.get(2);

            for(int i=0;i<ts.length;i++){
                if(point2sample[i][min_dist_point2sample[i]]<radius_list[min_dist_point2sample[i]]){
                    onepoint_matrix[i][time]=min_dist_point2sample[i]+time*psi1;
                    featuremap_count[i/width][onepoint_matrix[i][time]]+=1;
                }
            }
            
        }
        //feature map of D/width
        for(int i=0;i<ts.length/width;i++){
            for(int j=0;j<psi1*t;j++){
                featuremap_count[i][j] /= (width*1.0);
            }
        }
        int isextra = ts.length - (int)(ts.length / (width*1.0)) * width;
        if(isextra>0){
            for(int j=0;j<psi1*t;j++){
                featuremap_count[window_num-1][j] /= (isextra*1.0);
            }
        }
        return featuremap_count;
    }

    private double[] IK_inne_fm(double[][] X){
        double [][]onepoint_matrix=new double[X.length][t*psi2];
        for(int i=0;i<onepoint_matrix.length;i++){
            for(int j=0;j<onepoint_matrix[0].length;j++)
                onepoint_matrix[i][j]=0;
        }
        assert X.length <psi2 : "Sample size2 is too big";
        for(int time=0;time<t;time++){
            int sample_num=psi2;
            ArrayList<Integer> sample_list=random_sample(sample_num,X.length,time);
            //ArrayList<Integer> sample_list=new ArrayList<Integer>();
            //sample_list.add(17);
            //sample_list.add(7);
            double[][] sample=new double[sample_num][X[0].length];
            for(int i=0;i<sample_num;i++){
                for(int j=0;j<X[i].length;j++){
                    sample[i][j]=X[sample_list.get(i)][j];
                }
            }

            double[][] square_x=new double[X.length][X[0].length];
            for(int i=0;i<X.length;i++){
                for(int j=0;j<X[0].length;j++){
                    square_x[i][j]=Math.pow(X[i][j],2);
                }
            }
            double[][] sample_ones=new double[X[0].length][sample_num];
            for(int i=0;i<X[0].length;i++){
                for(int j=0;j<sample_num;j++){
                    sample_ones[i][j]=1;
                }
            }
            double[][] term1=dot(square_x,sample_ones);

            double[][] x_ones=new double[X.length][X[0].length];
            for(int i=0;i<X.length;i++){
                for(int j=0;j<X[0].length;j++){
                    x_ones[i][j]=1;
                }
            }
            double[][] square_sample=new double[X[0].length][sample_num];
            for(int i=0;i<X[0].length;i++){
                for(int j=0;j<sample_num;j++){
                    square_sample[i][j]=Math.pow(sample[j][i],2);
                }
            }

            double[][] term2=dot(x_ones,square_sample);
            double[][] ts_dot_sample=dot(X,transform(sample));
            double[][] point2sample=new double[X.length][sample_num];
            for(int i=0;i<X.length;i++){
                for(int j=0;j<sample_num;j++){
                    point2sample[i][j]=term1[i][j]+term2[i][j]-2*ts_dot_sample[i][j];
                }
            }
            double[][] sample2sample=new double[sample_num][sample_num];
            for(int i=0;i<sample2sample.length;i++){
                for(int j=0;j<sample2sample[i].length;j++){
                    if(i==j){
                        sample2sample[i][j]=999999;
                    }else{
                        sample2sample[i][j]=point2sample[sample_list.get(i)][j];
                    }
                }
            }
            HashMap<Integer,Object> map1=findmin(sample2sample);
            double[] radius_list= (double[]) map1.get(1);
            HashMap<Integer,Object> map2=findmin(point2sample);
            int[] min_dist_point2sample_index= (int[]) map2.get(2);
            int[] min_dist_point2sample=new int[X.length];
            for(int i=0;i<X.length;i++){
                min_dist_point2sample[i]=min_dist_point2sample_index[i]+time*psi2;
            }

            double[] point2sample_value=new double[X.length];
            for(int i=0;i<X.length;i++){
                point2sample_value[i]=point2sample[i][min_dist_point2sample_index[i]];
            }

            for(int i=0;i<onepoint_matrix.length;i++){
                if(point2sample_value[i]<radius_list[min_dist_point2sample_index[i]]){
                    onepoint_matrix[i][min_dist_point2sample[i]]=1;
                }
            }
        }
        double[][]feature_mean_map=new double[onepoint_matrix[0].length][1];
        for(int i=0;i<onepoint_matrix[0].length;i++){
            double sum=0;
            for(int j=0;j<onepoint_matrix.length;j++){
                sum+=onepoint_matrix[j][i];
            }
            sum/=(onepoint_matrix.length*1.0);
            feature_mean_map[i][0]=sum;
        }

        feature_mean_map=dot(onepoint_matrix,feature_mean_map);
        double[] result=new double[feature_mean_map.length];
        for(int i=0;i<result.length;i++){
            result[i]=feature_mean_map[i][0]/t;
        }
        return result;
    }

    private HashMap<Integer,Object> findmin(double[][] matrix) {
        HashMap<Integer,Object> map=new HashMap<Integer,Object>();
        double[] result=new double[matrix.length];
        int[] indexs=new int[matrix.length];
        for(int i=0;i<matrix.length;i++){
            double min = 999999;
            int index=0;
            for (int j=0;j<matrix[i].length;j++) {
                if (matrix[i][j] <min) {
                    index=j;
                    min =matrix[i][j] ;
                }
            }
            result[i]=min;
            indexs[i]=index;
        }
        map.put(1,result);
        map.put(2,indexs);
        return map;
    }

    private ArrayList<Integer> random_sample(int sample_num,int max,int seed){
        ArrayList<Integer> sample_list=new ArrayList<Integer>();
        Random random = new Random(seed);
        int num=0;
        boolean[] bool = new boolean[max];
        for (int i = 0; i < sample_num; i++) {
            do {
                num = random.nextInt(max);
            } while (bool[num]);
            bool[num] = true;
            sample_list.add(num);
        }
        return sample_list;
    }

    private double[][] dot(double[][] matrix1,double[][] matrix2){
        double[][] matrix3=new double[matrix1.length][matrix2[0].length];
        for (int i = 0; i < matrix1.length; i++) {
            for (int j = 0; j < matrix1[i].length; j++) {
                for (int k = 0; k < matrix2[j].length; k++) {
                    matrix3[i][k] += matrix1[i][j] * matrix2[j][k];
                }
            }
        }
        return matrix3;
    }

    private double[][] transform(double[][] matrix){
        double[][] matrix_t=new double[matrix[0].length][matrix.length];
        for(int i=0;i<matrix.length;i++){
            for(int j=0;j<matrix[i].length;j++){
                matrix_t[j][i]=matrix[i][j];
            }
        }
        return matrix_t;
    }
}
