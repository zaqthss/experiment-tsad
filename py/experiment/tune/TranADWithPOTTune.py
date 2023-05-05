import datetime
import sys
import traceback
from itertools import product

import torch

import algorithms
import instanceFactory as fact
import metaData
import tools.fileHandler as fh
from algorithms.algorithm import MachineLearningAlgorithm

dsName = "stock_pointg" if len(sys.argv) < 2 else sys.argv[1]
fileName = dsName + "_20000_0.1_1" if len(sys.argv) < 2 else sys.argv[1]
rootDir = metaData.dataSetsParameters[dsName]["dir"]
tedsDir = rootDir + r"\test"
vdsDir = rootDir + r"\valid"
tdsDir = rootDir + r"\train"
algName = "TranADWithPOT"
metricType = "point"
metricNames = ["precision", "recall", "fmeasure"]
outDir = "tune\\"+algName
args = {
    "lr": [0.1] if len(sys.argv) < 3 else [float(sys.argv[2])],
    "batch": [128],
    "n_window": [10],
    "epoch": [5],
    "lm": [(1.2, 3)]
}
writeMiddleResult = True
useValid = True
algMetrics = {}
totalMetrics = {}


def dictProduct(inp):
    return (dict(zip(inp.keys(), values)) for values in product(*inp.values()))


inst = fact.getAlgInstance(algName)
date = datetime.datetime.now().strftime("%Y%m%d")

if isinstance(inst, MachineLearningAlgorithm):
    print(tdsDir + "\\" + (fileName if fileName else dsName))
    tseries = fh.readMulDataWithLabel(tdsDir + "\\" + (fileName if fileName else dsName))
    if useValid:
        print(vdsDir + "\\" + (fileName if fileName else dsName) + "L")
        oseries = fh.readMulDataWithLabel(vdsDir + "\\" + (fileName if fileName else dsName) + "L")
    else:
        print(tedsDir + "\\" + (fileName if fileName else dsName))
        oseries = fh.readMulDataWithLabel(tedsDir + "\\" + (fileName if fileName else dsName))
else:
    if useValid:
        print(vdsDir + "\\" + (fileName if fileName else dsName) + "N")
        oseries = fh.readMulDataWithLabel(vdsDir + "\\" + (fileName if fileName else dsName) + "N")
    else:
        print(tedsDir + "\\" + (fileName if fileName else dsName))
        oseries = fh.readMulDataWithLabel(tedsDir + "\\" + (fileName if fileName else dsName))

if inst:
    for arg in dictProduct(args):
        inst = fact.getAlgInstance(algName)
        arg["dsName"] = dsName
        print(arg)
        algMetrics[algName] = arg.copy()
        for mn in metricNames:
            algMetrics[algName][mn] = 0.0
        try:
            if isinstance(inst, MachineLearningAlgorithm):
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
        fh.writeResult(outDir + "/" + '_'.join([algName, dsName, date]), algMetrics,
                       list(arg.keys()) + metricNames)
