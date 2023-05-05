import os
import time

from algorithms.algorithm import MachineLearningAlgorithm
import instanceFactory as fact
import metaData as meta
import tools.fileHandler as fh

dsName = "mul_subg_dim"
algNames = ["BeatGAN"]
metricType = "subsequence"
metricNames = ["precision", "recall", "fmeasure"]
outDir = "dim"
dims = [1,2,4,8,16,32,50]
hasSufix = True
sizes = ["10000"]
len='50'
type='subg'
seeds = ["1","2","3","4","5","6","7","8","9","10"]
rates = ["0.1"]


algMetrics = {}
totalMetrics = {}


def runtest(fname,dim):
    for algName in algNames:
        inst = fact.getAlgInstance(algName)
        if hasSufix:
            series = fh.readMulDataWithLabel(
                os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                             meta.dataSetsParameters.get(dsName).get("tedir"),
                             "_".join([meta.dataSetsParameters.get(dsName).get("prefix"), 'dim','50','len',len,size, rate, seed])))
            if isinstance(inst, MachineLearningAlgorithm):
                if meta.dataSetsParameters.get(dsName).get("tdir") is None:
                    raise IOError("Training file is needed")
                trainingSeries = fh.readMulDataWithLabel(
                    os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                 meta.dataSetsParameters.get(dsName).get("tdir"),
                                 "_".join([meta.dataSetsParameters.get(dsName).get("prefix"), 'dim','50','len',len,size])))
        else:
            series = fh.readMulDataWithLabel(
                os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                             meta.dataSetsParameters.get(dsName).get("tedir"),
                             dsName))
            if isinstance(inst, MachineLearningAlgorithm):
                if meta.dataSetsParameters.get(dsName).get("tdir") is None:
                    raise IOError("Training file is needed")
                trainingSeries = fh.readMulDataWithLabel(
                    os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                 meta.dataSetsParameters.get(dsName).get("tdir"),
                                 dsName))
        args = meta.algorithmsParameters.get(algName).get(dsName)
        inst = fact.getAlgInstance(algName)
        if inst:
            if not algMetrics.get(algName):
                tm = {}
                for mn in metricNames:
                    tm[mn] = 0.0
                tm["time"] = 0.0
                tm["algName"] = algName
                algMetrics[algName] = tm
            start = time.time()
            subs = series.getsubdim(dim)
            if isinstance(inst, MachineLearningAlgorithm):
                tsub = trainingSeries.getsubdim(dim)
                inst.init(args, subs, tsub)
            else:
                inst.init(args, subs)
            rseries = inst.run()
            algMetrics[algName]["time"] = float(algMetrics[algName].get("time")) + time.time() - start
            mtools = fact.getMetricInstance(metricType)
            mtools.init(series, rseries)
            for mn in metricNames:
                algMetrics[algName][mn] = algMetrics[algName][mn] + getattr(mtools, mn, None)()

for dim in dims:
    if hasSufix:
        for size in sizes:
            for rate in rates:
                print(" begin on dim " + str(dim))
                algName="BeatGAN"
                inst = fact.getAlgInstance("BeatGAN")
                trainingSeries = fh.readMulDataWithLabel(
                    os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                 meta.dataSetsParameters.get(dsName).get("tdir"),
                                 "_".join(
                                     [meta.dataSetsParameters.get(dsName).get("prefix"), 'dim', '50',
                                      'len', len, size])))
                beatganargs = meta.algorithmsParameters.get(algName).get(dsName)
                start = time.time()
                tsub = trainingSeries.getsubdim(dim)
                inst.init(beatganargs, tsub, tsub)
                trained_model = inst.training()
                train_time = time.time() - start
                for seed in seeds:
                    print(" begin on seed " + seed)
                    if hasSufix:
                        series = fh.readMulDataWithLabel(
                            os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                         meta.dataSetsParameters.get(dsName).get("tedir"),
                                         "_".join(
                                             [meta.dataSetsParameters.get(dsName).get("prefix"), 'dim', '50', 'len',
                                              len, size, rate, seed])))
                    else:
                        series = fh.readMulDataWithLabel(
                            os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                         meta.dataSetsParameters.get(dsName).get("tedir"),
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
                        subs = series.getsubdim(dim)
                        '''if isinstance(inst, MachineLearningAlgorithm):
                            tsub = trainingSeries.getsubdim(dim)
                            inst.init(args, subs, tsub)
                        else:
                            inst.init(args, subs)'''
                        # rseries = inst.run()
                        k = beatganargs['top_k']
                        rseries,_ = inst.predict(trained_model, subs, k)
                        algMetrics[algName]["time"] = float(
                            algMetrics[algName].get("time")) + time.time() - start + train_time
                        mtools = fact.getMetricInstance(metricType)
                        mtools.init(series, rseries)
                        for mn in metricNames:
                            algMetrics[algName][mn] = algMetrics[algName][mn] + getattr(mtools, mn, None)()
                for algMetric in algMetrics:
                    for metric in metricNames:
                        algMetrics[algMetric][metric] = algMetrics[algMetric][metric] / (seeds.__len__())
        totalMetrics[dim] = algMetrics
        algMetrics = {}
    else:
        runtest(meta.dataSetsParameters.get(dsName).get("dir") + "/" + dsName,dim)
        totalMetrics[dim] = algMetrics
        algMetrics = {}
fh.writeSubResult(outDir + "/" + "_".join(['mul_sub',dsName]), 'dim',dims,algNames,totalMetrics,metricNames+['time'])
