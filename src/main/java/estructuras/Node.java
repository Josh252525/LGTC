package estructuras;

/**
 * Clase base que representa un nodo genérico dentro de una estructura de datos enlazada.
 * Actúa como el bloque de construcción fundamental para la LinkedList, Pila y Cola.
 *
 * @param <T> El tipo de dato que almacenará el nodo.
 */
public class Node<T> {
    T data;         
    Node<T> next;
    
    /**
     * Constructor por defecto. Inicializa un nodo vacío sin datos y sin referencias.
     */
    public Node() {
        this.data = null;
        this.next = null;
    }
}