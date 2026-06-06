package algoritmos;

import estructuras.*;

public class Recorridos {
	
	private Grafo grafo;
	
	public Recorridos(Grafo grafo) {
		this.grafo = grafo;
	}
	
	public LinkedList<Integer> bfs(int nodoInicial) {
		int cantidadVertices = grafo.getCantidadVertices();
		boolean[] visitado = new boolean[cantidadVertices];
		Cola<Integer> cola = new Cola<>();
		LinkedList<Integer> resultado = new LinkedList<>(); // Guardará el recorrido
		
		visitado[nodoInicial] = true;
		cola.insert(nodoInicial);
		
		while (!cola.isEmpty()) {
			int actual = cola.remove();
			resultado.insert(actual); // Lo agregamos al resultado
			
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
	
	public LinkedList<Integer> dfs(int nodoInicial) {
		int cantidadVertices = grafo.getCantidadVertices();
		boolean[] visitado = new boolean[cantidadVertices];
		Pila<Integer> pila = new Pila<>();
		LinkedList<Integer> resultado = new LinkedList<>(); // Guardará el recorrido
		
		pila.push(nodoInicial);
		
		while (!pila.isEmpty()) {
			int actual = pila.pop();
			
			if (!visitado[actual]) {
				visitado[actual] = true;
				resultado.insert(actual); // Lo agregamos al resultado
				
				LinkedList<Conexion> vecinos = grafo.getVecinos(actual);
				for (int i = 0; i < vecinos.size(); i++) {
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