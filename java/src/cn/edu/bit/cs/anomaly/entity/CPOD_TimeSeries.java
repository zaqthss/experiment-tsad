package cn.edu.bit.cs.anomaly.entity;

import java.util.ArrayList;


/**
 * @author Antinomies
 */
public class CPOD_TimeSeries extends TimeSeriesMulDim<CPOD_TimePoint> {
    private int dim;

    public CPOD_TimeSeries(ArrayList<CPOD_TimePoint> timeseries) {
        setTimeseries(timeseries);
        if (timeseries.size() > 0) {
            dim = timeseries.get(0).getDim();
        } else
            dim = 0;
    }

    public CPOD_TimeSeries(TimeSeriesMulDim<TimePointMulDim> timeseries) {
        ArrayList<CPOD_TimePoint> tmp = new ArrayList<>();
        int i = 0;
        for (TimePointMulDim tp : timeseries.getTimeseries())
            tmp.add(new CPOD_TimePoint(tp, i++));
        setTimeseries(tmp);
        this.dim = timeseries.getDim();
    }

    public CPOD_TimeSeries() {
        setTimeseries(new ArrayList<CPOD_TimePoint>());
        this.dim = 0;
    }

}
