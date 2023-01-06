import instanceFactory as fact
import tools.fileHandler as fh
import tools.dataHandler as dh
from itertools import product

dsName = "uni_subs_sp"
dsDir = "syn/sub/uni_subs_sp"
algName = "NormA"
metricType = "subsequence"
metricNames = ["precision", "recall","fmeasure"]
middleoutDir="middle"
outDir = "tune"
args = {
    'pattern_length':[45],
    'nm_size':[180],
    'top_k':[2,3,4]
}
writeMiddleResult = False
algMetrics = {}
totalMetrics = {}


def dictProduct(inp):
    return (dict(zip(inp.keys(), values)) for values in product(*inp.values()))


series = fh.readUniDataWithLabel(dsDir + "/"+"valN/" + dsName)
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
        predictAnomaly=dh.getAnomalySequences(rseries)
        argList=dh.DicttoList(arg)
        argList='_'.join(argList)
        if writeMiddleResult:
            fh.writeMiddleResult(middleoutDir + "/" + '_'.join([dsName, 'exp',algName,argList,'sub']), predictAnomaly)
        for mn in metricNames:
            algMetrics[algName][mn] = algMetrics[algName][mn] + getattr(mtools, mn, None)()

        fh.writeResult(outDir + "/" + '_'.join([algName, dsName]), algMetrics,
                       list(arg.keys()) + metricNames)
