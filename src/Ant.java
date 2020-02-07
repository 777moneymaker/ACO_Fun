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

    /**
     * Parameters setting for ACO.
     * @param graph Graph object for ant to access.
     * @param alpha Pheromone impact.
     * @param beta Distance impact.
     * @param rho Vaporize coefficient.
     */
    public static void setParameters(Graph graph, double alpha, double beta, double rho){
        Ant.graph = graph;
        Ant.alpha = alpha;
        Ant.beta = beta;
        Ant.rho = rho;
    }

    /**
     * Travel method. Calls the method for selecting next.
     */
    public void travel() {
        this.visited.trimToSize();
        Set<Integer> unique_visited = new HashSet<>(this.visited);
        long start = System.currentTimeMillis();
        while (unique_visited.size() != Ant.graph.getVertex()) {
            this.selectNext();
            long end = System.currentTimeMillis();
            if((end - start) / 1000.0 > 3.0){
                System.out.println("Ant was travelling too long. Breaking...");
                break;
            }
            unique_visited = new HashSet<>(this.visited);
        }
    }

    /**
     * Method for calculating the next move on the basis of current node.
     */
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

    /**
     * Random choice from possible starting nodes.
     * @return Random starting node.
     */
    private int drawStart() {
        return new Random().nextInt(Ant.graph.getVertex());
    }

    /**
     * Generates allowed moves by accessing adjacent nodes of the current vertex.
     * @return ArrayList of allowed moves.
     */
    private ArrayList<Integer> generateAllowedMoves() {
        ArrayList<Integer> allowed = new ArrayList<>();
        for (int i = 0; i < graph.getVertex(); i++) {
            if (Ant.graph.getMatrix()[this.current][i] != 0 && !this.visited.contains(i)) {
                allowed.add(i);
            }
        }
        while (allowed.isEmpty()) {
            ArrayList<Integer> temp_visited = (ArrayList<Integer>)this.visited.clone();
            for (int i = 0; i < this.visited.size() / 2; i++) {
                temp_visited.remove(0);
                temp_visited.trimToSize();
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

    /**
     * Calculating the probability of going to next possible node.
     * @param next Possible node to get to from current vertex.
     * @return Probability of choosing given node.
     * @throws ArithmeticException Division by zero.
     */
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

    /**
     * Method for applying pheromone after ending the travel.
     */
    public void applyPheromone() {
        for (int i = 0, j = 1; j < this.visited.size(); i++, j++) {
            int x = this.visited.get(i), y = this.visited.get(j);
            Ant.graph.getPheromone()[x][y] += 1.0 / this.distance;
            Ant.graph.getPheromone()[y][x] += 1.0 / this.distance;
        }
    }

    /**
     * Vaporizes a proper amount of pheromones on every edge.
     */
    public static void vaporize(){
        for (int i = 0; i < Ant.graph.getVertex(); i++) {
            for (int j = 0; j < Ant.graph.getVertex(); j++) {
                graph.getPheromone()[i][j] *= 1.0 - Ant.rho;
            }
        }
    }

    /**
     * Get the list of visited nodes.
     * @return ArrayList of visited nodes.
     */
    public ArrayList<Integer> getVisited() {
        return this.visited;
    }

    /**
     * Get the total distance.
     * @return Total distance of the ant path.
     */
    public int getDistance() {
        return this.distance;
    }

    /**
     * Get graph on which ants are making calculations.
     * @return Ant's Graph.
     */
    public static Graph getGraph(){
        return Ant.graph;
    }
}
