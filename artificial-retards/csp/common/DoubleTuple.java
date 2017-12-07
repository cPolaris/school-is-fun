package common;

public class DoubleTuple {
    public final double x;
    public final double y;

    public DoubleTuple(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("(%f, %f)", this.x, this.y);
    }
}
