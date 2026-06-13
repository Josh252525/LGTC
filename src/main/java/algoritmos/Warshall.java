package algoritmos;

import estructuras.*;

/**
 * Implementa el algoritmo booleano de Warshall para determinar la clausura transitiva
 * de un grafo dirigido o no dirigido.
 * <p>
 * Permite responder rápidamente a la pregunta lógica: "¿Existe algún camino posible
 * (directo o con escalas) para llegar del nodo A al nodo B?"
 * </p>
 */
public class Warshall {
	
	public boolean[][] alcanzable;
	
	/**
	 * Constructor de Warshall. Al instanciarse, procesa toda la matriz de adyacencia
	 * aplicando la evaluación iterativa de nodos intermediarios en complejidad O(V^3).
	 * * @param grafo El grafo de la ciudad cuyas rutas de alcance serán evaluadas.
	 */
	public Warshall(Grafo grafo) {
		
		int n = grafo.getCantidadVertices();
		alcanzable = new boolean[n][n];
		
		for(int a = 0; a < n; a++) {
			alcanzable[a][a] = true;
		}
			
		for(int p = 0; p < n; p++) {
		    LinkedList<Conexion> vecinos = grafo.getVecinos(p);
		    int cantidadVecinos = vecinos.size();
		    
			for(int i = 0; i < cantidadVecinos; i++) {
			    Conexion conexion = vecinos.getAt(i);
				alcanzable[p][conexion.destino] = true;
			}
		}
				
		for(int k = 0; k < n; k++) {
			for(int i = 0; i < n; i++) {
				for(int j = 0; j < n; j++) {
					alcanzable[i][j] = alcanzable[i][j] || (alcanzable[i][k] && alcanzable[k][j]);
				}
			}
		}	
	}
	
	/**
	 * Verifica si un vértice de destino es alcanzable desde un vértice de origen.
	 * * @param origen  El ID numérico del vértice de partida.
	 * @param destino El ID numérico del vértice al que se desea llegar.
	 * @return true si existe al menos una ruta posible; false en caso contrario.
	 */
	public boolean esAlcanzable(int origen, int destino) {
		return alcanzable[origen][destino];
	}
}