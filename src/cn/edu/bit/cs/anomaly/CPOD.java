package cn.edu.bit.cs.anomaly;

import cn.edu.bit.cs.anomaly.entity.CPOD_TimePoint;
import cn.edu.bit.cs.anomaly.entity.CPOD_TimeSeries;
import cn.edu.bit.cs.anomaly.entity.TimePointMulDim;
import cn.edu.bit.cs.anomaly.entity.TimeSeriesMulDim;
import cn.edu.bit.cs.anomaly.util.Constants;
import cn.edu.bit.cs.anomaly.util.Constants.IS_ANOMALY;
import cn.edu.bit.cs.anomaly.util.mtree.CorePointMTree;
import cn.edu.bit.cs.anomaly.util.mtree.DistanceFunction;
import cn.edu.bit.cs.anomaly.util.mtree.MTree;

import java.util.*;
import java.util.Map.Entry;


public class CPOD implements MultiDimAlgorithm {

  public TimeSeriesMulDim<TimePointMulDim> origin;
  public CPOD_TimeSeries timeseries; // data stream
  public int wMultipleS; // multiple slide to window
  public int sSize;// slide size
  public double R;// distance threshold r
  public int K;// neighbor threshold k

  private ArrayList<CPOD_Slide> window;
  private List<CorePointMTree> allCores;
  private DistanceFunction<? super CPOD_TimePoint> distanceFunction;
  private ArrayList<CPOD_TimePoint> outliers;

  public CPOD() {
    this.sSize = Constants.CPOD.DEFAULT_SLIDE_SIZE;
    this.R = Constants.CPOD.DEFAULT_R;
    this.K = Constants.CPOD.DEFAULT_K;
    init();
  }


  public CPOD(int mul, int sSize, double R, int K) {
    this.wMultipleS = mul;
    this.sSize = sSize;
    this.R = R;
    this.K = K;
    init();
  }

  @Override
  public void run() {
    for (int i = 0; i < this.wMultipleS - 1; i++) {
      CPOD_Slide slide = new CPOD_Slide(i,
          timeseries.getSubPoints(i * sSize, (i + 1) * sSize - 1).getTimeseries());
      allCores.add(selectCore(slide));
      window.add(slide);
    }


    int slideCount = timeseries.getLength() / this.sSize;
    for (int itr = this.wMultipleS - 1; itr < slideCount; itr++) {
      if (itr != this.wMultipleS - 1) {
        CPOD_Slide sExpired = removeOldestSlide(window);
        processExpiredSlide(sExpired);
      }
      CPOD_Slide sNew = new CPOD_Slide(itr,
          timeseries.getSubPoints(itr * sSize, (itr + 1) * sSize - 1).getTimeseries());
      CorePointMTree corepoints = selectCore(sNew);
      allCores.add(itr, corepoints);
      updateHalfRCount(corepoints);
      this.window.add(sNew);
      for (CPOD_TimePoint tp : sNew) {
        if (tp.closeCore.e0.size() > this.K) {
          continue;
        }
        FindNeighbor(tp, sNew);
        if (tp.neighborCount.size() < this.K) {
          addOutlier(tp);
        }
      }
      for (CPOD_Slide ss : this.window) {
        if (ss.sidx == sNew.sidx) {
          continue;
        }
        for (CPOD_TimePoint tp : ss) {
          if (tp.neighborCount.size() < this.K) {
            FindNeighbor(tp, ss);
            if (tp.neighborCount.size() < this.K) {
              addOutlier(tp);
            }
          }
        }
      }
    }
    outputProcess();

  }

  private void outputProcess(){
    for (CPOD_TimePoint tp : outliers) {
      origin.getTimeseries().get(tp.id).setIs_anomaly(IS_ANOMALY.TRUE);
    }
  }
  private void addOutlier(CPOD_TimePoint tp){
    this.outliers.add(tp);
  }

  /**
   * @param args       mul integer the times of window and slide sSize integer slide size R double
   *                   distance threshold r K integer neighbor threshold k
   * @param timeseries
   */
  @Override
  public void init(Map<String, Object> args, TimeSeriesMulDim timeseries) {
    this.wMultipleS = (int) args.get("mul");
    this.sSize = (int) args.get("sSize");
    this.R = (double) args.get("R");
    this.K = (int) args.get("K");
    timeseries.clear();
    this.timeseries = new CPOD_TimeSeries(timeseries);
    this.origin = timeseries;
    init();
  }

  private void init() {
    this.window = new ArrayList<>();
    this.allCores = new ArrayList<CorePointMTree>();
    this.distanceFunction = new CorePointMTree().getDistanceFunction();
    this.outliers  = new ArrayList<CPOD_TimePoint>();
  }


  /**
   * init core point with first window
   */
  public CorePointMTree initCore(CPOD_Slide D) {
    return selectCore(D);
  }

  public CorePointMTree selectCore(List<CPOD_TimePoint> slide) {
    CorePointMTree corePoints = new CorePointMTree();
    for (CPOD_TimePoint tp : slide) {
      boolean flag = true;
      MTree<CPOD_TimePoint>.ResultItem ri = findCore(tp, corePoints);
      if (ri != null) {
        addToE(corePoints.points.get(ri.data.id), tp, ri.distance);
        tp.closeCore = corePoints.points.get(ri.data.id);
        tp.distanceToCore = ri.distance;
      } else {
        for (CorePointMTree ent : allCores) {
          ri = findCore(tp, ent);
          if (ri != null) {
            addToE(ent.points.get(ri.data.id), tp, ri.distance);
            corePoints.add(ent.points.get(ri.data.id));
            tp.closeCore = ent.points.get(ri.data.id);
            tp.distanceToCore = ri.distance;
            flag = false;
            break;
          }
        }
        if (flag) {
          corePoints.add(tp);
          addToE(corePoints.points.get(tp.id), tp, 0);
          tp.closeCore = corePoints.points.get(tp.id);
          tp.distanceToCore = 0;
        }
      }
    }
    for (Entry<Integer, CorePoint> c : corePoints.points.entrySet()) {
      for (Entry<Integer, CorePoint> c2 : corePoints.points.entrySet()) {
        if (c.getValue().dataPoint.id != c2.getValue().dataPoint.id) {
          if (distanceFunction.calculate(c.getValue().dataPoint,
              c2.getValue().dataPoint) <= 3 * R) {
            CheckCoreWList(c.getValue(), c2.getValue().e0);
            CheckCoreWList(c.getValue(), c2.getValue().e1);
          }
        }
      }
    }
    return corePoints;
  }

  private void CheckCoreWList(CorePoint cp, Map<Integer, CPOD_TimePoint> map) {
    for (Entry<Integer, CPOD_TimePoint> ent : map.entrySet()) {
      double distance = distanceFunction.calculate(cp.dataPoint, ent.getValue());
      addToE(cp, ent.getValue(), distance);
    }
  }

  private void FindNeighbor(CPOD_TimePoint q, CPOD_Slide s) {
    List<CPOD_Slide>[] r = splitSlide(s);
    for (CPOD_Slide ss : r[1]) {
      FindNeighborInSlide(q, ss);
      q.lastRight = s;
      if (q.neighborCount.size() >= this.K) {
        return;
      }
    }
    for (CPOD_Slide ss : r[0]) {
      FindNeighborInSlide(q, ss);
      q.lastLeft = ss;
      if (q.neighborCount.size() >= this.K) {
        return;
      }
    }
    return;
  }

  private List<CPOD_Slide>[] splitSlide(CPOD_Slide slide) {
    List<CPOD_Slide>[] r = new ArrayList[2];
    r[0] = new ArrayList<>();
    r[1] = new ArrayList<>();
    for (CPOD_Slide s : this.window) {
      if (s.sidx < slide.sidx) {
        r[0].add(0, s);
      } else {
        r[1].add(s);
      }
    }
    return r;
  }

  private void FindNeighborInSlide(CPOD_TimePoint tp, CPOD_Slide slide) {
    CorePoint cp = tp.closeCore;
    double distance = tp.distanceToCore;
    if (distance < this.R / 2) {
      tp.neighborCount.addAll(cp.e0.keySet());
      if (tp.neighborCount.size() >= K) {
        return;
      }
      findNbInList(tp, cp.e1.values());
      if (tp.neighborCount.size() >= K) {
        return;
      }
      findNbInList(tp, cp.e2.values());
      if (tp.neighborCount.size() >= K) {
        return;
      }
    } else if (distance < this.R) {
      findNbInList(tp, cp.e0.values());
      if (tp.neighborCount.size() >= K) {
        return;
      }
      findNbInList(tp, cp.e1.values());
      if (tp.neighborCount.size() >= K) {
        return;
      }
      findNbInList(tp, cp.e2.values());
      if (tp.neighborCount.size() >= K) {
        return;
      }
      findNbInList(tp, cp.e3.values());
      if (tp.neighborCount.size() >= K) {
        return;
      }
    } else if (distance < this.R * 2) {
      for (CorePoint c : allCores.get(slide.sidx).points.values()) {
        findNbInList(tp, c.e0.values());
        if (tp.neighborCount.size() >= K) {
          return;
        }
        findNbInList(tp, c.e1.values());
        if (tp.neighborCount.size() >= K) {
          return;
        }
      }
    }

  }

  private void findNbInList(CPOD_TimePoint q, Collection<CPOD_TimePoint> c) {
    for (CPOD_TimePoint tp : c) {
      if (!q.neighborCount.contains(tp)) {
        double d = this.distanceFunction.calculate(q, tp);
        if (d < R) {
          q.neighborCount.add(tp.id);
        }
      }
    }
  }

  private void updateHalfRCount(CorePointMTree mtree) {
    for (Entry<Integer, CorePoint> ent : mtree.points.entrySet()) {
      for (Entry<Integer, CPOD_TimePoint> ent1 : ent.getValue().e0.entrySet()) {
        // the set, numSucNeighbors need to be checked if it is used for this kind of
        // neighbor add
        ent1.getValue().neighborCount.addAll(ent.getValue().e0.keySet());
      }
    }
  }

  private CPOD_Slide removeOldestSlide(ArrayList<CPOD_Slide> window) {
    return window.remove(0);
  }

  private void processExpiredSlide(List<CPOD_TimePoint> sExpired) {
    for (CPOD_TimePoint tp : sExpired) {
      removePointFormEList(tp.id);
      for (CPOD_Slide slide : this.window) {
        for (CPOD_TimePoint wtp : slide) {
          wtp.neighborCount.remove(tp.id);
        }
      }
    }
  }

  private void removePointFormEList(int id) {
    for (CorePointMTree cpmt : allCores) {
      if (cpmt.points.size() > 0) {
        for (Entry<Integer, CPOD.CorePoint> ent : cpmt.points.entrySet()) {
          if (ent.getValue().e0.containsKey(id)) {
            ent.getValue().e0.remove(id);
          } else if (ent.getValue().e1.containsKey(id)) {
            ent.getValue().e1.remove(id);
          } else if (ent.getValue().e2.containsKey(id)) {
            ent.getValue().e2.remove(id);
          } else if (ent.getValue().e3.containsKey(id)) {
            ent.getValue().e3.remove(id);
          }
        }
      }
    }
  }

  private void addToE(CorePoint c, CPOD_TimePoint tp, double distance) {
    if (distance <= 0.5 * R) {
      if (!c.e0.containsKey(tp.id)) {
        c.e0.put(tp.id, tp);
      }
    } else if (distance <= R) {
      if (!c.e1.containsKey(tp.id)) {
        c.e1.put(tp.id, tp);
      }
    } else if (distance <= 1.5 * R) {
      if (!c.e2.containsKey(tp.id)) {
        c.e2.put(tp.id, tp);
      }
    } else if (distance < 2 * R) {
      if (!c.e3.containsKey(tp.id)) {
        c.e3.put(tp.id, tp);
      }
    }

  }


  private MTree<CPOD_TimePoint>.ResultItem findCore(CPOD_TimePoint tp, CorePointMTree mtree) {
    if (mtree != null) {
      MTree<CPOD_TimePoint>.Query q = mtree.getNearest(tp,  R, 1);
      if (q.iterator().hasNext()) {
        return q.iterator().next();
      }
    }
    return null;
  }


  public static class CPOD_Slide extends ArrayList<CPOD_TimePoint> {

    public CPOD_Slide(int sid, List<CPOD_TimePoint> list) {
      super(list);
      this.sidx = sid;
    }

    public int sidx;
  }

  public static class CorePoint {

    public CPOD_TimePoint dataPoint;
    public HashMap<Integer, CPOD_TimePoint> e0, e1, e2, e3;

    public CorePoint(CPOD_TimePoint cpodtp) {
      this.e0 = new HashMap<Integer, CPOD_TimePoint>();
      this.e1 = new HashMap<Integer, CPOD_TimePoint>();
      this.e2 = new HashMap<Integer, CPOD_TimePoint>();
      this.e3 = new HashMap<Integer, CPOD_TimePoint>();
      this.dataPoint = cpodtp;
    }
  }

}
