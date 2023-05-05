from .algorithm import *


class GDN(TranADAlagorithm):
    def __init__(self):
        super(GDN, self).__init__()
        self.accuracy_list = None
        self.epoch = None
        self.scheduler = None
        self.optimizer = None
        self.model = None
        self.alg = "GDN"

