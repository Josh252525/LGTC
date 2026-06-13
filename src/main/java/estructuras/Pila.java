package estructuras;

/**
 * Implementación de un TDA Pila (Stack) genérica basada en la regla LIFO (Last-In, First-Out).
 * Diseñada mediante el patrón de Composición utilizando la estructura LinkedList
 * subyacente para gestionar los elementos en el tope.
 *
 * @param <T> El tipo de datos que manejará la Pila.
 */
public class Pila<T> {
    
    private LinkedList<T> lista;

    /**
     * Inicializa una nueva Pila vacía.
     */
    public Pila() {
        this.lista = new LinkedList<>();
    }

    /**
     * Verifica si la pila carece de elementos.
     *
     * @return true si la pila está vacía, false en caso contrario.
     */
    public boolean isEmpty() {
        return lista.size() == 0;
    }

    /**
     * Apila un nuevo elemento colocándolo en el tope (inicio) de la estructura.
     *
     * @param data El elemento a insertar.
     */
    public void push(T data) {
        lista.insertAtStart(data); 
    }

    /**
     * Desapila y extrae el elemento que se encuentra en el tope de la estructura.
     *
     * @return El elemento más recientemente agregado, o null si la pila está vacía.
     */
    public T pop() {
        if (isEmpty()) {
            System.out.println("La pila está vacía");
            return null;
        }
        
        T valor = lista.getAt(0); 
        lista.deleteAt(0);        
        
        return valor;
    }

    /**
     * Observa el elemento en el tope de la pila sin extraerlo ni modificar la estructura.
     *
     * @return El elemento en el tope, o null si está vacía.
     */
    public T peek() {
        if (isEmpty()) {
            return null;
        }
        return lista.getAt(0);
    }
}