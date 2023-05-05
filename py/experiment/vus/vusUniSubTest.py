import os
import time

import instanceFactory as fact
import metaData as meta
import tools.dataHandler as dh
import tools.fileHandler as fh
from metrics import metricor

'''label=pd.read_csv("C:/Users/17658/Documents/label.csv",header=None);
label=np.array(label)
label=label.reshape(-1)
score=pd.read_csv("C:/Users/17658/Documents/LRRDS_uni_subg_sp_score.csv",header=None);
score=np.array(score)
score=score.reshape(-1)
slidingWindow = 50
grader = metricor()
R_AUC_ROC, R_AUC_PR, _, _, _ = grader.RangeAUC(labels=label, score=score, window=slidingWindow,
                                                   plot_ROC=True)
_,_,_, _, VUS_ROC, VUS_PR = grader.RangeAUC_volume(labels_original=label, score=score,
                                                         windowSize=2 * slidingWindow)'''
# Data Preprocessing
slidingWindow = 50 # user-defined subsequence length
metricType = "subsequence"
metricNames = ["precision", "recall", "fmeasure",'R_AUC_ROC','R_AUC_PR','VUS_ROC','VUS_PR']
seeds = ["1","2","3","4","5","6","7","8","9","10"]
algNames=['NormA','BeatGAN']
dsName='uni_subt_sp'
algMetrics = {}
methods_score = {}

for seed in seeds:
    print("begin on seed "+seed)
    for alg in algNames:
        methods_score[alg] = {}
        methods_score[alg]['rseries'] = []
        methods_score[alg]['score'] = []
    # data preprocess
    series = fh.readUniDataWithLabel(os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                                  meta.dataSetsParameters.get(dsName).get("tedir"),
                                                  "_".join([dsName,seed])))
    labels = dh.getLable(series)
    for algName in algNames:
        if algName == 'NormA':
            series = fh.readUniDataWithLabel(os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                                          meta.dataSetsParameters.get(dsName).get("tedir"),
                                                          "_".join([dsName,seed])))
            inst = fact.getAlgInstance(algName)
            normaargs = meta.algorithmsParameters.get(algName).get(dsName)
            if inst:
                if not algMetrics.get(algName):
                    tm = {}
                    for mn in metricNames:
                        tm[mn] = 0.0
                    #tm["time"] = 0.0
                    algMetrics[algName] = tm
                start = time.time()
                inst.init(normaargs, series.copy())
                rseries, score = inst.run()
                #algMetrics[algName]["time"] = (float(algMetrics[algName].get("time")) + time.time() - start) * 1000

        elif algName == "BeatGAN":
            series = fh.readMulDataWithLabel(
                os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                             meta.dataSetsParameters.get(dsName).get("tedir"),
                             "_".join([dsName,seed])))
            trainingSeries = fh.readMulDataWithLabel(os.path.join(meta.dataSetsParameters.get(dsName).get("dir"),
                                                                  meta.dataSetsParameters.get(dsName).get("tdir"),
                                                                  dsName))
            inst = fact.getAlgInstance(algName)
            beatganargs = meta.algorithmsParameters.get(algName).get(dsName)
            if inst:
                if not algMetrics.get(algName):
                    tm = {}
                    for mn in metricNames:
                        tm[mn] = 0.0
                    #tm["time"] = 0.0
                    algMetrics[algName] = tm
                start = time.time()
                inst.init(beatganargs, trainingSeries, trainingSeries)
                trained_model = inst.training()
                rseries, score = inst.predict(trained_model, series, beatganargs['top_k'])
                #algMetrics[algName]["time"] = (float(algMetrics[algName].get("time")) + time.time() - start) * 1000

        methods_score[algName]['rseries'] = rseries
        methods_score[algName]['score'] = score

    for algName in algNames:
        predictAnomaly = dh.getAnomalySequences(methods_score[algName]['rseries'])
        fh.writeMiddleResult("acc/" + '_'.join([algName, dsName]), predictAnomaly)
        fh.writeScore("acc/" + '_'.join([algName, dsName, 'score']), methods_score[algName]['score'])
        # compute RF
        mtools = fact.getMetricInstance(metricType)
        mtools.init(series, methods_score[algName]['rseries'])
        for i in range(len(metricNames)):
            if i<3:
                mn=metricNames[i]
                algMetrics[algName][mn] = algMetrics[algName][mn] + getattr(mtools, mn, None)()
        # compute vus
        grader = metricor()
        R_AUC_ROC, R_AUC_PR, _, _, _ = grader.RangeAUC(labels=labels, score=methods_score[algName]['score'],
                                                       window=slidingWindow,
                                                       plot_ROC=True)
        _, _, _, _, VUS_ROC, VUS_PR = grader.RangeAUC_volume(labels_original=labels,
                                                             score=methods_score[algName]['score'],
                                                             windowSize=2 * slidingWindow)
        algMetrics[algName]['R_AUC_ROC'] += R_AUC_ROC
        algMetrics[algName]['R_AUC_PR'] += R_AUC_PR
        algMetrics[algName]['VUS_ROC'] += VUS_ROC
        algMetrics[algName]['VUS_PR'] += VUS_PR

for algName in algNames:
    print(algName)
    for metric in algMetrics[algName].keys():
        print(algMetrics[algName][metric] / len(seeds))