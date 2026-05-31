package estructuras;

/*
Documentación
Cosa que no vimos en clase usada: Integer.MAX_VALUE.
Información sacada de: CodeGym, por Artem Diverttito.

Integer.MAX_VALUE es un número en la clase Java Integer del paquete java.lang. 
Es el número entero máximo posible que se puede representar en 32 bits. 
Su valor exacto es 2147483647 es decir 231-1. 

Se usa para:
Se utiliza para asignar automáticamente a cualquier variable el máximo número entero posible 
sin necesidad de recordar el número exacto. En este caso, representará al infinito en la distancia.
Ergo, las distancias que no conocemos aún.
 */

public class Dijkstra {
	
	private Grafo grafo;
	
	public Dijkstra(Grafo grafo) {
		this.grafo = grafo;
	}
	
	public int[] calcular(int origen) {
		
		int vertices = grafo.getListaAdyacencia().size();
		//Recordar que se hace una lista por cada uno de los vertices. 
		
		int[] distancia = new int[vertices];
		boolean[] visitado = new boolean[vertices];
		
		for(int i = 0; i < vertices; i++) {
			distancia[i] = Integer.MAX_VALUE;
		}
		
		distancia[origen] = 0;
		//El paso inicial de siempre. Distancia de nodo inicial a sí mismo es 0.
		
		ColaPrioridad cola = new ColaPrioridad(vertices * vertices);
		/*
		Multiplico la cantidad de vertices por sí mismo porque sino, la capacidad queda igual 
		a la cantidad de vertices. El problema? Dijkstra puede insertar el mismo vértice varias
		veces con prioridades distintas. 
		Ej:
		0 --> 2 (10)
		0 --> 1 (5)
		2 --> 1 (8)
		
		Vertice 1 podría entrar una vez con prioridad 5, otra con 8. El heap se puede llenar. 
		Por eso aumentamos la capacidad de la cola. Mejor que sobre a que falte.
		 */
		
		//Del nodo inicial a sí mismo hay peso 0.
		cola.insert(origen, 0);
		
		while (!cola.isEmpty()) {
			
			NodoHeap actual = cola.extraerMin();
			
			int verticeActual = actual.vertice;
			
			//Si ya lo visitamos, solo sigue el programa. 
			if(visitado[verticeActual]) {
				continue;
			}
			
			visitado[verticeActual] = true;
			
			//OJO A ESTA PARTE: LOS VECINOS.
			//Para cada conexion VECINA del vertice actual. Es un for each. Recorre la lista.
			for(Conexion conexion : grafo.getListaAdyacencia().get(verticeActual)) {
				int vecino = conexion.destino;
				
				//(int) vuelve al peso un interger porque lo puse como double en Grafo, cambiar después.
				int peso = (int) conexion.peso;
				
				int nuevaDistancia = distancia[verticeActual] + peso;
				
				if (nuevaDistancia < distancia[vecino]) {
					distancia[vecino] = nuevaDistancia;
					cola.insert(vecino, nuevaDistancia);
				}
			}
			
			
			
		}
		
		return distancia;
		
	}

}
