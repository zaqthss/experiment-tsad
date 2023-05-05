import os
import time

import numpy as np

import instanceFactory as fact
import metaData as meta
import tools.fileHandler as fh

algNames = ["NormA","BeatGAN"]
metricType = "subsequence"

dsNames=["power","sed","taxi","machine"]

outDir = "threshold"
metricNames = ["precision", "recall", "fmeasure"]
algMetrics = {}
totalMetrics = {}
aucmetrics={}
totalaucMetircs={}

def creatLinspace(start,end,num):
    diff = (end - start) / (num - 1)
    lin=[]
    lin.append(start)
    for i in range(1,num):
        lin.append(start + i * diff)
    return lin

num=200
vars=creatLinspace(0.0,1.0,num)


def addtodict2(thedict, key_a, key_b, val):
    if key_a in thedict:
        thedict[key_a].update({key_b: val})
    else:
        thedict.update({key_a:{key_b: val}})

for dsName in dsNames:

    for algName in algNames:
        if algName=="NormA":
            inst = fact.getAlgInstance(algName)
            series = fh.readUniDataWithLabel(
                os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                             meta.dataSetsParameters.get(dsName).get("tedir"),
                             meta.dataSetsParameters.get(dsName).get("prefix")))
            normaargs = meta.algorithmsParameters.get(algName).get(dsName)
            if inst:
                start = time.time()
                inst.init(normaargs, series.copy())
                rseries, _ = inst.run()
                vm = {}
                for var in vars:
                    vm[var] = 0.0
                    algMetrics[algName] = vm
                    k = (int)((var * (int)(len(series))) / normaargs['pattern_length'])
                    print(k)
                    rseries = inst.evaluate(k)
                    mtools = fact.getMetricInstance(metricType)
                    mtools.init(series, rseries)
                    tm = {}
                    for mn in metricNames:
                        tm[mn] = 0.0
                        algMetrics[algName][var] = tm
                        algMetrics[algName][var][mn] = algMetrics[algName][var][mn] + getattr(mtools, mn, None)()
                algMetrics[algName][0.0]['precision'] = 1.0
                algMetrics[algName][0.0]['recall'] = 0.0
                algMetrics[algName][1.0]['precision'] = 0.0
                algMetrics[algName][1.0]['recall'] = 1.0


        elif algName=="BeatGAN":
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
                start = time.time()
                k = beatganargs['top_k']
                rseries, _ = inst.predict(trained_model, series, k)
                vm = {}
                for var in vars:
                    vm[var] = 0.0
                    algMetrics[algName] = vm
                    k = (int)((var * (int)(len(series))) / beatganargs['seq_len'])
                    print(k)
                    rseries = inst.evaluate(k)
                    mtools = fact.getMetricInstance(metricType)
                    mtools.init(series, rseries)
                    tm = {}
                    for mn in metricNames:
                        tm[mn] = 0.0
                        algMetrics[algName][var] = tm
                        algMetrics[algName][var][mn] = algMetrics[algName][var][mn] + getattr(mtools, mn, None)()
                algMetrics[algName][0.0]['precision'] = 1.0
                algMetrics[algName][0.0]['recall'] = 0.0
                algMetrics[algName][1.0]['precision'] = 0.0
                algMetrics[algName][1.0]['recall'] = 1.0

        Recall_list = []
        Precision_list = []
        for var in vars:
            Recall_list.append(algMetrics[algName][var]['recall'])
            Precision_list.append(algMetrics[algName][var]['precision'])
        Recall_list=np.array(Recall_list)
        Precision_list=np.array(Precision_list)
        width_PR = Recall_list[1:] - Recall_list[:-1]
        height_PR = (Precision_list[1:] + Precision_list[:-1]) / 2
        AP_range = np.sum(width_PR * height_PR)
        aucmetrics[algName] = AP_range

    totalMetrics[dsName] = algMetrics
    algMetrics = {}
    totalaucMetircs[dsName]=aucmetrics
    aucmetrics= {}


fh.writeThresholdResult(outDir + "/", 'threshold' ,dsNames,vars ,algNames ,totalMetrics,metricNames)
fh.writeAUCResult(outDir+'/','threshold',dsNames,algNames,totalaucMetircs)
