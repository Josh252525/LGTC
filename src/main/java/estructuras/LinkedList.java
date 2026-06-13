package estructuras;

/*
Lista Enlazada Simple convertida a Genérica (<T>).
Al utilizar <T>, esta misma lista sirve para cualquier tipo de dato entonces está R.
- LinkedList<Integer> para la Pila/Cola.
- LinkedList<Conexion> para el Grafo.
- LinkedList<Paquete> para el Validador de Paquetes.
*/
public class LinkedList<T> {
    Node<T> head;

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

    public void insertAtStart(T data){
        Node<T> node = new Node<>();
        node.data = data;
        node.next = null;
        
        node.next = head;
        head = node;
    }

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
            if (n == null) return; // Protección por si el índice es mayor al tamaño
            n = n.next;
        }
        node.next = n.next;
        n.next = node;
    }

    public void deleteAt(int index) {
        if (head == null) return; // Protección si la lista está vacía

        if(index == 0){
            head = head.next;
        }
        else{
            Node<T> n = head;
            for (int i = 0; i < index - 1; i++) {
                if (n.next == null) return; // Protección de límites
                n = n.next;
            }
            Node<T> n1 = n.next;
            if (n1 != null) {
                n.next = n1.next;
                n1 = null; // Liberamos memoria (Garbage Collector)
            }
        }
    }

    // Retorna el dato del nodo en la posición index 
    public T getAt(int index){
        Node<T> n = head;

        for (int i = 0; i < index; i++) {
            if (n == null) return null;
            n = n.next;
        }
        return n != null ? n.data : null;
    }

    // Retorna la posición del nodo con el valor que se busca
    public int searchFor(T data){
        Node<T> node = head;
        int index = 0;

        while(node != null){
            // Usamos .equals() porque 'data' es un Objeto 
            if(node.data.equals(data)){
                return index;
            }
            index++;
            node = node.next;
        }
        return -1; // -1 es el estándar para "No se encontró"
    }

    // Nuevo método ÚTIL PARA EL GRAFO: Saber el tamaño de la lista
    public int size() {
        int count = 0;
        Node<T> n = head;
        while(n != null) {
            count++;
            n = n.next;
        }
        return count;
    }
}