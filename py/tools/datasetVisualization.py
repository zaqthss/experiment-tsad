import os

import matplotlib.pyplot as plt
import numpy as np
import pandas as pd

from metaData import dataSetsParameters

dsNames = ["twitter", "credit", "ecg", "pen", "dlr"]
dataPath = "..\\data\\"


def generateGraph(d, outPut):
    # data = data.iloc[15000:15100, :]
    timestamp = d[d.columns[0]]
    label = d[d.columns[-1]].replace(0, np.NaN)
    fig = plt.figure(figsize=(8, 6))
    ax1 = fig.add_subplot(111)
    ax1.plot(timestamp, d[d.columns[dim]])
    ax1.set_xlabel('TimeStamp')
    ax1.set_ylabel('Data')
    # ax1.set_ylim(-5, 5)
    ax2 = ax1.twinx()  # this is the important function
    ax2.plot(timestamp, label, 'ro')
    ax2.set_ylim(-0.1, 1.1)
    ax2.set_ylabel('Label')
    ax2.set_xlabel('Same')
    plt.savefig(outPut)
    plt.show()
    print(path)


for dsName in dsNames:
    path = os.path.join(dataPath, dataSetsParameters[dsName].get("dir"),
                        dataSetsParameters[dsName].get("odir"),
                        dataSetsParameters[dsName].get("prefix") + ".csv")
    data = pd.read_csv(path)
    for dim in range(1, data.shape[1] - 1):
        outPut = os.path.join("plots", "DataSet", "_".join([dsName, "data", str(dim), "Graph"]))
        generateGraph(data, outPut)

# df1 = pd.read_csv('OmniAnomaly_uni_pointg_20230322.csv')
# data = df1[df1["epoch"]==800]
# data = data[data["lr"]==0.01]
# plt.plot(data['currepoch'],data['MSE'])
# plt.savefig("OmniAnomaly_uni_pointg_20230322")

# df2 = pd.read_csv('100.csv')
# plt.plot(df2['currepoch'],df2['L1'])
# plt.savefig("100")

# df3 = pd.read_csv('400.csv')
# plt.plot(df3['currepoch'],df3['L1'])
# plt.savefig("400")
