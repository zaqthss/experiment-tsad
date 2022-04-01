package cn.edu.bit.cs.anomaly.util;

/**
 * @author Aoqian
 */
public class Constants {

    public static double EPSILON = 1.0e-5;
    public static double MINVAL = Double.MAX_VALUE;
    public static double MAXVAL = -MINVAL;
    public static long SEED = 20200923;

    public static enum OUTLIER_TYPE {
        UP, DOWN, MID, SHIFT
    }

    public static enum IS_ANOMALY {
        FALSE, TRUE
    }

    public static enum POS_BIAS {
        FLAT, FRONT_END, BACK_END, MIDDLE
    }

    /**
     * @author Antinomies The default Constants of Luminol alg
     */
    public static class Luminol {
        public static final double DEFAULT_NOISE_PCT_THRESHOLD = 0.001;
        public static final int DEFAULT_BITMAP_PRECISION = 4;
        public static final int DEFAULT_BITMAP_CHUNK_SIZE = 2;
        public static final int DEFAULT_BITMAP_LEADING_WINDOW_SIZE = 10;
        public static final int DEFAULT_BITMAP_LAGGING_WINDOW_SIZE = 10;
        //		public static final int DEFAULT_BITMAP_MINIMAL_POINTS_IN_WINDOWS = 50;
        public static final int DEFAULT_BITMAP_MINIMAL_POINTS_IN_WINDOWS = 10;
        public static final int DEFAULT_BITMAP_MAXIMAL_POINTS_IN_WINDOWS = 200;
    }

    /**
     * @author Antinomies The default constants of CPOD alg
     */
    public static class CPOD {
        public static final int DEFAULT_Multipie_SIZE = 4;
        public static final int DEFAULT_SLIDE_SIZE = 4;
        public static final int DEFAULT_R = 1;
        public static final int DEFAULT_K = 1;

    }

    /**
     * @author dsq The default Constants of NeighborProfile alg
     */
    public static class NeighborProfile {
        public static int N_NNBALLS = 100;
        public static int[] MAX_SAMPLE = {8, 16, 32, 64};
        public static int SUB_LEN = 750;
        public static int BATCHSIZE = 10000;
        public static int MIN = 99999;

        public static enum SCALE {
            auto, demean, zscore
        }
    }

    /**
     * @author dsq The default Constants of PBAD alg
     */
    public static class PBAD {
        public static double RELATIVE_MINSUP = 0.01;
        public static double JACCARD_THRESHOLD = 0.9;
    }
}
