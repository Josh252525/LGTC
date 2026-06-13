package algoritmos;

import estructuras.*;

/**
 * Provee los algoritmos clásicos de recorrido y exploración sistemática de grafos.
 * Incluye las implementaciones iterativas de Búsqueda en Anchura (BFS) y 
 * Búsqueda en Profundidad (DFS), garantizando que no existan desbordamientos de memoria (StackOverflow).
 */
public class Recorridos {
	
	private Grafo grafo;
	
	/**
	 * Constructor de la clase Recorridos.
	 * * @param grafo El grafo sobre el cual se ejecutarán las búsquedas.
	 * @throws IllegalArgumentException si el grafo inyectado es nulo.
	 */
	public Recorridos(Grafo grafo) {
		
		// Validación de seguridad contra grafos nulos
		if (grafo == null) {
			throw new IllegalArgumentException("Error Crítico: El grafo proporcionado a Recorridos no puede ser nulo.");
		}
		this.grafo = grafo;
	}
	
	/**
	 * Ejecuta una Búsqueda en Anchura (Breadth-First Search) partiendo de un nodo específico.
	 * Explora a los vecinos inmediatos (nivel 1) antes de profundizar a los siguientes niveles
	 * utilizando un TDA de tipo Cola (Queue).
	 * * @param nodoInicial El ID numérico del vértice donde comenzará el recorrido.
	 * @return Una LinkedList que contiene los IDs de los vértices en el orden exacto en que fueron visitados.
	 * @throws IllegalArgumentException si el nodo inicial no pertenece al dominio de los vértices.
	 */
	public LinkedList<Integer> bfs(int nodoInicial) {
		int cantidadVertices = grafo.getCantidadVertices();
		
		if (cantidadVertices == 0) {
			return new LinkedList<>(); 
		}
		if (nodoInicial < 0 || nodoInicial >= cantidadVertices) {
			throw new IllegalArgumentException("Error: El nodo inicial " + nodoInicial + " no existe en el grafo.");
		}

		boolean[] visitado = new boolean[cantidadVertices];
		Cola<Integer> cola = new Cola<>();
		LinkedList<Integer> resultado = new LinkedList<>(); 
		
		visitado[nodoInicial] = true;
		cola.insert(nodoInicial);
		
		while (!cola.isEmpty()) {
			int actual = cola.remove();
			resultado.insert(actual); 
			
			LinkedList<Conexion> vecinos = grafo.getVecinos(actual);
			for (int i = 0; i < vecinos.size(); i++) {
				int destino = vecinos.getAt(i).destino;
				if (!visitado[destino]) {
					visitado[destino] = true;
					cola.insert(destino);
				}
			}
		}
		return resultado;
	}
	
	/**
	 * Ejecuta una Búsqueda en Profundidad (Depth-First Search) partiendo de un nodo específico.
	 * Explora cada rama de conexión lo más lejos posible antes de retroceder (backtracking),
	 * simulando la recursividad a través de un TDA de tipo Pila (Stack).
	 * * @param nodoInicial El ID numérico del vértice donde comenzará el recorrido.
	 * @return Una LinkedList que contiene los IDs de los vértices en el orden en que fueron visitados.
	 * @throws IllegalArgumentException si el nodo inicial no pertenece al dominio de los vértices.
	 */
	public LinkedList<Integer> dfs(int nodoInicial) {
		int cantidadVertices = grafo.getCantidadVertices();
		
		if (cantidadVertices == 0) {
			return new LinkedList<>(); 
		}
		if (nodoInicial < 0 || nodoInicial >= cantidadVertices) {
			throw new IllegalArgumentException("Error: El nodo inicial " + nodoInicial + " no existe en el grafo.");
		}

		boolean[] visitado = new boolean[cantidadVertices];
		Pila<Integer> pila = new Pila<>();
		LinkedList<Integer> resultado = new LinkedList<>(); 
		
		pila.push(nodoInicial);
		
		while (!pila.isEmpty()) {
			int actual = pila.pop();
			
			if (!visitado[actual]) {
				visitado[actual] = true;
				resultado.insert(actual); 
				
				LinkedList<Conexion> vecinos = grafo.getVecinos(actual);
				for (int i = vecinos.size() - 1; i >= 0; i--) {
					int destino = vecinos.getAt(i).destino;
					if (!visitado[destino]) {
						pila.push(destino);
					}
				}
			}
		}
		return resultado;
	}
}