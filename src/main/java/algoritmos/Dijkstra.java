package algoritmos;

import estructuras.*;


public class Dijkstra {
	
	private Grafo grafo;
	
	public Dijkstra(Grafo grafo) {
		this.grafo = grafo;
	}
	
	// Ahora retorna double[] para respetar las distancias decimales
	public double[] calcular(int origen) {
		
		// 1. Tomamos la cantidad real de vértices desde el método seguro
		int vertices = grafo.getCantidadVertices();
		
		double[] distancia = new double[vertices];
		boolean[] visitado = new boolean[vertices];
		
		// Llenamos las distancias de todos con infinito (Double.MAX_VALUE)
		for(int i = 0; i < vertices; i++) {
			distancia[i] = Double.MAX_VALUE;
		}
		
		// La distancia hacia sí mismo siempre es 0
		distancia[origen] = 0;
		
		ColaPrioridad cola = new ColaPrioridad(vertices * vertices);
		
		// Metemos el nodo inicial a la cola
		cola.insert(origen, 0.0); 
		
		while (!cola.isEmpty()) {
			
			NodoHeap actual = cola.extraerMin();
			int u = actual.vertice;
			
			// Si ya pasamos por aquí de forma óptima, lo saltamos
			if(visitado[u]) {
				continue;
			}
			visitado[u] = true;
			
			// 2. Traemos la lista enlazada personalizada de los vecinos del nodo actual
			LinkedList<Conexion> vecinos = grafo.getVecinos(u);
			
			// Se itera usando los métodos de la LinkedList
			int cantidadVecinos = vecinos.size();
			for (int i = 0; i < cantidadVecinos; i++) {
				
				Conexion conexion = vecinos.getAt(i);
				int v = conexion.destino;
				double pesoArista = conexion.peso;
				
				// Relajación de Dijkstra: Si encontramos un camino más corto, lo actualizamos
				if (!visitado[v] && distancia[u] + pesoArista < distancia[v]) {
					
					distancia[v] = distancia[u] + pesoArista;
					cola.insert(v, distancia[v]); // Metemos a la cola con su nueva prioridad
					
				}
			}
		}
		
		return distancia;
	}
}