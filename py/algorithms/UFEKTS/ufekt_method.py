#!/usr/bin/env python
# coding: utf-8

import numpy as np
import pandas as pd
import random
import tensorly as tl
from sklearn.neighbors import NearestNeighbors
from tensorly.decomposition import tucker

tl.set_backend("numpy")


def make_kernel_matrix(x1, sigma=1, sub_len=2):
    '''
    function to make a kernel matrix
    Parameters:
    -----------
    x1 : numpy.array
        This is a vector of time series.
    sigma : double
        This is used for RBF kernel.
    sub_len : int
        This is length of subsequence. It is also called window-size.    
    '''
    d_len = len(x1) - sub_len + 1
    km = np.zeros([d_len, d_len])
    em1 = np.zeros([d_len, d_len])
    for i in range(d_len):
        for j in range(d_len):
            if i < j: continue
            element1 = 0
            for l in range(sub_len):
                element1 += (x1[i+l] - x1[j+l])**2
            km[i,j] = np.exp(-(element1)/(sigma**2))
            km[j,i] = km[i,j]
            em1[i,j] = element1
            em1[j,i] = em1[i,j]
            #print('element1/2', element1, element2, 'i', i, 'j', j, 'l', l)
    return(km, em1)

def make_kernel_tensor(mat, width, sigma):
    '''
    Paramters
    --------------
    mat : numpy.ndarray
        multivariate time series data (T x P) where P is the number 
        of variables and T is the number of time series
    width : int
        length of subsequences of multivariate time series
    sigma : double
        the sigma is used in RBF kernel, exp{-(x_i - x_j)^2/sigma}
        
    Return
    ----------
    km : numpy.ndarry
        tensor of a kernel
        three-dimensional array (third-order tensor)
    '''
    dim = mat.shape
    if len(dim) == 1:
        km_tmp,_ = make_kernel_matrix(mat, sigma, width)
        km = np.array(km_tmp)
    else:
        # the number of time stamps and variables
        N,K = mat.shape
        #km = np.zeros([K, K])
        #print('p = ', end='')
        for k1 in range(K):
            print(str(k1) + ' ', end='')
            km_tmp,_ = make_kernel_matrix(mat[:,k1], sigma, width)
            km_tmp = km_tmp.reshape(1, km_tmp.shape[0], km_tmp.shape[1])
            if k1==0:
                km = np.array(km_tmp)
            else:
                km = np.vstack([km, km_tmp])
        #print(' ')
    return(km)

def nn(mat, dis_th, _n_neighbors=5, _algorithm='ball_tree'):
    '''
    A function of the nearest neighbor algorithm
    Returns
    ----------
    distance : 
        distance from every data point to its nearest neighbor
        1st column indicates distance to itself, i.e., zero
        2nd column indicates distance to the nearest neighbor
        3rd column indicates distance to the 2nd nearest neighbor
        ...
    out : 
        ...
    '''
    np.random.seed(0)
    nn = NearestNeighbors(n_neighbors=_n_neighbors, algorithm=_algorithm).fit(mat)
    distances, indices = nn.kneighbors(mat)
    dis = distances[:,1]
    out = np.where(dis > dis_th)
    return(distances, out)

def min_max(x, axis=None):
    min = x.min(axis=axis, keepdims=True)
    max = x.max(axis=axis, keepdims=True)
    result = (x-min)/(max-min)
    return result

def optimize_ranks_coretensor(kt, ranks_max, rank_th=3, knn_k=5, norm=False):
    '''
    Optimize ranks of core tensor
    
    Parameters
    ----
    kt : np.array
        three-dimensional tensor to be decomposed
    ranks_max : np.array
        maximum ranks of core tensor, i.e., [4,100,100]
        second number in the ranks_max is used for loop count
        [i,j,j] i: the number of variables in multivariate time series, j: size of kernel matrices
    rank_th : int
        threshold to neglect a low rank
    knn_k : int
        k for kth-nearest neighbor
    norm : bool
        whether or not normalization is applied for decomposed factor matrix

    Returns
    ----
    df: pd.DataFrame
        summary of results
    rank_knndist: int
        optimized rank by kth nearest neighbor algorithm
    '''
    df = pd.DataFrame(columns=['rankid', 'knndist'])
    print(ranks_max[1])
    for i in range(ranks_max[1]):
        if (i % 5) == 0:
            print(i)
        ranks_tmp = np.array([ranks_max[0], i+1, i+1])
        #print(ranks_tmp, end='')
        # tensor decomposition
        core, factors = tucker(kt, ranks=ranks_tmp, random_state=0)
        if norm != False:
            # normalize a factor matrix
            fmat = min_max(factors[1], axis=0)
        else:
            fmat = factors[1]
        
        score_pred,outlier_rows = nn(fmat, dis_th=0.01, _n_neighbors=knn_k, _algorithm='ball_tree')
        knndist_vec = score_pred[:,(knn_k - 1)] # distance to k-th nearest neighbor
        knndist = np.max(knndist_vec)/np.sum(knndist_vec)

        rslt = pd.Series([i+1, knndist], index=df.columns)
        df = df.append(rslt, ignore_index=True)
        
    rank_knndist = np.argmax(np.array(df['knndist'][rank_th-1:])) + rank_th
    print(df['knndist'])
    return(df, rank_knndist)

def ufekt(mat, width, sigma, rank_range=None, rank=3, rank_th=10, seed=0):
    '''
    UFEKT (Unsupervised Feature Extraction uisng Kernel Method and Tucker Decomposition) can be 
    used for extracting fetatures from multivariate time series. The resulting 
    vectors from UFEKT are representing features of the multivariate time series and 
    they can be used for variety of applications such as outlier detection and clustering.
    
    Paramters
    ----------------
    mat : numpy.ndarray
        A matrix of multivariate time series data (T x P) 
        where T and P indicate length of the series and the number of variables, respectively.
    width : int
        Length of subsequences of multivariate time series. 
    sigma : double
        The sigma is used in RBF kernel, i.e., exp{-(x_i - x_j)^2/sigma}.
        Zero is prohibited.
    rank_range : list
        Three dimensional ranks must be specified here. 
        The first value would be the number of variables.
        The second and third values indicate maximum ranks which are used for search of the best ranks for outlier detection.        
    rank_th : int
        A minimum rank to specify search range to find the best rank for outlier detection.
    seed : int
        Random seed

    Returns
    ----------------
    factors : numpy.ndarray
        A matrix representing features of multivariate time series.
        It is also the set of feature vectors.
    core : numpy.ndarray (three-dimensional array)
        Core tensor decomposed by Tucker Decomposition
    factors : numpy.ndarray
        Factor matrices decomposed by Tucker Decomposition
    tensor : numpy.ndarray (three-dimensional array)
        Tensor of kernel
    ranks : list
        Ranks which is used for Tucker Decomposition
        The other outputs such as core and factors use these ranks when they are decomposed.
    df_opt : pd.DataFrame
        Results of optimization of ranks
    '''
    random.seed(seed)
    np.random.seed(seed)
    if sigma == 0:
        print('Error: sigma = 0')
    # make a tensor of kernel
    print('making a tensor, ', end='')
    tensor = make_kernel_tensor(mat, width, sigma)

    # optimize ranks of core tensor
    print('optimizing ranks, ', end='')
    if rank_range != None:
        df_opt,rank_knndist=optimize_ranks_coretensor(
                tensor, ranks_max=rank_range, rank_th=rank_th, norm=False)
        ranks = [mat.shape[1], rank_knndist, rank_knndist]
    else:
        ranks = [mat.shape[1], rank, rank]
        
    # decompose the tensor into one core tensor and three factor matrices
    print('Tucker decomposition.')
    core,factors = tucker(tensor, ranks, random_state=seed)

    return(core,factors,tensor,ranks,df_opt)



