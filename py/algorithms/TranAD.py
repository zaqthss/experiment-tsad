import copy

from .TranADs.alg import *
from .TranADs.src.constants import constants
from .algorithm import *


class TranAD(TranADAlagorithm):
    def __init__(self):
        super(TranAD, self).__init__()
        self.accuracy_list = None
        self.epoch = None
        self.scheduler = None
        self.optimizer = None
        self.model = None
        self.alg = "TranAD"


