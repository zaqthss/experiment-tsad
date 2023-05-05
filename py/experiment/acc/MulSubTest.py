import os
import time

import instanceFactory as fact
import metaData as meta
import tools.dataHandler as dh
import tools.fileHandler as fh

dsNames = ["taxi"]
algNames = ["NormA"]
metricType = "subsequence"
metricNames = ["precision", "recall", "fmeasure"]
outDir = "acc"
hasSufix = False

algMetrics = {}
totalMetrics = {}


def runtest(dsName, size=None, rate=None, seed=None):
    for algName in algNames:
        inst = fact.getAlgInstance(algName)
        if algName == "NormA":
            inst = fact.getAlgInstance(algName)
            series = fh.readMulDataWithLabel(
                os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                             meta.dataSetsParameters.get(dsName).get("tedir"),
                             dsName))
            tsArray = series.convert()
            normaargs = meta.algorithmsParameters.get(algName).get(dsName)
            rtsAarray = []
            start = time.time()
            for i, ts in enumerate(tsArray):
                print('dim:',i+1)
                inst.init(normaargs, ts)
                r = inst.run()
                rtsAarray.append(r)
            algMetrics[algName] = dh.evaluateMultoUni(rtsAarray, series, metricNames)
            algMetrics[algName]["time"] = time.time() - start
            algMetrics[algName]["time"] = algMetrics[algName]["time"] * 1000
            algMetrics[algName]['algName'] = algName
        elif algName=='BeatGAN':
            series = fh.readMulDataWithLabel(
                os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                             meta.dataSetsParameters.get(dsName).get("tedir"),
                             dsName))
            trainingSeries = fh.readMulDataWithLabel(
                    os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                 meta.dataSetsParameters.get(dsName).get("tdir"),
                                 dsName))
            args = meta.algorithmsParameters.get(algName).get(dsName)
            if inst:
                if not algMetrics.get(algName):
                    tm = {}
                    for mn in metricNames:
                        tm[mn] = 0.0
                    tm["time"] = 0.0
                    tm["algName"] = algName
                    algMetrics[algName] = tm
                start = time.time()
                inst.init(args, series, trainingSeries)
                rseries = inst.run()
                algMetrics[algName]["time"] = (float(algMetrics[algName].get("time")) + time.time() - start)
                mtools = fact.getMetricInstance(metricType)
                mtools.init(series, rseries)
                for mn in metricNames:
                    algMetrics[algName][mn] = algMetrics[algName][mn] + getattr(mtools, mn, None)()
                algMetrics[algName]["time"] = algMetrics[algName]["time"] * 1000



for dsName in dsNames:
    runtest(dsName)
    fh.writeResult(outDir + "/" + dsName, algMetrics, ["algName", "time"] + metricNames)
    algMetrics={}
