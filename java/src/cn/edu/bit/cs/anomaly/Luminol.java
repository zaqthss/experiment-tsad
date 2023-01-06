package cn.edu.bit.cs.anomaly;

import cn.edu.bit.cs.anomaly.entity.TimePoint;
import cn.edu.bit.cs.anomaly.entity.TimeSeries;
import cn.edu.bit.cs.anomaly.util.Constants;
import cn.edu.bit.cs.anomaly.util.Constants.IS_ANOMALY;

import java.util.*;
import java.util.Map.Entry;

public class Luminol implements UniDimAlgorithm {

  private int precision;
  private int lag_window_size;
  private int future_window_size;
  private int chunk_size;

  private double value_min, value_max;
  private String sax = "";

  private ArrayList<HashMap<String, Integer>> lag_dicts;
  private ArrayList<HashMap<String, Integer>> fut_dicts;

  public TimeSeries time_series;
  public int time_series_length;
  public TreeMap<Long, Double> anom_scores;

  public double threshold;

  @Override
  public void run() {
    System.out.print(this.precision+" ");
    System.out.print(this.chunk_size+" ");
    System.out.print(this.lag_window_size+" ");
    System.out.print(this.future_window_size+" ");
    System.out.println();
    set_scores();
    List<Map.Entry<Long,Double>> s = new ArrayList<>(anom_scores.entrySet());
    Collections.sort(s,Comparator.comparing(Map.Entry::getValue));
    for(int i =0;i< this.threshold*this.time_series.getLength();i++){
      this.time_series.getTimePoint(s.get(i).getKey()).setIs_anomaly(IS_ANOMALY.TRUE);
    }
    System.out.println(Collections.max(anom_scores.values()));
  }

  /**
   * @param args precision integer the sax value that is divided chunk_size integer the length of
   *             sax string that is used to compare lag_window_size integer the length of window
   *             before string future_window_size integer the length of window after string
   *             threshold double the threshold that used to check if it is an anomaly
   */

  @Override
  public void init(Map<String, Object> args, TimeSeries timeseries) {
    try {
      timeseries.clear();
      this.time_series = timeseries;
      this.time_series_length = time_series.getLength();
      this.precision = (int) args.get("precision");
      this.chunk_size = (int) args.get("chunk_size");
      this.lag_window_size = (int) args.get("lag_window_size");
      this.future_window_size = (int) args.get("future_window_size");
      this.threshold = (double) args.get("threshold");
      sanity_check();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }


  /**
   * Initializer :param TimeSeries time_series: a TimeSeries object. :param TimeSeries
   * baseline_time_series: baseline TimeSeries. :param int precision: how many sections to
   * categorize values. :param int lag_window_size: lagging window size. :param int
   * future_window_size: future window size. :param int chunk_size: chunk size.
   */
  public Luminol(TimeSeries time_series) throws Exception {
    this.precision = Constants.Luminol.DEFAULT_BITMAP_PRECISION;
    this.chunk_size = Constants.Luminol.DEFAULT_BITMAP_CHUNK_SIZE;
    this.lag_window_size = Constants.Luminol.DEFAULT_BITMAP_LAGGING_WINDOW_SIZE;
    this.future_window_size = Constants.Luminol.DEFAULT_BITMAP_LEADING_WINDOW_SIZE;
    this.time_series = time_series;
    this.time_series_length = time_series.getLength();
    sanity_check();
  }

  public Luminol() {

  }

  /**
   * Check if there are enough data points.
   */
  private void sanity_check() throws Exception {
    int windows = lag_window_size + future_window_size;
    if (lag_window_size < 1 || future_window_size < 1 || time_series_length < windows
        || windows < Constants.Luminol.DEFAULT_BITMAP_MINIMAL_POINTS_IN_WINDOWS) {
      throw new Exception("Not Enough Data Points");
    }
    if (lag_window_size > Constants.Luminol.DEFAULT_BITMAP_MAXIMAL_POINTS_IN_WINDOWS) {
      lag_window_size = Constants.Luminol.DEFAULT_BITMAP_MAXIMAL_POINTS_IN_WINDOWS;
    }
    if (future_window_size > Constants.Luminol.DEFAULT_BITMAP_MAXIMAL_POINTS_IN_WINDOWS) {
      future_window_size = Constants.Luminol.DEFAULT_BITMAP_MAXIMAL_POINTS_IN_WINDOWS;
    }
  }

  /**
   * Generate SAX representation(Symbolic Aggregate approXimation) for a single data point. Read
   * more about it here: Assumption-Free Anomaly Detection in Time Series(http://alumni.cs.ucr.edu/~ratana/SSDBM05.pdf).
   * :param dict sections: value sections. :param float value: value to be categorized. :return str:
   * a SAX representation.
   */
  private String generate_SAX_single(HashMap<Integer, Double> sections, double value) {

    int sax = 0;
    for (int section_number : sections.keySet()) {
      Double section_lower_bound = sections.get(section_number);
      if (value >= section_lower_bound) {
        sax = section_number;
      } else {
        break;
      }
    }
    return sax + "";
  }

  /**
   * Generate SAX representation for all values of the time series.
   */
  private void generate_SAX() {
    HashMap<Integer, Double> sections = new HashMap<Integer, Double>();
    value_min = Double.MAX_VALUE;
    value_max = Double.MIN_VALUE;
    for (TimePoint tp : time_series.getTimeseries()) {
      if (tp.getObsVal() > value_max) {
        value_max = tp.getObsVal();
      }
      if (tp.getObsVal() < value_min) {
        value_min = tp.getObsVal();
      }
    }

    double section_height = (value_max - value_min) / precision;
    for (int section_number = 0; section_number < precision; section_number++) {
      sections.put(section_number, value_min + section_number * section_height);
    }


    for (TimePoint tp : time_series.getTimeseries()) {
      this.s = generate_SAX_single(sections, tp.getObsVal());
      sax +=s;
    }
  }
  String s;
  /**
   * Form a chunk frequency dictionary from a SAX representation. :param str sax: a SAX
   * representation. :return dict: frequency dictionary for chunks in the SAX representation.
   */
  private HashMap<String, Integer> construct_SAX_chunk_dict(String sax) {
    HashMap<String, Integer> frequency = new HashMap<String, Integer>();
    int length = sax.length();
    for (int i = 0; i < length; i++) {
      if ((i + chunk_size) <= length) {
        String chunk = sax.substring(i, i + chunk_size);
        if (frequency.containsKey(chunk)) {
          frequency.put(chunk, (frequency.get(chunk) + 1));
        } else {
          frequency.put(chunk, 1);
        }
      }
    }
    return frequency;
  }

  /**
   * Construct the chunk dicts for lagging window and future window at each index. e.g: Suppose we
   * have a SAX sequence as '1234567890', both window sizes are 3, and the chunk size is 2. The
   * first index that has a lagging window is 3. For index equals 3, the lagging window has sequence
   * '123', the chunk to leave lagging window(lw_leave_chunk) is '12', and the chunk to enter
   * lagging window(lw_enter_chunk) is '34'. Therefore, given chunk dicts at i, to compute chunk
   * dicts at i+1, simply decrement the count for lw_leave_chunk, and increment the count for
   * lw_enter_chunk from chunk dicts at i. Same method applies to future window as well.
   */
  private void construct_all_SAX_chunk_dict() {

    int length = this.time_series_length;
    int lws = this.lag_window_size;
    int fws = this.future_window_size;
    int chunk_size = this.chunk_size;
    HashMap<String, Integer> lag_dict, fut_dict;
    String lw_leave_chunk = null, lw_enter_chunk = null, fw_leave_chunk = null, fw_enter_chunk = null;

    ArrayList<HashMap<String, Integer>> lag_dicts = new ArrayList<HashMap<String, Integer>>(
        Collections.nCopies(length, null));
    ArrayList<HashMap<String, Integer>> fut_dicts = new ArrayList<HashMap<String, Integer>>(
        Collections.nCopies(length, null));

    for (int i = 0; i < length; i++) {
      if (i < lws || i > length - fws - 1) {
        lag_dicts.set(i, null);
      } else {
        // Just enter valid range.
        if (lag_dicts.get(i - 1) == null) {
          lag_dict = construct_SAX_chunk_dict(sax.substring(i - lws, i));
          lag_dicts.set(i, lag_dict);
          lw_leave_chunk = this.sax.substring(0, chunk_size);
          lw_enter_chunk = this.sax.substring(i - chunk_size + 1, i + 1);

          fut_dict = construct_SAX_chunk_dict(sax.substring(i, i + fws));
          fut_dicts.set(i, fut_dict);
          fw_leave_chunk = this.sax.substring(i, i + chunk_size);
          fw_enter_chunk = this.sax.substring(i + fws + 1 - chunk_size, i + fws + 1);
        } else {
          // Update dicts according to leave_chunks and enter_chunks.
          lag_dict = (HashMap<String, Integer>) lag_dicts.get(i - 1).clone();
          if (lag_dict.containsKey(lw_leave_chunk)) {
            lag_dict.put(lw_leave_chunk, lag_dict.get(lw_leave_chunk) - 1);
            if(lag_dict.get(lw_leave_chunk)<1)
              lag_dict.remove(lw_leave_chunk);
          }
          if (lag_dict.containsKey(lw_enter_chunk)) {
            lag_dict.put(lw_enter_chunk, lag_dict.get(lw_enter_chunk) + 1);
          } else {
            lag_dict.put(lw_enter_chunk, 1);
          }
          lag_dicts.set(i, lag_dict);

          fut_dict = (HashMap<String, Integer>) fut_dicts.get(i - 1).clone();
          if (fut_dict.containsKey(fw_leave_chunk)) {
          fut_dict.put(fw_leave_chunk, fut_dict.get(fw_leave_chunk) - 1);
            if(fut_dict.get(fw_leave_chunk)<1)
              fut_dict.remove(fw_leave_chunk);
          }
          if (fut_dict.containsKey(fw_enter_chunk)) {
            fut_dict.put(fw_enter_chunk, fut_dict.get(fw_enter_chunk) + 1);
          } else {
            fut_dict.put(fw_enter_chunk, 1);
          }
          fut_dicts.set(i, fut_dict);

          // Updata leave_chunks and enter_chunks.
          lw_leave_chunk = this.sax.substring(i - lws, i - lws + chunk_size);
          lw_enter_chunk = this.sax.substring(i - chunk_size + 1, i + 1);
          fw_leave_chunk = this.sax.substring(i, i + chunk_size);
          fw_enter_chunk = this.sax.substring(i + fws + 1 - chunk_size, i + fws + 1);

        }
      }
    }
    this.lag_dicts = lag_dicts;
    this.fut_dicts = fut_dicts;
  }

  /**
   * Compute distance difference between two windows' chunk frequencies, which is then marked as the
   * anomaly score of the data point on the window boundary in the middle. :param int i: index of
   * the data point between two windows. :return float: the anomaly score.
   */
  private double compute_anom_score_between_two_windows(int i) {
    HashMap<String, Integer> lag_window_chunk_dict = this.lag_dicts.get(i);
    HashMap<String, Integer> future_window_chunk_dict = this.fut_dicts.get(i);
    float score = 0;
    for (Entry<String, Integer> chunk : lag_window_chunk_dict.entrySet()) {
      if (future_window_chunk_dict.containsKey(chunk.getKey())) {
        score += Math.pow(future_window_chunk_dict.get(chunk.getKey()) - chunk.getValue(), 2);
      } else {
        score += Math.pow(chunk.getValue(), 2);
      }
    }
    for (Entry<String, Integer> chunk : future_window_chunk_dict.entrySet()) {
      if (!lag_window_chunk_dict.containsKey(chunk.getKey())) {
        score += Math.pow(chunk.getValue(), 2);
      }
    }
    return score;
  }

  /**
   * Compute anomaly scores for the time series by sliding both lagging window and future window.
   */
  public void set_scores() {
    TreeMap<Long, Double> anom_scores = new TreeMap<Long, Double>();
    this.generate_SAX();
    this.construct_all_SAX_chunk_dict();
    int length = this.time_series_length;
    int lws = this.lag_window_size;
    int fws = this.future_window_size;
    for (int i = 0; i < this.time_series.getTimeseries().size(); i++) {
      if (i < lws || i > length - fws - 1) {
        anom_scores.put(this.time_series.getTimeseries().get(i).getTimestamp(), 0d);
      } else {
        anom_scores.put(this.time_series.getTimeseries().get(i).getTimestamp(),
            this.compute_anom_score_between_two_windows(i));
      }
    }
    this.anom_scores = this.denoise_scores(anom_scores);
  }

  /**
   * Denoise anomaly scores. Low anomaly scores could be noisy. The following two series will have
   * good correlation result with out denoise: [0.08, 4.6, 4.6, 4.6, 1.0, 1.0] [0.0010, 0.0012,
   * 0.0012, 0.0008, 0.0008] while the second series is pretty flat(suppose it has a max score of
   * 100). param dict scores: the scores to be denoised.
   */
  public TreeMap<Long, Double> denoise_scores(TreeMap<Long, Double> anom_scores) {
    if (anom_scores != null) {
      double maxmal = Collections.max(anom_scores.values());
      if (maxmal > 0) {
        for (Entry<Long, Double> ent : anom_scores.entrySet()) {
          if (anom_scores.get(ent.getKey())
              < Constants.Luminol.DEFAULT_NOISE_PCT_THRESHOLD * maxmal) {
            anom_scores.put(ent.getKey(), 0d);
          }
        }
      }
    }
    return anom_scores;
  }


}
