# Authors: Paul Boniol, Michele Linardi, Federico Roncallo, Themis Palpanas
# Date: 08/07/2020
# copyright retained by the authors
# algorithms protected by patent application FR2003946
# code provided as is, and can be used only for research purposes



import math

import numpy as np

#from cgi import log





saxBP = [-1.53, -1.15, -0.89, -0.67, -0.49, -0.32, -0.16, 0, 0.16, 0.32, 0.49, 0.67, 0.89, 1.15, 1.53]

def findValuesSax(real):
    global saxBP
    pos=0
    for x in saxBP:
        if real>x:
            pos+=1
        else:
            break
    return pos


def computeEntropy(T):
    D={}
    for p in T:
        x = findValuesSax(p)
        if x in D:
            D[x] = D[x] +1
        else:
            D[x] =  1

    entropy = 0
    nKeys = len(D.keys())
    for k in D.keys():
    #probability of key
        prob = float(D[k])/float(len(T))
        logProb = math.log(prob,2)
        entropy = entropy + prob*logProb

    entropy = -1 * entropy
    return entropy

def computeDescriptionLength(T):
    return (len(T) * computeEntropy(T))



def computeCondDescLength(A,B):
    diff = np.subtract(A,B)
    return computeDescriptionLength(diff)

def returnClustersMDL(dataFrameSubsequences, clusterNumbers):
    Cluster ={}
    ClustersCenters = {}
    numberElementsCluster = {}

    for i in range(len(clusterNumbers)):
        clNunmb = clusterNumbers[i]
        if clNunmb in ClustersCenters:
            listA = dataFrameSubsequences[str(i)].values
            listB = ClustersCenters[clNunmb]
            ClustersCenters[clNunmb] = np.add(listA, listB)
            Cluster[clNunmb].append(dataFrameSubsequences[str(i)].values)
            numberElementsCluster[clNunmb] +=1
        else:
            ClustersCenters[clNunmb]= dataFrameSubsequences[str(i)].values
            Cluster[clNunmb] = [dataFrameSubsequences[str(i)].values]
            numberElementsCluster[clNunmb] = 1

    MdlClusetrs ={}
    minDl = 0
    maxDl = 0
    minDlCenter = None
    bFirst=True
    numberClusterBest = 0
    for k in Cluster.keys():
        maxDlCl = 0
        sumDlCl = 0
        listSeq = Cluster[k]
        center = [ (i/numberElementsCluster[k]) for i in ClustersCenters[k]]
        desL = computeDescriptionLength(center)
        for seq in listSeq:
            dlCond = computeCondDescLength(seq,center)
            sumDlCl = sumDlCl+dlCond
            maxDlCl= max(maxDlCl,dlCond)

        dlc = desL - maxDlCl + sumDlCl
        MdlClusetrs[k] = dlc
        if(bFirst):
            bFirst=False
            minDl = dlc
            maxDl = dlc
            numberClusterBest = k
        else:
            minDl = min(dlc,minDl)
            maxDl = max(dlc,maxDl)
            if(minDl==dlc):
                minDlCenter = center
            numberClusterBest = k

    return MdlClusetrs, minDlCenter, numberClusterBest, minDl

def returnClustersMDL_AndSumMDL(dataFrameSubsequences, clusterNumbers):
    Cluster ={}
    ClustersCenters = {}
    numberElementsCluster = {}

    for i in range(len(clusterNumbers)):
        clNunmb = clusterNumbers[i]
        if clNunmb in ClustersCenters:
            listA = dataFrameSubsequences[str(i)].values
            listB = ClustersCenters[clNunmb]
            ClustersCenters[clNunmb] = np.add(listA, listB)
            Cluster[clNunmb].append(dataFrameSubsequences[str(i)].values)
            numberElementsCluster[clNunmb] +=1
        else:
            ClustersCenters[clNunmb]= dataFrameSubsequences[str(i)].values
            Cluster[clNunmb] = [dataFrameSubsequences[str(i)].values]
            numberElementsCluster[clNunmb] = 1

    MdlClusetrs ={}
    BitSaveCluster = {}
    minDl = 0
    maxDl = 0
    minDlCenter = None
    bFirst=True
    numberClusterBest = 0
    sumUptotalMDL = 0
    totalBitSavedClusters = 0
    for k in Cluster.keys():
        maxDlCl = 0
        sumDlCl = 0
        listSeq = Cluster[k]
        center = [ (i/numberElementsCluster[k]) for i in ClustersCenters[k]]
        desL = computeDescriptionLength(center)
        totalBitSeq = 0
        for seq in listSeq:
            dlCond = computeCondDescLength(seq,center)
            sumDlCl = sumDlCl+dlCond
            maxDlCl= max(maxDlCl,dlCond)
            totalBitSeq+=computeDescriptionLength(seq)

        dlc = desL - maxDlCl + sumDlCl
        MdlClusetrs[k] = dlc
        totalBitSave = totalBitSeq-dlc
        BitSaveCluster[k] = totalBitSave
        totalBitSavedClusters+=totalBitSave
        if(bFirst):
            bFirst=False
            minDl = dlc
            maxDl = dlc
            numberClusterBest = k
            minDlCenter = center
        else:
            minDl = min(dlc,minDl)
            maxDl = max(dlc,maxDl)
            if(minDl==dlc):
                minDlCenter = center
                numberClusterBest = k
        sumUptotalMDL+=dlc

    return MdlClusetrs, minDlCenter, numberClusterBest, sumUptotalMDL, BitSaveCluster, totalBitSavedClusters
