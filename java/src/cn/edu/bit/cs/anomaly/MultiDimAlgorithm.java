package cn.edu.bit.cs.anomaly;

import cn.edu.bit.cs.anomaly.entity.TimeSeriesMulDim;

import java.util.Map;

public interface MultiDimAlgorithm extends AnomalyAlgorithm {
    public void init(Map<String, Object> args, TimeSeriesMulDim timeseries);
}
