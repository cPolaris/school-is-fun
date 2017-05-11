import random
import http.client
import json
import time

with open('firstnames-male.txt', 'r') as f:
    first_name_m = f.read().splitlines()

with open('firstnames-female.txt', 'r') as f:
    first_name_f = f.read().splitlines()

with open('lastnames.txt', 'r') as f:
    last_name = f.read().splitlines()

with open('games.txt', 'r') as f:
    games = f.read().splitlines()

with open('majors.txt', 'r') as f:
    majors = f.read().splitlines()

with open('movies.txt', 'r') as f:
    movies = f.read().splitlines()

with open('music.txt', 'r') as f:
    music = f.read().splitlines()

with open('shows.txt', 'r') as f:
    shows = f.read().splitlines()

with open('sports.txt', 'r') as f:
    sports = f.read().splitlines()

# import sqlite3
# conn = sqlite3.connect('2rh-fake.db')
# conn.execute('CREATE TABLE IF NOT EXISTS Users (username TEXT PRIMARY KEY, email TEXT NOT NULL, hash TEXT NOT NULL, salt TEXT NOT NULL, token TEXT NOT NULL, name TEXT, gender INTEGER, major TEXT, bio TEXT)');
# conn.execute('CREATE TABLE IF NOT EXISTS Hobbies (username TEXT, category TEXT, content TEXT, PRIMARY KEY (username, category))');
# conn.commit()

def username_gen(input_name):
    names = input_name.split(" ")
    first_letter = names[0][0]
    three_letters_surname = names[-1][:3]
    number = '{:03d}'.format(random.randrange(1, 999))
    username = first_letter + three_letters_surname + number
    return username

def register_user(username, email, password):
    body = json.dumps({'username': username, 'email':email, 'password': password})
    headers = {"Content-type": "application/json"}
    conn = http.client.HTTPConnection("localhost",3000)
    conn.request("POST", "/api/users", body, headers)
    conn.close()

def put_hobbies(username, name, gender, major, bio, sport, music, movie, game, show):
    body = json.dumps({
        'username': username,
        'name':name,
        'gender': gender,
        'major': major,
        'bio': bio,
        'sport': sport,
        'music': music,
        'movie': movie,
        'game': game,
        'show': show
    })

    headers = {"Content-type": "application/json"}
    conn = http.client.HTTPConnection("localhost",3000)
    conn.request("PUT", "/api/users/" + username, body, headers)
    conn.close()

NUM_USERS = 10000
MIN_HOBBY_ENTRIES = 0
MAX_HOBBY_ENTRIES = 10

for i in range(NUM_USERS):
    gender = random.randint(0, 1)
    if gender:
        name = random.choice(first_name_m) + ' ' + random.choice(last_name)
    else:
        name = random.choice(first_name_f) + ' ' + random.choice(last_name)

    username = username_gen(name).lower()
    major = random.choice(majors)

    random.shuffle(music)
    random.shuffle(games)
    random.shuffle(movies)
    random.shuffle(shows)
    random.shuffle(sports)

    n = random.randint(MIN_HOBBY_ENTRIES, MAX_HOBBY_ENTRIES)
    music_like = '\n'.join(music[:n])
    n = random.randint(MIN_HOBBY_ENTRIES, MAX_HOBBY_ENTRIES)
    game_like = '\n'.join(games[:n])
    n = random.randint(MIN_HOBBY_ENTRIES, MAX_HOBBY_ENTRIES)
    movie_like = '\n'.join(movies[:n])
    n = random.randint(MIN_HOBBY_ENTRIES, MAX_HOBBY_ENTRIES)
    show_like = '\n'.join(shows[:n])
    n = random.randint(MIN_HOBBY_ENTRIES, MAX_HOBBY_ENTRIES)
    sport_like = '\n'.join(sports[:n])

    # print(username, name, gender, major)
    # print(json.dumps({
    #     'username': username,
    #     'name':name,
    #     'gender': gender,
    #     'major': major,
    #     'bio': 'This is my bio',
    #     'sport': sport_like,
    #     'music': music_like,
    #     'movie': movie_like,
    #     'game': game_like,
    #     'show': show_like
    # }))
    register_user(username, 'fake@fake.fake', 'a')
    time.sleep(0.2)
    put_hobbies(username, name, gender, major, 'This is a bio', sport_like, music_like, movie_like, game_like, show_like)
    time.sleep(0.2)
