package cn.edu.bit.cs.anomaly.total;

import java.util.*;

public class SubMetaData implements MetaData {

  public static SubMetaData getInstance(){
    return new SubMetaData();
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
    return subSets;
  }

  @Override
  public Map<String, String> getRawToName() {
    return rawToName;
  }

  public  Map<String, Map<String, Object>> dataset =
      new HashMap<String, Map<String, Object>>() {
        {
          // point
          put(
              "YahooA1",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "point");
                  put("rawPrefix", "YahooA1");
                  put("dim", 1);
                }
              });
          // _point_context_10000_0.1_1
          put(
              "stock_10k_point",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "syn");
                  put("rawPrefix", "stock_pointc_10000_0.1_1");
                  put("dim", 1);
                }
              });
          put(
              "uni_point",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "syn/point");
                  put("rawPrefix", "uni");
                  put("dim", 1);
                }
              });
          put(
              "credit",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "point");
                  put("rawPrefix", "credit");
                  put("dim", 29);
                }
              });
          put(
              "credit_5k",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "point");
                  put("rawPrefix", "credit_5k_num_35");
                  put("dim", 29);
                }
              });
          put(
              "ecg",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "point");
                  put("rawPrefix", "ECG");
                  put("dim", 29);
                }
              });
          put(
              "ecg_5k",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "point");
                  put("rawPrefix", "ECG_5k_num_765");
                  put("dim", 29);
                }
              });
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
              "smtp_10k",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "point");
                  put("rawPrefix", "SMTP_10k_num_13");
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
          put(
              "tao_10k",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "syn");
                  put("rawPrefix", "tao_pointc_10000_0.1_1");
                  put("dim", 3);
                }
              });
          // rate
          put(
              "uni_sub",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "syn/sub");
                  put("rawPrefix", "uni");
                  put("dim", 1);
                }
              });
          put(
                  "mul_sub",
                  new HashMap<String, Object>() {
                    {
                      put("dataDir", "syn/sub");
                      put("rawPrefix", "mul");
                      put("dim", 3);
                    }
                  });
          // size
          put(
                  "uni_sub_size",
                  new HashMap<String, Object>() {
                    {
                      put("dataDir", "syn/sub_size");
                      put("rawPrefix", "uni");
                      put("dim", 1);
                    }
                  });
          put(
                  "mul_sub_size",
                  new HashMap<String, Object>() {
                    {
                      put("dataDir", "syn/sub_size");
                      put("rawPrefix", "mul");
                      put("dim", 1);
                    }
                  });
          put(
                  "sed_size",
                  new HashMap<String, Object>() {
                      {
                          put("dataDir", "sub/sed");
                          put("rawPrefix", "SED");
                          put("dim", 1);
                      }
                  });
          put(
                  "swat_size",
                  new HashMap<String, Object>() {
                      {
                          put("dataDir", "sub/swat");
                          put("rawPrefix", "SWaT");
                          put("dim", 1);
                      }
                  });
          //dim
          put(
                  "mul_sub_dim",
                  new HashMap<String, Object>() {
                      {
                          put("dataDir", "syn/sub_dim");
                          put("rawPrefix", "mul");
                          put("dim", 50);
                      }
                  });
          //sub
          put(
              "kpi",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "sub");
                  put("rawPrefix", "KPI");
                  put("dim", 1);
                }
              });
          put(
              "kpi_10k",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "sub");
                  put("rawPrefix", "KPI_10k_sub_38.5_num_2");
                  put("dim", 1);
                }
              });
          put(
              "stock_10k_sub",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "syn");
                  put("rawPrefix", "stock_subg_len_40_num_957_10000_0.1_3");
                  put("dim", 1);
                }
              });
          put(
              "taxi",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "sub");
                  put("rawPrefix", "taxi");
                  put("dim", 1);
                }
              });
          put(
              "sed",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "sub");
                  put("rawPrefix", "SED");
                  put("dim", 1);
                }
              });
          put(
              "sed_10k",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "sub");
                  put("rawPrefix", "SED_10k");
                  put("dim", 1);
                }
              });

          put(
              "power",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "sub");
                  put("rawPrefix", "power");
                  put("dim", 1);
                }
              });
          put(
              "exercise",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "sub");
                  put("rawPrefix", "Exercise");
                  put("dim", 3);
                }
              });
          put(
              "exercise_1k",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "sub");
                  put("rawPrefix", "Exercise_1k");
                  put("dim", 3);
                }
              });
          put(
              "exathlon",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "sub");
                  put("rawPrefix", "Exathlon");
                  put("dim", 19);
                }
              });
          put(
                  "exathlon_dim1",
                  new HashMap<String, Object>() {
                    {
                      put("dataDir", "syn");
                      put("rawPrefix", "exathlon_dim1");
                      put("dim", 1);
                    }
                  });
          put(
              "swat",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "sub");
                  put("rawPrefix", "SWaT");
                  put("dim", 51);
                }
              });
          put(
              "swat_5k",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "sub");
                  put("rawPrefix", "SWat_5k_sub_588.7_num_3");
                  put("dim", 51);
                }
              });
          put(
              "swat_50k",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "sub");
                  put("rawPrefix", "SWaT_50k");
                  put("dim", 51);
                }
              });
          put(
              "smd",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "sub");
                  put("rawPrefix", "SMD");
                  put("dim", 33);
                }
              });
          put(
              "smd_5k",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "sub");
                  put("rawPrefix", "SMD_5k_sub_546.0_num_1");
                  put("dim", 33);
                }
              });
          // 4.3.1
          put(
              "mul_cor_mix",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "syn");
                  put("rawPrefix", "mul_cor_mix_len_50_5000_0.1_1");
                  put("dim", 3);
                }
              });
          put(
                  "mul_cor_subg",
                  new HashMap<String, Object>() {
                    {
                      put("dataDir", "syn");
                      put("rawPrefix", "mul_cor_subg_len_50_5000_0.1_1");
                      put("dim", 3);
                    }
                  });
          put(
                  "mul_ncor_mix",
                  new HashMap<String, Object>() {
                    {
                      put("dataDir", "syn");
                      put("rawPrefix", "mul_ncor_mix_len_50_5000_0.1_1");
                      put("dim", 3);
                    }
                  });
          put(
                  "mul_ncor_subg",
                  new HashMap<String, Object>() {
                    {
                      put("dataDir", "syn");
                      put("rawPrefix", "mul_ncor_subg_len_50_5000_0.1_1");
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
          // 4.3.3
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
          put(
              "http",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "test");
                  put("rawPrefix", "http");
                  put("dim", 3);
                }
              });
        }
      };

  // TODO: fill other parameters
  public  Map<String, Map<String, Map<String, Object>>> dataAlgParam =
      new HashMap<String, Map<String, Map<String, Object>>>() {
        {
          put(
        		  "uni_sub",
        		  new HashMap<String, Map<String, Object>>() {
        			  {
        		  put(
                          "PBAD",
                          new HashMap<String, Object>() {
                            {
                              put("window_size", 20);
                              put("window_incr", 10);
                              put("bin_size", 2);
                              put("max_feature", 50);
                              put("threshold", 0.1);
                            }
                          });
                      put(
                          "LRRDS",
                          new HashMap<String, Object>() {
                            {
                              put("compressed_rate", 0.5);
                              put("slack", 50);
                              put("sub_minlength", 10);
                            }
                          });
                      put(
                          "SAND",
                          new HashMap<String, Object>() {
                            {
                              put("k", 6);
                              put("init_length", 1000);
                              put("batch_size", 1000);
                              put("pattern_length", 60);
                              put("top_k", 10);
                            }
                          });
                      put(
                          "NP",
                          new HashMap<String, Object>() {
                            {
                              put("batchsize", 1000);
                              put("max_sample", 32);
                              put("sub_len", 50);
                              put("top_k", 10);
                              put("scale", "zscore");
                            }
                          });
        			  }
           });
          put(
        		  "mul_sub",
        		  new HashMap<String, Map<String, Object>>() {
        			  {
        		  put(
                          "PBAD",
                          new HashMap<String, Object>() {
                            {
                              put("window_size", 20);
                              put("window_incr", 10);
                              put("bin_size", 2);
                              put("max_feature", 50);
                              put("threshold", 0.1);
                            }
                          });
                      put(
                          "LRRDS",
                          new HashMap<String, Object>() {
                            {
                              put("compressed_rate", 0.5);
                              put("slack", 30);
                              put("sub_minlength", 10);
                            }
                          });
                      put(
                          "SAND",
                          new HashMap<String, Object>() {
                            {
                              put("k", 6);
                              put("init_length", 1000);
                              put("batch_size", 1000);
                              put("pattern_length", 60);
                              put("top_k", 10);
                            }
                          });
                      put(
                          "NP",
                          new HashMap<String, Object>() {
                            {
                              put("batchsize", 1000);
                              put("max_sample", 32);
                              put("sub_len", 50);
                              put("top_k", 10);
                              put("scale", "zscore");
                            }
                          });
        			  }
           });
          put(
        		  "uni_sub_size",
        		  new HashMap<String, Map<String, Object>>() {
        			  {
        		  put(
                          "PBAD",
                          new HashMap<String, Object>() {
                            {
                              put("window_size", 20);
                              put("window_incr", 10);
                              put("bin_size", 2);
                              put("max_feature", 50);
                              put("threshold", 0.1);
                            }
                          });
                      put(
                          "LRRDS",
                          new HashMap<String, Object>() {
                            {
                              put("compressed_rate", 0.5);
                              put("slack", 50);
                              put("sub_minlength", 10);
                            }
                          });
                      put(
                          "SAND",
                          new HashMap<String, Object>() {
                            {
                              put("k", 6);
                              put("init_length", 1000);
                              put("batch_size", 1000);
                              put("pattern_length", 60);
                              put("top_k", 250);
                            }
                          });
                      put(
                          "NP",
                          new HashMap<String, Object>() {
                            {
                              put("batchsize", 1000);
                              put("max_sample", 32);
                              put("sub_len", 50);
                              put("top_k", 250);
                              put("scale", "zscore");
                            }
                          });
        			  }
           });
          put(
        		  "mul_sub_size",
        		  new HashMap<String, Map<String, Object>>() {
        			  {
        		  put(
                          "PBAD",
                          new HashMap<String, Object>() {
                            {
                              put("window_size", 20);
                              put("window_incr", 10);
                              put("bin_size", 2);
                              put("max_feature", 50);
                              put("threshold", 0.1);
                            }
                          });
                      put(
                          "LRRDS",
                          new HashMap<String, Object>() {
                            {
                              put("compressed_rate", 0.5);
                              put("slack", 50);
                              put("sub_minlength", 10);
                            }
                          });
                      put(
                          "SAND",
                          new HashMap<String, Object>() {
                            {
                              put("k", 6);
                              put("init_length", 1000);
                              put("batch_size", 1000);
                              put("pattern_length", 60);
                              put("top_k", 250);
                            }
                          });
                      put(
                          "NP",
                          new HashMap<String, Object>() {
                            {
                              put("batchsize", 1000);
                              put("max_sample", 32);
                              put("sub_len", 50);
                              put("top_k", 250);
                              put("scale", "zscore");
                            }
                          });
        			  }
           });
          put(
        		  "mul_sub_dim",
        		  new HashMap<String, Map<String, Object>>() {
        			  {
        		  put(
                          "PBAD",
                          new HashMap<String, Object>() {
                            {
                              put("window_size", 20);
                              put("window_incr", 10);
                              put("bin_size", 2);
                              put("max_feature", 50);
                              put("threshold", 0.1);
                            }
                          });
                      put(
                          "LRRDS",
                          new HashMap<String, Object>() {
                            {
                              put("compressed_rate", 0.5);
                              put("slack", 50);
                              put("sub_minlength", 10);
                            }
                          });
                      put(
                          "SAND",
                          new HashMap<String, Object>() {
                            {
                              put("k", 6);
                              put("init_length", 1000);
                              put("batch_size", 1000);
                              put("pattern_length", 60);
                              put("top_k", 250);
                            }
                          });
                      put(
                          "NP",
                          new HashMap<String, Object>() {
                            {
                              put("batchsize", 1000);
                              put("max_sample", 32);
                              put("sub_len", 50);
                              put("top_k", 250);
                              put("scale", "zscore");
                            }
                          });
        			  }
           });
          put(
                  "sed_size",
                  new HashMap<String, Map<String, Object>>() {
                      {
                          put(
                                  "PBAD",
                                  new HashMap<String, Object>() {
                                      {
                                          put("window_size", 40);
                                          put("window_incr", 4);
                                          put("bin_size", 4);
                                          put("max_feature", 50);
                                          put("threshold", 0.032);
                                      }
                                  });
                          put(
                                  "LRRDS",
                                  new HashMap<String, Object>() {
                                      {
                                          put("compressed_rate", 0.5);
                                          put("slack", 30);
                                          put("sub_minlength", 10);
                                      }
                                  });
                          put(
                                  "SAND",
                                  new HashMap<String, Object>() {
                                      {
                                          put("k", 6);
                                          put("init_length", 5000);
                                          put("batch_size", 5000);
                                          put("pattern_length", 75);
                                          put("top_k", 49);
                                      }
                                  });
                          put(
                                  "NP",
                                  new HashMap<String, Object>() {
                                      {
                                          put("batchsize", 5000);
                                          put("max_sample", 32);
                                          put("sub_len", 75);
                                          put("scale", "zscore");
                                          put("top_k", 49);
                                      }
                                  });
                      }
                  });
          put(
                  "swat_size",
                  new HashMap<String, Map<String, Object>>() {
                    {
                     put(
                          "PBAD",
                          new HashMap<String, Object>() {
                            {
                              put("window_size", 350);
                              put("window_incr", 35);
                              put("bin_size", 35);
                              put("max_feature", 50);
                              put("threshold", 0.1);
                            }
                          });
                      put(
                          "LRRDS",
                          new HashMap<String, Object>() {
                            {
                              put("compressed_rate", 0.1);
                              put("slack", 50);
                              put("sub_minlength", 10);
                            }
                          });
                    }
                  });
          put(
              "exercise",
              new HashMap<String, Map<String, Object>>() {
                {
                  put(
                      "PBAD",
                      new HashMap<String, Object>() {
                        {
                          put("window_size", 100);
                          put("window_incr", 10);
                          put("bin_size", 10);
                          put("max_feature", 50);
                          put("threshold", 0.1);
                          put("max_feature", 50);
                        }
                      });
                  put(
                      "LRRDS",
                      new HashMap<String, Object>() {
                        {
                          put("compressed_rate", 0.2);
                          put("slack", 30);
                          put("sub_minlength", 10);
                        }
                      });
                  put(
                      "SAND",
                      new HashMap<String, Object>() {
                        {
                          put("k", 6);
                          put("init_length", 2000);
                          put("batch_size", 2000);
                          put("pattern_length", 50);
                          put("top_k", 12);
                        }
                      });
                  put(
                      "NP",
                      new HashMap<String, Object>() {
                        {
                          put("batchsize", 1000);
                          put("max_sample", 32);
                          put("sub_len", 140);
                          put("top_k", 12);
                          put("scale", "zscore");
                        }
                      });
                }
              });

          put(
              "exercise_1k",
              new HashMap<String, Map<String, Object>>() {
                {
                  put(
                      "PBAD",
                      new HashMap<String, Object>() {
                        {
                          put("window_size", 100);
                          put("window_incr", 10);
                          put("max_feature", 50);
                          put("threshold", 0.1);
                        }
                      });
                  put(
                      "LRRDS",
                      new HashMap<String, Object>() {
                        {
                          put("compressed_rate", 0.1);
                          put("slack", 30);
                          put("sub_minlength", 10);
                          put("top_k", 8);
                        }
                      });
                  put(
                      "SAND",
                      new HashMap<String, Object>() {
                        {
                          put("k", 6);
                          put("init_length", 2000);
                          put("batch_size", 2000);
                          put("pattern_length", 50);
                          put("top_k", 8);
                        }
                      });
                  put(
                      "NP",
                      new HashMap<String, Object>() {
                        {
                          put("batchsize", 1000);
                          put("max_sample", 32);
                          put("sub_len", 100);
                          put("top_k", 8);
                        }
                      });
                }
              });

          put(
              "taxi",
              new HashMap<String, Map<String, Object>>() {
                {
                  put(
                      "PBAD",
                      new HashMap<String, Object>() {
                        {
                          put("window_size", 40);
                          put("window_incr", 20);
                          put("bin_size", 4);
                          put("max_feature", 50);
                          put("threshold", 0.0233);
                        }
                      });
                  put(
                      "LRRDS",
                      new HashMap<String, Object>() {
                        {
                          put("compressed_rate", 0.1);
                          put("slack", 100);
                          put("sub_minlength", 10);
                        }
                      });
                  put(
                      "SAND",
                      new HashMap<String, Object>() {
                        {
                          put("k", 6);
                          put("init_length", 2000);
                          put("batch_size", 2000);
                          put("pattern_length", 60);
                          put("top_k", 5);
                        }
                      });
                  put(
                      "NP",
                      new HashMap<String, Object>() {
                        {
                          put("batchsize", 5000);
                          put("max_sample", 64);
                          put("sub_len", 60);
                          put("scale", "zscore");
                          put("top_k", 5);
                        }
                      });
                }
              });

          put(
              "sed",
              new HashMap<String, Map<String, Object>>() {
                {
                  put(
                      "PBAD",
                      new HashMap<String, Object>() {
                        {
                          put("window_size", 40);
                          put("window_incr", 4);
                          put("bin_size", 4);
                          put("max_feature", 50);
                          put("threshold", 0.032);
                        }
                      });
                  put(
                      "LRRDS",
                      new HashMap<String, Object>() {
                        {
                          put("compressed_rate", 0.5);
                          put("slack", 30);
                          put("sub_minlength", 10);
                        }
                      });
                  put(
                      "SAND",
                      new HashMap<String, Object>() {
                        {
                          put("k", 6);
                          put("init_length", 5000);
                          put("batch_size", 5000);
                          put("pattern_length", 75);
                          put("top_k", 49);
                        }
                      });
                  put(
                      "NP",
                      new HashMap<String, Object>() {
                        {
                          put("batchsize", 5000);
                          put("max_sample", 32);
                          put("sub_len", 75);
                          put("scale", "zscore");
                          put("top_k", 49);
                        }
                      });
                }
              });

          put(
              "power",
              new HashMap<String, Map<String, Object>>() {
                {
                  put(
                      "PBAD",
                      new HashMap<String, Object>() {
                        {
                          put("window_size", 700);
                          put("window_incr", 70);
                          put("bin_size", 70);
                          put("max_feature", 50);
                          put("threshold", 0.0856);
                        }
                      });
                  put(
                      "LRRDS",
                      new HashMap<String, Object>() {
                        {
                          put("compressed_rate", 0.1);
                          put("slack", 30);
                          put("sub_minlength", 10);
                        }
                      });
                  put(
                      "SAND",
                      new HashMap<String, Object>() {
                        {
                          put("k", 6);
                          put("init_length", 5000);
                          put("batch_size", 5000);
                          put("pattern_length", 50);
                          put("top_k", 60);
                        }
                      });
                  put(
                      "NP",
                      new HashMap<String, Object>() {
                        {
                          put("batchsize", 5000);
                          put("max_sample", 8);
                          put("sub_len", 750);
                          put("scale", "zscore");
                          put("top_k", 4);
                        }
                      });
                }
              });

          put(
              "exathlon",
              new HashMap<String, Map<String, Object>>() {
                {
                  put(
                      "PBAD",
                      new HashMap<String, Object>() {
                        {
                          put("window_size", 50);
                          put("window_incr", 25);
                          put("bin_size", 5);
                          put("threshold", 0.174);
                          put("max_feature", 50);
                        }
                      });
                  put(
                      "LRRDS",
                      new HashMap<String, Object>() {
                        {
                          put("compressed_rate", 0.5);
                          put("slack", 30);
                          put("sub_minlength", 10);
                        }
                      });
                  put(
                      "SAND",
                      new HashMap<String, Object>() {
                        {
                          put("k", 6);
                          put("init_length", 1000);
                          put("batch_size", 1000);
                          put("pattern_length", 64);
                          put("top_k", 9);
                          put("k", 6);
                        }
                      });
                  put(
                      "NP",
                      new HashMap<String, Object>() {
                        {
                          put("batchsize", 500);
                          put("max_sample", 32);
                          put("sub_len", 64);
                          put("scale", "zscore");
                          put("top_k", 9);
                        }
                      });
                }
              });
          put(
                  "exathlon_dim1",
                  new HashMap<String, Map<String, Object>>() {
                    {
                      put(
                          "PBAD",
                          new HashMap<String, Object>() {
                            {
                              put("window_size", 50);
                              put("window_incr", 25);
                              put("bin_size", 5);
                              put("threshold", 0.174);
                              put("max_feature", 50);
                            }
                          });
                      put(
                          "LRRDS",
                          new HashMap<String, Object>() {
                            {
                              put("compressed_rate", 0.5);
                              put("slack", 30);
                              put("sub_minlength", 10);
                            }
                          });
                      put(
                          "SAND",
                          new HashMap<String, Object>() {
                            {
                              put("k", 6);
                              put("init_length", 1000);
                              put("batch_size", 1000);
                              put("pattern_length", 64);
                              put("top_k", 9);
                              put("k", 6);
                            }
                          });
                      put(
                          "NP",
                          new HashMap<String, Object>() {
                            {
                              put("batchsize", 500);
                              put("max_sample", 32);
                              put("sub_len", 64);
                              put("scale", "zscore");
                              put("top_k", 9);
                            }
                          });
                    }
                  });
          put(
              "kpi",
              new HashMap<String, Map<String, Object>>() {
                {
                  put(
                      "PBAD",
                      new HashMap<String, Object>() {
                        {
                          put("window_size", 100);
                          put("window_incr", 10);
                          put("bin_size", 10);
                          put("threshold", 0.1);
                        }
                      });
                  put(
                      "LRRDS",
                      new HashMap<String, Object>() {
                        {
                          put("compressed_rate", 0.1);
                          put("slack", 30);
                          put("sub_minlength", 10);
                          put("top_k", 200);
                        }
                      });
                  put(
                      "SAND",
                      new HashMap<String, Object>() {
                        {
                          put("init_length", 1000);
                          put("batch_size", 1000);
                          put("pattern_length", 100);
                          put("top_k", 200);
                        }
                      });
                  put(
                      "NP",
                      new HashMap<String, Object>() {
                        {
                          put("batchsize", 1000);
                          put("max_sample", 32);
                          put("sub_len", 100);
                          put("top_k", 200);
                        }
                      });
                }
              });

          put(
              "swat",
              new HashMap<String, Map<String, Object>>() {
                {
                  put(
                      "PBAD",
                      new HashMap<String, Object>() {
                        {
                          put("window_size", 500);
                          put("window_incr", 50);
                          put("bin_size", 50);
                          put("max_feature", 50);
                          put("threshold", 0.14);
                        }
                      });
                  put(
                      "LRRDS",
                      new HashMap<String, Object>() {
                        {
                          put("compressed_rate", 0.1);
                          put("slack", 50);
                          put("sub_minlength", 10);
                        }
                      });
                  put(
                      "SAND",
                      new HashMap<String, Object>() {
                        {
                          put("k", 6);
                          put("init_length", 5000);
                          put("batch_size", 5000);
                          put("pattern_length", 75);
                          put("top_k", 735);
                        }
                      });
                  put(
                      "NP",
                      new HashMap<String, Object>() {
                        {
                          put("batchsize", 10000);
                          put("max_sample", 32);
                          put("sub_len", 1565);
                          put("top_k", 35);
                        }
                      });
                }
              });

          put(
              "swat_50k",
              new HashMap<String, Map<String, Object>>() {
                {
                  put(
                      "PBAD",
                      new HashMap<String, Object>() {
                        {
                          put("window_size", 350);
                          put("window_incr", 35);
                          put("bin_size", 35);
                          put("max_feature", 50);
                          put("threshold", 0.1);
                        }
                      });
                  put(
                      "LRRDS",
                      new HashMap<String, Object>() {
                        {
                          put("compressed_rate", 0.1);
                          put("slack", 50);
                          put("sub_minlength", 10);
                        }
                      });
                  put(
                      "SAND",
                      new HashMap<String, Object>() {
                        {
                          put("k", 6);
                          put("init_length", 5000);
                          put("batch_size", 5000);
                          put("pattern_length", 75);
                          put("top_k", 64);
                        }
                      });
                  put(
                      "NP",
                      new HashMap<String, Object>() {
                        {
                          put("batchsize", 10000);
                          put("max_sample", 32);
                          put("sub_len", 560);
                          put("top_k", 8);
                          put("scale", "zscore");
                        }
                      });
                }
              });

          put(
              "smd",
              new HashMap<String, Map<String, Object>>() {
                {
                  put(
                      "PBAD",
                      new HashMap<String, Object>() {
                        {
                          put("window_size", 400);
                          put("window_incr", 40);
                          put("bin_size", 40);
                          put("max_feature", 50);
                          put("threshold", 0.095);
                        }
                      });
                  put(
                      "LRRDS",
                      new HashMap<String, Object>() {
                        {
                          put("compressed_rate", 0.1);
                          put("slack", 30);
                          put("sub_minlength", 10);
                        }
                      });
                  put(
                      "SAND",
                      new HashMap<String, Object>() {
                        {
                          put("k", 6);
                          put("init_length", 5000);
                          put("batch_size", 5000);
                          put("pattern_length", 75);
                          put("top_k", 40);
                        }
                      });
                  put(
                      "NP",
                      new HashMap<String, Object>() {
                        {
                          put("batchsize", 10000);
                          put("max_sample", 32);
                          put("scale", "zscore");
                          put("sub_len", 340);
                          put("top_k", 8);
                        }
                      });
                }
              });

          // 4.3.1
          put(
              "mul_cor_subg",
              new HashMap<String, Map<String, Object>>() {
                {
                  put(
                      "PBAD",
                      new HashMap<String, Object>() {
                        {
                          put("window_size", 40);
                          put("window_incr", 20);
                          put("bin_size", 4);
                          put("max_feature",50);
                          put("threshold", 0.1);
                        }
                      });
                  put(
                      "LRRDS",
                      new HashMap<String, Object>() {
                        {
                          put("compressed_rate", 0.5);
                          put("slack", 200);
                          put("sub_minlength", 5);
                        }
                      });
                  put(
                      "SAND",
                      new HashMap<String, Object>() {
                        {
                          put("init_length", 1000);
                          put("batch_size", 1000);
                          put("pattern_length", 50);
                          put("k",6);
                          put("top_k", 8);
                        }
                      });
                  put(
                      "NP",
                      new HashMap<String, Object>() {
                        {
                          put("batchsize", 1000);
                          put("max_sample", 32);
                          put("sub_len", 50);
                          put("scale","zscore");
                          put("top_k", 8);
                        }
                      });
                }
              });
          put(
                  "mul_ncor_subg",
                  new HashMap<String, Map<String, Object>>() {
                    {
                      put(
                          "PBAD",
                          new HashMap<String, Object>() {
                            {
                              put("window_size", 40);
                              put("window_incr", 20);
                              put("bin_size", 4);
                              put("max_feature",50);
                              put("threshold", 0.1);
                            }
                          });
                      put(
                          "LRRDS",
                          new HashMap<String, Object>() {
                            {
                              put("compressed_rate", 0.5);
                              put("slack", 200);
                              put("sub_minlength", 5);
                            }
                          });
                      put(
                          "SAND",
                          new HashMap<String, Object>() {
                            {
                              put("init_length", 1000);
                              put("batch_size", 1000);
                              put("pattern_length", 50);
                              put("k",6);
                              put("top_k", 8);
                            }
                          });
                      put(
                          "NP",
                          new HashMap<String, Object>() {
                            {
                              put("batchsize", 1000);
                              put("max_sample", 32);
                              put("sub_len", 50);
                              put("scale","zscore");
                              put("top_k", 8);
                            }
                          });
                    }
                  });
          put(
                  "mul_cor_mix",
                  new HashMap<String, Map<String, Object>>() {
                    {
                      put(
                          "PBAD",
                          new HashMap<String, Object>() {
                            {
                              put("window_size", 40);
                              put("window_incr", 20);
                              put("bin_size", 4);
                              put("max_feature",50);
                              put("threshold", 0.1);
                            }
                          });
                      put(
                          "LRRDS",
                          new HashMap<String, Object>() {
                            {
                              put("compressed_rate", 0.5);
                              put("slack", 200);
                              put("sub_minlength", 5);
                            }
                          });
                      put(
                          "SAND",
                          new HashMap<String, Object>() {
                            {
                              put("init_length", 1000);
                              put("batch_size", 1000);
                              put("pattern_length", 50);
                              put("k",6);
                              put("top_k", 8);
                            }
                          });
                      put(
                          "NP",
                          new HashMap<String, Object>() {
                            {
                              put("batchsize", 1000);
                              put("max_sample", 32);
                              put("sub_len", 50);
                              put("scale","zscore");
                              put("top_k", 8);
                            }
                          });
                    }
                  });
          put(
                  "mul_ncor_mix",
                  new HashMap<String, Map<String, Object>>() {
                    {
                      put(
                          "PBAD",
                          new HashMap<String, Object>() {
                            {
                              put("window_size", 40);
                              put("window_incr", 20);
                              put("bin_size", 4);
                              put("max_feature",50);
                              put("threshold", 0.1);
                            }
                          });
                      put(
                          "LRRDS",
                          new HashMap<String, Object>() {
                            {
                              put("compressed_rate", 0.5);
                              put("slack", 200);
                              put("sub_minlength", 5);
                            }
                          });
                      put(
                          "SAND",
                          new HashMap<String, Object>() {
                            {
                              put("init_length", 1000);
                              put("batch_size", 1000);
                              put("pattern_length", 50);
                              put("k",6);
                              put("top_k", 8);
                            }
                          });
                      put(
                          "NP",
                          new HashMap<String, Object>() {
                            {
                              put("batchsize", 1000);
                              put("max_sample", 32);
                              put("sub_len", 50);
                              put("scale","zscore");
                              put("top_k", 8);
                            }
                          });
                    }
                  });
          // 4.3.2
          put(
              "stock_100k_point",
              new HashMap<String, Map<String, Object>>() {
                {
                  put(
                      "CPOD",
                      new HashMap<String, Object>() {
                        {
                          put("mul", 10);
                          put("sSize", 200);
                          put("R", 2.);
                          put("K", 20);
                        }
                      });
                  put(
                      "NETS",
                      new HashMap<String, Object>() {
                        {
                          put("R", 1.);
                          put("K", 50);
                          put("S", 200);
                          put("W", 2000);
                        }
                      });
                  put(
                      "Stare",
                      new HashMap<String, Object>() {
                        {
                          put("R", 2.);
                          put("K", 20);
                          put("S", 200);
                          put("W", 2000);
                        }
                      });
                  put(
                      "Luminol",
                      new HashMap<String, Object>() {
                        {
                          put("precision", 20);
                          put("chunk_size", 4);
                          put("threshold", 0.5);
                        }
                      });
                }
              });

          // 4.3.3
          put(
              "exathlon_sp",
              new HashMap<String, Map<String, Object>>() {
                {
                	put(
                            "PBAD",
                            new HashMap<String, Object>() {
                              {
                                put("window_size", 40);
                                put("window_incr", 20);
                                put("bin_size", 4);
                                put("threshold", 0.149);
                                put("max_feature", 50);
                              }
                            });
                        put(
                            "SAND",
                            new HashMap<String, Object>() {
                              {
                                put("k", 6);
                                put("init_length", 1000);
                                put("batch_size", 1000);
                                put("pattern_length", 55);
                                put("top_k", 4);
                              }
                            });
                        put(
                            "NP",
                            new HashMap<String, Object>() {
                              {
                                put("batchsize", 1000);
                                put("max_sample", 64);
                                put("sub_len", 55);
                                put("scale", "zscore");
                                put("top_k", 4);
                              }
                            });
                  put(
                      "CPOD",
                      new HashMap<String, Object>() {
                        {
                          put("mul", 10);
                          put("sSize", 50);
                          put("R", 1.);
                          put("K", 5);
                        }
                      });
                  put(
                      "NETS",
                      new HashMap<String, Object>() {
                        {
                          put("R", 1.);
                          put("K", 5);
                          put("S", 50);
                          put("W", 500);
                        }
                      });
                  put(
                      "Stare",
                      new HashMap<String, Object>() {
                        {
                          put("R", 1.);
                          put("K", 5);
                          put("S", 50);
                          put("W", 500);
                        }
                      });
                }
              });

          put(
              "uni_subs_sp",
              new HashMap<String, Map<String, Object>>() {
                {
                  put(
                      "PBAD",
                      new HashMap<String, Object>() {
                        {
                          put("window_size", 40);
                          put("window_incr", 20);
                          put("bin_size", 4);
                          put("threshold", 0.1);
                          put("max_feature", 50);
                        }
                      });
                  put(
                      "LRRDS",
                      new HashMap<String, Object>() {
                        {
                          put("compressed_rate", 0.5);
                          put("slack", 30);
                          put("sub_minlength", 10);
                        }
                      });
                  put(
                      "SAND",
                      new HashMap<String, Object>() {
                        {
                          put("init_length", 1000);
                          put("batch_size", 1000);
                          put("pattern_length", 100);
                          put("top_k", 8);
                          put("k",6);
                        }
                      });
                  put(
                      "NP",
                      new HashMap<String, Object>() {
                        {
                          put("batchsize", 1000);
                          put("max_sample", 32);
                          put("sub_len", 100);
                          put("top_k", 8);
                          put("scale", "zscore");
                        }
                      });
                }
              });
            put(
                    "uni_subg_sp",
                    new HashMap<String, Map<String, Object>>() {
                        {
                            put(
                                    "PBAD",
                                    new HashMap<String, Object>() {
                                        {
                                            put("window_size", 40);
                                            put("window_incr", 20);
                                            put("bin_size", 4);
                                            put("threshold", 0.1);
                                            put("max_feature", 50);
                                        }
                                    });
                            put(
                                    "LRRDS",
                                    new HashMap<String, Object>() {
                                        {
                                            put("compressed_rate", 0.5);
                                            put("slack", 30);
                                            put("sub_minlength", 10);
                                        }
                                    });
                            put(
                                    "SAND",
                                    new HashMap<String, Object>() {
                                        {
                                            put("init_length", 1000);
                                            put("batch_size", 1000);
                                            put("pattern_length", 100);
                                            put("top_k", 8);
                                            put("k",6);
                                        }
                                    });
                            put(
                                    "NP",
                                    new HashMap<String, Object>() {
                                        {
                                            put("batchsize", 1000);
                                            put("max_sample", 32);
                                            put("sub_len", 100);
                                            put("top_k", 8);
                                            put("scale", "zscore");
                                        }
                                    });
                        }
                    });
            put(
                    "uni_subt_sp",
                    new HashMap<String, Map<String, Object>>() {
                        {
                            put(
                                    "PBAD",
                                    new HashMap<String, Object>() {
                                        {
                                            put("window_size", 40);
                                            put("window_incr", 20);
                                            put("bin_size", 4);
                                            put("threshold", 0.1);
                                            put("max_feature", 50);
                                        }
                                    });
                            put(
                                    "LRRDS",
                                    new HashMap<String, Object>() {
                                        {
                                            put("compressed_rate", 0.5);
                                            put("slack", 30);
                                            put("sub_minlength", 10);
                                        }
                                    });
                            put(
                                    "SAND",
                                    new HashMap<String, Object>() {
                                        {
                                            put("init_length", 1000);
                                            put("batch_size", 1000);
                                            put("pattern_length", 100);
                                            put("top_k", 8);
                                            put("k",6);
                                        }
                                    });
                            put(
                                    "NP",
                                    new HashMap<String, Object>() {
                                        {
                                            put("batchsize", 1000);
                                            put("max_sample", 32);
                                            put("sub_len", 100);
                                            put("top_k", 8);
                                            put("scale", "zscore");
                                        }
                                    });
                        }
                    });
        }
      };

  public  Set<String> subSets =
      new HashSet<>(
          Arrays.asList(
              "kpi",
              "kpi_10k",
              "sed",
              "sed_10k",
              "stock_10k_sub",
              "taxi",
              "power",
              "exercise",
              "exercise_1k",
              "uni_subg_sp",
              "exathlon",
              "swat",
              "swat_5k",
              "swat_50k",
              "smd",
              "smd_5k",
              "uni_sub",
              "mul_sub",
              "tao_0.1"));


  public Map<String, String> rawToName = new HashMap<>();

  public SubMetaData() {
    if (rawToName.isEmpty()) {
      for (String dsName : dataset.keySet()) {
        rawToName.put((String) dataset.get(dsName).get("rawPrefix"), dsName);
      }
    }
  }


}
