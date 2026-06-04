package estructuras;

public class FloydWarshall {

    //Entra matriz y sale la

    public static int[][] hacerFloydWarshall(int[][] matrix) {
        //Mi nombre es Floyd, y yo soy warshall! Y juntos te traemos...!
        int n = matrix.length;

        int[][] result = matrix;

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (result[i][j] > result[i][k] + result[k][j]) {
                        System.out.println("Se cambio "+result[i][j]+" por "+result[k][j]);
                        result[i][j] = result[i][k] + result[k][j];
                    }
                }
            }
        }
        return result;
    }
}
