from typing import Dict
from algorithms.algorithm import algorithm
from entity import *


class algExp(algorithm):

    def __init__(self):
        self.series = None

    def init(self, args: Dict, series: timeSeriesUni):
        # print(args)
        self.series = series

    def run(self):
        # self.series.print()
        return self.series
