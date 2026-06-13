package estructuras;

/**
 * Implementación de un TDA Cola (Queue) genérica basada en la regla FIFO (First-In, First-Out).
 * Utiliza el patrón de diseño de Composición, delegando la gestión de memoria
 * a la clase interna LinkedList para garantizar encapsulamiento estricto.
 *
 * @param <T> El tipo de datos que manejará la Cola.
 */
public class Cola<T> {
    
    private LinkedList<T> lista;

    /**
     * Inicializa una nueva Cola vacía.
     */
    public Cola() {
        this.lista = new LinkedList<>();
    }

    /**
     * Verifica si la cola carece de elementos.
     *
     * @return true si la cola está vacía, false en caso contrario.
     */
    public boolean isEmpty() {
        return lista.size() == 0;
    }

    /**
     * Inserta (encola) un elemento al final de la fila.
     *
     * @param data El elemento a agregar.
     */
    public void insert(T data) {
        lista.insert(data); 
    }

    /**
     * Extrae (desencola) y retorna el primer elemento de la fila.
     *
     * @return El elemento que llevaba más tiempo en la cola, o null si la cola está vacía.
     */
    public T remove() {
        if (isEmpty()) {
            return null; 
        }
        
        T valor = lista.getAt(0); 
        lista.deleteAt(0);        
        
        return valor;
    }
}