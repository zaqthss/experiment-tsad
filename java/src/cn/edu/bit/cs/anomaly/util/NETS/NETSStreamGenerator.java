package cn.edu.bit.cs.anomaly.util.NETS;

import cn.edu.bit.cs.anomaly.entity.TimePointMulDim;
import cn.edu.bit.cs.anomaly.entity.TimeSeriesMulDim;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class NETSStreamGenerator {
	private double[] maxValues;
	private double[] minValues;

	private TimeSeriesMulDim<TimePointMulDim> timeSeries;

	public NETSStreamGenerator(TimeSeriesMulDim timeSeries){
		this.timeSeries = timeSeries;
		maxValues = new double[timeSeries.getDim()];
		minValues = new double[timeSeries.getDim()];
		Arrays.fill(maxValues,Double.MIN_VALUE);
		Arrays.fill(minValues,Double.MAX_VALUE);

		for(TimePointMulDim tp : this.timeSeries){
			for(int i=0;i<timeSeries.getDim();i++){
				if(minValues[i]>tp.getObsVal()[i])
					minValues[i] = tp.getObsVal()[i];
				if(maxValues[i]<tp.getObsVal()[i])
					maxValues[i] = tp.getObsVal()[i];
			}
		}

	}
	
	public ArrayList<Tuple> getNewSlideTuples(int itr, int S) {
		ArrayList<Tuple> newSlide = new ArrayList<Tuple>();
		for(int tid =itr*S ;tid<(itr+1)*S&&tid<this.timeSeries.getLength();tid++){
				TimePointMulDim tp = this.timeSeries.getTimePoint(tid);
				newSlide.add(new Tuple(tid,itr,tp.getObsVal()));
		}
		return newSlide;
	}

	public double[] getMaxValues() {
		return this.maxValues;
	}
	public double[] getMinValues(){
		return this.minValues;
	}

	
}

