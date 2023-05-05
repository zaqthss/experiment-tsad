import os
import time

from algorithms.algorithm import MachineLearningAlgorithm
import instanceFactory as fact
import metaData as meta
import tools.fileHandler as fh

dsNames = ["ecg", "smtp", "dlr"]#["dlr"]#
algNames = ["TranAD", "RCoder", "USAD", "GDN", "OmniAnomaly"]#["TranAD"]#
metricType = "point"
metricNames = ["fpr", "fnr"]
outDir = "application"
hasSufix = False

algMetrics = {}
totalMetrics = {}


def runtest(dsName, size=None, rate=None, seed=None):
    for algName in algNames:
        inst = fact.getAlgInstance(algName)
        training_series = fh.readMulDataWithLabel(
            os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                         meta.dataSetsParameters.get(dsName).get("tdir"),
                         dsName))
        test_series = fh.readMulDataWithLabel(
            os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                         meta.dataSetsParameters.get(dsName).get("tedir"),
                         dsName))
        valid_series = fh.readMulDataWithLabel(
            os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                         meta.dataSetsParameters.get(dsName).get("vdir"),
                         dsName + "L"))
        args = meta.algorithmsParameters.get(algName).get(dsName)

        if inst:
            if not algMetrics.get(algName):
                tm = {}
                for mn in metricNames:
                    tm[mn] = 0.0
                algMetrics[algName] = tm
            inst.init(args, test_series.copy(), training_series, valid_series)
            inst.training(writelossrate=False)
            rseries = inst.run()
            mtools = fact.getMetricInstance(metricType)
            mtools.init(test_series, rseries)
            for mn in metricNames:
                algMetrics[algName][mn] = algMetrics[algName][mn] + getattr(mtools, mn, None)()


for dsName in dsNames:
    runtest(dsName)
    totalMetrics[dsName] = algMetrics
    algMetrics = {}
fh.writeSubResult(outDir + "/" + 'uni_point', 'application', dsNames, algNames, totalMetrics, metricNames)
