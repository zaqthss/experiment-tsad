package cn.edu.bit.cs.anomaly.evaluate;

import cn.edu.bit.cs.anomaly.entity.Range;
import cn.edu.bit.cs.anomaly.util.Constants.POS_BIAS;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Range-based anomaly detection metric * Precision and Recall for Time Series NIPS2018
 */
public class RangeMetric {
    public double recall = 0;
    public double precision = 0;

    private ArrayList<Range> realAnomaly, predictAnomaly;
    private HashMap<Integer, Range> overlaps;
    private int[] hitR; // length = realAnomaly.size()
    private int[] hitP;
    private int rSize = 0, pSize = 0;
    private POS_BIAS pos_bias = POS_BIAS.FLAT;

    public void computeMetric(
            double alpha, POS_BIAS bias, ArrayList<Range> realAnomaly, ArrayList<Range> predictAnomaly) {
        recall = 0;
        precision = 0;
        overlaps = new HashMap<>();

        this.pos_bias = bias;
        this.realAnomaly = realAnomaly;
        this.predictAnomaly = predictAnomaly;

        this.rSize = realAnomaly.size();
        this.pSize = predictAnomaly.size();

        createOverlapSet(realAnomaly, predictAnomaly);
        // compute recall
        for (int rIndex = 0; rIndex < rSize; ++rIndex) {
            recall += alpha * calcExistenceReward(rIndex) + (1 - alpha) * calcOverlapReward(rIndex);
        }
        recall = recall / rSize;
        // compute precision
        for (int pIndex = 0; pIndex < pSize; ++pIndex) {
            double cardinalityFactor = gammaP(pIndex);
            double mul = 0;
            for (int rIndex = 0; rIndex < rSize; ++rIndex) {
                int key = calcKey(rIndex, pIndex);
                mul += omega(predictAnomaly.get(pIndex), overlaps.get(key));
            }
            assert mul <= 1 : "mul must <= 1";
            precision += cardinalityFactor * mul;
        }
        precision = precision / pSize;
    }

    private int calcKey(int rIndex, int pIndex) {
        return rIndex * pSize + pIndex;
    }

    // store overlaps
    private void createOverlapSet(ArrayList<Range> R, ArrayList<Range> P) {
        hitR = new int[rSize];
        hitP = new int[pSize];
        for (int rIndex = 0; rIndex < rSize; ++rIndex) {
            Range Ri = R.get(rIndex);
            for (int pIndex = 0; pIndex < pSize; ++pIndex) {
                Range overlap = (Range) Ri.clone();
                overlap.retainAll(P.get(pIndex));
                if (overlap.size() > 0) {
                    overlaps.put(calcKey(rIndex, pIndex), overlap);
                    hitR[rIndex]++;
                    hitP[pIndex]++;
                }
            }
        }
    }

    private double calcExistenceReward(int rIndex) {
        if (hitR[rIndex] > 0) {
            return 1;
        } else {
            return 0;
        }
    }

    private double calcOverlapReward(int rIndex) {
        double cardinalityFactor = gammaR(rIndex);
        double mul = 0;
        for (int pIndex = 0; pIndex < pSize; ++pIndex) {
            int key = calcKey(rIndex, pIndex);
            mul += omega(realAnomaly.get(rIndex), overlaps.get(key));
        }
        assert mul <= 1 : "mul must <= 1";
        return cardinalityFactor * mul;
    }

    private double gammaR(int index) {
        return 1.0;
        // return 1.0 / hitR[index];
    }

    private double gammaP(int index) {
        return 1.0;
        // return 1.0 / hitP[index];
    }

    private double omega(Range range, Range overlap) {
        if (overlap == null) {
            return 0;
        }
        double myValue = 0, maxValue = 0;
        int anomalyLength = range.size();
        ArrayList<Long> rangeList = new ArrayList<>(range);
        for (int i = 0; i < anomalyLength; ++i) {
            int bias = delta(i, anomalyLength);
            maxValue += bias;
            if (overlap.contains(rangeList.get(i))) {
                myValue += bias;
            }
        }
        return myValue / maxValue;
    }

    private int delta(int i, int anomalyLength) {
        switch (pos_bias) {
            case FLAT:
                return 1;
            case FRONT_END:
                // since in the paper i begins with 1, so we remove the +1
                // return anomalyLength - i + 1;
                return anomalyLength - i;
            case BACK_END:
                return i;
            case MIDDLE:
                if (i <= anomalyLength / 2) {
                    return i;
                } else {
                    // return anomalyLength - i + 1;
                    return anomalyLength - i;
                }
        }
        return 1; // will not be hit
    }
}
