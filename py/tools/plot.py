import matplotlib.pyplot as plt
import pandas as pd



df1 = pd.read_csv('OmniAnomaly_uni_pointg_20230322.csv')
data = df1[df1["epoch"]==800]
data = data[data["lr"]==0.01]
plt.plot(data['currepoch'],data['MSE'])
plt.savefig("OmniAnomaly_uni_pointg_20230322")

#df2 = pd.read_csv('100.csv')
#plt.plot(df2['currepoch'],df2['L1'])
#plt.savefig("100")

#df3 = pd.read_csv('400.csv')
#plt.plot(df3['currepoch'],df3['L1'])
#plt.savefig("400")
