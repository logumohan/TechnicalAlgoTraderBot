import pickle as pkl
import pandas as pd
with open("APOLLOHOSP-5minute-Hist", "rb") as f:
    object = pkl.load(f)
    
df = pd.DataFrame(object)
df.to_csv(r'APOLLOHOSP-5minute-Hist.csv')