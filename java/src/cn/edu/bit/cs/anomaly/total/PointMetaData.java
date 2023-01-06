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
        {put(
            "tao",
            new HashMap<String, Object>() {
              {
                put("dataDir", "Point\\mul\\Tao");
                put("tdir","train");
                put("tedir","test");
                put("vdir","valid");
                put("rawPrefix", "tao");
                put("dim", 3);
              }
            });
          put(
              "mul",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "Point\\mul\\Mul");
                  put("tdir","train");
                  put("tedir","test");
                  put("vdir","valid");
                  put("rawPrefix", "Mul");
                  put("dim", 32);
                }
              });
         put(
              "smtp",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "Point\\mul\\SMTP");
                  put("tdir","train");
                  put("tedir","test");
                  put("vdir","valid");
                  put("rawPrefix", "SMTP");
                  put("dim", 3);
                }
              });
          put(
              "smtp_10k_num_13",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "Point\\mul\\SMTP");
                  put("tdir","train");
                  put("tedir","test");
                  put("vdir","valid");
                  put("rawPrefix", "SMTP");
                  put("dim", 3);
                }
              });
          put(
              "pen",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "Point\\mul\\Pen");
                  put("tdir","train");
                  put("tedir","test");
                  put("vdir","valid");
                  put("rawPrefix", "Pen");
                  put("dim", 16);
                }
              });
          put(
              "ecg",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "Point\\mul\\ECG");
                  put("tdir","train");
                  put("tedir","test");
                  put("vdir","valid");
                  put("rawPrefix", "ECG");
                  put("dim", 32);
                }
              });
          put(
              "ecg_5k_num_765",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "Point\\mul\\ECG");
                  put("tdir","train");
                  put("tedir","test");
                  put("vdir","valid");
                  put("rawPrefix", "ecg_5k_num_765");
                  put("dim", 32);
                }
              });
          put(
              "credit",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "Point\\mul\\credit");
                  put("tdir","train");
                  put("tedir","test");
                  put("vdir","valid");
                  put("rawPrefix", "credit");
                  put("dim", 28);
                }
              });
          put(
              "credit_5k_num_35",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "Point\\mul\\credit");
                  put("tdir","train");
                  put("tedir","test");
                  put("vdir","valid");
                  put("rawPrefix", "credit_5k_num_35");
                  put("dim", 28);
                }
              });
          put(
              "stock",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "Point\\uni\\stock");
                  put("tdir","train");
                  put("tedir","test");
                  put("vdir","valid");
                  put("rawPrefix", "stock");
                  put("dim", 1);
                }
              });
          put(
              "twitter",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "Point\\uni\\twitter");
                  put("tdir","train");
                  put("tedir","test");
                  put("vdir","valid");
                  put("rawPrefix", "twitter");
                  put("dim", 1);
                }
              });
          put(
              "uni",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "Point\\uni\\Uni");
                  put("tdir","train");
                  put("tedir","test");
                  put("vdir","valid");
                  put("rawPrefix", "uni");
                  put("dim", 1);
                }
              });
          put(
              "yahoo",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "Point\\uni\\Yahoo");
                  put("tdir","train");
                  put("tedir","test");
                  put("vdir","valid");
                  put("rawPrefix", "Yahoo");
                  put("dim", 1);
                }
              });
          put(
              "dlr",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "Point\\mul\\dlr");
                  put("tdir","train");
                  put("tedir","test");
                  put("vdir","valid");
                  put("rawPrefix", "dlr");
                  put("dim", 3);
                }
              });
          put(
              "exathlon_sp_pos",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "Point\\other\\exathlon_sp_pos");
                  put("tdir","train");
                  put("tedir","test");
                  put("vdir","valid");
                  put("rawPrefix", "exathlon_sp_pos");
                  put("dim", 1);
                }
              });
          put(
              "uni_subg_sp_pos",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "Point\\other\\uni_subg_sp_pos");
                  put("tdir","train");
                  put("tedir","test");
                  put("vdir","valid");
                  put("rawPrefix", "uni_subg_sp_pos");
                  put("dim", 1);
                }
              });
          put(
              "uni_subs_sp_pos",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "Point\\other\\uni_subs_sp_pos");
                  put("tdir","train");
                  put("tedir","test");
                  put("vdir","valid");
                  put("rawPrefix", "uni_subs_sp_pos");
                  put("dim", 1);
                }
              });
          put(
              "uni_subt_sp_pos",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "Point\\other\\uni_subt_sp_pos");
                  put("tdir","train");
                  put("tedir","test");
                  put("vdir","valid");
                  put("rawPrefix", "uni_subt_sp_pos");
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
              "uni",
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
              "stock",
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
              "mul",
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
              "tao",
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
              "exathlon_sp_pos",
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
              "uni_subg_sp_pos",
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
              "uni_subs_sp_pos",
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
              "uni_subt_sp_pos",
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
      new HashSet<String>(dataset.keySet());

  public Map<String, String> rawToName = new HashMap<>();

  public PointMetaData() {
    if (rawToName.isEmpty()) {
      for (String dsName : dataset.keySet()) {
        rawToName.put((String) dataset.get(dsName).get("rawPrefix"), dsName);
      }
    }
  }


}
