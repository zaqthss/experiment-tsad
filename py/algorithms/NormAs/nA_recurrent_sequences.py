# Authors: Paul Boniol, Michele Linardi, Federico Roncallo, Themis Palpanas
# Date: 08/07/2020
# copyright retained by the authors
# algorithms protected by patent application FR2003946
# code provided as is, and can be used only for research purposes

# Python Version: 3.6.1

#######################################################################
#######################################################################
#####			NORMA RECURRENT SEQUENCES  FUNCTIONS			 ######
#######################################################################
#######################################################################
import numpy as np
import pandas as pd
from scipy import signal

from .tools import get_sequence_under_threshold


###################################################################################################
################################# EXTRATION SEQUENCE FUNCTION ####################################
###################################################################################################

####### Align the recurrent sequences #######
def _unshift_series(ts, sequence_rec,normalmodel_size):
    result = []
    ref = ts[sequence_rec[0][0]:sequence_rec[0][1]]
    for seq in sequence_rec:
        shift = (np.argmax(signal.correlate(ref, ts[seq[0]:seq[1]])) - len(ts[seq[0]:seq[1]]))
        if (len(ts[seq[0]-int(shift):seq[1]-int(shift)]) == normalmodel_size):
            result.append([seq[0]-int(shift),seq[1]-int(shift)])
    return result

def extract_recurrent_sequences_random(ts, normalmodel_size,
                                       percentage_sel = 0.2, overlapping_factor=1, sampling_division=2):
    """
        INPUT
            ts: a list representing the time series
            normalmodel_size: an integer representing the size of the normalmodel
                              you want to generate
            percentage_sel: the percentage of the dataset to sample respect to
                            all the possible sequences to select (depends also on the overlapping_factor).
                            Values range between [0-1]
            overlapping_factor: the overlapping factor to exclude:
                                1 is no overlapping, 0 is total overlapping allowed.
                                Values range between [0-1]
            sampling_division: the number of chunk in which we divide our time series during sampling.
                               Values range between [0-1]
        OUTPUT
            tuple(recurrent_sequence, sequence_rec)

            recurrent_sequence: a panda dataframe containg all the recurrent sequences, one per column
            sequence_rec: a list of couple(start,end) of each recurrent sequence in the original time series
    """
    recurrent_seq_num = ((len(ts) - normalmodel_size) //
                        (normalmodel_size*overlapping_factor))
    recurrent_seq_num = int(recurrent_seq_num * percentage_sel)
    recurrent_seq_4chunk = max(1, recurrent_seq_num // sampling_division)
    len_chunk = len(ts) // sampling_division
    sequence_rec = []

    for i in range(0,sampling_division):
        possible_idx = np.arange(i*len_chunk,
                                min(i*len_chunk + len_chunk, len(ts)-1) - normalmodel_size)
        for j in np.arange(0,recurrent_seq_4chunk):
            if len(possible_idx) == 0:
                break
            np.random.seed(1)
            idx_ts = np.random.choice(possible_idx)
            sequence_rec.append((idx_ts,idx_ts+normalmodel_size))
            possible_idx = list(set(possible_idx)-set(np.arange(idx_ts - normalmodel_size//overlapping_factor , idx_ts + normalmodel_size//overlapping_factor)))
    
    ####### try to align the recurrent sequences #######
    sequence_rec = _unshift_series(ts,sequence_rec,normalmodel_size)

    recurrent_sequence = pd.DataFrame()
    for i,sr in enumerate(sequence_rec):
        recurrent_sequence[str(i)] = ts[(sr[0]):(sr[1])]

    return recurrent_sequence, sequence_rec


def extract_recurrent_sequences_random_kshape(ts, normalmodel_size,
                                       percentage_sel = 0.2, overlapping_factor=1, sampling_division=10):
    """
        INPUT
            ts: a list representing the time series
            normalmodel_size: an integer representing the size of the normalmodel
                              you want to generate
            percentage_sel: the percentage of the dataset to sample respect to
                            all the possible sequences to select (depends also on the overlapping_factor).
                            Values range between [0-1]
            overlapping_factor: the overlapping factor to exclude:
                                1 is no overlapping, 0 is total overlapping allowed.
                                Values range between [0-1]
            sampling_division: the number of chunk in which we divide our time series during sampling.
                               Values range between [0-1]
        OUTPUT
            tuple(recurrent_sequence, sequence_rec)

            recurrent_sequence: a panda dataframe containg all the recurrent sequences, one per column
            sequence_rec: a list of couple(start,end) of each recurrent sequence in the original time series
    """
    recurrent_seq_num = ((len(ts) - normalmodel_size) //
                        (normalmodel_size*overlapping_factor))
    recurrent_seq_num = int(recurrent_seq_num * percentage_sel)
    recurrent_seq_4chunk = max(1, recurrent_seq_num // sampling_division)
    len_chunk = len(ts) // sampling_division
    sequence_rec = []

    for i in range(0,sampling_division):
        possible_idx = np.arange(i*len_chunk,
                                min(i*len_chunk + len_chunk, len(ts)-1) - normalmodel_size)
        for j in np.arange(0,recurrent_seq_4chunk):
            if len(possible_idx) == 0:
                break
            idx_ts = np.random.choice(possible_idx)
            sequence_rec.append((idx_ts,idx_ts+normalmodel_size))
            possible_idx = list(set(possible_idx)-set(np.arange(idx_ts - normalmodel_size//overlapping_factor , idx_ts + normalmodel_size//overlapping_factor)))

    recurrent_sequence = pd.DataFrame()
    for i,sr in enumerate(sequence_rec):
        recurrent_sequence[str(i)] = ts[(sr[0]):(sr[1])]

    return recurrent_sequence, sequence_rec


def extract_recurrent_sequences_motif(ts, self_join, normalmodel_size, pattern_length, threshold = 1, min_extraction= 10):
    """
        INPUT
            ts: a list representing the time series
            self_join: a list representing the self_join of ts given pattern_length
            pattern_length: an integer representing the size of the anomalies are you
                            looking for
            normalmodel_size: an integer representing the size of the normalmodel
                              you want to generate
            threshold: a parameter that allows to choose where to cut the self join in order to
                          select the recurrent pattern.
                          Higher it is less selective the cutting will be
                          It is a value that is summed to mean(self_join)
                          Default: it will be std(self_join)
        OUTPUT
            tuple(recurrent_sequence, sequence_rec)

            recurrent_sequence: a panda dataframe containg all the recurrent sequences, one per column
            sequence_rec: a list of couple(start,end) of each recurrent sequence in the original time series
    """

    threshold = np.mean(self_join)
    sequence_rec = []
    not_inf_loop = 0
    min_extraction = min(min_extraction,len(ts)//pattern_length)
    while ( len(sequence_rec) <= min_extraction ):
        sequence_rec = get_sequence_under_threshold(self_join, threshold, normalmodel_size)
        threshold += 0.01
        not_inf_loop +=1
        if(not_inf_loop == 10e6):
            raise ValueError('ERROR: Zero recurrent sequences found, check the self join output or the threshold parameter...')


    ####### try to align the recurrent sequences #######
    sequence_rec = _unshift_series(ts,sequence_rec,normalmodel_size)

    recurrent_sequence = pd.DataFrame()
    for i,sr in enumerate(sequence_rec):
        recurrent_sequence[str(i)] = ts[(sr[0]):(sr[1])]

    return recurrent_sequence, sequence_rec
