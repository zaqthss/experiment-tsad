package cn.edu.bit.cs.anomaly.entity;

import cn.edu.bit.cs.anomaly.util.Constants;
import cn.edu.bit.cs.anomaly.util.Constants.IS_ANOMALY;

/**
 * @author Aoqian
 */
public class TimePoint implements Comparable<TimePoint> {
    private long timestamp;
    private double truthVal;
    private double obsVal;
    private double predictVal;

    private double observe;
    private double truth;

    public int id;
    // In theory, if observe != truth, it is anomaly
    private IS_ANOMALY is_anomaly;

    public TimePoint(long timestamp, double val) {
        setTimestamp(timestamp);

        setTruth(val);
        setTruthVal(val);

        setObserve(val);
        setObsVal(val);
        setPredictVal(val);

        setIs_anomaly(IS_ANOMALY.FALSE);
    }

    public TimePoint(long timestamp, double truth, double observe) {
        setTimestamp(timestamp);

        setTruth(truth);
        setTruthVal(truth);

        setObserve(observe);
        setObsVal(observe);
        setPredictVal(observe);

        if (Math.abs(truth - observe) > Constants.EPSILON) {
            setIs_anomaly(IS_ANOMALY.TRUE);
        } else {
            setIs_anomaly(IS_ANOMALY.FALSE);
        }
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getTruthVal() {
        return truthVal;
    }

    public void setTruthVal(double truthVal) {
        this.truthVal = truthVal;
    }

    public double getObsVal() {
        return obsVal;
    }

    public void setObsVal(double obsVal) {
        this.obsVal = obsVal;
    }

    public double getPredictVal() {
        return predictVal;
    }

    public void setPredictVal(double predictVal) {
        this.predictVal = predictVal;
    }

    public double getObserve() {
        return observe;
    }

    public void setObserve(double observe) {
        this.observe = observe;
    }

    public double getTruth() {
        return truth;
    }

    public void setTruth(double truth) {
        this.truth = truth;
    }

    public IS_ANOMALY getIs_anomaly() {
        return is_anomaly;
    }

    public void setIs_anomaly(IS_ANOMALY is_anomaly) {
        this.is_anomaly = is_anomaly;
    }

    public void clear() {
        setTruthVal(truth);
        setObsVal(observe);
        setPredictVal(observe);
        setIs_anomaly(IS_ANOMALY.FALSE);
    }

    @Override
    /** compare timepoint by obsval */
    public int compareTo(TimePoint o) {
        if (this.getObsVal() > o.getObsVal()) {
            return 1;
        } else if (this.getObsVal() < o.getObsVal()) {
            return -1;
        } else {
            return 0;
        }
    }
}
