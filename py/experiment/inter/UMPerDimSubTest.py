import os

import algorithms.algorithm
import instanceFactory as fact
import tools.fileHandler as fh
import tools.dataHandler as dh
import metaData as meta
from tools.metricsHandler import pointMetrics
import time

import time

dsNames=["mul_ncor_subg"]
algNames = ["BeatGAN"]
metricType = "subsequence"
metricNames = ["precision", "recall", "fmeasure"]
outDir="um"
algMetrics = {}
umalgMetrics = {}
mmalgMetrics = {}
totalMetrics = {}
mmtotalMetrics={}
umtotalMetrics={}
for dsName in dsNames:
    umalgMetrics = {}
    mmalgMetrics = {}
    series = fh.readMulDataWithLabel(
        os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                     meta.dataSetsParameters.get(dsName).get("tedir"),
                     meta.dataSetsParameters.get(dsName).get("prefix")))
    trainingSeries = fh.readMulDataWithLabel(
        os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                     meta.dataSetsParameters.get(dsName).get("tdir"),
                     meta.dataSetsParameters.get(dsName).get("prefix")))
    rseries=series.copy()
    for algName in algNames:
        totalMetrics={}
        algMetrics = {}
        if algName == "NormA":
            inst = fact.getAlgInstance(algName)
            tsArray=series.convert()
            normaargs = meta.algorithmsParameters.get(algName).get(dsName)
            dims=[]
            rtsAarray = []
            start = time.time()
            for i,ts in enumerate(tsArray):
                dims.append(str(i))
                inst.init(normaargs, ts)
                r = inst.run()
                rtsAarray.append(r)
                mtools = fact.getMetricInstance(metricType)
                mtools.init(ts, r)
                for mn in metricNames:
                    algMetrics[mn] = getattr(mtools, mn, None)()
                totalMetrics[i]=algMetrics
                algMetrics={}
            umalgMetrics[algName] = dh.evaluateMultoUni(rtsAarray, series, metricNames)
            umalgMetrics[algName]["time"] = time.time() - start
            fh.subDumper(outDir + "/" + '_'.join([algName, dsName,'sub']), 'dim',dims,metricNames,totalMetrics)
        elif algName == "BeatGAN":
            inst = fact.getAlgInstance(algName)
            beatganargs = meta.algorithmsParameters.get(algName).get(dsName)
            # mul to mul
            tm = {}
            for mn in metricNames:
                tm[mn] = 0.0
            tm["time"] = 0.0
            mmalgMetrics[algName] = tm
            start = time.time()
            inst.init(beatganargs, series, trainingSeries)
            rseries = inst.run()
            mmalgMetrics[algName]["time"] = float(mmalgMetrics[algName].get("time")) + time.time() - start
            mtools = fact.getMetricInstance(metricType)
            mtools.init(series, rseries)
            for mn in metricNames:
                mmalgMetrics[algName][mn] = mmalgMetrics[algName][mn] + getattr(mtools, mn, None)()

            #mul to uni
            tsArray = series.converttoMul()
            traintsArray=trainingSeries.converttoMul()
            dims = []
            rtsAarray=[]
            start = time.time()
            for i, ts in enumerate(tsArray):
                dims.append(str(i))
                inst.init(beatganargs, ts,traintsArray[i])
                r = inst.run()
                rtsAarray.append(r)
                mtools = fact.getMetricInstance(metricType)
                mtools.init(ts, r)
                for mn in metricNames:
                    algMetrics[mn] = getattr(mtools, mn, None)()
                totalMetrics[i] = algMetrics
                algMetrics = {}
            umalgMetrics[algName] = dh.evaluateMultoUni(rtsAarray, series, metricNames)
            umalgMetrics[algName]["time"] =time.time() - start
            fh.subDumper(outDir + "/" + '_'.join([algName, dsName, 'sub']), 'dim', dims, metricNames, totalMetrics)

    for mmalgMetric in mmalgMetrics:
        mmalgMetrics[mmalgMetric]["time"] = mmalgMetrics[mmalgMetric]["time"] * 1000
    mmtotalMetrics[dsName] = mmalgMetrics

    for umalgMetric in umalgMetrics:
        umalgMetrics[umalgMetric]["time"] = umalgMetrics[umalgMetric]["time"] * 1000
    umtotalMetrics[dsName] = umalgMetrics

fh.writeSubResult(outDir + "/" + 'dim-sub', 'mm' ,dsNames,algNames,mmtotalMetrics,metricNames +['time'])
fh.writeSubResult(outDir + "/" + 'dim-sub', 'um' ,dsNames,algNames,umtotalMetrics,metricNames +['time'])