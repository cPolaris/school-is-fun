package brkthru;

import java.util.Iterator;

public class AlphaBetaAgent extends Agent {

    private final int depth;


    public AlphaBetaAgent(Breakthrough gameHost, byte side, StateEvaluator evaluator, int depth) {
        super(gameHost, side, evaluator);
        this.depth = depth;
    }

    @Override
    protected GameMove thinkMove(GameMove opponentMove) {
        // alpha-beta search
        double alpha = Double.MIN_VALUE;
        double beta = Double.MAX_VALUE;
        GameMove chosenMove = null;
        double chosenValue = Double.MIN_VALUE;
        Iterator<GameMove> actionItr = newActionIterator(opponentMove.result);

        while (actionItr.hasNext()) {
            GameMove nextMove = actionItr.next();
            double nextMoveValue = minValue(nextMove.result, this.depth - 1, alpha, beta);
            if (nextMoveValue > chosenValue) {
                chosenValue = nextMoveValue;
                chosenMove = nextMove;
            }
            if (chosenValue > alpha) alpha = chosenValue;
        }

        return chosenMove;
    }

    private double maxValue(GameState st, int currDepth, double alpha, double beta) {
        if (st.terminal || currDepth == 0) {
            return evaluator.evalute(st);
        }
        double chosenVal = Double.MIN_VALUE;
        Iterator<GameMove> actionItr = newActionIterator(st);
        while (actionItr.hasNext()) {
            GameMove nextMove = actionItr.next();
            double nextMoveValue = minValue(nextMove.result, currDepth - 1, alpha, beta);
            if (nextMoveValue > chosenVal) chosenVal = nextMoveValue;
            if (chosenVal >= beta) return chosenVal;
            if (chosenVal > alpha) alpha = chosenVal;
        }
        return chosenVal;
    }

    private double minValue(GameState st, int currDepth, double alpha, double beta) {
        if (st.terminal || currDepth == 0) {
            return evaluator.evalute(st);
        }
        double chosenVal = Double.MAX_VALUE;
        Iterator<GameMove> actionItr = newActionIterator(st);
        while (actionItr.hasNext()) {
            GameMove nextMove = actionItr.next();
            double nextMoveValue = maxValue(nextMove.result, currDepth - 1, alpha, beta);
            if (nextMoveValue < chosenVal) chosenVal = nextMoveValue;
            if (chosenVal <= alpha) return chosenVal;
            if (chosenVal < beta) beta = chosenVal;
        }
        return chosenVal;
    }


    @Override
    public String agentInfo() {
        return String.format("AlphaBeta depth %d", this.depth);
    }
}
