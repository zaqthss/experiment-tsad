import os
import instanceFactory as fact
import tools.fileHandler as fh
import metaData as meta
import time

algNames = ["NormA","BeatGAN"]
metricType = "subsequence"
len="50"
size="5000"
types=["subg","subs","subt"]
seeds = ["1","2","3","4","5","6","7","8","9","10"]
rate="0.1"
outDir = "type"
metricNames = ["precision", "recall", "fmeasure"]


for type in types:
    algMetrics = {}
    totalMetrics = {}
    print(" begin on type " + type)
    dsName = "uni_" + type + "_rate"
    paramName = "uni_" + type + "_sp"
    for seed in seeds:
        print(" begin on seed " + seed)

        for algName in algNames:
            if algName == "NormA":
                inst = fact.getAlgInstance(algName)
                series = fh.readUniDataWithLabel(
                    os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                 meta.dataSetsParameters.get(dsName).get("tedir"),
                                 "_".join(
                                     [meta.dataSetsParameters.get(dsName).get("prefix"), type, 'len', len, size,
                                      rate,
                                      seed])).replace("\\", "/"))
                normaargs = meta.algorithmsParameters.get(algName).get(paramName)
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
                    algMetrics[algName]["time"] = float(algMetrics[algName].get("time")) + time.time() - start
                    mtools = fact.getMetricInstance(metricType)
                    mtools.init(series, rseries)
                    for mn in metricNames:
                        algMetrics[algName][mn] = algMetrics[algName][mn] + getattr(mtools, mn, None)()
            elif algName == "BeatGAN":
                inst = fact.getAlgInstance(algName)
                series = fh.readMulDataWithLabel(
                    os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                 meta.dataSetsParameters.get(dsName).get("tedir"),
                                 "_".join(
                                     [meta.dataSetsParameters.get(dsName).get("prefix"), type, 'len', len, size,
                                      rate,
                                      seed])).replace("\\", "/"))
                trainingSeries = fh.readMulDataWithLabel(
                    os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                 meta.dataSetsParameters.get(dsName).get("tdir"),
                                 "_".join(
                                     [meta.dataSetsParameters.get(dsName).get("prefix"), type, 'len', len,
                                      size])).replace("\\", "/"))
                beatganargs = meta.algorithmsParameters.get('BeatGAN').get(paramName)
                k = beatganargs['top_k']
                start = time.time()
                inst.init(beatganargs, trainingSeries, trainingSeries)
                trained_model = inst.training()
                train_time = time.time() - start
                if inst:
                    if not algMetrics.get(algName):
                        tm = {}
                        for mn in metricNames:
                            tm[mn] = 0.0
                        tm["time"] = 0.0
                        algMetrics[algName] = tm
                    start = time.time()
                    rseries,_ = inst.predict(trained_model, series, k)
                    algMetrics[algName]["time"] = float(
                        algMetrics[algName].get("time")) + time.time() - start + train_time
                    mtools = fact.getMetricInstance(metricType)
                    mtools.init(series, rseries)
                    for mn in metricNames:
                        algMetrics[algName][mn] = algMetrics[algName][mn] + getattr(mtools, mn, None)()
        for algMetric in algMetrics:
            algMetrics[algMetric]["time"] = algMetrics[algMetric]["time"] * 1000
        totalMetrics[seed] = algMetrics
        algMetrics={}
    fh.writeSubResult(outDir + "/" + 'uni_'+type, 'type' ,seeds ,algNames ,totalMetrics,metricNames +['time'])