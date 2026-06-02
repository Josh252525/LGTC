package pruebas;

import datos.*;

public class MainPruebaDijkstra {
	
	public static void main(String [] args) {
		
		Grafo grafo = new Grafo(5);
		
		Dijkstra ejemplo = new Dijkstra(grafo);
		
		grafo.agregarArista(0, 1, 4);
		grafo.agregarArista(0, 2, 1);
		grafo.agregarArista(2, 1, 2);
		grafo.agregarArista(1, 3, 1);
		grafo.agregarArista(2, 3, 5);
		grafo.agregarArista(3, 4, 3);
		
		double[] resultado = ejemplo.calcular(0);

		for(int i = 0; i < resultado.length; i++) {
		    System.out.println(
		        "Distancia a " +
		        i +
		        ": " +
		        resultado[i] + "km"
		    );
		}
		
		
		
		
	}

}
