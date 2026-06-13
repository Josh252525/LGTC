package estructuras;

/**
 * Estructura contenedora utilizada exclusivamente por la Cola de Prioridad.
 * Asocia un vértice del grafo con su prioridad (costo o distancia) actual,
 * permitiendo el ordenamiento interno del Min-Heap.
 */
public class NodoHeap {
    
    /** El ID numérico de la ciudad o intersección. */
    public int vertice;
    
    /** El costo acumulado o distancia mínima calculada hacia este vértice. */
    public double prioridad;
    
    /**
     * Construye un nuevo registro para el Heap.
     *
     * @param vertice   El vértice.
     * @param prioridad Su peso o costo.
     */
    public NodoHeap(int vertice, double prioridad) {
        this.vertice = vertice;
        this.prioridad = prioridad;
    }
}