import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static Graph G = new Graph(50);
    private static int bestDistance = (int) Double.POSITIVE_INFINITY;

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Give colony, alpha, beta, rho: ");
        double colony = scan.nextDouble(), alpha = scan.nextDouble(), beta = scan.nextDouble(), rho = scan.nextDouble();
        for (int i = 0; i < 50; i++) {
            System.out.println("Gen no: " + i);
            Main.optimize(colony, alpha, beta, rho);
        }
    }

    private static void optimize(double colony, double alpha, double beta, double rho) {
        ArrayList<Ant> ants = new ArrayList<>();
        ArrayList<Ant> best_ants = new ArrayList<>();
        for (int i = 0; i < (int) colony; i++) {
            ants.add(new Ant(Main.G, alpha, beta));
        }
        for (var A : ants) {
            A.travel();
            if (A.getDistance() < Main.bestDistance) {
                Main.bestDistance = A.getDistance();
                System.out.println(Main.bestDistance);
                best_ants.add(A);
            }
        }
        for (var A : best_ants) {
            A.applyPheromone();
        }
        for (int i = 0; i < Main.G.getVertex(); i++) {
            for (int j = 0; j < Main.G.getVertex(); j++) {
                Main.G.getPheromone()[i][j] *= rho;
            }
        }
    }
}
