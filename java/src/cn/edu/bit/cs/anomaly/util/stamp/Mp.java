package cn.edu.bit.cs.anomaly.util.stamp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Mp {

    public ArrayList<Double> stamp(ArrayList<Double> tsA, ArrayList<Double> tsB, int window) {
        Random rnd = new Random(0);
        RandomOrder order = new RandomOrder((int) (tsA.size() - window + 1), 1.0, rnd);
        return matrixProfile(tsA, window, order, tsB);
    }

    public ArrayList<Double> matrixProfile(ArrayList<Double> tsA, int window, RandomOrder order, ArrayList<Double> tsB) {
        ArrayList<Double> mp = new ArrayList<Double>();
        for (int i = 0; i < tsB.size() - window + 1; i++) {
            mp.add(999999.0);
        }
        //int idx = order.getNext();
        int idx=0;
        while (idx != -1) {
            ArrayList<Double> distanceProfile = stampDistanceProfile(tsA, tsB, window, idx);
            for (int i = 0; i < distanceProfile.size(); i++) {
                if (mp.get(i) > distanceProfile.get(i)) {
                    mp.set(i, distanceProfile.get(i));
                }
            }
            //idx = order.getNext();
            idx++;
            if(idx==tsA.size() - window + 1) {
            	break;
            }
        }

        return mp;
    }

    public ArrayList<Double> stampDistanceProfile(ArrayList<Double> tsA, ArrayList<Double> tsB, int window, int idx) {
        ArrayList<Double> query = new ArrayList<Double>();
        for (int i = idx; i < idx + window; i++) {
            query.add(tsA.get(i));
        }
        int n = tsB.size();
        Mass m = new Mass();
        ArrayList<Double> distanceProfile = m.mass(query, tsB);
        return distanceProfile;
    }
}
