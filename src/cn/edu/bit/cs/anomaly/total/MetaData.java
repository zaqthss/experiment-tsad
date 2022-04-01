package cn.edu.bit.cs.anomaly.total;

import java.util.Map;
import java.util.Set;

public interface MetaData {
  public Map<String, Map<String, Object>> getDataset();
  public Map<String, Map<String, Map<String, Object>>> getDataAlgParam();
  public Set<String> getSets();
  public Map<String, String> getRawToName();

}
