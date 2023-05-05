import copy

import numpy

from algorithms.TranADs.alg import *
from algorithms.TranADs.src.constants import constants
from entity import timeSeries
from tools import fileHandler


class Algorithm:
    def __init__(self):
        self.testSeries = timeSeries()

    def init(self, args, series: timeSeries):
        print("Please extend this class and override init and run function !")

    def run(self):
        print("Please extend this class and override init and run function !")
        return self.testSeries


class MachineLearningAlgorithm(Algorithm):

    def init(self, args, testSeries: timeSeries, trainingSeries: timeSeries, validSeries: timeSeries):
        print("Please extend this class and override init and run function !")

    def training(self, writelossrate=True):
        print("Please extend this class and override init and run function !")


class TranADAlagorithm(MachineLearningAlgorithm):
    def __init__(self):
        self.trainingSeries = None
        self.testSeries = None
        self.testLabels = None
        self.validSeries = None
        self.validLabels = None

        self.trainO = None
        self.trainD = None
        self.testO = None
        self.testD = None
        self.validO = None
        self.validD = None
        self.currScore = None
        self.num_epochs = None
        self.args = None

    def init(self, args, testSeries: timeSeries, trainingSeries: timeSeries, validSeries: timeSeries):
        self.args = args
        constants.Hyperparameters[self.alg] = copy.deepcopy(args)
        self.trainingSeries = trainingSeries
        self.testSeries = testSeries
        self.validSeries = validSeries
        self.num_epochs = args["epoch"]
        train_loader, test_loader, self.testLabels, valid_loader, self.validLabels = self.load_datasetwithSeries(
            self.testSeries, trainingSeries, validSeries)
        self.testSeries.clear()
        self.model, self.optimizer, self.scheduler, self.epoch, self.accuracy_list = load_model(
            self.alg, self.testLabels.shape[1])
        ## Prepare data
        self.trainD, self.testD, self.validD = next(iter(train_loader)), next(iter(test_loader)), next(
            iter(valid_loader))
        self.trainO, self.testO, self.validO = self.trainD, self.testD, self.validD
        if self.model.name in ['Attention', 'DAGMM', 'USAD', 'MSCRED', 'CAE_M', 'GDN', 'MTAD_GAT',
                               'MAD_GAN'] or 'TranAD' in self.model.name:
            self.trainD, self.testD, self.validD = convert_to_windows(self.trainD, self.model), convert_to_windows(
                self.testD, self.model), convert_to_windows(self.validD, self.model)

    def load_datasetwithSeries(self, testSeries, trainingSeries, validSeries):
        loader = [0, 1, 2, 3, 4]
        loader[0] = np.zeros([len(trainingSeries.timeseries), trainingSeries.dim])
        loader[1] = np.zeros([len(testSeries.timeseries), testSeries.dim])
        loader[2] = np.zeros_like(loader[1])
        loader[3] = np.zeros([len(validSeries.timeseries), validSeries.dim])
        loader[4] = np.zeros_like(loader[3])

        for i in range(len(trainingSeries.timeseries)):
            loader[0][i] = trainingSeries.timeseries[i].obsVal
        for i in range(len(testSeries.timeseries)):
            loader[1][i] = testSeries.timeseries[i].obsVal
            if testSeries.timeseries[i].is_anomaly:
                loader[2][i, :] = 1
        for i in range(len(validSeries.timeseries)):
            loader[3][i] = validSeries.timeseries[i].obsVal
            if validSeries.timeseries[i].is_anomaly:
                loader[4][i, :] = 1
        # TDB:if removed precision come to 0

        min_temp, max_temp = np.min(loader[0]), np.max(loader[0])
        loader[0] = (loader[0] - min_temp) / (max_temp - min_temp)
        min_temp, max_temp = np.min(loader[1]), np.max(loader[1])
        loader[1] = (loader[1] - min_temp) / (max_temp - min_temp)
        min_temp, max_temp = np.min(loader[3]), np.max(loader[3])
        loader[3] = (loader[3] - min_temp) / (max_temp - min_temp)

        # loader[0] = cut_array(0.2, loader[0])
        train_loader = DataLoader(loader[0], batch_size=loader[0].shape[0])
        test_loader = DataLoader(loader[1], batch_size=loader[1].shape[0])
        t_labels = loader[2]
        valid_loader = DataLoader(loader[3], batch_size=loader[3].shape[0])
        v_labels = loader[4]
        return train_loader, test_loader, t_labels, valid_loader, v_labels

    def changeData(self, testSeries: timeSeries, validSeries: timeSeries):
        print(self.alg + " changed data")
        self.testSeries = testSeries
        self.validSeries = validSeries
        train_loader, test_loader, self.testLabels, valid_loader, self.validLabels = self.load_datasetwithSeries(
            self.testSeries, self.trainingSeries, self.validSeries)
        self.testSeries.clear()
        self.trainD, self.testD, self.validD = next(iter(train_loader)), next(iter(test_loader)), next(
            iter(valid_loader))
        self.trainO, self.testO, self.validO = self.trainD, self.testD, self.validD
        if self.model.name in ['Attention', 'DAGMM', 'USAD', 'MSCRED', 'CAE_M', 'GDN', 'MTAD_GAT',
                               'MAD_GAN'] or 'TranAD' in self.model.name:
            self.trainD, self.testD, self.validD = convert_to_windows(self.trainD, self.model), convert_to_windows(
                self.testD, self.model), convert_to_windows(self.validD, self.model)

    def training(self, writelossrate=True):
        ### Training phase

        start = time()
        for e in list(range(self.epoch + 1, self.epoch + self.num_epochs + 1)):
            lossT, lr = backprop(e, self.model, self.trainD, self.trainO, self.optimizer, self.scheduler,
                                 writeLR=writelossrate)
            self.accuracy_list.append((lossT, lr))
        return time() - start

    def run(self):
        ### Testing phase
        torch.zero_grad = True
        self.model.eval()
        loss, y_pred = backprop(0, self.model, self.testD, self.testO, self.optimizer, self.scheduler, training=False)
        vloss, vy_pred = backprop(0, self.model, self.validD, self.validO, self.optimizer, self.scheduler,
                                  training=False)
        # lossT, _ = backprop(0, self.model, self.trainD, self.trainO, self.optimizer, self.scheduler, training=False)

        t, th = bf_search(vloss, self.validLabels, start=0., end=0.9, step_num=int((0.9 - 0.) / 0.0001),
                          display_freq=50)
        tt, tth = bf_search(loss, self.testLabels, start=0., end=0.9, step_num=int((0.9 - 0.) / 0.0001),
                            display_freq=50)
        # fileHandler.writeTemporal("threshold_middle", ",".join(
        #    [self.args["dsName"], self.alg, "th", str(th), str(t[0]), "tth", str(tth), str(tt[0])]))

        preds = []

        self.currScore = numpy.max(copy.deepcopy(loss), axis=1).argsort()
        for i in range(loss.shape[1]):
            l = loss[:, i]
            lmin, lmax = np.min(l), np.max(l)
            l = (l - lmin) / (lmax - lmin)
            pred = l > th
            preds.append(pred)
        #   df = df.append(result, ignore_index=True)

        for i in range(len(preds[0])):
            for p in preds:
                self.testSeries.timeseries[i].is_anomaly = self.testSeries.timeseries[i].is_anomaly or p[i]
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
