package estructuras;

/*
Documentación:
Cola Genérica basada en LinkedList.
Utiliza el principio de Composición: delegamos el manejo de memoria a la lista
para mantener esta clase limpia y enfocada solo en la lógica FIFO.
*/
public class Cola<T> {
    
    private LinkedList<T> lista;

    public Cola() {
        this.lista = new LinkedList<>();
    }

    public boolean isEmpty() {
        return lista.size() == 0;
    }

    // Encolar: Entra al final de la fila
    public void insert(T data) {
        lista.insert(data); 
    }

    // Desencolar: Sale el primero de la fila
    public T remove() {
        if (isEmpty()) {
            return null; // Retorna null de forma segura si está vacía
        }
        
        T valor = lista.getAt(0); // Se toma al que está de primero
        lista.deleteAt(0);        // Lo sacamos de la fila
        
        return valor;
    }
}