import java.awt.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.PriorityQueue;
import java.util.Random;

class Playground {
    public static void main(String[] args) {
        stats();
    }


    private static void pq() {
        PriorityQueue<Integer> q = new PriorityQueue<>();
        q.add(1);
        q.add(66);
        q.add(4);
        q.add(23);
        q.add(17);
        q.add(46);

        while (!q.isEmpty()) {
            System.out.println(q.remove());
        }
    }

    private static void stats() {
        int size = 2000;
        double[] a = new double[size];
        for (int i = 0; i < size; i++) {
            a[i] = normal(90.0, 26.0);
        }

        double minA = StdStats.min(a);
        System.out.printf("       min %10.3f\n", minA);
        System.out.printf("      mean %10.3f\n", StdStats.mean(a));
        System.out.printf("       max %10.3f\n", StdStats.max(a));
        System.out.printf("       sum %10.3f\n", StdStats.sum(a));
        System.out.printf("    stddev %10.3f\n", StdStats.stddev(a));
        System.out.printf("       var %10.3f\n", StdStats.var(a));
        System.out.printf("   stddevp %10.3f\n", StdStats.stddevp(a));
        System.out.printf("      varp %10.3f\n", StdStats.varp(a));
    }

    private static double normal(double avg, double stdev) {
        Random rrr = new Random();
        double candidate = rrr.nextGaussian() * stdev + avg;
        while  (candidate < avg - 3 * stdev || candidate > avg + 3 * stdev) {
            candidate = rrr.nextGaussian() * stdev + avg;
        }
        return candidate;
    }
}
