import os
import time
import traceback

from algorithms.algorithm import MachineLearningAlgorithm
import instanceFactory as fact
import metaData as meta
import tools.fileHandler as fh

dsNames = ["mul_pointg", "tao_pointg"]
placeholder = "pointg"
types = ["pointg", "pointc"]

algNames = ["TranAD", "USAD", "GDN", "OmniAnomaly"]  # ["TranAD", "OmniAnomaly"]
metricType = "point"
metricNames = ["precision", "recall", "fmeasure"]
outDir = "type"
hasSufix = False
seeds = list(range(0, 10))

algMetrics = {}
totalMetrics = {}

training_series = None


def runtest(dsName, seed=None):
    global training_series
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
        print(os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                           meta.dataSetsParameters.get(dsName).get("vdir"),
                           fileName + seed))

        test_series = fh.readMulDataWithLabel(
            os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                         meta.dataSetsParameters.get(dsName).get("tedir"),
                         fileName + seed))

        if isinstance(inst, MachineLearningAlgorithm):
            if meta.dataSetsParameters.get(dsName).get("tdir") is None:
                raise IOError("Trining file is needed")
            training_series = fh.readMulDataWithLabel(
                os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                             meta.dataSetsParameters.get(dsName).get("tdir"),
                             trainFile))
            valid_series = fh.readMulDataWithLabel(
                os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                             meta.dataSetsParameters.get(dsName).get("vdir"),
                             fileName + seed + "L"))
        else:
            valid_series = fh.readMulDataWithLabel(
                os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                             meta.dataSetsParameters.get(dsName).get("vdir"),
                             fileName + seed + "N"))

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
                    inst.init(args, test_series.copy(), training_series, valid_series)
                    inst.training(writelossrate=False)
                rseries = inst.run()
                algMetrics[algName]["time"] = float(algMetrics[algName].get("time")) + time.time() - start
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


for dsName in dsNames:
    fileName = dsName + "_20000_0.1_"
    for type in types:
        fileName = fileName.replace(placeholder, type)
        trainFile = dsName + "_20000_0.1_1"
        for seed in seeds:
            runtest(dsName, str(seed))
        for algMetric in algMetrics:
            for metric in metricNames:
                algMetrics[algMetric][metric] = algMetrics[algMetric][metric] / (len(seeds))
        for an in algMetrics:
            for mn in algMetrics[an]:
                if not totalMetrics.get(mn):
                    totalMetrics[mn] = {}
                if not totalMetrics[mn].get(fileName):
                    totalMetrics[mn][fileName] = {}
                totalMetrics[mn][fileName][an] = algMetrics[an][mn]
                totalMetrics[mn][fileName]["fileName"] = fileName
        algMetrics = {}
for mn in metricNames:
    fh.writeResult(outDir + "/" + "_".join(["type-mul", mn]), totalMetrics[mn], ["fileName"] + algNames)
