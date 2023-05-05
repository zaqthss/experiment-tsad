from typing import Dict

from entity import *
from .UFEKTS.ufekt_method import *
from .algorithm import Algorithm


class UFEKT(Algorithm):
    def __init__(self):
        super(UFEKT, self).__init__()
        self.series = None

    def init(self, args: Dict, series: timeSeriesMul):
        self.series = series
        self.max_rank = args.get('max_rank')
        self.min_rank = args.get('min_rank')
        self.window_size=args.get('window_size')
        self.sigma=args.get('sigma')
        self.knn_k=args.get('knn_k')
        self.top_k=args.get('top_k')
        self.od=True

    def run(self):
        x=[]
        for i in range(self.series.dim):
            ts=self.series.getunibydim(i)
            xt=[]
            for i,p in enumerate(ts.timeseries):
                xt.append(p.observe)
            x.append(xt)
        x=np.array(x)
        x=x.T
        UFEKT_rank_range = [x.shape[1] - 1, self.max_rank, self.max_rank]

        core, factors, tensor, ranks, df_opt = ufekt(
            x[:, 0:], width=self.window_size, sigma=self.sigma, rank_range=UFEKT_rank_range,
            rank_th=self.min_rank, seed=0)
        if self.od:
            score_pred, outlier_rows = nn(factors[1], dis_th=0.01, _n_neighbors=self.knn_k, _algorithm='ball_tree')
            score=score_pred[:,(self.knn_k-1)]
            score = np.argsort(score)
            score = np.flipud(score)
            reseries = self.series.copy()
            reseries.clear()
            for k in range(self.top_k):
                index = score[0]
                for i in range(index, index + self.window_size):
                    reseries.timeseries[i].is_anomaly = True
                score = drop_score(score, index, pattern_length=self.window_size, len=len(self.series.timeseries))
                if len(score) == 0:
                    break
            return reseries

def drop_score(all_score,index_start,pattern_length,len):
	new_all_score=[]
	minn = max(index_start - pattern_length, 0)
	maxx = min(index_start + pattern_length, len)
	for s in all_score:
		if s>=minn and s<maxx:
			continue
		else:
			new_all_score.append(s)
	return new_all_score


