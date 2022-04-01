//package
package cn.edu.bit.cs.anomaly.util.Stare;
import cn.edu.bit.cs.anomaly.util.Stare.Tuple;
import cn.edu.bit.cs.anomaly.entity.TimePointMulDim;
import cn.edu.bit.cs.anomaly.entity.TimeSeriesMulDim;

import java.util.ArrayList;
import java.util.Arrays;

public class StareStreamGenerator {
	private double[] maxValues; //max values
	private double[] minValues; //min values

	public TimeSeriesMulDim<TimePointMulDim> timeSeries;


	public StareStreamGenerator(TimeSeriesMulDim<TimePointMulDim> series){
		timeSeries = series;

		minValues = new double[series.getDim()];
		maxValues = new double[series.getDim()];
		Arrays.fill(minValues,Double.MAX_VALUE);
		Arrays.fill(maxValues,Double.MIN_VALUE);
		int id =0;
		for(TimePointMulDim tp :series) {
			for(int i =0;i<tp.getDim();i++){
				if(minValues[i]>tp.getObsVal()[i])
					minValues[i] = tp.getObsVal()[i];
				if(maxValues[i]<tp.getObsVal()[i])
					maxValues[i] = tp.getObsVal()[i];
			}
		}
	}

	//get new slide
	public ArrayList<Tuple> getNewSlideTuples(int itr, int S)  {
		ArrayList<Tuple> newSlide = new ArrayList<Tuple>();
		for(int tid = itr*S;tid <(itr+1)*S&&tid<timeSeries.getLength();tid++){
			newSlide.add(new Tuple(tid, itr, timeSeries.getTimePoint(tid).getObsVal(), false));
		}
		return newSlide;
	}

	//get new tuples whose ids are between start and end
	public ArrayList<Tuple> getTuples(int start, int end)  {
		ArrayList<Tuple> tuples = new ArrayList<Tuple>();
		for(int tid = start;tid<end&&tid<timeSeries.getLength();tid++)
			tuples.add(new Tuple(tid, 0, timeSeries.getTimePoint(tid).getObsVal(), false));
		return tuples;
	}


	public double[] getMaxValues() {
		return this.maxValues;
	}
	public double[] getMinValues(){
		return this.minValues;
	}
}