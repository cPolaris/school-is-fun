package brkthru;

public abstract class StateEvaluator {
    abstract double evalute(GameState state);

    @Override
    public abstract String toString();
}
