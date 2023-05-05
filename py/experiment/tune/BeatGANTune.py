from itertools import product

import instanceFactory as fact
import tools.dataHandler as dh
import tools.fileHandler as fh

dsName = "uni_subg_sp"
dsDir = "syn/sub/uni_subg_sp"
algName = "BeatGAN"
middleoutDir="middle"
metricType = "subsequence"
metricNames = ["precision", "recall","fmeasure"]
outDir = "tune"
args = {"lr": [0.01],
        "seq_len":[32,64],
        "hidden_size":[100],
        "rep_size":[20,50],
        "batch_size":[64],
        "max_epoch":[10],
        "lambdaa":[0.1,1,0.01],
        "network":['CNN'],
        "top_k":[8,9,10,12,15,20]
        }
algMetrics = {}
totalMetrics = {}
writeMiddleResult = False

def dictProduct(inp):
    return (dict(zip(inp.keys(), values)) for values in product(*inp.values()))

inst = fact.getAlgInstance(algName)
oseries = fh.readMulDataWithLabel(dsDir + "/" +"test/" + dsName+"_1")
tseries = fh.readMulDataWithLabel(dsDir + "/" +"train/" + dsName)
if inst:
    for arg in dictProduct(args):
        algMetrics[algName] = arg.copy()
        for mn in metricNames:
            algMetrics[algName][mn] = 0.0
        inst.init(arg, oseries, tseries)
        rseries = inst.run()
        mtools = fact.getMetricInstance(metricType)
        mtools.init(oseries, rseries)
        predictAnomaly=dh.getAnomalySequences(rseries)
        argList=dh.DicttoList(arg)
        argList='_'.join(argList)
        if writeMiddleResult:
            fh.writeMiddleResult(middleoutDir + "/" + '_'.join([dsName, 'exp',algName,argList,'sub']), predictAnomaly)
        for mn in metricNames:
            algMetrics[algName][mn] = algMetrics[algName][mn] + getattr(mtools, mn, None)()

        fh.writeResult(outDir + "/" + '_'.join([algName, dsName]), algMetrics,
                       list(arg.keys()) + metricNames)
