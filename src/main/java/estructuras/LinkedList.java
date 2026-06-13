package estructuras;

/**
 * Implementación personalizada de una Lista Enlazada Simple genérica.
 * Diseñada para cumplir con la restricción de no utilizar el Framework de Colecciones de Java.
 * Permite almacenamiento dinámico y secuencial de cualquier tipo de objeto.
 *
 * @param <T> El tipo de elementos que contendrá la lista.
 */
public class LinkedList<T> {
    Node<T> head;

    /**
     * Inserta un nuevo elemento al final de la lista.
     *
     * @param data El elemento a insertar.
     */
    public void insert(T data){
        Node<T> node = new Node<>();
        node.data = data;
        node.next = null;

        if (head == null){
            head = node;
        }
        else{
            Node<T> n = head;
            while(n.next != null){
                n = n.next;
            }
            n.next = node;
        }
    }

    /**
     * Inserta un nuevo elemento al inicio de la lista, desplazando al resto.
     *
     * @param data El elemento a insertar como nueva cabeza (head).
     */
    public void insertAtStart(T data){
        Node<T> node = new Node<>();
        node.data = data;
        node.next = null;
        
        node.next = head;
        head = node;
    }

    /**
     * Inserta un elemento en una posición específica de la lista.
     *
     * @param index La posición en la que se desea insertar el elemento (basado en índice 0).
     * @param data  El elemento a insertar.
     */
    public void insertAt(int index, T data){
        if(index == 0){
            insertAtStart(data);
            return;
        }
        
        Node<T> node = new Node<>();
        node.data = data;
        node.next = null;

        Node<T> n = head;
        for (int i = 0; i < index - 1; i++) {
            if (n == null) return; 
            n = n.next;
        }
        if (n != null) {
            node.next = n.next;
            n.next = node;
        }
    }

    /**
     * Elimina el nodo ubicado en una posición específica.
     *
     * @param index El índice del elemento a eliminar.
     */
    public void deleteAt(int index){
        if (head == null) return;

        if(index == 0){
            head = head.next;
        }
        else{
            Node<T> n = head;
            for (int i = 0; i < index - 1; i++) {
                if (n.next == null) return; 
                n = n.next;
            }
            Node<T> n1 = n.next;
            if (n1 != null) {
                n.next = n1.next;
                n1 = null; 
            }
        }
    }

    /**
     * Recupera el elemento ubicado en una posición específica sin eliminarlo.
     *
     * @param index La posición del elemento a recuperar.
     * @return El dato almacenado en el índice especificado, o null si el índice está fuera de los límites.
     */
    public T getAt(int index){
        Node<T> n = head;

        for (int i = 0; i < index; i++) {
            if (n == null) return null;
            n = n.next;
        }
        return n != null ? n.data : null;
    }

    /**
     * Busca la primera aparición de un elemento en la lista.
     *
     * @param data El elemento a buscar.
     * @return El índice del elemento si se encuentra, o -1 si el elemento no existe en la lista.
     */
    public int searchFor(T data){
        Node<T> node = head;
        int index = 0;

        while(node != null){
            if(node.data.equals(data)){
                return index;
            }
            index++;
            node = node.next;
        }
        return -1;
    }

    /**
     * Calcula la cantidad actual de elementos en la lista.
     *
     * @return El número entero de nodos almacenados.
     */
    public int size() {
        int count = 0;
        Node<T> current = head;
        while (current != null) {
            count++;
            current = current.next;
        }
        return count;
    }
}