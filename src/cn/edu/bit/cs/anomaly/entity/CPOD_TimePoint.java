package cn.edu.bit.cs.anomaly.entity;

import cn.edu.bit.cs.anomaly.CPOD;
import cn.edu.bit.cs.anomaly.util.mtree.DistanceFunctions.EuclideanCoordinate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CPOD_TimePoint extends TimePointMulDim<CPOD_TimePoint> implements EuclideanCoordinate {

    public Set<Integer> neighborCount; // total number of found neighbors
    //public Set<Long> preNeighborMap;// the counts of neighbors in preceding slides
    //public Set<Long> numSucNeighbors;// the number of succeeding neighbors
    public CPOD.CorePoint closeCore;// the core point in the range of R/2 from p
    public double distanceToCore;
    public ArrayList<CPOD.CorePoint> coreList; // the list of the core points that are linked to p
    public CPOD.CPOD_Slide lastRight, lastLeft;// the last searched succeeding and preceding slides
    public int id; //index of TimePoint


    public CPOD_TimePoint(long timestamp, double[] val, int dim, int id) {
        super(timestamp, val, dim);
        this.id = id;
        init();
    }

    public CPOD_TimePoint(long timestamp, double[] truth, double[] observe, int id) {
        super(timestamp, truth, observe);
        this.id = id;
        init();
    }

    public CPOD_TimePoint(TimePointMulDim tp, int id) {
        super(tp.getTimestamp(), tp.getObsVal(), tp.getDim());
        this.id = id;
        init();
    }

    private void init() {
        coreList = new ArrayList<CPOD.CorePoint>();
        neighborCount = new HashSet<>();
    }


    @Override
    public int dimensions() {

        return this.getDim();
    }

    @Override
    public double get(int index) {

        return this.getObsVal()[index];
    }

}
