package brkthru;

import java.util.Random;

public class DefensiveOne extends StateEvaluator {

    private final byte side;
    Random rnd = new Random();

    public DefensiveOne(byte side) {
        this.side = side;
    }

    /**
     * 2*(number_of_own_pieces_remaining) + random()
     */
    @Override
    public double evalute(GameState state) {
        if (side == Breakthrough.WHITE) {
            return 2.0 * state.whiteStones.size() + rnd.nextDouble();
        } else {
            return 2.0 * state.blackStones.size() + rnd.nextDouble();
        }
    }

    @Override
    public String toString() {
        return "Defensive 1";
    }
}
