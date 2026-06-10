package unitarias;

import algoritmos.Kruskal;
import estructuras.Grafo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class KruskalTest {

    @Test
    public void testCostoMSTNormal() {

        Grafo grafo = new Grafo(4);

        // Triángulo con una cola
        grafo.agregarArista(0, 1, 10.0);
        grafo.agregarArista(1, 2, 5.0);
        grafo.agregarArista(0, 2, 1.0);
        grafo.agregarArista(2, 3, 8.0);

        Kruskal kruskal = new Kruskal(grafo);

        double costoTotal = kruskal.calcMST();

        // MST esperado: (0-2)=1 + (1-2)=5 + (2-3)=8 = 14
        assertEquals(
                14.0,
                costoTotal,
                0.001,
                "El costo del MST con Kruskal debe ser 14.0"
        );
    }

    @Test
    public void testGrafoVacio() {

        Grafo grafo = new Grafo(0);

        Kruskal kruskal = new Kruskal(grafo);

        assertEquals(
                0.0,
                kruskal.calcMST(),
                0.001,
                "Un grafo vacío debe retornar costo 0.0"
        );
    }

    @Test
    public void testGrafoTotalmenteDesconectado() {

        Grafo grafo = new Grafo(3);

        Kruskal kruskal = new Kruskal(grafo);

        double costo = kruskal.calcMST();

        assertEquals(
                0.0,
                costo,
                0.001,
                "Un grafo sin aristas debe retornar costo 0.0"
        );
    }

    @Test
    public void testMSTConectividadBasica() {

        Grafo grafo = new Grafo(3);

        grafo.agregarArista(0, 1, 4.0);
        grafo.agregarArista(1, 2, 2.0);
        grafo.agregarArista(0, 2, 10.0);

        Kruskal kruskal = new Kruskal(grafo);

        assertEquals(
                6.0,
                kruskal.calcMST(),
                0.001,
                "Debe elegir las aristas 1-2 y 0-1"
        );
    }
}
