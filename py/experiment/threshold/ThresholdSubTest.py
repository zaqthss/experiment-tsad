import os
import time

import numpy as np

import instanceFactory as fact
import metaData as meta
import tools.fileHandler as fh

algNames = ["NormA","BeatGAN"]
metricType = "subsequence"
len="50"
size="5000"
dsNames=["uni_subs_sp"]
types=["subs"]
seeds = ["1"]
rate="0.1"
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

for type in types:
    dsName = "uni_" + type + "_rate"
    paramName="uni_" + type + "_sp"
    for algName in algNames:
        if algName=="NormA":
            for seed in seeds:
                print(" begin on seed " + seed);
                inst = fact.getAlgInstance(algName)
                series = fh.readUniDataWithLabel(
                        os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                 meta.dataSetsParameters.get(dsName).get("tedir"),
                                 "_".join(
                                     [meta.dataSetsParameters.get(dsName).get("prefix"), type, 'len', len, size, rate,
                                      seed])).replace("\\", "/"))
                normaargs = meta.algorithmsParameters.get(algName).get(paramName)
                if inst:
                    start = time.time()
                    inst.init(normaargs, series.copy())
                    rseries,_ = inst.run()
                    vm={}
                    for var in vars:
                        vm[var] = 0.0
                        algMetrics[algName] = vm
                        k = (int)((var * (int)(size)) / 50)
                        print(k)
                        rseries=inst.evaluate(k)
                        mtools = fact.getMetricInstance(metricType)
                        mtools.init(series, rseries)
                        tm = {}
                        for mn in metricNames:
                            tm[mn] = 0.0
                            algMetrics[algName][var]=tm
                            algMetrics[algName][var][mn] = algMetrics[algName][var][mn] + getattr(mtools, mn, None)()
                    algMetrics[algName][0.0]['precision']=1.0
                    algMetrics[algName][0.0]['recall'] = 0.0
                    algMetrics[algName][1.0]['precision'] = 0.0
                    algMetrics[algName][1.0]['recall'] = 1.0


        elif algName=="BeatGAN":
            inst = fact.getAlgInstance(algName)
            trainingSeries = fh.readMulDataWithLabel(
                os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                             meta.dataSetsParameters.get(dsName).get("tdir"),
                             "_".join(
                                 [meta.dataSetsParameters.get(dsName).get("prefix"), type, 'len', len,
                                  size])).replace("\\", "/"))
            beatganargs = meta.algorithmsParameters.get(algName).get(paramName)
            k=beatganargs['top_k']
            start = time.time()
            inst.init(beatganargs, trainingSeries, trainingSeries)
            trained_model=inst.training()
            train_time=time.time()-start
            for seed in seeds:
                print(" begin on seed " + seed);
                series = fh.readMulDataWithLabel(
                        os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                 meta.dataSetsParameters.get(dsName).get("tedir"),
                                 "_".join(
                                     [meta.dataSetsParameters.get(dsName).get("prefix"), type, 'len', len, size, rate,
                                      seed])).replace("\\", "/"))
                if inst:
                    start = time.time()
                    rseries,_ = inst.predict(trained_model,series,k)
                    vm = {}
                    for var in vars:
                        vm[var] = 0.0
                        algMetrics[algName] = vm
                        k = (int)((var * (int)(size)) / 50)
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

    for algMetric in algMetrics:
        for var in vars:
            for metric in metricNames:
                algMetrics[algMetric][var][metric] = algMetrics[algMetric][var][metric] / (seeds.__len__())
    totalMetrics[type] = algMetrics
    algMetrics = {}
    totalaucMetircs[type]=aucmetrics
    aucmetrics=[]


fh.writeThresholdResult(outDir + "/", 'threshold' ,types,vars ,algNames ,totalMetrics,metricNames)
fh.writeAUCResult(outDir+'/','threshold',types,algNames,totalaucMetircs)
