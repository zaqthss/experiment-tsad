import copy
from time import time

from entity import timeSeries
from .TranADsWithPOT.alg import *
from .TranADsWithPOT.src.constants import constants
from .TranADsWithPOT.src.pot import pot_eval
from .algorithm import MachineLearningAlgorithm


class TranADWithPOT(MachineLearningAlgorithm):
    def __init__(self):
        super(TranADWithPOT, self).__init__()
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
        self.alg = "TranAD"
        self.num_epochs = None

    def init(self, args, series: timeSeries, trainingSeries: timeSeries, validSeries=None):
        self.series = series
        self.trainingSeries = trainingSeries
        constants.Hyperparameters[self.alg] = copy.deepcopy(args)
        self.num_epochs = args["epoch"]
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

    def load_datasetwithSeries(self, series, trainingSeries, validSeries: timeSeries=None):
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

    def training(self,writelossrate=False):
        ### Training phase

        start = time()
        for e in list(range(self.epoch + 1, self.epoch + self.num_epochs + 1)):
            lossT, lr = backprop(e, self.model, self.trainD, self.trainO, self.optimizer, self.scheduler)
            self.accuracy_list.append((lossT, lr))
        return time() - start

    def run(self):
        ### Testing phase
        torch.zero_grad = True
        self.model.eval()
        loss, y_pred = backprop(0, self.model, self.testD, self.testO, self.optimizer, self.scheduler, training=False)

        lossT, _ = backprop(0, self.model, self.trainD, self.trainO, self.optimizer, self.scheduler, training=False)
        preds = []
        for i in range(loss.shape[1]):
            lt, l, ls = lossT[:, i], loss[:, i], self.labels[:, i]
            result, pred = pot_eval(lt, l, ls, self.alg);
            preds.append(pred)

        # preds = np.concatenate([i.reshape(-1, 1) + 0 for i in preds], axis=1)
        # pd.DataFrame(preds, columns=[str(i) for i in range(10)]).to_csv('labels.csv')
        lossTfinal, lossFinal = np.mean(lossT, axis=1), np.mean(loss, axis=1)
        labelsFinal = (np.sum(self.labels, axis=1) >= 1) + 0
        result, _ = pot_eval(lossTfinal, lossFinal, labelsFinal,self.alg)
        result.update(hit_att(loss, self.labels))
        result.update(ndcg(loss, self.labels))

        print(result)

        for i in range(len(preds[0])):
            for p in preds:
                self.series.timeseries[i].is_anomaly = self.series.timeseries[i].is_anomaly or p[i]
        return self.series
