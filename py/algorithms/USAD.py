from .TranADs.alg import *

from .algorithm import *
from .TranADs.src.constants import constants


class USAD(machineLearningAlgorithm):
    def __init__(self):
        super(USAD, self).__init__()

        self.trainingSeries = None
        self.labels = None
        self.testO = None
        self.testD = None
        self.accuracy_list = None
        self.trainO = None
        self.trainD = None
        self.epoch = None
        self.scheduler = None
        self.optimizer = None
        self.model = None
        self.alg = "USAD"

    def init(self, args, series: timeSeries, trainingSeries: timeSeries):
        self.series = series
        self.trainingSeries = trainingSeries
        constants.lr[self.alg] = args["lr"]
        train_loader, test_loader, self.labels = self.load_datasetwithSeries(self.series, trainingSeries)
        self.series.clear()
        self.model, self.optimizer, self.scheduler, self.epoch, self.accuracy_list = load_model(
            self.alg,
            self.labels.shape[1])
        ## Prepare data
        self.trainD, self.testD = next(iter(train_loader)), next(iter(test_loader))
        self.trainO, self.testO = self.trainD, self.testD
        if self.model.name in ['Attention', 'DAGMM', 'USAD', 'MSCRED', 'CAE_M', 'GDN', 'MTAD_GAT',
                               'MAD_GAN'] or 'TranAD' in self.model.name:
            self.trainD, self.testD = convert_to_windows(self.trainD, self.model), convert_to_windows(self.testD
                                                                                                      , self.model)

    def load_datasetwithSeries(self, series, trainingSeries):
        loader = [0, 1, 2]
        loader[0] = np.zeros([len(trainingSeries.timeseries), trainingSeries.dim])
        loader[1] = np.zeros([len(series.timeseries), series.dim])
        loader[2] = np.zeros_like(loader[1])
        for i in range(len(trainingSeries.timeseries)):
            loader[0][i] = trainingSeries.timeseries[i].obsVal
        for i in range(len(series.timeseries)):
            loader[1][i] = series.timeseries[i].obsVal
            if series.timeseries[i].is_anomaly:
                loader[2][i, :] = 1

        # TDB:if removed precision come to 0
        min_temp, max_temp = np.min(loader[0]), np.max(loader[0])
        loader[0] = (loader[0] - min_temp) / (max_temp - min_temp)
        min_temp, max_temp = np.min(loader[1]), np.max(loader[1])
        loader[1] = (loader[1] - min_temp) / (max_temp - min_temp)

        # loader[0] = cut_array(0.2, loader[0])
        train_loader = DataLoader(loader[0], batch_size=loader[0].shape[0])
        test_loader = DataLoader(loader[1], batch_size=loader[1].shape[0])
        labels = loader[2]
        return train_loader, test_loader, labels

    def changeData(self, series: timeSeries):
        print(self.alg + " changed data")
        self.series = series
        train_loader, test_loader, self.labels = self.load_datasetwithSeries(self.series, self.trainingSeries)
        self.series.clear()
        self.trainD, self.testD = next(iter(train_loader)), next(iter(test_loader))
        self.trainO, self.testO = self.trainD, self.testD
        if self.model.name in ['Attention', 'DAGMM', 'USAD', 'MSCRED', 'CAE_M', 'GDN', 'MTAD_GAT',
                               'MAD_GAN'] or 'TranAD' in self.model.name:
            self.trainD, self.testD = convert_to_windows(self.trainD, self.model), convert_to_windows(self.testD
                                                                                                      , self.model)

    def training(self):
        ### Training phase
        num_epochs = 5
        start = time()
        for e in list(range(self.epoch + 1, self.epoch + num_epochs + 1)):
            lossT, lr = backprop(e, self.model, self.trainD, self.trainO, self.optimizer, self.scheduler)
            self.accuracy_list.append((lossT, lr))
        return time() - start

    def run(self):
        ### Testing phase
        torch.zero_grad = True
        self.model.eval()
        loss, y_pred = backprop(0, self.model, self.testD, self.testO, self.optimizer, self.scheduler, training=False)

        # lossT, _ = backprop(0, self.model, self.trainD, self.trainO, self.optimizer, self.scheduler, training=False)

        t, th = bf_search(loss, self.labels, start=0., end=0.9,
                          step_num=int((0.9 - 0.) / 0.0001),
                          display_freq=50)
        preds = []
        for i in range(loss.shape[1]):
            l, ls = loss[:, i], self.labels[:, i]
            lmin, lmax = np.min(l), np.max(l)
            l = (l - lmin) / (lmax - lmin)
            pred = l > th
            preds.append(pred)
        #   df = df.append(result, ignore_index=True)

        for i in range(len(preds[0])):
            for p in preds:
                self.series.timeseries[i].is_anomaly = self.series.timeseries[i].is_anomaly or p[i]
        return self.series
