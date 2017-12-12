import sys
import time
import math
import random
import pygame
import numpy as np
import pongagent
from pygame.locals import *

BLUE = (45, 178, 255)
RED = (226, 36, 36)
GREEN = (57, 79, 24)
WHITE = (255, 255, 255)
FPS = 30
KEY_W_CODE = 119
KEY_S_CODE = 115
CANVAS_SIZE = 500

NUM_STATES = 10369
NUM_ACTIONS = 3


# @todo this is more efficient
# class IntervalTree:
#     """Non-overlapping interval tree. Supports efficient interval lookup"""
#     def __init__(self, lo, hi, intervals):
#         itv_len = (hi - lo) / intervals
#         self.boundaries = np.zeros(intervals)
#         for i in range(intervals):
#             self.boundaries[i] = lo + i * itv_len

class PongSP():
    """Single player Pong"""
    def __init__(self, agent, train_rounds=5):
        self.paddle_height = 0.2
        self.agent = agent
        self.roundnum = 0
        self.num_rounds = train_rounds

    def set_init_state(self):
        # state: (ball_x, ball_y, v_x, v_y, paddle_y)
        # paddle_y: top of the paddle
        self.ball_x = 0.5
        self.ball_y = 0.5
        self.v_x = 0.03
        self.v_y = 0.01
        self.paddle_y = 0.5 - self.paddle_height / 2
        self.player_up = False
        self.player_down = False
        self.bounced = 0
        self.curr_reward = 0
        self.gameover = False

    def train(self):
        self.set_init_state()
        while True:
            self.handle_agent()
            self.curr_reward = 0
            self.apply_physics()
            self.agent.reward_cb(self.curr_reward, self.discretized_state())
            if self.gameover:
                self.roundnum += 1
                if self.roundnum == self.num_rounds:
                    break
                self.gameover = False
                self.set_init_state()
        print('training stopped')

    def run(self, rounds):
        round_count = 0
        pygame.init()
        pygame.display.set_caption('Pong Game')
        self.DISPLAYSURF = pygame.display.set_mode((CANVAS_SIZE, CANVAS_SIZE), 0, 16)
        self.fpsClock = pygame.time.Clock()
        self.bounce_history = []
        self.set_init_state()
        while True:
            self.handle_events()
            self.handle_agent()
            self.apply_physics()
            self.DISPLAYSURF.fill((0,0,0))
            self.draw_scene()
            pygame.display.update()
            self.fpsClock.tick(FPS)
            if self.gameover:
                print('bounced', self.bounced)
                self.bounce_history.append(self.bounced)
                self.bounced = 0
                round_count += 1
                if round_count == rounds:
                    self.print_report()
                    break
                self.gameover = False
                self.set_init_state()

    def handle_agent(self):
        # if self.agent is None:
        #     if self.player_up:
        #         self.paddle_y -= 0.04
        #     if self.player_down:
        #         self.paddle_y += 0.04
        # else:
        action = self.agent.act(self.discretized_state())
        if action == -1:  # down
            self.paddle_y += 0.04
        elif action == 1: # up
            self.paddle_y -= 0.04

    def discretized_state(self):
        """
        Returns discritized current state as a tuple:
        (ball_x, ball_y, v_x, v_y, paddle_y)
        """
        if self.gameover:
            return (-1,-1,-1,-1,-1)
        dis_vx = 1 if self.v_x > 0 else -1
        dis_vy = 0
        if abs(self.v_y) < 0.015:
            dis_vy = 0
        elif self.v_y > 0:
            dis_vy = 1
        else:
            dis_vy = -1
        dis_py = 11 if self.paddle_y == 0.8 else math.floor(12 * self.paddle_y / 0.8)
        dis_bx = 11 if self.ball_x == 1.0 else math.floor(12 * self.ball_x)
        dis_by = 11 if self.ball_y == 1.0 else math.floor(12 * self.ball_y)
        return (dis_bx, dis_by, dis_vx, dis_vy, dis_py)

    def apply_physics(self):
        # constraint paddle
        if self.paddle_y < 0:
            self.paddle_y = 0
        elif self.paddle_y > 0.8:
            self.paddle_y = 0.8
        # move ball
        self.ball_x += self.v_x
        self.ball_y += self.v_y
        # bounce ball
        if self.ball_y < 0:
            self.ball_y = -self.ball_y
            self.v_y = -self.v_y
        elif self.ball_y > 1:
            self.ball_y = 2 - self.ball_y
            self.v_y = -self.v_y
        if self.ball_x < 0:
            self.ball_x = -self.ball_x
            self.v_x = -self.v_x
        elif self.ball_x > 1:
            if self.ball_y > self.paddle_y and self.ball_y < self.paddle_y + 0.2:
                self.bounced += 1
                self.ball_x = 2 - self.ball_x
                self.v_x = - self.v_x + random.uniform(-0.015, 0.015)
                self.v_y = self.v_y + random.uniform(-0.03, 0.03)
                self.curr_reward = 1
            else:
                self.gameover = True
                self.curr_reward = -1
        # bound velocity
        if abs(self.v_x) < 0.03:
            self.v_x = 0.03 if self.v_x > 0 else -0.03
        if abs(self.v_x) > 1.0:
            self.v_x = 1.0 if self.v_x > 0 else -1.0
        if abs(self.v_y) > 1.0:
            self.v_y = 1.0 if self.v_y > 0 else -1.0

    def print_report(self):
        print('--------------------------------------------')
        print('avg bounced', np.average(self.bounce_history))

    def draw_scene(self):
        # walls
        pygame.draw.line(self.DISPLAYSURF, GREEN, (0,0), (0,500), 20)
        pygame.draw.line(self.DISPLAYSURF, GREEN, (0,0), (500,0), 20)
        pygame.draw.line(self.DISPLAYSURF, GREEN, (0,500), (500,500), 20)
        # paddle
        cvs_paddle_top = self.paddle_y * 500
        pygame.draw.line(self.DISPLAYSURF, BLUE, (500, cvs_paddle_top), (500, cvs_paddle_top + 100), 20)
        # ball
        cvs_ball_x = int(self.ball_x * 500)
        cvs_ball_y = int(self.ball_y * 500)
        pygame.draw.circle(self.DISPLAYSURF, WHITE, (cvs_ball_x, cvs_ball_y), 4, 0)

    def handle_events(self):
        for event in pygame.event.get():
            if event.type == QUIT:
                pygame.quit()
                sys.exit()
            elif event.type == KEYDOWN:
                if event.key == KEY_W_CODE:
                    # print('w down')
                    self.player_up = True
                elif event.key == KEY_S_CODE:
                    # print('s down')
                    self.player_down = True
            elif event.type == KEYUP:
                if event.key == KEY_W_CODE:
                    # print('w up')
                    self.player_up = False
                elif event.key == KEY_S_CODE:
                    # print('s up')
                    self.player_down = False
class PongTP():
    """Two players Pong"""
    def __init__(self, agent, train_rounds=5, play_rounds=5):
        self.paddle_height = 0.2
        self.agent = agent
        self.roundnum = 0
        self.train_rounds = train_rounds
        self.play_rounds = play_rounds
        self.win_rounds = 0

    def set_init_state(self):
        # state: (ball_x, ball_y, v_x, v_y, paddle_y)
        # paddle_y: top of the paddle
        self.ball_x = 0.5
        self.ball_y = 0.5
        self.v_x = 0.03
        self.v_y = 0.01
        self.p2_y = 0.5 - self.paddle_height / 2
        self.paddle_y = 0.5 - self.paddle_height / 2
        self.player_up = False
        self.player_down = False
        self.curr_reward = 0
        self.gameover = False

    def train(self):
        self.set_init_state()
        while True:
            self.handle_hardcode_player()
            self.handle_agent()
            self.curr_reward = 0
            self.apply_physics()
            self.agent.reward_cb(self.curr_reward, self.discretized_state())
            if self.gameover:
                self.roundnum += 1
                if self.roundnum == self.train_rounds:
                    break
                self.gameover = False
                self.set_init_state()
        print('training stopped')

    def run(self):
        round_count = 0
        pygame.init()
        pygame.display.set_caption('Pong Game')
        self.DISPLAYSURF = pygame.display.set_mode((CANVAS_SIZE, CANVAS_SIZE), 0, 16)
        self.fpsClock = pygame.time.Clock()
        self.bounce_history = []
        self.set_init_state()
        while True:
            self.handle_events()
            # self.handle_hardcode_player()
            self.handle_human()
            self.handle_agent()
            self.apply_physics()
            self.DISPLAYSURF.fill((0,0,0))
            self.draw_scene()
            pygame.display.update()
            self.fpsClock.tick(FPS)
            if self.gameover:
                round_count += 1
                if round_count == self.play_rounds:
                    self.print_report()
                    break
                self.gameover = False
                self.set_init_state()

    def handle_hardcode_player(self):
        direction = np.sign(self.ball_y - (self.p2_y+0.1))
        self.p2_y += 0.02 * direction

    def handle_human(self):
        if self.player_up:
            self.p2_y -= 0.04
        if self.player_down:
            self.p2_y += 0.04
    def handle_agent(self):
        action = self.agent.act(self.discretized_state())
        if action == -1:  # down
            self.paddle_y += 0.04
        elif action == 1: # up
            self.paddle_y -= 0.04


    def discretized_state(self):
        """
        Returns discritized current state as a tuple:
        (ball_x, ball_y, v_x, v_y, paddle_y)
        """
        if self.gameover:
            return (-1,-1,-1,-1,-1)
        dis_vx = 1 if self.v_x > 0 else -1
        dis_vy = 0
        if abs(self.v_y) < 0.015:
            dis_vy = 0
        elif self.v_y > 0:
            dis_vy = 1
        else:
            dis_vy = -1
        dis_py = 11 if self.paddle_y == 0.8 else math.floor(12 * self.paddle_y / 0.8)
        dis_bx = 11 if self.ball_x == 1.0 else math.floor(12 * self.ball_x)
        dis_by = 11 if self.ball_y == 1.0 else math.floor(12 * self.ball_y)
        return (dis_bx, dis_by, dis_vx, dis_vy, dis_py)

    def apply_physics(self):
        # constraint paddle
        if self.paddle_y < 0:
            self.paddle_y = 0
        elif self.paddle_y > 0.8:
            self.paddle_y = 0.8
        if self.p2_y < 0:
            self.p2_y = 0
        elif self.p2_y > 0.8:
            self.p2_y = 0.8
        # move ball
        self.ball_x += self.v_x
        self.ball_y += self.v_y
        # bounce ball
        if self.ball_y < 0:
            self.ball_y = -self.ball_y
            self.v_y = -self.v_y
        elif self.ball_y > 1:
            self.ball_y = 2 - self.ball_y
            self.v_y = -self.v_y
        if self.ball_x < 0:
            # p2 side
            if self.ball_y > self.p2_y and self.ball_y < self.p2_y + 0.2:
                self.ball_x = -self.ball_x
                self.v_x = - self.v_x + random.uniform(-0.015, 0.015)
                self.v_y = self.v_y + random.uniform(-0.03, 0.03)
            else:
                self.gameover = True
                self.win_rounds += 1
        elif self.ball_x > 1:
            # agent side
            if self.ball_y > self.paddle_y and self.ball_y < self.paddle_y + 0.2:
                self.ball_x = 2 - self.ball_x
                self.v_x = - self.v_x + random.uniform(-0.015, 0.015)
                self.v_y = self.v_y + random.uniform(-0.03, 0.03)
                self.curr_reward = 1
            else:
                self.gameover = True
                self.curr_reward = -1
        # bound velocity
        if abs(self.v_x) < 0.03:
            self.v_x = 0.03 if self.v_x > 0 else -0.03
        if abs(self.v_x) > 1.0:
            self.v_x = 1.0 if self.v_x > 0 else -1.0
        if abs(self.v_y) > 1.0:
            self.v_y = 1.0 if self.v_y > 0 else -1.0

    def print_report(self):
        print('--------------------------------------------')
        print('agent win rate', self.win_rounds / self.play_rounds)

    def draw_scene(self):
        # walls
        pygame.draw.line(self.DISPLAYSURF, GREEN, (0,0), (500,0), 20)
        pygame.draw.line(self.DISPLAYSURF, GREEN, (0,500), (500,500), 20)
        # p2
        cvs_paddle_top = self.p2_y * 500
        pygame.draw.line(self.DISPLAYSURF, RED, (0, cvs_paddle_top), (0, cvs_paddle_top + 100), 20)
        # paddle
        cvs_paddle_top = self.paddle_y * 500
        pygame.draw.line(self.DISPLAYSURF, BLUE, (500, cvs_paddle_top), (500, cvs_paddle_top + 100), 20)
        # ball
        cvs_ball_x = int(self.ball_x * 500)
        cvs_ball_y = int(self.ball_y * 500)
        pygame.draw.circle(self.DISPLAYSURF, WHITE, (cvs_ball_x, cvs_ball_y), 4, 0)

    def handle_events(self):
        for event in pygame.event.get():
            if event.type == QUIT:
                pygame.quit()
                sys.exit()
            elif event.type == KEYDOWN:
                if event.key == KEY_W_CODE:
                    # print('w down')
                    self.player_up = True
                elif event.key == KEY_S_CODE:
                    # print('s down')
                    self.player_down = True
            elif event.type == KEYUP:
                if event.key == KEY_W_CODE:
                    # print('w up')
                    self.player_up = False
                elif event.key == KEY_S_CODE:
                    # print('s up')
                    self.player_down = False


def play_single():
    # train_rs = 100000
    # g = PongSP(pongagent.EpsilonAgent(0.7, 0.1), train_rounds=train_rs)
    # start_time = time.time()
    # g.train()
    # print('training for %d rounds, took %f minutes' % (train_rs, (time.time() - start_time)/60))
    # g.agent.save_agent('last_run.pickle')
    # g.run(10)
    pretrained = pongagent.EpsilonAgent(None, 0)
    pretrained.load_agent('13bounce.pickle')
    g = PongSP(pretrained)
    g.run(50)

def play_two():
    # g = PongTP(None,play_rounds=50)
    pretrained = pongagent.EpsilonAgent(None, 0)
    pretrained.load_agent('13bounce.pickle')
    g = PongTP(pretrained, play_rounds=50)
    g.run()

def main():
    play_two()

if __name__ == '__main__':
    main()
