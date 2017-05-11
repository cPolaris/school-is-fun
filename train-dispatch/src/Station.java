import java.util.Map;


/**
 * Each station knows its neighbors, and has a queue for each outbound connection
 */
abstract class Station {

    public final int index;         // Index in the adjacency list or adjacency matrix
    public final String name;
    public final Location location;      // Location
    public final Router router;

    Map<Integer, Rail> adj;

    public Station(String name, int index, Location location, Router router) {
        this.name = name;
        this.location = location;
        this.index = index;
        this.router = router;
    }

    public void setAdjMap(Map<Integer, Rail> adjmap) {
        adj = adjmap;
    }

    /**
     * The train wishes to head to start off from this station.
     * Help the train decide whether to:
     * 1. Wait in queue
     * 2. Reroute
     * @param t the train
     * @param now current timeStart
     */
    public abstract void willMove(int now, Train t);

    /**
     * The train at the front of the queue for this station has just left and
     * runnint on trail
     * @param station
     * @param now
     */
    public abstract void didHeadTo(int station, int now, Train t);

    /**
     * The train using the rail to the station has left it.
     * @param station
     */
    public abstract void haveReached(int station, int now, Train t);

}
