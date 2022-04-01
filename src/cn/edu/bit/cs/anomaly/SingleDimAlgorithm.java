package cn.edu.bit.cs.anomaly;

import cn.edu.bit.cs.anomaly.entity.TimeSeries;

import java.util.Map;

public interface SingleDimAlgorithm extends AnomalyAlgorithm {
    public void init(Map<String, Object> args, TimeSeries timeseries);
}
