"""compatible with 3pong"""
import random
import pickle
import numpy as np

UP = 1
DOWN = -1
NOTHING = 0
FAIL_STATE = (-1,-1,-1,-1,-1)
C = 3

class PongAgent:
    def __init__(self, discount):
        self.discount = discount
        self.last_state = None
        self.last_action = None
        self.qtable = np.zeros((20,20,2,3,20,3))
        self.action_count = np.zeros((20,20,2,3,20,3))
        self.qtable[FAIL_STATE] = np.zeros(3)
    def act(self, state):
        pass
    def reward_cb(self):
        pass
    def save_agent(self, file):
        with open(file, 'wb') as f:
            pickle.dump((self.qtable,self.action_count), f, pickle.HIGHEST_PROTOCOL)
    def load_agent(self, file):
        with open(file, 'rb') as f:
            qt, ac = pickle.load(f)
            self.qtable = qt
            self.action_count = ac

class EpsilonAgent(PongAgent):
    def __init__(self, discount, epsilon):
        super().__init__(discount)
        self.epsilon = epsilon

    def argmaxifkey(self, key):
        qs = self.qtable[key]
        if qs[0] == qs[1] == qs[2]:
            return random.randint(0,2)
        return np.argmax(qs)

    def act(self, state, train=True):
        a = None
        if train and random.random() < self.epsilon:
            a = random.randint(0, 2) - 1
        else:
            a = self.argmaxifkey(state) - 1
        self.action_count[state][a+1] += 1
        self.last_state = state
        self.last_action = a
        return a

    def reward_cb(self, r, new_state):
        a = self.last_action
        curr_q = self.qtable[self.last_state][a+1]
        lr = C / (C + self.action_count[self.last_state][a + 1])
        self.qtable[self.last_state][a+1] = curr_q + lr * (r + self.discount * max(self.qtable[new_state]) - curr_q)

# exploration function agent
class ExploreAgent:
    pass
