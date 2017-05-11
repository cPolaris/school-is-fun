
class ImprovedTrain extends Train {

    public ImprovedTrain(String name, int departTime, int from, int to, TrainType traintype, double trainLen, double speed, double costPerMile, double costPerIdleTick) {
        super(name, departTime, from, to, traintype, trainLen, speed, costPerMile, costPerIdleTick);
        state = TrainState.IDLE;
    }

    @Override
    public void update(int now) {
        if (now < sitUntil) return;

        switch (state) {
            case IDLE:
                previousStation = path.remove(0);
                previousStation.willMove(now, this);
                break;

            case WAITING:
                previousStation.didHeadTo(path.get(0).index, now, this);
                break;

            case ENROUTE:
                previousStation.haveReached(path.get(0).index, now, this);
                break;
        }
    }

}
