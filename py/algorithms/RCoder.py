import copy

import numpy
import numpy as np
import pandas as pd
from sklearn.preprocessing import MinMaxScaler

from algorithms.RCoders.RANSynCoders import RANSynCoders
from algorithms.RCoders.eval_methods import bf_search
from algorithms.TranADs.src.constants import constants
from algorithms.algorithm import MachineLearningAlgorithm
from entity import timeSeriesMul


class RCoder(MachineLearningAlgorithm):
    def __init__(self):
        super(RCoder, self).__init__()
        self.currScore = None
        self.model = None
        self.trainingSeries = None
        self.epochs = None
        self.freq_warmup = None
        self.batch_size = None
        self.sin_warmup = None
        self.x_test = None
        self.y_test = None
        self.t_test = None
        self.x_test_scaled = None
        self.x_train = None
        self.t_train = None
        self.x_train_scaled = None
        self.x_valid = None
        self.y_valid = None
        self.t_valid = None
        self.x_valid_scaled = None
        self.N = None
        self.encoder_layers = None
        self.z = None
        self.decoder_layers = None
        self.activation = None
        self.output_activation = None
        self.delta = None
        self.S = None
        self.alg = "RCoder"

    def init(self, args, testSeries: timeSeriesMul, trainingSeries: timeSeriesMul, validSeries: timeSeriesMul):
        self.testSeries = testSeries
        self.trainingSeries = trainingSeries
        self.validSeries = validSeries
        self.x_train, _ = self.series_to_dataframe(trainingSeries)
        self.x_train.fillna(0, inplace=True)  # imputes missing values
        self.x_test, self.y_test = self.series_to_dataframe(testSeries)
        self.x_valid, self.y_valid = self.series_to_dataframe(validSeries)
        self.testSeries.clear()
        self.t_train = np.tile(self.x_train.index.values.reshape(-1, 1), (1, self.x_train.shape[1]))
        self.t_test = np.tile(self.x_test.index.values.reshape(-1, 1), (1, self.x_train.shape[1]))
        self.t_valid = np.tile(self.x_valid.index.values.reshape(-1, 1), (1, self.x_train.shape[1]))

        xscaler = MinMaxScaler()
        self.x_train_scaled = xscaler.fit_transform(self.x_train.values)
        self.x_test_scaled = xscaler.transform(self.x_test.values)
        self.x_valid_scaled = xscaler.transform(self.x_valid.values)

        self.N = 5 * round(
            (self.x_train.shape[1] / 3) / 5)  # 10 for both bootstrap sample size and number of estimators
        if self.N < 1:
            self.N = self.x_train.shape[1] - 1
        self.encoder_layers = 1  # number of hidden layers for each encoder
        self.decoder_layers = 2  # number of hidden layers for each decoder
        self.z = int((self.N / 2) - 1)  # size of latent space
        self.activation = 'relu'
        self.output_activation = 'sigmoid'
        self.S = args["S"]  # 5  # Number of frequency components to fit to input signals
        self.delta = args["delta"]  # 0.05
        self.batch_size = args["batch_size"]  # 180
        self.freq_warmup = args["freq_warmup"]  # 5  # pre-training epochs 5
        self.sin_warmup = args["sin_warmup"]  # 5  # synchronization pre-training 5
        self.epochs = args["epoch"]  # 10 #10
        constants.Hyperparameters["Rcoder"] = copy.deepcopy(args)
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

    def training(self, writelossrate=True):
        self.model.fit(self.x_train_scaled, self.t_train, epochs=self.epochs, batch_size=self.batch_size,
                       freq_warmup=self.freq_warmup, sin_warmup=self.sin_warmup, writeLR=writelossrate)

    def changeData(self, testSeries: timeSeriesMul, validSeries: timeSeriesMul):
        print(self.alg + " changed data")
        self.testSeries = testSeries
        self.validSeries = validSeries

        self.x_test, self.y_test = self.series_to_dataframe(self.testSeries)
        self.testSeries.clear()
        self.t_test = np.tile(self.x_test.index.values.reshape(-1, 1), (1, self.x_train.shape[1]))

        self.x_valid, self.y_valid = self.series_to_dataframe(self.validSeries)
        self.t_valid = np.tile(self.x_valid.index.values.reshape(-1, 1), (1, self.x_train.shape[1]))

        xscaler = MinMaxScaler()
        self.x_train_scaled = xscaler.fit_transform(self.x_train.values)
        self.x_test_scaled = xscaler.transform(self.x_test.values)
        self.x_valid_scaled = xscaler.transform(self.x_valid.values)

    def run(self):
        test_sins, test_synched, test_upper, test_lower = self.model.predict(self.x_test_scaled, self.t_test,
                                                                             batch_size=self.batch_size * 10)
        test_synched_tiles = np.tile(test_synched.reshape(test_synched.shape[0], 1, test_synched.shape[1]),
                                     (1, self.N, 1))
        test_result = np.where((test_synched_tiles < test_lower) | (test_synched_tiles > test_upper), 1, 0)
        test_inference = np.mean(np.mean(test_result, axis=1), axis=1)

        valid_sins, valid_synched, valid_upper, valid_lower = self.model.predict(self.x_valid_scaled, self.t_valid,
                                                                                 batch_size=self.batch_size * 10)
        valid_synched_tiles = np.tile(valid_synched.reshape(valid_synched.shape[0], 1, valid_synched.shape[1]),
                                      (1, self.N, 1))
        valid_result = np.where((valid_synched_tiles < valid_lower) | (valid_synched_tiles > valid_upper), 1, 0)
        valid_inference = np.mean(np.mean(valid_result, axis=1), axis=1)

        t, th = bf_search(valid_inference.reshape(-1, 1), self.y_valid.values, start=0., end=0.9,
                          step_num=int((0.9 - 0.) / 0.0001), display_freq=5000)

        self.currScore = copy.deepcopy(test_inference).argsort()
        result = test_inference > th
        for i in range(len(result)):
            self.testSeries.timeseries[i].is_anomaly = result[i]

        return self.testSeries

    def getResultWithThreshold(self, th):
        series = self.testSeries.copy()
        series.clear()

        #        for i in range(self.currScore.shape[1]):
        #            l = self.currScore[:, i]
        #            lmin, lmax = np.min(l), np.max(l)
        #            l = (l - lmin) / (lmax - lmin)
        #            pred = l > th
        #            preds.append(pred)
        #           df = df.append(result, ignore_index=True)
        if th == 0:
            return series
        size = int(th * len(self.currScore))
        anomaly = self.currScore[-size:]
        for i in anomaly:
            series.timeseries[i].is_anomaly = True
        return series
        """
        series = self.testSeries.copy()
        series.clear()
        result = self.currScore > th
        for i in range(len(result)):
            series.timeseries[i].is_anomaly = result[i]
            

        return series
        """