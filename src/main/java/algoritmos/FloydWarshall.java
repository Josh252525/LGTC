package algoritmos;

import estructuras.Grafo;
import estructuras.LinkedList;
import estructuras.Conexion;

/**
 * Implementa el algoritmo de Floyd-Warshall para encontrar los caminos mínimos 
 * entre todos los pares de nodos de un grafo dirigido o no dirigido.
 */
public class FloydWarshall {

    /**
     * Calcula una matriz bidimensional con las distancias más cortas entre cualquier
     * par de vértices en el grafo, permitiendo evaluaciones de ruteo global (O(V^3)).
     * * @param grafo El grafo de la ciudad a evaluar.
     * @return Una matriz de tipo double[][] donde el valor en la posición [i][j] 
     * representa la distancia mínima en kilómetros desde el vértice i hasta el vértice j.
     * Si no existe camino, el valor será Double.POSITIVE_INFINITY.
     */
    public static double[][] hacerFloydWarshall(Grafo grafo) {
        
        int n = grafo.getCantidadVertices();
        double[][] distancias = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    distancias[i][j] = 0.0; 
                } else {
                    distancias[i][j] = Double.POSITIVE_INFINITY; 
                }
            }
        }

        for (int i = 0; i < n; i++) {
            LinkedList<Conexion> vecinos = grafo.getVecinos(i);
            for (int j = 0; j < vecinos.size(); j++) {
                Conexion conexion = vecinos.getAt(j);
                distancias[i][conexion.destino] = conexion.peso; 
            }
        }

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    
                    if (distancias[i][k] != Double.POSITIVE_INFINITY && distancias[k][j] != Double.POSITIVE_INFINITY) {
                        if (distancias[i][j] > distancias[i][k] + distancias[k][j]) {
                            distancias[i][j] = distancias[i][k] + distancias[k][j];
                        }
                    }
                }
            }
        }

        return distancias;
    }
}