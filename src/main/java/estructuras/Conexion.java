package estructuras;

/**
 * Representa una arista adyacente en el grafo.
 * Funciona como un modelo de datos (DTO simple) para vincular un vértice
 * de origen con un vértice de destino, incluyendo el costo de dicho trayecto.
 */
public class Conexion {

    /** El ID numérico del vértice al cual se dirige esta conexión. */
    public int destino;
    
    /** La magnitud, peso o costo (en kilómetros) de transitar esta conexión. */
    public double peso;
    
    /**
     * Construye una nueva conexión dirigida.
     *
     * @param destino El nodo destino.
     * @param peso    El costo de la arista.
     */
    public Conexion(int destino, double peso) {
        this.destino = destino;
        this.peso = peso;
    }
}