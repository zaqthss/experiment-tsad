from algorithms.OmniAnomaly import OmniAnomaly
from algorithms.RCoder import RCoder
from algorithms.TranAD import TranAD
from algorithms.GDN import GDN
from algorithms.TranADWithPOT import TranADWithPOT
from algorithms.USAD import USAD
from algorithms.example import algExp
from algorithms.NormA import NormA
from algorithms.UFEKT import UFEKT
from algorithms.BeatGAN import BeatGAN
from algorithms.algorithm import algorithm
from tools.metricsHandler import *


def getAlgInstance(name) -> algorithm:
    if name == "example":
        return algExp()
    elif name == "TranAD":
        return TranAD()
    elif name == "TranADWithPOT":
        return TranADWithPOT()
    elif name == "USAD":
        return USAD()
    elif name == "OmniAnomaly":
        return OmniAnomaly()
    elif name == "GDN":
        return GDN()
    elif name == "RCoder":
        return RCoder()
    elif name=="NormA":
        return NormA()
    elif name=="UFEKT":
        return UFEKT()
    elif name=="BeatGAN":
        return BeatGAN()
    else:
        return None


def getMetricInstance(name) -> metrics:
    if name == "point":
        return pointMetrics()
    elif name == "subsequence":
        return rangeMetrics()
    else:
        return None
