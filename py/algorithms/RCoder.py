import math

from algorithms.RCoders.eval_methods import bf_search
from algorithms.RCoders.RANSynCoders import RANSynCoders
from sklearn.preprocessing import MinMaxScaler
import matplotlib.pyplot as plt
import numpy as np
import os
import pandas as pd

from algorithms.algorithm import machineLearningAlgorithm
from entity import timeSeries, timeSeriesMul


class RCoder(machineLearningAlgorithm):
    def __init__(self):
        super(RCoder, self).__init__()
        self.model = None
        self.tseries = None
        self.epochs = None
        self.freq_warmup = None
        self.batch_size = None
        self.sin_warmup = None
        self.x_test_scaled = None
        self.x_train_scaled = None
        self.x_test = None
        self.y_test = None
        self.t_train = None
        self.t_test = None
        self.x_train = None
        self.N = None
        self.encoder_layers = None
        self.z = None
        self.decoder_layers = None
        self.activation = None
        self.output_activation = None
        self.delta = None
        self.S = None
        self.alg = "RCoder"

    def init(self, args, series: timeSeriesMul, trainingSeries: timeSeriesMul):
        self.series = series
        self.tseries = trainingSeries
        self.x_train, _ = self.series_to_dataframe(trainingSeries)

        self.x_train.fillna(0, inplace=True)  # imputes missing values

        self.x_test, self.y_test = self.series_to_dataframe(series)
        self.series.clear()
        self.t_train = np.tile(self.x_train.index.values.reshape(-1, 1), (1, self.x_train.shape[1]))
        self.t_test = np.tile(self.x_test.index.values.reshape(-1, 1), (1, self.x_train.shape[1]))

        xscaler = MinMaxScaler()
        self.x_train_scaled = xscaler.fit_transform(self.x_train.values)
        self.x_test_scaled = xscaler.transform(self.x_test.values)

        self.N = 5 * round(
            (self.x_train.shape[1] / 3) / 5)  # 10 for both bootstrap sample size and number of estimators
        if self.N <1:
            self.N = self.x_train.shape[1]-1
        self.encoder_layers = 1  # number of hidden layers for each encoder
        self.decoder_layers = 2  # number of hidden layers for each decoder
        self.z = int((self.N / 2) - 1)  # size of latent space
        self.activation = 'relu'
        self.output_activation = 'sigmoid'
        self.S = 5  # Number of frequency components to fit to input signals
        self.delta = 0.05
        self.batch_size = 180
        self.freq_warmup = 5  # pre-training epochs 5
        self.sin_warmup = 5  # synchronization pre-training 5
        self.epochs = 10 #10

        self.model = RANSynCoders(
            n_estimators=self.N,
            max_features=self.N,
            encoding_depth=self.encoder_layers,
            latent_dim=self.z,
            decoding_depth=self.decoder_layers,
            activation=self.activation,
            output_activation=self.output_activation,
            delta=self.delta,
            synchronize=True,
            max_freqs=self.S,
        )

    def series_to_dataframe(self, series: timeSeriesMul):
        columns = list(range(series.dim))
        data = []
        label = []
        for tp in series.timeseries:
            data.append(tp.obsVal)
            if tp.is_anomaly:
                label.append(True)
            else:
                label.append(False)
        return pd.DataFrame(data, columns=columns), pd.DataFrame(label, columns=["label"]),

    def training(self):
        self.model.fit(self.x_train_scaled, self.t_train, epochs=self.epochs, batch_size=self.batch_size,
                       freq_warmup=self.freq_warmup, sin_warmup=self.sin_warmup)

    def changeData(self, series: timeSeriesMul):
        print(self.alg + " changed data")
        self.series = series

        self.x_test, self.y_test = self.series_to_dataframe(self.series)
        self.series.clear()
        self.t_test = np.tile(self.x_test.index.values.reshape(-1, 1), (1, self.x_train.shape[1]))

        xscaler = MinMaxScaler()
        self.x_train_scaled = xscaler.fit_transform(self.x_train.values)
        self.x_test_scaled = xscaler.transform(self.x_test.values)

    def run(self):

        sins, synched, upper, lower = self.model.predict(self.x_test_scaled, self.t_test, batch_size=self.batch_size * 10)

        synched_tiles = np.tile(synched.reshape(synched.shape[0], 1, synched.shape[1]), (1, self.N, 1))
        result = np.where((synched_tiles < lower) | (synched_tiles > upper), 1, 0)
        inference = np.mean(np.mean(result, axis=1), axis=1)
        t, th = bf_search(inference.reshape(-1, 1), self.y_test.values, start=0., end=0.9, step_num=int((0.9 - 0.) / 0.0001),
                          display_freq=5000)

        result = inference>th
        for i in range(len(result)):
            self.series.timeseries[i].is_anomaly = result[i]

        return self.series
