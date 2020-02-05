import java.io.*;
import java.util.Random;
import java.util.ArrayList;

public class Graph{
    private int vertex;
    private int[][] matrix;
    private double[][] pheromone;

    public Graph(int n_vertices) {
        this.vertex = n_vertices;
        this.matrix = createMatrix();
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

    public void smoothPheromone() {
        double max = 0.0, min = 1.0, min_pheromone;
        for(var row : this.matrix){
            for(var num : row){
                if(num > max) max = num;
                if(num < min) min = num;
            }
        }

        min_pheromone = max - min;
        for(var row : this.pheromone){
            for(int i = 0; i < row.length; i++){
                if(row[i] > min_pheromone){
                    row[i] = min_pheromone * Math.log(max/min_pheromone);
                }
            }
        }
        System.out.println("Matrix smooth");
    }

    public void showMatrix() {
        for (var row : this.matrix) {
            for (var num : row) {
                System.out.print(num + " ");
            }
            System.out.println();
        }
    }

    public void showPheromone() {
        for (var row : this.pheromone) {
            for (var value : row) {
                System.out.print(value + " ");
            }
            System.out.println();
        }
    }

    public void saveMatrix(String instanceFile){
        try {
            BufferedWriter reader = new BufferedWriter(new FileWriter(instanceFile));
            for (var row : this.matrix) {
                for (var num : row) {
                    reader.write(num + " ");
                }
                reader.newLine();
            }
            reader.flush();
        } catch (IOException e){}
    }

    public void loadMatrix(String instanceFile) throws IOException {
        String line;
        BufferedReader reader = new BufferedReader(new FileReader(instanceFile));
        ArrayList<ArrayList<Integer>> matrix = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            ArrayList<Integer> rowVector = new ArrayList<>();
            for (var value : line.split(" ")) {
                rowVector.add(Integer.valueOf(value));
            }
            matrix.add(rowVector);
        }
        reader.close();

        int[][] newMatrix = new int[matrix.size()][matrix.size()];
        for(int i = 0; i < newMatrix.length; i++){
            for(int j = 0; j < newMatrix.length; j++){
                newMatrix[i][j] = matrix.get(i).get(j);
            }
        }
        this.matrix = newMatrix;
        this.vertex = newMatrix.length;
        this.pheromone = createPheromone();
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
            int v1 = rand.nextInt(this.vertex), v2 = rand.nextInt(this.vertex);
            matrix[v1][v2] = 0;
            matrix[v2][v1] = 0;
        }
        return matrix;
    }

    private double[][] createPheromone() {
        int len = this.vertex;
        double[][] pheromone = new double[len][len];
        double base = 1.0 / len;

        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                pheromone[i][j] = Math.pow(base, 2);
            }
        }
        return pheromone;
    }

    private int n_Choose_k(int n, int k) {
        int numerator = 1, denominator = 1;
        for (int i = n; i >= (n - k + 1); i--) {
            numerator *= i;
        }
        for (int i = k; i >= 1; i--) {
            denominator *= i;
        }
        return numerator / denominator;
    }
}
