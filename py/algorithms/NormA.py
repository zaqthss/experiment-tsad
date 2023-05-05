# Authors: Paul Boniol, Michele Linardi, Federico Roncallo, Themis Palpanas
# Date: 08/07/2020
# copyright retained by the authors
# algorithms protected by patent application FR2003946
# code provided as is, and can be used only for research purposes



import math
from typing import Dict

import numpy as np
from sklearn.preprocessing import MinMaxScaler

from entity import *
from .NormAs.matrixprofile import *
from .NormAs.nA_normalmodel import *
from .NormAs.nA_recurrent_sequences import *
from .NormAs.tools import *
from .algorithm import Algorithm


class NormA(Algorithm):

	def __init__(self):
		super(NormA, self).__init__()
		self.series = None

	def init(self, args: Dict, series: timeSeriesUni):
		self.series = series
		self.pattern_length = args.get('pattern_length')
		self.nm_size = args.get('nm_size')
		self.top_k=args.get('top_k')
		self.percentage_sel=0.4


	def run(self):
		ts=[]
		for i,point in enumerate(self.series.timeseries):
				ts.append(point.observe)
		recurrent_sequence,sequence_rec = extract_recurrent_sequences_random(ts, self.nm_size,percentage_sel=self.percentage_sel)
		listcluster,dendogram = clustering_method(recurrent_sequence)
		nms,scores_nms= choose_normalmodel(listcluster,recurrent_sequence, sequence_rec)
		
		self.normalmodel = [nms,scores_nms]

		all_join = []
		for index_name,nm in enumerate(nms):
			join=stomp(ts,self.pattern_length,nm,1)
			join = np.array(join)
			join = (join - min(join))/(max(join) - min(join))
			all_join.append(join)

		join = [0]*len(all_join[0])
		for sub_join,scores_sub_join in zip(all_join,scores_nms):
			join = [float(j) + float(sub_j)*float(scores_sub_join) for j,sub_j in zip(list(join),list(sub_join))]

		join = np.array(join)
		join = running_mean(join,self.pattern_length)
		joinall = np.array([join[0]] * (self.pattern_length // 2) + list(join) + [join[-1]] * (self.pattern_length // 2))
		joinall = MinMaxScaler(feature_range=(0, 1)).fit_transform(joinall.reshape(-1, 1)).ravel()
		joinall = np.array([joinall[0]] * math.ceil((self.pattern_length - 1) // 2) + list(joinall) + [joinall[-1]] * (
				(self.pattern_length - 1) // 2))
		self.score=joinall
		score=np.argsort(join)
		score=np.flipud(score)
		reseries=self.series.copy()
		reseries.clear()
		for k in range(self.top_k):
			index=score[0]
			for i in range(index,min(index+self.pattern_length,len(reseries.timeseries))):
				reseries.timeseries[i].is_anomaly=True
			score=drop_score(score,index,pattern_length=self.pattern_length,len=len(self.series.timeseries))
			if len(score)==0:
				break
		return reseries,joinall

	def evaluate(self,k):
		reseries = self.series.copy()
		reseries.clear()
		score_sorted = np.argsort(self.score)
		score_sorted = np.flipud(score_sorted)

		for t in range(k):
			index = score_sorted[0]
			for i in range(index, min(index + self.pattern_length, len(reseries.timeseries))):
				reseries.timeseries[i].is_anomaly = True
			score_sorted = drop_score(score_sorted, index, pattern_length=self.pattern_length, len=len(self.series.timeseries))
			if len(score_sorted) == 0:
				break
		return reseries



def drop_score(all_score,index_start,pattern_length,len):
	new_all_score=[]
	minn = max(index_start - pattern_length, 0)
	maxx = min(index_start + pattern_length, len)
	for s in all_score:
		if s>=minn and s<maxx:
			continue
		else:
			new_all_score.append(s)
	return new_all_score




