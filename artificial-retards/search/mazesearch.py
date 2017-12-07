import numpy as np


WALL = '%'
START = 'P'
DOT = '.'
SPACE = ' '
PATHCHAR = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'


class MazeSearchNode:
    def __init__(self, state, parent=None, cost=0):
        self.state = state
        self.parent = parent
        self.cost = cost
        self.ate = None

    def __eq__(self, other):
        return self.state == other.state


class MazeSearch:
    """A generalized maze search problem"""

    def __init__(self, input_file):
        """input_file: path to input txt"""
        self.init_st = None
        self.maze = None
        with open(input_file, 'r') as f:
            self.maze, self.init_st = MazeSearch.text_to_maze(f.read())
        self.nrows, self.ncols = self.maze.shape
        self.curr_node = None
        self.visited = set()    # a set of states
        self.frontier = list()  # a list of nodes

    @staticmethod
    def text_to_maze(txt):
        """returns (initial_state, maze)
        State is a tuple:
            (initial_x, initial_y, frozenset([(bit_x,bit_y)]))
        Use frozenset to ensure hashable.
        Maze is a bool np.ndarray that denotes wall as False"""
        lines = txt.splitlines()
        nrow = len(lines)
        ncol = len(lines[0])
        maze = np.zeros((nrow, ncol), dtype=bool)
        init_row = None
        init_col = None
        bits = []
        for r in range(nrow):
            for c in range(ncol):
                car = lines[r][c]
                maze[r, c] = not car == WALL
                if car == 'P':
                    init_row, init_col = r, c
                elif car == '.':
                    bits.append((r, c))
        st = (init_row, init_col, frozenset(bits))
        return (maze, st)

    @staticmethod
    def distance(x1, y1, x2, y2):
        """Manhattan distance"""
        return abs(x1 - x2) + abs(y1 - y2)

    def reached_goal(self):
        """Return True if current state is goal state"""
        return len(self.curr_node.state[2]) == 0

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
        this one's cost is lower."""
        def try_add_node(row, col, old_bits):
            ate = None
            if (row,col) in old_bits:
                new_state = (row, col, old_bits - {(row, col)})
                ate = (row,col)
            else:
                new_state = (row, col, old_bits)
            if not new_state in self.visited:
                new_node = MazeSearchNode(new_state,
                                          parent=self.curr_node,
                                          cost=self.curr_node.cost + 1)
                new_node.ate = ate
                try:
                    # try replace a node in frontier that contains
                    # the same state but higher cost
                    ind = self.frontier.index(new_node)
                    if self.frontier[ind].cost > new_node.cost:
                        self.frontier[ind] = new_node
                except ValueError:
                    self.frontier.append(new_node)
        self.visited.add(self.curr_node.state)
        curr_r, curr_c, curr_bits = self.curr_node.state
        # UP
        if self.maze[curr_r - 1, curr_c]:
            try_add_node(curr_r - 1, curr_c, curr_bits)
        # DOWN
        if self.maze[curr_r + 1, curr_c]:
            try_add_node(curr_r + 1, curr_c, curr_bits)
        # LEFT
        if self.maze[curr_r, curr_c - 1]:
            try_add_node(curr_r, curr_c - 1, curr_bits)
        # RIGHT
        if self.maze[curr_r, curr_c + 1]:
            try_add_node(curr_r, curr_c + 1, curr_bits)

    def run_from_start(self):
        """reset states and run from initial state"""
        self.visited.clear()
        self.frontier.clear()
        self.frontier.append(MazeSearchNode(self.init_st))
        while not len(self.frontier) == 0:
            self.curr_node = self.frontier_select()
            if self.reached_goal():
                break
            else:
                self.expand()
            # self.print_curr_status()  # @debug
        self.report()

    def print_curr_status(self):
        # print('(%d, %d) expanded: %d   cost: %d' %
        #       (self.curr_node.state[0],
        #        self.curr_node.state[1],
        #        len(self.visited),
        #        self.curr_node.cost), end='\r')
        print(len(self.visited), end='\r')

    def report(self):
        """prints string representation of the path for current node,
        also reports stats for the last run"""
        print('\nExpanded %d nodes' %
              (len(self.visited),))
        print('found path cost: %d' % (self.curr_node.cost,))
        self.draw_path()
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
        path = MazeSearch.trace_path(self.curr_node)
        nrow, ncol = self.maze.shape
        lines = [['%' for _ in range(ncol)] for _ in range(nrow)]
        for row in range(nrow):
            for col in range(ncol):
                if self.maze[row, col]:
                    lines[row][col] = SPACE
                if (row, col) in path:
                    lines[row][col] = DOT
        lines = [''.join(r) for r in lines]
        print('\n'.join(lines))


class BFS(MazeSearch):
    """BFS maze search. Selects oldest in frontier"""

    def frontier_select(self):
        return self.frontier.pop(0)


class DFS(MazeSearch):
    """DFS maze search. Selects latest in frontier"""

    def frontier_select(self):
        return self.frontier.pop()


class GreedySingle(MazeSearch):
    """Greedy best-first maze search.
    Selects least heuristic cost in frontier"""
    @staticmethod
    def heuristic(node):
        """manhattan distance to the single bit"""
        cost = 0
        curr_r, curr_c = node.state[0], node.state[1]
        for bit_r, bit_c in node.state[2]:
            cost += GreedySingle.distance(curr_r, curr_c, bit_r, bit_c)
        return cost

    def frontier_select(self):
        return self.frontier.pop(np.argmin(list(map(GreedySingle.heuristic, self.frontier))))


class AstarSingle(GreedySingle):
    """A* maze search.
    Heuristic = sum of manhattan distance to all bits"""

    @staticmethod
    def evaluation(node):
        return AstarSingle.heuristic(node) + node.cost

    def frontier_select(self):
        return self.frontier.pop(np.argmin(list(map(AstarSingle.evaluation, self.frontier))))


class AstarMulti(MazeSearch):
    """A* maze search.
    Selects least heuristic + path cost in frontier"""

    @staticmethod
    def trace_path(node):
        eat_order = []
        path = []
        while True:
            path.insert(0, (node.state[0], node.state[1]))
            if node.ate:
                eat_order.insert(0, node.ate)
            node = node.parent
            if not node:
                break
        return (path, eat_order)

    def draw_path(self):
        path, eat_order = AstarMulti.trace_path(self.curr_node)
        nrow, ncol = self.maze.shape
        lines = [['%' for _ in range(ncol)] for _ in range(nrow)]
        for row in range(nrow):
            for col in range(ncol):
                if self.maze[row, col]:
                    lines[row][col] = SPACE
        for ind, (row, col) in enumerate(eat_order):
            lines[row][col] = PATHCHAR[ind % len(PATHCHAR)]
        lines = [''.join(r) for r in lines]
        print('\n'.join(lines))

    @staticmethod
    def heuristic(node):
        # cost = 0
        # curr_r, curr_c = node.state[0], node.state[1]
        # for bit_r, bit_c in node.state[2]:
        #     cost += AstarMulti.distance(curr_r, curr_c, bit_r, bit_c)
        # return cost
        return len(node.state[2])
        # try:
        #     md = max(list(map(
        #         lambda b: AstarMulti.distance(node.state[0], node.state[1], b[0], b[1]),
        #         node.state[2])))
        # except ValueError:
        #     md = 0
        # return md

    @staticmethod
    def evaluation(node):
        return AstarMulti.heuristic(node) + node.cost

    def frontier_select(self):
        return self.frontier.pop(np.argmin(list(map(AstarMulti.evaluation, self.frontier))))


class AstarSubopt(AstarMulti):
    # def run_from_start(self):
    #     """reset states and run from initial state"""
    #     self.visited.clear()
    #     self.frontier.clear()
    #     self.frontier.append(MazeSearchNode(self.init_st))
    #     while not len(self.frontier) == 0:
    #         self.curr_node = self.frontier_select()
    #         if self.reached_goal():
    #             break
    #         else:
    #             self.expand()
    #         self.print_curr_status()  # @debug
    #     self.report()
    def heuristic(self, node):
        try:
            dists = list(map(
                lambda b: self.distance(node.state[0], node.state[1], b[0], b[1]),
                list(node.state[2])))
            md = min(dists)
        except ValueError:
            md = 0
        return len(node.state[2]) * md

    def frontier_select(self):
        look_num = 4
        pop_ind = 0
        if len(self.frontier) > look_num:
            pop_ind = len(self.frontier) - look_num  + np.argmin(list(map(self.heuristic, self.frontier[-look_num:])))
        else:
            pop_ind = np.argmin(list(map(self.heuristic, self.frontier)))
        return self.frontier.pop(pop_ind)
    def print_curr_status(self):
        print('%d %d          ' % (len(self.visited), len(self.curr_node.state[2])), end='\r')