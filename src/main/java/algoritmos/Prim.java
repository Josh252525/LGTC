package algoritmos;

import estructuras.*;

/**
 * Implementación del algoritmo de Prim para calcular el Árbol de Expansión Mínima (MST) 
 * de un grafo ponderado no dirigido.
 * <p>
 * El algoritmo crece el árbol en etapas sucesivas, seleccionando siempre la arista 
 * de menor costo disponible que conecte un nodo visitado con uno no visitado,
 * garantizando una complejidad temporal óptima mediante el uso de una Cola de Prioridad.
 * </p>
 */
public class Prim {
	
	private Grafo grafo;
	
	/**
     * Constructor de la clase Prim.
     * * @param grafo El grafo no dirigido sobre el cual se calculará el MST.
     */
	public Prim(Grafo grafo) {
		this.grafo = grafo;
	}
	
	/**
     * Construye el Árbol de Expansión Mínima y retorna la sumatoria de sus pesos.
     * Inicia de manera predeterminada en el vértice 0 (Depósito).
     * * @return El costo total (distancia en km) de todas las aristas que conforman el MST.
     * Si el grafo está vacío, retorna 0.0.
     */
	public double calcMST() {
		
		int vertices = grafo.getCantidadVertices();
		
		if (vertices == 0) {
			return 0.0;
		}
		
		boolean[] incluido = new boolean[vertices];
		double CostoTotal = 0;
        
        Grafo arbolMST = new Grafo(vertices);
        
        double[] costoMinimo = new double[vertices];
        int[] padre = new int[vertices];
        
        for (int i = 0; i < vertices; i++) {
            costoMinimo[i] = Double.MAX_VALUE;
            padre[i] = -1;
        }
        
        ColaPrioridad cola = new ColaPrioridad(vertices * vertices);
        
        costoMinimo[0] = 0.0;
        cola.insert(0, 0.0);
        
        while (!cola.isEmpty()) {
            NodoHeap actual = cola.extraerMin();
            int u = actual.vertice;
            
            if (incluido[u]) {
                continue;
            }
            incluido[u] = true;
            
            if (padre[u] != -1) {
                arbolMST.agregarArista(padre[u], u, costoMinimo[u]);
                arbolMST.agregarArista(u, padre[u], costoMinimo[u]); 
            }
            
            LinkedList<Conexion> vecinos = grafo.getVecinos(u);
            for (int i = 0; i < vecinos.size(); i++) {
                Conexion c = vecinos.getAt(i);
                int v = c.destino;
                double peso = c.peso;
                
                if (!incluido[v] && peso < costoMinimo[v]) {
                    costoMinimo[v] = peso;
                    padre[v] = u; 
                    cola.insert(v, peso);
                }
            }
        }
        
        for (int i = 0; i < vertices; i++) {
            if (padre[i] != -1) {
                CostoTotal += costoMinimo[i];
            }
        }
		
		return CostoTotal;
	}
}