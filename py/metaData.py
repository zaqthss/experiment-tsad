algorithmsParameters = {
    # point
    "USAD": {
        "twitter": {
            "lr": 0.0001,
            "n_latent": 5,
            "n_window": 5,
            "epoch": 5
        },
        "yahoo": {
            "lr": 0.01,
            "n_latent": 5,
            "n_window": 5,
            "epoch": 5
        },
        "tao_pointg": {
            "lr": 0.001,
            "n_latent": 5,
            "n_window": 5,
            "epoch": 5
        },
        "ecg": {
            "lr": 0.01,
            "n_latent": 5,
            "n_window": 5,
            "epoch": 5
        },
        "smtp": {
            "lr": 0.1,
            "n_latent": 5,
            "n_window": 5,
            "epoch": 5
        },
        "stock_pointg": {
            "lr": 0.001,
            "n_latent": 5,
            "n_window": 5,
            "epoch": 5
        },
        "uni_pointg": {
            "lr": 0.001,
            "n_latent": 5,
            "n_window": 5,
            "epoch": 5
        },
        "credit": {
            "lr": 0.1,
            "n_latent": 5,
            "n_window": 5,
            "epoch": 5
        },
        "mul_pointg": {
            "lr": 0.001,
            "n_latent": 5,
            "n_window": 5,
            "epoch": 5
        },
        "dlr": {
            "lr": 0.01,
            "n_latent": 5,
            "n_window": 5,
            "epoch": 5
        },
        "exathlon_sp_pos": {
            "lr": 0.001,
            "n_latent": 5,
            "n_window": 5,
            "epoch": 5
        },
        "uni_subg_sp_pos": {
            "lr": 0.1,
            "n_latent": 5,
            "n_window": 5,
            "epoch": 5
        },
        "uni_subg_sp": {
            "lr": 0.1,
            "n_latent": 5,
            "n_window": 5,
            "epoch": 5
        },
        "uni_subs_sp_pos": {
            "lr": 0.01,
            "n_latent": 5,
            "n_window": 5,
            "epoch": 5
        },
        "uni_subt_sp_pos": {
            "lr": 0.001,
            "n_latent": 5,
            "n_window": 5,
            "epoch": 5
        },
        "swat-p": {
            "lr": 0.008,
            "n_latent": 5,
            "n_window": 5,
            "epoch": 5
        },
    },
    "USADWithPOT": {
        "twitter": {
            "lr": 0.0001,
            "n_latent": 5,
            "n_window": 5,
            "lm":(0.7, 1.0),
            "epoch": 5
        },
        "yahoo": {
            "lr": 0.01,
            "n_latent": 5,
            "n_window": 5,
            "lm": (1.2, 3),
            "epoch": 5
        },
        "tao_pointg": {
            "lr": 0.001,
            "n_latent": 5,
            "n_window": 5,
            "lm": (0.95, 0.99),
            "epoch": 5
        },
        "ecg": {
            "lr": 0.01,
            "n_latent": 5,
            "n_window": 5,
            "lm": (0.95, 0.99),
            "epoch": 5
        },
        "smtp": {
            "lr": 0.1,
            "n_latent": 5,
            "n_window": 5,
            "lm": (0.95, 0.99),
            "epoch": 5
        },
        "stock_pointg": {
            "lr": 0.001,
            "n_latent": 5,
            "n_window": 5,
            "lm": (1.0, 1.0),
            "epoch": 5
        },
        "uni_pointg": {
            "lr": 0.001,
            "n_latent": 5,
            "n_window": 5,
            "lm": (1.0, 1.0),
            "epoch": 5
        },
        "credit": {
            "lr": 0.1,
            "n_latent": 5,
            "n_window": 5,
            "lm": (0.95, 0.99),
            "epoch": 5
        },
        "mul_pointg": {
            "lr": 0.001,
            "n_latent": 5,
            "n_window": 5,
            "lm": (0.95, 0.99),
            "epoch": 5
        },
        "dlr": {
            "lr": 0.01,
            "n_latent": 5,
            "n_window": 5,
            "lm": (0.95, 0.99),
            "epoch": 5
        },
        "exathlon_sp_pos": {
            "lr": 0.001,
            "n_latent": 5,
            "n_window": 5,
            "lm": (0.95, 0.99),
            "epoch": 5
        },
        "uni_subg_sp_pos": {
            "lr": 0.1,
            "n_latent": 5,
            "n_window": 5,
            "lm": (0.95, 0.99),
            "epoch": 5
        },
        "uni_subs_sp_pos": {
            "lr": 0.01,
            "n_latent": 5,
            "n_window": 5,
            "lm": (0.95, 0.99),
            "epoch": 5
        },
        "uni_subt_sp_pos": {
            "lr": 0.001,
            "n_latent": 5,
            "n_window": 5,
            "lm": (0.95, 0.99),
            "epoch": 5
        },
        "swat-p": {
            "lr": 0.008,
            "n_latent": 5,
            "n_window": 5,
            "lm": (0.95, 0.99),
            "epoch": 5
        },
    },
    "OmniAnomaly": {
        "twitter": {
            "lr": 0.0001,
            "beta": 0.01,
            "n_latent": 8,
            "epoch": 5,
        },
        "yahoo": {
            "lr": 0.01,
            "beta": 0.01,
            "n_latent": 8,
            "epoch": 5,
        },
        "tao_pointg": {
            "lr": 0.01,
            "beta": 0.01,
            "n_latent": 8,
            "epoch": 5,
        },
        "ecg": {
            "lr": 0.01,
            "beta": 0.01,
            "n_latent": 8,
            "epoch": 5,
        },
        "smtp": {
            "lr": 0.0001,
            "beta": 0.01,
            "n_latent": 8,
            "epoch": 5,
        },
        "stock_pointg": {
            "lr": 0.01,
            "beta": 0.01,
            "n_latent": 8,
            "epoch": 5,
        },
        "uni_pointg": {
            "lr": 0.01,
            "beta": 0.01,
            "n_latent": 8,
            "epoch": 5,
        },
        "credit": {
            "lr": 0.01,
            "beta": 0.01,
            "n_latent": 8,
            "epoch": 5,
        },
        "mul_pointg": {
            "lr": 0.001,
            "beta": 0.01,
            "n_latent": 8,
            "epoch": 5,
        },
        "dlr": {
            "lr": 0.1,
            "beta": 0.01,
            "n_latent": 8,
            "epoch": 5,
        },
        "exathlon_sp_pos": {
            "lr": 0.0001,
            "beta": 0.01,
            "n_latent": 8,
            "epoch": 5,
        },
        "uni_subg_sp_pos": {
            "lr": 0.1,
            "beta": 0.01,
            "n_latent": 8,
            "epoch": 5,
        },
        "uni_subg_sp": {
            "lr": 0.1,
            "beta": 0.01,
            "n_latent": 8,
            "epoch": 5,
        },
        "uni_subs_sp_pos": {
            "lr": 0.001,
            "beta": 0.01,
            "n_latent": 8,
            "epoch": 5,
        },
        "uni_subt_sp_pos": {
            "lr": 0.0001,
            "beta": 0.01,
            "n_latent": 8,
            "epoch": 5,
        },
        "swat-p": {
            "lr": 0.008,
            "beta": 0.01,
            "n_latent": 8,
            "epoch": 5,
        },
    },
    "OmniAnomalyWithPOT": {
        "twitter": {
            "lr": 0.0001,
            "beta": 0.01,
            "n_latent": 8,
            "lm": (0.7, 1.0),
            "epoch": 5,
        },
        "yahoo": {
            "lr": 0.01,
            "beta": 0.01,
            "n_latent": 8,
            "lm": (1.2, 3),
            "epoch": 5,
        },
        "tao_pointg": {
            "lr": 0.01,
            "beta": 0.01,
            "n_latent": 8,
            "lm": (0.95, 0.99),
            "epoch": 5,
        },
        "ecg": {
            "lr": 0.01,
            "beta": 0.01,
            "n_latent": 8,
            "lm": (0.95, 0.99),
            "epoch": 5,
        },
        "smtp": {
            "lr": 0.0001,
            "beta": 0.01,
            "n_latent": 8,
            "lm": (0.95, 0.99),
            "epoch": 5,
        },
        "stock_pointg": {
            "lr": 0.01,
            "beta": 0.01,
            "n_latent": 8,
            "lm": (1.0, 1.0),
            "epoch": 5,
        },
        "uni_pointg": {
            "lr": 0.01,
            "beta": 0.01,
            "n_latent": 8,
            "lm": (1.0, 1.0),
            "epoch": 5,
        },
        "credit": {
            "lr": 0.01,
            "beta": 0.01,
            "n_latent": 8,
            "lm": (0.95, 0.99),
            "epoch": 5,
        },
        "mul_pointg": {
            "lr": 0.001,
            "beta": 0.01,
            "n_latent": 8,
            "lm": (0.95, 0.99),
            "epoch": 5,
        },
        "dlr": {
            "lr": 0.1,
            "beta": 0.01,
            "n_latent": 8,
            "lm": (0.95, 0.99),
            "epoch": 5,
        },
        "exathlon_sp_pos": {
            "lr": 0.0001,
            "beta": 0.01,
            "n_latent": 8,
            "lm": (0.95, 0.99),
            "epoch": 5,
        },
        "uni_subg_sp_pos": {
            "lr": 0.1,
            "beta": 0.01,
            "n_latent": 8,
            "lm": (0.95, 0.99),
            "epoch": 5,
        },
        "uni_subs_sp_pos": {
            "lr": 0.001,
            "beta": 0.01,
            "n_latent": 8,
            "lm": (0.95, 0.99),
            "epoch": 5,
        },
        "uni_subt_sp_pos": {
            "lr": 0.0001,
            "beta": 0.01,
            "n_latent": 8,
            "lm": (0.95, 0.99),
            "epoch": 5,
        },
        "swat-p": {
            "lr": 0.008,
            "beta": 0.01,
            "n_latent": 8,
            "lm": (0.95, 0.99),
            "epoch": 5,
        },
    },
    "TranAD": {
        "twitter": {
            "lr": 0.0001,
            "batch": 128,
            "n_window": 10,
            "epoch": 5
        },
        "yahoo": {
            "lr": 0.01,
            "batch": 128,
            "n_window": 10,
            "epoch": 5
        },
        "tao_pointg": {
            "lr": 0.001,
            "batch": 128,
            "n_window": 10,
            "epoch": 5
        },
        "ecg": {
            "lr": 0.1,
            "batch": 128,
            "n_window": 10,
            "epoch": 5
        },
        "smtp": {
            "lr": 0.01,
            "batch": 128,
            "n_window": 10,
            "epoch": 5
        },
        "stock_pointg": {
            "lr": 0.1,
            "batch": 128,
            "n_window": 10,
            "epoch": 5
        },
        "uni_pointg": {
            "lr": 0.1,
            "batch": 128,
            "n_window": 10,
            "epoch": 5
        },
        "credit": {
            "lr": 0.001,
            "batch": 128,
            "n_window": 10,
            "epoch": 5
        },
        "mul_pointg": {
            "lr": 0.001,
            "batch": 128,
            "n_window": 10,
            "epoch": 5
        },
        "dlr": {
            "lr": 1,
            "batch": 128,
            "n_window": 10,
            "epoch": 5
        },
        "exathlon_sp_pos": {
            "lr": 0.01,
            "batch": 128,
            "n_window": 10,
            "epoch": 5
        },
        "uni_subg_sp_pos": {
            "lr": 0.001,
            "batch": 128,
            "n_window": 10,
            "epoch": 5
        },
        "uni_subg_sp": {
            "lr": 0.001,
            "batch": 128,
            "n_window": 10,
            "epoch": 5
        },
        "uni_subs_sp_pos": {
            "lr": 0.001,
            "batch": 128,
            "n_window": 10,
            "epoch": 5
        },
        "uni_subt_sp_pos": {
            "lr": 0.0001,
            "batch": 128,
            "n_window": 10,
            "epoch": 5
        },
        "swat-p": {
            "lr": 0.008,
            "batch": 128,
            "n_window": 10,
            "epoch": 5
        },
    },
    "TranADWithPOT": {
        "twitter": {
            "lr": 0.0001,
            "batch": 128,
            "n_window": 10,
            "lm": (0.7, 1.0),
            "epoch": 5
        },
        "yahoo": {
            "lr": 0.01,
            "batch": 128,
            "n_window": 10,
            "lm": (1.2, 3),
            "epoch": 5
        },
        "tao_pointg": {
            "lr": 0.001,
            "batch": 128,
            "n_window": 10,
            "lm": (0.95, 0.99),
            "epoch": 5
        },
        "ecg": {
            "lr": 0.1,
            "batch": 128,
            "n_window": 10,
            "lm": (0.95, 0.99),
            "epoch": 5
        },
        "smtp": {
            "lr": 0.01,
            "batch": 128,
            "n_window": 10,
            "lm": (0.95, 0.99),
            "epoch": 5
        },
        "stock_pointg": {
            "lr": 0.1,
            "batch": 128,
            "n_window": 10,
            "lm": (1.0, 1.0),
            "epoch": 5
        },
        "uni_pointg": {
            "lr": 0.1,
            "batch": 128,
            "n_window": 10,
            "lm": (1.0, 1.0),
            "epoch": 5
        },
        "credit": {
            "lr": 0.001,
            "batch": 128,
            "n_window": 10,
            "lm": (0.95, 0.99),
            "epoch": 5
        },
        "mul_pointg": {
            "lr": 0.001,
            "batch": 128,
            "n_window": 10,
            "lm": (0.95, 0.99),
            "epoch": 5
        },
        "dlr": {
            "lr": 1,
            "batch": 128,
            "n_window": 10,
            "lm": (0.95, 0.99),
            "epoch": 5
        },
        "exathlon_sp_pos": {
            "lr": 0.01,
            "batch": 128,
            "n_window": 10,
            "lm": (0.95, 0.99),
            "epoch": 5
        },
        "uni_subg_sp_pos": {
            "lr": 0.001,
            "batch": 128,
            "n_window": 10,
            "lm": (0.95, 0.99),
            "epoch": 5
        },
        "uni_subs_sp_pos": {
            "lr": 0.001,
            "batch": 128,
            "n_window": 10,
            "lm": (0.95, 0.99),
            "epoch": 5
        },
        "uni_subt_sp_pos": {
            "lr": 0.0001,
            "batch": 128,
            "n_window": 10,
            "lm": (0.95, 0.99),
            "epoch": 5
        },
        "swat-p": {
            "lr": 0.008,
            "batch": 128,
            "n_window": 10,
            "lm": (0.95, 0.99),
            "epoch": 5
        },
    },
    "GDN": {
        "twitter": {
            "lr": 0.1,
            "n_window": 5,
            "epoch": 5
        },
        "yahoo": {
            "lr": 0.01,
            "n_window": 5,
            "epoch": 5
        },
        "tao_pointg": {
            "lr": 0.001,
            "n_window": 5,
            "epoch": 5
        },
        "ecg": {
            "lr": 0.01,
            "n_window": 5,
            "epoch": 5
        },
        "smtp": {
            "lr": 0.0001,
            "n_window": 5,
            "epoch": 5
        },
        "stock_pointg": {
            "lr": 0.001,
            "n_window": 5,
            "epoch": 5
        },
        "uni_pointg": {
            "lr": 0.1,
            "n_window": 5,
            "epoch": 5
        },
        "credit": {
            "lr": 0.01,
            "n_window": 5,
            "epoch": 5
        },
        "mul_pointg": {
            "lr": 0.001,
            "n_window": 5,
            "epoch": 5
        },
        "dlr": {
            "lr": 0.0001,
            "n_window": 5,
            "epoch": 5
        },
        "exathlon_sp_pos": {
            "lr": 0.001,
            "n_window": 5,
            "epoch": 5
        },
        "uni_subg_sp_pos": {
            "lr": 0.1,
            "n_window": 5,
            "epoch": 5
        },
        "uni_subs_sp_pos": {
            "lr": 0.0001,
            "n_window": 5,
            "epoch": 5
        },
        "uni_subg_sp": {
            "lr": 0.1,
            "n_window": 5,
            "epoch": 5
        },
        "uni_subt_sp_pos": {
            "lr": 0.1,
            "n_window": 5,
            "epoch": 5
        },
        "swat-p": {
            "lr": 0.008,
            "n_window": 5,
            "epoch": 5
        },
    },
    "GDNWithPOT": {
        "twitter": {
            "lr": 0.1,
            "n_window": 5,
            "lm": (0.7, 1.0),
            "epoch": 5
        },
        "yahoo": {
            "lr": 0.01,
            "n_window": 5,
            "lm": (1.2, 3),
            "epoch": 5
        },
        "tao_pointg": {
            "lr": 0.001,
            "n_window": 5,
            "lm": (0.95, 0.99),
            "epoch": 5
        },
        "ecg": {
            "lr": 0.01,
            "n_window": 5,
            "lm": (0.95, 0.99),
            "epoch": 5
        },
        "smtp": {
            "lr": 0.0001,
            "n_window": 5,
            "lm": (0.95, 0.99),
            "epoch": 5
        },
        "stock_pointg": {
            "lr": 0.001,
            "n_window": 5,
            "lm": (1.0, 1.0),
            "epoch": 5
        },
        "uni_pointg": {
            "lr": 0.1,
            "n_window": 5,
            "lm": (1.0, 1.0),
            "epoch": 5
        },
        "credit": {
            "lr": 0.01,
            "n_window": 5,
            "lm": (0.95, 0.99),
            "epoch": 5
        },
        "mul_pointg": {
            "lr": 0.001,
            "n_window": 5,
            "lm": (0.95, 0.99),
            "epoch": 5
        },
        "dlr": {
            "lr": 0.0001,
            "n_window": 5,
            "lm": (0.95, 0.99),
            "epoch": 5
        },
        "exathlon_sp_pos": {
            "lr": 0.001,
            "n_window": 5,
            "lm": (0.95, 0.99),
            "epoch": 5
        },
        "uni_subg_sp_pos": {
            "lr": 0.1,
            "n_window": 5,
            "lm": (0.95, 0.99),
            "epoch": 5
        },
        "uni_subs_sp_pos": {
            "lr": 0.0001,
            "n_window": 5,
            "lm": (0.95, 0.99),
            "epoch": 5
        },
        "uni_subt_sp_pos": {
            "lr": 0.1,
            "n_window": 5,
            "lm": (0.95, 0.99),
            "epoch": 5
        },
        "swat-p": {
            "lr": 0.008,
            "n_window": 5,
            "lm": (0.95, 0.99),
            "epoch": 5
        },
    },
    "RCoder": {
        "twitter": {
            "S": 5,
            "delta": 0.05,
            "batch_size": 180,
            "freq_warmup": 10,
            "sin_warmup": 10,
            "epoch": 5
        },
        "yahoo": {
            "S": 5,
            "delta": 0.05,
            "batch_size": 180,
            "freq_warmup": 10,
            "sin_warmup": 10,
            "epoch": 5
        },
        "tao_pointg": {
            "S": 5,
            "delta": 0.05,
            "batch_size": 180,
            "freq_warmup": 10,
            "sin_warmup": 10,
            "epoch": 5
        },
        "ecg": {
            "S": 5,
            "delta": 0.05,
            "batch_size": 180,
            "freq_warmup": 10,
            "sin_warmup": 10,
            "epoch": 5
        },
        "smtp": {
            "S": 5,
            "delta": 0.05,
            "batch_size": 180,
            "freq_warmup": 10,
            "sin_warmup": 10,
            "epoch": 5
        },
        "stock_pointg": {
            "S": 5,
            "delta": 0.05,
            "batch_size": 180,
            "freq_warmup": 10,
            "sin_warmup": 10,
            "epoch": 5
        },
        "uni_pointg": {
            "S": 5,
            "delta": 0.05,
            "batch_size": 180,
            "freq_warmup": 10,
            "sin_warmup": 10,
            "epoch": 5
        },
        "credit": {
            "S": 5,
            "delta": 0.05,
            "batch_size": 180,
            "freq_warmup": 10,
            "sin_warmup": 10,
            "epoch": 5
        },
        "mul_pointg": {
            "S": 5,
            "delta": 0.05,
            "batch_size": 180,
            "freq_warmup": 10,
            "sin_warmup": 10,
            "epoch": 5
        },
        "dlr": {
            "S": 5,
            "delta": 0.05,
            "batch_size": 180,
            "freq_warmup": 10,
            "sin_warmup": 10,
            "epoch": 5
        },
        "exathlon_sp_pos": {
            "S": 5,
            "delta": 0.05,
            "batch_size": 180,
            "freq_warmup": 10,
            "sin_warmup": 10,
            "epoch": 5
        },
        "uni_subg_sp_pos": {
            "S": 5,
            "delta": 0.05,
            "batch_size": 180,
            "freq_warmup": 10,
            "sin_warmup": 10,
            "epoch": 5
        },
        "uni_subs_sp_pos": {
            "S": 5,
            "delta": 0.05,
            "batch_size": 180,
            "freq_warmup": 10,
            "sin_warmup": 10,
            "epoch": 5
        },
        "uni_subt_sp_pos": {
            "S": 5,
            "delta": 0.05,
            "batch_size": 180,
            "freq_warmup": 10,
            "sin_warmup": 10,
            "epoch": 5
        },
        "swat-p": {
            "S": 5,
            "delta": 0.05,
            "batch_size": 180,
            "freq_warmup": 10,
            "sin_warmup": 10,
            "epoch": 5
        },
    },
    # sub
    "NormA": {
        "power": {
            'pattern_length': 700,
            'nm_size': 1750,
            'top_k': 3
        },
        'sed': {
            'pattern_length': 64,
            'nm_size': 256,
            'top_k': 33
        },
        'taxi': {
            'pattern_length': 200,
            'nm_size': 500,
            'top_k': 5
        },
        'machine': {
            'pattern_length': 550,
            'nm_size': 1100,
            'top_k': 4
        },
        'exercise': {
            'pattern_length': 140,
            'nm_size': 560,
            'top_k': 8
        },
        'exathlon': {
            'pattern_length': 55,
            'nm_size': 220,
            'top_k': 7
        },
        'smd': {
            'pattern_length': 260,
            'nm_size': 1040,
            'top_k': 6
        },
        'swat': {
            'pattern_length': 90,
            'nm_size': 360,
            'top_k': 110
        },
        'uni_subg_rate': {
            'pattern_length': 45,
            'nm_size': 180,
            'top_k': 4
        },
        'uni_subs_rate': {
            'pattern_length': 45,
            'nm_size': 180,
            'top_k': 10
        },
        'uni_subg_size': {
            'pattern_length': 45,
            'nm_size': 180,
            'top_k': 4
        },
        'uni_subs_size': {
            'pattern_length': 45,
            'nm_size': 180,
            'top_k': 10
        },
        'sed_size': {
            'pattern_length': 64,
            'nm_size': 256,
            'top_k': 10
        },
        'uni_subg_sp': {
            'pattern_length': 45,
            'nm_size': 180,
            'top_k': 11
        },
        'uni_subs_sp': {
            'pattern_length': 45,
            'nm_size': 180,
            'top_k': 11
        },
        'uni_subt_sp': {
            'pattern_length': 50,
            'nm_size': 200,
            'top_k': 10
        },
        'exathlon_sp': {
            'pattern_length': 50,
            'nm_size': 200,
            'top_k': 8
        },
        'mul_cor_subg': {
            'pattern_length': 50,
            'nm_size': 200,
            'top_k': 11
        },
        'mul_ncor_subg': {
            'pattern_length': 50,
            'nm_size': 200,
            'top_k': 11
        },
    },
    'BeatGAN': {
        'machine': {
            'lr': 0.01,
            'seq_len': 512,
            'hidden_size': 100,
            'rep_size': 20,
            'batch_size': 64,
            'max_epoch': 10,
            'lambdaa': 1,
            'network': 'CNN',
            'top_k': 2
        },
        'taxi': {
            'lr': 0.01,
            'seq_len': 256,
            'hidden_size': 100,
            'rep_size': 20,
            'batch_size': 64,
            'max_epoch': 10,
            'lambdaa': 0.1,
            'network': 'CNN',
            'top_k': 4
        },
        'power': {
            'lr': 0.01,
            'seq_len': 512,
            'hidden_size': 100,
            'rep_size': 20,
            'batch_size': 64,
            'max_epoch': 10,
            'lambdaa': 0.1,
            'network': 'CNN',
            'top_k': 3
        },
        'sed': {
            'lr': 0.01,
            'seq_len': 64,
            'hidden_size': 100,
            'rep_size': 20,
            'batch_size': 64,
            'max_epoch': 10,
            'lambdaa': 0.1,
            'network': 'CNN',
            'top_k': 22
        },
        'exercise': {
            'lr': 0.01,
            'seq_len': 128,
            'hidden_size': 100,
            'rep_size': 20,
            'batch_size': 64,
            'max_epoch': 10,
            'lambdaa': 0.1,
            'network': 'CNN',
            'top_k': 9
        },
        'exathlon': {
            'lr': 0.01,
            'seq_len': 128,
            'hidden_size': 100,
            'rep_size': 20,
            'batch_size': 64,
            'max_epoch': 10,
            'lambdaa': 0.1,
            'network': 'CNN',
            'top_k': 7
        },
        'smd': {
            'lr': 0.01,
            'seq_len': 512,
            'hidden_size': 100,
            'rep_size': 20,
            'batch_size': 64,
            'max_epoch': 10,
            'lambdaa': 1,
            'network': 'CNN',
            'top_k': 6
        },
        'swat': {
            'lr': 0.01,
            'seq_len': 128,
            'hidden_size': 100,
            'rep_size': 20,
            'batch_size': 64,
            'max_epoch': 10,
            'lambdaa': 0.1,
            'network': 'CNN',
            'top_k': 110
        },
        'uni_subg_rate': {
            'lr': 0.01,
            'seq_len': 64,
            'hidden_size': 100,
            'rep_size': 50,
            'batch_size': 64,
            'max_epoch': 10,
            'lambdaa': 0.01,
            'network': 'CNN',
            'top_k': 8
        },
        'uni_subs_rate': {
            'lr': 0.01,
            'seq_len': 64,
            'hidden_size': 100,
            'rep_size': 20,
            'batch_size': 64,
            'max_epoch': 10,
            'lambdaa': 0.1,
            'network': 'CNN',
            'top_k': 4
        },
        'mul_subg_rate': {
            'lr': 0.01,
            'seq_len': 64,
            'hidden_size': 100,
            'rep_size': 20,
            'batch_size': 64,
            'max_epoch': 10,
            'lambdaa': 1,
            'network': 'CNN',
            'top_k': 4
        },
        'mul_subs_rate': {
            'lr': 0.01,
            'seq_len': 64,
            'hidden_size': 100,
            'rep_size': 20,
            'batch_size': 64,
            'max_epoch': 10,
            'lambdaa': 0.1,
            'network': 'CNN',
            'top_k': 4
        },
        'uni_subg_size': {
            'lr': 0.01,
            'seq_len': 64,
            'hidden_size': 100,
            'rep_size': 50,
            'batch_size': 64,
            'max_epoch': 10,
            'lambdaa': 0.01,
            'network': 'CNN',
            'top_k': 8
        },
        'uni_subs_size': {
            'lr': 0.01,
            'seq_len': 64,
            'hidden_size': 100,
            'rep_size': 20,
            'batch_size': 64,
            'max_epoch': 10,
            'lambdaa': 0.1,
            'network': 'CNN',
            'top_k': 4
        },
        'sed_size': {
            'lr': 0.01,
            'seq_len': 64,
            'hidden_size': 100,
            'rep_size': 20,
            'batch_size': 64,
            'max_epoch': 10,
            'lambdaa': 0.1,
            'network': 'CNN',
            'top_k': 22
        },
        'mul_subg_size': {
            'lr': 0.01,
            'seq_len': 64,
            'hidden_size': 100,
            'rep_size': 20,
            'batch_size': 64,
            'max_epoch': 10,
            'lambdaa': 1,
            'network': 'CNN',
            'top_k': 4
        },
        'mul_subs_size': {
            'lr': 0.01,
            'seq_len': 64,
            'hidden_size': 100,
            'rep_size': 20,
            'batch_size': 64,
            'max_epoch': 10,
            'lambdaa': 0.1,
            'network': 'CNN',
            'top_k': 4
        },
        'swat_size': {
            'lr': 0.01,
            'seq_len': 128,
            'hidden_size': 100,
            'rep_size': 20,
            'batch_size': 64,
            'max_epoch': 10,
            'lambdaa': 0.1,
            'network': 'CNN',
            'top_k': 26
        },
        'mul_subg_dim': {
            'lr': 0.01,
            'seq_len': 64,
            'hidden_size': 100,
            'rep_size': 20,
            'batch_size': 64,
            'max_epoch': 10,
            'lambdaa': 0.01,
            'network': 'CNN',
            'top_k': 18
        },
        # type
        'uni_subg_sp': {
            'lr': 0.01,
            'seq_len': 64,
            'hidden_size': 100,
            'rep_size': 50,
            'batch_size': 64,
            'max_epoch': 10,
            'lambdaa': 0.01,
            'network': 'CNN',
            'top_k': 8
        },
        'uni_subs_sp': {
            'lr': 0.01,
            'seq_len': 64,
            'hidden_size': 100,
            'rep_size': 20,
            'batch_size': 64,
            'max_epoch': 10,
            'lambdaa': 0.1,
            'network': 'CNN',
            'top_k': 10
        },
        'uni_subt_sp': {
            'lr': 0.01,
            'seq_len': 64,
            'hidden_size': 100,
            'rep_size': 20,
            'batch_size': 64,
            'max_epoch': 10,
            'lambdaa': 1,
            'network': 'CNN',
            'top_k': 11
        },
        'exathlon_sp': {
            'lr': 0.01,
            'seq_len': 128,
            'hidden_size': 100,
            'rep_size': 20,
            'batch_size': 64,
            'max_epoch': 10,
            'lambdaa': 0.1,
            'network': 'CNN',
            'top_k': 8
        },
        'mul_cor_subg': {
            'lr': 0.01,
            'seq_len': 64,
            'hidden_size': 100,
            'rep_size': 20,
            'batch_size': 64,
            'max_epoch': 10,
            'lambdaa': 1,
            'network': 'CNN',
            'top_k': 10
        },
        'mul_ncor_subg': {
            'lr': 0.01,
            'seq_len': 64,
            'hidden_size': 100,
            'rep_size': 50,
            'batch_size': 64,
            'max_epoch': 10,
            'lambdaa': 1,
            'network': 'CNN',
            'top_k': 10
        },
    }
}

dataSetsParameters = {
    # point
    "yahoo": {
        "dir": "point\\uni\\yahoo",
        "prefix": "yahoo",
        "odir": "origin",
        "tdir": "train",
        "vdir": "valid", 
        "tedir": "test"
    },
    "credit": {
        "dir": "point\\mul\\Credit",
        "prefix": "Credit",
        "odir": "origin",
        "tdir": "train",
        "vdir": "valid", 
        "tedir": "test"
    },
    "uni_pointg": {
        "dir": "point\\uni\\Uni",
        "prefix": "uni_pointg",
        "odir": "origin",
        "tdir": "train",
        "vdir": "valid", 
        "tedir": "test"
    },
    "twitter": {
        "dir": "point\\uni\\Twitter",
        "prefix": "twitter",
        "odir": "origin",
        "tdir": "train",
        "vdir": "valid", 
        "tedir": "test"
    },
    "stock_pointg": {
        "dir": "point\\uni\\stock",
        "prefix": "stock_pointg",
        "odir": "origin",
        "tdir": "train",
        "vdir": "valid", 
        "tedir": "test",
    },
    "ecg": {
        "dir": "point\\mul\\ECG",
        "prefix": "ecg",
        "odir": "origin",
        "tdir": "train",
        "vdir": "valid", 
        "tedir": "test"
    },
    "ECG_5k_num_765": {
        "dir": "point\\mul\\ECG",
        "prefix": "",
        "odir": "origin",
        "tdir": "train",
        "vdir": "valid", 
        "tedir": "test"
    },
    "pen": {
        "dir": "point\\mul\\Pen",
        "prefix": "pen",
        "odir": "origin",
        "tdir": "train",
        "vdir": "valid", 
        "tedir": "test"
    },
    "smtp": {
        "dir": "point\\mul\\SMTP",
        "prefix": "smtp",
        "odir": "origin",
        "tdir": "train",
        "vdir": "valid", 
        "tedir": "test"
    },
    "tao_pointg": {
        "dir": "point\\mul\\tao",
        "prefix": "tao_pointg",
        "odir": "origin",
        "tdir": "train",
        "vdir": "valid", 
        "tedir": "test"
    },
    "mul_pointg": {
        "dir": "point\\mul\\mul",
        "prefix": "mul_pointg",
        "odir": "origin",
        "tdir": "train",
        "vdir": "valid", 
        "tedir": "test"
    },
    "dlr": {
        "dir": "point\\mul\\dlr",
        "prefix": "dlr",
        "odir": "origin",
        "tdir": "train",
        "vdir": "valid", 
        "tedir": "test"
    },
    "exathlon_sp_pos": {
        "dir": "point\\other\\exathlon_sp_pos",
        "prefix": "exathlon_sp_pos",
        "odir": "origin",
        "tdir": "train",
        "vdir": "valid", 
        "tedir": "test"
    },
    "uni_subg_sp_pos": {
        "dir": "point\\other\\uni_subg_sp_pos",
        "prefix": "uni_subg_sp_pos",
        "odir": "origin",
        "tdir": "train",
        "vdir": "valid", 
        "tedir": "test"
    },
    "uni_subs_sp_pos": {
        "dir": "point\\other\\uni_subs_sp_pos",
        "prefix": "uni_subs_sp_pos",
        "odir": "origin",
        "tdir": "train",
        "vdir": "valid", 
        "tedir": "test"
    },
    "uni_subt_sp_pos": {
        "dir": "point\\other\\uni_subt_sp_pos",
        "prefix": "uni_subt_sp_pos",
        "odir": "origin",
        "tdir": "train",
        "vdir": "valid", 
        "tedir": "test"
    },
    "swat-p": {
        "dir": "point\\other\\swat",
        "prefix": "swat",
        "odir": "origin",
        "tdir": "train",
        "vdir": "valid", 
        "tedir": "test"
    },
    # sub
    "power": {
        "dir": 'sub\\power',
        'prefix': 'power',
        "tdir": "train",
        "vdir": "valN",
        "tedir": "test"

    },
    'sed': {
        'dir': 'sub\\sed',
        'prefix': 'sed',
        'tdir': 'train',
        'vdir': 'valN',
        'tedir': 'test'
    },
    'taxi': {
        'dir': 'sub\\taxi',
        'prefix': 'taxi',
        'tdir': 'train',
        'vdir': 'valN',
        'tedir': 'test'
    },
    'machine': {
        'dir': 'sub\\machine',
       'prefix': 'machine',
        'tdir': 'train',
        'vdir': 'valN',
        'tedir': 'test'
    },
    'exercise': {
        'dir': 'sub\\exercise',
        'prefix': 'exercise',
        'tdir': 'train',
        'vdir': 'valN',
        'tedir': 'test'
    },
    'exathlon': {
        'dir': 'sub\\exathlon',
        'prefix': 'exathlon',
        'tdir': 'train',
        'vdir': 'valN',
        'tedir': 'test'
    },
    'smd': {
        'dir': 'sub\\smd',
        'tdir': 'train',
        'vdir': 'valN',
        'tedir': 'test'
    },
    'swat': {
        'dir': 'sub\\swat',
        'prefix': 'swat',
        'tdir': 'train',
        'vdir': 'valN',
        'tedir': 'test'
    },
    # sub_rate
    'uni_subg_rate': {
        'dir': 'syn\\sub\\uni_subg_rate',
        'prefix': 'uni',
        'tdir': 'train',
        'vdir': 'valN',
        'tedir': 'test'
    },
    'uni_subs_rate': {
        'dir': 'syn\\sub\\uni_subs_rate',
        'prefix': 'uni',
        'tdir': 'train',
        'vdir': 'valN',
        'tedir': 'test'
    },
    'uni_subt_rate': {
        'dir': 'syn\\sub\\uni_subt_rate',
        'prefix': 'uni',
        'tdir': 'train',
        'vdir': 'valN',
        'tedir': 'test'
    },
    'mul_subg_rate': {
        'dir': 'syn\\sub\\mul_subg_rate',
        'prefix': 'mul',
        'tdir': 'train',
        'vdir': 'valN',
        'tedir': 'test'
    },
    'mul_subs_rate': {
        'dir': 'syn\\sub\\mul_subs_rate',
        'prefix': 'mul',
        'tdir': 'train',
        'vdir': 'valN',
        'tedir': 'test'
    },
    # sub_size
    'uni_subg_size': {
        'dir': 'syn\\sub\\uni_subg_size',
        'prefix': 'uni',
        'tdir': 'train',
        'vdir': 'valN',
        'tedir': 'test'
    },
    'uni_subs_size': {
        'dir': 'syn\\sub\\uni_subs_size',
        'prefix': 'uni',
        'tdir': 'train',
        'vdir': 'valN',
        'tedir': 'test'
    },
    'sed_size': {
        'dir': 'sub\\sed_size',
        'prefix': 'sed',
        'tdir': 'train',
        'vdir': 'valN',
        'tedir': 'test'
    },
    'mul_subg_size': {
        'dir': 'syn\\sub\\mul_subg_size',
        'prefix': 'mul',
        'tdir': 'train',
        'vdir': 'valN',
        'tedir': 'test'
    },
    'mul_subs_size': {
        'dir': 'syn\\sub\\mul_subs_size',
        'prefix': 'mul',
        'tdir': 'train',
        'vdir': 'valN',
        'tedir': 'test'
    },
    'swat_size': {
        'dir': 'sub\\swat_size',
        'prefix': 'swat',
        'tdir': 'train',
        'vdir': 'valN',
        'tedir': 'test'
    },
    # sub_dim
    'mul_subg_dim': {
        'dir': 'syn\\sub\\mul_subg_dim',
        'prefix': 'mul_subg',
        'tdir': 'train',
        'vdir': 'valN',
        'tedir': 'test'
    },
    # sub_type
    'uni_subg_sp': {
        'dir': 'syn\\sub\\uni_subg_sp',
        'prefix': 'uni_subg_sp',
        'tdir': 'train',
        'vdir': 'valN',
        'tedir': 'test'
    },
    'uni_subs_sp': {
        'dir': 'syn\\sub\\uni_subs_sp',
        'prefix': 'uni_subs_sp',
        'tdir': 'train',
        'vdir': 'valN',
        'tedir': 'test'
    },
    'uni_subt_sp': {
        'dir': 'syn\\sub\\uni_subt_sp',
        'prefix': 'uni_subt_sp',
        'tdir': 'train',
        'vdir': 'valN',
        'tedir': 'test'
    },
    'exathlon_sp': {
        'dir': 'sub\\exathlon_sp',
        'prefix': 'exathlon_sp',
        'tdir': 'train',
        'vdir': 'valN',
        'tedir': 'test'
    },
    # um
    'mul_cor_subg': {
        'dir': 'syn\\sub\\mul_cor_subg',
        'prefix': 'mul_cor_subg_len_50_5000_0.1_1',
        'tdir': 'train',
        'vdir': 'valN',
        'tedir': 'test'
    },
    'mul_ncor_subg': {
        'dir': 'syn\\sub\\mul_ncor_subg',
        'prefix': 'mul_ncor_subg_len_50_5000_0.1_1',
        'tdir': 'train',
        'vdir': 'valN',
        'tedir': 'test'
    }
}
