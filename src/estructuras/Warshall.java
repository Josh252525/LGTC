package estructuras;

public class Warshall {
	
	public boolean[][] alcanzable;
	
	public Warshall(Grafo grafo) {
		
		int n = grafo.getCantidadVertices();
		alcanzable = new boolean[n][n];
		
		for(int a = 0; a < n; a++) {
			alcanzable[a][a] = true;
			//Todo nodo se puede alcanzar a sí mismo.
		}
			
		for(int p = 0; p < n; p++) {
			
			for(Conexion conexion : grafo.getListaAdyacencia().get(p)) {
				alcanzable[p][conexion.destino] = true;
				//Conexión directa, lo que sabe desde el inicio.
				
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
