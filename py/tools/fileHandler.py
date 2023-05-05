import csv
import os.path
import pathlib
from datetime import datetime
from typing import Dict
from algorithms.TranADs.src.constants import constants
import pandas

from entity import *

dataPath = "../data/"
resultPath = "result/"
middlePath = "../middle/"
LRPath = "LossRate/"

def readUniDataWithoutLabel(fname):
    ts = timeSeriesUni()
    with open(dataPath + fname + ".csv") as f:
        origin = csv.reader(f)
        next(origin)
        i = 1
        for line in origin:
            tp = timePointUni()
            tp.id = i
            i = i + 1
            tp.timestamp = int(line[0])
            tp.truth = float(line[1])
            tp.observe = float(line[1])
            tp.obsVal = float(line[1])
            tp.predictVal = float(line[1])
            tp.truthVal = float(line[1])
            tp.is_anomaly = False
            ts.timeseries.append(tp)
    return ts

def readUniDataWithLabel(fname):
    ts = timeSeriesUni()
    with open(dataPath + fname + ".csv") as f:
        origin = csv.reader(f)
        next(origin)
        i = 1
        for line in origin:
            tp = timePointUni()
            tp.id = i
            i = i + 1
            tp.timestamp = int(float(line[0]))
            tp.truth = float(line[1])
            tp.observe = float(line[1])
            tp.obsVal = float(line[1])
            tp.predictVal = float(line[1])
            tp.truthVal = float(line[1])
            tp.is_anomaly = bool(int(float(line[-1])))
            ts.timeseries.append(tp)
    return ts

def readMulDataWithoutLabel(fname):
    ts = timeSeriesMul()
    with open(dataPath + fname + ".csv") as f:
        origin = csv.reader(f)
        next(origin)
        i = 1
        for line in origin:
            tp = timePointMul()
            tp.id = i
            i = i + 1
            tp.timestamp = int(line[0])
            arr = list(map(float, line[1:len(line)]))
            tp.truth = arr.copy()
            tp.observe = arr.copy()
            tp.obsVal = arr.copy()
            tp.predictVal = arr.copy()
            tp.truthVal = arr.copy()
            tp.is_anomaly = False
            ts.timeseries.append(tp)
        ts.dim = len(line) - 1
    return ts

def readMulDataWithLabel(fname):
    ts = timeSeriesMul()
    with open(dataPath + fname + ".csv") as f:
        origin = csv.reader(f)
        next(origin)
        i = 1
        for line in origin:
            tp = timePointMul()
            tp.id = i
            i = i + 1
            tp.timestamp = int(float(line[0]))
            arr = list(map(float, line[1:len(line) - 1]))
            tp.truth = arr.copy()
            tp.observe = arr.copy()
            tp.obsVal = arr.copy()
            tp.predictVal = arr.copy()
            tp.truthVal = arr.copy()
            tp.is_anomaly = bool(int(float(line[-1])))
            ts.timeseries.append(tp)
        ts.dim = len(line) - 2
    return ts

def writeResult(fname, result: Dict, title):
    fname = resultPath + fname + ".csv"
    with open(fname, 'a', newline='') as f:
        writer = csv.DictWriter(f, title)
        if not os.path.exists(fname) or not os.path.getsize(fname):
            writer.writeheader()
        for value in result.values():
            writer.writerows([value])  # writerows方法是一下子写入多行内容

def writeScore(fname,result):
    fname = middlePath + fname + ".csv"
    with open(fname,'w', newline='') as f:
        writer = csv.writer(f)
        for value in result:
            writer.writerow([value])  # writerows方法是一下子写入多行内容
    f.close()

def writeMiddleResult(fname,result):
    fname = middlePath + fname + ".csv"
    with open(fname,'w', newline='') as f:
        writer = csv.writer(f)
        for value in result:
            writer.writerows([value])  # writerows方法是一下子写入多行内容
    f.close()

def writePointMiddleResult(fname,result):
    fname = middlePath + fname + ".csv"
    with open(fname,'w', newline='') as f:
        writer = csv.writer(f)
        writer.writerows([result])  # writerows方法是一下子写入多行内容
    f.close()

def writeSubResult(fname,title,vars,algNames,result,metricNames):
    head = []
    head.append(title)
    for a in algNames:
        head.append(a)
    for metric in metricNames:
        fname_total = resultPath + '_'.join([fname,metric,title]) + ".csv"
        with open(fname_total, 'w', newline='') as f:
            writer = csv.writer(f)
            writer.writerow(head)
            for v in vars:
                line=[]
                line.append(v)
                for alg in algNames:
                    if result.get(v).get(alg):
                        line.append(result.get(v).get(alg).get(metric))
                writer.writerow(line)
        f.close()
    for a in algNames:
        fname_total = resultPath + '_'.join([fname,metric,title]) + ".csv"
        with open(fname_total, 'w', newline='') as f:
            writer = csv.writer(f)
            writer.writerow(head)
            for v in vars:
                line=[]
                line.append(v)
                for alg in algNames:
                    if result.get(v).get(alg):
                        line.append(result.get(v).get(alg).get(metric))
                writer.writerow(line)
        f.close()

def writeThresholdResult(fname,title,types,vars,algNames,result,metricNames):
    head = []
    head.append(title)
    for metric in metricNames:
        head.append(metric)
    for type in types:
        for a in algNames:
            fname_total = resultPath +fname+ '-'.join(['uni_'+type+'_sp',a, title]) + ".csv"
            with open(fname_total, 'w', newline='') as f:
                writer = csv.writer(f)
                writer.writerow(head)
                for v in vars:
                    line = []
                    line.append(v)
                    if result.get(type).get(a).get(v):
                        for metric in metricNames:
                            line.append(result.get(type).get(a).get(v).get(metric))
                    writer.writerow(line)
            f.close()

def writeAUCResult(fname,title,types,algNames,result):
    head = []
    head.append(title)
    for a in algNames:
        head.append(a)
    for type in types:
        fname_total = resultPath + fname + '-'.join(['uni_' + type + '_sp', title]) + ".csv"
        with open(fname_total, 'w', newline='') as f:
            writer = csv.writer(f)
            writer.writerow(head)
            for a in algNames:
                line = []
                line.append(a)
                if result.get(type).get(a):
                    line.append(result.get(type).get(a))
                writer.writerow(line)
        f.close()

def subDumper(fname,title,vars,metricNames,result):
    head = []
    head.append(title)
    for m in metricNames:
        head.append(m)
    fname_total = resultPath + fname + ".csv"
    with open(fname_total, 'w', newline='') as f:
        writer = csv.writer(f)
        writer.writerow(head)
        for v in vars:
            line=[]
            line.append(v)
            for m in metricNames:
                line.append(result.get(int(v)).get(m))
            writer.writerow(line)
    f.close()

def writeSeries(fname, series: timeSeries):
    ts = []
    val = {"val0": []}
    label = []
    for tp in series.timeseries:
        ts.append(tp.timestamp)
        if not (series.dim is None):
            for i in range(series.dim):
                if not val.get("val" + str(i)):
                    val["val" + str(i)] = []
                val["val" + str(i)].append(tp.obsVal[i])
        else:
            val["val0"].append(tp.obsVal)
        label.append(tp.is_anomaly)
    dict = {"timestamp": ts}
    dict.update(val)
    dict.update({"label": label})
    df = pandas.DataFrame(dict)

    for i in range(9999):
        if not os.path.exists("_".join([resultPath + fname,str(i),".csv"])):
            fname = ("_".join([resultPath + fname,str(i),".csv"]))
            break
    df.to_csv(fname)

def writeLR(model,epoch,lr):
    date = datetime.now().strftime("%Y%m%d")
    result = {**constants.Hyperparameters[model],**{"currepoch":epoch},**lr}
    fname = resultPath+LRPath+'_'.join([model, result["dsName"], date])+".csv"
    exist = pathlib.Path(fname).exists()
    with open(fname, 'a', newline='') as f:
        writer = csv.DictWriter(f,result.keys())
        if not exist:
            writer.writeheader()
        writer.writerow(result)  # writerows方法是一下子写入多行内容
    f.close()


def writeTemporal(fname,data):
    fname = os.path.join(resultPath, "temp", fname)+".csv"
    with open(fname, 'a', newline='') as f:
        f.write(data)
        f.write(os.linesep)
