package estructuras;

/**
 * Estructura principal que representa un Grafo ponderado utilizando Listas de Adyacencia.
 * Optimizada para el enrutamiento logístico y operaciones matemáticas de caminos mínimos.
 */
public class Grafo {
    
    private LinkedList<Conexion>[] listaAdyacencia;
    private int cantidadVertices;
    
    /**
     * Inicializa un nuevo Grafo con una cantidad fija de vértices.
     *
     * @param vertices El número total de intersecciones o nodos en la ciudad.
     */
    @SuppressWarnings("unchecked") 
    public Grafo(int vertices) {
        this.cantidadVertices = vertices;
        listaAdyacencia = new LinkedList[vertices];
        
        for(int i = 0; i < vertices; i++) {
            listaAdyacencia[i] = new LinkedList<Conexion>();
        }
    }
    
    /**
     * Agrega una nueva calle (arista bidireccional) entre dos vértices existentes.
     * Como es un grafo no dirigido, la conexión se inyecta en ambos sentidos.
     *
     * @param origen  Vértice A.
     * @param destino Vértice B.
     * @param peso    Distancia o costo de la arista.
     */
    public void agregarArista(int origen, int destino, double peso) {
        listaAdyacencia[origen].insert(new Conexion(destino, peso));
        listaAdyacencia[destino].insert(new Conexion(origen, peso));
    }
    
    /**
     * Extrae todas las calles adyacentes conectadas directamente a un vértice dado.
     *
     * @param vertice El ID del nodo a consultar.
     * @return Una LinkedList que contiene objetos de tipo Conexion.
     */
    public LinkedList<Conexion> getVecinos(int vertice) {
        return listaAdyacencia[vertice];
    }
    
    /**
     * Retorna la capacidad total de nodos configurados en el grafo.
     *
     * @return El número de vértices.
     */
    public int getCantidadVertices() {
        return cantidadVertices;
    }
}