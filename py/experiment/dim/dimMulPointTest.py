import os
import sys
import traceback

import numpy as np

import algorithms.algorithm
import instanceFactory as fact
import tools.fileHandler as fh
import metaData as meta
from entity import timeSeriesMul
from tools.metricsHandler import pointMetrics
import time


dsName = "stock_pointg" if len(sys.argv)<2 else sys.argv[1]
fileName = dsName+"_20000_0.1_1" if len(sys.argv)<2 else sys.argv[1]
algNames = ["TranAD", "RCoder", "USAD", "GDN", "OmniAnomaly"]#["TranAD", "OmniAnomaly"]
metricType = "point"
metricNames = ["precision", "recall", "fmeasure"]
outDir = "rate"
hasSufix = False
trainFile = dsName+"_20000_0.1_1" if len(sys.argv)<2 else sys.argv[1]
dims=[1,2,4,8,16,32]
seeds = list(range(1, 6))


algMetrics = {}
totalMetrics = {}

triningSeries = None


def runtest(dsName, dim, seed=None  ):
    global triningSeries
    for algName in algNames:
        inst = fact.getAlgInstance(algName)
        args = meta.algorithmsParameters.get(algName).get(dsName)
        print(args)
        print(os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                         meta.dataSetsParameters.get(dsName).get("tdir"),
                         fileName))
        print(os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                         meta.dataSetsParameters.get(dsName).get("tedir"),
                         fileName))
        series = fh.readMulDataWithLabel(
            os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                         meta.dataSetsParameters.get(dsName).get("tedir"),
                         fileName))
        if inst.__class__.__base__ is algorithms.algorithm.machineLearningAlgorithm and not triningSeries:
            if meta.dataSetsParameters.get(dsName).get("tdir") is None:
                raise IOError("Trining file is needed")
            triningSeries = fh.readMulDataWithLabel(
                os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                             meta.dataSetsParameters.get(dsName).get("tdir"),
                             trainFile))

        if inst:
            if not algMetrics.get(algName):
                tm = {}
                for mn in metricNames:
                    tm[mn] = 0.0
                tm["time"] = 0.0
                tm["algName"] = algName
                algMetrics[algName] = tm
            else:
                tm = algMetrics[algName]
            try:
                start = time.time()
                if inst.__class__.__base__ is algorithms.algorithm.machineLearningAlgorithm:
                    inst.init(args, series.getsubdim(dim).copy(), triningSeries.getsubdim(dim))
                    inst.training()
                rseries = inst.run()
                algMetrics[algName]["time"] = float(algMetrics[algName].get("time")) + time.time() - start
                mtools = fact.getMetricInstance(metricType)
                mtools.init(series, rseries)
                for mn in metricNames:
                    algMetrics[algName][mn] = algMetrics[algName][mn] + getattr(mtools, mn, None)()
            except BaseException as be:
                traceback.print_exc()
                for mn in metricNames:
                    tm[mn] = 0.0
                tm["time"] = 0.0
                algMetrics[algName] = tm



for dim in dims:
    for seed in seeds:
        runtest(dsName, dim, seed)
    for algMetric in algMetrics:
        for metric in metricNames:
            algMetrics[algMetric][metric] = algMetrics[algMetric][metric] / (len(seeds))
    for an in algMetrics:
        for mn in algMetrics[an]:
            if not totalMetrics.get(mn):
                totalMetrics[mn] = {}
            if not totalMetrics[mn].get(dim):
                totalMetrics[mn][dim] = {}
            totalMetrics[mn][dim][an] = algMetrics[an][mn]
            totalMetrics[mn][dim]["dim"] = dim
    algMetrics = {}
for mn in metricNames:
    fh.writeResult(outDir + "/" + "_".join([dsName, mn]), totalMetrics[mn], ["rate"] + algNames)
