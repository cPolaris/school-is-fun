import java.util.List;


class ImprovedStation extends Station {

    private static final int CACHE_SIZE = 40;  // Max size of paths cache


    public ImprovedStation(String name, int id, Location loc, Router router) {
        super(name, id, loc, router);
    }

    @Override
    public void willMove(int now, Train t) {
        List<Station> path = t.path;

        if (path.size() == 0) {
            t.BOSS.done(t);
            return;
        }

        Rail connection = adj.get(path.get(0).index);
        int arrivalTime = connection.enqueue(index, t, now);
        int tripDuration = connection.timeToTravel(t.length, t.speed);
        int timeDepart = arrivalTime - tripDuration;

        if (timeDepart == now) {
            t.setState(TrainState.ENROUTE);
            t.setSit(now + tripDuration);
        } else {
            t.setState(TrainState.WAITING);
            t.setSit(timeDepart);
        }
    }

    @Override
    public void didHeadTo(int station, int now, Train t) {
        Rail connection = adj.get(t.path.get(0).index);
        int tripDuration = connection.timeToTravel(t.length, t.speed);
        t.setState(TrainState.ENROUTE);
        t.setSit(now + tripDuration);
    }

    @Override
    public void haveReached(int station, int now, Train t) {
        t.setState(TrainState.IDLE);
        t.update(now);
    }
}
