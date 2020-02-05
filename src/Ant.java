import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.ArrayList;

public class Ant {
    private static Graph graph;
    private static double alpha, beta, rho;

    private Integer previous = null, current, distance = 0;
    private ArrayList<Integer> visited = new ArrayList<>();

    public Ant() {
        this.current = drawStart();
        this.visited.add(this.current);
    }

    private int drawStart() {
        return new Random().nextInt(Ant.graph.getVertex());
    }

    public static void setParameters(Graph graph, double alpha, double beta, double rho){
        Ant.graph = graph;
        Ant.alpha = alpha;
        Ant.beta = beta;
        Ant.rho = rho;
    }

    public static Graph getGraph(){
        return Ant.graph;
    }


    public void travel() {
        this.visited.trimToSize();
        Set<Integer> unique_visited = new HashSet<>(this.visited);
        while (unique_visited.size() != Ant.graph.getVertex()) {
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
        if(this.previous != null && Ant.graph.getMatrix()[this.previous][this.current] < Ant.graph.getMatrix()[this.current][next]){
            this.distance -= Ant.graph.getMatrix()[this.previous][this.current];
            this.distance += Ant.graph.getMatrix()[this.previous][this.current] * 10;
        }else {
            this.distance += Ant.graph.getMatrix()[this.current][next];
        }
        this.visited.add(next);
        this.previous = this.current;
        this.current = next;
    }

    public int getDistance() {
        return this.distance;
    }

    public ArrayList<Integer> getVisited() {
        return this.visited;
    }

    private ArrayList<Integer> generateAllowedMoves() {
        ArrayList<Integer> allowed = new ArrayList<>();
        for (int i = 0; i < graph.getVertex(); i++) {
            if (Ant.graph.getMatrix()[this.current][i] != 0 && !this.visited.contains(i)) {
                allowed.add(i);
            }
        }
        while (allowed.isEmpty()) {
            ArrayList<Integer> temp_visited = (ArrayList<Integer>)this.visited.clone();
            Random R = new Random();
            for (int i = 0; i < this.visited.size() / 4; i++) {
                temp_visited.remove(R.nextInt(temp_visited.size()));
            }
            for (int i = 0; i < Ant.graph.getVertex(); i++) {
                if (Ant.graph.getMatrix()[this.current][i] != 0 && !temp_visited.contains(i)) {
                    allowed.add(i);
                }
            }
            allowed.trimToSize();
        }
        return allowed;
    }

    private Double calculateProbability(int next) throws ArithmeticException{
        double numerator, denominator = 0;

        numerator = Math.pow(Ant.graph.getPheromone()[this.current][next], Ant.alpha) *
                (1 / Math.pow(Ant.graph.getMatrix()[this.current][next], Ant.beta));

        for (var move : this.generateAllowedMoves()) {
            denominator += Math.pow(Ant.graph.getPheromone()[this.current][move], Ant.alpha) *
                    (1 / Math.pow(Ant.graph.getMatrix()[this.current][move], Ant.beta));
        }

        if(denominator == 0.0)
            throw new ArithmeticException("Division by 0!");

        return numerator / denominator;
    }

    public void applyPheromone() {
        for (int i = 0, j = 1; j < this.visited.size(); i++, j++) {
            Ant.graph.getPheromone()[this.visited.get(i)][this.visited.get(j)] += 1.0 / this.distance;
            Ant.graph.getPheromone()[this.visited.get(j)][this.visited.get(i)] += 1.0 / this.distance;
        }
    }

    public static void vaporize(){
        for (int i = 0; i < Ant.graph.getVertex(); i++) {
            for (int j = 0; j < Ant.graph.getVertex(); j++) {
                graph.getPheromone()[i][j] *= 1.0 - Ant.rho;
            }
        }
    }
}
