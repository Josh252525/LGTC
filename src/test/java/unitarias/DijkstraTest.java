package unitarias;

import algoritmos.Dijkstra;
import estructuras.Grafo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DijkstraTest {

    private Grafo grafoNormal;
    private Grafo grafoDesconectado;

    @BeforeEach
    public void setUp() {
        // Grafo de 4 nodos para pruebas normales
        grafoNormal = new Grafo(4);
        grafoNormal.agregarArista(0, 1, 10.0);
        grafoNormal.agregarArista(1, 2, 5.0);
        grafoNormal.agregarArista(0, 2, 20.0); // Ruta directa más cara
        grafoNormal.agregarArista(2, 3, 2.0);

        // Grafo donde el nodo 2 está completamente aislado
        grafoDesconectado = new Grafo(3);
        grafoDesconectado.agregarArista(0, 1, 5.0);
    }

    @Test
    public void testRutaMasCortaNormal() {
        Dijkstra dijkstra = new Dijkstra(grafoNormal);
        double[] distancias = dijkstra.calcular(0);

        assertEquals(0.0, distancias[0], "La distancia a sí mismo debe ser 0");
        assertEquals(10.0, distancias[1], "La distancia 0->1 es 10");
        // Dijkstra debe preferir 0->1->2 (costo 15) sobre 0->2 directo (costo 20)
        assertEquals(15.0, distancias[2], "Debe tomar la ruta más corta pasando por 1");
        assertEquals(17.0, distancias[3], "La distancia 0->3 debe ser 10 + 5 + 2 = 17");
    }

    @Test
    public void testNodoInalcanzable() {
        Dijkstra dijkstra = new Dijkstra(grafoDesconectado);
        double[] distancias = dijkstra.calcular(0);

        assertEquals(5.0, distancias[1], "El nodo 1 es alcanzable");
        // El nodo 2 está desconectado, debe tener distancia infinita
        assertEquals(Double.MAX_VALUE, distancias[2], "Un nodo inalcanzable debe tener distancia MAX_VALUE");
    }

    @Test
    public void testOrigenInvalido() {
        Dijkstra dijkstra = new Dijkstra(grafoNormal);
        
        // Si le pasamos un nodo que no existe, debería lanzar IndexOutOfBounds
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            dijkstra.calcular(99);
        }, "Debe lanzar excepción si el origen está fuera de los límites del grafo");
    }
}