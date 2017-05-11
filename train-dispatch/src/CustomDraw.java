import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * <a href="http://introcs.cs.princeton.edu/java/stdlib/StdDraw.java">original version</a>
 * Copyright © 2000–2011, Robert Sedgewick and Kevin Wayne.
 * Last updated: Tue Mar 22 08:59:19 EDT 2016.
 * @author Robert Sedgewick
 * @author Kevin Wayne
 *
 * Modifed by:
 * @author Tiangang
 */

@SuppressWarnings({"FieldCanBeLocal", "unused"})
final class CustomDraw implements Runnable {

    // default colors
    private final Color DEFAULT_PEN_COLOR = Color.black;
    private final Color DEFAULT_CLEAR_COLOR = Color.white;

    // current pen color
    private Color penColor;

    // default canvas size is DEFAULT_SIZE-by-DEFAULT_SIZE
    private final int DEFAULT_SIZE = 512;
    private int width = DEFAULT_SIZE;
    private int height = DEFAULT_SIZE;

    // default pen radius
    private final double DEFAULT_PEN_RADIUS = 0.002;

    // current pen radius
    private double penRadius;

    // we drawSelf immediately or wait until next run?
    // private boolean defer = false;

    // boundary of drawing canvas, 5 %
    private final double BORDER = 0.05;
    private final double DEFAULT_XMIN = 0.0;
    private final double DEFAULT_XMAX = 1.0;
    private final double DEFAULT_YMIN = 0.0;
    private final double DEFAULT_YMAX = 1.0;
    private double xmin, ymin, xmax, ymax;

    // default font
    private final Font DEFAULT_FONT = new Font("SansSerif", Font.PLAIN, 12);
    private final Font MONO_BOLD = new Font("Monospaced", Font.PLAIN, 12);

    // current font
    private Font font;

    // double buffered graphics
    private BufferedImage offscreenImage, onscreenImage, railwayBufferedImage;
    private Graphics2D offscreen, onscreen;

    // the animationFrame for drawing to the screen
    private JFrame animationFrame;

    // Graph related
    private Collection<Rail>[] adj;
    private List<RoutingRecord> records;
    private Map<Integer, Station> stations;
    private Map<String, TrainSprite> sprites;
    private List<TrainSprite> spriteRemoveList;

    // info about the run
    private JLabel statusLabel;
    private int duration;
    private double totalCost;
    private double minCost;
    private String strategy;
    private int realTimeSeconds;
    private boolean keepRunning;

    CustomDraw(Collection<Rail>[] a, Map<Integer, Station> ss, List<RoutingRecord> re, int t, double actcost, double mcost, String strat, int realSec, boolean halfSize) {
        duration = t;
        totalCost = actcost;
        minCost = mcost;
        strategy = strat;
        stations = ss;
        adj = a;
        records = re;
        realTimeSeconds = realSec;
        sprites = new TreeMap<>();
        keepRunning = true;

        // process stations to get max X and Y coordinates in order to set canvas size
        double maxX = -1;
        double maxY = -1;
        for (Station st : ss.values()) {
            double currX = st.location.x;
            double currY = st.location.y;
            if (currX > maxX) maxX = currX;
            if (currY > maxY) maxY = currY;
        }

        if (halfSize) {
            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            int screenHeight = gd.getDisplayMode().getHeight();

            setCanvasSize((int) (screenHeight / 2.0 / maxY * maxX), (int) (screenHeight / 2.0));
            // init() has been called by setCanvasSize
        } else {
            setCanvasSize((int) (500 / maxY * maxX), 500);
            // init() has been called by setCanvasSize
        }

        setXscale(0, maxX);
        setYscale(0, maxY);
    }


    @Override
    public void run() {
        records.sort(RoutingRecord.comparator());
        spriteRemoveList = new ArrayList<>();

        int millisPerFrame = (int) Math.ceil(1000.0 / duration * realTimeSeconds);
        if (millisPerFrame < 10) {
            millisPerFrame = 0;
        }

        loadRailwayBufferedImage();

        int now = 0;
        animationFrame.setVisible(true);

        // @TODO use thread interruption instead of custom flag
        while (keepRunning && now <= duration) {
            clear();
            drawStatusBar(now, sprites.size());

            addNewSprites(now);
            setPenColor(Color.RED);
            drawSprites(now);
            cleanup();

            show(millisPerFrame);
            now++;
        }
    }


    private void init() {
        // animationFrame
        if (animationFrame != null) animationFrame.setVisible(false);
        animationFrame = new JFrame(strategy);
        offscreenImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        onscreenImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        offscreen = offscreenImage.createGraphics();
        onscreen = onscreenImage.createGraphics();
        offscreen.setColor(DEFAULT_CLEAR_COLOR);
        offscreen.fillRect(0, 0, width, height);
        setPenColor();
        setPenRadius();
        setFont();
        clear();

        // add antialiasing
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        offscreen.addRenderingHints(hints);

        // Interface layout
        ImageIcon icon = new ImageIcon(onscreenImage);
        JLabel draw = new JLabel(icon);
        statusLabel = new JLabel();
        JPanel drawingPanel = new JPanel();
        drawingPanel.setLayout(new BoxLayout(drawingPanel, BoxLayout.Y_AXIS));
        drawingPanel.add(draw);
        drawingPanel.add(statusLabel);

        animationFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        animationFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                keepRunning = false;
            }
        });

        animationFrame.setContentPane(drawingPanel);
        animationFrame.setMinimumSize(new Dimension(width, height + 40));
        animationFrame.pack();
        animationFrame.setResizable(false);
    }

    private void loadRailwayBufferedImage() {
        setPenColor(Color.BLACK);
        drawStations();
        drawEdges();
        railwayBufferedImage = deepCopy(offscreenImage);
    }

    private void addNewSprites(int now) {
        // add new sprites
        Iterator<RoutingRecord> recordsItr = records.iterator();
        while (recordsItr.hasNext()) {
            RoutingRecord r = recordsItr.next();
            if (r.timeStart != now) break;

            Location fromLoc = stations.get(r.from).location;
            Location toLoc   = stations.get(r.to).location;

            sprites.put(r.trainName, new TrainSprite(r.trainName,
                    fromLoc.x, fromLoc.y, toLoc.x, toLoc.y,
                    r.timeStart, r.timeEnd));

            recordsItr.remove();
        }
    }

    private void drawSprites(int now) {
        // process existing sprites
        for (TrainSprite s : sprites.values()) {
            if (s.timeEnd == now) {
                spriteRemoveList.add(s);
            } else {
                s.drawSelf(now);
            }
        }
    }


    private void cleanup() {
        for (TrainSprite sp : spriteRemoveList) {
            sprites.remove(sp.name);
        }
        spriteRemoveList.clear();
    }

    private void drawStatusBar(int t, int numT) {
        statusLabel.setText(String.format("Lower bound: %.2f    Actual: %.2f    Time: %d / %d    Moving trains: %d",
                minCost, totalCost, t, duration, numT));
    }

    private void drawStations() {
        for (Station station : stations.values())
            text(station.location.x, station.location.y, station.name);
    }

    private void drawEdges() {
        setPenRadius(0.001);
        for (Collection<Rail> rails : adj) {
            for (Rail rail : rails) {
                Location kore = stations.get(rail.kore).location;
                Location sore = stations.get(rail.sore).location;
                line(kore.x, kore.y, sore.x, sore.y);
            }
        }
    }

    /**
     * Set the window size to the default size 512-by-512 pixels.
     * This method must be called before any other commands.
     */
    private void setCanvasSize() {
        setCanvasSize(DEFAULT_SIZE, DEFAULT_SIZE);
    }

    /**
     * Set the window size to w-by-h pixels.
     * This method must be called before any other commands.
     *
     * @param w the width as a number of pixels
     * @param h the height as a number of pixels
     * @throws IllegalArgumentException if the width or height is 0 or negative
     */
    private void setCanvasSize(int w, int h) {
        if (w < 1 || h < 1) throw new IllegalArgumentException("width and height must be positive");
        width = w;
        height = h;
        init();
    }


    /*************************************************************************
     *  User and screen coordinate systems
     *************************************************************************/

    /**
     * Set the x-scale to be the default (between 0.0 and 1.0).
     */
    private void setXscale() {
        setXscale(DEFAULT_XMIN, DEFAULT_XMAX);
    }

    /**
     * Set the y-scale to be the default (between 0.0 and 1.0).
     */
    private void setYscale() {
        setYscale(DEFAULT_YMIN, DEFAULT_YMAX);
    }

    /**
     * Set the x-scale
     *
     * @param min the minimum value of the x-scale
     * @param max the maximum value of the x-scale
     */
    private void setXscale(double min, double max) {
        double size = max - min;
        xmin = min - BORDER * size;
        xmax = max + BORDER * size;
    }

    /**
     * Set the y-scale
     *
     * @param min the minimum value of the y-scale
     * @param max the maximum value of the y-scale
     */
    private void setYscale(double min, double max) {
        double size = max - min;
        ymin = min - BORDER * size;
        ymax = max + BORDER * size;
    }

    /**
     * Set the x-scale and y-scale
     *
     * @param min the minimum value of the x- and y-scales
     * @param max the maximum value of the x- and y-scales
     */
    private void setScale(double min, double max) {
        double size = max - min;
        xmin = min - BORDER * size;
        xmax = max + BORDER * size;
        ymin = min - BORDER * size;
        ymax = max + BORDER * size;
    }

    // helper functions that scale from user coordinates to screen coordinates and back
    private double scaleX(double x) {
        return width * (x - xmin) / (xmax - xmin);
    }

    private double scaleY(double y) {
        return height * (ymax - y) / (ymax - ymin);
    }

    private double factorX(double w) {
        return w * width / Math.abs(xmax - xmin);
    }

    private double factorY(double h) {
        return h * height / Math.abs(ymax - ymin);
    }

    private double userX(double x) {
        return xmin + x * (xmax - xmin) / width;
    }

    private double userY(double y) {
        return ymax - y * (ymax - ymin) / height;
    }


    /**
     * Clear the screen to the default color (white).
     */
    private void clear() {
        clear(DEFAULT_CLEAR_COLOR);
    }

    /**
     * Clear the screen to the given color.
     *
     * @param color the Color to make the background
     */
    private void clear(Color color) {
        offscreen.setColor(color);
        offscreen.fillRect(0, 0, width, height);
        offscreen.setColor(penColor);

        // draw the railway
        offscreen.drawImage(railwayBufferedImage, 0, 0, null);
    }

    /**
     * Set the pen size to the default (.002).
     */
    private void setPenRadius() {
        setPenRadius(DEFAULT_PEN_RADIUS);
    }

    /**
     * Set the radius of the pen to the given size.
     *
     * @param r the radius of the pen
     * @throws IllegalArgumentException if r is negative
     */
    private void setPenRadius(double r) {
        if (r < 0) throw new IllegalArgumentException("pen radius must be nonnegative");
        penRadius = r;
        float scaledPenRadius = (float) (r * DEFAULT_SIZE);
        BasicStroke stroke = new BasicStroke(scaledPenRadius, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        // BasicStroke stroke = new BasicStroke(scaledPenRadius);
        offscreen.setStroke(stroke);
    }

    /**
     * Set the pen color to the default color (black).
     */
    private void setPenColor() {
        setPenColor(DEFAULT_PEN_COLOR);
    }

    /**
     * Set the pen color to the given color. The available pen colors are
     * BLACK, BLUE, CYAN, DARK_GRAY, GRAY, GREEN, LIGHT_GRAY, MAGENTA,
     * ORANGE, PINK, RED, WHITE, and YELLOW.
     *
     * @param color the Color to make the pen
     */
    private void setPenColor(Color color) {
        penColor = color;
        offscreen.setColor(penColor);
    }

    /**
     * Set the pen color to the given RGB color.
     *
     * @param red   the amount of red (between 0 and 255)
     * @param green the amount of green (between 0 and 255)
     * @param blue  the amount of blue (between 0 and 255)
     * @throws IllegalArgumentException if the amount of red, green, or blue are outside prescribed range
     */
    private void setPenColor(int red, int green, int blue) {
        if (red < 0 || red >= 256) throw new IllegalArgumentException("amount of red must be between 0 and 255");
        if (green < 0 || green >= 256) throw new IllegalArgumentException("amount of green must be between 0 and 255");
        if (blue < 0 || blue >= 256) throw new IllegalArgumentException("amount of blue must be between 0 and 255");
        setPenColor(new Color(red, green, blue));
    }

    /**
     * Get the current font.
     */
    private Font getFont() {
        return font;
    }

    /**
     * Set the font to the default font (sans serif, 16 point).
     */
    private void setFont() {
        setFont(DEFAULT_FONT);
    }

    /**
     * Set the font to the given value.
     *
     * @param f the font to make text
     */
    private void setFont(Font f) {
        font = f;
    }

    /**
     * Draw a line from (x0, y0) to (x1, y1).
     *
     * @param x0 the x-coordinate of the starting point
     * @param y0 the y-coordinate of the starting point
     * @param x1 the x-coordinate of the destination point
     * @param y1 the y-coordinate of the destination point
     */
    private void line(double x0, double y0, double x1, double y1) {
        offscreen.draw(new Line2D.Double(scaleX(x0), scaleY(y0), scaleX(x1), scaleY(y1)));
    }

    /**
     * Write the given text string in the current font, centered on (x, y).
     *
     * @param x the center x-coordinate of the text
     * @param y the center y-coordinate of the text
     * @param s the text
     */
    private void text(double x, double y, String s) {
        offscreen.setFont(font);
        FontMetrics metrics = offscreen.getFontMetrics();
        double xs = scaleX(x);
        double ys = scaleY(y);
        int ws = metrics.stringWidth(s);
        int hs = metrics.getDescent();
        offscreen.drawString(s, (float) (xs - ws / 2.0), (float) (ys + hs));
    }

    /**
     * Display on screen, pause for t milliseconds, and turn on
     * <em>animation mode</em>: subsequent calls to
     * drawing methods such as <tt>line()</tt>, <tt>circle()</tt>, and <tt>square()</tt>
     * will not be displayed on screen until the next call to <tt>ikuzo()</tt>.
     * This is useful for producing animations (clear the screen, drawSelf a bunch of shapes,
     * display on screen for a fixed amount of timeStart, and repeat). It also speeds up
     * drawing a huge number of shapes (call <tt>ikuzo(0)</tt> to defer drawing
     * on screen, drawSelf the shapes, and call <tt>ikuzo(0)</tt> to display them all
     * on screen at once).
     *
     * @param t number of milliseconds
     */
    private void show(int t) {
        draw();
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            System.out.println("Error sleeping");
        }
    }

    private void draw() {
        onscreen.drawImage(offscreenImage, 0, 0, null);
        animationFrame.repaint();
    }

    private static BufferedImage deepCopy(BufferedImage original) {
        ColorModel cm = original.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = original.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }


    private class TrainSprite {
        String name;
        double fromX;
        double fromY;
        int timeStart;
        int timeEnd;
        double rangeX;
        double rangeY;

        TrainSprite(String nm, double fx, double fy, double tx, double ty, int tS, int tE) {
            name = nm;
            fromX = fx;
            fromY = fy;
            timeStart = tS;
            timeEnd = tE;
            rangeX = tx - fx;
            rangeY = ty - fy;
        }

        void drawSelf(int now) {
            double progress = (now - timeStart) / (double) (timeEnd - timeStart);
            setFont(MONO_BOLD);
            text(fromX + progress * rangeX, fromY + progress * rangeY, name);
            setFont();
        }
    }

}
