package flowtree;

import common.IntTuple;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;


/**
 * Variable: X_ij
 * Value: a character
 * Domain: given by input file. A set of certain characters
 * Constraints:
 * - Must have 1 neighbor of same color
 * The final assignment for a path of any color requires 2 neighbors
 * Complete test:
 * - no unassigned variable
 * - path between each pair of start and end
 */
public class FlowTree {
    private static final char SPACE = '_';

    enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private final List<Character> domain;
    private final int nrows;
    private final int ncols;
    private final char[][] initState;
    private final List<IntTuple> initUnassedVars;
    private final Set<IntTuple> initSourceVars;
    private long attemptedAssignments = 0;


//    private final Object reportLock = new Object();


    /**
     * Init with given file input.
     *
     * @param inputPath path to input file
     * @throws IOException if cannot read file
     */
    public FlowTree(String inputPath) throws IOException {
        List<String> rawLines = Files.readAllLines(Paths.get(inputPath));

        this.nrows = rawLines.size();
        this.ncols = rawLines.get(0).length();

        char[][] grid = new char[nrows][ncols];
        Set<Character> uniqueVars = new HashSet<>();
        this.initSourceVars = new HashSet<>();
        this.initUnassedVars = new ArrayList<>();

        for (int r = 0; r < nrows; r++) {
            for (int c = 0; c < ncols; c++) {
                char curr = rawLines.get(r).charAt(c);
                grid[r][c] = curr;
                if (curr == SPACE) {
                    initUnassedVars.add(new IntTuple(r, c));
                } else {
                    uniqueVars.add(curr);
                    initSourceVars.add(new IntTuple(r, c));
                }
            }
        }

        this.domain = new ArrayList<>(uniqueVars);
        this.initState = grid;
    }

    public boolean solveDumbRandom() {
        this.attemptedAssignments = 0;

        LinkedList<IntTuple> unassed = copyOf(initUnassedVars);
        Collections.shuffle(unassed);

        long startMillis = System.currentTimeMillis();
        char[][] result = solveDumbRandomRecurse(copyOf(this.initState), copyOf(unassed));
        long elapsedMillis = System.currentTimeMillis() - startMillis;

        System.out.printf("attempted assignments: %d%n", this.attemptedAssignments);
        System.out.printf("time elapsed: %d ms%n", elapsedMillis);
        print2d(result);

        return result != null;
    }


    private char[][] solveDumbRandomRecurse(char[][] currAss, LinkedList<IntTuple> unassed) {
        if (unassed.size() == 0) return currAss;

        IntTuple var = unassed.removeLast();
        int varRow = var.x;
        int varCol = var.y;

        LinkedList<Character> domainCopy = new LinkedList<>(this.domain);
        Collections.shuffle(domainCopy);

        for (Character val : domainCopy) {
            currAss[varRow][varCol] = val;
            attemptedAssignments++;

            if (consistent(currAss)) {
                char[][] result = solveDumbRandomRecurse(copyOf(currAss), copyOf(unassed));
                if (result != null && isSolution(result)) return result;
            }
        }

        return null;
    }


    public boolean solveDumbRandomMultiThread() {
        LinkedList<IntTuple> unassed = copyOf(initUnassedVars);
        Collections.shuffle(unassed);

        long startMillis = System.currentTimeMillis();

        IntTuple firstVar = unassed.removeLast();
        LinkedList<Callable<char[][]>> solvers = new LinkedList<>();

        for (Character val : this.domain) {
            char[][] initCopy = copyOf(initState);
            initCopy[firstVar.x][firstVar.y] = val;
            solvers.add(new DumbRandomWorker(initCopy, copyOf(unassed)));
        }

        int n = solvers.size();
        System.out.printf("putting %d threads to work%n", n);
        ExecutorService exeService = Executors.newFixedThreadPool(n);
        CompletionService<char[][]> ecs = new ExecutorCompletionService<>(exeService);

//        List<Future<char[][]>> futures = new ArrayList<>(n);
        char[][] result = null;
        try {
            for (Callable<char[][]> s : solvers) {
//                futures.add(ecs.submit(s));
                ecs.submit(s);
            }

            for (int i = 0; i < n; i++) {
                try {
                    char[][] currResult = ecs.take().get();
                    if (currResult != null) {
                        result = currResult;
                        break;
                    }
                } catch (ExecutionException | InterruptedException ignore) {
                    System.out.println("ExecutionException | InterruptedException");
                }
            }
        } finally {
//            for (Future<char[][]> f : futures)
//                f.cancel(true);
            System.out.println("shutting down");
            exeService.shutdownNow();
        }

        long elapsedMillis = System.currentTimeMillis() - startMillis;

        System.out.printf("time elapsed: %d ms%n", elapsedMillis);
        print2d(result);

        return result != null;
    }

    /**
     * Smart variable selection, value ordering, forward checking
     *
     * @return true if solved
     */
    public boolean solveSmart() {

        this.attemptedAssignments = 0;

        List<FlowTreeVariable> unassVars = new ArrayList<>();
        for (IntTuple uaVar : this.initUnassedVars) {
            unassVars.add(new FlowTreeVariable(uaVar.x, uaVar.y,
                    numNeighbors(this.initState, uaVar.x, uaVar.y),
                    new LinkedList<>(this.domain)));
        }

        long startMillis = System.currentTimeMillis();
        char[][] result = solveSmartRecurse(copyOf(this.initState), unassVars);
        long elapsedMillis = System.currentTimeMillis() - startMillis;

        System.out.printf("attempted assignments: %d%n", this.attemptedAssignments);
        System.out.printf("time elapsed: %d ms%n", elapsedMillis);
        print2d(result);
        return result != null;

    }

    private char[][] solveSmartRecurse(char[][] currAss, List<FlowTreeVariable> unassedVars) {
        if (unassedVars.size() == 0) return currAss;

        FlowTreeVariable var_max = findMax(unassedVars);
        unassedVars.remove(var_max);
        //System.out.println(unassedVars.size());

        int varRow=var_max.row;
        int varCol=var_max.col;
        updateNeighborFourDirection(var_max,unassedVars);
        LinkedList<Character> domainCopy = new LinkedList<>(var_max.legalValues);

        for (Character val : domainCopy) {
            currAss[varRow][varCol] = val;
            attemptedAssignments++;

            if (consistent(currAss)) {
                char[][] result = solveSmartRecurse(copyOf(currAss), copyof(unassedVars));
                if (result != null && isSolution(result)) return result;
            }
        }
        return null;
    }

    public boolean solveSmarter() {

        this.attemptedAssignments = 0;


        List<FlowTreeVariable> unassVars = new ArrayList<>();
        for (IntTuple uaVar : this.initUnassedVars) {
            unassVars.add(new FlowTreeVariable(uaVar.x, uaVar.y,
                    numNeighbors(this.initState, uaVar.x, uaVar.y),
                    new ArrayList<>(this.domain)));
        }

        long startMillis = System.currentTimeMillis();
        char[][] result = solveSmarterRecurse(copyOf(this.initState), unassVars);
        long elapsedMillis = System.currentTimeMillis() - startMillis;

        System.out.printf("attempted assignments: %d%n", this.attemptedAssignments);
        System.out.printf("time elapsed: %d ms%n", elapsedMillis);
        print2d(result);
        return result != null;

    }

    private char[][] solveSmarterRecurse(char[][] currAss, List<FlowTreeVariable> unassedVars) {
        if (unassedVars.size() == 0) return currAss;

        print2d(currAss);
        FlowTreeVariable var_max = findMax(unassedVars);

        //System.out.println(var_max.legalValues+" "+var_max.row+" "+var_max.col);
        //print2d(currAss);
        int varRow=var_max.row;
        int varCol=var_max.col;
        unassedVars.remove(var_max);
        updateNeighborFourDirection(var_max,unassedVars);
        //LinkedList<Character> domainCopy = new LinkedList<>(var_max.legalValues);

        for (Character val : var_max.legalValues) {
            currAss[varRow][varCol] = val;
            attemptedAssignments++;

            if (consistent(currAss)) {
                boolean inference=ac3(copyOf(currAss),copyof(unassedVars));
                if(inference == true){
                    char[][] result = solveSmarterRecurse(copyOf(currAss), copyof(unassedVars));
                    if (result != null && isSolution(result)) return result;
                }
            }
        }
        return null;
    }

    private boolean ac3 (char[][] ass, List<FlowTreeVariable> unassedVars) {
        List<Pair> worklist = new ArrayList<>();

        for (int row = 0; row < this.nrows; row++) {
            for (int col = 0; col < this.ncols; col++) {
                char currVal = ass[row][col];
                if (currVal != SPACE) continue;
                // up
                if (row > 0) {
                    if (ass[row - 1][col] == SPACE) {
                        worklist.add(new Pair(new IntTuple(row,col),new IntTuple(row-1,col)));
                    }
                }
                // down
                if (row < nrows - 1) {
                    if (ass[row + 1][col] == SPACE) {
                        worklist.add(new Pair(new IntTuple(row,col),new IntTuple(row + 1,col)));
                    }
                }
                // left
                if (col > 0) {
                    if (ass[row][col - 1] == SPACE) {
                        worklist.add(new Pair(new IntTuple(row,col),new IntTuple(row,col - 1)));
                    }
                }
                // right
                if (col < ncols - 1) {
                    if (ass[row][col + 1] == SPACE) {
                        worklist.add(new Pair(new IntTuple(row,col),new IntTuple(row,col + 1)));
                    }
                }
            }

        }

        while (!worklist.isEmpty()){
            Pair selected_arc=worklist.remove(0);
            if (arc_reduce(ass,selected_arc,unassedVars)){
                if (findFlowTreeVariable(selected_arc,unassedVars).legalValues.size()==0){
                    System.out.println(findFlowTreeVariable(selected_arc,unassedVars).row+" "+findFlowTreeVariable(selected_arc,unassedVars).col);
                    System.out.println(findFlowTreeVariable(selected_arc,unassedVars).legalValues);
                    return false;
                }
                // up
                if (selected_arc.left.x > 0) {
                    if (ass[selected_arc.left.x - 1][selected_arc.left.y] == SPACE &&
                            selected_arc.left.x - 1 != selected_arc.right.x &&
                            selected_arc.left.y != selected_arc.right.y) {
                        worklist.add(new Pair(new IntTuple(selected_arc.left.x - 1,selected_arc.left.y),new IntTuple(selected_arc.left.x ,selected_arc.left.y)));

                    }
                }
                // down
                if (selected_arc.left.x  < nrows - 1) {
                    if (ass[selected_arc.left.x + 1][selected_arc.left.y] == SPACE &&
                            selected_arc.left.x + 1 != selected_arc.right.x &&
                            selected_arc.left.y != selected_arc.right.y) {
                        worklist.add(new Pair(new IntTuple(selected_arc.left.x + 1,selected_arc.left.y),new IntTuple(selected_arc.left.x ,selected_arc.left.y)));

                    }
                }
                // left
                if (selected_arc.left.y > 0) {
                    if (ass[selected_arc.left.x][selected_arc.left.y - 1] == SPACE &&
                            selected_arc.left.x != selected_arc.right.x &&
                            selected_arc.left.y - 1 != selected_arc.right.y) {
                        worklist.add(new Pair(new IntTuple(selected_arc.left.x,selected_arc.left.y - 1),new IntTuple(selected_arc.left.x ,selected_arc.left.y)));

                    }
                }
                // right
                if (selected_arc.left.y < ncols - 1) {
                    if (ass[selected_arc.left.x][selected_arc.left.y + 1] == SPACE &&
                            selected_arc.left.x != selected_arc.right.x &&
                            selected_arc.left.y + 1 != selected_arc.right.y) {
                        worklist.add(new Pair(new IntTuple(selected_arc.left.x,selected_arc.left.y + 1),new IntTuple(selected_arc.left.x ,selected_arc.left.y)));

                    }
                }

            }
        }

        return true;

    }

    private boolean arc_reduce (char[][] ass, Pair p, List<FlowTreeVariable> unassedVars) {
        char[][] ass_copy=copyOf(ass);

        boolean change=false;
        int x_left=p.left.x;
        int y_left=p.left.y;
        int x_right=p.right.x;
        int y_right=p.right.y;
        List<Character> char_to_remove= new ArrayList<>();

        for (FlowTreeVariable wtf: unassedVars) {
            if (wtf.row == x_left && wtf.col == y_left) {
                char_to_remove.clear();
                for (FlowTreeVariable wtfy: unassedVars){
                    if (wtfy.row == x_right && wtfy.col == y_right){
                        for (Character cx: wtf.legalValues){
                            ass_copy[wtf.row][wtf.col]=cx;
                            int consis_counter=0;
                            for (Character cy: wtfy.legalValues){
                                ass_copy[wtfy.row][wtfy.col]=cy;
                                if (consistent(ass_copy)){
                                    consis_counter++;
                                }
                            }
                            if (consis_counter == 0){
                                char_to_remove.add(cx);
                                //wtf.legalValues.remove(cx);
                                change=true;
                            }
                        }
                    }
                }
            }
        }

        if (char_to_remove.size() != 0) {
            for (FlowTreeVariable wtf: unassedVars) {
                if (wtf.row == x_left && wtf.col == y_left) {
                    for (Character e: char_to_remove){
                        wtf.legalValues.remove(e);
                    }
                }
            }

        }
        /*
        if (unassedVars.size()!=0) {
            for (FlowTreeVariable wtf: unassedVars) {
                //System.out.println(wtf.row);
                //System.out.println(wtf.col);
                if (wtf.row == x_left && wtf.col == y_left) {
                    vx = wtf;
                }
            }

            for (FlowTreeVariable wtfy: unassedVars) {
                System.out.println(wtfy.legalValues.size());
                wtfy.legalValues.remove(0);
                if (wtfy.row == x_right && wtfy.col == y_right) {
                    vy = wtfy;
                }
            }
            //System.out.println(vx.legalValues.size());
            for (Character cx: vx.legalValues){
                ass_copy[vx.row][vx.col]=cx;
                int consis_counter=0;
                for (Character cy: vy.legalValues){
                    ass_copy[vy.row][vy.col]=cy;
                    if (consistent(ass_copy)){
                        consis_counter++;
                    }
                }
                if (consis_counter == 0){
                    //vx.legalValues.remove(cx);
                    change=true;
                }
            }
        }
        */
        return change;
    }

    private FlowTreeVariable findFlowTreeVariable(Pair p, List<FlowTreeVariable> unassedVars){
        int x_left=p.left.x;
        int y_left=p.left.y;
        int x_right=p.right.x;
        int y_right=p.right.y;
        FlowTreeVariable returned=null;
        for (FlowTreeVariable ft: unassedVars){
            if (ft.row == x_left && ft.col == y_left) {
                returned=new FlowTreeVariable(ft.row,ft.col,ft.numNeighbors,ft.legalValues);
            }
        }
        return returned;
    }

    public static class Pair {
        public final IntTuple left;
        public final IntTuple right;

        public Pair (IntTuple left, IntTuple right) {
            this.left=left;
            this.right=right;
        }

        @Override
        public int hashCode() {
            return this.left.hashCode()-this.right.hashCode();
        }

        @Override
        public boolean equals(Object other) {
            return other.getClass() == Pair.class
                    && ((Pair) other).left.equals(this.left)
                    && ((Pair) other).right.equals(this.right);

        }
    }

    private FlowTreeVariable findMax(List<FlowTreeVariable> ft) {
        if (ft.size() == 0) return null;
        int row=-1;
        int col=-1;
        int numNeighbors=-1;
        List<Character> legalValues=null;

        for (FlowTreeVariable wtf: ft) {
            if (wtf.numNeighbors > numNeighbors) {
                numNeighbors=wtf.numNeighbors;
                row=wtf.row;
                col=wtf.col;
                legalValues=copyOfCharacter(wtf.legalValues);
            }
        }

        return new FlowTreeVariable(row,col,numNeighbors,legalValues);
    }
    /**
     * @param ass an assignment
     * @return true if:
     * 1. for all init assigned vars, neighbor with the same color cannot exceed 1
     * 2. for all init unassigned vars, but currently assigned,
     * neighbor with the same color cannot exceed 2
     */
    private boolean consistent(char[][] ass) {
        for (int row = 0; row < this.nrows; row++) {
            for (int col = 0; col < this.ncols; col++) {
                char currVal = ass[row][col];
                if (currVal == SPACE) continue;

                int sameValCount = 0;
                int neighborCount = 0;

                // up
                if (row > 0) {
                    if (ass[row - 1][col] == currVal) sameValCount++;
                    if (ass[row - 1][col] != SPACE) neighborCount++;
                }

                // down
                if (row < nrows - 1) {
                    if (ass[row + 1][col] == currVal) sameValCount++;
                    if (ass[row + 1][col] != SPACE) neighborCount++;
                }

                // left
                if (col > 0) {
                    if (ass[row][col - 1] == currVal) sameValCount++;
                    if (ass[row][col - 1] != SPACE) neighborCount++;
                }

                // right
                if (col < ncols - 1) {
                    if (ass[row][col + 1] == currVal) sameValCount++;
                    if (ass[row][col + 1] != SPACE) neighborCount++;
                }

                if (row == 0) neighborCount++;
                if (col == 0) neighborCount++;
                if (row == nrows-1) neighborCount++;
                if (col == ncols-1) neighborCount++;

                //System.out.println(row+" "+col+" "+sameValCount+" "+neighborCount);

                if (this.initSourceVars.contains(new IntTuple(row, col))) { // this is a source var
                    if (sameValCount > 1 || (neighborCount == 4 && sameValCount != 1)) return false;
                } else {
                    if (sameValCount > 2 || (neighborCount == 4 && sameValCount != 2 )) return false;
                    //print2d(ass);

                    //if (sameValCount == 1 && neighborCount == 3) return false;
                }
            }
        }

        return true;
    }

    /**
     * @param ass an assignment
     * @return true if there is a path of the same color connecting
     * every pair of source cells. In other words:
     * 1. Each source cell has exactly one neighbor of the same color
     * 2. Each intermediate cell has exactly two neighbors of the same color
     */
    private boolean isSolution(char[][] ass) {
        for (int row = 0; row < this.nrows; row++) {
            for (int col = 0; col < this.ncols; col++) {
                char currVal = ass[row][col];
                int sameValCount = 0;

                // up
                if (row > 0) {
                    if (ass[row - 1][col] == currVal) sameValCount++;
                }

                // down
                if (row < nrows - 1) {
                    if (ass[row + 1][col] == currVal) sameValCount++;
                }

                // left
                if (col > 0) {
                    if (ass[row][col - 1] == currVal) sameValCount++;
                }

                // right
                if (col < ncols - 1) {
                    if (ass[row][col + 1] == currVal) sameValCount++;
                }

                if (this.initSourceVars.contains(new IntTuple(row, col))) { // this is a source var
                    if (sameValCount != 1) return false;
                } else {
                    if (sameValCount != 2) return false;
                }
            }
        }

        return true;
    }

    private static int numNeighbors(char[][] ass, int row, int col) {
        int count = 0;
        int nrows = ass.length;
        int ncols = ass[0].length;
        //System.out.println(ncols);
        // up
        if (row > 0 && ass[row - 1][col] != SPACE) count++;
        // down
        if (row < nrows - 1 && ass[row + 1][col] != SPACE) count++;
        // left
        if (col > 0 && ass[row][col - 1] != SPACE) count++;
        // right
        if (col < ncols - 1 && ass[row][col + 1] != SPACE) count++;

        if (row == nrows-1 ||row == 0) count++;

        if (col == ncols-1 ||col == 0) count++;

        return count;
    }

    public static char[][] copyOf(char[][] original) {
        int rows = original.length;
        int cols = original[0].length;
        char[][] copied = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(original[i], 0, copied[i], 0, cols);
        }
        return copied;
    }

    public static LinkedList<IntTuple> copyOf(List<IntTuple> original) {
        LinkedList<IntTuple> copied = new LinkedList<>();
        for (IntTuple ot : original) {
            copied.add(new IntTuple(ot.x, ot.y));
        }
        return copied;
    }

    public ArrayList<FlowTreeVariable> copyof(List<FlowTreeVariable> original) {
        ArrayList<FlowTreeVariable> copied = new ArrayList<>();
        for (FlowTreeVariable wtf: original) {
            copied.add(new FlowTreeVariable(wtf.row,wtf.col,wtf.numNeighbors,wtf.legalValues));
        }
        return copied;
    }

    public ArrayList<Character> copyOfCharacter(List<Character> original) {
        ArrayList<Character> copied = new ArrayList<>();
        for (Character e : original) {
            copied.add(e);
        }
        return copied;
    }

    private void updateNeighborFourDirection (FlowTreeVariable ft, List<FlowTreeVariable> unassedVars) {
        //left
        int row_left=ft.row;
        int col_left=ft.col-1;
        //right
        int row_right=row_left;
        int col_right=ft.col+1;
        //up
        int row_up=ft.row-1;
        int col_up=ft.col;
        //down
        int row_down=ft.row+1;
        int col_down=ft.col;

        boolean left=false;
        boolean right=false;
        boolean up=false;
        boolean down=false;

        if (col_left >= 0 ) left=true; //left
        if (col_right <= this.ncols ) right=true; //right
        if (row_up >= 0 ) up=true; //up
        if (row_down <= this.nrows) down=true; //down

        for (FlowTreeVariable wtf:unassedVars) {
            if (left) {
                if (row_left == wtf.row && col_left == wtf.col) {
                    wtf.updateNeighbor();
                }
            }
            if (right){
                if (row_right == wtf.row && col_right == wtf.col) {
                    wtf.updateNeighbor();
                }
            }
            if (up) {
                if (row_up == wtf.row && col_up == wtf.col) {
                    wtf.updateNeighbor();
                }
            }
            if (down) {
                if (row_down == wtf.row && col_down == wtf.col) {
                    wtf.updateNeighbor();
                }
            }
        }

    }

    public static void print2d(char[][] arr) {
        if (arr == null) return;
        System.out.println("solution:");
        for (char[] anArr : arr) {
            for (int j = 0; j < arr[0].length; j++) {
                System.out.printf("%c ", (int) anArr[j]);
            }
            System.out.println();
        }
    }

    private final class DumbRandomWorker implements Callable<char[][]> {

        char[][] is;
        LinkedList<IntTuple> uass;
        long assignments;

        DumbRandomWorker(char[][] initState, LinkedList<IntTuple> unassed) {
            this.is = initState;
            this.uass = unassed;
            this.assignments = 0;
        }

        @Override
        public char[][] call() {
            char[][] result = recurse(this.is, this.uass);
            if (result != null) {
                System.out.printf("%s solved: attempted assignments: %d%n", Thread.currentThread().getName(), this.assignments);
            }
            return result;
        }

        private char[][] recurse(char[][] currAss, LinkedList<IntTuple> unassed) {
            if (unassed.size() == 0) return currAss;

            IntTuple var = unassed.removeLast();
            int varRow = var.x;
            int varCol = var.y;

            LinkedList<Character> domainCopy = new LinkedList<>(FlowTree.this.domain);
            Collections.shuffle(domainCopy);

            for (Character val : domainCopy) {
                if (Thread.currentThread().isInterrupted()) return null;

                currAss[varRow][varCol] = val;
                this.assignments++;

                if (consistent(currAss)) {
                    char[][] result = recurse(copyOf(currAss), copyOf(unassed));
                    if (result != null && isSolution(result)) return result;
                }
            }

            return null;
        }
    }

    private class FlowTreeVariable {
        final int row;
        final int col;
        int numNeighbors;
        List<Character> legalValues;

        private FlowTreeVariable(int row, int col, int numNeighbors, List<Character> legalValues) {
            this.row = row;
            this.col = col;
            this.numNeighbors = numNeighbors;
            this.legalValues = legalValues;
        }

        private void updateNeighbor() {
            this.numNeighbors++;
        }

        /*
        Contract of hashCode:
        hashCode() of the same object must not change during execution
        if two objects equals(), they must have the same hashCode()
        if two objects have the same hashCode(), they do not have to be equals(), but better if they do
         */

        @Override
        public int hashCode() {
            return row + row * col;
        }

        @Override
        public boolean equals(Object other) {
            return other.getClass() == FlowTreeVariable.class
                    && ((FlowTreeVariable)other).row == this.row
                    && ((FlowTreeVariable)other).col == this.col;
        }
        //what if we want to look up the object based on row and col?
    }
}
