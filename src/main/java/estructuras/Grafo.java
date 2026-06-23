package estructuras;

public class Grafo {
	
	// Arreglo estático de propias Listas Enlazadas
	private LinkedList<Conexion>[] listaAdyacencia;
	private int cantidadVertices;
	
	@SuppressWarnings("unchecked") // Le dice a Java que confíe en la creación del arreglo genérico, literalmente tuvimos problemas de merge por eso.
	public Grafo(int vertices) {
		this.cantidadVertices = vertices;
		
		// Inicializamos el arreglo nativo
		listaAdyacencia = new LinkedList[vertices];
		
		// Creamos una LinkedList de Conexiones para cada intersección
		for(int i = 0; i < vertices; i++) {
			listaAdyacencia[i] = new LinkedList<Conexion>();
		}
	}
	
	public void agregarArista(int origen, int destino, double peso) {
		// Grafo no dirigido: se conectan mutuamente
		listaAdyacencia[origen].insert(new Conexion(destino, peso));
		listaAdyacencia[destino].insert(new Conexion(origen, peso));
	}
	
	
	public LinkedList<Conexion> getVecinos(int vertice) {
	    return listaAdyacencia[vertice];
	}
	
	public int getCantidadVertices() {
		return cantidadVertices;
	}
}