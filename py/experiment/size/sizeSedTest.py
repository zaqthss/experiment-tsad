import os
import time

import instanceFactory as fact
import metaData as meta
import tools.fileHandler as fh

dsName = "sed_size"

algNames = ["BeatGAN"]
metricType = "subsequence"
metricNames = ["precision", "recall", "fmeasure"]
outDir = "size"
hasSufix = True
type=""
sizes = ["5000","10000", "20000","30000","40000","50000"]
len="50"
seeds = ["1"]
rates = ["0.026","0.019","0.026","0.028","0.027","0.027"]

algMetrics = {}
totalMetrics = {}

def addtodict2(thedict, key_a, key_b, val):
    if key_a in thedict:
        thedict[key_a].update({key_b: val})
    else:
        thedict.update({key_a:{key_b: val}})

def runtest(dsName, size=None, rate=None, seed=None):
    for algName in algNames:
        if algName=="NormA":
            inst = fact.getAlgInstance(algName)
            if hasSufix:
                series = fh.readUniDataWithLabel(
                    os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                 meta.dataSetsParameters.get(dsName).get("tedir"),
                                 "_".join(
                                     [meta.dataSetsParameters.get(dsName).get("prefix"), size, rate])).replace("\\", "/"))
            else:
                series = fh.readUniDataWithLabel(
                    os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                 meta.dataSetsParameters.get(dsName).get("tedir"),
                                 dsName))
            normaargs = meta.algorithmsParameters.get(algName).get(dsName)
            k=int(float(rate)*float(size)/int(len))
            normaargs['top_k']=k
            if inst:
                if not algMetrics.get(algName):
                    tm = {}
                    for mn in metricNames:
                        tm[mn] = 0.0
                    tm["time"] = 0.0
                    algMetrics[algName]=tm
                start = time.time()
                inst.init(normaargs, series.copy())
                rseries = inst.run()
                algMetrics[algName]["time"] = float(algMetrics[algName].get("time")) + time.time() - start
                mtools = fact.getMetricInstance(metricType)
                mtools.init(series, rseries)
                for mn in metricNames:
                    algMetrics[algName][mn] = algMetrics[algName][mn] + getattr(mtools, mn, None)()

        elif algName=="BeatGAN":
            inst = fact.getAlgInstance(algName)
            if hasSufix:
                series = fh.readMulDataWithLabel(
                    os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                 meta.dataSetsParameters.get(dsName).get("tedir"),
                                 "_".join(
                                     [meta.dataSetsParameters.get(dsName).get("prefix"), size, rate])).replace("\\", "/"))
                trainingSeries = fh.readMulDataWithLabel(
                    os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                 meta.dataSetsParameters.get(dsName).get("tdir"),
                                 'sed').replace("\\", "/"))
            else:
                series = fh.readMulDataWithLabel(
                    os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                 meta.dataSetsParameters.get(dsName).get("tedir"),
                                 dsName))
            beatganargs = meta.algorithmsParameters.get(algName).get(dsName)
            k=int(float(rate)*float(size)/int(len))
            beatganargs['top_k']=k
            if inst:
                if not algMetrics.get(algName):
                    tm = {}
                    for mn in metricNames:
                        tm[mn] = 0.0
                    tm["time"] = 0.0
                    algMetrics[algName]=tm
                start = time.time()
                inst.init(beatganargs, series, trainingSeries)
                rseries = inst.run()
                algMetrics[algName]["time"] = float(algMetrics[algName].get("time")) + time.time() - start
                mtools = fact.getMetricInstance(metricType)
                mtools.init(series, rseries)
                for mn in metricNames:
                    algMetrics[algName][mn] = algMetrics[algName][mn] + getattr(mtools, mn, None)()



if hasSufix:
    for i,size in enumerate(sizes):
        rate=rates[i]
        runtest(dsName, size, rate, 1)
        for algMetric in algMetrics:
            for metric in metricNames:
                    algMetrics[algMetric][metric] = algMetrics[algMetric][metric] / (seeds.__len__())
            algMetrics[algMetric]["time"]=algMetrics[algMetric]["time"]*1000

        totalMetrics[size] = algMetrics
        algMetrics = {}
    fh.writeSubResult(outDir + "/" + 'sed', 'size',sizes,algNames,totalMetrics,metricNames+['time'])
else:
    runtest(dsName)
    fh.writeResult(outDir + "/" + dsName, algMetrics, ["algName", "time"] + metricNames)
