# Authors: Paul Boniol, Michele Linardi, Federico Roncallo, Themis Palpanas
# Date: 08/07/2020
# copyright retained by the authors
# algorithms protected by patent application FR2003946
# code provided as is, and can be used only for research purposes

# Python Version: 3.6.1
# -*- coding: utf-8 -*-

#COMMENT IF IN PYTHON3, KEEP IF PYTHON2
from __future__ import print_function
import math
#######################################################################
#######################################################################
#####				NORMA NORMAL MODEL  FUNCTIONS				 ######
#######################################################################
#######################################################################

import numpy as np
import pandas as pd
import scipy.cluster.hierarchy as hac
from scipy.cluster.hierarchy import fcluster
from tqdm import tqdm
from tslearn.clustering import KShape

from .entropy_MDL import returnClustersMDL_AndSumMDL


#######################################################################
#####				     CLUSTERING FUNCTIONS				     ######
#######################################################################

##### EXPENSIVE OPERATIONS #####
def generate_dendrogram(recurrent_sequence, corr_method="pearson",linkage_method="complete",metric='euclidean'):
    """
        INPUT
            recurrent_sequence: A dataframe containg the list of time series in the columns to cluster
            corr_method: The correlation method to use in order to create the dendogram used to cluster
                            opt('pearson', 'kendall', 'spearman')
            linkage_method: The linkage method to use in order to create the dendogram used to cluster
                            check: https://docs.scipy.org/doc/scipy/reference/generated/scipy.cluster.hierarchy.linkage.html
            metric: the metric used to crete the dendogram
                    check: https://docs.scipy.org/doc/scipy/reference/generated/scipy.spatial.distance.pdist.html#scipy.spatial.distance.pdist

        OUTPUT
            tuple(correlation_matrix, dendo, distances)
            correlation_matrix: the correlation_matrix of the recurrent sequences
            dendo: a Dendrogram representation
            distances: all the pairwise distance of the matrix profile
    """
    if len(recurrent_sequence.columns) == 1:
        return None, None, None

    correlation_matrix = recurrent_sequence.corr(method = corr_method)
    n,m=correlation_matrix.shape
    for i in range(n):
        for j in range(m):
            if math.isnan(correlation_matrix.iloc[i,j]):
                correlation_matrix.iloc[i, j]=1e-6
    dendo = hac.linkage(correlation_matrix, linkage_method, metric=metric)
    distances = hac.distance.pdist(correlation_matrix, metric=metric)
    return correlation_matrix, dendo, distances


def cutting_method(recurrent_sequence, correlation_matrix, dendo, distances,
                   cut_method="max", cluster_level=0.33):
    """
        INPUT
            recurrent_sequence: A dataframe containg the list of time series in the columns to cluster
            correlation_matrix: A dataframe matrix representing the correlation_matrix of the recurrent sequences
            dendo: the dendrogram of the recurrent sequences
            distances: the pairwise distances of the correlation_matrix
            cut_method: The method used to cut the dendogram and generate the clusters
                            opt('max','minmax','auto')
            cluster_level: A constant variable used to tune the cluster cutting
        OUTPUT
            listcluster: is a list containg all the clusters generated
    """
    if cut_method not in ['max','minmax','auto']:
        raise ValueError("cut_method must be in ['max','minmax','auto']")


    if cut_method == "max":
        listcluster = fcluster(dendo,cluster_level*distances.max(),'distance')

    elif cut_method == "minmax":
        listcluster = fcluster(dendo,(cluster_level * (distances.max()-distances.min()))+ distances.min(),'distance')

    elif cut_method == "auto":
		###### MDL VERSION TOP DOWN ######
        DicBitSavedForCluster = None
        totalBitSaved = None
        start = max(0,distances.max()-0.0001) # starting point (top cut)
        end =  max(0,distances.min()-0.0001) # end point top cut
        listclusterUpperMost= fcluster(dendo, start, 'distance')
        nunmberOfCluster = len(set(listclusterUpperMost))
        setDisttemp = set(distances) - set([distances.min()])
        step = np.min(list(setDisttemp)) - distances.min()

        #time saver 
        step = max(step,0.0001)
        
        listClusterReturn = listclusterUpperMost
        DdlClusters, centerWithMinDl, chosenCluster, sumDl, DicBitSavedForCluster, totalBitSaved = returnClustersMDL_AndSumMDL(recurrent_sequence, listClusterReturn)
        lastNumberCluster = nunmberOfCluster
        lastSumDL = sumDl
        bestCenter = centerWithMinDl
        bestChosenClusterNumber = chosenCluster
        level = start-step

        while (level>=end):
            listclusterActual= fcluster(dendo, level, 'distance')
            nunmberOfCluster = len(set(listclusterActual))
            if(lastNumberCluster<nunmberOfCluster):
                DdlClusters, centerWithMinDl, chosenCluster, sumDl, DicBitSavedForClusterAc,totalBitSavedAc = returnClustersMDL_AndSumMDL(recurrent_sequence,listclusterActual)
                lastNumberCluster = nunmberOfCluster
                if(totalBitSaved<totalBitSavedAc):
                    totalBitSaved = totalBitSavedAc
                    lastSumDL=sumDl
                    bestCenter = centerWithMinDl
                    bestChosenClusterNumber = chosenCluster
                    listClusterReturn = listclusterActual
                else:
                    break
            level = level - step
        listcluster = listClusterReturn
    return listcluster

def clustering_method(recurrent_sequence, corr_method="pearson",linkage_method="complete", cut_method="max",
                     cluster_level=0.33, metric='euclidean'): #this metric is the default scipy metric for the used functions
    """
        INPUT
            recurrent_sequence: A dataframe containg the list of time series in the columns to cluster
            corr_method: The correlation method to use in order to create the dendogram used to cluster
                            opt('pearson', 'kendall', 'spearman')
            linkage_method: The linkage method to use in order to create the dendogram used to cluster
                            check: https://docs.scipy.org/doc/scipy/reference/generated/scipy.cluster.hierarchy.linkage.html
            cut_method: The method used to cut the dendogram and generate the clusters
                            opt('max','minmax','auto')
            cluster_level: A constant variable used to tune the cluster cutting
            metric: the metric used to crete the dendogram
                    check: https://docs.scipy.org/doc/scipy/reference/generated/scipy.spatial.distance.pdist.html#scipy.spatial.distance.pdist

        OUTPUT
            tuple(listcluster, dendo)
            listcluster: is a list containg all the clusters generated
            dendo: a Dendrogram representation
    """
    correlation_matrix, dendo, distances = generate_dendrogram(recurrent_sequence, corr_method=corr_method,
                                                                linkage_method=linkage_method, metric=metric)
    listcluster = cutting_method(recurrent_sequence, correlation_matrix, dendo, distances,
                                    cut_method=cut_method, cluster_level=cluster_level)
    return listcluster, dendo





def choose_normalmodel(listcluster,recurrent_sequence, sequence_rec):
    """
        INPUT
            listcluster: a list representing all the candidate to score
            recurrent_sequence:  dataframe of all the recurrent sequences
            sequence_rec:  a list of couple(start,end) of each recurrent sequence in the original time series
			score_funtion: the scoring function used to evaluate the clusters
						   opt('standard','extended')
        OUTPUT
            tuple(normalmodel,scores,min_max_index,cluster_mean)

            normalmodel: a list containg the normal model
            scores: a dictionary containg the following scores
                - score_time
                - score_mean
                - score_weight
            min_max_index: a list containg all the min max indexes for all the candidates
			cluster_mean: a list of all the centroids of the cluster
    """
    cluster_mean, min_max_index, score_time, score_mean, score_weight, score_distribution,all_index_p  = [], [], [], [], [], [], []
    # getting all the scoring variable

    for k in range(len(set(listcluster))):
        mean = pd.DataFrame()
        count = 0
        index_seq = []
        for i in range(len(listcluster)):
            if listcluster[i] == k+1 :
                index_seq.append(sequence_rec[i])
                mean_t = np.mean(recurrent_sequence[str(i)].values)
                std_t = np.std(recurrent_sequence[str(i)].values)

                data_to_join = [(float(j) - mean_t)/(std_t) for j in recurrent_sequence[str(i)].values]
                mean[str(count)] = data_to_join
                count += 1

        cluster_mean.append(mean.mean(axis=1).values)
        score_weight.append(count)

        i_seq_m = [ (i_s[1] + i_s[0])/2 for i_s in index_seq]
        all_index_p.append(i_seq_m)
        score_time.append(np.mean(i_seq_m))
        min_max_index.append([np.min(i_seq_m), np.max(i_seq_m)])
        diff_off = np.diff(np.sort([i_s[0] for i_s in index_seq]))
        score_distribution.append(np.sum([np.abs((d_f/(min_max_index[-1][1] - min_max_index[-1][0])) -
                                    ((count-1)/(min_max_index[-1][1] - min_max_index[-1][0]))) for d_f in diff_off]))
    ########################################

    ###### pre process data to normalize later #####
    cluster_mean_diff = []
    min_max_diff = [np.diff(min_max)[0] for min_max in min_max_index]
    for c_mean in cluster_mean:
        cluster_mean_diff.append(np.sum([np.linalg.norm((c_mean - x), ord=1) for x in cluster_mean]))

    ###### compute scores ########
    for weight,min_max,c_mean, dist in zip(score_weight,min_max_diff,cluster_mean_diff,score_distribution):
        
        weight_n = (float(weight - np.min(score_weight))/float(np.max(score_weight)-np.min(score_weight)+1))+1.0
        min_max_n = (float(min_max - np.min(min_max_diff))/float(np.max(min_max_diff)-np.min(min_max_diff)+1))+1.0
        dist_n = (float(dist - np.min(score_distribution))/float(np.max(score_distribution)-np.min(score_distribution)+1))+1.0
        c_mean_n = (float(c_mean - np.min(cluster_mean_diff))/float(np.max(cluster_mean_diff)-np.min(cluster_mean_diff)+1))+1.0

        
        score_mean.append((weight_n*weight_n * min_max_n) / c_mean_n)
        

    return cluster_mean,score_mean


def choose_normalmodel_kshape(ks,listcluster,recurrent_sequence, sequence_rec):
   
    all_index_p, cluster_mean, min_max_index,score_time, score_mean, score_weight, score_distribution = [], [], [], [], [], [], []
    
    ########################################
    # getting all the scoring variable

    for k,idx_c in enumerate(set(listcluster)):
        mean = pd.DataFrame()
        count = 0
        index_seq = []
        for i in range(len(listcluster)):
            if listcluster[i] == idx_c :
                index_seq.append(sequence_rec[i])
                mean_t = np.mean(recurrent_sequence[str(i)].values)
                std_t = np.std(recurrent_sequence[str(i)].values)

                data_to_join = [(float(j) - mean_t)/(std_t) for j in recurrent_sequence[str(i)].values]
                mean[str(count)] = data_to_join
                count += 1

        cluster_mean.append(ks.cluster_centers_[k])
        score_weight.append(count)

        i_seq_m = [ (i_s[1] + i_s[0])/2 for i_s in index_seq]
        all_index_p.append(i_seq_m)
        score_time.append(np.mean(i_seq_m))
        min_max_index.append([np.min(i_seq_m), np.max(i_seq_m)])
        diff_off = np.diff(np.sort([i_s[0] for i_s in index_seq]))
        score_distribution.append(np.sum([np.abs((d_f/(min_max_index[-1][1] - min_max_index[-1][0])) -
                                    ((count-1)/(min_max_index[-1][1] - min_max_index[-1][0]))) for d_f in diff_off]))
    ########################################

    ###### pre process data to normalize later #####
    cluster_mean_diff = []
    min_max_diff = [np.diff(min_max)[0] for min_max in min_max_index]
    for c_mean in cluster_mean:
        cluster_mean_diff.append(np.sum([np.linalg.norm((c_mean - x), ord=1) for x in cluster_mean]))

    ###### compute scores ########
    for weight,min_max,c_mean, dist in zip(score_weight,min_max_diff,cluster_mean_diff,score_distribution):
        #normalize all the scores between (1,2)
        weight_n = (float(weight - np.min(score_weight))/float(np.max(score_weight)-np.min(score_weight)+1))+1.0
        min_max_n = (float(min_max - np.min(min_max_diff))/float(np.max(min_max_diff)-np.min(min_max_diff)+1))+1.0
        dist_n = (float(dist - np.min(score_distribution))/float(np.max(score_distribution)-np.min(score_distribution)+1))+1.0
        c_mean_n = (float(c_mean - np.min(cluster_mean_diff))/float(np.max(cluster_mean_diff)-np.min(cluster_mean_diff)+1))+1.0

        score_mean.append((weight_n*weight_n * min_max_n) / c_mean_n)
        
    return cluster_mean,score_mean
