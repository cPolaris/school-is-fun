package brkthru;

import common.IntTuple;

import java.util.Random;

/**
 * This one should beat OffensiveOne
 */
public class DefensiveTwo extends StateEvaluator {

    private final byte side;
    Random rnd = new Random();

    public DefensiveTwo(byte side) {
        this.side = side;
    }

    @Override
    public double evalute(GameState state) {
        if (side == Breakthrough.WHITE) {
            double rowSum = 0;
            int opVanguardRow = 0;

            for (IntTuple ws : state.whiteStones) {
                rowSum += ws.x;
            }

            for (IntTuple bs : state.blackStones) {
                if (bs.x > opVanguardRow) opVanguardRow = bs.x;
            }

            return 12.0 * state.whiteStones.size()
                    + 5.0 * (30.0 - state.blackStones.size())
                    + rowSum * 2.0
                    - 20.0 * opVanguardRow
                    + rnd.nextDouble();
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "Defensive 2";
    }
}
