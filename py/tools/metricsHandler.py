from entity import *


class metrics:
    def init(self, originalseries: timeSeries, series: timeSeries):
        print("This method need to be implement by child !")

    def precision(self):
        print("This method need to be implement by child !")

    def recall(self):
        print("This method need to be implement by child !")

    def accuracy(self):
        print("This method need to be implement by child !")

    def sensitive(self):
        print("This method need to be implement by child !")

    def specificity(self):
        print("This method need to be implement by child !")

    def errorrate(self):
        print("This method need to be implement by child !")

    def fmeasure(self):
        print("This method need to be implement by child !")


class pointMetrics(metrics):
    def __init__(self):
        self.tp = 0
        self.tn = 0
        self.fp = 0
        self.fn = 0

    def init(self, originalseries: timeSeries, series: timeSeries):
        if len(originalseries) != len(series):
            print("Series length error")
            return None
        real = originalseries.timeseries
        predict = series.timeseries
        for i in range(len(series)):
            if real[i].is_anomaly and predict[i].is_anomaly:
                self.tp = self.tp + 1
            elif real[i].is_anomaly and (not predict[i].is_anomaly):
                self.fn = self.fn + 1
            elif (not real[i].is_anomaly) and predict[i].is_anomaly:
                self.fp = self.fp + 1
            else:
                self.tn = self.tn + 1

    def precision(self):
        return 1.0 * self.tp / (self.tp + self.fp)

    def recall(self):
        return 1.0 * self.tp / (self.tp + self.fn)

    def accuracy(self):
        return 1.0 * (self.tp + self.tn) / (self.tp + self.tn + self.fp + self.fn)

    def sensitive(self):
        return 1.0 * self.tp / (self.tp + self.fp)

    def specificity(self):
        return 1.0 * self.tn / (self.tn + self.fn)

    def errorrate(self):
        return 1.0 * (self.fp + self.fn) / (self.tp + self.tn + self.fp + self.fn)

    def fmeasure(self):
        if self.precision() + self.recall() > 0:
            return 2 * self.precision() * self.recall() / (self.precision() + self.recall())
        else:
            return -1



class rangeMetrics(metrics):

    def __init__(self, alpha=0.0, bias="flat"):
        self.recallValue = 0.0
        self.precisionValue = 0.0
        self.fmeasureValue=0.0
        self.realAnomaly = {}
        self.rseq = {}
        self.pseq = {}
        self.hitR = {}
        self.hitP = {}
        self.rSize = 0
        self.pSize = 0
        self.pos_bias = bias
        self.alpha = alpha

    def init(self, originalseries: timeSeries, series: timeSeries):
        self.recallValue = 0.0
        self.precisionValue = 0.0
        self.fmeasureValue=0.0
        self.overlaps = {}
        self.rseq = self.getAnomalySequences(originalseries)
        self.pseq = self.getAnomalySequences(series)
        self.rSize = len(self.rseq)
        self.pSize = len(self.pseq)

        self.createOverlapSet(self.rseq, self.pseq)

        for rIndex in range(0, self.rSize):
            self.recallValue = self.recallValue+self.alpha * self.calcExistenceReward(rIndex) + (
                    1 - self.alpha) * self.calcOverlapReward(
                rIndex)
        self.recallValue = self.recallValue / self.rSize
        for pIndex in range(0, self.pSize):
            cardinalityFactor = self.gammaP(pIndex)
            mul = 0;
            for rIndex in range(0, self.rSize):
                key = self.calcKey(rIndex, pIndex)
                mul += self.omega(self.pseq[pIndex], self.overlaps.get(key))

            assert mul <= 1, "mul must <= 1"
            self.precisionValue += cardinalityFactor * mul

        self.precisionValue = self.precisionValue / self.pSize
        if self.precisionValue + self.recallValue > 0:
            self.fmeasureValue=2 * self.precisionValue * self.recallValue / (self.precisionValue + self.recallValue)
        else:
            self.fmeasureValue=0

    def computeMetric(self, realAnomaly,predictAnomaly):
        self.recallValue = 0.0
        self.precisionValue = 0.0
        self.fmeasureValue=0.0
        self.overlaps = {}
        self.rseq = realAnomaly
        self.pseq = predictAnomaly
        self.rSize = len(self.rseq)
        self.pSize = len(self.pseq)

        self.createOverlapSet(self.rseq, self.pseq)

        for rIndex in range(0, self.rSize):
            self.recallValue = self.recallValue+self.alpha * self.calcExistenceReward(rIndex) + (
                    1 - self.alpha) * self.calcOverlapReward(
                rIndex)
        self.recallValue = self.recallValue / self.rSize
        for pIndex in range(0, self.pSize):
            cardinalityFactor = self.gammaP(pIndex)
            mul = 0;
            for rIndex in range(0, self.rSize):
                key = self.calcKey(rIndex, pIndex)
                mul += self.omega(self.pseq[pIndex], self.overlaps.get(key))

            assert mul <= 1, "mul must <= 1"
            self.precisionValue += cardinalityFactor * mul

        self.precisionValue = self.precisionValue / self.pSize
        if self.precisionValue + self.recallValue > 0:
            self.fmeasureValue=2 * self.precisionValue * self.recallValue / (self.precisionValue + self.recallValue)
        else:
            self.fmeasureValue=0

    def getAnomalySequences(self, series: timeSeries):
        currSet = []
        totalSet = []
        series = series.timeseries
        for id in range(0, len(series)):
            if series[id].is_anomaly:
                currSet.append(series[id].id)
            else:
                if len(currSet) > 0:
                    totalSet.append(currSet)
                    currSet = []
        if len(currSet) > 0:
            totalSet.append(currSet)
        return totalSet

    def createOverlapSet(self, r, p):
        self.hitR = [0] * self.rSize
        self.hitP = [0] * self.pSize
        for rIndex in range(0, self.rSize, 1):
            for pIndex in range(0, self.pSize, 1):
                overlap = set(r[rIndex]) & set(p[pIndex])
                if len(overlap) > 0:
                    self.overlaps[self.calcKey(rIndex, pIndex)] = overlap
                    self.hitR[rIndex] = self.hitR[rIndex] + 1
                    self.hitP[pIndex] = self.hitP[pIndex] + 1

    def calcKey(self, rIndex, pIndex):
        return rIndex * self.pSize + pIndex

    def calcExistenceReward(self, rIndex):
        if self.hitR[rIndex] > 0:
            return 1
        else:
            return 0

    def calcOverlapReward(self, rIndex):
        cardinalityFactor = self.gammaR(rIndex)
        mul = 0;
        for pIndex in range(0, self.pSize):
            key = self.calcKey(rIndex, pIndex)
            mul = mul + self.omega(self.rseq[rIndex], self.overlaps.get(key))
        assert mul <= 1, "mul must <= 1"
        return cardinalityFactor * mul

    def gammaR(self, index):
        return 1.0

    def gammaP(self, index):
        return 1.0

    def omega(self, ranges, overlap):
        if overlap is None:
            return 0
        myValue = 0
        maxValue = 0
        anomalyLength = len(ranges)
        rangeList = list(ranges)
        for i in range(0, anomalyLength):
            bias = self.delta(i, anomalyLength)
            maxValue = maxValue + bias
            if rangeList[i] in overlap:
                myValue += bias
        return myValue / maxValue

    def delta(self, i, anomalyLength):
        if self.pos_bias == "flat":
            return 1
        elif self.pos_bias == "FRONT_END":
            # since in the paper i begins with 1, so we remove the +1
            # return anomalyLength - i + 1;
            return anomalyLength - i
        elif self.pos_bias == "BACK_END":
            return i
        elif self.pos_bias == "MIDDLE":
            if i <= anomalyLength / 2:
                return i
            else:
                # return anomalyLength - i + 1;
                return anomalyLength - i
        return 1  # will not be hit

    def precision(self):
        return self.precisionValue

    def recall(self):
        return self.recallValue
    def fmeasure(self):
        return self.fmeasureValue
