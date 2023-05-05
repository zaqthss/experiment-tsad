import numpy as np
from sklearn.preprocessing import MinMaxScaler
import math
from entity import *
from .BeatGANs.BeatGAN_CNN import BeatGAN_CNN
from .BeatGANs.BeatGAN_RNN import BeatGAN_RNN
from .BeatGANs.preprocess import load_datasetwithSeries
from .algorithm import MachineLearningAlgorithm

WINDOW=64
STRIDE_TRAIN=5
STRIDE_TEST=20

class BeatGAN(MachineLearningAlgorithm):

	def __init__(self):
		super(BeatGAN, self).__init__()
		self.series = None
		self.trainingSeries = None

	def init(self, args, series: timeSeriesMul, trainingSeries: timeSeriesMul):
		self.batch_size=args.get('batch_size')
		self.seq_len=args.get('seq_len')
		self.series=series
		self.dataloader= load_datasetwithSeries(trainingSeries,suq_len=self.seq_len,batch_size=self.batch_size)
		self.testdataloader=load_datasetwithSeries(series, suq_len=self.seq_len,batch_size=self.batch_size,is_train=False)
		self.network=args.get('network')
		self.input_size=series.dim
		self.max_epoch=args.get('max_epoch')
		self.hidden_size=args.get('hidden_size')
		self.req_size=args.get('rep_size')
		self.stride_size=5
		self.lambdaa=args.get('lambdaa')
		self.top_k=args.get('top_k')
		#self.threshold = args.get('threshold')

	def training(self):
		if self.network=='CNN':
			beatgan_CNN = BeatGAN_CNN(self.dataloader,seq_len=self.seq_len,input_size=self.input_size
									  ,rep_size=self.req_size,max_epoch=self.max_epoch,lambdaa=self.lambdaa)
			trained_model=beatgan_CNN.fit()
		elif self.network=='RNN':
			beatgan_RNN = BeatGAN_RNN(self.dataloader,seq_len=self.seq_len,input_size=self.input_size,lambdaa=self.lambdaa,
									  hidden_size=self.hidden_size,rep_size=self.req_size,max_epoch=self.max_epoch)
			trained_model=beatgan_RNN.fit()
		return trained_model

	def predict(self,trained_model,series: timeSeriesMul,k):
		self.testdataloader = load_datasetwithSeries(series, suq_len=self.seq_len, batch_size=self.batch_size,
													 is_train=False)
		self.top_k=k
		rec_diff, ori_ts, rec_ts, rec_err = trained_model.predict(self.testdataloader)
		new_score = []
		window_size = 8
		for scores in rec_err:
			tmp_score = 0
			for i in range(0, len(scores), window_size):
				tmp_score = max(tmp_score, np.mean(scores[i:i + window_size]))
			new_score.append(tmp_score)
		join=new_score
		alljoin=[]
		for i in range(0,series.__len__()):
			end = int(i / self.stride_size)
			start = int((i - self.seq_len) / self.stride_size)
			alljoin.append(np.average(join[max(0,start):min(end, len(join))+1]))
		alljoin=np.array(alljoin)
		alljoin = MinMaxScaler(feature_range=(0, 1)).fit_transform(alljoin.reshape(-1, 1)).ravel()
		self.score=alljoin
		score = np.argsort(alljoin)
		score = np.flipud(score)
		reseries = series.copy()
		reseries.clear()
		for k in range(self.top_k):
			index = score[0]
			for i in range(index, min(index + self.seq_len,len(series.timeseries))):
				reseries.timeseries[i].is_anomaly = True
			score = drop_score(score, index, self.seq_len, len(series.timeseries))
			if len(score) == 0:
				break
		return reseries,alljoin

	def run(self):
		rec_err=[]
		if self.network=='CNN':
			beatgan_CNN = BeatGAN_CNN(self.dataloader,seq_len=self.seq_len,input_size=self.input_size
									  ,rep_size=self.req_size,max_epoch=self.max_epoch,lambdaa=self.lambdaa)
			beatgan_CNN.fit()
			rec_diff, ori_ts, rec_ts, rec_err = beatgan_CNN.predict(self.testdataloader)
		elif self.network=='RNN':
			beatgan_RNN = BeatGAN_RNN(self.dataloader,seq_len=self.seq_len,input_size=self.input_size,lambdaa=self.lambdaa,
									  hidden_size=self.hidden_size,rep_size=self.req_size,max_epoch=self.max_epoch)
			beatgan_RNN.fit()
			rec_diff, ori_ts, rec_ts, rec_err = beatgan_RNN.predict(self.testdataloader)

		new_score = []
		window_size = 8
		for scores in rec_err:
			tmp_score = 0
			for i in range(0, len(scores), window_size):
				tmp_score = max(tmp_score, np.mean(scores[i:i + window_size]))
			new_score.append(tmp_score)
		score = np.argsort(new_score)
		score = np.flipud(score)
		reseries = self.series.copy()
		reseries.clear()
		for k in range(self.top_k):
			index = score[0]
			indext=index*self.stride_size
			for i in range(indext, indext + self.seq_len):
				reseries.timeseries[i].is_anomaly = True
			score = drop_score(score, index, self.seq_len,len(self.series.timeseries))
			if len(score) == 0:
				break
		return reseries

	'''def evaluate(self,score_threshold):
		reseries = self.series.copy()
		reseries.clear()
		th_num = (int)(score_threshold * self.score.size)
		score_sorted=np.sort(self.score)
		score_sorted=np.flipud(score_sorted)
		th_value = score_sorted[th_num-1]
		for i in range(self.score.size):
			if self.score[i]>=th_value:
				reseries.timeseries[i].is_anomaly = True
		return reseries'''
	def evaluate(self,k):

		reseries = self.series.copy()
		reseries.clear()
		score_sorted = np.argsort(self.score)
		score_sorted = np.flipud(score_sorted)

		for t in range(k):
			index = score_sorted[0]
			for i in range(index, min(index + self.seq_len, len(reseries))):
				reseries.timeseries[i].is_anomaly = True
			score_sorted = drop_score(score_sorted, index, self.seq_len, len(score_sorted))
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

