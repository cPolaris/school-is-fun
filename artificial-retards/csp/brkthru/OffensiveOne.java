package brkthru;

import java.util.Random;

public class OffensiveOne extends StateEvaluator {

    private final byte side;
    Random rnd = new Random();

    public OffensiveOne(byte side) {
        this.side = side;
    }

    /**
     *  2*(30 - number_of_opponent_pieces_remaining) + random()
     */
    @Override
    public double evalute(GameState state) {
        if (side == Breakthrough.WHITE) {
            return 2.0 * (30.0 - state.blackStones.size()) + rnd.nextDouble();
        } else {
            return 2.0 * (30.0 - state.whiteStones.size()) + rnd.nextDouble();
        }
    }

    @Override
    public String toString() {
        return "Offensive 1";
    }
}
