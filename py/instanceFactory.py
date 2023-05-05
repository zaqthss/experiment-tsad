from algorithms.BeatGAN import BeatGAN
from algorithms.GDN import GDN
from algorithms.GDNWithPOT import GDNWithPOT
from algorithms.NormA import NormA
from algorithms.OmniAnomaly import OmniAnomaly
from algorithms.OmniAnomalyWithPOT import OmniAnomalyWithPOT
from algorithms.RCoder import RCoder
from algorithms.TranAD import TranAD
from algorithms.TranADWithPOT import TranADWithPOT
from algorithms.UFEKT import UFEKT
from algorithms.USAD import USAD
from algorithms.USADWithPOT import USADWithPOT
from algorithms.algorithm import Algorithm
from algorithms.example import algExp
from tools.metricsHandler import *


def getAlgInstance(name) -> Algorithm:
    if name == "example":
        return algExp()
    elif name == "TranAD":
        return TranAD()
    elif name == "TranADWithPOT":
        return TranADWithPOT()
    elif name == "USAD":
        return USAD()
    elif name == "USADWithPOT":
        return USADWithPOT()
    elif name == "OmniAnomaly":
        return OmniAnomaly()
    elif name == "OmniAnomalyWithPOT":
        return OmniAnomalyWithPOT()
    elif name == "GDN":
        return GDN()
    elif name == "GDNWithPOT":
        return GDNWithPOT()
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
    elif name == "subfront":
        return rangeMetrics(bias="FRONT_END")
    else:
        return None
