import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static int noChange, startDistance, bestDistance = (int) Double.POSITIVE_INFINITY;
    private static ArrayList<Integer> bestPath;
    private static boolean startAssign = false;

    public static void main(String[] args) throws IOException {
        Scanner scan = new Scanner(System.in);
        // File path in the CWD, ex. 'Instance10.txt'
        System.out.println("Give a file to load matrix from:");
        String graphFile = scan.next();

        /*
            @graphSize = Unnecessary if graph is loaded from the file. You can set it to 1.0.
            @iteration = Number of iterations.
            @colony = Number of ants. Passed to optimize method.
            @alpha = Pheromone impact on the decision of the ant.
            @beta = Distance impact on the decision of the ant..
            @rho = Percent (%) of the pheromone evaporating after every iteration.
         */
        System.out.println("Give graph size, iterations, colony, alpha, beta, rho:");
        Double graphSize = scan.nextDouble(),
                iteration = scan.nextDouble(),
                colony = scan.nextDouble(),
                alpha = scan.nextDouble(),
                beta = scan.nextDouble(),
                rho = scan.nextDouble();

        Graph graph = new Graph(graphSize.intValue());
        graph.loadMatrix(graphFile);
        Ant.setParameters(graph, alpha, beta, rho);

        for (int i = 0; i < iteration.intValue(); ) {
            System.out.println("Gen no: " + ++i);
            Main.optimize(colony);
        }
        System.out.println("Start result: " + Main.startDistance + " Final result: " + Main.bestDistance);
        System.out.println("Final path: " + Main.bestPath);
    }

    /**
     * Optimization method containing every variable and object necessary for optimization.
     * @param colony = Number of ants.
     */
    private static void optimize(Double colony) {
        boolean changeMade = false;
        if(Main.noChange > 20){
            Ant.getGraph().smoothPheromone();
            Main.noChange = 0;
        }
        ArrayList<Ant> ants = new ArrayList<>(), best_ants = new ArrayList<>();
        for (int i = 0; i < colony.intValue(); i++)
            ants.add(new Ant());

        for (var A : ants) {
            A.travel();
            if(!Main.startAssign){
                Main.startDistance = A.getDistance();
                Main.startAssign = true;
            }
            if (A.getDistance() < Main.bestDistance) {
                Main.bestDistance = A.getDistance();
                Main.bestPath = A.getVisited();
                Main.noChange = 0;
                changeMade = true;
                best_ants.add(A);
                System.out.println("Best cost: " + Main.bestDistance + " Path len: " + Main.bestPath.size());
            }
        }
        if(!changeMade)
            Main.noChange++;
        for (var A : best_ants)
            A.applyPheromone();

        Ant.vaporize();
    }
}
