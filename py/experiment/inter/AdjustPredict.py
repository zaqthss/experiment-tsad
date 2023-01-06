import os
import traceback

import algorithms.algorithm
import instanceFactory as fact
import tools.fileHandler as fh
import metaData as meta
from entity import timeSeriesMul
from tools.metricsHandler import pointMetrics
import time

#dsNames = ["yahoo", "twitter", "dlr", "ecg", "smtp", "exathlon_sp_pos", "uni_subg_sp_pos", "uni_subs_sp_pos","uni_subt_sp_pos"]
dsNames = ["uni_subg_sp_pos"]
#algNames = ["TranADWithPOT", "TranAD", "USAD", "OmniAnomaly", "RCoder", "GDN"]
algNames = ["OmniAnomaly"]
metricType = "subsequence"
metricNames = ["precision", "recall", "fmeasure"]
outDir = "ap"

algMetrics = {}
totalMetrics = {}


def runtest(dsName, ):
    for algName in algNames:
        inst = fact.getAlgInstance(algName)
        series = fh.readMulDataWithLabel(
            os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                         meta.dataSetsParameters.get(dsName).get("tedir"),
                         meta.dataSetsParameters.get(dsName).get("prefix")))
        if inst.__class__.__base__ is algorithms.algorithm.machineLearningAlgorithm:
            if meta.dataSetsParameters.get(dsName).get("tdir") is None:
                raise IOError("Trining file is needed")
            triningSeries = fh.readMulDataWithLabel(
                os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                             meta.dataSetsParameters.get(dsName).get("tdir"),
                             meta.dataSetsParameters.get(dsName).get("prefix")))
        args = meta.algorithmsParameters.get(algName).get(dsName)

        if inst:
            if not algMetrics.get(algName):
                tm = {}
                for mn in metricNames:
                    tm[mn] = 0.0
                tm["time"] = 0.0
                tm["latency"] = 0.0
                # tm["algName"] = algName
                algMetrics[algName] = tm
                algMetrics[algName + "_ap"] = tm.copy()
            try:
                start = time.time()
                if inst.__class__.__base__ is algorithms.algorithm.machineLearningAlgorithm:
                    inst.init(args, series.copy(), triningSeries)
                    inst.training()
                else:
                    inst.init(args, series.copy())
                rseries = inst.run()
                algMetrics[algName]["time"] = time.time() - start
                mtools = fact.getMetricInstance(metricType)
                mtools.init(series, rseries)
                for mn in metricNames:
                    algMetrics[algName][mn] = getattr(mtools, mn, None)()
                rseries, latency = adjustpredict(series, rseries)
                mtools = fact.getMetricInstance(metricType)
                mtools.init(series, rseries)
                algMetrics[algName + "_ap"]["latency"] = latency
                for mn in metricNames:
                    algMetrics[algName + "_ap"][mn] = getattr(mtools, mn, None)()

            except BaseException as be:
                traceback.print_exc()


def adjustpredict(series: timeSeriesMul, rseries: timeSeriesMul):
    actual = series.timeseries
    predict = rseries.timeseries
    anomaly_state = False
    anomaly_count = 0
    latency = 0
    for i in range(len(actual)):
        if actual[i].is_anomaly and predict[i].is_anomaly and not anomaly_state:
            anomaly_state = True
            anomaly_count += 1
            for j in range(i, 0, -1):
                if not actual[j].is_anomaly:
                    break
                else:
                    if not predict[j].is_anomaly:
                        predict[j].is_anomaly = True
                        latency += 1
        elif not actual[i].is_anomaly:
            anomaly_state = False
        if anomaly_state:
            predict[i].is_anomaly = True
    rseries.timeseries = predict
    return rseries, latency / (anomaly_count + 1e-4)


for mn in metricNames:
    totalMetrics[mn] = {}
totalMetrics["time"] = {}
totalMetrics["latency"] = {}

for dsName in dsNames:
    runtest(dsName)
    for an in algMetrics:
        for mn in algMetrics[an]:
            if not totalMetrics[mn].get(dsName):
                totalMetrics[mn][dsName] = {}
            totalMetrics[mn][dsName][an] = algMetrics[an][mn]
            totalMetrics[mn][dsName]["dsName"] = dsName
    algMetrics = {}
for mn in totalMetrics:
    fh.writeResult(outDir + "/" + "_".join(["ap_only_sub", metricType, mn]), totalMetrics[mn],
                   ["dsName"] + algNames + [s + "_ap" for s in algNames])
