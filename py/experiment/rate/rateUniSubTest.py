import os
import time

import instanceFactory as fact
import metaData as meta
import tools.dataHandler as dh
import tools.fileHandler as fh

dsName = "uni_subg_rate"

algNames = ["NormA","BeatGAN"]
metricType = "subsequence"
metricNames = ["precision", "recall", "fmeasure"]
outDir = "rate"
hasSufix = True
type="subg"
dsName = "uni_"+type+"_rate"
sizes = ["5000"]
len="50"
seeds = ["1","2","3","4","5","6","7","8","9","10"]
rates = ["0.05","0.075","0.1","0.125","0.15","0.175","0.2","0.225","0.25"]

algMetrics = {}
totalMetrics = {}

for rate in rates:
    for size in sizes:
        for algName in algNames:
            print(" begin on rate " + rate)
            if algName == "NormA":
                inst = fact.getAlgInstance(algName)
                for seed in seeds:
                    print(" begin on seed " + seed)
                    if hasSufix:
                        series = fh.readUniDataWithLabel(
                            os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                         meta.dataSetsParameters.get(dsName).get("tedir"),
                                         "_".join(
                                             [meta.dataSetsParameters.get(dsName).get("prefix"), type, 'len', len,
                                              size, rate,
                                              seed])).replace("\\", "/"))
                    else:
                        series = fh.readUniDataWithLabel(
                            os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                         meta.dataSetsParameters.get(dsName).get("tedir"),
                                         dsName))
                    normaargs = meta.algorithmsParameters.get(algName).get(dsName)
                    k = int(float(rate) * float(size) / int(len))
                    normaargs['top_k'] = k+1
                    if inst:
                        if not algMetrics.get(algName):
                            tm = {}
                            for mn in metricNames:
                                tm[mn] = 0.0
                            tm["time"] = 0.0
                            algMetrics[algName] = tm
                        start = time.time()
                        inst.init(normaargs, series.copy())
                        rseries,_ = inst.run()
                        predictAnomaly = dh.getAnomalySequences(rseries)
                        fh.writeMiddleResult("rate/" + '_'.join([algName, dsName, rate, seed]), predictAnomaly)
                        algMetrics[algName]["time"] = float(algMetrics[algName].get("time")) + time.time() - start
                        mtools = fact.getMetricInstance(metricType)
                        mtools.init(series, rseries)
                        for mn in metricNames:
                            algMetrics[algName][mn] = algMetrics[algName][mn] + getattr(mtools, mn, None)()
            elif algName == "BeatGAN":
                inst = fact.getAlgInstance(algName)
                trainingSeries = fh.readMulDataWithLabel(
                    os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                 meta.dataSetsParameters.get(dsName).get("tdir"),
                                 "_".join(
                                     [meta.dataSetsParameters.get(dsName).get("prefix"), type, 'len', len,
                                      size])).replace("\\", "/"))
                beatganargs = meta.algorithmsParameters.get(algName).get(dsName)
                start = time.time()
                inst.init(beatganargs, trainingSeries, trainingSeries)
                trained_model = inst.training()
                train_time = time.time() - start
                for seed in seeds:
                    print(" begin on seed " + seed)
                    if hasSufix:
                        series = fh.readMulDataWithLabel(
                            os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                         meta.dataSetsParameters.get(dsName).get("tedir"),
                                         "_".join(
                                             [meta.dataSetsParameters.get(dsName).get("prefix"), type, 'len', len,
                                              size, rate,
                                              seed])).replace("\\", "/"))
                    else:
                        series = fh.readMulDataWithLabel(
                            os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                         meta.dataSetsParameters.get(dsName).get("tedir"),
                                         dsName))
                    if inst:
                        if not algMetrics.get(algName):
                            tm = {}
                            for mn in metricNames:
                                tm[mn] = 0.0
                            tm["time"] = 0.0
                            algMetrics[algName] = tm
                        start = time.time()
                        k = int(float(rate) * float(size) / int(len))
                        rseries,_ = inst.predict(trained_model, series, int(k*0.8))
                        predictAnomaly = dh.getAnomalySequences(rseries)
                        fh.writeMiddleResult("rate/" + '_'.join([algName, dsName, rate, seed]), predictAnomaly)
                        algMetrics[algName]["time"] = float(
                            algMetrics[algName].get("time")) + time.time() - start + train_time
                        mtools = fact.getMetricInstance(metricType)
                        mtools.init(series, rseries)
                        for mn in metricNames:
                            algMetrics[algName][mn] = algMetrics[algName][mn] + getattr(mtools, mn, None)()
    for algMetric in algMetrics:
        for metric in metricNames:
            algMetrics[algMetric][metric] = algMetrics[algMetric][metric] / (seeds.__len__())
        algMetrics[algMetric]["time" ] =algMetrics[algMetric]["time" ] *1000
    totalMetrics[rate] = algMetrics
    algMetrics = {}
fh.writeSubResult(outDir + "/" + "_".join(['uni_sub', type]), 'rate', rates, algNames, totalMetrics,
                  metricNames + ['time'])
