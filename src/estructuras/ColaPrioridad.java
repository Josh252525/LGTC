package estructuras;
//La idea de la Cola de prioridad es devolver el elemento con menor costo/prioridad.
//La usamos en Dijsktra y Prim. 
//Ej: Si A = 3, B = 1 y C = 2, devuelve B.

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
	
	public void insert(int vertice, int prioridad) {
		NodoHeap nuevoNodo = new NodoHeap(vertice, prioridad);
		
		heap[size] = nuevoNodo;
		
		
		heapifyUp(size);
		
		size++;
	}
	
	//El heapify up hace que el elemento insertado suba si es menor a su padre.
	public void heapifyUp(int index) {
		
		while (index > 0) {
			int padre = (index - 1)/2;
			
			//Si su prioridad es menor a la de su padre.
			if(heap[index].prioridad < heap[padre].prioridad){
				//Guardamos referencia del papa
				NodoHeap temp = heap[index];
				//El heap en la posición del index ahora es igual a la posición del padre, subiendo.
				heap[index] = heap[padre];
				//El padre está ahora en la posición donde estaba el index.
				heap[padre] = temp;
				
				index = padre;
			}else {
				break;
			}
		}
	}
	
	//heapifyDown hace que al sacar el mínimo se reorganiza el árbol.
	//Baja el elemento hasta llevarlo a donde pertenence.
	public void heapifyDown(int index) {
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
			if(menor != index) {
				NodoHeap temp = heap[index];
				heap[index] = heap[menor];
				heap[menor] = temp;
				
				index = menor;
			}else {
				break;
			}
		}
	}
	
	//extraerMin() no solo devuelve el peso, sino el nodo completo. Cada nodo tiene vertice y prioridad.
	public NodoHeap extraerMin() {
		if(isEmpty()) {
			return null;
		}
		
		NodoHeap minimo = heap[0];
		heap[0] = heap[size-1];
		size--;
		heapifyDown(0);
		return minimo;
		
	}
	

}
