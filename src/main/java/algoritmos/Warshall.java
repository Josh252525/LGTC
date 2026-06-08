package algoritmos;

import estructuras.*;

public class Warshall {
	
	public boolean[][] alcanzable;
	
	public Warshall(Grafo grafo) {
		
		int n = grafo.getCantidadVertices();
		alcanzable = new boolean[n][n];
		
		for(int a = 0; a < n; a++) {
			alcanzable[a][a] = true;
		}
			
		//Adaptado a la LinkedList Genérica
		for(int p = 0; p < n; p++) {
		    LinkedList<Conexion> vecinos = grafo.getVecinos(p);
		    int cantidadVecinos = vecinos.size();
		    
		    // Usamos el for clásico y el método getAt() que construimos
			for(int i = 0; i < cantidadVecinos; i++) {
			    Conexion conexion = vecinos.getAt(i);
				alcanzable[p][conexion.destino] = true;
			}
		}
				
		for(int k = 0; k < n; k++) {
			for(int i = 0; i < n; i++) {
				for(int j = 0; j < n; j++) {
					alcanzable[i][j] = alcanzable[i][j] || (alcanzable[i][k] && alcanzable[k][j]);
				}
			}
		}	
	}
	
	public boolean esAlcanzable(int origen, int destino) {
		return alcanzable[origen][destino];
	}
}