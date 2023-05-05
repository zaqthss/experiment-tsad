import copy

from .TranADs.alg import *
from .TranADs.src.constants import constants
from .algorithm import *


class USAD(TranADAlagorithm):
    def __init__(self):
        super(USAD, self).__init__()
        self.accuracy_list = None
        self.epoch = None
        self.scheduler = None
        self.optimizer = None
        self.model = None
        self.alg = "USAD"


