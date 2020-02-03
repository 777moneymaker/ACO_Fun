import java.util.Random;

public class Graph {
    private int vertex;
    private int[][] matrix;
    private double[][] pheromone;

    public Graph(int n_vertices) {
        this.vertex = n_vertices;
        this.matrix = createMatrix();
        this.pheromone = createPheromone();
    }

    public int[][] getMatrix() {
        return this.matrix;
    }

    public double[][] getPheromone() {
        return pheromone;
    }

    public int getVertex() {
        return this.vertex;
    }

    public void showMatrix() {
        for (int[] row : this.matrix) {
            for (int num : row) {
                System.out.print(num + " ");
            }
            System.out.println();
        }
    }

    public void showPheromone() {
        for (double[] row : this.pheromone) {
            for (double value : row) {
                System.out.print(value + " ");
            }
            System.out.println();
        }
    }

    private int[][] createMatrix() {
        int[][] matrix = new int[this.vertex][this.vertex];
        Random rand = new Random();
        for (int i = 0; i < this.vertex; i++) {
            for (int j = 0; j < this.vertex; j++) {
                var length = rand.nextInt(99) + 1;
                matrix[i][j] = length;
                matrix[j][i] = length;
            }
        }
        for (int i = 0; i < this.vertex; i++) {
            matrix[i][i] = 0;
        }
        for (int i = 0; i < this.n_Choose_k(this.vertex, 2) / 5; i++) {
            int v1 = rand.nextInt(this.vertex);
            int v2 = rand.nextInt(this.vertex);

            matrix[v1][v2] = 0;
            matrix[v2][v1] = 0;
        }
        return matrix;
    }

    private double[][] createPheromone() {
        int len = this.vertex;
        double[][] pheromone = new double[len][len];
        double base = (1.0 / len);

        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                pheromone[i][j] = Math.pow(base, 2);
            }
        }
        return pheromone;
    }

    private int n_Choose_k(int n, int k) {
        int numerator = 1;
        int denominator = 1;

        for (int i = n; i >= (n - k + 1); i--) {
            numerator *= i;
        }

        for (int i = k; i >= 1; i--) {
            denominator *= i;
        }

        return (numerator / denominator);
    }
}
