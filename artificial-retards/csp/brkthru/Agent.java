package brkthru;

import common.IntTuple;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static brkthru.Breakthrough.BLACK;
import static brkthru.Breakthrough.WHITE;

public abstract class Agent {

    public final byte side;
    protected final StateEvaluator evaluator;
    private final Breakthrough gameHost;
    private long thinkMillis;
    private long totalExpanded;
    private int madeMoves;

    /**
     *
     * @param side 1 - white. 2 - black. Determines the direction of the player's objective.
     *             White player moves from bottom to top.
     *             Black player moves from top to bottom.
     */
    public Agent(Breakthrough gameHost, byte side, StateEvaluator evaluator) {
        this.gameHost = gameHost;
        this.side = side;
        this.evaluator = evaluator;
    }

    /**
     * Given current game state, figure out the next move.
     * This method wraps {@link #thinkMove(GameMove)} by recording time spent on thinking
     * @param opponentMove opponent's last move and the resulting state
     */
    public final GameMove makeMove(GameMove opponentMove) {
        long beginMillis = System.currentTimeMillis();
        GameMove result = thinkMove(opponentMove);
        long endMillis = System.currentTimeMillis();
        thinkMillis += endMillis - beginMillis;
        madeMoves++;
        return result;
    }

    protected abstract GameMove thinkMove(GameMove opponentMove);

    public Iterator<GameMove> newActionIterator(GameState state) {
        return new ActionIterator(state, this.side);
    }

    /**
     * Lazily expand the given state. Returns all possible legal moves
     * from given state and side eventually
     */
    private class ActionIterator implements Iterator<GameMove> {
        /*
        state:
        0 - check forward
        1 - check left
        2 - check right
        3 - already checked all for current stone,
            retrieve next stone and then check forward
         */
        private byte stateByte;
        private IntTuple currStone;
        private GameMove nextMove;
        private Iterator<IntTuple> stoneIterator;

        private final byte side;
        private final List<IntTuple> whiteStones;
        private final List<IntTuple> blackStones;

        public ActionIterator(GameState state, byte side) {
            totalExpanded++;
            this.side = side;
            this.whiteStones = state.whiteStones;
            this.blackStones = state.blackStones;

            if (side == WHITE) {
                this.stoneIterator = this.whiteStones.iterator();
            } else {
                this.stoneIterator = this.blackStones.iterator();
            }

            if (stoneIterator.hasNext()) {
                nextMove = new GameMove(null, null, null);  // so that it's not null for the first call of next()
                currStone = stoneIterator.next();
                stateByte = 0;
                next();  // make the first move ready
            } else {
                nextMove = null;
            }
        }

        @Override
        public boolean hasNext() {
            return nextMove != null;
        }

        @Override
        public GameMove next() {
            if (!hasNext()) throw new NoSuchElementException();
            GameMove currMove = nextMove;

            // prepare next result
            int tryRow;
            int tryCol;

            whileLoop:
            while (true) {
                // check moves for current stone
                //  System.out.printf("checking %s %s%n", currStone, stateByte);
                switch (stateByte) {
                    case 0:
                        tryRow = side == WHITE ? currStone.x - 1 : currStone.x + 1;
                        tryCol = currStone.y;

                        if (tryRow >= 0 && tryRow < gameHost.nrows
                                && indexOfStone(whiteStones, tryRow, tryCol) == -1
                                && indexOfStone(blackStones, tryRow, tryCol) == -1) {
                            List<IntTuple> wsCopy = new ArrayList<>(whiteStones);
                            List<IntTuple> bsCopy = new ArrayList<>(blackStones);

                            if (side == WHITE) {
                                moveStone(wsCopy, currStone.x, currStone.y, tryRow, tryCol);
                            } else {
                                moveStone(bsCopy, currStone.x, currStone.y, tryRow, tryCol);
                            }

                            GameState nextState = new GameState(gameHost.isTerminal(wsCopy, bsCopy), wsCopy, bsCopy);
                            nextMove = new GameMove(new IntTuple(currStone.x, currStone.y),
                                    new IntTuple(tryRow, tryCol), nextState);
                            stateByte++;
                            return currMove;
                        }
                        break;
                    case 1:
                        tryRow = side == WHITE ? currStone.x - 1 : currStone.x + 1;
                        tryCol = side == WHITE ? currStone.y - 1 : currStone.y + 1;
                        if (tryGoLeftOrRight(tryRow, tryCol)) return currMove;
                        break;
                    case 2:
                        tryRow = side == WHITE ? currStone.x - 1 : currStone.x + 1;
                        tryCol = side == WHITE ? currStone.y + 1 : currStone.y - 1;
                        if (tryGoLeftOrRight(tryRow, tryCol)) return currMove;
                        break;
                    case 3:
                        // no legal moves for current stone, move on to the next
                        if (stoneIterator.hasNext()) {
                            currStone = stoneIterator.next();
                            stateByte = -1;
                        } else {
                            break whileLoop;  // no more legal moves
                        }
                        break;
                }
                stateByte++;
            }

            nextMove = null;
            return currMove;
        }

        private boolean tryGoLeftOrRight(int tryRow, int tryCol) {
            int tryWhiteInd = indexOfStone(whiteStones, tryRow, tryCol);
            int tryBlackInd = indexOfStone(blackStones, tryRow, tryCol);

            if (tryRow >= 0 && tryRow < gameHost.nrows && tryCol >= 0 && tryCol < gameHost.ncols) {

                List<IntTuple> wsCopy = new ArrayList<>(whiteStones);
                List<IntTuple> bsCopy = new ArrayList<>(blackStones);

                if (side == WHITE && tryWhiteInd == -1) {
                    moveStone(wsCopy, currStone.x, currStone.y, tryRow, tryCol);
                    if (tryBlackInd != -1) bsCopy.remove(tryBlackInd);
                } else if (side == BLACK && tryBlackInd == -1) {
                    moveStone(bsCopy, currStone.x, currStone.y, tryRow, tryCol);
                    if (tryWhiteInd != -1) wsCopy.remove(tryWhiteInd);
                } else {
                    return false;
                }

                GameState nextState = new GameState(gameHost.isTerminal(wsCopy, bsCopy),
                        wsCopy, bsCopy);
                nextMove = new GameMove(new IntTuple(currStone.x, currStone.y),
                        new IntTuple(tryRow, tryCol), nextState);
                stateByte++;
                return true;
            }

            return false;
        }

        private int indexOfStone(List<IntTuple> stones, int x, int y) {
            int i = 0;
            for (IntTuple curr : stones) {
                if (curr.x == x && curr.y == y) return i;
                i++;
            }
            return -1;
        }

        private void moveStone(List<IntTuple> stones, int fromR, int fromC, int toR, int toC) {
            int ind = indexOfStone(stones, fromR, fromC);
            stones.set(ind, new IntTuple(toR, toC));
        }
    }


    /**
     * @return total number of expanded nodes
     */
    public long getTotalExpanded() {
        return this.totalExpanded;
    }

    /**
     * @return total time in milliseconds spent on thinking
     */
    public long getTotalThinkingMillis() {
        return this.thinkMillis;
    }

    @Override
    public final String toString() {
        return String.format("%s: %s + %s%nExpanded %d   TotalThink %d ms%nAvg Expand %.2f   Avg Think %.0f ms",
                (side == 1 ? "WHITE" : "BLACK"), agentInfo(), evaluator,
                this.getTotalExpanded(), this.getTotalThinkingMillis(),
                (double) this.getTotalExpanded() / madeMoves, (double) this.getTotalThinkingMillis() / madeMoves
        );
    }

    protected abstract String agentInfo();
}
