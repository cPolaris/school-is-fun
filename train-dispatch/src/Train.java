import java.util.Comparator;
import java.util.List;

abstract class Train implements Comparable<Train> {

    // PHYSICAL PROPERTIES
    public final String namae;
    public final int departTime;       // Moment the train sets off
    public final int fromInd;
    public final int toInd;
    public final double speed;         // mph
    public final double length;        // Length
    public final double CPM;           // cost per mile
    public final double CPT;           // cost per idle tick
    public final TrainType type;       // train category


    // Cumulative cost of this train
    protected double cost = 0.0;

    // The path for the train to follow
    protected List<Station> path;

    // Previous station traveled to
    protected Station previousStation;

    // The train will not update until this tick
    protected int sitUntil = 0;

    protected TrainState state;

    // The scheduler to report to
    public Scheduler BOSS;


    public Train(String name, int departTime, int from, int to, TrainType traintype, double trainLen, double speed, double costPerMile, double costPerIdleTick, Scheduler boss) {
        namae = name;
        this.departTime = departTime;
        fromInd = from;
        toInd = to;
        type = traintype;
        length = trainLen;
        this.speed = speed;
        CPM = costPerMile;
        CPT = costPerIdleTick;
        BOSS = boss;
    }

    public Train(String name, int departTime, int from, int to, TrainType traintype, double trainLen, double speed, double costPerMile, double costPerIdleTick) {
        namae = name;
        this.departTime = departTime;
        fromInd = from;
        toInd = to;
        type = traintype;
        length = trainLen;
        this.speed = speed;
        CPM = costPerMile;
        CPT = costPerIdleTick;
    }

    /**
     * Core method for Train.
     * Give this train a syncing clock for taking actions according to its state.
     * @param now current master clock timeStart
     */
    public abstract void update(int now);

    public void addCost(int time, double distance) {
        cost += CPT * time;
        cost += CPM * distance;
    }

    public void setSit(int until) {
        sitUntil = until;
    }

    public void setPath(List<Station> newPath) {
        path = newPath;
    }

    public void setBoss(Scheduler theBoss) {
        BOSS = theBoss;
    }

    public void setState(TrainState st) {
        state = st;
    }

    public List<Station> path() {
        return path;
    }


    // COMPARISON RELATED
    @Override
    public int compareTo(Train b) {
        return (int) (b.CPT * b.type.timeCost() - this.CPT * this.type.timeCost());
    }

    public static Comparator<Train> comparator() {
        return new TrainPriorityComparator();
    }

    protected static class TrainPriorityComparator implements Comparator<Train> {
        /**
         * Trains with higher priorities are "smaller" when being compared.
         */
        @Override
        public int compare(Train a, Train b) {
            return a.compareTo(b);
        }
    }

}
