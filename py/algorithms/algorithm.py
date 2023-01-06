from entity import timeSeries


class algorithm:
    def __init__(self):
        self.series = timeSeries()

    def init(self, args, series: timeSeries):
        print("Please extend this class and override init and run function !")

    def run(self):
        print("Please extend this class and override init and run function !")
        return self.series


class machineLearningAlgorithm(algorithm):

    def init(self, args, series: timeSeries, trainingSeries: timeSeries):
        print("Please extend this class and override init and run function !")

    def training(self):
        print("Please extend this class and override init and run function !")
