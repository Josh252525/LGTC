package pruebas;

import datos.*; // Se importan los modelos
import estructuras.*;

public class MainPruebaWarshall {

    public static void main(String[] args) {

        // 1. Creamos los vértices usando un arreglo nativo
        Vertice[] vertices = new Vertice[] {
                new Vertice(0, "DEPOT", 0, 0),
                new Vertice(1, "INTERSECCION", 0, 0),
                new Vertice(2, "INTERSECCION", 0, 0),
                new Vertice(3, "INTERSECCION", 0, 0)
        };

        // 2. Creamos las aristas usando un arreglo nativo
        Arista[] aristas = new Arista[] {
                new Arista(0, 1, 10.0),
                new Arista(1, 2, 10.0)
        };

        Ciudad ciudad = new Ciudad(vertices, aristas);

        // 3. Creamos los paquetes usando un arreglo nativo
        Paquete[] paquetes = new Paquete[] {
                new Paquete(1, 2, 5.0, 1),
                new Paquete(2, 3, 4.0, 2) // El destino 3 está desconectado en este grafo
        };

        ConfigLogisTEC config = new ConfigLogisTEC(
                ciudad,
                paquetes,
                new Camion[0] // Arreglo de camiones vacío para la prueba
        );

        ValidadorPaquetes validador = new ValidadorPaquetes(config);

        LinkedList<Paquete> rechazados = validador.validarPaquetes();

        System.out.println("--- REPORTE DE PAQUETES INALCANZABLES ---");
        
        // 5. Iteramos usando los métodos de LinkedList
        int cantidadRechazados = rechazados.size();
        for(int i = 0; i < cantidadRechazados; i++) {
            Paquete paquete = rechazados.getAt(i);
            System.out.println(
                    "Paquete rechazado ID: " + paquete.id() + 
                    " (El camión no puede llegar al destino " + paquete.destino() + ")"
            );
        }
    }
}