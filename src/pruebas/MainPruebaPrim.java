package pruebas;

import estructuras.*;

public class MainPruebaPrim {

    public static void main(String[] args) {

        // Creamos un grafo de prueba
        Grafo grafo = new Grafo(5);

        // Aristas (grafo no dirigido)
        grafo.agregarArista(0, 1, 2);
        grafo.agregarArista(0, 3, 6);
        grafo.agregarArista(1, 2, 3);
        grafo.agregarArista(1, 3, 8);
        grafo.agregarArista(1, 4, 5);
        grafo.agregarArista(2, 4, 7);
        grafo.agregarArista(3, 4, 9);

        // Ejecutamos Prim
        Prim prim = new Prim(grafo);

        double costo = prim.calcMST();

        System.out.println("Costo MST: " + costo);

        // Valor esperado del MST:
        // 0-1 (2)
        // 1-2 (3)
        // 1-4 (5)
        // 0-3 (6)
        // Total = 16

        System.out.println("Esperado: 16");
    }
}
