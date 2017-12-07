package brkthru;

import java.util.Iterator;

public class GreedyAgent extends Agent {

    public GreedyAgent(Breakthrough gameHost, byte side, StateEvaluator evaluator) {
        super(gameHost, side, evaluator);
    }

    @Override
    protected GameMove thinkMove(GameMove opponentMove) {
        GameMove chosenMove = null;
        double chosenValue = Double.MIN_VALUE;
        Iterator<GameMove> actionItr = newActionIterator(opponentMove.result);
        while (actionItr.hasNext()) {
            GameMove nextMove = actionItr.next();
            double nextMoveValue = evaluator.evalute(nextMove.result);
            if (nextMoveValue > chosenValue) {
                chosenValue = nextMoveValue;
                chosenMove = nextMove;
            }
        }
        return chosenMove;
    }

    @Override
    public String agentInfo() {
        return "1-depth Greedy";
    }
}
