package unitarias;

import estructuras.LinkedList;
import planner.EnrutadorMST;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/*
Documentación:
Batería de pruebas unitarias para el EnrutadorMST.
Valida el ruteo normal (TSP-Approximation) y blinda contra los casos extremos
como grafos desconectados, listas nulas, duplicados y depósitos erróneos.
*/
public class EnrutadorMSTTest {

    private EnrutadorMST enrutador;
    private double[][] matrizCiudadNormal;

    @BeforeEach
    public void setUp() {
        enrutador = new EnrutadorMST();
        
        // Creamos una matriz de ciudad de 4x4 simulada desde Floyd-Warshall
        // 0: Depósito
        // 1, 2, 3: Destinos
        matrizCiudadNormal = new double[][] {
            {0.0, 10.0, 20.0, 30.0},
            {10.0, 0.0,  5.0, 15.0},
            {20.0, 5.0,  0.0,  8.0},
            {30.0, 15.0, 8.0,  0.0}
        };
    }

    @Test
    public void testRutaNormalMatematica() {
        LinkedList<Integer> destinos = new LinkedList<>();
        destinos.insert(1);
        destinos.insert(2);
        destinos.insert(3);

        LinkedList<Integer> ruta = enrutador.generarRuta(0, destinos, matrizCiudadNormal);

        // El MST debería conectar: 0-1 (10), 1-2 (5), 2-3 (8)
        // El preorden DFS empezando en 0 debe visitar: 0 -> 1 -> 2 -> 3
        // La regla de negocio dice que se debe volver al depósito (0) al final.
        // Total de paradas esperadas: 5 (Depósito -> C1 -> C2 -> C3 -> Depósito)
        
        assertEquals(5, ruta.size(), "La ruta debe incluir todos los nodos más el regreso al depósito");
        assertEquals(0, ruta.getAt(0), "Debe empezar en el depósito");
        assertEquals(1, ruta.getAt(1), "Debe ir al nodo 1 primero por el MST");
        assertEquals(2, ruta.getAt(2), "Debe ir al nodo 2 después del 1");
        assertEquals(3, ruta.getAt(3), "Debe ir al nodo 3 al final");
        assertEquals(0, ruta.getAt(4), "Debe volver al depósito al terminar");
    }

    @Test
    public void testSinDestinosAsignados() {
        LinkedList<Integer> destinosVacios = new LinkedList<>();
        
        // Si el camión sale sin carga, debe reportar solo que se quedó en el depósito
        LinkedList<Integer> ruta = enrutador.generarRuta(0, destinosVacios, matrizCiudadNormal);

        assertEquals(1, ruta.size(), "Solo debe estar el depósito");
        assertEquals(0, ruta.getAt(0), "Debe ser el nodo 0");
    }

    @Test
    public void testDestinosDuplicadosYDepositoEnLista() {
        LinkedList<Integer> destinosSucios = new LinkedList<>();
        destinosSucios.insert(1);
        destinosSucios.insert(1); // ¡Duplicado!
        destinosSucios.insert(0); // ¡Pusieron el depósito como destino por error!
        destinosSucios.insert(2);

        // El enrutador debe limpiar la lista y tratarla como si solo fueran [1, 2]
        LinkedList<Integer> ruta = enrutador.generarRuta(0, destinosSucios, matrizCiudadNormal);

        // Ruta esperada: 0 -> 1 -> 2 -> 0 (Total 4 nodos)
        assertEquals(4, ruta.size(), "Debe limpiar duplicados y el depósito de la lista de pendientes");
        assertEquals(0, ruta.getAt(0));
        assertEquals(1, ruta.getAt(1));
        assertEquals(2, ruta.getAt(2));
        assertEquals(0, ruta.getAt(3));
    }

    @Test
    public void testCiudadInalcanzable() {
        // Matriz donde el nodo 1 está aislado (Infinito)
        double[][] matrizRota = {
            {0.0, Double.POSITIVE_INFINITY},
            {Double.POSITIVE_INFINITY, 0.0}
        };
        
        LinkedList<Integer> destinos = new LinkedList<>();
        destinos.insert(1);

        // Al intentar rutear a un nodo inalcanzable, el programa DEBE lanzar IllegalStateException
        assertThrows(IllegalStateException.class, () -> {
            enrutador.generarRuta(0, destinos, matrizRota);
        }, "Debe detenerse y lanzar excepción si se manda a una ciudad sin camino válido");
    }

    @Test
    public void testGuardiasDeSeguridadYExcepciones() {
        LinkedList<Integer> destinos = new LinkedList<>();
        destinos.insert(1);

        // 1. Matriz nula
        assertThrows(IllegalArgumentException.class, () -> {
            enrutador.generarRuta(0, destinos, null);
        }, "Debe fallar si la matriz de Floyd es nula");

        // 2. Destinos nulos
        assertThrows(IllegalArgumentException.class, () -> {
            enrutador.generarRuta(0, null, matrizCiudadNormal);
        }, "Debe fallar si la lista de destinos es nula");

        // 3. Depósito fuera del mapa (El mapa es 4x4, el límite es de 0 a 3)
        assertThrows(IllegalArgumentException.class, () -> {
            enrutador.generarRuta(99, destinos, matrizCiudadNormal);
        }, "Debe fallar si el depósito inicial no existe");
        
        // 4. Ciudad destino fuera del mapa
        destinos.insert(99);
        assertThrows(IllegalArgumentException.class, () -> {
            enrutador.generarRuta(0, destinos, matrizCiudadNormal);
        }, "Debe fallar si una ciudad destino no existe en la matriz");
    }
}