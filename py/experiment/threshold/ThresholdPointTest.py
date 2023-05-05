import os
import time

import numpy as np

import instanceFactory as fact
import metaData as meta
import tools.fileHandler as fh
from algorithms.algorithm import MachineLearningAlgorithm

algNames = ["RCoder", "TranAD", "USAD", "GDN", "OmniAnomaly"] #
metricType = "point"
size = "20000"
hasSufix = False
dsName = "ecg"
type = "pointg"
seeds = ["1"]
rate = "0.1"
outDir = "threshold"
metricNames = ["precision", "recall", "fmeasure"]
algMetrics = {}
totalMetrics = {}
aucmetrics = {}
totalaucMetircs = {}


def creatLinspace(start, end, num):
    diff = (end - start) / (num - 1)
    lin = []
    lin.append(start)
    for i in range(1, num):
        lin.append(start + i * diff)
    return lin


num = 200
vars = creatLinspace(0.0, 1.0, num)


def addtodict2(thedict, key_a, key_b, val):
    if key_a in thedict:
        thedict[key_a].update({key_b: val})
    else:
        thedict.update({key_a: {key_b: val}})



for algName in algNames:
    for seed in seeds:
        print(" begin on seed " + seed);
        inst = fact.getAlgInstance(algName)
        if hasSufix:
            test_series = fh.readMulDataWithLabel(
                os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                             meta.dataSetsParameters.get(dsName).get("tedir"),
                             "_".join([dsName, size, rate, seed])))
            if isinstance(inst, MachineLearningAlgorithm):
                if meta.dataSetsParameters.get(dsName).get("tdir") is None:
                    raise IOError("Trining file is needed")
                training_series = fh.readMulDataWithLabel(
                    os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                 meta.dataSetsParameters.get(dsName).get("tdir"),
                                 "_".join([dsName, size, rate, seed])))
                valid_series = fh.readMulDataWithLabel(
                    os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                 meta.dataSetsParameters.get(dsName).get("vdir"),
                                 "_".join([dsName, size, rate, seed]) + "L"))
            else:
                valid_series = fh.readMulDataWithLabel(
                    os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                 meta.dataSetsParameters.get(dsName).get("vdir"),
                                 "_".join([dsName, size, rate, seed]) + "N"))
        else:
            test_series = fh.readMulDataWithLabel(
                os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                             meta.dataSetsParameters.get(dsName).get("tedir"),
                             dsName))
            if isinstance(inst, MachineLearningAlgorithm):
                if meta.dataSetsParameters.get(dsName).get("tdir") is None:
                    raise IOError("Trining file is needed")
                training_series = fh.readMulDataWithLabel(
                    os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                 meta.dataSetsParameters.get(dsName).get("tdir"),
                                 dsName))
                valid_series = fh.readMulDataWithLabel(
                    os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                 meta.dataSetsParameters.get(dsName).get("vdir"),
                                 dsName + "L"))
            else:
                valid_series = fh.readMulDataWithLabel(
                    os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                 meta.dataSetsParameters.get(dsName).get("vdir"),
                                 dsName + "N"))
        args = meta.algorithmsParameters.get(algName).get(dsName)
        if inst:
            start = time.time()
            if isinstance(inst, MachineLearningAlgorithm):
                inst.init(args, test_series.copy(), training_series, valid_series)
                inst.training(writelossrate=False)
            else:
                inst.init(args, test_series.copy(), valid_series)
            inst.run()
            vm = {}
            for var in vars:
                vm[var] = 0.0
                algMetrics[algName] = vm
                k = (int)((var * (int)(size)) / 50)
                print(var)
                rseries = inst.getResultWithThreshold(var)
                mtools = fact.getMetricInstance(metricType)
                mtools.init(test_series, rseries)
                tm = {}
                for mn in metricNames:
                    tm[mn] = 0.0
                    algMetrics[algName][var] = tm
                    algMetrics[algName][var][mn] = algMetrics[algName][var][mn] + getattr(mtools, mn, None)()
            algMetrics[algName][0.0]['precision'] = 1.0
            algMetrics[algName][0.0]['recall'] = 0.0
            algMetrics[algName][1.0]['precision'] = 0.0
            algMetrics[algName][1.0]['recall'] = 1.0



    Recall_list = []
    Precision_list = []
    for var in vars:
        Recall_list.append(algMetrics[algName][var]['recall'])
        Precision_list.append(algMetrics[algName][var]['precision'])
    Recall_list = np.array(Recall_list)
    Precision_list = np.array(Precision_list)
    width_PR = Recall_list[1:] - Recall_list[:-1]
    height_PR = (Precision_list[1:] + Precision_list[:-1]) / 2
    AP_range = np.sum(width_PR * height_PR)
    aucmetrics[algName] = AP_range

for algMetric in algMetrics:
    for var in vars:
        for metric in metricNames:
            algMetrics[algMetric][var][metric] = algMetrics[algMetric][var][metric] / (seeds.__len__())
totalMetrics[type] = algMetrics
algMetrics = {}
totalaucMetircs[type] = aucmetrics
aucmetrics = []

fh.writeThresholdResult(outDir + "/", 'threshold', [type], vars, algNames, totalMetrics, metricNames)
fh.writeAUCResult(outDir + '/', 'threshold', [type], algNames, totalaucMetircs)
