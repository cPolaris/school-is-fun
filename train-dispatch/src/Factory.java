class Factory {

    private Factory() {}

    static Station newStation(RoutingStrategy strategy, String name, int index, Location loc, Router router) {
        switch (strategy) {
        case BASELINE:
            return new BaselineStation(name, index, loc, router);
        case IMPROVED:
            return new ImprovedStation(name, index, loc, router);
        }
        return null;
    }

    static Train newTrain(RoutingStrategy strategy, String name, int departTime, int from, int to, TrainType type, double trainLength, double speed, double costPerMile, double costPerIdleTick) {
        switch (strategy) {
            case BASELINE:
                return new BaselineTrain(name, departTime, from, to, type, trainLength, speed, costPerMile, costPerIdleTick);
            case IMPROVED:
                return new ImprovedTrain(name, departTime, from, to, type, trainLength, speed, costPerMile, costPerIdleTick);
        }
        return null;
    }
}
