import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.ArrayList;

public class Ant {
    private Integer previous = null, current, distance = 0;
    private double alpha, beta;
    private ArrayList<Integer> visited = new ArrayList<>();
    private Graph graph;

    public Ant(Graph G, double alpha, double beta) {
        this.graph = G;
        this.current = drawStart();
        this.visited.add(this.current);
        this.alpha = alpha;
        this.beta = beta;
    }

    private int drawStart() {
        return new Random().nextInt(this.graph.getVertex());
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
        int next = -1;
        ArrayList<Integer> allowed = this.generateAllowedMoves();
        ArrayList<ArrayList<Double>> moves = new ArrayList<>(allowed.size());

        for (int i = 0; i < allowed.size(); i++) {
            moves.add(new ArrayList<>());
            moves.get(i).add(allowed.get(i).doubleValue());
            moves.get(i).add(this.calculateProbability(allowed.get(i)));
        }
        Random R = new Random();
        while (next < 0) {
            double prob = R.nextDouble(), cumulativeProbability = 0.0;
            for (var move : moves) {
                cumulativeProbability += move.get(1);
                if (prob <= cumulativeProbability) {
                    next = move.get(0).intValue();
                    break;
                }
            }
        }
        if(this.previous != null && this.graph.getMatrix()[this.previous][this.current] < this.graph.getMatrix()[this.current][next]){
            this.distance -= this.graph.getMatrix()[this.previous][this.current];
            this.distance += this.graph.getMatrix()[this.previous][this.current] * 10;
        }else {
            this.distance += this.graph.getMatrix()[this.current][next];
        }
        this.visited.add(next);
        this.previous = this.current;
        this.current = next;
    }

    private ArrayList<Integer> generateAllowedMoves() {
        ArrayList<Integer> allowed = new ArrayList<>();
        for (int i = 0; i < graph.getVertex(); i++) {
            if (this.graph.getMatrix()[this.current][i] != 0 && !this.visited.contains(i)) {
                allowed.add(i);
            }
        }
        while (allowed.isEmpty()) {
            ArrayList<Integer> temp_visited = (ArrayList<Integer>)this.visited.clone();
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

    private Double calculateProbability(int next) throws ArithmeticException{
        double numerator, denominator = 0;

        numerator = Math.pow(this.graph.getPheromone()[this.current][next], this.alpha) *
                (1 / Math.pow(this.graph.getMatrix()[this.current][next], this.beta));

        for (var move : this.generateAllowedMoves()) {
            denominator += Math.pow(this.graph.getPheromone()[this.current][move], this.alpha) *
                    (1 / Math.pow(this.graph.getMatrix()[this.current][move], this.beta));
        }

        if(denominator == 0.0)
            throw new ArithmeticException("Division by 0!");

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

    public ArrayList<Integer> getVisited() {
        return this.visited;
    }
}
