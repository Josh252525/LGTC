package estructuras;

public class Node<T> {
    T data;         // 'T' representa cualquier Tipo de objeto
    Node<T> next;
    
    public Node() {
        this.data = null;
        this.next = null;
    }
}