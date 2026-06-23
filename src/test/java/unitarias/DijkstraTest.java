package unitarias;

import algoritmos.Dijkstra;
import estructuras.Grafo;
import estructuras.LinkedList;
import org.junit.jupiter.api.Test; // Si usas JUnit 4, cambia a org.junit.Test
import static org.junit.jupiter.api.Assertions.*; // Si usas JUnit 4, usa org.junit.Assert.*

public class DijkstraTest {

    @Test
    public void testRutaOptimaConAtajo() {
        // 1. Arrange: Preparamos un grafo de 5 vértices
        Grafo grafo = new Grafo(5);
        
        // Agregamos aristas simulando calles. 
        // Nota: Hay una conexión directa 0 -> 1 que cuesta 10km.
        // Pero el camino 0 -> 2 -> 1 cuesta solo 5km + 2km = 7km. Dijkstra DEBE elegir el atajo.
        grafo.agregarArista(0, 1, 10.0);
        grafo.agregarArista(0, 2, 5.0);
        grafo.agregarArista(2, 1, 2.0);
        grafo.agregarArista(1, 3, 1.0);
        grafo.agregarArista(3, 4, 4.0);

        Dijkstra dijkstra = new Dijkstra(grafo);

        // 2. Act: Ejecutamos el método que refactorizamos
        LinkedList<Integer> ruta = dijkstra.calcular(0, 4);

        // 3. Assert: Validamos que haya tomado el camino más barato, no el de menos saltos
        assertNotNull(ruta, "La ruta no debería ser nula");
        assertEquals(5, ruta.size(), "La ruta óptima debe tener exactamente 5 paradas");
        
        // Verificamos las 'migas de pan' calle por calle
        assertEquals(0, ruta.getAt(0)); // Origen
        assertEquals(2, ruta.getAt(1)); // Atajo
        assertEquals(1, ruta.getAt(2));
        assertEquals(3, ruta.getAt(3));
        assertEquals(4, ruta.getAt(4)); // Destino final
    }

    @Test
    public void testNodoInalcanzable() {
        // 1. Arrange: Un grafo donde el nodo 2 está desconectado
        Grafo grafo = new Grafo(3);
        grafo.agregarArista(0, 1, 5.0);
        // No agregamos aristas para el nodo 2

        Dijkstra dijkstra = new Dijkstra(grafo);

        // 2. Act: Intentamos ir al nodo aislado
        LinkedList<Integer> ruta = dijkstra.calcular(0, 2);

        // 3. Assert: Nuestra implementación devuelve una lista vacía para nodos inalcanzables
        assertNotNull(ruta);
        assertEquals(0, ruta.size(), "La ruta hacia un nodo inalcanzable debe estar vacía");
    }

    @Test
    public void testMismoOrigenYDestino() {
        // 1. Arrange
        Grafo grafo = new Grafo(2);
        grafo.agregarArista(0, 1, 10.0);

        Dijkstra dijkstra = new Dijkstra(grafo);

        // 2. Act: El camión pide ir del Depósito al Depósito
        LinkedList<Integer> ruta = dijkstra.calcular(0, 0);

        // 3. Assert: Debe devolver la lista solo con el nodo de origen
        assertNotNull(ruta);
        assertEquals(1, ruta.size(), "Debe contener solo el origen");
        assertEquals(0, ruta.getAt(0), "El nodo debe ser el 0");
    }
}