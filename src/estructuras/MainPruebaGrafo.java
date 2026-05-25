package estructuras;

public class MainPruebaGrafo {
	
	public static void main(String [] args) {
		
		Grafo grafo = new Grafo(5);
		
		grafo.agregarArista(0, 1, 5);
		grafo.agregarArista(0, 2, 3);
		grafo.agregarArista(1, 4, 7);
		
		grafo.imprimir();
	}

}
