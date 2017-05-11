import numpy as np

g = {1: [2, 5, 8, 17],
     3: [4, 6, 19],
     5: [3],
     7: [1, 8],
     11: [16],
     14: [5],
     17: [2],
     20: [15]}


def salsa(hub_ids, auth_ids, g):
    # create map from matrix index to id in graph
    hlen = len(hub_ids)
    alen = len(auth_ids)

    hmap = dict([p for p in enumerate(hub_ids)])
    amap = dict([p for p in enumerate(auth_ids)])

    # compute hub score
    H = np.zeros((hlen, hlen))
    for rowid in range(hlen):
        for colid in range(hlen):
            pass


def bipartitify(base_set, g):
    auth_set = set()
    edge_set = set()
    gkeys = g.keys()

    for fid in base_set:
        if fid in gkeys:
            for tid in g[fid]:
                auth_set.add(tid)
                edge_set.add((fid, tid))

    return auth_set, edge_set


hubs = [1, 3]
auths = bipartitify(hubs, g)
salsa(hubs, list(auths), g)
