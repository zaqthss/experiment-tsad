import os

import algorithms.algorithm
import instanceFactory as fact
import tools.fileHandler as fh
import metaData as meta
from tools.metricsHandler import pointMetrics
import time

import time

dsName = "swat_size"

algNames = ["BeatGAN"]
metricType = "subsequence"
metricNames = ["precision", "recall", "fmeasure"]
outDir = "size"
hasSufix = True
type=""
sizes = ["5000","10000","20000","30000","40000","50000"]
len="90"
seeds = ["1"]
rates = ["0.067","0.07","0.051","0.125","0.219","0.177"]

algMetrics = {}
totalMetrics = {}

def addtodict2(thedict, key_a, key_b, val):
    if key_a in thedict:
        thedict[key_a].update({key_b: val})
    else:
        thedict.update({key_a:{key_b: val}})

def runtest(dsName, size=None, rate=None, seed=None):
    for algName in algNames:
        if algName=="BeatGAN":
            inst = fact.getAlgInstance(algName)
            if inst:
                series = fh.readMulDataWithLabel(os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                                              meta.dataSetsParameters.get(dsName).get("tedir"),
                                                              "_".join([meta.dataSetsParameters.get(dsName).get("prefix"),
                                                                   size,rate])).replace('\\','/'))
                trainingSeries = fh.readMulDataWithLabel(
                    os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                 meta.dataSetsParameters.get(dsName).get("tdir"),
                                 meta.dataSetsParameters.get(dsName).get("prefix")))
                beatganargs = meta.algorithmsParameters.get(algName).get(dsName)
                k = int(float(rate) * float(size) / int(len))
                beatganargs['top_k'] = k
                if not algMetrics.get(algName):
                    tm = {}
                    for mn in metricNames:
                        tm[mn] = 0.0
                    tm["time"] = 0.0
                    algMetrics[algName] = tm
                start = time.time()
                inst.init(beatganargs, series, trainingSeries)
                rseries = inst.run()
                algMetrics[algName]["time"] = float(algMetrics[algName].get("time")) + time.time() - start
                mtools = fact.getMetricInstance(metricType)
                mtools.init(series, rseries)
                for mn in metricNames:
                    algMetrics[algName][mn] = algMetrics[algName][mn] + getattr(mtools, mn, None)()



if hasSufix:
    for i, size in enumerate(sizes):
        rate = rates[i]
        for seed in seeds:
            print(" begin on seed " + seed);
            runtest(dsName, size, rate, seed)
        for algMetric in algMetrics:
            for metric in metricNames:
                    algMetrics[algMetric][metric] = algMetrics[algMetric][metric] / (seeds.__len__())
            algMetrics[algMetric]["time"]=algMetrics[algMetric]["time"]*1000

        totalMetrics[size] = algMetrics
        algMetrics = {}
    fh.writeSubResult(outDir + "/" + 'swat', 'size',sizes,algNames,totalMetrics,metricNames+['time'])
else:
    runtest(dsName)
    fh.writeResult(outDir + "/" + dsName, algMetrics, ["algName", "time"] + metricNames)