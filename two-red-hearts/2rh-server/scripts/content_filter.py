import logging
import json
import MySQLdb
import numpy as np
from scipy import spatial

LOGGING_FILE = '/home/star/content_filter.log'
CONFIG_FILE = '/home/star/server/config/server-config.json'

logging.basicConfig(filename=LOGGING_FILE,
                    level=logging.DEBUG,
                    format='%(asctime)s %(message)s')

logging.info('content-filter started')

json_file = open(CONFIG_FILE, 'r', encoding='utf8').read()
config = json.loads(json_file)

db = MySQLdb.connect(config['db']['host'],
                     config['db']['user'],
                     config['db']['password'],
                     config['db']['database'])
cursor = db.cursor()

ranks = {}
user_ids = []
hobby_ids = []
userid_majorcode_dict = {}

cursor.execute('CALL GetUserIds()')
for row in cursor.fetchall():
    user_ids.append(row[0])

cursor.execute('SELECT * FROM HobbyTag')
for row in cursor.fetchall():
    hobby_ids.append(row[0])

tag_count = len(hobby_ids) + 1

# initialize vector
for userId in user_ids:
    ranks[userId] = np.zeros(tag_count)
    cursor.execute('CALL GetHobbyTagIdsOfUser(%s)', (userId,))
    for tag in cursor.fetchall():
        ranks[userId][tag[0]] = 1
changes = 0

cursor.execute('SELECT id,majorCode FROM User')
for row in cursor.fetchall():
    userid_majorcode_dict[row[0]] = row[1]

# calculate scores
for forId in user_ids:
    for recId in user_ids:
        if not forId == recId:
            score = 1 - spatial.distance.cosine(ranks[forId], ranks[recId])
            if np.isnan(score):
                score = 0

            cursor.execute('CALL SetRankProfileScore(%s,%s,%s)', (forId, recId, score))
            changes += 1

db.commit()
db.close()

logging.info("content-filter finished. %d changes" % (changes,))
