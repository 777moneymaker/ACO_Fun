import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

public class Ant {
    private int previous, current, distance;
    private double alpha, beta;
    private Vector<Integer> visited = new Vector<>();
    private Graph graph;

    public Ant(Graph G, double alpha, double beta) {
        this.graph = G;
        this.current = drawStart();
        this.visited.add(this.current);
        this.alpha = alpha;
        this.beta = beta;
    }

    private int drawStart() {
        Random rand = new Random();
        return rand.nextInt(this.graph.getVertex());
    }


    public void travel() {
        this.visited.trimToSize();
        Set<Integer> unique_visited = new HashSet<>(this.visited);
        while (unique_visited.size() != this.graph.getVertex()) {
            this.selectNext();
            unique_visited = new HashSet<>(this.visited);
        }
    }

    private void selectNext() {
        Random R = new Random();
        int next = -1;
        var allowed = this.generateAllowedMoves();
        Vector<Vector<Double>> moves = new Vector<>();

        for (int i = 0; i < allowed.size(); i++) {
            moves.add(new Vector<Double>());
            moves.get(i).add(allowed.get(i).doubleValue());
            moves.get(i).add(this.calculateProbability(allowed.get(i)));
        }
        while (next == -1) {
            double prob = R.nextDouble();
            for (var move : moves) {
                if (moves.size() == 1) {
                    next = move.get(0).intValue();
                    break;
                } else if (move.get(1) <= prob) {
                    next = move.get(0).intValue();
                }
            }
        }

        this.previous = this.current;
        this.distance += this.graph.getMatrix()[this.current][next];
        this.current = next;
        this.visited.add(current);
    }

    private Vector<Integer> generateAllowedMoves() {
        Vector<Integer> allowed = new Vector<>();
        for (int i = 0; i < graph.getVertex(); i++) {
            if (this.graph.getMatrix()[this.current][i] != 0 && !this.visited.contains(i)) {
                allowed.add(i);
            }
        }
        while (allowed.isEmpty()) {
            Vector<Integer> temp_visited = (Vector<Integer>) this.visited.clone();
            Random R = new Random();
            for (int i = 0; i < this.visited.size() / 4; i++) {
                temp_visited.remove(R.nextInt(temp_visited.size()));
            }
            for (int i = 0; i < this.graph.getVertex(); i++) {
                if (this.graph.getMatrix()[this.current][i] != 0 && !temp_visited.contains(i)) {
                    allowed.add(i);
                }
            }
            allowed.trimToSize();
        }
        return allowed;
    }

    private Double calculateProbability(int next) {
        double numerator, denominator = 0;

        numerator = Math.pow(this.graph.getPheromone()[this.current][next], this.alpha) *
                (1 / Math.pow(this.graph.getMatrix()[this.current][next], this.beta));

        for (var move : this.generateAllowedMoves()) {
            denominator += Math.pow(this.graph.getPheromone()[this.current][move], this.alpha) *
                    (1 / Math.pow(this.graph.getMatrix()[this.current][move], this.beta));
        }

        return numerator / denominator;
    }

    public void applyPheromone() {
        for (int i = 0, j = 1; j < this.visited.size(); i++, j++) {
            this.graph.getPheromone()[this.visited.get(i)][this.visited.get(j)] += 1.0 / this.distance;
        }
    }

    public int getDistance() {
        return this.distance;
    }

    public Vector<Integer> getVisited() {
        return this.visited;
    }

}
