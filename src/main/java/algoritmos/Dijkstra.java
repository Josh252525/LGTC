package algoritmos;

import estructuras.*;

public class Dijkstra {
	
	private Grafo grafo;
	
	public Dijkstra(Grafo grafo) {
		this.grafo = grafo;
	}
	
	/**
	 * Calcula el camino más corto entre un origen y un destino específico.
	 * Utiliza un arreglo de 'padres' para reconstruir la ruta calle por calle.
	 */
	public LinkedList<Integer> calcular(int origen, int destino) {
		
		int vertices = grafo.getCantidadVertices();
		
		double[] distancia = new double[vertices];
		boolean[] visitado = new boolean[vertices];
		int[] padre = new int[vertices]; // ¡CLAVE! Aquí guardamos las "migas de pan"
		
		// Llenamos las distancias con infinito y los padres con -1
		for(int i = 0; i < vertices; i++) {
			distancia[i] = Double.MAX_VALUE;
			padre[i] = -1; // -1 significa que no tiene predecesor aún
		}
		
		distancia[origen] = 0;
		ColaPrioridad cola = new ColaPrioridad(vertices * vertices);
		cola.insert(origen, 0.0); 
		
		while (!cola.isEmpty()) {
			
			NodoHeap actual = cola.extraerMin();
			int u = actual.vertice;
			
			// Optimización: Si llegamos al destino que buscábamos, podemos detener la búsqueda
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
				
				// Relajación de aristas
				if (!visitado[v] && distancia[u] + pesoArista < distancia[v]) {
					distancia[v] = distancia[u] + pesoArista;
					padre[v] = u; // ¡Guardamos que llegamos a 'v' pasando por 'u'!
					cola.insert(v, distancia[v]);
				}
			}
		}
		
		// --- FASE 2: RECONSTRUCCIÓN DEL CAMINO ---
		LinkedList<Integer> caminoFinal = new LinkedList<>();
		int actual = destino;
		
		// Si el destino no tiene padre y no es el origen, significa que es inalcanzable
		if (padre[actual] == -1 && actual != origen) {
		    return caminoFinal; // Retornamos lista vacía
		}
		
		// Retrocedemos desde el destino hasta el origen usando las migas de pan
		while (actual != -1) {
		    // Usamos el método de tu compañero para insertar al inicio y que la ruta quede al derecho
		    caminoFinal.insertAtStart(actual); 
		    actual = padre[actual];
		}
		
		return caminoFinal;
	}
}