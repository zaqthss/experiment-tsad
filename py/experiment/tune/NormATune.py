from itertools import product

import instanceFactory as fact
import tools.dataHandler as dh
import tools.fileHandler as fh

dsName = "uni_subg_sp"
dsDir = "syn/sub/uni_subg_sp"
algName = "NormA"
metricType = "subsequence"
metricNames = ["precision", "recall","fmeasure"]
middleoutDir="middle"
outDir = "tune"
args = {
    'pattern_length':[45,50,55,60],
    'nm_size':[180,200,220,240],
    'top_k':[1,2,3,4,5]
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
        rseries,_ = inst.run()
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
