package brkthru;

import common.IntTuple;

public class GameMove {
    public final IntTuple from;
    public final IntTuple to;
    public final GameState result;

    public GameMove(IntTuple from, IntTuple to, GameState result) {
        this.from = from;
        this.to = to;
        this.result = result;
    }

    public GameMove(GameMove another) {
        this.from = new IntTuple(another.from.x, another.from.y);
        this.to = new IntTuple(another.to.x, another.to.y);
        this.result = new GameState(another.result);
    }

    @Override
    public String toString() {
        return String.format("%s -> %s", from, to);
    }

    @Override
    public int hashCode() {
        return this.from.hashCode() - this.to.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other.getClass() == GameMove.class
                && ((GameMove) other).from.equals(this.from)
                && ((GameMove) other).to.equals(this.to);
    }
}
