package planner;

import estructuras.LinkedList;

public class EnrutadorNearestNeighbor {
	
	public LinkedList<Integer> generarRuta(int deposito, LinkedList<Integer> destinosCamion, double[][] matrizFloyd){
		
		if (matrizFloyd == null || matrizFloyd.length == 0) {
            throw new IllegalArgumentException("Matriz Floyd inválida.");
        }
		
		LinkedList<Integer> ruta = new LinkedList<>();
		
		// Caso extremo: sin entregas
        if (destinosCamion == null || destinosCamion.size() == 0) {
            ruta.insert(deposito);
            return ruta;
        }
        
        int cantidadDestinos = destinosCamion.size();
        boolean[] visitado = new boolean[cantidadDestinos];
        int ciudadActual = deposito;
        
        // Iniciamos en el depósito
        ruta.insert(deposito);
        
        for(int entregasRealizadas = 0; entregasRealizadas < cantidadDestinos; entregasRealizadas++) {
        	
        	double mejorDistancia = Double.POSITIVE_INFINITY;
            int mejorIndice = -1;
            
            // Buscar el destino no visitado más cercano
            for (int i = 0; i < cantidadDestinos; i++) {
                if (visitado[i]) {
                    continue;
                }

                int ciudadDestino = destinosCamion.getAt(i);
                double distancia = matrizFloyd[ciudadActual][ciudadDestino];
                
                //Caso extremo: Si la distancia es infinita. 
                if (distancia == Double.POSITIVE_INFINITY) {
                    continue;
                }
                        
                if (distancia < mejorDistancia) {
                    mejorDistancia = distancia;
                    mejorIndice = i;
                }
            }
            
            // Seguridad
            if (mejorIndice == -1) {
                break;
            }
            
            visitado[mejorIndice] = true;
            ciudadActual = destinosCamion.getAt(mejorIndice);
            ruta.insert(ciudadActual);
        }
        
        // Volver al depósito
        ruta.insert(deposito);

        return ruta;
	}
}