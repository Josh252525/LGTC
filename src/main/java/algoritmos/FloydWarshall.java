package algoritmos;

import estructuras.Grafo;
import estructuras.LinkedList;
import estructuras.Conexion;

/*
Algoritmo de Floyd-Warshall para encontrar los caminos mínimos entre todos los pares de nodos.
*/
public class FloydWarshall {

    public static double[][] hacerFloydWarshall(Grafo grafo) {
        
        int n = grafo.getCantidadVertices();
        double[][] distancias = new double[n][n];

        // --- 1. Inicialización Matemática de la Matriz ---
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    distancias[i][j] = 0.0; // La distancia a sí mismo es siempre 0
                } else {
                    distancias[i][j] = Double.POSITIVE_INFINITY; // Si no hay conexión, es infinito
                }
            }
        }

        // --- 2. Inyección de los Pesos Reales del Grafo ---
        for (int i = 0; i < n; i++) {
            LinkedList<Conexion> vecinos = grafo.getVecinos(i);
            for (int j = 0; j < vecinos.size(); j++) {
                Conexion conexion = vecinos.getAt(j);
                distancias[i][conexion.destino] = conexion.peso; 
            }
        }

        // --- 3. El Corazón del Algoritmo (Las Fases de 'k') ---
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    
                    // Solo evaluamos si el nodo intermediario 'k' es alcanzable
                    if (distancias[i][k] != Double.POSITIVE_INFINITY && distancias[k][j] != Double.POSITIVE_INFINITY) {
                        
                        // Si el camino pasando por 'k' es más corto, actualizamos
                        if (distancias[i][j] > distancias[i][k] + distancias[k][j]) {
                            distancias[i][j] = distancias[i][k] + distancias[k][j];
                        }
                    }
                }
            }
        }
        
        return distancias; // Retorna la matriz con todas las distancias mínimas calculadas
    }
}