package datos;

import estructuras.*;
import algoritmos.Warshall;

public class ValidadorPaquetes {
	
	private ConfigLogisTEC config;
	
	public ValidadorPaquetes(ConfigLogisTEC config) {
		this.config = config;
	}
	
	public int encontrarDeposito() {
		for(Vertice vertice : config.ciudad().vertices()) {
			if(vertice.tipo().equalsIgnoreCase("DEPOT")) {
				return vertice.id();
			}
		}
		throw new IllegalStateException("No existe deposito en la ciudad"); 
	}
	
	public Grafo construirGrafo() {
		Ciudad ciudad = config.ciudad();
		Grafo grafo = new Grafo(ciudad.vertices().length);
		
		for(Arista arista : ciudad.aristas()) {
			grafo.agregarArista(arista.u(), arista.v(), arista.distancia());
		}
		return grafo;
	}
	
	public LinkedList<Paquete> validarPaquetes(){
		
		// 1. Usamos nuestra propia LinkedList genérica
		LinkedList<Paquete> rechazados = new LinkedList<>();
		
		int deposito = encontrarDeposito();
		Grafo grafo = construirGrafo();
		
		// 2. Ejecutamos el algoritmo de Warshall
		Warshall warshall = new Warshall(grafo);
		
		// 3. Revisamos paquete por paquete del JSON
		for(Paquete paquete : config.paquetes()) {
		    // Si el destino del paquete NO es alcanzable desde el depósito...
		    if(!warshall.esAlcanzable(deposito, paquete.destino())) {
		        rechazados.insert(paquete); // ...lo agregamos a la lista de rechazados
		    }
		}
		
		return rechazados;
	}
}