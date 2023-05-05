from itertools import product

import instanceFactory as fact
import tools.fileHandler as fh

dsName = "exercise"
dsDir = "sub"
algName = "UFEKT"
metricType = "subsequence"
metricNames = ["precision", "recall"]
outDir = "tune"
args = {
    'max_rank':[50],
    'min_rank':[10],
    'window_size':[10],
    'sigma':[1.0],
    'knn_k':[5],
    'top_k':[6]
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

        inst.init(arg, series)
        rseries = inst.run()
        mtools = fact.getMetricInstance(metricType)
        mtools.init(series, rseries)
        for mn in metricNames:
            algMetrics[algName][mn] = algMetrics[algName][mn] + getattr(mtools, mn, None)()
        fh.writeResult(outDir + "/" + '_'.join([algName, dsName]), algMetrics,
                       list(arg.keys()) + metricNames)
