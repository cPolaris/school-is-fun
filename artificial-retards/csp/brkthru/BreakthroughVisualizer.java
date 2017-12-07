package brkthru;

import common.DoubleTuple;
import common.IntTuple;
import common.StdDraw;

import java.awt.Color;
import java.util.List;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * StdDraw adapted to build breakthrough game interface
 * <p>
 * Using Solarized color palette
 * http://ethanschoonover.com/solarized
 */
public class BreakthroughVisualizer implements Runnable {
    public static final Color BOARD_BORDER_COLOR = new Color(0, 43, 54);
    public static final Color BORDER_COLOR = Color.BLACK;
    public static final Color BACKGROUND_COLOR = new Color(238, 232, 213);
    public static final Color WHITE_INNER_COLOR = new Color(253, 246, 227);
    public static final Color BLACK_INNER_COLOR = new Color(130, 97, 0);
    public static final Color CYAN_COLOR = new Color(42, 161, 152);
    public static final Color ORANGE_COLOR = new Color(203, 75, 22);

    public static final double NORMAL_PEN_RADIUS = 0.005;

    private final int nRows;
    private final int nCols;
    private final double halfLength;
    private final double halfLengthWithBorder;
    private final DoubleTuple[][] centerCoords;
    private byte[][] currBoard;

    public boolean humanMoveAvailable = false;
    public IntTuple humanFrom;
    public IntTuple humanTo;

    /**
     * Initialize with given initial state.
     * The number of rows and columns cannot change again
     *
     * @param initBoard initial state
     */
    public BreakthroughVisualizer(byte[][] initBoard) {
        this.nRows = initBoard.length;
        this.nCols = initBoard[0].length;
        double bigger = nRows > nCols ? nRows : nCols;
        this.halfLength = 1.0 / bigger / 2.0;
        this.halfLengthWithBorder = halfLength * 0.85;
        this.centerCoords = new DoubleTuple[nRows][nCols];
        this.currBoard = initBoard;
        StdDraw.setCanvasSize(800, 800);
        StdDraw.enableDoubleBuffering();
    }

    /**
     * Draw a new state
     *
     * @param st the dimension should match with the one used in constructor
     */
    public void drawBoard(byte[][] st) {
        StdDraw.clear(BACKGROUND_COLOR);
        drawLineGrid();
        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nCols; j++) {
                drawStone(i, j, st[i][j]);
            }
        }
        StdDraw.show();
    }

    public void drawBoard(List<IntTuple> whites, List<IntTuple> blacks) {
        StdDraw.clear(BACKGROUND_COLOR);
        drawLineGrid();
        for (IntTuple ws : whites) {
            drawStone(ws.x, ws.y, Breakthrough.WHITE);
        }
        for (IntTuple bs : blacks) {
            drawStone(bs.x, bs.y, Breakthrough.BLACK);
        }
        StdDraw.show();
    }

    private void drawStone(int row, int col, byte stoneType) {
        DoubleTuple coord = centerCoords[row][col];
        StdDraw.setPenRadius(NORMAL_PEN_RADIUS);
        switch (stoneType) {
            case Breakthrough.WHITE:
                StdDraw.setPenColor(WHITE_INNER_COLOR);
                StdDraw.filledCircle(coord.x, coord.y, halfLengthWithBorder);
                StdDraw.setPenColor(BORDER_COLOR);
                StdDraw.circle(coord.x, coord.y, halfLengthWithBorder);
                break;
            case Breakthrough.BLACK:
                StdDraw.setPenColor(BLACK_INNER_COLOR);
                StdDraw.filledCircle(coord.x, coord.y, halfLengthWithBorder);
                StdDraw.setPenColor(BORDER_COLOR);
                StdDraw.circle(coord.x, coord.y, halfLengthWithBorder);
                break;
        }
    }

    private void drawLineGrid() {
        StdDraw.setPenRadius(NORMAL_PEN_RADIUS);
        StdDraw.setPenColor(BOARD_BORDER_COLOR);

        for (int i = 0; i < nRows; i++) {  // row
            for (int j = 0; j < nCols; j++) { // column
                double currX = halfLength + halfLength * 2.0 * j;
                double currY = halfLength + halfLength * 2.0 * i;
                this.centerCoords[nRows - 1 - i][j] = new DoubleTuple(currX, currY);
                StdDraw.square(currX, currY, halfLength);
            }
        }
    }

    public void drawCellOutline(int row, int col, Color clr) {
        DoubleTuple centerXY = centerCoords[row][col];
        StdDraw.setPenRadius(NORMAL_PEN_RADIUS * 2);
        StdDraw.setPenColor(clr);
        StdDraw.square(centerXY.x, centerXY.y, halfLength - NORMAL_PEN_RADIUS);
        StdDraw.show();
    }

    @Override
    public void run() {
        StdDraw.setMouseListener(new PlayerMoveListener());
        drawBoard(currBoard);
    }

    private class PlayerMoveListener implements MouseListener {
        // false: listening for first click
        // true: listening for second click
        private boolean listenSecondLocation;
        private IntTuple fromCell;

        private IntTuple getClickedCell(double clickedX, double clickedY) {
            for (int i = 0; i < nRows; i++) {
                for (int j = 0; j < nCols; j++) {
                    double xLow = centerCoords[i][j].x - halfLength;
                    double xHi = centerCoords[i][j].x + halfLength;
                    double yLow = centerCoords[i][j].y - halfLength;
                    double yHi = centerCoords[i][j].y + halfLength;
                    if (clickedX > xLow && clickedX < xHi && clickedY > yLow && clickedY < yHi) {
                        return new IntTuple(i, j);
                    }
                }
            }
            return null;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            int screenX = e.getX();
            int screenY = e.getY();
            double clickedX = StdDraw.userX(screenX);
            double clickedY = StdDraw.userY(screenY);
            IntTuple clickedCell = getClickedCell(clickedX, clickedY);
//            System.out.println("\tmouse pressed: " + clickedCell);

            if (!listenSecondLocation) {
                if (clickedCell == null) return;
                fromCell = clickedCell;
                drawCellOutline(clickedCell.x, clickedCell.y, ORANGE_COLOR);
                listenSecondLocation = true;
            } else {
                if (clickedCell == null) {
                    listenSecondLocation = false;
                    return;
                }

//                System.out.printf("from: %s  to: %s\n", fromCell, clickedCell);

                listenSecondLocation = false;

                // calculated next state
                synchronized (BreakthroughVisualizer.this) {
                    BreakthroughVisualizer.this.humanMoveAvailable = true;
                    BreakthroughVisualizer.this.humanFrom = fromCell;
                    BreakthroughVisualizer.this.humanTo = clickedCell;
                    BreakthroughVisualizer.this.notify();
                }
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            // do nothing
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            // do nothing
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            // do nothing
        }

        @Override
        public void mouseExited(MouseEvent e) {
            // do nothing
        }

    }
}
