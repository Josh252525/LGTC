package unitarias;

import algoritmos.Kruskal;
import estructuras.Grafo;
import org.junit.jupiter.api.Test;

import java.util.List;

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

        Kruskal kruskal = new Kruskal();

        List<Kruskal.MSTEdge> mst = kruskal.kruskal(grafo);

        double costoTotal = 0.0;

        for (Kruskal.MSTEdge arista : mst) {
            costoTotal += arista.peso;
        }

        assertEquals(
                14.0,
                costoTotal,
                0.001,
                "El costo total del MST debe ser la suma de las aristas mínimas sin ciclos"
        );
    }

    @Test
    public void testGrafoVacio() {

        Grafo grafo = new Grafo(0);

        Kruskal kruskal = new Kruskal();

        List<Kruskal.MSTEdge> mst = kruskal.kruskal(grafo);

        assertEquals(
                0,
                mst.size(),
                "Un grafo vacío debe producir un MST vacío"
        );
    }

    @Test
    public void testGrafoTotalmenteDesconectado() {

        Grafo grafo = new Grafo(3);

        Kruskal kruskal = new Kruskal();

        List<Kruskal.MSTEdge> mst = kruskal.kruskal(grafo);

        double costoTotal = 0.0;

        for (Kruskal.MSTEdge arista : mst) {
            costoTotal += arista.peso;
        }

        assertEquals(
                0.0,
                costoTotal,
                0.001,
                "Un grafo sin aristas debe producir un MST de costo 0"
        );

        assertEquals(
                0,
                mst.size(),
                "Un grafo sin aristas no debe contener aristas en el MST"
        );
    }
}