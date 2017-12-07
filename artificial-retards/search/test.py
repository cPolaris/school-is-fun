import mazesearch
import sokobansearch


def part1_1():
    run_all('./maze/mediumMaze.txt')
    run_all('./maze/bigMaze.txt')
    run_all('./maze/openMaze.txt')


def part1_2():
    input_file = './maze/tinySearch.txt'
    runner = mazesearch.AstarMulti(input_file)
    runner.run_from_start()
    # input_file = './maze/smallSearch.txt'
    # runner = mazesearch.AstarMulti(input_file)
    # runner.run_from_start()
    # input_file = './maze/mediumSearch.txt'
    # runner = mazesearch.AstarMulti(input_file)
    # runner.run_from_start()

def part1_e():
    # input_file = './maze/tinySearch.txt'
    # input_file = './maze/smallSearch.txt'
    # runner = mazesearch.AstarSubopt(input_file)
    # runner.run_from_start()
    # input_file = './maze/mediumSearch.txt'
    # runner = mazesearch.AstarSubopt(input_file)
    # runner.run_from_start()
    input_file = './maze/bigDots.txt'
    runner = mazesearch.AstarSubopt(input_file)
    runner.run_from_start()

def run_all(input_file):
    print(input_file)
    print('DFS')
    run = mazesearch.DFS(input_file)
    run.run_from_start()
    print('BFS')
    run = mazesearch.BFS(input_file)
    run.run_from_start()
    print('Greedy')
    run = mazesearch.GreedySingle(input_file)
    run.run_from_start()
    print('A*')
    run = mazesearch.AstarSingle(input_file)
    run.run_from_start()
    print('\n\n')

def part2():
    print('BFS')
    input_file = './sokoban/sokoban3.txt'
    runner = sokobansearch.BFS(input_file)
    runner.run_from_start()
    print('A*')
    input_file = './sokoban/sokoban3.txt'
    runner = sokobansearch.Astar(input_file)
    runner.run_from_start()
    # print('BFS')
    # runner = sokobansearch.BFS(input_file)
    # runner.run_from_start()

def main():
    # part1_1()
    part1_2()
    # part1_e()
    # part2()

if __name__ == '__main__':
    main()
