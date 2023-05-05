from itertools import product

import instanceFactory as fact
import tools.fileHandler as fh

dsName = "mul_pointc_10000_0.1_1"
dsDir = "mul"
algName = "example"
metricType = "point"
metricNames = ["precision", "recall", "fmeasure"]
outDir = "tune"
args = {
    "a": [1, 2, 3, 4, 5, 6],
    "b": [2, 3, 4, 5, 6, 7],
    "c": [2, 3, 4, 5, 6, 7]
}
algMetrics = {}
totalMetrics = {}


def dictProduct(inp):
    return (dict(zip(inp.keys(), values)) for values in product(*inp.values()))


series = fh.readMulDataWithLabel(dsDir + "/" + dsName)
inst = fact.getAlgInstance(algName)
if inst:
    for arg in dictProduct(args):
        algMetrics[algName] = arg.copy()
        for mn in metricNames:
            algMetrics[algName][mn] = 0.0
        try:
            inst.init(arg, series)
            rseries = inst.run()
            mtools = fact.getMetricInstance(metricType)
            mtools.init(series, rseries)
            for mn in metricNames:
                algMetrics[algName][mn] = algMetrics[algName][mn] + getattr(mtools, mn, None)()
        except BaseException:
            print("Some exception happened!")
            for mn in metricNames:
                algMetrics[algName][mn] = "Error"
        fh.writeResult(outDir + "/" + '_'.join([algName, dsName]), algMetrics,
                       list(arg.keys()) + metricNames)
