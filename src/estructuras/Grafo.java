package estructuras;

import java.util.ArrayList;
import java.util.List;

//Recordar: Es grafo NO dirigido. Osea, si 1 y 4 se conectan, ambos se conocen.
// 1<->4. 

public class Grafo {
	
	private List<List<Conexion>> listaAdyacencia;
	e
	public Grafo(int vertices) {
		listaAdyacencia = new ArrayList();
		
		for(int i = 0; i < vertices; i++) {
			listaAdyacencia.add(new ArrayList<>());
			
		}
	}
	
	public void agregarArista(int origen, int destino, double peso) {
		listaAdyacencia.get(origen).add(new Conexion(destino, peso));
		listaAdyacencia.get(destino).add(new Conexion(origen, peso));
		
	}
	
<<<<<<< Updated upstream
=======
	public List<List<Conexion>> getListaAdyacencia(){
		return listaAdyacencia;
	}
	
	public int getCantidadVertices() {
		return listaAdyacencia.size();
	}
	
>>>>>>> Stashed changes
	public void imprimir() {
		
		for (int i = 0; i < listaAdyacencia.size(); i++) {
			System.out.println(i + " -> ");
			
			for(Conexion conexion : listaAdyacencia.get(i)) {
				System.out.println("(" + conexion.destino + ", peso: " + conexion.peso + ")");
			}
			
			System.out.println();
		}
	}
	
	

}

/* 
Explicación visual de lo que simboliza
0 -> [(1,5), (2,3)] Como 0 me conectó a 1 con peso 5/a 2 con peso 3.
1 -> [(0,5)] Como 1 me conecto a 0 con peso 5
2 -> [(0,3)] Como 2 me conecto a 0 con peso 3

Ejemplo visual de la función del for
Si new Grafo(5)
Entonces listaAdyacencia va a ser [[], [], [], [], []] Una lista vacía por vertice.
*/

