import os
import time

from algorithms.algorithm import MachineLearningAlgorithm
import instanceFactory as fact
import metaData as meta
import tools.fileHandler as fh

dsNames = ["power","sed","taxi","machine"]
algNames = ["NormA","BeatGAN"]
metricType = "point"
metricNames = ["fpr","fnr"]
outDir = "application"
hasSufix = False

algMetrics = {}
totalMetrics = {}


def runtest(dsName, size=None, rate=None, seed=None):

    for algName in algNames:
        if algName == "NormA":
            inst = fact.getAlgInstance(algName)
            series = fh.readUniDataWithLabel(
                os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                             meta.dataSetsParameters.get(dsName).get("tedir"),
                             meta.dataSetsParameters.get(dsName).get("prefix")))
            normaargs = meta.algorithmsParameters.get(algName).get(dsName)
            if inst:
                if not algMetrics.get(algName):
                    tm = {}
                    for mn in metricNames:
                        tm[mn] = 0.0
                    algMetrics[algName] = tm
                inst.init(normaargs, series.copy())
                rseries, _ = inst.run()
                mtools = fact.getMetricInstance(metricType)
                mtools.init(series, rseries)
                for mn in metricNames:
                    algMetrics[algName][mn] = algMetrics[algName][mn] + getattr(mtools, mn, None)()
        elif algName == "BeatGAN":
            inst = fact.getAlgInstance(algName)
            trainingSeries = fh.readMulDataWithLabel(
                os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                             meta.dataSetsParameters.get(dsName).get("tdir"),
                             meta.dataSetsParameters.get(dsName).get("prefix")))
            beatganargs = meta.algorithmsParameters.get(algName).get(dsName)
            inst.init(beatganargs, trainingSeries, trainingSeries)
            trained_model = inst.training()
            series = fh.readMulDataWithLabel(
                os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                             meta.dataSetsParameters.get(dsName).get("tedir"),
                             meta.dataSetsParameters.get(dsName).get("prefix")))
            if inst:
                if not algMetrics.get(algName):
                    tm = {}
                    for mn in metricNames:
                        tm[mn] = 0.0
                    algMetrics[algName] = tm
                start = time.time()
                beatganaargs = meta.algorithmsParameters.get(algName).get(dsName)
                rseries, _ = inst.predict(trained_model, series, beatganaargs['top_k'])
                mtools = fact.getMetricInstance(metricType)
                mtools.init(series, rseries)
                for mn in metricNames:
                    algMetrics[algName][mn] = algMetrics[algName][mn] + getattr(mtools, mn, None)()


for dsName in dsNames:
    runtest(dsName)
    totalMetrics[dsName] = algMetrics
    algMetrics = {}
fh.writeSubResult(outDir + "/" + 'uni_sub', 'application',dsNames, algNames, totalMetrics,metricNames)
