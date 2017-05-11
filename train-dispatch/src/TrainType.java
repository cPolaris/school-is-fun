enum TrainType {
    A(2),     // priority
    P(1),     // passenger
    F(0.7);   // freight

    private final double timeCost;

    TrainType(double cost) {
        this.timeCost = cost;
    }

    public double timeCost() {
        return this.timeCost;
    }

}
