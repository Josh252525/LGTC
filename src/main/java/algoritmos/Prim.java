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
		
		if (vertices == 0) {
			return 0.0;
		}
		
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
	/*
     * Añadido para el Enrutador MST
     * Construye y devuelve físicamente el Árbol de Expansión Mínima como un objeto Grafo.
     */
    public Grafo obtenerGrafoMST() {
        int vertices = grafo.getCantidadVertices();
        // --- CASOS EXTREMOS ---
        
        // 1. Si el grafo no tiene vértices, devolvemos un grafo vacío sin hacer cálculos.
        if (vertices == 0) {
            return new Grafo(0);
        }
        // 2. Si el grafo tiene solo 1 vértice, devolvemos un grafo de 1 nodo sin aristas.
        if (vertices == 1) {
            return new Grafo(1);
        }
        
        
        Grafo arbolMST = new Grafo(vertices); // El grafo vacío que vamos a llenar
        
        boolean[] incluido = new boolean[vertices];
        double[] costoMinimo = new double[vertices];
        int[] padre = new int[vertices]; // ¡Clave! Recuerda quién conectó a quién
        
        // Inicializamos arreglos
        for (int i = 0; i < vertices; i++) {
            costoMinimo[i] = Double.MAX_VALUE;
            padre[i] = -1;
        }
        
        ColaPrioridad cola = new ColaPrioridad(vertices * vertices);
        
        // Empezamos en el nodo 0
        costoMinimo[0] = 0.0;
        cola.insert(0, 0.0);
        
        while (!cola.isEmpty()) {
            NodoHeap actual = cola.extraerMin();
            int u = actual.vertice;
            
            if (incluido[u]) {
                continue;
            }
            incluido[u] = true;
            
            // Si el nodo actual tiene un padre válido, agregamos la arista a nuestro árbol
            if (padre[u] != -1) {
                arbolMST.agregarArista(padre[u], u, costoMinimo[u]);
                arbolMST.agregarArista(u, padre[u], costoMinimo[u]); // Es bidireccional
            }
            
            // Revisamos vecinos
            LinkedList<Conexion> vecinos = grafo.getVecinos(u);
            for (int i = 0; i < vecinos.size(); i++) {
                Conexion c = vecinos.getAt(i);
                int v = c.destino;
                double peso = c.peso;
                
                // Si el vecino no está en el árbol y encontramos un camino más barato hacia él
                if (!incluido[v] && peso < costoMinimo[v]) {
                    costoMinimo[v] = peso;
                    padre[v] = u; // Guardamos que 'u' descubrió a 'v'
                    cola.insert(v, peso);
                }
            }
        }
        
        return arbolMST; // ¡Devolvemos la estructura completa!
    }

}
