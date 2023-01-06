import os

import algorithms.algorithm
import instanceFactory as fact
import tools.fileHandler as fh
import metaData as meta
from tools.metricsHandler import pointMetrics
import time

dsNames = ["ecg", "smtp", "dlr"]
algNames = ["TranAD", "USAD", "GDN", "OmniAnomaly", "RCoder"]
metricType = "point"
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
                    raise IOError("Trining file is needed")
                triningSeries = fh.readMulDataWithLabel(
                    os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                 meta.dataSetsParameters.get(dsName).get("tdir"),
                                 "_".join([dsName, size, rate, seed])))
        else:
            series = fh.readMulDataWithLabel(
                os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                             meta.dataSetsParameters.get(dsName).get("tedir"),
                             dsName))
            if inst.__class__.__base__ is algorithms.algorithm.machineLearningAlgorithm:
                if meta.dataSetsParameters.get(dsName).get("tdir") is None:
                    raise IOError("Trining file is needed")
                triningSeries = fh.readMulDataWithLabel(
                    os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                 meta.dataSetsParameters.get(dsName).get("tdir"),
                                 dsName))
        args = meta.algorithmsParameters.get(algName).get(dsName)

        if inst:
            if not algMetrics.get(algName):
                tm = {}
                for mn in metricNames:
                    tm[mn] = 0.0
                tm["time"] = 0.0
                # tm["algName"] = algName
                algMetrics[algName] = tm
            try:
                start = time.time()
                if inst.__class__.__base__ is algorithms.algorithm.machineLearningAlgorithm:
                    inst.init(args, series.copy(), triningSeries)
                    inst.training()
                else:
                    inst.init(args, series.copy())
                rseries = inst.run()
                algMetrics[algName]["time"] = float(algMetrics[algName].get("time")) + time.time() - start
                mtools = fact.getMetricInstance(metricType)
                mtools.init(series, rseries)
                for mn in metricNames:
                    algMetrics[algName][mn] = algMetrics[algName][mn] + getattr(mtools, mn, None)()
            except BaseException as be:
                print(be)
                for mn in metricNames:
                    tm[mn] = "Error"
                tm["time"] = "Error"
                algMetrics[algName] = tm


for mn in metricNames:
    totalMetrics[mn] = {}
totalMetrics["time"] = {}

if hasSufix:
    for dsName in dsNames:
        for size in sizes:
            for rate in rates:
                for seed in seeds:
                    runtest(dsName, size, rate, seed)
                for algMetric in algMetrics:
                    for metric in metricNames:
                        algMetrics[algMetric][metric] = algMetrics[algMetric][metric] / (len(seeds))
                for an in algMetrics:
                    for mn in algMetrics[an]:
                        if not totalMetrics[mn].get(dsName):
                            totalMetrics[mn][dsName] = {}
                        totalMetrics[mn][dsName][an] = algMetrics[an][mn]
                        totalMetrics[mn][dsName]["dsName"] = dsName
                for mn in totalMetrics:
                    fh.writeResult(outDir + "/" + "_".join([dsName, size, rate]), totalMetrics[mn],
                                   ["dsName"] + algNames)
                algMetrics = {}
                for mn in totalMetrics:
                    totalMetrics[mn] = {}

else:
    for dsName in dsNames:
        runtest(dsName)
        for an in algMetrics:
            for mn in algMetrics[an]:
                if not totalMetrics[mn].get(dsName):
                    totalMetrics[mn][dsName] = {}
                totalMetrics[mn][dsName][an] = algMetrics[an][mn]
                totalMetrics[mn][dsName]["dsName"] = dsName
        algMetrics={}
    for mn in totalMetrics:
        fh.writeResult(outDir + "/" + "_".join(["acc", "mul", mn]), totalMetrics[mn], ["dsName"] + algNames)
