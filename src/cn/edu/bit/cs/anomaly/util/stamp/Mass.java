package cn.edu.bit.cs.anomaly.util.stamp;

import org.apache.commons.math3.complex.Complex;
import org.jtransforms.fft.DoubleFFT_1D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class Mass {

    public ArrayList<Double> zscore(ArrayList<Double> t, int ddof) {
        double mean = calcMean(t);
        double std = calcStd(t, ddof);
        for (int i = 0; i < t.size(); i++) {
            t.set(i, (double)(t.get(i) - mean) / std);
        }
        return t;
    }

    public static double calcMean(ArrayList<Double> t) {
        double sum = 0;
        for (int i = 0; i < t.size(); i++) {
            sum += t.get(i);
        }
        double mean = sum / t.size();
        return mean;

    }

    public static double calcStd(ArrayList<Double> t, int ddof) {
        double total = 0;
        double mean = calcMean(t);
        for (int i = 0; i < t.size(); i++) {
            total +=
                    (t.get(i) - mean)
                            * (t.get(i) - mean);
        }
        int dev = t.size();
        if (ddof == 1) {
            dev = dev - 1;
        }
        double std = Math.sqrt(total / dev);
        return std;

    }

    public ArrayList<Double> mass(ArrayList<Double> query, ArrayList<Double> ts) {
        query = zscore(query, 0);

        int m = query.size();
        int n = ts.size();
        ArrayList<Double> stdv = movstd(ts, m);
        Collections.reverse(query);
        for (int i = 0; i < n - m; i++) {
            query.add(0.0);
        }
        Complex[] complexTs = new Complex[n];
        Complex[] complexQuery = new Complex[n];

        for (int i = 0; i < n; i++) {
            complexTs[i] = new Complex(ts.get(i), 0);
            complexQuery[i] = new Complex(query.get(i), 0);
        }

        complexTs = fft1D(complexTs);
        complexQuery = fft1D(complexQuery);


        //multiply two fft results
        Complex[] complexDot = new Complex[n];
        for (int i = 0; i < n; i++) {
            complexDot[i] = complexTs[i].multiply(complexQuery[i]);
        }

        // inverse fft for dot computation
        complexDot = ifft1D(complexDot);
        double[] realDot = new double[complexDot.length];

        for (int i = 0; i < complexDot.length; i++) {
            realDot[i] = complexDot[i].getReal();
        }
        ArrayList<Double> res = new ArrayList<Double>();
        for (int i = 0; i < stdv.size(); i++) {
            int j = i + m - 1;
            double temp = (m - (double) realDot[j] / stdv.get(i)) * 2;
            temp=Math.abs(temp);
            res.add(Math.sqrt(temp));
        }
        return res;

    }

    public ArrayList<Double> cumsum(ArrayList<Double> x) {
        ArrayList<Double> y = new ArrayList<Double>();
        for (int i = 0; i < x.size(); i++) {
            if (i == 0) {
                y.add(0.0);
            } else {
                y.add(y.get(i - 1));
            }
            y.set(i, y.get(i) + x.get(i));
        }
        return y;
    }

    public ArrayList<Double> movstd(ArrayList<Double> ts, int m) {
        ArrayList<Double> s = new ArrayList<Double>();
        s.add(0.0);
        s.addAll(cumsum(ts));
        ArrayList<Double> tss = new ArrayList<Double>();
        for (int i = 0; i < ts.size(); i++) {
            tss.add(Math.pow(ts.get(i), 2));
        }
        ArrayList<Double> sSq = new ArrayList<Double>();
        sSq.add(0.0);
        sSq.addAll(cumsum(tss));
        ArrayList<Double> seqSum = new ArrayList<Double>();
        for (int i = 0; i < ts.size() + 1 - m; i++) {
            int j = i + m;
            seqSum.add(s.get(j) - s.get(i));
        }
        ArrayList<Double> seqSumSq = new ArrayList<Double>();
        for (int i = 0; i < ts.size() + 1 - m; i++) {
            int j = i + m;
            seqSumSq.add(sSq.get(j) - sSq.get(i));
        }
        ArrayList<Double> ts_a = new ArrayList<Double>();
        for (int i = 0; i < ts.size() + 1 - m; i++) {
            double temp = (double) seqSumSq.get(i) / m - Math.pow((double) seqSum.get(i) / m, 2);
            ts_a.add(Math.sqrt(temp));
        }
        return ts_a;
    }
    public HashMap<String,ArrayList<Double>> movmeanstd(ArrayList<Double> ts, int m) {
        ArrayList<Double> s = new ArrayList<Double>();
        s.add(0.0);
        s.addAll(cumsum(ts));
        ArrayList<Double> tss = new ArrayList<Double>();
        for (int i = 0; i < ts.size(); i++) {
            tss.add(Math.pow(ts.get(i), 2));
        }
        ArrayList<Double> sSq = new ArrayList<Double>();
        sSq.add(0.0);
        sSq.addAll(cumsum(tss));
        ArrayList<Double> seqSum = new ArrayList<Double>();
        for (int i = 0; i < ts.size() + 1 - m; i++) {
            int j = i + m;
            seqSum.add(s.get(j) - s.get(i));
        }
        ArrayList<Double> seqSumSq = new ArrayList<Double>();
        for (int i = 0; i < ts.size() + 1 - m; i++) {
            int j = i + m;
            seqSumSq.add(sSq.get(j) - sSq.get(i));
        }
        ArrayList<Double> mean = new ArrayList<Double>();
        ArrayList<Double> std = new ArrayList<Double>();
        for (int i = 0; i < ts.size() + 1 - m; i++) {
        	double temp1=(double) seqSumSq.get(i) / m;
        	double temp2=(double) seqSumSq.get(i) / m - Math.pow((double) seqSum.get(i) / m, 2);
        	mean.add(temp1);
            std.add(Math.sqrt(temp2));
        }
        HashMap<String,ArrayList<Double>> map=new HashMap<String,ArrayList<Double>>();
        map.put("mean", mean);
        map.put("std", std);
        return map;
    }
    private Complex[] fft1D(Complex[] signal) {
        int n = signal.length;
        Complex[] fourier = new Complex[n];

        double[] coeff = new double[2 * n];
        int i = 0;
        for (Complex c : signal) {
            coeff[i++] = c.getReal();
            coeff[i++] = c.getImaginary();
        }

        DoubleFFT_1D fft = new DoubleFFT_1D(n);
        fft.complexForward(coeff);

        for (i = 0; i < 2 * n; i += 2) {
            Complex c = new Complex(coeff[i], coeff[i + 1]);
            fourier[i / 2] = c;
        }
        return fourier;
    }

    private Complex[] ifft1D(Complex[] fourier) {
        int n = fourier.length;
        double s = (1.0 / (double) n);

        Complex[] signal = new Complex[n];
        double[] coeff = new double[2 * n];

        int i = 0;
        for (Complex c : fourier) {
            coeff[i++] = c.getReal();
            coeff[i++] = c.getImaginary();
        }

        DoubleFFT_1D fft = new DoubleFFT_1D(n);
        fft.complexInverse(coeff, false);

        for (i = 0; i < 2 * n; i += 2) {
            Complex c = new Complex(s * coeff[i], s * coeff[i + 1]);
            signal[i / 2] = c;
        }
        return signal;
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        ArrayList<Double> a = new ArrayList<Double>(Arrays.asList(0.0, 1.0, -1.0, 0.0));
        ArrayList<Double> b = new ArrayList<Double>(Arrays.asList(-1.0, 1.0, 0.0, 0.0, -1.0, 1.0));
        Mass m = new Mass();
        System.out.println(m.mass(b, a));
    }

}
