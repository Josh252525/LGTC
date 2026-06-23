package estructuras;

/*
Complejidad: O(log V) para inserciones y extracciones. 
Método extraerMin() que es el que Dijkstra llama para sacar 
   el vértice más cercano de la cola.
*/
public class ColaPrioridad {
	
	private NodoHeap[] heap;
	private int size;
	private int capacidad;
	
	public ColaPrioridad(int capacidad) {
		this.capacidad = capacidad;
		heap = new NodoHeap[capacidad];
		size = 0;
	}
	
	public boolean isEmpty() {
		return size == 0;
	}
	
	public void insert(int vertice, double prioridad) {
        // Si el heap se llena, se evita que el programa colapse
        if (size == capacidad) {
            System.out.println("Cola de prioridad llena");
            return;
        }

		NodoHeap nuevoNodo = new NodoHeap(vertice, prioridad);
		heap[size] = nuevoNodo;
		
		heapifyUp(size);
		size++;
	}

    // Método que necesita el Dijkstra para sacar al que tiene menor distancia
    public NodoHeap extraerMin() {
        if (isEmpty()) {
            return null;
        }

        // El mínimo siempre está en la raíz (posición 0) en un Min-Heap
        NodoHeap min = heap[0];

        // Se mueve el último elemento del arreglo a la raíz
        heap[0] = heap[size - 1];
        heap[size - 1] = null; // Fucking limpiando la vara porque ajá
        size--;

        // Reorganizamos hacia abajo para que el nuevo nodo en la raíz baje a su lugar correcto
        heapifyDown(0);

        return min;
    }
	
	// El heapifyUp hace que el elemento insertado suba si es menor a su padre.
	private void heapifyUp(int index) {
		while (index > 0) {
			int padre = (index - 1) / 2;
			
			// Si su prioridad es menor a la de su padre, se intercambian
			if (heap[index].prioridad < heap[padre].prioridad) {
				NodoHeap temp = heap[index];
				heap[index] = heap[padre];
				heap[padre] = temp;
				
				index = padre; // Seguimos subiendo
			} else {
				break; // Si ya es mayor que el padre, está en su lugar correcto
			}
		}
	}
	
	// Baja el elemento hasta llevarlo a donde pertenece.
	private void heapifyDown(int index) {
		while (true) {
			int izquierda = 2 * index + 1;
			int derecha = 2 * index + 2;
			int menor = index;
			
			// Comparamos con el hijo izquierdo
			if (izquierda < size && heap[izquierda].prioridad < heap[menor].prioridad) {
				menor = izquierda;
			}
			// Comparamos con el hijo derecho
			if (derecha < size && heap[derecha].prioridad < heap[menor].prioridad) {
				menor = derecha;
			}
			
			// Si el menor ya no es el padre original, intercambiamos
			if (menor != index) {
				NodoHeap temp = heap[index];
				heap[index] = heap[menor];
				heap[menor] = temp;
				
				index = menor; // Seguimos bajando
			} else {
				break;
			}
		}
	}
}