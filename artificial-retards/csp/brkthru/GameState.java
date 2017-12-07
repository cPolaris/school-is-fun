package brkthru;

import common.IntTuple;

import java.util.LinkedList;
import java.util.List;

public class GameState {
    public final boolean terminal;
    public final List<IntTuple> whiteStones;
    public final List<IntTuple> blackStones;

    /**
     * The number of rows and columns should be inferred from the game environment
     * @param terminal is this a terminal state
     * @param whiteStones remaining white stone locations
     * @param blackStones remaining black stone locations
     */
    public GameState(boolean terminal, List<IntTuple> whiteStones, List<IntTuple> blackStones) {
        this.terminal = terminal;
        this.whiteStones = new LinkedList<>(whiteStones);
        this.blackStones = new LinkedList<>(blackStones);
    }

    public GameState(GameState another) {
        this.terminal = another.terminal;
        this.whiteStones = new LinkedList<>(another.whiteStones);
        this.blackStones = new LinkedList<>(another.blackStones);
    }

}
