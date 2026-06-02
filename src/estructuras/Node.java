package estructuras;

public class Node<T> {
    T data;         // 'T' representa cualquier Tipo de objeto
    Node<T> siguiente;
    
    public Node(T data) {
        this.data = data;
        this.siguiente = null;
    }
}