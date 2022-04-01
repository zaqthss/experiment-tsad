package cn.edu.bit.cs.anomaly.util;

import cn.edu.bit.cs.anomaly.entity.TimePoint;

import java.util.Comparator;

/**
 * sort in the ascending order Might be deprecated
 *
 * @author Aoqian
 */
public class ComparatorTimestamp implements Comparator<TimePoint> {

    @Override
    public int compare(TimePoint tp1, TimePoint tp2) {
        if (tp1.getTimestamp() > tp2.getTimestamp()) {
            return 1;
        } else if (tp1.getTimestamp() < tp2.getTimestamp()) {
            return -1;
        } else {
            return 0;
        }
    }
}
