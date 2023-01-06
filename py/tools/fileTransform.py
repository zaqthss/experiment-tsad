import math
import os.path

import pandas as pd
import numpy as np
from pandas import DataFrame
from tqdm import tqdm
import threading

origin = "origin"
test = "test"
valid = "valid"
train = "train"

trainrate = 0.4
valrate = 0.1
testrate = 0.5
des = DataFrame({"File Name": [],
                 "TrainLength": [],
                 "Valid Deep Learning Length": [],
                 "Valid Deep Learning  Anomaly": [],
                 "Valid Normal Length": [],
                 "Valid Normal Anomaly": [],
                 "Test Length": [],
                 "Test Anomaly": []})


def splitdatasets(path, file,i):
    if os.path.exists(os.path.join(path, test, file + ".csv")):
        return
    df = pd.read_csv(os.path.join(path, origin, file + ".csv"))
    length = df.shape[0]
    print(file)
    #print("total leangth :" + str(length))

    trainl = math.floor(length * trainrate)
    valNl = math.floor(length * (valrate + trainrate))
    valLl = math.floor(length * valrate)
    aList = df[df[df.columns[-1]] == 1].index
    shift = len(aList[aList < length / 2])
    if (2 * shift < len(aList)):
        # most of the anomalies are at the last part
        while True:
            valanomalyL = math.ceil(len(aList) * (valrate / testrate))
            testanomalyL = len(aList) - valanomalyL
            valLastAnomalyPos = aList[valanomalyL - 1]
            testFirstAnomalyPos = aList[valanomalyL]
            vallEnd = math.floor(
                (testFirstAnomalyPos - valLastAnomalyPos) * (valrate + trainrate)) + valLastAnomalyPos
            if vallEnd + 1 - valNl > 0:
                trainBegin = vallEnd - valNl
                vallBegin = vallEnd - valLl
                trainEnd = vallBegin
                break;
            else:
                aList = aList.delete(0)
    else:
        # most of the anomalies are in the front part
        while True:
            valanomalyL = math.ceil(len(aList) * (valrate / testrate))
            testanomalyL = len(aList) - valanomalyL
            valFirstAnomalyPos = aList[-valanomalyL]
            testLastAnomalyPos = aList[-valanomalyL - 1]
            vallBegin = valFirstAnomalyPos + math.ceil(
                (valFirstAnomalyPos - testLastAnomalyPos) * (valrate + trainrate))
            if vallBegin + valNl < length:
                trainBegin = vallBegin + valLl
                vallEnd = trainBegin
                trainEnd = trainBegin + trainl
                break;
            else:
                aList = aList.delete(-1)
    testl = math.floor(length * testrate)
    #print("train leangth :" + str(trainl))
    #print("vaNl leangth :" + str(valNl))
    #print("vaLl leangth :" + str(valLl))
    #print("test leangth :" + str(testl))
    trdf = DataFrame(columns=df.columns)
    valNdf = DataFrame(columns=df.columns)
    valLdf = DataFrame(columns=df.columns)
    tedf = DataFrame(columns=df.columns)

    trainR = range(trainBegin, trainEnd)
    vallR = range(vallBegin, vallEnd)

    #with tqdm(total=df.shape[0],position=i) as pbar:
    for index, row in df.iterrows():
        if index in trainR:
            if row[-1]:
                continue
            else:
                trdf = trdf.append(row)
                valNdf = valNdf.append(row)
        elif index in vallR:
            valLdf = valLdf.append(row)
            valNdf = valNdf.append(row)
        else:
            tedf = tedf.append(row)
      #      pbar.update(1)
    trdf.to_csv(os.path.join(path, train, file + ".csv"), index=False)
    valLdf.to_csv(os.path.join(path, valid, file + "L.csv"), index=False)
    valNdf.to_csv(os.path.join(path, valid, file + "N.csv"), index=False)
    tedf.to_csv(os.path.join(path, test, file + ".csv"), index=False)
    global des
    des = des.append({"File Name": file,
                      "TrainLength": trdf.shape[0],
                      "Valid Deep Learning Length": valLdf.shape[0],
                      "Valid Deep Learning  Anomaly": str(valLdf[valLdf.columns[-1]].value_counts()),
                      "Valid Normal Length": valNdf.shape[0],
                      "Valid Normal Anomaly": str(valNdf[valNdf.columns[-1]].value_counts()),
                      "Test Length": tedf.shape[0],
                      "Test Anomaly": str(tedf[tedf.columns[-1]].value_counts())}, ignore_index=True)
    print(file+" end")


path = r"E:\test"
dir = ""
file = ""
if "__main__":
    list = os.listdir(os.path.join(path, dir, origin))
    threads = []
    i=0
    for file in list:
        file = file.replace(".csv", "")
        t = threading.Thread(target=splitdatasets, args=(os.path.join(path, dir), file,i))
        i+=1
        threads.append(t)
        t.start()
        while len(threads) >= 15:
            for t in threads:
                t.join()
            i=0
            threads.clear()
    for t in threads:
        t.join()
    print("fin")
    des.to_csv(os.path.join(path, dir, "Description.csv"), index=False)
