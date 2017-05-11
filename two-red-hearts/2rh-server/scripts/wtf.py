import logging
import json
import MySQLdb
import numpy as np
from scipy.sparse.linalg import eigs
from numpy.linalg import norm

LOGGING_FILE = '/home/star/wtf.log'
CONFIG_FILE = '/home/star/server/config/server-config.json'

logging.basicConfig(filename=LOGGING_FILE,
                    level=logging.DEBUG,
                    format='%(asctime)s %(message)s')


def top_indicies(arr, topn):
    if topn > len(arr):
        topn = len(arr)
    return sorted(range(len(arr)), key=lambda i: arr[i], reverse=True)[-topn:]


def personalized_pagerank(center_id, size, topn=50, epsilon=0.1):
    # transition matrix
    # @FIXME can we use sparse representation?
    A = np.zeros((size, size))

    for i in range(size):
        if pr_idarr[i] in graph:
            neighbors = graph[pr_idarr[i]]
            out_count = len(neighbors)
            for tid in neighbors:
                tindex = pr_idarr.index(tid)
                A[tindex, i] = 1.0 / out_count

    # don't let the name fool you...
    # we are actuall running general pagerank here
    # muh ha ha
    E = np.ones(size) / size
    # E[user_id] = 1.0
    R = E.copy()
    delta = 1

    while delta > epsilon:
        R_next = A @ R
        diff = norm(R, 1) - norm(R_next, 1)
        R_next += diff * E
        delta = norm(R_next - R, 1)
        R = R_next

    return R


def salsa():
    H = np.zeros((len(hubs), len(hubs)))
    A = np.zeros((len(auths), len(auths)))
    # @ todo ...


if __name__ == '__main__':
    logging.info('wtf started')

    json_file = open(CONFIG_FILE, 'r', encoding='utf8').read()
    config = json.loads(json_file)

    db = MySQLdb.connect(config['db']['host'],
                         config['db']['user'],
                         config['db']['password'],
                         config['db']['database'])
    cursor = db.cursor()

    # construct the complete graph... yes...
    graph = {}

    # set of all user IDs
    all_ids = set()

    # only users with following relationship is meaningful
    cursor.execute('CALL GetFollow()')
    for ind, row in enumerate(cursor.fetchall()):
        from_id, to_id = row
        all_ids.add(from_id)
        all_ids.add(to_id)

        if from_id in graph:
            graph[from_id].append(to_id)
        else:
            graph[from_id] = [to_id]

    # map from matrix index to actual user ID
    # we can do this with an array
    pr_idarr = []  # pagerank id array
    for uid in all_ids:
        pr_idarr.append(uid)

    pr_result = personalized_pagerank(1, len(all_ids))

    # onward to SALSA
    hubs = set([pr_idarr[ind] for ind in top_indicies(pr_result, 50)])
    auths = set()

    # we need to remove hubs that have no outgoing edges
    # to maintain bipartite property
    hub_remove_list = []

    for uid in hubs:
        if uid in graph:
            for nb in graph[uid]:
                auths.add(nb)
        else:
            hub_remove_list.append(uid)

    for rmid in hub_remove_list:
        hubs.remove(rmid)

    h_idarr = []
    a_idarr = []

    for uid in hubs:
        h_idarr.append(uid)

    for uid in auths:
        h_idarr.append(uid)

    salsa()

    for rec_ind, rank in enumerate(pr_result):
        for for_id in all_ids:
            cursor.execute('CALL SetRankWtfScore(%s,%s,%s)', (for_id, pr_idarr[rec_ind], rank))

    db.commit()
    db.close()
    logging.info('wtf finished')
