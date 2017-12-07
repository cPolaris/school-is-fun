import numpy as np
import time


WALL = '%'
BOX = 'b'
STORED_BOX = 'B'
START = 'P'
STORE_LOC = '.'
SPACE = ' '
PATHCHAR = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'


class SokobanNode:
    """
    State representation:
    (person_row, person_col, frozenset((box_n_r, box_n_c)))
    """

    def __init__(self, state, parent=None, cost=0):
        self.state = state
        self.parent = parent
        self.cost = cost

    def __eq__(self, other):
        return self.state == other.state


class SokobanSearch:
    """A generalized sokoban search problem"""

    def __init__(self, input_file):
        """input_file: path to input txt"""
        self.steps = 0
        self.init_st = None
        self.maze = None
        with open(input_file, 'r') as f:
            self.maze, self.init_st, self.goal_st_boxes = SokobanSearch.text_to_maze(
                f.read())
        self.nrows, self.ncols = self.maze.shape
        self.curr_node = None
        self.visited = set()    # a set of states
        self.frontier = list()  # a list of nodes
        self.elapsed = 0

    @staticmethod
    def text_to_maze(txt):
        """Convert text file to sokoban representation
        returns (maze, initial_state, goal_state_boxes)
        """
        lines = txt.splitlines()
        nrow = len(lines)
        ncol = len(lines[0])
        maze = np.zeros((nrow, ncol), dtype=bool)
        init_row = None
        init_col = None
        box_locs = set()
        goal_locs = set()
        for r in range(nrow):
            for c in range(ncol):
                car = lines[r][c]
                maze[r, c] = not car == WALL
                if car == START:
                    init_row, init_col = r, c
                elif car == BOX:
                    box_locs.add((r, c))
                elif car == STORE_LOC:
                    goal_locs.add((r, c))
                elif car == STORED_BOX:
                    box_locs.add((r, c))
                    goal_locs.add((r, c))
        init_st = (init_row, init_col, frozenset(box_locs))
        goal_st_boxes = frozenset(goal_locs)
        return (maze, init_st, goal_st_boxes)

    @staticmethod
    def distance(x1, y1, x2, y2):
        """Manhattan distance"""
        return abs(x1 - x2) + abs(y1 - y2)

    def reached_goal(self):
        """Return True if location of box at current node is same
        as goal box locations"""
        return self.curr_node.state[2] == self.goal_st_boxes

    def frontier_select(self):
        """select an appropriate node in self.frontier"""
        pass

    def expand(self):
        """
        First, adds the current state to visited set.
        Adds neighbors of current node to the frontier.
        Won't add if action is invalid (hit wall).
        Won't add if state has been visited.
        If has appeared in frontier, replace the one in frontier if
        this one's cost is lower.

        For sokoban, the possible situations of taking an step is as follows:
        - next step is WALL. Cannot move
        - next step is BOX, and one more step away is WALL. Cannot move
        - next step is SPACE
          Then person moves
        - next step is BOX, and one more step away is SPACE
          Then both person and box move
        """
        def try_add_node(row, col, new_locs):
            new_state = (row, col, new_locs)
            if not new_state in self.visited:
                new_node = SokobanNode(new_state,
                                       parent=self.curr_node,
                                       cost=self.curr_node.cost + 1)
                try:
                    # try replace a node in frontier that contains
                    # the same state but higher cost
                    ind = self.frontier.index(new_node)
                    if self.frontier[ind].cost > new_node.cost:
                        self.frontier[ind] = new_node
                except ValueError:
                    self.frontier.append(new_node)
        self.visited.add(self.curr_node.state)
        curr_r, curr_c, curr_box_locs = self.curr_node.state
        # UP
        if ((curr_r - 1, curr_c) in curr_box_locs and self.maze[curr_r - 2, curr_c]):
            try_add_node(curr_r - 1, curr_c, curr_box_locs -
                         {(curr_r - 1, curr_c)} | {(curr_r - 2, curr_c)})
        elif self.maze[curr_r - 1, curr_c]:
            try_add_node(curr_r - 1, curr_c, curr_box_locs)
        # DOWN
        if ((curr_r + 1, curr_c) in curr_box_locs and self.maze[curr_r + 2, curr_c]):
            try_add_node(curr_r + 1, curr_c, curr_box_locs -
                         {(curr_r + 1, curr_c)} | {(curr_r + 2, curr_c)})
        elif self.maze[curr_r + 1, curr_c]:
            try_add_node(curr_r + 1, curr_c, curr_box_locs)
        # LEFT
        if ((curr_r, curr_c - 1) in curr_box_locs and self.maze[curr_r, curr_c - 2]):
            try_add_node(curr_r, curr_c - 1, curr_box_locs -
                         {(curr_r, curr_c - 1)} | {(curr_r, curr_c - 2)})
        elif self.maze[curr_r, curr_c]:
            try_add_node(curr_r, curr_c - 1, curr_box_locs)
        # RIGHT
        if ((curr_r, curr_c + 1) in curr_box_locs and self.maze[curr_r, curr_c + 2]):
            try_add_node(curr_r, curr_c + 1, curr_box_locs -
                         {(curr_r, curr_c + 1)} | {(curr_r, curr_c + 2)})
        elif self.maze[curr_r, curr_c + 1]:
            try_add_node(curr_r, curr_c + 1, curr_box_locs)

    def run_from_start(self):
        """reset states and run from initial state"""
        self.elapsed = 0
        self.steps = 0
        self.visited.clear()
        self.frontier.clear()
        self.frontier.append(SokobanNode(self.init_st))
        start_time = time.time()
        while not len(self.frontier) == 0:
            self.curr_node = self.frontier_select()
            if self.reached_goal():
                break
            else:
                self.expand()
            self.steps += 1
            # self.print_curr_status()  # @debug
        self.elapsed = time.time() - start_time
        self.report()

    def print_curr_status(self):
        print('(%d, %d) expanded: %d   cost: %d' %
              (self.curr_node.state[0],
               self.curr_node.state[1],
               len(self.visited),
               self.curr_node.cost), end='\r')

    def report(self):
        """prints string representation of the path for current node,
        also reports stats for the last run"""
        print('\n%f seconds. Expanded %d nodes. Found path cost: %d' %
              (self.elapsed, len(self.visited),self.curr_node.cost))
        # self.draw_path()
        print('\n\n')

    @staticmethod
    def trace_path(node):
        path = []
        while True:
            path.insert(0, (node.state[0], node.state[1]))
            node = node.parent
            if not node:
                break
        return path

    def draw_path(self):
        path = SokobanSearch.trace_path(self.curr_node)
        nrow, ncol = self.maze.shape
        lines = [['%' for _ in range(ncol)] for _ in range(nrow)]
        for row in range(nrow):
            for col in range(ncol):
                if self.maze[row, col]:
                    lines[row][col] = SPACE
        for ind, (row, col) in enumerate(path):
            lines[row][col] = PATHCHAR[ind % len(PATHCHAR)]
        lines = [''.join(r) for r in lines]
        print('\n'.join(lines))
        print(path)


class BFS(SokobanSearch):
    def frontier_select(self):
        return self.frontier.pop(0)


class Astar(SokobanSearch):
    """A* sokoban search.
    Heuristic = sum ( distance from each box to its closet storage location )
    """

    def heuristic(self, node):
        cost = 0
        for bl in node.state[2]:
            cost += min(list(map(lambda other: self.distance(bl[0], bl[1], other[0], other[1]), self.goal_st_boxes)))
        return cost

    def evaluation(self, node):
        return self.heuristic(node) + node.cost

    def frontier_select(self):
        return self.frontier.pop(np.argmin(list(map(self.evaluation, self.frontier))))

class AstarSubopt(SokobanSearch):
    """Suboptimal sokoban search"""
    def heuristic(self, node):
        pass

    def evaluation(self, node):
        pass

    def frontier_select(self):
        pass
