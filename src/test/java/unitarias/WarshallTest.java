package unitarias;

import algoritmos.Warshall;
import estructuras.Grafo;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WarshallTest {

    @Test
    public void testCierreTransitivoNormal() {
        Grafo grafo = new Grafo(4);
        // 0 se conecta con 1. 1 se conecta con 2. 2 se conecta con 3.
        // Formando una línea: 0 - 1 - 2 - 3
        grafo.agregarArista(0, 1, 1.0);
        grafo.agregarArista(1, 2, 1.0);
        grafo.agregarArista(2, 3, 1.0);

        // El constructor de Warshall pre-calcula toda la matriz de una vez
        Warshall warshall = new Warshall(grafo);

        // Caminos directos
        assertTrue(warshall.esAlcanzable(0, 1), "0 debe alcanzar a 1 directamente");
        
        // Caminos transitivos (Warshall debe deducir estos)
        assertTrue(warshall.esAlcanzable(0, 3), "0 debe alcanzar a 3 pasando por 1 y 2");
        assertTrue(warshall.esAlcanzable(3, 0), "3 debe alcanzar a 0 porque es no dirigido");
        
        // Reflexividad (Llegar a sí mismo)
        assertTrue(warshall.esAlcanzable(2, 2), "Todo nodo es alcanzable desde sí mismo");
    }

    @Test
    public void testGrafoSeparadoEnIslas() {
        Grafo grafo = new Grafo(4);
        // Isla A: Nodos 0 y 1
        grafo.agregarArista(0, 1, 5.0);
        
        // Isla B: Nodos 2 y 3
        grafo.agregarArista(2, 3, 8.0);

        Warshall warshall = new Warshall(grafo);

        assertTrue(warshall.esAlcanzable(0, 1), "0 y 1 están en la misma isla");
        assertTrue(warshall.esAlcanzable(2, 3), "2 y 3 están en la misma isla");
        
        // Comprobación de que Warshall no mezcla islas
        assertFalse(warshall.esAlcanzable(0, 2), "0 no debería poder alcanzar a 2");
        assertFalse(warshall.esAlcanzable(1, 3), "1 no debería poder alcanzar a 3");
    }

    @Test
    public void testOrigenVacioYExcepciones() {
        Grafo grafo = new Grafo(2);
        Warshall warshall = new Warshall(grafo);
        
        // Si pedimos una ciudad fuera del rango de la matriz pre-calculada
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            warshall.esAlcanzable(5, 1);
        }, "Debe lanzar excepción al consultar nodos que no existían cuando se calculó la matriz");
    }
}