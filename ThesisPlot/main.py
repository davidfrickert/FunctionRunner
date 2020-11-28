import glob

import pandas as pd
import matplotlib.pyplot as plt
import matplotlib.cbook as cbook
import numpy as np


def plotDF(df: pd.DataFrame):
    HEAP_BEFORE: pd.DataFrame = df.HEAP_BEFORE
    HEAP_AFTER: pd.DataFrame = df.HEAP_AFTER
    HEAP_DIFF: pd.DataFrame = HEAP_BEFORE.subtract(HEAP_AFTER)

    HEAP_DIFF.plot()

    plt.legend()


def plot_time_taken_with_gc_timestamp(df: pd.DataFrame, name: str, ax=None):
    timetaken = f'TIME_TAKEN-{name}'
    heapbefore = f'HEAP_BEFORE-{name}'
    heapafter = f'HEAP_AFTER-{name}'

    df.rename(columns={'TIME_TAKEN': timetaken,
                       'HEAP_BEFORE': heapbefore,
                       'HEAP_AFTER': heapafter
                       }

              , inplace=True)



    if ax:
        _ax = df.plot(x='GC_TIMESTAMP', y=timetaken, ax=ax, alpha=1)
    else:
        _ax = df.plot(x='GC_TIMESTAMP', y=timetaken, alpha=1)

    df.plot.area(x='GC_TIMESTAMP', y=heapbefore, ax=_ax, secondary_y=True, alpha=0.2)
    df.plot.area(x='GC_TIMESTAMP', y=heapafter, ax=_ax, secondary_y=True, alpha=0.3)

    plt.legend()
    return _ax


#isol: pd.DataFrame = pd.read_csv('log/isolate-sequentialbench13581015-19:44_2020-11-18')
#no_isol: pd.DataFrame = pd.read_csv('log/noisolate-sequentialbench13581015-17:34_2020-11-21')
isol: pd.DataFrame = pd.read_csv(glob.glob('log/isolate-se*')[0])
no_isol: pd.DataFrame = pd.read_csv(glob.glob('log/noisolate-se*')[0])
othernoisol: pd.DataFrame = pd.read_csv(glob.glob('log/reg*')[0])
otherisol: pd.DataFrame = pd.read_csv(glob.glob('log/log*')[0])

#plotDF(isol)
#plotDF(no_isol)
#plt.show()
ax = None
#ax = \
#plot_time_taken_with_gc_timestamp(isol, "isolate")
#plot_time_taken_with_gc_timestamp(no_isol, "no isolate", ax=ax)

plot_time_taken_with_gc_timestamp(othernoisol, "regular", ax=ax)
plot_time_taken_with_gc_timestamp(otherisol, "isolate", ax=ax)


plt.show()

# alterar o eixo do X para ser o tempo em que ocorreu o GC
# correr os benchmarks com c=1
# comparar isolates com implementação original dos photons
# logar latencia de requests

# logar latencia de requests
