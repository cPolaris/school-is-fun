package brkthru;

import common.IntTuple;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;


/*
  Black player goes from top to bottom,
  white player bottom to top.
  White player makes the first move.
 */
public class Breakthrough {

    // meaning of byte values
    public static final byte EMPTY = 0;
    public static final byte WHITE = 1;
    public static final byte BLACK = 2;

    public static final int NUM_REACH_BASE = 1;  // a constant of the universe

    public final int nrows;
    public final int ncols;
    public final int initWorkers;
    public final byte[][] initBoard;
    public final GameState initState;
    private BreakthroughVisualizer visualizer;

    public Breakthrough(byte[][] initBoard) {
        this.initBoard = initBoard;
        this.initState = boardToGameState(initBoard);
        this.initWorkers = initState.blackStones.size();
        this.nrows = initBoard.length;
        this.ncols = initBoard[0].length;
        visualizer = new BreakthroughVisualizer(initBoard);
        visualizer.run();
    }

    public void playComputerAgents(Agent white, Agent black) {
        int totalMoves = 0;

        GameMove mv = white.makeMove(new GameMove(null, null, initState));
        totalMoves++;
        System.out.println("WHITE " + mv);
        visualizer.drawBoard(mv.result.whiteStones, mv.result.blackStones);
        // if (mv.result.terminal) keepPlaying = false;  // trust user isn't a dick

        while (true) {
            mv = black.makeMove(mv);
            totalMoves++;
            System.out.println("BLACK " + mv);
            visualizer.drawBoard(mv.result.whiteStones, mv.result.blackStones);
            if (mv.result.terminal) {
                System.out.println("\nBLACK WINS");
                break;
            }

            mv = white.makeMove(mv);
            totalMoves++;
            System.out.println("WHITE " + mv);
            visualizer.drawBoard(mv.result.whiteStones, mv.result.blackStones);
            if (mv.result.terminal) {
                System.out.println("\nWHITE WINS");
                break;
            }
        }

        System.out.printf("%nTotal moves: %d%n", totalMoves);
        System.out.printf("%n%s%n%n%s%n%nwhite captures: %d   black captures: %d%n", white, black,
                initWorkers - mv.result.blackStones.size(),
                initWorkers - mv.result.whiteStones.size());
    }

    public void playComputerAgents(Agent white, Agent black, int numMatches) {
        int whiteWins = 0;
        int blackWins = 0;
        int 长者 = 0;

        for (int i = 0; i < numMatches; i++) {
            GameMove mv = white.makeMove(new GameMove(null, null, initState));
            长者 += 1;
            while (true) {
                mv = black.makeMove(mv);
                长者 += 1;
                if (mv.result.terminal) {
                    blackWins++;
                    break;
                }
                mv = white.makeMove(mv);
                长者 += 1;
                if (mv.result.terminal) {
                    whiteWins++;
                    break;
                }
            }
        }

        System.out.printf("%d games:%n", numMatches);
        System.out.println(white);
        System.out.println(black);
    }

    public void playHumanVsComputer(Agent computer) throws InterruptedException {
        GameMove mv = playerNextMove(initState);
        int turns = 0;
        // if (mv.result.terminal) keepPlaying = false;  // trust user isn't a dick

        while (true) {
            mv = computer.makeMove(mv);
            System.out.println(computer.getTotalExpanded());
            visualizer.drawBoard(mv.result.whiteStones, mv.result.blackStones);
            if (mv.result.terminal) {
                System.out.println("Computer wins");
                break;
            }
            turns++;
            mv = playerNextMove(mv.result);
            visualizer.drawBoard(mv.result.whiteStones, mv.result.blackStones);
            if (mv.result.terminal) {
                System.out.println("Player wins");
                break;
            }
        }

        System.out.printf("Turns: %d%n", turns);
        System.out.println(computer);
    }


    public boolean isTerminal(List<IntTuple> whiteStones, List<IntTuple> blackStones) {
        if (blackStones.size() == 0 || whiteStones.size() == 0) return true;

        int whiteFinishCount = 0;
        int blackFinishCount = 0;

        for (IntTuple ws : whiteStones)
            if (ws.x == 0) whiteFinishCount++;

        if (whiteFinishCount >= NUM_REACH_BASE) return true;

        for (IntTuple ws : blackStones)
            if (ws.x == nrows - 1) blackFinishCount++;

        return blackFinishCount >= NUM_REACH_BASE;
    }

    /**
     * Wait for human input
     */
    private GameMove playerNextMove(GameState currState) throws InterruptedException {
        synchronized (visualizer) {
            while (!visualizer.humanMoveAvailable) {
                visualizer.wait();
            }
            visualizer.humanMoveAvailable = false;
        }

        IntTuple from = visualizer.humanFrom;
        IntTuple to = visualizer.humanTo;
        List<IntTuple> wsCopy = new ArrayList<>(currState.whiteStones);
        List<IntTuple> bsCopy = new ArrayList<>(currState.blackStones);

        int fromInd = wsCopy.indexOf(from);
        wsCopy.set(fromInd, to);
        int toInd = bsCopy.indexOf(to);
        if (toInd != -1) bsCopy.remove(toInd);

        return new GameMove(from, to, new GameState(isTerminal(wsCopy, bsCopy), wsCopy, bsCopy));
    }

    private static GameState boardToGameState(byte[][] board) {
        List<IntTuple> whites = new ArrayList<>();
        List<IntTuple> blacks = new ArrayList<>();

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == WHITE) whites.add(new IntTuple(i, j));
                else if (board[i][j] == BLACK) blacks.add(new IntTuple(i, j));
            }
        }

        return new GameState(false, whites, blacks);
    }

    public static void main(String[] args) {
//
//        System.out.println();
//
//        brkthru = new Breakthrough(testBoard2);
//        iter = brkthru.getActionIterator(brkthru.initState, WHITE);
//        while (iter.hasNext()) {
//            System.out.println(iter.next()); // 4 moves
//        }
//
//        System.out.println();
//
//        iter = brkthru.getActionIterator(brkthru.initState, BLACK);
//        while (iter.hasNext()) {
//            System.out.println(iter.next()); // 4 moves
//        }
    }

}
