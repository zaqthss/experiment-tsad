package cn.edu.bit.cs.anomaly.total;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PointMetaData implements MetaData{

  public static PointMetaData getInstance(){
    return new PointMetaData();
  }
  @Override
  public Map<String, Map<String, Object>> getDataset() {
    return dataset;
  }

  @Override
  public Map<String, Map<String, Map<String, Object>>> getDataAlgParam() {
    return dataAlgParam;
  }

  @Override
  public Set<String> getSets() {
    return pointSets;
  }

  @Override
  public Map<String, String> getRawToName() {
    return rawToName;
  }

  public Map<String, Map<String, Object>> dataset =
      new HashMap<String, Map<String, Object>>() {
        {
          // _point_context_10000_0.1_1
         put(
              "smtp",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "point");
                  put("rawPrefix", "SMTP");
                  put("dim", 3);
                }
              });
          put(
              "tao",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "syn");
                  put("rawPrefix", "tao_pointc_100000_0.1_1");
                  put("dim", 3);
                }
              });

          // 4.3.2
          put(
              "stock_100k_point",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "syn");
                  put("rawPrefix", "stock_pointc_100000_0.1_1");
                  put("dim", 1);
                }
              });
          //Cui part
          put(
              "uni_100",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "cui/uni");
                  put("rawPrefix", "uni_pointg_1000_0.1_1");
                  put("dim", 1);
                }
              });
          put(
              "uni_0.1",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "cui/uni");
                  put("rawPrefix", "uni_pointg_10000_0.1_1");
                  put("dim", 1);
                }
              });
          put(
              "uni_0.3",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "cui/uni");
                  put("rawPrefix", "uni_pointg_10000_0.3_1");
                  put("dim", 1);
                }
              });
          put(
              "uni_0.05",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "cui/uni");
                  put("rawPrefix", "uni_pointg_10000_0.05_1");
                  put("dim", 1);
                }
              });
          put(
              "uni_all",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "cui/uni");
                  put("rawPrefix", "uni");
                  put("dim", 1);
                }
              });
          put(
              "stock_0.1",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "cui/uni");
                  put("rawPrefix", "stock_pointg_10000_0.1_1");
                  put("dim", 1);
                }
              });
          put(
              "stock_0.1_tc",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "cui/uni");
                  put("rawPrefix", "stock_pointc_10000_0.1_2");
                  put("dim", 1);
                }
              });
          put(
              "stock_0.3",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "cui/uni");
                  put("rawPrefix", "stock_pointg_10000_0.3_1");
                  put("dim", 1);
                }
              });
          put(
              "stock_all",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "cui/uni");
                  put("rawPrefix", "stock");
                  put("dim", 1);
                }
              });
          put(
              "mul_0.1",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "cui/mul");
                  put("rawPrefix", "mul_pointg_10000_0.1_1");
                  put("dim", 32);
                }
              });
          put(
              "mul_all",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "cui/mul");
                  put("rawPrefix", "mul");
                  put("dim", 32);
                }
              });
          put(
              "tao_0.1",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "cui/mul");
                  put("rawPrefix", "tao_pointg_1000_0.1_1");
                  put("dim", 3);
                }
              });
          put(
              "tao_5000_0.1",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "cui/mul");
                  put("rawPrefix", "tao_pointg_5000_0.1_1");
                  put("dim", 3);
                }
              });
          put(
              "tao_all",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "cui/mul");
                  put("rawPrefix", "tao");
                  put("dim", 3);
                }
              });
          //Single Datasets
          put(
              "yahoo",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "cui/uni");
                  put("rawPrefix", "yahoo");
                  put("dim", 1);
                }
              });
          put(
              "twitter",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "cui/uni");
                  put("rawPrefix", "twitter");
                  put("dim", 1);
                }
              });
          put(
              "smtp_10k",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "cui/mul");
                  put("rawPrefix", "SMTP_10k_num_13");
                  put("dim", 3);
                }
              });
          put(
              "smtp",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "cui/mul");
                  put("rawPrefix", "SMTP");
                  put("dim", 3);
                }
              });

          put(
              "credit_5k",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "cui/mul");
                  put("rawPrefix", "credit_5k_num_35");
                  put("dim", 28);
                }
              });
          put(
              "credit",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "cui/mul");
                  put("rawPrefix", "credit");
                  put("dim", 28);
                }
              });
          put(
              "ecg_5k",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "cui/mul");
                  put("rawPrefix", "ECG_5k_num_765");
                  put("dim", 32);
                }
              });
          put(
              "ecg",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "cui/mul");
                  put("rawPrefix", "ECG");
                  put("dim", 32);
                }
              });
          put(
              "pen",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "cui/mul");
                  put("rawPrefix", "pen");
                  put("dim", 16);
                }
              });
          put(
              "dlr",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "cui/mul");
                  put("rawPrefix", "dlr");
                  put("dim", 9);
                }
              });
          put(
              "uni_subs_sp",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "syn/sub");
                  put("rawPrefix", "uni_subs_len_50_5000_0.1_1");
                  put("dim", 1);
                }
              });
          put(
              "uni_subg_sp",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "syn/sub");
                  put("rawPrefix", "uni_subg_len_50_5000_0.1_1");
                  put("dim", 1);
                }
              });
          put(
              "uni_subt_sp",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "syn/sub");
                  put("rawPrefix", "uni_subt_len_50_5000_0.1_1");
                  put("dim", 1);
                }
              });
          put(
              "exathlon_sp",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "syn");
                  put("rawPrefix", "exathlon_dim1");
                  put("dim", 1);
                }
              });
        }
      };

  // TODO: fill other parameters
  public Map<String, Map<String, Map<String, Object>>> dataAlgParam =
      new HashMap<String, Map<String, Map<String, Object>>>() {
        {
          put(
              "dlr",
              new HashMap<String, Map<String, Object>>() {
                {
                  put(
                      "CPOD",
                      new HashMap<String, Object>() {
                        {
                          put("mul", 30);
                          put("sSize", 100);
                          put("R", 11.);
                          put("K", 90);
                        }
                      });
                  put(
                      "NETS",
                      new HashMap<String, Object>() {
                        {
                          put("R", 9.);
                          put("K", 70);
                          put("S", 60);
                          put("W", 2750);
                          put("subDim",1);
                          put("nW",10000);
                        }
                      });
                  put(
                      "Stare",
                      new HashMap<String, Object>() {
                        {
                          put("R", 20.);
                          put("K", 200);
                          put("S",  5000);
                          put("W", 10000);
                          put("rate", 0.2);
                          put("skipThred", 0.15);
                          put("nW", 10000);
                        }
                      });
                  put(
                      "Luminol",
                      new HashMap<String, Object>() {
                        {
                          put("precision", 8);
                          put("chunk_size", 4);
                          put("lag_window_size", 10);
                          put("future_window_size", 10);
                          put("threshold", 0.5);
                        }
                      });
                  put(
                      "SHESD",
                      new HashMap<String, Object>() {
                        {
                          put("seasonality", 100);
                          put("maxAnoms", 0.3);
                          put("alpha", 0.3);
                          put("anomsThreshold", 0.5);
                        }
                      });
                }
              });
          put(
              "smtp",
              new HashMap<String, Map<String, Object>>() {
                {
                  put(
                      "CPOD",
                      new HashMap<String, Object>() {
                        {
                          put("mul", 10);
                          put("sSize", 100);
                          put("R", 9.);
                          put("K", 200);
                        }
                      });
                  put(
                      "NETS",
                      new HashMap<String, Object>() {
                        {
                          put("R", 9.);
                          put("K", 200);
                          put("S", 100);
                          put("W", 1000);
                          put("subDim",1);
                          put("nW",10000);
                        }
                      });
                  put(
                      "Stare",
                      new HashMap<String, Object>() {
                        {
                          put("R", 7.);
                          put("K", 100);
                          put("S", 10000);
                          put("W", 20000);
                          put("rate", 0.0003);
                          put("skipThred", 0.15);
                          put("nW", 10000);
                        }
                      });
                  put(
                      "Luminol",
                      new HashMap<String, Object>() {
                        {
                          put("precision", 8);
                          put("chunk_size", 4);
                          put("lag_window_size", 10);
                          put("future_window_size", 10);
                          put("threshold", 0.5);
                        }
                      });
                  put(
                      "SHESD",
                      new HashMap<String, Object>() {
                        {
                          put("seasonality", 100);
                          put("maxAnoms", 0.3);
                          put("alpha", 0.3);
                          put("anomsThreshold", 0.5);
                        }
                      });
                }
              });
          put(
              "ecg",
              new HashMap<String, Map<String, Object>>() {
                {
                  put(
                      "CPOD",
                      new HashMap<String, Object>() {
                        {
                          put("mul", 20);
                          put("sSize", 150);
                          put("R", 2.25);
                          put("K", 300);
                        }
                      });
                  put(
                      "NETS",
                      new HashMap<String, Object>() {
                        {
                          put("R", 2.25);
                          put("K", 300);
                          put("S", 150);
                          put("W", 3000);
                          put("subDim",1);
                          put("nW",10000);
                        }
                      });
                  put(
                      "Stare",
                      new HashMap<String, Object>() {
                        {
                          put("R", 3.);
                          put("K", 600);
                          put("S", 150);
                          put("W", 1000);
                          put("rate", 0.05);
                          put("skipThred", 0.15);
                          put("nW", 10000);
                        }
                      });
                  put(
                      "Luminol",
                      new HashMap<String, Object>() {
                        {
                          put("precision", 8);
                          put("chunk_size", 4);
                          put("lag_window_size", 10);
                          put("future_window_size", 10);
                          put("threshold", 0.5);
                        }
                      });
                  put(
                      "SHESD",
                      new HashMap<String, Object>() {
                        {
                          put("seasonality", 100);
                          put("maxAnoms", 0.3);
                          put("alpha", 0.3);
                          put("anomsThreshold", 0.5);
                        }
                      });
                }
              });
          put(
              "uni_all",
              new HashMap<String, Map<String, Object>>() {
                {
                  put(
                      "CPOD",
                      new HashMap<String, Object>() {
                        {
                          put("mul", 4);
                          put("sSize", 125);
                          put("R", 1.);
                          put("K", 150);
                        }
                      });
                  put(
                      "NETS",
                      new HashMap<String, Object>() {
                        {
                          put("R", 1.);
                          put("K", 150);
                          put("S", 125);
                          put("W", 500);
                          put("subDim",1);
                          put("nW",10000);
                        }
                      });
                  put(
                      "Stare",
                      new HashMap<String, Object>() {
                        {
                          put("R", 0.5);
                          put("K", 125);
                          put("S", 200);
                          put("W", 2000);
                          put("rate", 0.1);
                          put("skipThred", 0.1);
                          put("nW", 10000);
                        }
                      });
                  put(
                      "Luminol",
                      new HashMap<String, Object>() {
                        {
                          put("precision", 4);
                          put("chunk_size", 8);
                          put("lag_window_size", 10);
                          put("future_window_size", 10);
                          put("threshold", 0.8);
                        }
                      });
                  put(
                      "SHESD",
                      new HashMap<String, Object>() {
                        {
                          put("seasonality", 100);
                          put("maxAnoms", 0.3);
                          put("alpha", 0.3);
                          put("anomsThreshold", 0.5);
                        }
                      });
                }
              });
          put(
              "stock_all",
              new HashMap<String, Map<String, Object>>() {
                {
                  put(
                      "CPOD",
                      new HashMap<String, Object>() {
                        {
                          put("mul", 6);
                          put("sSize", 50);
                          put("R", 8.);
                          put("K", 30);
                        }
                      });
                  put(
                      "NETS",
                      new HashMap<String, Object>() {
                        {
                          put("R", 8.);
                          put("K", 30);
                          put("S", 50);
                          put("W", 300);
                          put("subDim",1);
                          put("nW",10000);
                        }
                      });
                  put(
                      "Stare",
                      new HashMap<String, Object>() {
                        {
                          put("R", 10.);
                          put("K", 5);
                          put("S", 4);
                          put("W", 16);
                          put("rate", 0.1);
                          put("skipThred", 0.15);
                          put("nW", 10000);
                        }
                      });
                  put(
                      "Luminol",
                      new HashMap<String, Object>() {
                        {
                          put("precision", 3);
                          put("chunk_size", 9);
                          put("lag_window_size", 10);
                          put("future_window_size", 10);
                          put("threshold", 0.7);
                        }
                      });
                  put(
                      "SHESD",
                      new HashMap<String, Object>() {
                        {
                          put("seasonality", 1000);
                          put("maxAnoms", 0.3);
                          put("alpha", 0.3);
                          put("anomsThreshold", 0.5);
                        }
                      });

                }
              });
          put(
              "stock_0.1",
              new HashMap<String, Map<String, Object>>() {
                {
                  put(
                      "CPOD",
                      new HashMap<String, Object>() {
                        {
                          put("mul", 6);
                          put("sSize", 50);
                          put("R", 8.);
                          put("K", 30);
                        }
                      });
                  put(
                      "NETS",
                      new HashMap<String, Object>() {
                        {
                          put("R", 8.);
                          put("K", 30);
                          put("S", 50);
                          put("W", 300);
                          put("subDim",1);
                          put("nW",10000);
                        }
                      });
                  put(
                      "Stare",
                      new HashMap<String, Object>() {
                        {
                          put("R", 10.);
                          put("K", 5);
                          put("S", 4);
                          put("W", 16);
                          put("rate", 0.1);
                          put("skipThred", 0.15);
                          put("nW", 10000);
                        }
                      });
                  put(
                      "Luminol",
                      new HashMap<String, Object>() {
                        {
                          put("precision", 3);
                          put("chunk_size", 9);
                          put("lag_window_size", 10);
                          put("future_window_size", 10);
                          put("threshold", 0.7);
                        }
                      });
                  put(
                      "SHESD",
                      new HashMap<String, Object>() {
                        {
                          put("seasonality", 1000);
                          put("maxAnoms", 0.3);
                          put("alpha", 0.3);
                          put("anomsThreshold", 0.5);
                        }
                      });

                }
              });
          put(
              "stock_0.1_tc",
              new HashMap<String, Map<String, Object>>() {
                {
                  put(
                      "CPOD",
                      new HashMap<String, Object>() {
                        {
                          put("mul", 6);
                          put("sSize", 50);
                          put("R", 8.);
                          put("K", 30);
                        }
                      });
                  put(
                      "NETS",
                      new HashMap<String, Object>() {
                        {
                          put("R", 8.);
                          put("K", 30);
                          put("S", 50);
                          put("W", 300);
                          put("subDim",1);
                          put("nW",10000);
                        }
                      });
                  put(
                      "Stare",
                      new HashMap<String, Object>() {
                        {
                          put("R", 10.);
                          put("K", 5);
                          put("S", 4);
                          put("W", 16);
                          put("rate", 0.1);
                          put("skipThred", 0.15);
                          put("nW", 10000);
                        }
                      });
                  put(
                      "Luminol",
                      new HashMap<String, Object>() {
                        {
                          put("precision", 3);
                          put("chunk_size", 9);
                          put("lag_window_size", 10);
                          put("future_window_size", 10);
                          put("threshold", 0.7);
                        }
                      });
                  put(
                      "SHESD",
                      new HashMap<String, Object>() {
                        {
                          put("seasonality", 1000);
                          put("maxAnoms", 0.3);
                          put("alpha", 0.3);
                          put("anomsThreshold", 0.5);
                        }
                      });

                }
              });
          put(
              "mul_all",
              new HashMap<String, Map<String, Object>>() {
                {
                  put(
                      "CPOD",
                      new HashMap<String, Object>() {
                        {
                          put("mul", 30);
                          put("sSize", 125);
                          put("R", 19.);
                          put("K", 2000);
                        }
                      });
                  put(
                      "NETS",
                      new HashMap<String, Object>() {
                        {
                          put("R", 19.);
                          put("K", 2000);
                          put("S", 125);
                          put("W", 3750);
                          put("subDim",1);
                          put("nW",10000);
                        }
                      });
                  put(
                      "Stare",
                      new HashMap<String, Object>() {
                        {
                          put("R", 21.);
                          put("K", 1000);
                          put("S", 200);
                          put("W", 3000);
                          put("rate", 0.05);
                          put("skipThred", 0.15);
                          put("nW", 10000);
                        }
                      });
                  put(
                      "Luminol",
                      new HashMap<String, Object>() {
                        {
                          put("precision", 5);
                          put("chunk_size", 5);
                          put("lag_window_size", 10);
                          put("future_window_size", 10);
                          put("threshold", 0.1);
                        }
                      });
                  put(
                      "SHESD",
                      new HashMap<String, Object>() {
                        {
                          put("seasonality", 1000);
                          put("maxAnoms", 0.3);
                          put("alpha", 0.3);
                          put("anomsThreshold", 0.2);
                        }
                      });

                }
              });
          put(
              "tao_all",
              new HashMap<String, Map<String, Object>>() {
                {
                  put(
                      "CPOD",
                      new HashMap<String, Object>() {
                        {
                          put("mul", 10);
                          put("sSize", 100);
                          put("R", 10.);
                          put("K", 200);
                        }
                      });
                  put(
                      "NETS",
                      new HashMap<String, Object>() {
                        {
                          put("R", 10.);
                          put("K", 200);
                          put("S", 100);
                          put("W", 1000);
                          put("subDim",1);
                          put("nW",10000);
                        }
                      });
                  put(
                      "Stare",
                      new HashMap<String, Object>() {
                        {
                          put("R", 50.);
                          put("K", 50);
                          put("S", 100);
                          put("W", 1000);
                          put("rate", 0.1);
                          put("skipThred", 0.15);
                          put("nW", 10000);
                        }
                      });
                  put(
                      "Luminol",
                      new HashMap<String, Object>() {
                        {
                          put("precision", 5);
                          put("chunk_size", 3);
                          put("lag_window_size", 100);
                          put("future_window_size", 100);
                          put("threshold", 0.1);
                        }
                      });
                  put(
                      "SHESD",
                      new HashMap<String, Object>() {
                        {
                          put("seasonality", 1000);
                          put("maxAnoms", 0.1);
                          put("alpha", 0.1);
                          put("anomsThreshold", 0.5);
                        }
                      });

                }
              });
          put(
              "exathlon_sp",
              new HashMap<String, Map<String, Object>>() {
                {
                  put(
                      "CPOD",
                      new HashMap<String, Object>() {
                        {
                          put("mul", 4);
                          put("sSize", 20);
                          put("R", 1.5);
                          put("K", 60);
                        }
                      });
                  put(
                      "NETS",
                      new HashMap<String, Object>() {
                        {
                          put("R", 1.5);
                          put("K", 60);
                          put("S", 20);
                          put("W", 80);
                          put("subDim",1);
                          put("nW",10000);
                        }
                      });
                  put(
                      "Stare",
                      new HashMap<String, Object>() {
                        {
                          put("R", 10.);
                          put("K", 10);
                          put("S", 50);
                          put("W", 300);
                          put("rate", 0.7);
                          put("skipThred", 0.15);
                          put("nW", 10000);
                        }
                      });
                  put(
                      "SHESD",
                      new HashMap<String, Object>() {
                        {
                          put("seasonality", 1000);
                          put("maxAnoms", 0.2);
                          put("alpha", 0.2);
                          put("anomsThreshold", 0.4);
                        }
                      });

                }
              });
          put(
              "uni_subg_sp",
              new HashMap<String, Map<String, Object>>() {
                {
                  put(
                      "CPOD",
                      new HashMap<String, Object>() {
                        {
                          put("mul", 20);
                          put("sSize", 30);
                          put("R", 1.);
                          put("K", 200);
                        }
                      });
                  put(
                      "NETS",
                      new HashMap<String, Object>() {
                        {
                          put("R", 2.5);
                          put("K", 100);
                          put("S", 30);
                          put("W", 200);
                          put("subDim",1);
                          put("nW",10000);
                        }
                      });
                  put(
                      "Stare",
                      new HashMap<String, Object>() {
                        {
                          put("R", 2.);
                          put("K", 100);
                          put("S", 10);
                          put("W", 600);
                          put("rate", 0.1);
                          put("skipThred", 0.15);
                          put("nW", 10000);
                        }
                      });
                  put(
                      "SHESD",
                      new HashMap<String, Object>() {
                        {
                          put("seasonality", 100);
                          put("maxAnoms", 0.15);
                          put("alpha", 0.17);
                          put("anomsThreshold", 0.7);
                        }
                      });

                }
              });
          put(
              "uni_subs_sp",
              new HashMap<String, Map<String, Object>>() {
                {
                  put(
                      "CPOD",
                      new HashMap<String, Object>() {
                        {
                          put("mul", 10);
                          put("sSize", 5);
                          put("R", 1.2);
                          put("K", 15);
                        }
                      });
                  put(
                      "NETS",
                      new HashMap<String, Object>() {
                        {
                          put("R", 1.2);
                          put("K", 20);
                          put("S", 5);
                          put("W", 50);
                          put("subDim",1);
                          put("nW",10000);
                        }
                      });
                  put(
                      "Stare",
                      new HashMap<String, Object>() {
                        {
                          put("R", 2.);
                          put("K", 100);
                          put("S", 2);
                          put("W", 50);
                          put("rate", 0.05);
                          put("skipThred", 0.15);
                          put("nW", 10000);
                        }
                      });
                  put(
                      "SHESD",
                      new HashMap<String, Object>() {
                        {
                          put("seasonality", 50);
                          put("maxAnoms", 0.15);
                          put("alpha", 0.2);
                          put("anomsThreshold", 0.7);
                        }
                      });

                }
              });
          put(
              "yahoo",
              new HashMap<String, Map<String, Object>>() {
                {
                  put(
                      "CPOD",
                      new HashMap<String, Object>() {
                        {
                          put("mul", 4);
                          put("sSize", 100);
                          put("R", 700.);
                          put("K", 7);
                        }
                      });
                  put(
                      "NETS",
                      new HashMap<String, Object>() {
                        {
                          put("R", 600.);
                          put("K", 5);
                          put("S", 100);
                          put("W", 400);
                          put("subDim",1);
                          put("nW",10000);
                        }
                      });
                  put(
                      "Stare",
                      new HashMap<String, Object>() {
                        {
                          put("R", 200.);
                          put("K", 20);
                          put("S", 10);
                          put("W", 400);
                          put("rate", 0.005);
                          put("skipThred", 0.15);
                          put("nW", 10000);
                        }
                      });
                  put(
                      "SHESD",
                      new HashMap<String, Object>() {
                        {
                          put("seasonality", 50);
                          put("maxAnoms", 0.007);
                          put("alpha", 0.007);
                          put("anomsThreshold", 0.1);
                        }
                      });
                  put(
                      "Luminol",
                      new HashMap<String, Object>() {
                        {
                          put("precision", 3);
                          put("chunk_size", 4);
                          put("lag_window_size", 200);
                          put("future_window_size", 200);
                          put("threshold", 0.8);
                        }
                      });

                }
              });
          put(
              "twitter",
              new HashMap<String, Map<String, Object>>() {
                {
                  put(
                      "CPOD",
                      new HashMap<String, Object>() {
                        {
                          put("mul", 3);
                          put("sSize", 150);
                          put("R", 50.);
                          put("K", 80);
                        }
                      });
                  put(
                      "NETS",
                      new HashMap<String, Object>() {
                        {
                          put("R", 50.);
                          put("K", 90);
                          put("S", 100);
                          put("W", 700);
                          put("subDim",1);
                          put("nW",10000);
                        }
                      });
                  put(
                      "Stare",
                      new HashMap<String, Object>() {
                        {
                          put("R", 60.);
                          put("K", 100);
                          put("S", 1000);
                          put("W", 7000);
                          put("rate", 0.05);
                          put("skipThred", 0.15);
                          put("nW", 10000);
                        }
                      });
                  put(
                      "SHESD",
                      new HashMap<String, Object>() {
                        {
                          put("seasonality", 200);
                          put("maxAnoms", 0.007);
                          put("alpha", 0.006);
                          put("anomsThreshold", 0.006);
                        }
                      });
                  put(
                      "Luminol",
                      new HashMap<String, Object>() {
                        {
                          put("precision", 10);
                          put("chunk_size", 8);
                          put("lag_window_size", 100);
                          put("future_window_size", 100);
                          put("threshold", 0.1);
                        }
                      });


                }
              });
        }
      };

  public Set<String> pointSets =
      new HashSet<>(
          Arrays.asList(
              "stock_10k_point",
              "uni_point",
              "credit_5k",
              "ecg_5k",
              "smtp",
              "smtp_10k",
              "tao",
              "tao_10k",
              //Proformance
              "http",
              "ecg",
              "yahoo",
              "credit",
              "pen",
              "dlr",
              "twitter",
              //Experiment
              "stock_all",
              "stock_0.3",
              "stock_0.1",
              "uni_all",
              "uni_0.1",
              "uni_0.3",
              "uni_0.05",
              "uni_100",
              "tao_all",
              "tao_0.1",
              "mul_all",
              "mul_0.1",
              "uni_subs_sp",
              "uni_subg_sp",
              "uni_subt_sp",
              "exathlon_sp",
              "tao_5000_0.1",
              "stock_0.1_tc"));

  public Map<String, String> rawToName = new HashMap<>();

  public PointMetaData() {
    if (rawToName.isEmpty()) {
      for (String dsName : dataset.keySet()) {
        rawToName.put((String) dataset.get(dsName).get("rawPrefix"), dsName);
      }
    }
  }


}
