package cn.edu.bit.cs.anomaly.test;

import cn.edu.bit.cs.anomaly.CPOD;
import cn.edu.bit.cs.anomaly.entity.TimeSeriesMulDim;
import cn.edu.bit.cs.anomaly.util.FileHandler;

import java.util.HashMap;
import java.util.Map;

public class CPODTest {

    public static void main(String[] arg) {
        FileHandler fh = new FileHandler();
        TimeSeriesMulDim ts = fh.readMulData("test/test_for_pbad_mul.csv", 3);
        CPOD cpod = new CPOD();
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("mul", 4);
        args.put("sSize", 4);
        args.put("R", 1d);
        args.put("K", 1);
        cpod.init(args, ts);
        cpod.run();
    }
}
