import pandas as pd
import numpy as np


def series_segmentation(data, stepsize=1):
    return np.split(data, np.where(np.diff(data) != stepsize)[0] + 1)


def generate_tune_file_point(data_dir, file_name, start_idx, number):
    raw_fpath = "%s/%s.csv" % (data_dir, file_name)
    df = pd.read_csv(raw_fpath)
    if number > 0:
        df_test = df.iloc[start_idx: start_idx + number, :]
    else:
        df_test = df

    out_idxs = df_test[df_test['Label'] == 1].index.to_list()
    print(out_idxs)
    out_num = len(out_idxs)
    print(out_num)
    print(out_num / df_test.shape[0])

    tune_dir = "../../dataset/tune"
    suffix = str(number // 1000) + "k"
    tune_fpath = "%s/%s_%s_num_%s.csv" % (tune_dir, file_name, suffix, out_num)
    print(tune_fpath)

    df_test.to_csv(tune_fpath, index=False)


def generate_tune_file_sub(data_dir, file_name, start_idx, number):
    raw_fpath = "%s/%s.csv" % (data_dir, file_name)
    df = pd.read_csv(raw_fpath)
    if number > 0:
        df_test = df.iloc[start_idx: start_idx + number, :]
    else:
        df_test = df

    out_idxs = df_test[df_test['Label'] == 1].index.to_list()
    print(out_idxs)
    out_subs = series_segmentation(out_idxs)
    out_num = len(out_subs)
    avg_len = 0
    for data_sub in out_subs:
        avg_len += len(data_sub)
        print(len(data_sub))
        print(data_sub)
    avg_len = round(avg_len / out_num, 1)

    tune_dir = "../../dataset/tune"
    suffix = str(number // 1000) + "k"
    tune_fpath = "%s/%s_%s_sub_%s_num_%s.csv" % (tune_dir, file_name, suffix, avg_len, out_num)
    print(tune_fpath)

    df_test.to_csv(tune_fpath, index=False)


def reformat_data(data_dir, file_name):
    raw_fpath = "%s/%s.csv" % (data_dir, file_name)
    df = pd.read_csv(raw_fpath, header=None)

    length = df.shape[0]
    dim = df.shape[1]

    timestamp = np.arange(1, length + 1)
    df.insert(0, 'timestamp', timestamp)

    col_names = ['timestamp']
    col_names.extend(['value{}'.format(i) for i in range(1, dim)])
    col_names.append('Label')
    df.columns = col_names

    out_idxs = df[df['Label'] == 1].index.to_list()
    out_point_num = len(out_idxs)
    print("anomaly point number: %s" % out_point_num)
    print("anomaly rate: %s" % (out_point_num / length))
    out_subs = series_segmentation(out_idxs)
    out_sub_num = len(out_subs)
    print("anomaly sub number: %s" % out_sub_num)
    avg_len = 0
    for data_sub in out_subs:
        avg_len += len(data_sub)
        # print(len(data_sub))
        # print(data_sub)
    avg_len = round(avg_len / out_sub_num, 1)
    print("average length: %s" % avg_len)

    df.to_csv('D:/git/exp-tsad/dataset/withlabel/%s.csv' % file_name, index=False)

    # outlier_idxs = df_raw[df_raw.loc[:, dim - 1] == 1]
    # print(outlier_idxs)


def get_summary(data_dir, file_name, start_idx, number):
    raw_fpath = "%s/%s.csv" % (data_dir, file_name)
    df_raw = pd.read_csv(raw_fpath)

    if number > 0:
        df = df_raw.iloc[start_idx: start_idx + number, :]
    else:
        df = df_raw

    length = df.shape[0]

    out_idxs = df[df['Label'] == 1].index.to_list()
    out_point_num = len(out_idxs)
    print("anomaly point number: %s" % out_point_num)
    print("anomaly rate: %s" % (out_point_num / length))
    out_subs = series_segmentation(out_idxs)
    out_sub_num = len(out_subs)
    print("anomaly sub number: %s" % out_sub_num)
    avg_len = 0
    for data_sub in out_subs:
        avg_len += len(data_sub)
        print(len(data_sub))
        # print(data_sub)
    avg_len = round(avg_len / out_sub_num, 1)
    print("average length: %s" % avg_len)


if __name__ == '__main__':
    # data_dir = 'D:/git/exp-tsad/code/tsad/data/sub'
    data_dir = 'D:/git/exp-tsad/code/tsad/data/point'
    file_name = "ECG"
    number = 1000
    start_idx = 200
    # generate_tune_file_sub(data_dir, file_name, start_idx, number)
    # generate_tune_file_point(data_dir, file_name, start_idx, number)
    # get_summary("D:/git/exp-tsad/code/tsad/data/sub", "Exathlon", start_idx, number)

    # get_summary("D:/code/pycharm/tsad/dataset", "uni_subt_len_30_num_90_1000_0.1_1", 0, -1)
    get_summary("D:/code/pycharm/tsad/dataset", "mul_subs_len_50_num_460_5000_0.1_1", 0, -1)
    '''
    data_dir = "D:/git/exp-tsad/dataset/StareData"
    file_names = ["YahooA1", "DLR", "ECG"]
    for file_name in file_names:
        print(file_name)
        reformat_data(data_dir, file_name)
    '''