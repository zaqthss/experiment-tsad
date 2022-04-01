package cn.edu.bit.cs.anomaly.entity;

import cn.edu.bit.cs.anomaly.util.Constants;
import cn.edu.bit.cs.anomaly.util.Constants.IS_ANOMALY;

public class TimePointMulDim<T extends TimePointMulDim> implements Comparable<T> {
    private long timestamp;
    private int dim;
    private double[] truthVal;
    private double[] obsVal;
    private double[] predictVal;
    private double[] observe;
    private double[] truth;

    // In theory, if observe != truth, it is anomaly
    private IS_ANOMALY is_anomaly = IS_ANOMALY.FALSE;

    public int id;

    private void init() {
        truthVal = new double[dim];
        obsVal = new double[dim];
        predictVal = new double[dim];
        observe = new double[dim];
        truth = new double[dim];
    }

    public TimePointMulDim(long timestamp, double[] val, int dim) {
        setTimestamp(timestamp);
        setDim(dim);
        init();

        setTruth(val);
        setTruthVal(val);
        setObserve(val);
        setObsVal(val);
        setPredictVal(val);
    }

    public TimePointMulDim(long timestamp, double[] truth, double[] observe) {
        setTimestamp(timestamp);
        assert (truth.length == observe.length);
        setDim(truth.length);
        init();

        setTruth(truth);
        setTruthVal(truth);
        setObserve(observe);
        setObsVal(observe);
        setPredictVal(observe);

        for (int i = 0; i < dim; i++) {
            if (Math.abs(truth[i] - observe[i]) < Constants.EPSILON) {
                setIs_anomaly(IS_ANOMALY.FALSE);
            }
        }
    }

    public TimePointMulDim(TimePointMulDim tp) {
        setTimestamp(tp.getTimestamp());
        setDim(tp.getDim());
        init();

        setTruth(tp.getTruth());
        setTruthVal(tp.getTruthVal());
        setObserve(tp.getObserve());
        setObsVal(tp.getObsVal());
        setPredictVal(tp.getPredictVal());

        setIs_anomaly(tp.getIs_anomaly());
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getDim() {
        return dim;
    }

    public void setDim(int dim) {
        this.dim = dim;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double[] getTruthVal() {
        return truthVal;
    }

    public void setTruthVal(double[] truthVal) {
        this.truthVal = truthVal;
    }

    public void setTruthValAt(double truthVal, int i) {
        if (i < dim) {
            this.truthVal[i] = truthVal;
        }
    }

    public double[] getObsVal() {
        return obsVal;
    }

    public void setObsVal(double[] obsVal) {
        this.obsVal = obsVal;
    }

    public void setObsValAt(double obsVal, int i) {
        if (i < dim) {
            this.obsVal[i] = obsVal;
        }
    }

    public double[] getPredictVal() {
        return predictVal;
    }

    public void setPredictVal(double[] predictVal) {
        this.predictVal = predictVal;
    }

    public void setPredictValAt(double predictVal, int i) {
        if (i < dim) {
            this.predictVal[i] = predictVal;
        }
    }

    public double[] getObserve() {
        return observe;
    }

    public void setObserve(double[] observe) {
        this.observe = observe;
    }

    public void setObserveValAt(double observe, int i) {
        if (i < dim) {
            this.observe[i] = observe;
        }
    }

    public double[] getTruth() {
        return truth;
    }

    public void setTruth(double[] truth) {
        this.truth = truth;
    }

    public void setTruthAt(double truth, int i) {
        if (i < dim) {
            this.truth[i] = truth;
        }
    }

    public IS_ANOMALY getIs_anomaly() {
        return is_anomaly;
    }

    public void setIs_anomaly(IS_ANOMALY is_anomaly) {
        this.is_anomaly = is_anomaly;
    }

    public void clear() {
        setTruthVal(truthVal);
        setObsVal(observe);
        setPredictVal(observe);
        setIs_anomaly(IS_ANOMALY.FALSE);
    }

    @Override
    public int compareTo(TimePointMulDim that) {
        int dimensions = Math.min(this.getDim(), that.getDim());
        for (int i = 0; i < dimensions; i++) {
            double v1 = this.getObsVal()[i];
            double v2 = that.getObsVal()[i];
            if (v1 > v2) {
                return +1;
            }
            if (v1 < v2) {
                return -1;
            }
        }

        if (this.getDim() > dimensions) {
            return +1;
        }

        if (that.getDim() > dimensions) {
            return -1;
        }

        return 0;
    }
}
