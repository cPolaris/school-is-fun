package flowtree;

import common.DoubleTuple;
import common.StdDraw;

import java.awt.Color;
import java.util.*;

import static brkthru.BreakthroughVisualizer.NORMAL_PEN_RADIUS;

/**
 * StdDraw library adapted to visualize the FlowTree game.
 */
public class FlowTreeVisualizer {

    enum Direction {
        LEFT, RIGHT, UP, DOWN
    }

    private final int nRows;
    private final int nCols;
    private final double halfLength;
    private final DoubleTuple[][] rowColToCanvasCoord;
    private static final Color BORDER_COLOR = Color.DARK_GRAY;
    private static final Color BACKGROUND_COLOR = Color.getColor("#3e3e3e");

    public FlowTreeVisualizer(char[][] result) {
        nRows = result.length;
        nCols = result[0].length;
        double bigger = nRows > nCols ? nRows : nCols;
        this.halfLength = 1.0 / bigger / 2.0;
        this.rowColToCanvasCoord = new DoubleTuple[nRows][nCols];
        StdDraw.setCanvasSize(800, 800);
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(BACKGROUND_COLOR);
        drawResult(result);
    }

    private void drawResult(char[][] result) {
        Set<Character> uniqueChars = new HashSet<>();

        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nCols; j++) {
                uniqueChars.add(result[i][j]);
            }
        }

        int numChars = uniqueChars.size();
        List<Color> colors = colorGradient(numChars);
        Collections.shuffle(colors);
        Map<Character, Color> colorDict = new HashMap<>();

        int ind = 0;
        for (Character uc : uniqueChars) {
            colorDict.put(uc, colors.get(ind));
            ind++;
        }

        drawLineGrid();
        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nCols; j++) {
                drawSquare(i, j, colorDict.get(result[i][j]));
            }
        }
        StdDraw.show();
    }

    private void drawLineGrid() {
        StdDraw.setPenRadius(NORMAL_PEN_RADIUS);
        StdDraw.setPenColor(BORDER_COLOR);

        for (int i = 0; i < nRows; i++) {  // row
            for (int j = 0; j < nCols; j++) { // column
                double currX = halfLength + halfLength * 2.0 * j;
                double currY = halfLength + halfLength * 2.0 * i;
                this.rowColToCanvasCoord[nRows - 1 - i][j] = new DoubleTuple(currX, currY);
                StdDraw.square(currX, currY, halfLength);
            }
        }
    }

    private void drawSquare(int row, int col, Color color) {
        DoubleTuple coord = rowColToCanvasCoord[row][col];
        StdDraw.setPenColor(color);
        StdDraw.filledSquare(coord.x, coord.y, halfLength);
    }

    /**
     * direction refers to the direction of the "left arm" of L
     * UP: arms of L goes UP and RIGHT
     * RIGHT: RIGHT, DOWN
     * DOWN: DOWN, LEFT
     * LEFT: LEFT, UP
     */
    private void drawEl(int row, int col, Direction dir) {
        // @todo P E R F E C T I O N
    }

    private void drawSourceDot(int row, int col, Direction dir) {
        // @todo P E R F E C T I O N
        switch (dir) {
            case LEFT:
                break;
            case RIGHT:
                break;
            case UP:
                break;
            case DOWN:
                break;
        }
    }

    private void drawPipe(int row, int col, Direction dir) {
        // @todo P E R F E C T I O N
        if (dir == Direction.LEFT || dir == Direction.RIGHT) {

        } else {

        }
    }

    public static List<Color> colorGradient(int numColors) {
        List<Color> colors = new ArrayList<>();
        float hueDivision = 360.0f / numColors;
        for (int i = 0; i < numColors; i++)
            colors.add(Color.getHSBColor(i * hueDivision / 360.0f, 0.62f, 0.88f));
        return colors;
    }

    public static void main(String[] args) {
        char[][] in55 = {
                {'B', '_', '_', 'R', 'O'},
                {'_', '_', '_', 'Y', '_'},
                {'_', '_', 'Y', '_', '_'},
                {'_', 'R', 'O', '_', 'G'},
                {'_', 'B', 'G', '_', '_'}};

        char[][] out55 = {
                {'B', 'R', 'R', 'R', 'O'},
                {'B', 'R', 'Y', 'Y', 'O'},
                {'B', 'R', 'Y', 'O', 'O'},
                {'B', 'R', 'O', 'O', 'G'},
                {'B', 'B', 'G', 'G', 'G'}
        };

        FlowTreeVisualizer viz = new FlowTreeVisualizer(out55);
    }
}
