import os

import algorithms.algorithm
import instanceFactory as fact
import tools.fileHandler as fh
import tools.dataHandler as dh
import metaData as meta
from tools.metricsHandler import pointMetrics
import time

dsNames = ["sed"]
algNames = ["NormA"]
metricType = "subsequence"
metricNames = ["precision", "recall", "fmeasure"]
outDir = "acc"
hasSufix = False
sizes = ["5000"]
seeds = ["1"]
rates = ["0.1"]

algMetrics = {}
totalMetrics = {}


def runtest(dsName, size=None, rate=None, seed=None):
    for algName in algNames:
        inst = fact.getAlgInstance(algName)
        if hasSufix:
            series = fh.readMulDataWithLabel(
                os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                             meta.dataSetsParameters.get(dsName).get("tedir"),
                             "_".join([dsName, size, rate, seed])))
            if inst.__class__.__base__ is algorithms.algorithm.machineLearningAlgorithm:
                if meta.dataSetsParameters.get(dsName).get("tdir") is None:
                    raise IOError("Training file is needed")
                trainingSeries = fh.readMulDataWithLabel(
                    os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                 meta.dataSetsParameters.get(dsName).get("tdir"),
                                 "_".join([dsName, size, rate, seed])))
        else:
            series = fh.readMulDataWithLabel(
                os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                             meta.dataSetsParameters.get(dsName).get("tedir"),
                             meta.dataSetsParameters.get(dsName).get("prefix")))
            if inst.__class__.__base__ is algorithms.algorithm.machineLearningAlgorithm:
                if meta.dataSetsParameters.get(dsName).get("tdir") is None:
                    raise IOError("Training file is needed")
                trainingSeries = fh.readMulDataWithLabel(
                    os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                 meta.dataSetsParameters.get(dsName).get("tdir"),
                                 meta.dataSetsParameters.get(dsName).get("prefix")))
        args = meta.algorithmsParameters.get(algName).get(dsName)
        if inst:
            if not algMetrics.get(algName):
                tm = {}
                for mn in metricNames:
                    tm[mn] = 0.0
                tm["time"] = 0.0
                tm["algName"] = algName
                algMetrics[algName] = tm
            start = time.time()
            if inst.__class__.__base__ is algorithms.algorithm.machineLearningAlgorithm:
                inst.init(args, series, trainingSeries)
            else:
                inst.init(args, series)
            rseries = inst.run()
            algMetrics[algName]["time"] = (float(algMetrics[algName].get("time")) + time.time() - start)
            algMetrics[algName]["time"] = algMetrics[algName]["time"] * 1000
            mtools = fact.getMetricInstance(metricType)
            mtools.init(series, rseries)
            for mn in metricNames:
                algMetrics[algName][mn] = algMetrics[algName][mn] + getattr(mtools, mn, None)()


for dsName in dsNames:
    runtest(dsName)
    fh.writeResult(outDir + "/" + dsName, algMetrics, ["algName", "time"] + metricNames)
