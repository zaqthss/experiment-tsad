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

dsNames = ["mul_pointg"]
placeholder = "pointg"
types = ["pointc"]

algNames = ["TranAD", "RCoder", "USAD", "GDN", "OmniAnomaly"]  # ["TranAD", "USAD"]
metricType = "point"
metricNames = ["fmeasure"]
outDir = "type"
hasSufix = False
seeds = list(range(0, 10))

algMetrics = {}
totalMetrics = {}

triningSeries = None


def runtest(dsName, seed=None):
    global triningSeries
    for algName in algNames:
        inst = fact.getAlgInstance(algName)
        args = meta.algorithmsParameters.get(algName).get(dsName)
        print(args)
        print(os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                           meta.dataSetsParameters.get(dsName).get("tedir"),
                           fileName + seed))
        print(os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                           meta.dataSetsParameters.get(dsName).get("tdir"),
                           trainFile))
        series = fh.readMulDataWithLabel(
            os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                         meta.dataSetsParameters.get(dsName).get("tedir"),
                         fileName + seed))
        if inst.__class__.__base__ is algorithms.algorithm.machineLearningAlgorithm:
            if meta.dataSetsParameters.get(dsName).get("tdir") is None:
                raise IOError("Trining file is needed")
            triningSeries = fh.readMulDataWithLabel(
                os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                             meta.dataSetsParameters.get(dsName).get("tdir"),
                             trainFile))

        if inst:
            if not algMetrics.get(str(seed)):
                tm = {}
                for mn in metricNames:
                    tm[mn] = {}
                algMetrics[str(seed)] = tm
            else:
                tm = algMetrics[str(seed)]
            try:
                if inst.__class__.__base__ is algorithms.algorithm.machineLearningAlgorithm:
                    inst.init(args, series.copy(), triningSeries)
                    inst.training()
                rseries = inst.run()
                mtools = fact.getMetricInstance(metricType)
                mtools.init(series, rseries)

                for mn in metricNames:
                    algMetrics[str(seed)][mn][algName] = getattr(mtools, mn, None)()
                algMetrics[str(seed)][mn]["seed"] = str(seed)

            except BaseException as be:
                traceback.print_exc()
                for mn in metricNames:
                    tm[mn] = 0.0
                tm["time"] = 0.0
                algMetrics[algName] = tm


for dsName in dsNames:
    fileName = dsName + "_20000_0.1_"
    for type in types:
        fileName = fileName.replace(placeholder, type)
        trainFile = dsName + "_20000_0.1_1"
        for seed in seeds:
            runtest(dsName, str(seed))
        for an in algMetrics:
            for mn in algMetrics[an]:
                if not totalMetrics.get(mn):
                    totalMetrics[mn] = {}
                totalMetrics[mn][an] = algMetrics[an][mn]

        algMetrics = {}
for mn in metricNames:
    fh.writeResult(outDir + "/" + "_".join(["type_seed", dsNames[0], types[0], mn]), totalMetrics[mn],
                   ["seed"] + algNames)
