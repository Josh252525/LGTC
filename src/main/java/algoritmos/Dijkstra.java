package algoritmos;

import estructuras.*;

/**
 * Implementa el algoritmo de Dijkstra para encontrar el camino más corto en un grafo ponderado.
 * Esta clase se utiliza para generar las rutas físicas calle por calle que deben
 * recorrer los camiones para llegar de un punto a otro.
 */
public class Dijkstra {
	
	private Grafo grafo;
	
	/**
	 * Constructor de la clase Dijkstra.
	 * * @param grafo El grafo de la ciudad sobre el cual se calcularán las distancias.
	 */
	public Dijkstra(Grafo grafo) {
		this.grafo = grafo;
	}
	
	/**
	 * Calcula el camino más corto entre un vértice de origen y un vértice de destino.
	 * Utiliza una Cola de Prioridad para optimizar la extracción del vértice más cercano
	 * y un arreglo de predecesores para reconstruir la ruta exacta.
	 * * @param origen  El ID numérico del vértice de salida.
	 * @param destino El ID numérico del vértice de llegada.
	 * @return Una LinkedList de enteros que representa la secuencia exacta de vértices 
	 * por los que debe pasar el camión. Si el destino es inalcanzable, devuelve una lista vacía.
	 */
	public LinkedList<Integer> calcular(int origen, int destino) {
		
		int vertices = grafo.getCantidadVertices();
		
		double[] distancia = new double[vertices];
		boolean[] visitado = new boolean[vertices];
		int[] padre = new int[vertices]; 
		
		for(int i = 0; i < vertices; i++) {
			distancia[i] = Double.MAX_VALUE;
			padre[i] = -1; 
		}
		
		distancia[origen] = 0;
		ColaPrioridad cola = new ColaPrioridad(vertices * vertices);
		cola.insert(origen, 0.0); 
		
		while (!cola.isEmpty()) {
			
			NodoHeap actual = cola.extraerMin();
			int u = actual.vertice;
			
			if (u == destino) {
			    break;
			}
			
			if(visitado[u]) {
				continue;
			}
			visitado[u] = true;
			
			LinkedList<Conexion> vecinos = grafo.getVecinos(u);
			int cantidadVecinos = vecinos.size();
			
			for (int i = 0; i < cantidadVecinos; i++) {
				Conexion conexion = vecinos.getAt(i);
				int v = conexion.destino;
				double pesoArista = conexion.peso;
				
				if (!visitado[v] && distancia[u] + pesoArista < distancia[v]) {
					distancia[v] = distancia[u] + pesoArista;
					padre[v] = u; 
					cola.insert(v, distancia[v]);
				}
			}
		}
		
		LinkedList<Integer> caminoFinal = new LinkedList<>();
		int actual = destino;
		
		if (padre[actual] == -1 && actual != origen) {
		    return caminoFinal; 
		}
		
		while (actual != -1) {
		    caminoFinal.insertAtStart(actual); 
		    actual = padre[actual];
		}
		
		return caminoFinal;
	}
}