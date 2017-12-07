package common;

public class IntTuple {
    public final int x;
    public final int y;

    public IntTuple(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", this.x, this.y);
    }

    @Override
    public boolean equals(Object obj) {
        IntTuple casted = (IntTuple) obj;
        return obj.getClass() == IntTuple.class && casted.x == this.x && casted.y == this.y;
    }

    @Override
    public int hashCode() {
        return x + x * y;
    }
}
