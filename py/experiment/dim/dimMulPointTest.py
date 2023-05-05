import os
import sys
import time
import traceback

from algorithms.algorithm import MachineLearningAlgorithm
import instanceFactory as fact
import metaData as meta
import tools.fileHandler as fh

dsName = "stock_pointg" if len(sys.argv) < 2 else sys.argv[1]
fileName = dsName + "_20000_0.1_1" if len(sys.argv) < 2 else sys.argv[1]
algNames = ["TranAD", "RCoder", "USAD", "GDN", "OmniAnomaly"]  # ["RCoder"]  # ["TranAD", "OmniAnomaly"]
metricType = "point"
metricNames = ["precision", "recall", "fmeasure"]
outDir = "rate"
hasSufix = False
trainFile = dsName + "_20000_0.1_1" if len(sys.argv) < 2 else sys.argv[1]
dims = [1, 2, 4, 8, 16, 32]
seeds = [0]

algMetrics = {}
totalMetrics = {}

training_series = None


def runtest(dsName, dim, seed=None):
    global training_series
    for algName in algNames:
        inst = fact.getAlgInstance(algName)
        args = meta.algorithmsParameters.get(algName).get(dsName)
        print(args)
        print({"dim": dim})
        print(os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                           meta.dataSetsParameters.get(dsName).get("tdir"),
                           fileName))
        print(os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                           meta.dataSetsParameters.get(dsName).get("tedir"),
                           fileName))
        print(os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                           meta.dataSetsParameters.get(dsName).get("vdir"),
                           fileName))
        test_series = fh.readMulDataWithLabel(
            os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                         meta.dataSetsParameters.get(dsName).get("tedir"),
                         fileName))
        if isinstance(inst, MachineLearningAlgorithm):
            if not training_series:
                if meta.dataSetsParameters.get(dsName).get("tdir") is None:
                    raise IOError("Trining file is needed")
                training_series = fh.readMulDataWithLabel(
                    os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                 meta.dataSetsParameters.get(dsName).get("tdir"),
                                 trainFile))
            valid_series = fh.readMulDataWithLabel(
                os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                             meta.dataSetsParameters.get(dsName).get("vdir"),
                             fileName + "L"))
        else:
            valid_series = fh.readMulDataWithLabel(
                os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                             meta.dataSetsParameters.get(dsName).get("vdir"),
                             fileName + "N"))

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
                    inst.init(args, test_series.getsubdim(dim).copy(), training_series.getsubdim(dim),
                              valid_series.getsubdim(dim))
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
