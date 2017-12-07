package brkthru;

import java.util.Random;

/**
 * This one should beat DefensiveOne
 */
public class OffensiveTwo extends StateEvaluator {

    private final byte side;
    Random rnd = new Random();

    public OffensiveTwo(byte side) {
        this.side = side;
    }

    @Override
    public double evalute(GameState state) {
        return 0;
    }

    @Override
    public String toString() {
        return "Offensive 2";
    }
}
