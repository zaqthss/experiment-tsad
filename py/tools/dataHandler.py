import numpy as np

from entity import timeSeries
import instanceFactory as fact

def getAnomalySequences(series: timeSeries):
    currSet = []
    totalSet = []
    series = series.timeseries
    for id in range(0, len(series)):
        if series[id].is_anomaly:
            currSet.append(series[id].id)
        else:
            if len(currSet) > 0:
                totalSet.append(currSet)
                currSet = []
    if len(currSet) > 0:
        totalSet.append(currSet)
    return totalSet

def getAnomalyPoint(series: timeSeries):
    totalSet = []
    series = series.timeseries
    for id in range(0, len(series)):
        if series[id].is_anomaly:
            totalSet.append(series[id].id)
    return totalSet

def transPointToRange(pointAnomaly):
    currSet = []
    totalSet = []
    curTs=-1
    for p in pointAnomaly:
        if len(currSet)>0 and p-curTs!=1:
            totalSet.append(currSet)
            currSet=[]
        currSet.append(p)
        curTs=p
    if len(currSet)>0:
        totalSet.append(currSet)
    return totalSet

def DicttoList(dict):
    list=[]
    for d in dict:
        list.append(str(d))
        list.append(str(dict.get(d)))
    return list

#mul range to uni alg
def evaluateMultoUni(tsArray,series,metricNames):
    mtools = fact.getMetricInstance('subsequence')
    realAnomaly=getAnomalySequences(series)
    predictAnomalyCombine=[]
    for ts in tsArray:
        predictAnomalyCombine=predictAnomalyCombine+getAnomalyPoint(ts)
    predictAnomalyCombine = list({}.fromkeys(predictAnomalyCombine).keys())
    predictAnomalyCombine.sort()
    predictAnomalyCombine=transPointToRange(predictAnomalyCombine)
    mtools.computeMetric(realAnomaly, predictAnomalyCombine)
    algMetrics={}
    for mn in metricNames:
        algMetrics[mn] = getattr(mtools, mn, None)()
    return algMetrics

def getLable(series: timeSeries):
    label=[]
    series = series.timeseries
    for id in range(0, len(series)):
        if series[id].is_anomaly:
            label.append(1)
        else:
            label.append(0)
    label=np.array(label)
    return label

