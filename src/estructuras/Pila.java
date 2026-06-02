package estructuras;

/*
Documentación:
Pila (Stack) Genérica basada en nuestra propia LinkedList.
Reemplaza la antigua implementación de arreglos. Funciona bajo el principio LIFO.
*/
public class Pila<T> {
    
    private LinkedList<T> lista;

    public Pila() {
        this.lista = new LinkedList<>();
    }

    public boolean isEmpty() {
        return lista.size() == 0;
    }

    // Apilar: Lo ponemos en el tope de la pila (al inicio)
    public void push(T data) {
        lista.insertAtStart(data); // Tu método que mete al principio
    }

    // Desapilar: Sacamos el que está en el tope
    public T pop() {
        if (isEmpty()) {
            System.out.println("La pila está vacía");
            return null;
        }
        
        T valor = lista.getAt(0); // Tomamos el del tope
        lista.deleteAt(0);        // Lo quitamos
        
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