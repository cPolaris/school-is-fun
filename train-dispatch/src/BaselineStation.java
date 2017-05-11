import java.util.List;


class BaselineStation extends Station {

    public BaselineStation(String name, int id, Location loc, Router router) {
        super(name, id, loc, router);
    }


    @Override
    public void willMove(int now, Train t) {
        List<Station> path = t.path();

        if (path.size() == 0) {
            t.BOSS.done(t);
            return;
        }

        int latestUnlock = -1;
        Station currStation = null;
        Station nextStation = this;

        // Calculate latest unlock time
        for (Station nextInPath : path) {
            currStation = nextStation;
            nextStation = nextInPath;
            int unlockToNext = currStation.adj.get(nextStation.index).blockedUntil();
            if (unlockToNext > latestUnlock) {
                latestUnlock = unlockToNext;
            }
        }

        if (latestUnlock <= now) {
            didHeadTo(-1, now, t);
        } else {
            t.setState(TrainState.WAITING);
            t.setSit(latestUnlock);
            t.addCost(latestUnlock - now, 0);
            t.update(now);
        }

    }

    @Override
    public void didHeadTo(int station, int now, Train t) {
        Station currStation = null;
        Station nextStation = this;

        // queue for each station in the path
        int tripTime = now;
        for (Station nextInPath : t.path) {
            currStation = nextStation;
            nextStation = nextInPath;
            tripTime = currStation.adj.get(nextStation.index).enqueue(currStation.index, t, tripTime);
        }

        t.path.clear();
        t.setState(TrainState.ENROUTE);
        t.setSit(tripTime);
    }

    @Override
    public void haveReached(int station, int now, Train t) {

    }

}
