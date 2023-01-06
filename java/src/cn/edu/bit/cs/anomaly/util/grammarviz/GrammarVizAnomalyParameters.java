package cn.edu.bit.cs.anomaly.util.grammarviz;

import com.beust.jcommander.Parameter;
import java.util.ArrayList;
import java.util.List;
import net.seninp.gi.GIAlgorithm;
import cn.edu.bit.cs.anomaly.util.grammarviz.anomaly.AnomalyAlgorithm;
import net.seninp.jmotif.sax.NumerosityReductionStrategy;

/**
 * Implements a parameters template for CLI conversion.
 * 
 * @author psenin
 * 
 */
public class GrammarVizAnomalyParameters {

  // general setup
  //
  @Parameter
  public List<String> parameters = new ArrayList<String>();

  @Parameter(names = { "--help", "-h" }, help = true)
  public boolean help;

  // dataset
  //
  @Parameter(names = { "--data", "-i" }, description = "The input file name")
  public static String IN_FILE;

  // output
  //
  @Parameter(names = { "--output", "-o" }, description = "The output file prefix")
  public static String OUT_FILE = "";

  // discretization parameters
  //
  @Parameter(names = { "--window_size", "-w" }, description = "Sliding window size")
  public static int SAX_WINDOW_SIZE = 170;

  @Parameter(names = { "--word_size", "-p" }, description = "PAA word size")
  public static int SAX_PAA_SIZE = 4;

  @Parameter(names = { "--alphabet_size", "-a" }, description = "SAX alphabet size")
  public static int SAX_ALPHABET_SIZE = 4;

  @Parameter(names = "--strategy", description = "Numerosity reduction strategy")
  public static NumerosityReductionStrategy SAX_NR_STRATEGY = NumerosityReductionStrategy.EXACT;

  @Parameter(names = "--threshold", description = "Normalization threshold")
  public static double SAX_NORM_THRESHOLD = 0.01;

  // the algorithms params
  //
  @Parameter(names = { "--algorithm", "-alg" }, description = "The algorithm to use")
  public static AnomalyAlgorithm ALGORITHM = AnomalyAlgorithm.RRA;
  
  @Parameter(names = { "--discords_num", "-n" }, description = "The algorithm to use")
  public static int DISCORDS_NUM = 5;
  
  // GI parameter
  //
  @Parameter(names = { "--gi", "-g" }, description = "GI algorithm to use")
  public static GIAlgorithm GI_ALGORITHM_IMPLEMENTATION = GIAlgorithm.SEQUITUR;

  // sub-sampling parameter
  //
  @Parameter(names = {
      "--subsample" }, description = "RRASAMPLED subsampling fraction (0.0 - 1.0) for longer time series")
  public static Double SUBSAMPLING_FRACTION = Double.NaN;

  // grid boundaries for discretization parameters
  //
  @Parameter(names = { "--bounds",
      "-b" }, description = "RRASAMPLED grid boundaries (Wmin Wmax Wstep Pmin Pmax Pstep Amin Amax Astep)")
  public static String GRID_BOUNDARIES = "10 100 10 10 50 10 2 12 2";

}
