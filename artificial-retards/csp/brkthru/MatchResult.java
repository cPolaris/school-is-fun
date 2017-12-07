package brkthru;

public class MatchResult {
    private final int turns;
    private final byte winner;
    private final long whiteExpand;
    private final long blackExpand;
    private final long whiteMillis;
    private final long blackMillis;
    private final GameState finalState;

    public MatchResult(int turns, byte winner, long whiteExpand, long blackExpand, long whiteMillis, long blackMillis, GameState finalState) {
        this.turns = turns;
        this.winner = winner;
        this.whiteExpand = whiteExpand;
        this.blackExpand = blackExpand;
        this.whiteMillis = whiteMillis;
        this.blackMillis = blackMillis;
        this.finalState = finalState;
    }
}
