import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static int noChange, startDistance, bestDistance = (int) Double.POSITIVE_INFINITY;
    private static boolean startAssign = false;

    public static void main(String[] args) throws IOException {
        Scanner scan = new Scanner(System.in);
        System.out.println("Give a file to load matrix from:");
        String graphFile = scan.next();
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
    }

    private static void optimize(Double colony) {
        boolean changeMade = false;
        if(Main.noChange > 20){
            Ant.getGraph().smoothPheromone();
            Main.noChange = 0;
        }
        ArrayList<Ant> ants = new ArrayList<>(), best_ants = new ArrayList<>();
        for (int i = 0; i < colony.intValue(); i++) {
            ants.add(new Ant());
        }
        for (var A : ants) {
            A.travel();
            if(!Main.startAssign){
                Main.startDistance = A.getDistance();
                Main.startAssign = true;
            }
            if (A.getDistance() < Main.bestDistance) {
                Main.bestDistance = A.getDistance();
                Main.noChange = 0;
                changeMade = true;
                best_ants.add(A);
                System.out.println("Best cost: " + Main.bestDistance);
            }
        }
        if(!changeMade) Main.noChange++;
        for (var A : best_ants) {
            A.applyPheromone();
        }
    }
}
