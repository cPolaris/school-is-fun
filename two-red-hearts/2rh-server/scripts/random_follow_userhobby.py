import random
import MySQLdb
import json

CONFIG_FILE = '/home/star/server/config/server-config.json'


def generate_follow():
    a = random.choice(num_list_user)
    b = random.choice(num_list_user)
    if a != b:
        add_user_set.add((a, b))


json_file = open(CONFIG_FILE, 'r', encoding='utf8').read()
config = json.loads(json_file)
db = MySQLdb.connect(config['db']['host'],
                     config['db']['user'],
                     config['db']['password'],
                     config['db']['database'])
cursor = db.cursor()

cursor.execute('CALL GetUserIds()')
num_list_user = []

for row in cursor.fetchall():
    num_list_user.append(row[0])

# num_list_user=list(range(1,user_count+1))
num_hobb_user = list(range(1, 17 + 1))

add_hobby_set = set()
add_user_set = set()
list_use = []
for i in range(0, 50):  # choose number of population here
    add_hobby_set.add((random.choice(num_list_user), random.choice(num_hobb_user)))
    generate_follow()

for row in add_user_set:
    cursor.execute('CALL AddFollow(%s,%s)', (row[0], row[1]))
for row in add_hobby_set:
    cursor.execute('CALL AddUserHobby(%s,%s)', (row[0], row[1]))

db.commit()
db.close()
