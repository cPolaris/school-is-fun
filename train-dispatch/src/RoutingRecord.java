import java.util.Comparator;


class RoutingRecord {

    final int timeStart;
    final int timeEnd;
    final String trainName;
    final int from;
    final int to;


    public RoutingRecord(int timeStart, int timeEnd, String trainName, int from, int to) {
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.trainName = trainName;
        this.from = from;
        this.to = to;
    }

    public static Comparator<RoutingRecord> comparator() {
        return new RecordTimeComparator();
    }

    private static class RecordTimeComparator implements Comparator<RoutingRecord> {
        @Override
        public int compare(RoutingRecord o1, RoutingRecord o2) {
            return o1.timeStart - o2.timeStart;
        }
    }

}
