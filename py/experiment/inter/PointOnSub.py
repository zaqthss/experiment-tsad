import os
import time
import traceback

from algorithms.algorithm import MachineLearningAlgorithm
import instanceFactory as fact
import metaData as meta
import tools.dataHandler as dh
import tools.fileHandler as fh

dsNames = ["exathlon_sp_pos", "uni_subg_sp_pos", "uni_subs_sp_pos", "uni_subt_sp_pos"]
algNames = ["TranAD", "USAD", "GDN", "OmniAnomaly", "RCoder"]
metricType = "subsequence"
metricNames = ["precision", "recall", "fmeasure"]
outDir = "inter"
middleoutDir = "sp"
hasSufix = False
sizes = ["5000"]
seeds = ["1"]
rates = ["0.1"]

algMetrics = {}
totalMetrics = {}


def runtest(dsName, size=None, rate=None, seed=None):
    global training_series
    for algName in algNames:
        inst = fact.getAlgInstance(algName)
        if hasSufix:
            test_series = fh.readMulDataWithLabel(
                os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                             meta.dataSetsParameters.get(dsName).get("tedir"),
                             "_".join([dsName, size, rate, seed])))
            if isinstance(inst, MachineLearningAlgorithm):
                if meta.dataSetsParameters.get(dsName).get("tdir") is None:
                    raise IOError("Trining file is needed")
                training_series = fh.readMulDataWithLabel(
                    os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                 meta.dataSetsParameters.get(dsName).get("tdir"),
                                 "_".join([dsName, size, rate, seed])))
                valid_series = fh.readMulDataWithLabel(
                    os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                 meta.dataSetsParameters.get(dsName).get("vdir"),
                                 "_".join([dsName, size, rate, seed]) + "L"))
            else:
                valid_series = fh.readMulDataWithLabel(
                    os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                 meta.dataSetsParameters.get(dsName).get("vdir"),
                                 "_".join([dsName, size, rate, seed]) + "N"))

        else:
            test_series = fh.readMulDataWithLabel(
                os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                             meta.dataSetsParameters.get(dsName).get("tedir"),
                             dsName))
            if isinstance(inst, MachineLearningAlgorithm):
                if meta.dataSetsParameters.get(dsName).get("tdir") is None:
                    raise IOError("Trining file is needed")
                training_series = fh.readMulDataWithLabel(
                    os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                 meta.dataSetsParameters.get(dsName).get("tdir"),
                                 dsName))
                valid_series = fh.readMulDataWithLabel(
                    os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                 meta.dataSetsParameters.get(dsName).get("vdir"),
                                 dsName + "L"))
            else:
                valid_series = fh.readMulDataWithLabel(
                    os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                 meta.dataSetsParameters.get(dsName).get("vdir"),
                                 dsName + "N"))
        args = meta.algorithmsParameters.get(algName).get(dsName)

        if inst:
            if not algMetrics.get(algName):
                tm = {}
                for mn in metricNames:
                    tm[mn] = 0.0
                tm["time"] = 0.0
                # tm["algName"] = algName
                algMetrics[algName] = tm
            try:
                start = time.time()
                if isinstance(inst, MachineLearningAlgorithm):
                    inst.init(args, test_series.copy(), training_series, valid_series)
                    inst.training(writelossrate=False)
                else:
                    inst.init(args, test_series.copy(), valid_series)
                rseries = inst.run()
                algMetrics[algName]["time"] = float(algMetrics[algName].get("time")) + time.time() - start
                predictAnomaly = dh.getAnomalySequences(rseries)
                fh.writeMiddleResult(middleoutDir + "/" + '_'.join([algName, dsName, 'l', 'sub']), predictAnomaly)
                mtools = fact.getMetricInstance(metricType)
                mtools.init(test_series, rseries)
                for mn in metricNames:
                    algMetrics[algName][mn] = algMetrics[algName][mn] + getattr(mtools, mn, None)()
            except BaseException as be:
                traceback.print_exc()
                for mn in metricNames:
                    tm[mn] = "Error"
                tm["time"] = "Error"
                algMetrics[algName] = tm


for mn in metricNames:
    totalMetrics[mn] = {}
totalMetrics["time"] = {}

if hasSufix:
    for dsName in dsNames:
        for size in sizes:
            for rate in rates:
                for seed in seeds:
                    runtest(dsName, size, rate, seed)
                for algMetric in algMetrics:
                    for metric in metricNames:
                        algMetrics[algMetric][metric] = algMetrics[algMetric][metric] / (len(seeds))
                for an in algMetrics:
                    for mn in algMetrics[an]:
                        if not totalMetrics[mn].get(dsName):
                            totalMetrics[mn][dsName] = {}
                        totalMetrics[mn][dsName][an] = algMetrics[an][mn]
                        totalMetrics[mn][dsName]["dsName"] = dsName
                for mn in metricNames:
                    fh.writeResult(outDir + "/" + "_".join([dsName, size, rate]), totalMetrics[mn],
                                   ["dsName"] + algNames)
                algMetrics = {}
                for mn in metricNames:
                    totalMetrics[mn] = {}
                totalMetrics["time"] = {}

else:
    for dsName in dsNames:
        runtest(dsName)
        for an in algMetrics:
            for mn in algMetrics[an]:
                if not totalMetrics[mn].get(dsName):
                    totalMetrics[mn][dsName] = {}
                totalMetrics[mn][dsName][an] = algMetrics[an][mn]
                totalMetrics[mn][dsName]["dsName"] = dsName
        algMetrics = {}
    for mn in metricNames:
        fh.writeResult(outDir + "/" + "_".join(["acc", "mul", mn]), totalMetrics[mn], ["dsName"] + algNames)
