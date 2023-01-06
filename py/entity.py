import copy

class timePoint:
    def __init__(self):
        self.timestamp = None
        self.id = None
        self.is_anomaly = None
        self.truthVal = None
        self.obsVal = None
        self.predictVal = None
        self.observe = None
        self.truth = None


    def print(self):
        print(f"id:{self.id}\ttimestamp:{self.timestamp}"
              f"is_anomaly:{self.is_anomaly}")



class timePointUni(timePoint):
    def __init__(self):
        super(timePointUni, self).__init__()


    def print(self):
        print(f"id:{self.id}\ttimestamp:{self.timestamp}\ttruthVal:{self.truthVal}\tobsVal:{self.obsVal}\t"
              f"predictVal:{self.predictVal}\tobserve:{self.observe}\ttruth:{self.truth}\t"
              f"is_anomaly:{self.is_anomaly}")


class timePointMul(timePoint):
    def __init__(self):
        super(timePointMul, self).__init__()
        self.dim = None


    def print(self):
        print(f"id:{self.id}\ttimestamp:{self.timestamp}\tdim:{self.dim}\ttruthVal:{self.truthVal}\t"
              f"obsVal:{self.obsVal}\tpredictVal:{self.predictVal}\t"
              f"observe:{self.observe}\ttruth:{self.truth}\t"
              f"is_anomaly:{self.is_anomaly}")


class timeSeries:
    def __init__(self):
        self.timeseries = []

    def __len__(self):
        return len(self.timeseries)

    def clear(self):
        for tp in self.timeseries:
            tp.is_anomaly = False

    def print(self):
        for tp in self.timeseries:
            tp.print()

    def copy(self):
        cp = timeSeries()
        cp.timeseries = copy.deepcopy(self.timeseries)
        return cp


class timeSeriesUni(timeSeries):
    def __init__(self):
        super(timeSeriesUni, self).__init__()
        self.timeseries = []


class timeSeriesMul(timeSeries):
    def __init__(self):
        super(timeSeriesMul, self).__init__()
        self.timeseries = []
        self.dim = -1

    def getsubdim(self, dim):
        series = timeSeriesMul()
        series.timeseries = copy.deepcopy(self.timeseries)
        series.dim = dim
        for tp in series.timeseries:
            tp.truthVal = tp.truthVal[0:dim]
            tp.obsVal = tp.obsVal[0:dim]
            tp.predictVal = tp.predictVal[0:dim]
            tp.observe = tp.observe[0:dim]
            tp.truth = tp.truth[0:dim]
        return series

    def getunibydim(self, dim):
        ts = timeSeriesUni()
        for tp in self.timeseries:
            new_tp=timePointUni()
            new_tp.id=tp.id
            new_tp.truthVal = tp.truthVal[dim]
            new_tp.obsVal = tp.obsVal[dim]
            new_tp.predictVal = tp.predictVal[dim]
            new_tp.observe = tp.observe[dim]
            new_tp.truth = tp.truth[dim]
            new_tp.is_anomaly=tp.is_anomaly
            ts.timeseries.append(new_tp)
        return ts

    def getMulbydim(self, dim):
        ts = timeSeriesMul()
        ts.dim = 1
        for tp in self.timeseries:
            new_tp=timePointMul()
            new_tp.id=tp.id
            arr=[]
            arr.append(tp.truthVal[dim])
            new_tp.truthVal = arr.copy()
            new_tp.obsVal = arr.copy()
            new_tp.predictVal = arr.copy()
            new_tp.observe = arr.copy()
            new_tp.truth = arr.copy()
            new_tp.is_anomaly=tp.is_anomaly
            ts.timeseries.append(new_tp)
        return ts

    def convert(self):
        tsArray=[]
        for d in range(self.dim):
            ts=self.getunibydim(d)
            tsArray.append(ts)
        return tsArray

    def converttoMul(self):
        tsArray=[]
        for d in range(self.dim):
            ts=self.getMulbydim(d)
            tsArray.append(ts)
        return tsArray

    def copy(self):
        cp = super(timeSeriesMul, self).copy()
        cp.dim = self.dim
        return cp
