# Authors: Paul Boniol, Michele Linardi, Federico Roncallo, Themis Palpanas
# Date: 08/07/2020
# copyright retained by the authors
# algorithms protected by patent application FR2003946
# code provided as is, and can be used only for research purposes


# Python Version: 3.6.1

#COMMENT IF IN PYTHON3, KEEP IF PYTHON2
from __future__ import print_function

#######################################################################
#######################################################################
#####				   NORMA AUXILIARY FUNCTIONS				 ######
#######################################################################
#######################################################################
import sys
import numpy as np
import pandas as pd
from tqdm import tqdm
from scipy.signal import argrelextrema
from math import sqrt

###################################################################################################
########################################## TOOLS FUNCTIONS ########################################
###################################################################################################


def get_bounded_ts(filename,inf_bound=-10000000, sup_bound=10000000):
	"""
	Given a file and two boundaries the timeseries contained
	in the files is returned after being cleaned
	by the out boundaries point
	"""
	ts = [float(point.strip('\n')) for point in open(filename, "r") ]
	for i,pnt in enumerate(ts):
	    if i == 0:
	        if pnt < inf_bound:
	            ts[i] = inf_bound
	        elif pnt > sup_bound:
	            ts[i] = sup_bound
	    elif pnt < inf_bound or pnt > sup_bound:
	        ts[i] = ts[i-1]
	return ts


###########################################
####### 	AGGREGATE FUNCTIONS		#######
###########################################

def running_mean(x,N):
	return (np.cumsum(np.insert(x,0,0))[N:] - np.cumsum(np.insert(x,0,0))[:-N])/N


###########################################
####### 	  GET FUNCTIONS			#######
###########################################


def get_sequence_under_threshold(list_y,T,length):
    result = []
    list_y = np.array(list_y)
    if len(list_y) == 0:
        return result
    ### Get value under threshold and order it in a ascendent way
    idx_uT = np.where(list_y<T)[0]
    idx_uT_ord = idx_uT[np.argsort(list_y[idx_uT])]
    idx_uT_ord = idx_uT_ord[::-1]


    if len(idx_uT_ord) == 0:
        return result

    ## Remove overlapping sequences given priority to the lowest selfJoin values
    match_mask = [0] * (len(list_y)) #create the mask
    for off in idx_uT_ord:
        is_overlapping_match = False
        for mask in match_mask[max(0,(off-length)):min(len(match_mask),(off+length))]:
            if(mask == 1):
                is_overlapping_match = True
                break
        if(not is_overlapping_match):
            result.append([off,off+length])
            match_mask[off] = 1
    return result



###########################################
##### DISTANCE & LOWER BOUND FUNCTION #####
###########################################

def DTWDistance(s1, s2,w = 20):
	DTW={}
	w = max(w, abs(len(s1)-len(s2)))
	for i in range(-1,len(s1)):
		for j in range(-1,len(s2)):
			DTW[(i, j)] = float('inf')
	DTW[(-1, -1)] = 0
	for i in range(len(s1)):
		for j in range(max(0, i-w), min(len(s2), i+w)):
			dist= (s1[i]-s2[j])**2
			DTW[(i, j)] = dist + min(DTW[(i-1, j)],DTW[(i, j-1)], DTW[(i-1, j-1)])
	return sqrt(DTW[len(s1)-1, len(s2)-1])

def LB_Keogh(s1,s2,r=20):
	LB_sum=0
	for ind,i in enumerate(s1):
		lower_bound=min(s2[(ind-r if ind-r>=0 else 0):(ind+r)])
		upper_bound=max(s2[(ind-r if ind-r>=0 else 0):(ind+r)])
		if i>upper_bound:
			LB_sum=LB_sum+(i-upper_bound)**2
		elif i<lower_bound:
			LB_sum=LB_sum+(i-lower_bound)**2
	return sqrt(LB_sum)




