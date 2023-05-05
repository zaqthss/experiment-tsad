import os
import sys
import time
import traceback

from algorithms.algorithm import MachineLearningAlgorithm
import instanceFactory as fact
import metaData as meta
import tools.dataHandler as dh
import tools.fileHandler as fh

dsName = "mul_pointg" if len(sys.argv) < 2 else sys.argv[1]
algNames = ["TranAD", "RCoder", "USAD", "GDN", "OmniAnomaly"]
metricType = "point"
metricNames = ["precision", "recall", "fmeasure"]
outDir = "size"
hasSufix = False
trainFileSufix = "20000_0.1_1"
sizes = [2000, 4000, 10000, 20000, 40000, 100000, 200000]
seeds = list(range(0, 10))
rates = [0.1]

algMetrics = {}
totalMetrics = {}

instance = {}
hastrained = []
training_series = None


def runtest(dsName, size=None, rate=None, seed=None, ):
    global training_series
    for algName in algNames:
        if instance.get(algName):
            inst = instance.get(algName)
        else:
            inst = fact.getAlgInstance(algName)
            instance[algName] = inst
        args = meta.algorithmsParameters.get(algName).get(dsName)
        print(args)
        print(os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                           meta.dataSetsParameters.get(dsName).get("tedir"),
                           "_".join(
                               [meta.dataSetsParameters.get(dsName).get("prefix"), str(size), str(rate), str(seed)])))
        print(os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                           meta.dataSetsParameters.get(dsName).get("tdir"),
                           "_".join([meta.dataSetsParameters.get(dsName).get("prefix"), trainFileSufix])))
        print(os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                           meta.dataSetsParameters.get(dsName).get("vdir"),
                           "_".join(
                               [meta.dataSetsParameters.get(dsName).get("prefix"), str(size), str(rate), str(seed)])))

        test_series = fh.readMulDataWithLabel(
            os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                         meta.dataSetsParameters.get(dsName).get("tedir"),
                         "_".join(
                             [meta.dataSetsParameters.get(dsName).get("prefix"), str(size), str(rate), str(seed)])))
        if isinstance(inst, MachineLearningAlgorithm):
            if not training_series:
                if meta.dataSetsParameters.get(dsName).get("tdir") is None:
                    raise IOError("Trining file is needed")
                training_series = fh.readMulDataWithLabel(
                    os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                 meta.dataSetsParameters.get(dsName).get("tdir"),
                                 "_".join([meta.dataSetsParameters.get(dsName).get("prefix"), trainFileSufix])))
            valid_series = fh.readMulDataWithLabel(
                os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                             meta.dataSetsParameters.get(dsName).get("vdir"),
                             "_".join([meta.dataSetsParameters.get(dsName).get("prefix"), str(size), str(rate),
                                       str(seed)]) + "L"))
        else:
            valid_series = fh.readMulDataWithLabel(
                os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                             meta.dataSetsParameters.get(dsName).get("vdir"),
                             "_".join([meta.dataSetsParameters.get(dsName).get("prefix"), str(size), str(rate),
                                       str(seed)]) + "N"))

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
                if isinstance(inst, MachineLearningAlgorithm):
                    if not (algName in hastrained):
                        inst.init(args, test_series.copy(), training_series, valid_series)
                        inst.training(writelossrate=False)
                        hastrained.append(algName)
                    else:
                        inst.changeData(test_series.copy())
                rseries = inst.run()
                algMetrics[algName]["time"] = float(algMetrics[algName].get("time")) + time.time() - start
                predictAnomaly = dh.getAnomalyPoint(rseries)
                fh.writePointMiddleResult("Size\\" + "_".join([algName, dsName, str(size), str(rate), str(seed)]),
                                          predictAnomaly)
                mtools = fact.getMetricInstance(metricType)
                mtools.init(test_series, rseries)
                for mn in metricNames:
                    algMetrics[algName][mn] = algMetrics[algName][mn] + getattr(mtools, mn, None)()
            except BaseException as be:
                traceback.print_exc()
                for mn in metricNames:
                    tm[mn] = 0.0
                tm["time"] = 0.0
                algMetrics[algName] = tm


for size in sizes:
    for rate in rates:
        for seed in seeds:
            runtest(dsName, size, rate, seed)
        for algMetric in algMetrics:
            for metric in metricNames:
                algMetrics[algMetric][metric] = algMetrics[algMetric][metric] / (len(seeds))
        for an in algMetrics:
            for mn in algMetrics[an]:
                if not totalMetrics.get(mn):
                    totalMetrics[mn] = {}
                if not totalMetrics[mn].get(size):
                    totalMetrics[mn][size] = {}
                totalMetrics[mn][size][an] = algMetrics[an][mn]
                totalMetrics[mn][size]["size"] = size
        algMetrics = {}
for mn in metricNames:
    fh.writeResult(outDir + "/" + "_".join([dsName, mn]), totalMetrics[mn], ["size"] + algNames)
