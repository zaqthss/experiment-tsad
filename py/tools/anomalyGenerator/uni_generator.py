import random

import matplotlib.pyplot as plt
import numpy as np
import pandas as pd


def series_segmentation(data, stepsize=1):
    return np.split(data, np.where(np.diff(data) != stepsize)[0] + 1)


def sine(length, freq=0.04, coef=1.5, offset=0.0, noise_amp=0.05):
    # timestamp = np.linspace(0, 10, length)
    timestamp = np.arange(length)
    value = np.sin(2 * np.pi * freq * timestamp)
    if noise_amp != 0:
        noise = np.random.normal(0, 1, length)
        value = value + noise_amp * noise
    value = coef * value + offset
    return value


def square_sine(level=5, length=500, freq=0.04, coef=1.5, offset=0.0, noise_amp=0.05):
    value = np.zeros(length)
    for i in range(level):
        value += 1 / (2 * i + 1) * sine(length=length, freq=freq * (2 * i + 1), coef=coef, offset=offset, noise_amp=noise_amp)
    return value


def collective_global_synthetic(length, base, coef=1.5, noise_amp=0.005):
    value = []
    norm = np.linalg.norm(base)
    base = base / norm
    num = int(length / len(base))
    for i in range(num):
        value.extend(base)
    residual = length - len(value)
    value.extend(base[:residual])
    value = np.array(value)
    noise = np.random.normal(0, 1, length)
    value = coef * value + noise_amp * noise
    return value


class UniDataGenerator:
    def __init__(self, stream_length, behavior=sine, behavior_config=None):
        self.STREAM_LENGTH = stream_length
        self.behavior = behavior
        self.behavior_config = behavior_config if behavior_config is not None else {}

        self.data = None
        self.label = None
        self.data_origin = None
        self.timestamp = None

        # self.generate_timeseries()

    def generate_timeseries(self):
        self.behavior_config['length'] = self.STREAM_LENGTH
        self.data = self.behavior(**self.behavior_config)
        self.data_origin = self.data.copy()
        self.label = np.zeros(self.STREAM_LENGTH, dtype=int)
        self.timestamp = np.arange(self.STREAM_LENGTH)

    def set_timeseries(self, data, timestamp):
        self.STREAM_LENGTH = data.size
        self.behavior_config['length'] = self.STREAM_LENGTH
        self.data = data
        self.data_origin = self.data.copy()
        self.label = np.zeros(self.STREAM_LENGTH, dtype=int)
        self.timestamp = timestamp

    def point_global_outliers_mul(self, ratio, factor, radius):
        """
        Add point global outliers to original data
        Args:
            ratio: what ratio outliers will be added
            factor: the larger, the outliers are farther from inliers
            radius: the radius of collective outliers range
        """
        position = (np.random.rand(round(self.STREAM_LENGTH * ratio)) * self.STREAM_LENGTH).astype(int)
        maximum, minimum = max(self.data), min(self.data)
        print(maximum, minimum)
        for i in position:
            local_std = self.data_origin[max(0, i - radius):min(i + radius, self.STREAM_LENGTH)].std()
            self.data[i] = self.data_origin[i] * factor * local_std
            if 0 <= self.data[i] < maximum: self.data[i] = maximum
            if 0 > self.data[i] > minimum: self.data[i] = minimum
            self.label[i] = 1

    def point_global_outliers_add(self, ratio, factor, radius):
        """
        Add point global outliers to original data
        Args:
            ratio: what ratio outliers will be added
            factor: the larger, the outliers are farther from inliers
            radius: the radius of collective outliers range
        """
        position = (np.random.rand(round(self.STREAM_LENGTH * ratio)) * self.STREAM_LENGTH).astype(int)
        maximum, minimum = max(self.data), min(self.data)
        print(maximum, minimum)
        for i in position:
            local_std = self.data_origin[max(0, i - radius):min(i + radius, self.STREAM_LENGTH)].std()
            flag = random.random()
            addition = factor * local_std
            if flag > 0.5:
                addition = -addition
            self.data[i] = self.data_origin[i] + addition
            if minimum < self.data[i] < maximum:
                if flag > 0.5:
                    self.data[i] = minimum - factor * random.random()
                else:
                    self.data[i] = maximum + factor * random.random()
            self.label[i] = 1

    def point_contextual_outliers(self, ratio, factor, radius):
        """
        Add point contextual outliers to original data
        Args:
            ratio: what ratio outliers will be added
            factor: the larger, the outliers are farther from inliers
                    Notice: point contextual outliers will not exceed the range of [min, max] of original data
            radius: the radius of collective outliers range
        """
        position = (np.random.rand(round(self.STREAM_LENGTH * ratio)) * self.STREAM_LENGTH).astype(int)
        maximum, minimum = max(self.data), min(self.data)
        prop = maximum / minimum
        print(maximum, minimum)
        for i in position:
            local_std = self.data_origin[max(0, i - radius):min(i + radius, self.STREAM_LENGTH)].std()
            self.data[i] = self.data_origin[i] * factor * local_std
            if self.data[i] > maximum:
                self.data[i] = maximum * min(0.95, abs(np.random.normal(1, 0.5)))
            # if self.data[i] < minimum: self.data[i] = minimum * min(0.95, abs(np.random.normal(0, 1)))
            if self.data[i] < minimum < 0:
                self.data[i] = minimum * min(0.95, abs(np.random.normal(1, 0.5)))
            if self.data[i] < minimum:
                mul = np.random.normal(prop, 0.1)
                self.data[i] = minimum * min(1.5, mul)

            self.label[i] = 1
        # print(maximum, minimum)
        # for i in position:
        #     local_std = self.data_origin[max(0, i - radius):min(i + radius, self.STREAM_LENGTH)].std()
        #     self.data[i] = self.data_origin[i] * factor * local_std
        #     if self.data[i] > maximum: self.data[i] = maximum * min(0.95, abs(np.random.normal(0, 0.5)))  # previous(0, 1)
        #     if self.data[i] < minimum: self.data[i] = minimum * min(0.95, abs(np.random.normal(0, 0.5)))
        #
        #     self.label[i] = 1

    def collective_global_outliers(self, ratio, radius, option='square', coef=3., noise_amp=0.0,
                                    level=5, freq=0.04, offset=0.0, # only used when option=='square'
                                    base=[0.,]): # only used when option=='other'
        """
        Add collective global outliers to original data
        Args:
            ratio: what ratio outliers will be added
            radius: the radius of collective outliers range
            option: if 'square': 'level' 'freq' and 'offset' are used to generate square sine wave
                    if 'other': 'base' is used to generate outlier shape
            level: how many sine waves will square_wave synthesis
            base: a list of values that we want to substitute inliers when we generate outliers
        """
        position = (np.random.rand(round(self.STREAM_LENGTH * ratio / (2 * radius))) * self.STREAM_LENGTH).astype(int)

        valid_option = {'square', 'other'}
        if option not in valid_option:
            raise ValueError("'option' must be one of %r." % valid_option)

        if option == 'square':
            sub_data = square_sine(level=level, length=self.STREAM_LENGTH, freq=freq,
                                   coef=coef, offset=offset, noise_amp=noise_amp)
        else:
            sub_data = collective_global_synthetic(length=self.STREAM_LENGTH, base=base,
                                                   coef=coef, noise_amp=noise_amp)
        for i in position:
            start, end = max(0, i - radius), min(self.STREAM_LENGTH, i + radius)
            self.data[start:end] = sub_data[start:end]
            self.label[start:end] = 1

    def collective_trend_outliers(self, ratio, factor, radius):
        """
        Add collective trend outliers to original data
        Args:
            ratio: what ratio outliers will be added
            factor: how dramatic will the trend be
            radius: the radius of collective outliers range
        """
        position = (np.random.rand(round(self.STREAM_LENGTH * ratio / (2 * radius))) * self.STREAM_LENGTH).astype(int)
        for i in position:
            start, end = max(0, i - radius), min(self.STREAM_LENGTH, i + radius)
            slope = np.random.choice([-1, 1]) * factor * np.arange(end - start)
            self.data[start:end] = self.data_origin[start:end] + slope
            self.data[end:] = self.data[end:] + slope[-1]
            self.label[start:end] = 1

    def collective_seasonal_outliers(self, ratio, factor, radius):
        """
        Add collective seasonal outliers to original data
        Args:
            ratio: what ratio outliers will be added
            factor: how many times will frequency multiple
            radius: the radius of collective outliers range
        """
        position = (np.random.rand(round(self.STREAM_LENGTH * ratio / (2 * radius))) * self.STREAM_LENGTH).astype(int)
        seasonal_config = self.behavior_config
        seasonal_config['freq'] = factor * self.behavior_config['freq']
        for i in position:
            start, end = max(0, i - radius), min(self.STREAM_LENGTH, i + radius)
            self.data[start:end] = self.behavior(**seasonal_config)[start:end]
            self.label[start:end] = 1


def generate_by_ratio(size=1000):
    seed = 1
    np.random.seed(seed)

    BEHAVIOR_CONFIG = {'freq': 0.04, 'coef': 1.5, "offset": 0.0, 'noise_amp': 0.05}
    BASE = [1.4529900e-01, 1.2820500e-01, 9.4017000e-02, 7.6923000e-02, 1.1111100e-01, 1.4529900e-01, 1.7948700e-01,
            2.1367500e-01, 2.1367500e-01]

    ratios = np.arange(0.05, 0.45, 0.05)
    # ratios = [0.1]
    length = size
    factor = 1.5
    radius = 20

    # univariate_data = UniDataGenerator(stream_length=length, behavior=sine, behavior_config=BEHAVIOR_CONFIG)
    # univariate_data.generate_timeseries()
    # print(univariate_data.data.shape)

    for ratio in ratios:
        univariate_data = UniDataGenerator(stream_length=length, behavior=sine, behavior_config=BEHAVIOR_CONFIG)
        univariate_data.generate_timeseries()
        # univariate_data.point_global_outliers(ratio=ratio, factor=factor, radius=radius)
        univariate_data.point_contextual_outliers(ratio=ratio, factor=factor, radius=radius)
        # univariate_data.collective_global_outliers(ratio=ratio, radius=radius)
        # univariate_data.collective_seasonal_outliers(ratio=ratio, factor=factor, radius=radius)

        plt.plot(univariate_data.timestamp, univariate_data.data)

        outlier_idxs = np.where(univariate_data.label == 1)[0]
        outliers = list(univariate_data.data[outlier_idxs])
        outcome = series_segmentation(outlier_idxs)
        for outlier in outcome:
             if len(outlier) == 1:
                  plt.plot(outlier, univariate_data.data[outlier], 'ro')
             else:
                  if len(outlier) != 0:
                       plt.axvspan(outlier[0], outlier[-1], color='red', alpha=0.5)

        plt.show()

        df = pd.DataFrame(
            {'timestamp': range(1, length + 1), 'value': univariate_data.data, 'Label': univariate_data.label})
        df.to_csv("../../dataset/uni_pointc_{len}_{rate}_{seed}.csv".format(
            len=length, rate=round(ratio, 2), seed=seed), index=False)


def add_stock_point(size=10000):
    seed = 1
    np.random.seed(seed)

    df = pd.read_csv("../../dataset/input/Stock.csv", header=None)
    data = df.to_numpy().flatten()[:size]
    timestamp = np.arange(1, data.size + 1)

    generator = UniDataGenerator(stream_length=size)
    generator.set_timeseries(data, timestamp)

    ratio = 0.1
    factor = 3
    radius = size
    # generator.point_contextual_outliers(ratio, factor, radius)
    generator.point_global_outliers_add(ratio, factor, radius)

    plt.plot(generator.timestamp, generator.data)

    outlier_idxs = np.where(generator.label == 1)[0]
    outliers = list(generator.data[outlier_idxs])
    outcome = series_segmentation(outlier_idxs)
    for outlier in outcome:
        if len(outlier) == 1:
            plt.plot(outlier, generator.data[outlier], 'ro')
        else:
            if len(outlier) != 0:
                plt.axvspan(outlier[0], outlier[-1], color='red', alpha=0.5)

    plt.show()

    df = pd.DataFrame(
        {'timestamp': timestamp, 'value': generator.data, 'Label': generator.label}
    )
    df.to_csv("../../dataset/stock_pointg_{size}_{rate}_{seed}.csv".format(
        size=size, rate=round(ratio, 2), seed=seed), index=False)


def add_stock_sub(size=10000):
    seed = 3
    np.random.seed(seed)

    df = pd.read_csv("../../dataset/input/Stock.csv", header=None)
    data = df.to_numpy().flatten()[:size]
    print(min(data), max(data))
    timestamp = np.arange(1, data.size + 1)
    coef = 1.5
    # offset = np.mean(data)
    offset = np.mean(data) / 1.8

    BEHAVIOR_CONFIG = {'freq': 0.04, 'coef': coef, "offset": offset, 'noise_amp': 0.05}
    generator = UniDataGenerator(stream_length=size, behavior_config=BEHAVIOR_CONFIG)
    generator.set_timeseries(data, timestamp)

    ratio = 0.1
    factor = 1.5
    radius = int(size / 500)
    print(radius)
    # generator.collective_seasonal_outliers(ratio, factor, radius)
    generator.collective_global_outliers(ratio=ratio, radius=radius, coef=coef, offset=offset)

    plt.plot(generator.timestamp, generator.data)

    outlier_idxs = np.where(generator.label == 1)[0]
    outliers = list(generator.data[outlier_idxs])
    print(outliers)
    outcome = series_segmentation(outlier_idxs)
    for outlier in outcome:
        if len(outlier) == 1:
            plt.plot(outlier, generator.data[outlier], 'ro')
        else:
            if len(outlier) != 0:
                plt.axvspan(outlier[0], outlier[-1], color='red', alpha=0.5)

    plt.show()

    df = pd.DataFrame(
        {'timestamp': timestamp, 'value': generator.data, 'Label': generator.label}
    )
    #df.to_csv("../../dataset/stock_subg_len_{length}_num_{num}_{size}_{rate}_{seed}.csv".format(
    #    length=radius*2, num=len(outliers), size=size, rate=round(ratio, 2), seed=seed), index=False)


def generate_uni_sp(size, type):
    seed = 1
    np.random.seed(seed)

    BEHAVIOR_CONFIG = {'freq': 0.04, 'coef': 1.5, "offset": 0.0, 'noise_amp': 0.05}
    BASE = [1.4529900e-01, 1.2820500e-01, 9.4017000e-02, 7.6923000e-02, 1.1111100e-01, 1.4529900e-01, 1.7948700e-01,
            2.1367500e-01, 2.1367500e-01]

    ratio = 0.1
    length = size
    factor = 0.4
    radius = 15

    univariate_data = UniDataGenerator(stream_length=length, behavior=sine, behavior_config=BEHAVIOR_CONFIG)
    univariate_data.generate_timeseries()
    # univariate_data.point_global_outliers(ratio=ratio, factor=factor, radius=radius)
    # univariate_data.point_contextual_outliers(ratio=ratio, factor=factor, radius=radius)
    if type == "subt":
        univariate_data.collective_trend_outliers(ratio, factor, radius)
    elif type == "subg":
        univariate_data.collective_global_outliers(ratio=ratio, radius=radius)
    elif type == "subs":
        univariate_data.collective_seasonal_outliers(ratio=ratio, factor=factor, radius=radius)


    plt.plot(univariate_data.timestamp, univariate_data.data)

    outlier_idxs = np.where(univariate_data.label == 1)[0]
    outliers = list(univariate_data.data[outlier_idxs])
    outcome = series_segmentation(outlier_idxs)
    for outlier in outcome:
        if len(outlier) == 1:
            plt.plot(outlier, univariate_data.data[outlier], 'ro')
        else:
            if len(outlier) != 0:
                plt.axvspan(outlier[0], outlier[-1], color='red', alpha=0.5)

    plt.show()

    df = pd.DataFrame(
        {'timestamp': range(1, length + 1), 'value': univariate_data.data, 'Label': univariate_data.label})
    df.to_csv("../../dataset/uni_{type}_len_{length}_num_{num}_{size}_{rate}_{seed}.csv".format(
        type=type, length=radius * 2, num=len(outliers), size=size, rate=round(ratio, 2), seed=seed), index=False)


if __name__ == '__main__':
    size = 10000
    # generate_by_ratio(size=size)
    # add_stock_sub(size=500)
    add_stock_point(size=10000)
    # anomaly_type = "subc"
    # generate_uni_sp(size=1000, type=anomaly_type)





