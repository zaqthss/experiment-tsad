import os
import time
import traceback

import instanceFactory as fact
import metaData as meta
import tools.dataHandler as dh
import tools.fileHandler as fh
from algorithms.algorithm import MachineLearningAlgorithm

dsNames=["uni_subg_sp"]
algNames = ["TranAD", "USAD", "GDN", "OmniAnomaly"]
metricType = "subsequence"
outDir = "length"
metricNames = ["precision", "recall", "fmeasure"]
lens=["20","30","40","50","60","70","100"]
seeds = ["1","2","3","4","5","6","7","8","9","10"]
algMetrics = {}
totalMetrics = {}
writeMiddleResult = True
middleoutDir='sp'

training_series = None
hastrained = []
instance = {}

def addtodict2(thedict, key_a, key_b, val):
    if key_a in thedict:
        thedict[key_a].update({key_b: val})
    else:
        thedict.update({key_a:{key_b: val}})

for dsName in dsNames:
    for len in lens:
        for seed in seeds:
            print(" begin on seed " + seed);
            for algName in algNames:
                if algName == "NormA":
                    inst = fact.getAlgInstance(algName)
                    series = fh.readUniDataWithLabel(
                        os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                     meta.dataSetsParameters.get(dsName).get("tedir"),
                                     "_".join([meta.dataSetsParameters.get(dsName).get("prefix"),seed])))
                    normaargs = meta.algorithmsParameters.get(algName).get(dsName)
                    normaargs['pattern_length'] = int(len)
                    normaargs['nm_size'] = int(len) * 4
                    if inst:
                        if not algMetrics.get(algName):
                            tm = {}
                            for mn in metricNames:
                                tm[mn] = 0.0
                            tm["time"] = 0.0
                            algMetrics[algName] = tm
                        start = time.time()
                        inst.init(normaargs, series.copy())
                        rseries,_ = inst.run()
                        algMetrics[algName]["time"] = float(algMetrics[algName].get("time")) + time.time() - start
                        if writeMiddleResult:
                            predictAnomaly = dh.getAnomalySequences(rseries)
                            fh.writeMiddleResult(middleoutDir + "/" + '_'.join([algName, dsName, len, 'l', 'sub']),
                                                 predictAnomaly)
                        mtools = fact.getMetricInstance(metricType)
                        mtools.init(series, rseries)
                        for mn in metricNames:
                            algMetrics[algName][mn] = algMetrics[algName][mn] + getattr(mtools, mn, None)()

                elif algName == "BeatGAN":
                    inst = fact.getAlgInstance(algName)
                    if inst:
                        if not algMetrics.get(algName):
                            tm = {}
                            for mn in metricNames:
                                tm[mn] = 0.0
                            tm["time"] = 0.0
                            algMetrics[algName] = tm
                        series = fh.readUniDataWithLabel(
                            os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                         meta.dataSetsParameters.get(dsName).get("tedir"),
                                         meta.dataSetsParameters.get(dsName).get("prefix")))
                        trainingSeries = fh.readMulDataWithLabel(
                            os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                         meta.dataSetsParameters.get(dsName).get("tdir"),
                                         meta.dataSetsParameters.get(dsName).get("prefix")))
                        beatganargs = meta.algorithmsParameters.get(algName).get(dsName)
                        start = time.time()
                        inst.init(beatganargs, series, trainingSeries)
                        rseries = inst.run()
                        algMetrics[algName]["time"] = float(algMetrics[algName].get("time")) + time.time() - start
                        if writeMiddleResult:
                            predictAnomaly = dh.getAnomalySequences(rseries)
                            fh.writeMiddleResult(middleoutDir + "/" + '_'.join([algName, dsName, len, 'l', 'sub']),
                                                 predictAnomaly)
                        mtools = fact.getMetricInstance(metricType)
                        mtools.init(series, rseries)
                        for mn in metricNames:
                            algMetrics[algName][mn] = algMetrics[algName][mn] + getattr(mtools, mn, None)()

                else:
                    if instance.get(algName):
                        inst = instance.get(algName)
                    else:
                        inst = fact.getAlgInstance(algName)
                        instance[algName] = inst
                    args = meta.algorithmsParameters.get(algName).get(dsName)
                    print(args)
                    print(os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                     meta.dataSetsParameters.get(dsName).get("tedir"),
                                     meta.dataSetsParameters.get(dsName).get("prefix")))
                    print(os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                             meta.dataSetsParameters.get(dsName).get("tdir"),
                                             meta.dataSetsParameters.get(dsName).get("prefix")))
                    print(os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                             meta.dataSetsParameters.get(dsName).get("vdir"),
                                             meta.dataSetsParameters.get(dsName).get("prefix")))

                    test_series = fh.readMulDataWithLabel(
                        os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                     meta.dataSetsParameters.get(dsName).get("tedir"),
                                     meta.dataSetsParameters.get(dsName).get("prefix")+"_"+seed))
                    if isinstance(inst, MachineLearningAlgorithm):
                        if not training_series:
                            if meta.dataSetsParameters.get(dsName).get("tdir") is None:
                                raise IOError("Trining file is needed")
                            training_series = fh.readMulDataWithLabel(
                                os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                             meta.dataSetsParameters.get(dsName).get("tdir"),
                                             meta.dataSetsParameters.get(dsName).get("prefix")))

                        valid_series = fh.readMulDataWithLabel(
                            os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                         meta.dataSetsParameters.get(dsName).get("tedir"),
                                         meta.dataSetsParameters.get(dsName).get("prefix")+"_"+seed))
                    else:
                        valid_series = fh.readMulDataWithLabel(
                            os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                         meta.dataSetsParameters.get(dsName).get("tedir"),
                                         meta.dataSetsParameters.get(dsName).get("prefix")+"_"+seed))

                    if inst:
                        if not algMetrics.get(algName):
                            tm = {}
                            for mn in metricNames:
                                tm[mn] = 0.0
                            tm["time"] = 0.0
                            tm["algName"] = algName
                            algMetrics[algName] = tm
                        else:
                            tm = algMetrics[algName]
                        try:
                            start = time.time()
                            if isinstance(inst, MachineLearningAlgorithm):
                                if not (algName in hastrained):
                                    inst.init(args, test_series.copy(), training_series, valid_series)
                                    inst.training(writelossrate=False)
                                    hastrained.append(algName)
                                else:
                                    inst.changeData(test_series.copy(), valid_series)
                            rseries = inst.run()
                            algMetrics[algName]["time"] = float(algMetrics[algName].get("time")) + time.time() - start
                            predictAnomaly = dh.getAnomalyPoint(rseries)
                            fh.writePointMiddleResult(
                                r"sp/" + "_".join([algName, dsName, str(seed)]),
                                predictAnomaly)
                            mtools = fact.getMetricInstance(metricType)
                            mtools.init(test_series, rseries)
                            for mn in metricNames:
                                algMetrics[algName][mn] = algMetrics[algName][mn] + getattr(mtools, mn, None)()
                        except BaseException as be:
                            traceback.print_exc()
                            for mn in metricNames:
                                tm[mn] = 0.0
                            tm["time"] = 0.0
                            algMetrics[algName] = tm

        for algMetric in algMetrics:
            for metric in metricNames:
                algMetrics[algMetric][metric] = algMetrics[algMetric][metric] / (seeds.__len__())
            algMetrics[algMetric]["time"] = algMetrics[algMetric]["time"] * 1000
        totalMetrics[len] = algMetrics
        algMetrics = {}
    fh.writeSubResult(outDir + "/" + '_'.join(['length_sub',dsName]), 'length' ,lens ,algNames ,totalMetrics,metricNames +['time'])
    totalMetrics = {}