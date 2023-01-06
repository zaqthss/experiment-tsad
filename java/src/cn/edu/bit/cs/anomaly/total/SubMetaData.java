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
          // rate
          put(
              "uni_subg_rate",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "syn/sub/uni_subg_rate");
                  put("rawPrefix", "uni");
                  put("syn", true);
                  put("dim", 1);
                }
              });
          put(
                  "uni_subs_rate",
                  new HashMap<String, Object>() {
                    {
                      put("dataDir", "syn/sub/uni_subs_rate");
                      put("rawPrefix", "uni");
                      put("syn", true);
                      put("dim", 1);
                    }
                  });
            put(
                    "uni_subt_rate",
                    new HashMap<String, Object>() {
                        {
                            put("dataDir", "syn/sub/uni_subt_rate");
                            put("rawPrefix", "uni");
                            put("syn", true);
                            put("dim", 1);
                        }
                    });
          put(
                  "mul_subg_rate",
                  new HashMap<String, Object>() {
                    {
                      put("dataDir", "syn/sub/mul_subg_rate");
                      put("rawPrefix", "mul");
                      put("syn", true);
                      put("dim", 3);
                    }
                  });
            put(
                    "mul_subs_rate",
                    new HashMap<String, Object>() {
                        {
                            put("dataDir", "syn/sub/mul_subs_rate");
                            put("rawPrefix", "mul");
                            put("syn", true);
                            put("dim", 3);
                        }
                    });
          // size
          put(
                  "uni_subg_size",
                  new HashMap<String, Object>() {
                    {
                      put("dataDir", "syn/sub/uni_subg_size");
                      put("rawPrefix", "uni");
                      put("dim", 1);
                    }
                  });
            put(
                    "uni_subs_size",
                    new HashMap<String, Object>() {
                        {
                            put("dataDir", "syn/sub/uni_subs_size");
                            put("rawPrefix", "uni");
                            put("dim", 1);
                        }
                    });
          put(
                  "mul_subg_size",
                  new HashMap<String, Object>() {
                    {
                      put("dataDir", "syn/sub/mul_subg_size");
                      put("rawPrefix", "mul");
                      put("dim", 3);
                    }
                  });
          put(
                  "mul_subs_size",
                  new HashMap<String, Object>() {
                    {
                      put("dataDir", "syn/sub/mul_subs_size");
                      put("rawPrefix", "mul");
                      put("dim", 3);
                    }
                  });
          put(
                  "sed_size",
                  new HashMap<String, Object>() {
                      {
                          put("dataDir", "sub/sed_size");
                          put("rawPrefix", "sed");
                          put("dim", 1);
                      }
                  });
          put(
                  "swat_size",
                  new HashMap<String, Object>() {
                      {
                          put("dataDir", "sub/swat_size");
                          put("rawPrefix", "swat");
                          put("dim", 1);
                      }
                  });
          //dim
          put(
                  "mul_subg_dim",
                  new HashMap<String, Object>() {
                      {
                          put("dataDir", "syn/sub/mul_subg_dim");
                          put("rawPrefix", "mul");
                          put("dim", 50);
                      }
                  });
          //sub
          put(
              "taxi",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "sub/taxi");
                  put("rawPrefix", "taxi");
                  put("dim", 1);
                }
              });
          put(
              "sed",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "sub/sed");
                  put("rawPrefix", "sed");
                  put("dim", 1);
                }
              });
          put(
              "power",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "sub/power");
                  put("rawPrefix", "power");
                  put("dim", 1);
                }
              });
          put(
                  "machine",
                  new HashMap<String, Object>() {
                    {
                      put("dataDir", "sub/machine");
                      put("rawPrefix", "machine");
                      put("dim", 1);
                    }
                  });
          put(
              "exercise",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "sub/exercise");
                  put("rawPrefix", "exercise");
                  put("dim", 3);
                }
              });
          put(
              "exathlon",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "sub/exathlon");
                  put("rawPrefix", "exathlon");
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
                  put("dataDir", "sub/swat");
                  put("rawPrefix", "swat");
                  put("dim", 51);
                }
              });
          put(
              "smd",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "sub/smd");
                  put("rawPrefix", "smd");
                  put("dim", 38);
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
                      put("dataDir", "syn/sub/mul_cor_subg");
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
                      put("dataDir", "syn/sub/mul_ncor_subg");
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
                  put("dataDir", "syn/sub/uni_subs_sp");
                  put("rawPrefix", "uni_subs_sp");
                  put("dim", 1);
                }
              });
          put(
               "uni_subg_sp",
               new HashMap<String, Object>() {
                 {
                    put("dataDir", "syn/sub/uni_subg_sp");
                    put("rawPrefix", "uni_subg_sp");
                    put("dim", 1);
                 }
               });
          put(
               "uni_subt_sp",
               new HashMap<String, Object>() {
                  {
                     put("dataDir", "syn/sub/uni_subt_sp");
                     put("rawPrefix", "uni_subt_sp");
                     put("dim", 1);
                  }
               });
          put(
              "exathlon_sp",
              new HashMap<String, Object>() {
                {
                  put("dataDir", "sub/exathlon_sp");
                  put("rawPrefix", "exathlon_sp");
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
        		  "uni_subg_rate",
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
                              put("slack", 70);
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
                              put("pattern_length", 45);
                              put("top_k", 10);
                            }
                          });
                      put(
                          "NP",
                          new HashMap<String, Object>() {
                            {
                              put("batchsize", 5000);
                              put("max_sample", 32);
                              put("sub_len", 45);
                              put("top_k", 10);
                              put("scale", "zscore");
                            }
                          });
                      put(
                              "MERLIN",
                              new HashMap<String, Object>() {
                                {
                                  put("minL", 50);
                                  put("maxL", 50);
                                  put("top_k", 10);
                                }
                              });
                      put(
                              "GrammarViz",
                              new HashMap<String, Object>() {
                                {
                                  put("SAX_WINDOW_SIZE", 40);
                                  put("SAX_PAA_SIZE", 10);
                                  put("SAX_ALPHABET_SIZE", 10);
                                  put("SAX_NORM_THRESHOLD", 0.01);
                                  put("DISCORDS_NUM", 10);
                                }
                              });
        			  }
           });
          put(
        		  "uni_subs_rate",
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
                              put("slack", 40);
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
                              put("pattern_length", 55);
                              put("top_k", 10);
                            }
                          });
                      put(
                          "NP",
                          new HashMap<String, Object>() {
                            {
                              put("batchsize", 5000);
                              put("max_sample", 64);
                              put("sub_len", 55);
                              put("top_k", 10);
                              put("scale", "demean");
                            }
                          });
                      put(
                              "MERLIN",
                              new HashMap<String, Object>() {
                                {
                                  put("minL", 55);
                                  put("maxL", 55);
                                  put("top_k", 10);
                                }
                              });
                      put(
                              "GrammarViz",
                              new HashMap<String, Object>() {
                                {
                                  put("SAX_WINDOW_SIZE", 60);
                                  put("SAX_PAA_SIZE", 10);
                                  put("SAX_ALPHABET_SIZE", 4);
                                  put("SAX_NORM_THRESHOLD", 0.01);
                                  put("DISCORDS_NUM", 10);
                                }
                              });
        			  }
           });
            put(
                    "uni_subt_rate",
                    new HashMap<String, Map<String, Object>>() {
                        {
                            put(
                                    "PBAD",
                                    new HashMap<String, Object>() {
                                        {
                                            put("window_size", 20);
                                            put("window_incr", 10);
                                            put("bin_size", 2);
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
                                            put("sub_minlength", 1);
                                        }
                                    });
                            put(
                                    "SAND",
                                    new HashMap<String, Object>() {
                                        {
                                            put("init_length", 1000);
                                            put("batch_size", 1000);
                                            put("pattern_length", 60);
                                            put("top_k", 10);
                                            put("k",6);
                                        }
                                    });
                            put(
                                    "NP",
                                    new HashMap<String, Object>() {
                                        {
                                            put("batchsize", 1000);
                                            put("max_sample", 32);
                                            put("sub_len", 45);
                                            put("top_k", 11);
                                            put("scale", "demean");
                                        }
                                    });
                            put(
                                    "MERLIN",
                                    new HashMap<String, Object>() {
                                        {
                                            put("minL", 55);
                                            put("maxL", 55);
                                            put("top_k", 20);
                                        }
                                    });
                            put(
                                    "GrammarViz",
                                    new HashMap<String, Object>() {
                                        {
                                            put("SAX_WINDOW_SIZE", 50);
                                            put("SAX_PAA_SIZE", 10);
                                            put("SAX_ALPHABET_SIZE", 4);
                                            put("SAX_NORM_THRESHOLD", 0.01);
                                            put("DISCORDS_NUM", 10);
                                        }
                                    });
                        }
                    });
          put(
        		  "mul_subg_rate",
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
                              put("slack", 40);
                              put("sub_minlength", 10);
                            }
                          });            
        			  }
           });
            put(
                    "mul_subs_rate",
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
                                            put("threshold", 0.1);
                                        }
                                    });
                            put(
                                    "LRRDS",
                                    new HashMap<String, Object>() {
                                        {
                                            put("compressed_rate", 0.5);
                                            put("slack", 40);
                                            put("sub_minlength", 10);
                                        }
                                    });
                        }
                    });
          put(
        		  "uni_subg_size",
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
                                      put("slack", 70);
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
                                      put("pattern_length", 45);
                                      put("top_k", 10);
                                    }
                                  });
                              put(
                                  "NP",
                                  new HashMap<String, Object>() {
                                    {
                                      put("batchsize", 5000);
                                      put("max_sample", 32);
                                      put("sub_len", 45);
                                      put("top_k", 10);
                                      put("scale", "zscore");
                                    }
                                  });
                              put(
                                      "MERLIN",
                                      new HashMap<String, Object>() {
                                        {
                                          put("minL", 50);
                                          put("maxL", 50);
                                          put("top_k", 10);
                                        }
                                      });
                              put(
                                      "GrammarViz",
                                      new HashMap<String, Object>() {
                                        {
                                          put("SAX_WINDOW_SIZE", 40);
                                          put("SAX_PAA_SIZE", 10);
                                          put("SAX_ALPHABET_SIZE", 10);
                                          put("SAX_NORM_THRESHOLD", 0.01);
                                          put("DISCORDS_NUM", 10);
                                        }
                                      });
        			  }
           });
            put(
                    "uni_subs_size",
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
                                            put("slack", 40);
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
                                            put("pattern_length", 55);
                                            put("top_k", 10);
                                        }
                                    });
                            put(
                                    "NP",
                                    new HashMap<String, Object>() {
                                        {
                                            put("batchsize", 5000);
                                            put("max_sample", 64);
                                            put("sub_len", 55);
                                            put("top_k", 10);
                                            put("scale", "demean");
                                        }
                                    });
                            put(
                                    "MERLIN",
                                    new HashMap<String, Object>() {
                                        {
                                            put("minL", 55);
                                            put("maxL", 55);
                                            put("top_k", 10);
                                        }
                                    });
                            put(
                                    "GrammarViz",
                                    new HashMap<String, Object>() {
                                        {
                                            put("SAX_WINDOW_SIZE", 60);
                                            put("SAX_PAA_SIZE", 10);
                                            put("SAX_ALPHABET_SIZE", 4);
                                            put("SAX_NORM_THRESHOLD", 0.01);
                                            put("DISCORDS_NUM", 10);
                                        }
                                    });
                        }
                    });
          put(
        		  "mul_subg_size",
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
                            put("slack", 40);
                            put("sub_minlength", 10);
                          }
                        });  
        	}
           });
          put(
                  "mul_subs_size",
                  new HashMap<String, Map<String, Object>>() {
                    {
                      put(
                          "PBAD",
                          new HashMap<String, Object>() {
                            {
                              put("window_size", 20);
                              put("window_incr", 10);
                              put("bin_size", 2);
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
                    }
                  });
          put(
        		  "mul_subg_dim",
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
                              put("threshold", 0.09);
                            }
                          });
                      put(
                          "LRRDS",
                          new HashMap<String, Object>() {
                            {
                              put("compressed_rate", 0.5);
                              put("slack", 200);
                              put("sub_minlength", 10);
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
                               put("window_size", 100);
                               put("window_incr", 10);
                               put("bin_size", 10);
                               put("max_feature", 50);
                               put("threshold", 0.02);
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
                               put("top_k", 21);
                             }
                           });
                       put(
                           "NP",
                           new HashMap<String, Object>() {
                             {
                               put("batchsize", 5000);
                               put("max_sample", 64);
                               put("sub_len", 75);
                               put("scale", "zscore");
                               put("top_k", 21);
                             }
                           });
                       put(
                               "MERLIN",
                               new HashMap<String, Object>() {
                                 {
                                   put("minL", 80);
                                   put("maxL", 80);
                                   put("top_k", 28);
                                 }
                               });
                       put(
                               "GrammarViz",
                               new HashMap<String, Object>() {
                                 {
                                   put("SAX_WINDOW_SIZE", 70);
                                   put("SAX_PAA_SIZE", 4);
                                   put("SAX_ALPHABET_SIZE", 4);
                                   put("SAX_NORM_THRESHOLD", 0.01);
                                   put("DISCORDS_NUM", 21);
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
                              put("compressed_rate", 0.2);
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
                          put("window_size", 130);
                          put("window_incr", 13);
                          put("bin_size", 13);
                          put("max_feature", 50);
                          put("threshold", 0.205);
                          put("max_feature", 50);
                        }
                      });
                  put(
                      "LRRDS",
                      new HashMap<String, Object>() {
                        {
                          put("compressed_rate", 0.2);
                          put("slack", 40);
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
                                    put("pattern_length", 75);
                                    put("top_k", 16);
                                }
                            });
                    put(
                            "NP",
                            new HashMap<String, Object>() {
                                {
                                    put("batchsize", 500);
                                    put("max_sample", 32);
                                    put("sub_len", 150);
                                    put("scale", "zscore");
                                    put("top_k", 8);
                                }
                            });
                    put(
                            "MERLIN",
                            new HashMap<String, Object>() {
                                {
                                    put("minL", 150);
                                    put("maxL", 150);
                                    put("top_k", 8);
                                }
                            });
                    put(
                            "GrammarViz",
                            new HashMap<String, Object>() {
                                {
                                    put("SAX_WINDOW_SIZE", 150);
                                    put("SAX_PAA_SIZE", 10);
                                    put("SAX_ALPHABET_SIZE", 10);
                                    put("SAX_NORM_THRESHOLD", 0.01);
                                    put("DISCORDS_NUM", 8);
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
                          put("window_size", 50);
                          put("window_incr", 5);
                          put("bin_size", 5);
                          put("max_feature", 50);
                          put("threshold", 0.14);
                        }
                      });
                  put(
                      "LRRDS",
                      new HashMap<String, Object>() {
                        {
                          put("compressed_rate", 0.5);
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
                          put("pattern_length", 100);
                          put("top_k", 8);
                        }
                      });
                  put(
                      "NP",
                      new HashMap<String, Object>() {
                        {
                          put("batchsize", 5000);
                          put("max_sample", 32);
                          put("sub_len", 220);
                          put("scale", "zscore");
                          put("top_k", 4);
                        }
                      });
                  put(
                          "MERLIN",
                          new HashMap<String, Object>() {
                            {
                              put("minL", 207);
                              put("maxL", 207);
                              put("top_k", 4);
                            }
                          });
                  put(
                          "GrammarViz",
                          new HashMap<String, Object>() {
                            {
                              put("SAX_WINDOW_SIZE", 100);
                              put("SAX_PAA_SIZE", 4);
                              put("SAX_ALPHABET_SIZE", 10);
                              put("SAX_NORM_THRESHOLD", 0.01);
                              put("DISCORDS_NUM", 4);
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
                          put("window_size", 100);
                          put("window_incr", 10);
                          put("bin_size", 10);
                          put("max_feature", 50);
                          put("threshold", 0.02);
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
                          put("top_k", 21);
                        }
                      });
                  put(
                      "NP",
                      new HashMap<String, Object>() {
                        {
                          put("batchsize", 5000);
                          put("max_sample", 64);
                          put("sub_len", 75);
                          put("scale", "zscore");
                          put("top_k", 21);
                        }
                      });
                  put(
                          "MERLIN",
                          new HashMap<String, Object>() {
                            {
                              put("minL", 80);
                              put("maxL", 80);
                              put("top_k", 28);
                            }
                          });
                  put(
                          "GrammarViz",
                          new HashMap<String, Object>() {
                            {
                              put("SAX_WINDOW_SIZE", 70);
                              put("SAX_PAA_SIZE", 4);
                              put("SAX_ALPHABET_SIZE", 4);
                              put("SAX_NORM_THRESHOLD", 0.01);
                              put("DISCORDS_NUM", 21);
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
                          put("threshold", 0.128);
                        }
                      });
                  put(
                      "LRRDS",
                      new HashMap<String, Object>() {
                        {
                          put("compressed_rate", 0.1);
                          put("slack", 20);
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
                          put("top_k", 45);
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
                  put(
                          "MERLIN",
                          new HashMap<String, Object>() {
                            {
                              put("minL", 800);
                              put("maxL", 800);
                              put("top_k", 3);
                            }
                          });
                  put(
                          "GrammarViz",
                          new HashMap<String, Object>() {
                            {
                              put("SAX_WINDOW_SIZE", 500);
                              put("SAX_PAA_SIZE", 10);
                              put("SAX_ALPHABET_SIZE", 4);
                              put("SAX_NORM_THRESHOLD", 0.01);
                              put("DISCORDS_NUM", 3);
                            }
                          });
                }
              });
          put(
                  "machine",
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
                              put("threshold", 0.08);
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
                              put("pattern_length", 30);
                              put("top_k", 40);
                            }
                          });
                      put(
                          "NP",
                          new HashMap<String, Object>() {
                            {
                              put("batchsize", 5000);
                              put("max_sample", 32);
                              put("sub_len", 567);
                              put("scale", "demean");
                              put("top_k", 4);
                            }
                          });
                      put(
                              "MERLIN",
                              new HashMap<String, Object>() {
                                {
                                  put("minL", 567);
                                  put("maxL", 567);
                                  put("top_k", 6);
                                }
                              });
                      put(
                              "GrammarViz",
                              new HashMap<String, Object>() {
                                {
                                  put("SAX_WINDOW_SIZE", 567);
                                  put("SAX_PAA_SIZE", 10);
                                  put("SAX_ALPHABET_SIZE", 10);
                                  put("SAX_NORM_THRESHOLD", 0.01);
                                  put("DISCORDS_NUM", 2);
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
                          put("window_incr", 5);
                          put("bin_size", 5);
                          put("threshold", 0.147);
                          put("max_feature", 50);
                        }
                      });
                  put(
                      "LRRDS",
                      new HashMap<String, Object>() {
                        {
                          put("compressed_rate", 0.2);
                          put("slack", 30);
                          put("sub_minlength", 2);
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
                          put("top_k", 7);
                        }
                      });
                  put(
                      "NP",
                      new HashMap<String, Object>() {
                        {
                          put("batchsize", 500);
                          put("max_sample", 32);
                          put("sub_len", 60);
                          put("scale", "zscore");
                          put("top_k", 7);
                        }
                      });
                    put(
                            "MERLIN",
                            new HashMap<String, Object>() {
                                {
                                    put("minL", 60);
                                    put("maxL", 60);
                                    put("top_k", 7);
                                }
                            });
                    put(
                            "GrammarViz",
                            new HashMap<String, Object>() {
                                {
                                    put("SAX_WINDOW_SIZE", 60);
                                    put("SAX_PAA_SIZE", 10);
                                    put("SAX_ALPHABET_SIZE", 10);
                                    put("SAX_NORM_THRESHOLD", 0.01);
                                    put("DISCORDS_NUM", 7);
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
              "swat",
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
                          put("threshold", 0.135);
                        }
                      });
                  put(
                      "LRRDS",
                      new HashMap<String, Object>() {
                        {
                          put("compressed_rate", 0.2);
                          put("slack",100);
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
                          put("top_k", 208);
                        }
                      });
                  put(
                      "NP",
                      new HashMap<String, Object>() {
                        {
                          put("batchsize", 10000);
                          put("max_sample", 32);
                          put("sub_len", 380);
                          put("scale", "zscore"); 
                          put("top_k", 26);
                        }
                      });
                    put(
                            "MERLIN",
                            new HashMap<String, Object>() {
                                {
                                    put("minL", 380);
                                    put("maxL", 380);
                                    put("top_k", 26);
                                }
                            });
                    put(
                            "GrammarViz",
                            new HashMap<String, Object>() {
                                {
                                    put("SAX_WINDOW_SIZE", 400);
                                    put("SAX_PAA_SIZE", 10);
                                    put("SAX_ALPHABET_SIZE", 10);
                                    put("SAX_NORM_THRESHOLD", 0.01);
                                    put("DISCORDS_NUM", 26);
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
                          put("window_size", 550);
                          put("window_incr", 55);
                          put("bin_size", 55);
                          put("max_feature", 50);
                          put("threshold", 0.07);
                        }
                      });
                  put(
                      "LRRDS",
                      new HashMap<String, Object>() {
                        {
                          put("compressed_rate", 0.2);
                          put("slack", 70);
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
                          put("top_k", 36);
                        }
                      });
                  put(
                      "NP",
                      new HashMap<String, Object>() {
                        {
                          put("batchsize", 10000);
                          put("max_sample", 32);
                          put("scale", "zscore");
                          put("sub_len", 270);
                          put("top_k", 6);
                        }
                      });
                    put(
                            "MERLIN",
                            new HashMap<String, Object>() {
                                {
                                    put("minL", 270);
                                    put("maxL", 270);
                                    put("top_k", 6);
                                }
                            });
                    put(
                            "GrammarViz",
                            new HashMap<String, Object>() {
                                {
                                    put("SAX_WINDOW_SIZE", 300);
                                    put("SAX_PAA_SIZE", 10);
                                    put("SAX_ALPHABET_SIZE", 10);
                                    put("SAX_NORM_THRESHOLD", 0.01);
                                    put("DISCORDS_NUM", 6);
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
                                      put("window_size", 20);
                                      put("window_incr", 10);
                                      put("bin_size", 2);
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
                                      put("pattern_length", 45);
                                      put("top_k", 12);
                                      put("k",6);
                                  }
                              });
                      put(
                              "NP",
                              new HashMap<String, Object>() {
                                  {
                                      put("batchsize", 5000);
                                      put("max_sample", 64);
                                      put("sub_len", 50);
                                      put("top_k", 12);
                                      put("scale", "demean");
                                  }
                              });
                      put(
                              "MERLIN",
                              new HashMap<String, Object>() {
                                  {
                                      put("minL", 55);
                                      put("maxL", 55);
                                      put("top_k", 12);
                                  }
                              });
                      put(
                              "GrammarViz",
                              new HashMap<String, Object>() {
                                  {
                                      put("SAX_WINDOW_SIZE", 50);
                                      put("SAX_PAA_SIZE", 10);
                                      put("SAX_ALPHABET_SIZE", 4);
                                      put("SAX_NORM_THRESHOLD", 0.01);
                                      put("DISCORDS_NUM", 10);
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
                                          put("window_size", 20);
                                          put("window_incr", 10);
                                          put("bin_size", 2);
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
                                          put("pattern_length", 45);
                                          put("top_k", 12);
                                          put("k",6);
                                      }
                                  });
                          put(
                                  "NP",
                                  new HashMap<String, Object>() {
                                      {
                                          put("batchsize", 5000);
                                          put("max_sample", 64);
                                          put("sub_len", 50);
                                          put("top_k", 12);
                                          put("scale", "demean");
                                      }
                                  });
                          put(
                                  "MERLIN",
                                  new HashMap<String, Object>() {
                                      {
                                          put("minL", 55);
                                          put("maxL", 55);
                                          put("top_k", 12);
                                      }
                                  });
                          put(
                                  "GrammarViz",
                                  new HashMap<String, Object>() {
                                      {
                                          put("SAX_WINDOW_SIZE", 50);
                                          put("SAX_PAA_SIZE", 10);
                                          put("SAX_ALPHABET_SIZE", 4);
                                          put("SAX_NORM_THRESHOLD", 0.01);
                                          put("DISCORDS_NUM", 10);
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
                                put("window_size", 50);
                                put("window_incr", 5);
                                put("bin_size", 5);
                                put("threshold", 0.14);
                                put("max_feature", 50);
                              }
                            });
                        put(
                            "LRRDS",
                            new HashMap<String, Object>() {
                              {
                                put("compressed_rate", 0.2);
                                put("slack", 30);
                                put("sub_minlength", 2);
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
                                put("top_k", 8);
                              }
                            });
                        put(
                            "NP",
                            new HashMap<String, Object>() {
                              {
                                put("batchsize", 500);
                                put("max_sample", 32);
                                put("sub_len", 60);
                                put("scale", "zscore");
                                put("top_k", 8);
                              }
                            });
                          put(
                                  "MERLIN",
                                  new HashMap<String, Object>() {
                                      {
                                          put("minL", 60);
                                          put("maxL", 60);
                                          put("top_k", 8);
                                      }
                                  });
                          put(
                                  "GrammarViz",
                                  new HashMap<String, Object>() {
                                      {
                                          put("SAX_WINDOW_SIZE", 60);
                                          put("SAX_PAA_SIZE", 10);
                                          put("SAX_ALPHABET_SIZE", 10);
                                          put("SAX_NORM_THRESHOLD", 0.01);
                                          put("DISCORDS_NUM", 8);
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
                          put("window_size", 20);
                          put("window_incr", 10);
                          put("bin_size", 2);
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
                          put("pattern_length", 55);
                          put("top_k", 10);
                          put("k",6);
                        }
                      });
                  put(
                      "NP",
                      new HashMap<String, Object>() {
                        {
                          put("batchsize", 5000);
                          put("max_sample", 32);
                          put("sub_len", 55);
                          put("top_k", 10);
                          put("scale", "demean");
                        }
                      });
                  put(
                          "MERLIN",
                          new HashMap<String, Object>() {
                              {
                                  put("minL", 60);
                                  put("maxL", 60);
                                  put("top_k", 10);
                              }
                          });
                  put(
                          "GrammarViz",
                          new HashMap<String, Object>() {
                              {
                                  put("SAX_WINDOW_SIZE", 40);
                                  put("SAX_PAA_SIZE", 10);
                                  put("SAX_ALPHABET_SIZE", 4);
                                  put("SAX_NORM_THRESHOLD", 0.01);
                                  put("DISCORDS_NUM", 10);
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
                                            put("window_size", 20);
                                            put("window_incr", 10);
                                            put("bin_size", 2);
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
                                            put("pattern_length", 45);
                                            put("top_k", 12);
                                            put("k",6);
                                        }
                                    });
                            put(
                                    "NP",
                                    new HashMap<String, Object>() {
                                        {
                                            put("batchsize", 5000);
                                            put("max_sample", 64);
                                            put("sub_len", 50);
                                            put("top_k", 12);
                                            put("scale", "demean");
                                        }
                                    });
                            put(
                                    "MERLIN",
                                    new HashMap<String, Object>() {
                                        {
                                            put("minL", 55);
                                            put("maxL", 55);
                                            put("top_k", 12);
                                        }
                                    });
                            put(
                                    "GrammarViz",
                                    new HashMap<String, Object>() {
                                        {
                                            put("SAX_WINDOW_SIZE", 50);
                                            put("SAX_PAA_SIZE", 10);
                                            put("SAX_ALPHABET_SIZE", 4);
                                            put("SAX_NORM_THRESHOLD", 0.01);
                                            put("DISCORDS_NUM", 10);
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
                                            put("window_size", 20);
                                            put("window_incr", 10);
                                            put("bin_size", 2);
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
                                            put("sub_minlength", 1);
                                        }
                                    });
                            put(
                                    "SAND",
                                    new HashMap<String, Object>() {
                                        {
                                            put("init_length", 1000);
                                            put("batch_size", 1000);
                                            put("pattern_length", 60);
                                            put("top_k", 10);
                                            put("k",6);
                                        }
                                    });
                            put(
                                    "NP",
                                    new HashMap<String, Object>() {
                                        {
                                            put("batchsize", 1000);
                                            put("max_sample", 32);
                                            put("sub_len", 45);
                                            put("top_k", 11);
                                            put("scale", "demean");
                                        }
                                    });
                            put(
                                    "MERLIN",
                                    new HashMap<String, Object>() {
                                        {
                                            put("minL", 55);
                                            put("maxL", 55);
                                            put("top_k", 20);
                                        }
                                    });
                            put(
                                    "GrammarViz",
                                    new HashMap<String, Object>() {
                                        {
                                            put("SAX_WINDOW_SIZE", 50);
                                            put("SAX_PAA_SIZE", 10);
                                            put("SAX_ALPHABET_SIZE", 4);
                                            put("SAX_NORM_THRESHOLD", 0.01);
                                            put("DISCORDS_NUM", 10);
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
              "dodgers",
              "machine",
              "exercise",
              "exercise_1k",
              "uni_subg_sp",
              "exathlon",
              "swat",
              "swat_5k",
              "swat_50k",
              "smd",
              "smd_5k",
              "daphnet",
              "uni_sub",
              "mul_sub",
              "tao_0.1",
              "uni_subg_sp",
              "uni_subs_sp",
              "uni_subt_sp",
              "mul_subg_dim"
              )
          );


  public Map<String, String> rawToName = new HashMap<>();

  public SubMetaData() {
    if (rawToName.isEmpty()) {
      for (String dsName : dataset.keySet()) {
        rawToName.put((String) dataset.get(dsName).get("rawPrefix"), dsName);
      }
    }
  }


}
