package estructuras;

//Clase encargada de realizar los recorridos estándar sobre el Grafo.

public class Recorridos {
	
	private Grafo grafo;
	
	public Recorridos(Grafo grafo) {
		this.grafo = grafo;
	}
	
	// --- BFS: BÚSQUEDA EN AMPLITUD ---
	public void bfs(int nodoInicial) {
		int cantidadVertices = grafo.getCantidadVertices();
		boolean[] visitado = new boolean[cantidadVertices];
		
		// Usamos nuestra propia Cola genérica
		Cola<Integer> cola = new Cola<>();
		
		visitado[nodoInicial] = true;
		cola.insert(nodoInicial);
		
		System.out.print("Recorrido BFS desde intersección " + nodoInicial + ": ");
		
		while (!cola.isEmpty()) {
			// Sacamos al primero de la fila
			int actual = cola.remove();
			System.out.print(actual + " ");
			
			// Obtenemos los vecinos usando nuestra LinkedList
			LinkedList<Conexion> vecinos = grafo.getVecinos(actual);
			int cantidadVecinos = vecinos.size();
			
			// Iteramos con un for clásico
			for (int i = 0; i < cantidadVecinos; i++) {
				Conexion conexion = vecinos.getAt(i);
				int destino = conexion.destino;
				
				// Si no hemos visitado a este vecino, lo marcamos y lo encolamos
				if (!visitado[destino]) {
					visitado[destino] = true;
					cola.insert(destino);
				}
			}
		}
		System.out.println(); // Salto de línea final
	}
	
	// --- DFS: BÚSQUEDA EN PROFUNDIDAD (Iterativo) ---
	public void dfs(int nodoInicial) {
		int cantidadVertices = grafo.getCantidadVertices();
		boolean[] visitado = new boolean[cantidadVertices];
		
		// Usamos nuestra propia Pila genérica
		Pila<Integer> pila = new Pila<>();
		
		pila.push(nodoInicial);
		
		System.out.print("Recorrido DFS desde intersección " + nodoInicial + ": ");
		
		while (!pila.isEmpty()) {
			// Sacamos al que está en el tope de la pila
			int actual = pila.pop();
			
			// En el DFS con Pila, verificamos si fue visitado justo al sacarlo
			if (!visitado[actual]) {
				visitado[actual] = true;
				System.out.print(actual + " ");
				
				// Buscamos a sus vecinos
				LinkedList<Conexion> vecinos = grafo.getVecinos(actual);
				int cantidadVecinos = vecinos.size();
				
				// Metemos a todos los vecinos no visitados a la pila
				for (int i = 0; i < cantidadVecinos; i++) {
					Conexion conexion = vecinos.getAt(i);
					int destino = conexion.destino;
					
					if (!visitado[destino]) {
						pila.push(destino);
					}
				}
			}
		}
		System.out.println(); // Salto de línea final
	}
}