package cn.edu.bit.cs.anomaly.util.mtree;

import cn.edu.bit.cs.anomaly.CPOD;
import cn.edu.bit.cs.anomaly.CPOD.CorePoint;
import cn.edu.bit.cs.anomaly.entity.CPOD_TimePoint;

import java.util.HashMap;
import java.util.Set;

public class CorePointMTree extends MTree<CPOD_TimePoint> {

    public HashMap<Integer, CPOD.CorePoint> points;

    private static final PromotionFunction<CPOD_TimePoint> nonRandomPromotion = new PromotionFunction<CPOD_TimePoint>() {
        @Override
        public Pair<CPOD_TimePoint> process(Set<CPOD_TimePoint> dataSet,
                                            DistanceFunction<? super CPOD_TimePoint> distanceFunction) {
            return Utils.minMax(dataSet);
        }
    };

    // init MTree class with euclidean distance
    public CorePointMTree() {
        super(2, DistanceFunctions.EUCLIDEAN, new ComposedSplitFunction<CPOD_TimePoint>(nonRandomPromotion,
                new PartitionFunctions.BalancedPartition<CPOD_TimePoint>()));
        points = new HashMap<Integer, CPOD.CorePoint>();
    }

    public void add(CPOD_TimePoint data) {
        if (!points.containsKey(data.id)) {
            super.add(data);
            points.put(data.id, new CorePoint(data));
            _check();
        }
    }
    public void add(CPOD.CorePoint data) {
        if (!points.containsKey(data.dataPoint.id)) {
            super.add(data.dataPoint);
            points.put(data.dataPoint.id,data);
            _check();
        }
    }

    public boolean remove(CPOD_TimePoint data) {
        if (points.containsKey(data.id)) {
            boolean result = super.remove(data);
            points.remove(data.id);
            _check();
            return result;
        }
        return false;
    }

    public void merge(CorePointMTree cpmt) {
        for (java.util.Map.Entry<Integer, CorePoint> ent : cpmt.points.entrySet()) {
            if (!this.points.containsKey(ent.getKey())) {
                add(ent.getValue().dataPoint);

            }
        }
    }

    public DistanceFunction<? super CPOD_TimePoint> getDistanceFunction() {
        return distanceFunction;
    }

};
