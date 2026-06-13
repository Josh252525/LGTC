package estructuras;

/**
 * Implementación eficiente de una Cola de Prioridad basada en un Min-Heap binario puro.
 * Permite inserciones y extracciones del elemento mínimo en tiempo logarítmico O(log V),
 * siendo un componente crítico para acelerar los algoritmos de Dijkstra y Prim.
 */
public class ColaPrioridad {
    
    private NodoHeap[] heap;
    private int size;
    private int capacidad;
    
    /**
     * Construye un Min-Heap con un límite predefinido de elementos.
     *
     * @param capacidad Tamaño máximo del arreglo subyacente.
     */
    public ColaPrioridad(int capacidad) {
        this.capacidad = capacidad;
        heap = new NodoHeap[capacidad];
        size = 0;
    }
    
    /**
     * Verifica si la cola está vacía.
     *
     * @return true si no hay elementos en el Heap.
     */
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * Inserta un nuevo vértice en la cola manteniendo la propiedad de ordenamiento del Min-Heap.
     *
     * @param vertice   ID del nodo.
     * @param prioridad Distancia o costo asociado al vértice.
     */
    public void insert(int vertice, double prioridad) {
        if (size == capacidad) {
            System.out.println("Cola de prioridad llena");
            return;
        }

        NodoHeap nuevoNodo = new NodoHeap(vertice, prioridad);
        heap[size] = nuevoNodo;
        
        heapifyUp(size);
        size++;
    }

    /**
     * Extrae y remueve de la estructura el vértice con la menor distancia/prioridad registrada.
     *
     * @return El NodoHeap con prioridad mínima, o null si la estructura está vacía.
     */
    public NodoHeap extraerMin() {
        if (isEmpty()) {
            return null;
        }

        NodoHeap min = heap[0];

        heap[0] = heap[size - 1];
        heap[size - 1] = null; 
        size--;

        heapifyDown(0);

        return min;
    }
    
    // El heapifyUp hace que el elemento insertado suba si es menor a su padre.
    private void heapifyUp(int index) {
        while (index > 0) {
            int padre = (index - 1) / 2;
            
            if (heap[index].prioridad < heap[padre].prioridad) {
                NodoHeap temp = heap[index];
                heap[index] = heap[padre];
                heap[padre] = temp;
                
                index = padre; 
            } else {
                break; 
            }
        }
    }
    
    // Baja el elemento hasta llevarlo a donde pertenece en el árbol.
    private void heapifyDown(int index) {
        while (true) {
            int izquierda = 2 * index + 1;
            int derecha = 2 * index + 2;
            int menor = index;
            
            if (izquierda < size && heap[izquierda].prioridad < heap[menor].prioridad) {
                menor = izquierda;
            }
            if (derecha < size && heap[derecha].prioridad < heap[menor].prioridad) {
                menor = derecha;
            }
            
            if (menor != index) {
                NodoHeap temp = heap[index];
                heap[index] = heap[menor];
                heap[menor] = temp;
                index = menor;
            } else {
                break;
            }
        }
    }
}