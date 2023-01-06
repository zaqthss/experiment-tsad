
import os
import numpy as np
import torch
from  torch.utils.data import DataLoader,TensorDataset


STRIDE_TRAIN=5


def param_default(params: dict, key: str, default):
    if type(default) is dict:
        return params[key] if params.__contains__(key) else default[key]
    else:
        return params[key] if params.__contains__(key) else default


def load_datasetwithSeries(series,suq_len=256,batch_size=64,is_train=True):
    #read timeseries
    x = []
    for i in range(series.dim):
        ts = series.getunibydim(i)
        xt = []
        for i, p in enumerate(ts.timeseries):
            xt.append(p.observe)
        x.append(xt)

    x = np.array(x)
    x=x.T
    for i in range(x.shape[1]):
        x[:, i] = normalize(x[:, i])
    y=[]
    for p in series.timeseries:
        if p.is_anomaly==True:
            y.append(1)
        else:
            y.append(0)
    y = np.array(y)

    # cut with batchsize
    samples_x = []
    samples_y = []
    stride = STRIDE_TRAIN
    for start in np.arange(0, x.shape[0], stride):
        if start + suq_len >= x.shape[0]:
            break
        samples_x.append(x[start:start + suq_len, :])
        if (y[start:start + suq_len]==1).any():
            samples_y.append(1)
        else:
            samples_y.append(0)
    x = np.array(samples_x)
    y=[]
    y.append(samples_y)
    y = np.array(y)
    y=y.T



    # convert to tensor
    dataset = TensorDataset(torch.Tensor(x), torch.Tensor(y))

    data_loader = DataLoader(
        dataset=dataset,
        batch_size=batch_size,
        shuffle=is_train,
        drop_last=is_train
    )
    return data_loader

def normalize(seq):
    if np.max(seq)==np.min(seq):
        return 1
    else:
        return 2 * (seq - np.min(seq)) / (np.max(seq) - np.min(seq)) - 1






