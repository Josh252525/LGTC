package pruebas;

import estructuras.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/*
Documentación:
Pruebas unitarias robustas para los recorridos BFS y DFS.
Se utiliza JUnit 5 para asegurar que los algoritmos visiten los vértices
en el orden matemático correcto y manejen grafos desconectados.
*/
public class RecorridosTest {

    private Grafo grafoNormal;
    private Recorridos explorador;

    @BeforeEach
    public void setUp() {
        // Se ejecuta antes de CADA prueba para tener un grafo limpio
        // Grafo de prueba:
        // 0 -- 1
        // |    |
        // 2 -- 3
        grafoNormal = new Grafo(4);
        grafoNormal.agregarArista(0, 1, 10.0);
        grafoNormal.agregarArista(0, 2, 20.0);
        grafoNormal.agregarArista(1, 3, 30.0);
        grafoNormal.agregarArista(2, 3, 40.0);
        
        explorador = new Recorridos(grafoNormal);
    }

    @Test
    public void testBfsRecorridoNormal() {
        // Ejecutamos el BFS desde el vértice 0
        LinkedList<Integer> resultado = explorador.bfs(0);

        // Validaciones robustas
        assertNotNull(resultado, "El resultado no debería ser nulo");
        assertEquals(4, resultado.size(), "Debería haber visitado los 4 vértices");

        // El orden esperado en BFS (por niveles): 0, luego 1 y 2, luego 3
        assertEquals(0, resultado.getAt(0));
        assertEquals(1, resultado.getAt(1));
        assertEquals(2, resultado.getAt(2));
        assertEquals(3, resultado.getAt(3));
    }

    @Test
    public void testDfsRecorridoNormal() {
        // Ejecutamos el DFS iterativo desde el vértice 0
        LinkedList<Integer> resultado = explorador.dfs(0);

        // Validaciones robustas
        assertNotNull(resultado, "El resultado no debería ser nulo");
        assertEquals(4, resultado.size(), "Debería haber visitado los 4 vértices");

        // El orden esperado en DFS con Pila (LIFO): 
        // Mete 1 y 2 a la pila. Saca el 2 primero. 
        // Desde 2, mete el 3. Saca el 3. Luego saca el 1.
        // Orden esperado: 0 -> 2 -> 3 -> 1
        assertEquals(0, resultado.getAt(0));
        assertEquals(2, resultado.getAt(1));
        assertEquals(3, resultado.getAt(2));
        assertEquals(1, resultado.getAt(3));
    }

    @Test
    public void testGrafoDesconectado() {
        // Escenario: Un grafo de 5 vértices donde el vértice 4 está totalmente aislado
        Grafo grafoAislado = new Grafo(5);
        grafoAislado.agregarArista(0, 1, 5.0);
        grafoAislado.agregarArista(1, 2, 5.0);
        grafoAislado.agregarArista(2, 3, 5.0);
        // Vértice 4 nunca se conecta
        
        Recorridos exploradorAislado = new Recorridos(grafoAislado);
        
        LinkedList<Integer> resultadoBFS = exploradorAislado.bfs(0);
        LinkedList<Integer> resultadoDFS = exploradorAislado.dfs(0);

        // Si es robusto, no debe fallar, pero solo debe encontrar 4 vértices, no 5
        assertEquals(4, resultadoBFS.size(), "El BFS no debe llegar al nodo desconectado");
        assertEquals(4, resultadoDFS.size(), "El DFS no debe llegar al nodo desconectado");
        
        // Verificamos que el nodo 4 efectivamente NO esté en la lista
        for(int i = 0; i < resultadoBFS.size(); i++) {
            assertNotEquals(4, resultadoBFS.getAt(i), "El nodo 4 es inalcanzable");
        }
    }
}