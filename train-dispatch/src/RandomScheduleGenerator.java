import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class RandomScheduleGenerator {

    // constants
    static final double AVG_SPEED = 90.0;
    static final double MAX_SPEED_STDEV = 26.0;

    static final double AVG_CPM_FUEL = 20.0;
    static final double AVG_CPM_LABOR = 80.0;
    static final double MAX_CPM_STDEV_FUEL = 1.0;
    static final double MAX_CPM_STDEV_LABOR = 5.0;
    static final double MAX_CPT = 100.0;

    static final double AVG_TRAIN_LEN = 1.0;
    static final double TRAIN_LEN_STDEV = 0.05;

    // context
    private Random rgen;
    private RoutingStrategy strategy;  // used for creating trains
    private int numStations;
    private int numTrains;
    private long customSeed;
    private int timeFrame;       // time limit for all trains to depart

    // generator params, all are the same as the slider values
    private int burst;           // number of trains to depart at the same time
    private int crowdedness;     // number of trains to depart at the same station
    private int timeSense;       // how valuable is time
    private int morePTrain;      // passenger/freight train ratio
    private int speedVariance;   // how differed are trains' speeds

    // value trackers
    private int sentTrains;         // number of trains already dispatched
    private int lastChosenDepartTime;
    private int lastChosenSourceInd;

    // payload
    private List<Train> trains;

    public RandomScheduleGenerator(int nv, int nt, int tf, RoutingStrategy st, long cuseed) {
        // context
        strategy = st;
        numStations = nv;
        numTrains = nt;
        customSeed = cuseed;
        timeFrame = tf;

        // params
        timeFrame = 20;
        burst = 0;
        crowdedness = 0;
        timeSense = 0;
        morePTrain = 0;
        speedVariance = 0;

        // variable init
        sentTrains = 0;
        lastChosenDepartTime = 0;
        rgen = new Random(cuseed);
    }

    public List<Train> getSchedule() {
        trains = new ArrayList<>(numTrains);

        while (sentTrains < numTrains) {
            int nextSourceVal = nextSource();
            TrainType nextType = nextTrainType();
            trains.add(Factory.newTrain(strategy, Integer.toHexString(sentTrains++),
                nextDepart(),   // uniform. [timeFrame] controls range. [burst] controls clustering
                nextSourceVal,  // normal, clustered around vertex with max degree. [crowdedness] controls stdev
                nextDest(nextSourceVal),    // uniform, excludes source index
                nextType,  // uniform. [morePTrain] controls cutoff values
                nextTrainLen(),   // uniform. normal, around an empirical value
                nextSpeed(),      // normal, flat, based on empirical value.
                nextCpm(nextType),  //
                nextCpt() * nextType.timeCost()));  //
        }

        return trains;
    }

    public void printSchedule() {
        System.out.println("-------------------------------------------------------");
        System.out.println("name,departTime,fromInd,toInd,type,length,speed,CPM,CPT");
        for (Train train : trains) {
            System.out.printf("%s,%d,%d,%d,%s,%s,%s,%s,%s%n",
                    train.namae,
                    train.departTime,
                    train.fromInd,
                    train.toInd,
                    train.type,
                    train.length,
                    train.speed,
                    train.CPM,
                    train.CPT);
        }
    }

    /**
     * Prob of (Burst / 255) to be the same as last. Otherwise uniform among all stations
     */
    private int nextDepart() {
        int candidate = uniform(0, 256) <= burst ? lastChosenDepartTime : uniform(0, timeFrame + 1);
        lastChosenDepartTime = candidate;
        return candidate;
    }

    /**
     * Prob of (Crowdedness / 255) to be the same as last. Otherwise uniform among all stations
     */
    private int nextSource() {
        int candidate = uniform(0, 256) <= crowdedness ? lastChosenSourceInd : uniform(0, numStations);
        lastChosenSourceInd = candidate;
        return candidate;
    }

    /**
     * Uniform among all stations. Won't be the same as source
     */
    private int nextDest(int sourceInd) {
        int candidate = uniform(0, numStations);
        while (candidate == sourceInd) {
            candidate = uniform(0, numStations);
        }
        return candidate;
    }

    /**
     * Normal with stdev linear to Speed Variance
     */
    private double nextSpeed() {
        return normal(AVG_SPEED, speedVariance / 255.0 * MAX_SPEED_STDEV);
    }

    /**
     * Only fuel cost for F. Additional labor cost for A and P
     */
    private double nextCpm(TrainType tp) {
        if (tp == TrainType.F) {
            return normal(AVG_CPM_FUEL, MAX_CPM_STDEV_FUEL);
        } else {
            return normal(AVG_CPM_LABOR, MAX_CPM_STDEV_LABOR) + normal(AVG_CPM_FUEL, MAX_CPM_STDEV_FUEL);
        }
    }

    /**
     * Linear as Time Worth
     */
    private double nextCpt() {
        return timeSense / 255.0 * MAX_CPT;
    }

    /**
     * Trivial number. Fixed normal
     */
    private double nextTrainLen() {
        return normal(AVG_TRAIN_LEN, TRAIN_LEN_STDEV);
    }

    /**
     * Priority train fixed prob 0.1 (25/255)
     * Passenger train prob equal to P Train Ratio
     */
    private TrainType nextTrainType() {
        double v = uniform(0, 256);
        if (v < 25)         return TrainType.A;
        if (v < morePTrain) return TrainType.P;
        return TrainType.F;
    }

    /**
     * Normal dist with values outside 3 sigma cut off
     */
    public double normal(double avg, double stdev) {
        double candidate = rgen.nextGaussian() * stdev + avg;
        while  (candidate < avg - 3 * stdev || candidate > avg + 3 * stdev) {
            candidate = rgen.nextGaussian() * stdev + avg;
        }
        return candidate;
    }

    /**
     * Uniform int between min (inclusive) and max (exclusive)
     */
    public int uniform(int min, int max) {
        return rgen.nextInt(max - min) + min;
    }


    //////// Setters for generator params ////////

    public void setBurst(int b) {
        burst = b;
    }

    public void setCrowdedness(int crd) {
        crowdedness = crd;
    }

    public void setTimeSensitivity(int ts) {
        timeSense = ts;
    }

    public void setCompositionRatio(int cr) {
        morePTrain = cr;
    }

    public void setSpeedVar(int sv) {
        speedVariance = sv;
    }

}
