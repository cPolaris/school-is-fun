import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;


class Router {

    Collection<Rail>[] adj;      // adjacency list
    public final int numVertices;
    private double[] distanceToSource;
    private int[] vertexTo;
    private Map<Integer, Station> stations;


    @SuppressWarnings("unchecked")
    public Router(int num_v, Map<Integer, Station> stationMap) {
        numVertices = num_v;
        stations = stationMap;
        adj = (Collection<Rail>[]) new Collection<?>[num_v];
        for (int i = 0; i < num_v; i++) {
            adj[i] = new LinkedList<>();
        }
    }


    /**
     * @return list of station [from, step2, step3 ... destination]
     */
    public List<Station> shortest(int from, int to) {
        LinkedList<Station> shortestPath = new LinkedList<>();

        dijkstra(from);

        for (int i = to; i != from; i = vertexTo[i]) {
            shortestPath.addFirst(stations.get(i));
        }
        shortestPath.addFirst(stations.get(from));

        return shortestPath;
    }


    private void dijkstra(int source) {
        distanceToSource = new double[numVertices];
        Arrays.fill(distanceToSource, Double.POSITIVE_INFINITY);
        distanceToSource[source] = 0.0;

        vertexTo = new int[numVertices];
        Arrays.fill(vertexTo, -1);

        PriorityQueue<Integer> pq = new PriorityQueue<>(new DijkstraVertexIndexComparator());

        pq.add(source);

        while (!pq.isEmpty()) {
            int curr = pq.remove();
            for (Rail r : adj[curr]) {
                int other = r.other(curr);
                double newDistance = distanceToSource[curr] + r.railLen;
                if (newDistance < distanceToSource[other]) {
                    distanceToSource[other] = newDistance;
                    vertexTo[other] = curr;
                    pq.add(other);
                }
            }
        }
    }

    public void addEdge(int a, int b, double weight) {
        int kore =     a < b ? a : b;  // assign kore the lower index
        int sore = kore == a ? b : a;  // and sore the higher one

        if (!neighbors(a, b)) {
            Rail newRail = new Rail(kore, sore, weight);
            adj[a].add(newRail);
            adj[b].add(newRail);
        }
    }


    /**
     * @return the index of the vertex that has max degree
     */
    public int maxDegreeVertexIndex() {
        int maxInd = -1;
        int maxDeg = -1;
        for (int i = 0; i < adj.length; i++) {
            int currSize = adj[i].size();
            if (currSize > maxDeg) {
                maxDeg = currSize;
                maxInd = i;
            }
        }
        return maxInd;
    }


    public Map<Integer, Rail> getAdjMap(int stationInd) {
        Map<Integer, Rail> adjMap = new HashMap<>(adj[stationInd].size());
        for (Rail r : adj[stationInd]) {
            adjMap.put(r.other(stationInd), r);
        }
        return adjMap;
    }


    public boolean neighbors(int a, int b) {
        for (Rail r : adj[a]) {
            if (r.other(a) == b) return true;
        }
        return false;
    }


    /**
     * Distance between two adjacent stations
     */
    public double distanceAdj(int a, int b) {
        for (Rail e : adj[a]) {
            if (e.other(a) == b) return e.railLen;
        }
        throw new NoSuchElementException(String.format("No edge between %d and %d", a, b));
    }


    /**
     * Length of a path.
     * @param p the path
     * @return railLen of the path
     */
    public double pathTotalLength(List<Station> p) {
        double totalDistance = 0.0;
        for (int i = 1; i < p.size(); i++) {
            totalDistance += distanceAdj(p.get(i-1).index, p.get(i).index);
        }
        return totalDistance;
    }


//    public List<Station> reroute(int from, int to, int ... exclude) {
//        return null;
//    }


    private class DijkstraVertexIndexComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            return Double.compare(distanceToSource[o1], distanceToSource[o2]);
        }
    }
}
