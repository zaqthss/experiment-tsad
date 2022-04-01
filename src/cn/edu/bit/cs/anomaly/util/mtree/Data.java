package cn.edu.bit.cs.anomaly.util.mtree;

import cn.edu.bit.cs.anomaly.util.mtree.DistanceFunctions.EuclideanCoordinate;

import java.util.Random;


public class Data implements EuclideanCoordinate, Comparable<Data> {

    public double[] values;
    public final int hashCode;

    //arrival time
    public int arrivalTime;


    public Data(double... values) {
        this.values = values;

        int hashCode2 = 1;
        for (double value : values) {
            hashCode2 = 31 * hashCode2 + (int) value + (new Random()).nextInt(100000);
        }
        this.hashCode = hashCode2;
    }

    @Override
    public int dimensions() {
        return values.length;
    }

    @Override
    public double get(int index) {
        return values[index];
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Data) {
            Data that = (Data) obj;
            if (this.arrivalTime != that.arrivalTime) return false;
            if (this.dimensions() != that.dimensions()) {
                return false;
            }
            for (int i = 0; i < this.dimensions(); i++) {
                if (this.values[i] != that.values[i]) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(Data that) {
        int dimensions = Math.min(this.dimensions(), that.dimensions());
        for (int i = 0; i < dimensions; i++) {
            double v1 = this.values[i];
            double v2 = that.values[i];
            if (v1 > v2) {
                return +1;
            }
            if (v1 < v2) {
                return -1;
            }
        }

        if (this.dimensions() > dimensions) {
            return +1;
        }

        if (that.dimensions() > dimensions) {
            return -1;
        }

        return 0;
    }

}