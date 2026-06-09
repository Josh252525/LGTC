package unitarias;

import algoritmos.Prim;
import estructuras.Grafo;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PrimTest {

    @Test
    public void testCostoMSTNormal() {
        Grafo grafo = new Grafo(4);
        // Triángulo con una cola. El algoritmo debe romper el ciclo eliminando la arista más cara.
        grafo.agregarArista(0, 1, 10.0);
        grafo.agregarArista(1, 2, 5.0);
        grafo.agregarArista(0, 2, 1.0);  // Debería elegir esta y (1,2) para conectar 0, 1, 2
        grafo.agregarArista(2, 3, 8.0);  // Obligatoria para llegar a 3

        Prim prim = new Prim(grafo);
        double costoTotal = prim.calcMST();

        // El MST debe estar formado por: (0,2) peso 1 + (1,2) peso 5 + (2,3) peso 8 = 14.0
        assertEquals(14.0, costoTotal, 0.001, "El costo total del MST debe ser la suma de las aristas mínimas sin ciclos");
    }

    @Test
    public void testGrafoVacio() {
        Grafo grafoVacio = new Grafo(0);
        Prim prim = new Prim(grafoVacio);
        
        // Gracias a la guardia que le pusiste (if vertices == 0 return 0.0;) esto no explotará
        assertEquals(0.0, prim.calcMST(), "Un grafo sin vértices debe tener costo 0.0");
    }

    @Test
    public void testGrafoTotalmenteDesconectado() {
        Grafo grafo = new Grafo(3);
        // 3 nodos, 0 aristas.
        Prim prim = new Prim(grafo);
        
        // Al empezar en 0, no tiene vecinos. La cola se vacía de inmediato. Costo = 0.
        assertEquals(0.0, prim.calcMST(), "Un grafo sin aristas debe retornar costo 0.0 sin entrar en ciclos infinitos");
    }
}