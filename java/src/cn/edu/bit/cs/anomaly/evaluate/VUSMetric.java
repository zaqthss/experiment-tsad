package cn.edu.bit.cs.anomaly.evaluate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class VUSMetric {

    public HashMap<Integer,Double> RangeAUC(ArrayList<Integer> labels, ArrayList<Double> score,int window){
        ArrayList<Double> score_sorted=new ArrayList<Double>();
        score_sorted.addAll(score);
        Collections.sort(score_sorted);
        Collections.reverse(score_sorted);
        int P=0;
        for(int i=0;i<labels.size();i++){
            P+=labels.get(i);
        }
        ArrayList<Double> label = extend_postive_range(labels, window);
        ArrayList<ArrayList<Integer>> L = range_convers_new(label);
        ArrayList<Double> TPR_list =new ArrayList<Double>();
        ArrayList<Double> FPR_list =new ArrayList<Double>();
        ArrayList<Double> Precision_list =new ArrayList<Double>();
        TPR_list.add(0.0);
        FPR_list.add(0.0);
        Precision_list.add(1.0);
        ArrayList<Integer> tlist=creatLinspace(0,score.size()-1,250);
        for(int t=0;t<tlist.size();t++){
            int i=tlist.get(t);
            double threshold=score_sorted.get(i);
            ArrayList<Integer> pred=new ArrayList<Integer>();
            for (int j=0;j<score.size();j++){
                if(score.get(j)>=threshold){
                    pred.add(1);
                }else{
                    pred.add(0);
                }
            }
            HashMap<Integer,Double> confusion=TPR_FPR_RangeAUC(label, pred, P,L);
            double TPR=confusion.get(1);
            double FPR=confusion.get(2);
            double Rprecision=confusion.get(3);
            TPR_list.add(TPR);
            FPR_list.add(FPR);
            Precision_list.add(Rprecision);
        }
        TPR_list.add(1.0);
        FPR_list.add(1.0); //otherwise, range-AUC will stop earlier than (1,1)

        double AUC_range=0.0;
        double AP_range=0.0;
        ArrayList<Double> width=new ArrayList<Double>();
        ArrayList<Double> height=new ArrayList<Double>();
        ArrayList<Double> width_PR=new ArrayList<Double>();
        ArrayList<Double> height_PR=new ArrayList<Double>();
        for (int i=0;i<TPR_list.size()-1;i++){
            width.add(FPR_list.get(i+1)-FPR_list.get(i));
            height.add((double)(TPR_list.get(i)+TPR_list.get(i+1))/2);
            if(i<TPR_list.size()-2){
                width_PR.add(TPR_list.get(i+1)-TPR_list.get(i));
                height_PR.add((double)(Precision_list.get(i)+Precision_list.get(i+1))/2);
            }

        }
        for (int i=0;i<width.size();i++){
            AUC_range+=width.get(i)*height.get(i);
            if(i<width.size()-1){
                AP_range+=width_PR.get(i)*height_PR.get(i);
            }
        }
        HashMap<Integer,Double> RangeAUC=new HashMap<Integer,Double>();
        RangeAUC.put(1,AUC_range);
        RangeAUC.put(2,AP_range);
        return RangeAUC;
    }

    public HashMap<Integer,Double> RangeAUC_volume(ArrayList<Integer> labels_original, ArrayList<Double> score,int windowSize){
        ArrayList<Double> score_sorted=new ArrayList<Double>();
        score_sorted.addAll(score);
        Collections.sort(score_sorted);
        Collections.reverse(score_sorted);
        ArrayList<Double> tpr_3d =new ArrayList<Double>();
        ArrayList<Double> fpr_3d =new ArrayList<Double>();
        ArrayList<Double> prec_3d =new ArrayList<Double>();

        ArrayList<Double> auc_3d =new ArrayList<Double>();
        ArrayList<Double> ap_3d =new ArrayList<Double>();
        ArrayList<Integer> window_3d =new ArrayList<Integer>();
        for(int i=0;i<=windowSize;i++){
            window_3d.add(i);
        }
        int P=0;
        for(int i=0;i<labels_original.size();i++){
            P+=labels_original.get(i);
        }
        ArrayList<Integer> tlist=creatLinspace(0,score.size()-1,250);
        for(int window:window_3d){
            ArrayList<Double> labels = extend_postive_range(labels_original, window);
            ArrayList<ArrayList<Integer>> L = range_convers_new(labels);
            ArrayList<Double> TPR_list =new ArrayList<Double>();
            ArrayList<Double> FPR_list =new ArrayList<Double>();
            ArrayList<Double> Precision_list =new ArrayList<Double>();
            TPR_list.add(0.0);
            FPR_list.add(0.0);
            Precision_list.add(1.0);

            for(int t=0;t<tlist.size();t++){
                int i=tlist.get(t);
                double threshold=score_sorted.get(i);
                ArrayList<Integer> pred=new ArrayList<Integer>();
                for (int j=0;j<score.size();j++){
                    if(score.get(j)>=threshold){
                        pred.add(1);
                    }else{
                        pred.add(0);
                    }
                }
                HashMap<Integer,Double> confusion=TPR_FPR_RangeAUC(labels, pred, P,L);
                double TPR=confusion.get(1);
                double FPR=confusion.get(2);
                double Rprecision=confusion.get(3);
                TPR_list.add(TPR);
                FPR_list.add(FPR);
                Precision_list.add(Rprecision);
            }
            TPR_list.add(1.0);
            FPR_list.add(1.0);
            double AUC_range=0.0;
            double AP_range=0.0;
            ArrayList<Double> width=new ArrayList<Double>();
            ArrayList<Double> height=new ArrayList<Double>();
            ArrayList<Double> width_PR=new ArrayList<Double>();
            ArrayList<Double> height_PR=new ArrayList<Double>();
            for (int i=0;i<FPR_list.size()-1;i++){
                width.add(FPR_list.get(i+1)-FPR_list.get(i));
                height.add((double)(TPR_list.get(i)+TPR_list.get(i+1))/2);
                if(i<TPR_list.size()-2){
                    width_PR.add(TPR_list.get(i+1)-TPR_list.get(i));
                    height_PR.add((double)(Precision_list.get(i)+Precision_list.get(i+1))/2);
                }
            }
            for (int i=0;i<width.size();i++){
                AUC_range+=width.get(i)*height.get(i);
                if(i<width.size()-1){
                    AP_range+=width_PR.get(i)*height_PR.get(i);
                }
            }
            auc_3d.add(AUC_range);
            ap_3d.add(AP_range);
        }
        double VUS_ROC=0.0;
        double VUS_PR=0.0;
        for(int i=0;i<auc_3d.size();i++){
            VUS_ROC+=auc_3d.get(i);
            VUS_PR+=ap_3d.get(i);
        }
        VUS_ROC/=window_3d.size();
        VUS_PR/=window_3d.size();
        HashMap<Integer,Double> VUS=new HashMap<Integer,Double>();
        VUS.put(1,VUS_ROC);
        VUS.put(2,VUS_PR);
        return VUS;
    }

    private HashMap<Integer,Double> TPR_FPR_RangeAUC(ArrayList<Double> labels,ArrayList<Integer> pred,int P,ArrayList<ArrayList<Integer>> L){
        ArrayList<Double> product=new ArrayList<Double>();
        Double TP=0.0;
        Double P0=0.0;
        Double sumpred=0.0;
        for(int i=0;i<labels.size();i++){
            product.add(i,labels.get(i)*pred.get(i));
            TP+=product.get(i);
            P0+=labels.get(i);
            sumpred+=pred.get(i);

        }
        double P_new=(double) ((P+P0)/2);
        double recall = Math.min((double)(TP/P_new),1);
        int existence=0;
        for(ArrayList<Integer> seg :L){
            double sump=0;
            for(int i=seg.get(0);i<=seg.get(1);i++){
                sump+=product.get(i);
            }
            if(sump>0){
                existence++;
            }
        }
        double existence_ratio = (double)existence/L.size();
        double TPR_RangeAUC = recall*existence_ratio;
        double FP=sumpred-TP;
        double N_new = labels.size() - P_new;
        double FPR_RangeAUC = (double)FP/N_new;
        double Precision_RangeAUC = (double)TP/sumpred;
        HashMap<Integer,Double> TPR_FPR=new HashMap<Integer,Double>();
        TPR_FPR.put(1,TPR_RangeAUC);
        TPR_FPR.put(2,FPR_RangeAUC);
        TPR_FPR.put(3,Precision_RangeAUC);
        return TPR_FPR;
    }
    private ArrayList<Double> extend_postive_range(ArrayList<Integer> x, int window) {
        ArrayList<Double> label= new ArrayList<Double>();
        for(int i=0;i<x.size();i++){
            label.add((double)x.get(i));
        }
        ArrayList<ArrayList<Integer>> L = range_convers_new(label);
        int length=label.size();
        for(int k=0;k<L.size();k++){
            int s=L.get(k).get(0);
            int e=L.get(k).get(1);
            for(int i=e;i<Math.min(e+window/2,length);i++){
                double kk=Math.sqrt(1-(double)(i-e)/window);
                label.set(i,label.get(i)+Math.sqrt(1-(double)(i-e)/window));
            }
            for(int i=Math.max(s-window/2,0);i<s;i++) {
                label.set(i, label.get(i) + Math.sqrt(1 - (double)(s - i) / window));
            }
        }
        for(int k=0;k<length;k++){
            if(label.get(k)>1){
                label.set(k,1.0);
            }
        }
        return label;
    }

    private ArrayList<ArrayList<Integer>> range_convers_new(ArrayList<Double> labels) {
        ArrayList<ArrayList<Integer>> L = new ArrayList<ArrayList<Integer>>();
        int i = 0;
        int j = 0;
        while (j < labels.size()) {
            while (labels.get(i) == 0.0) {
                i++;
                if (i >= labels.size()) {
                    break;
                }
            }
            j = i + 1;
            if (j >= labels.size()) {
                if (j == labels.size()) {
                    ArrayList<Integer> l = new ArrayList<Integer>();
                    l.add(i);
                    l.add(j - 1);
                    L.add(l);
                }
                break;
            }
            while (labels.get(j) != 0) {
                j++;
                if (j >= labels.size()) {
                    ArrayList<Integer> l = new ArrayList<Integer>();
                    l.add(i);
                    l.add(j - 1);
                    L.add(l);
                    break;
                }
            }
            if (j >= labels.size()) {
                break;
            }
            ArrayList<Integer> l = new ArrayList<Integer>();
            l.add(i);
            l.add(j - 1);
            L.add(l);
            i = j;
        }
        return L;
    }
    private ArrayList<Integer> creatLinspace(int start,int end,int num){
        double diff=(double) (end-start)/(num-1);
        ArrayList<Integer> lin=new ArrayList<Integer>();
        lin.add(start);
        for(int i=1;i<=num-1;i++){
            lin.add((int)(start+i*diff));
        }
        return lin;
    }
}
