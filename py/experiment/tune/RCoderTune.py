import traceback

import instanceFactory as fact
import tools.fileHandler as fh
import algorithms
from itertools import product

dsName = "SMTP_10k_num_13"
dsDir = r"Point\mul\smtp\test"
tdsDir = r"Point\mul\smtp\train"
algName = "RCoder"
metricType = "subsequence"
metricNames = ["precision", "recall", "fmeasure"]
outDir = "tune"
args = {"None": [1],
        }
writeMiddleResult = False
algMetrics = {}
totalMetrics = {}


def dictProduct(inp):
    return (dict(zip(inp.keys(), values)) for values in product(*inp.values()))


inst = fact.getAlgInstance(algName)
oseries = fh.readMulDataWithLabel(dsDir + "/" + dsName)
if inst.__class__.__base__ is algorithms.algorithm.machineLearningAlgorithm:
    tseries = fh.readMulDataWithLabel(tdsDir + "/" + dsName)

if inst:
    for arg in dictProduct(args):
        algMetrics[algName] = arg.copy()
        for mn in metricNames:
            algMetrics[algName][mn] = 0.0
        try:
            if inst.__class__.__base__ is algorithms.algorithm.machineLearningAlgorithm:
                inst.init(arg, oseries.copy(), tseries)
                inst.training()
            else:
                inst.init(arg, oseries.copy())
            rseries = inst.run()
            mtools = fact.getMetricInstance(metricType)
            mtools.init(oseries, rseries)
            if writeMiddleResult:
                fh.writeSeries(outDir + "/" + '_'.join([algName, dsName, "middle"]), rseries)
            for mn in metricNames:
                algMetrics[algName][mn] = algMetrics[algName][mn] + getattr(mtools, mn, None)()
        except BaseException as be:
            traceback.print_exc()
            for mn in metricNames:
                algMetrics[algName][mn] = "Error"
        fh.writeResult(outDir + "/" + '_'.join([algName, dsName]), algMetrics,
                       list(arg.keys()) + metricNames)
