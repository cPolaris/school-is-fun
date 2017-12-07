package brkthru;

import java.util.Iterator;

public class NaiveMinimaxAgent extends Agent {
    private final int depth;

    public NaiveMinimaxAgent(Breakthrough gameHost, byte side, StateEvaluator evaluator, int depth) {
        super(gameHost, side, evaluator);
        this.depth = depth;
    }

    @Override
    protected GameMove thinkMove(GameMove opponentMove) {
        GameMove chosenMove = null;
        double chosenValue = Double.MIN_VALUE;
        Iterator<GameMove> actionItr = newActionIterator(opponentMove.result);
        while (actionItr.hasNext()) {
            GameMove nextMove = actionItr.next();
            double nextMoveValue = minValue(nextMove.result, this.depth - 1);
            if (nextMoveValue > chosenValue) {
                chosenValue = nextMoveValue;
                chosenMove = nextMove;
            }
        }
        return chosenMove;
    }

    private double minValue(GameState st, int currDepth) {
        if (st.terminal || currDepth == 0) {
            return evaluator.evalute(st);
        }
        double minVal = Double.MAX_VALUE;
        Iterator<GameMove> actionItr = newActionIterator(st);
        while (actionItr.hasNext()) {
            GameMove nextMove = actionItr.next();
            double nextMoveValue = maxValue(nextMove.result, currDepth - 1);
            if (nextMoveValue < minVal) {
                minVal = nextMoveValue;
            }
        }
        return minVal;
    }

    private double maxValue(GameState st, int currDepth) {
        if (st.terminal || currDepth == 0) {
            return evaluator.evalute(st);
        }
        double minVal = Double.MIN_VALUE;
        Iterator<GameMove> actionItr = newActionIterator(st);
        while (actionItr.hasNext()) {
            GameMove nextMove = actionItr.next();
            double nextMoveValue = minValue(nextMove.result, currDepth - 1);
            if (nextMoveValue > minVal) {
                minVal = nextMoveValue;
            }
        }
        return minVal;
    }

    @Override
    public String agentInfo() {
        return String.format("Naive Minimax depth %d", this.depth);
    }
}
