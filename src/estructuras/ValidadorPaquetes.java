package estructuras;

import java.util.ArrayList;
import java.util.List;

import Datos.Arista;
import Datos.Ciudad;
import Datos.ConfigLogisTEC;
import Datos.Paquete;
import Datos.Vertice;

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
		
		//Si el cliente tiene la maravillosa idea de no poner depósito:
		throw new IllegalStateException("No existe deposito en la ciudad"); 
			
	}
	
	//Aquí abajo se construye la ciudad a un grafo. 
	public Grafo construirGrafo() {
		Ciudad ciudad = config.ciudad();
		
		Grafo grafo = new Grafo(ciudad.vertices().size());
		
		for(Arista arista : ciudad.aristas()) {
			grafo.agregarArista(arista.u(), arista.v(), arista.distancia());
		}
		return grafo;
	}
	
	//Aquí abajo devuelve una lista con los paquetes rechazados.
	public List<Paquete> validarPaquetes(){
		
		List<Paquete> rechazados = new ArrayList<>();
		
		int deposito = encontrarDeposito();
		
		
		Grafo grafo = construirGrafo();
		Warshall warshall = new Warshall(grafo);
		
		for(Paquete paquete : config.paquetes()) {
			if(!warshall.esAlcanzable(deposito, paquete.destino())) {
				rechazados.add(paquete);
			}
		}
		return rechazados;
	}

}
