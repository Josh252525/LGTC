package estructuras;

/*
Pila (Stack) Genérica basada en nuestra propia LinkedList.
Funciona bajo el principio LIFO.
*/
public class Pila<T> {
    
    private LinkedList<T> lista;

    public Pila() {
        this.lista = new LinkedList<>();
    }

    public boolean isEmpty() {
        return lista.size() == 0;
    }

    // Apilar: Lo ponemos al inicio de la pila 
    public void push(T data) {
        lista.insertAtStart(data); 
    }

    // Desapilar: Sacamos el que está en el inicio
    public T pop() {
        if (isEmpty()) {
            System.out.println("La pila está vacía");
            return null;
        }
        
        T valor = lista.getAt(0); 
        lista.deleteAt(0);        
        
        return valor;
    }

    // Ver el tope sin sacarlo
    public T peek() {
        if (isEmpty()) {
            return null;
        }
        return lista.getAt(0);
    }
}