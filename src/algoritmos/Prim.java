package algoritmos;

import estructuras.*;

/*
 Implementación del algoritmo de Prim para calcular el Árbol de Expansión Mínima (MST) de un grafo no dirigido.
 
 El algoritmo construye el MST seleccionando siempre la arista
 de menor peso que conecta un vértice incluido con uno no incluido.
 
 Complejidad aproximada: O(E log V) debido al uso de una cola de prioridad.
 */


public class Prim {
	
	private Grafo grafo;
	
	/*
     Constructor de Prim. Parametro grafo Grafo no dirigido sobre el cual se calculará el MST.
     */
	
	public Prim(Grafo grafo) {
		this.grafo = grafo;
	}
	
	/*
     Calcula el costo total del Árbol de Expansión Mínima (MST) usando el algoritmo de Prim.
     
     El algoritmo inicia desde el vértice 0 y expande el árbol siempre seleccionando la arista de menor peso disponible.
     
     Retorna el costo total del MST como un valor double.
     */
	
	public double calcMST() {
		
		//Cuantos nodos tiene el grafo.
		int vertices = grafo.getCantidadVertices();
		
		//Cuales vertices ya están dentro o no están adentro del MST.
		boolean[] incluido = new boolean[vertices];
		
		//Igual que en Dijkstra. Sacar menor arista.
		ColaPrioridad cola = new ColaPrioridad(vertices * vertices);
		
		//Se acumula hasta el costo final del MST.
		double CostoTotal = 0;
		
		//Se agrega nodo inicial. Todo nodo inicial tiene una distancia 0 a sí mismo.
		cola.insert(0,0);
		
		while(!cola.isEmpty()) {
			NodoHeap actual = cola.extraerMin();
			
			int VerticeActual = actual.vertice;
			
			//Igual que en Dijkstra. Si encontramos vertex repetido, ignoramos y continuamos.
			if(incluido[VerticeActual]) {
				continue;
			}
			
			incluido[VerticeActual] = true;
			
			CostoTotal += actual.prioridad;
			
			LinkedList<Conexion> vecinos = grafo.getVecinos(VerticeActual);
			
			for(int i = 0; i < vecinos.size(); i++) {
				Conexion conexion = vecinos.getAt(i);
				
				int vecino = conexion.destino;
				 
				
				if(!incluido[vecino]) {
					cola.insert(vecino, conexion.peso);
				}
			}
		}
		
		return CostoTotal;
		
		
	}

}
