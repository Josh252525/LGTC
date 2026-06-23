package unitarias;

import datos.*;
import estructuras.Grafo;
import estructuras.LinkedList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ValidadorPaquetesTest {

    @Test
    public void testEncontrarDepositoExitoso() {
        // Configuramos una ciudad de prueba con el nodo 1 como depósito.
        // Agregamos ,0,0 al final para cumplir con la firma (id, tipo, x, y)
        Vertice[] vertices = {
            new Vertice(0, "NORMAL", 0, 0),
            new Vertice(1, "DEPOT", 0, 0), // El depósito está en el ID 1
            new Vertice(2, "NORMAL", 0, 0)
        };
        Ciudad ciudad = new Ciudad(vertices, new Arista[0]);
        ConfigLogisTEC config = new ConfigLogisTEC(ciudad, new Paquete[0], new Camion[0]);

        ValidadorPaquetes validador = new ValidadorPaquetes(config);
        
        assertEquals(1, validador.encontrarDeposito(), "Debe encontrar que el depósito es el ID 1");
    }

    @Test
    public void testCiudadSinDeposito() {
        // Ciudad pura de nodos normales, se les olvidó poner el DEPOT
        Vertice[] vertices = { new Vertice(0, "NORMAL", 0, 0), new Vertice(1, "NORMAL", 0, 0) };
        Ciudad ciudad = new Ciudad(vertices, new Arista[0]);
        ConfigLogisTEC config = new ConfigLogisTEC(ciudad, new Paquete[0], new Camion[0]);

        ValidadorPaquetes validador = new ValidadorPaquetes(config);
        
        assertThrows(IllegalStateException.class, () -> {
            validador.encontrarDeposito();
        }, "Debe lanzar IllegalStateException si la ciudad no tiene un DEPOT");
    }

    @Test
    public void testConstruccionGrafo() {
        Vertice[] vertices = { new Vertice(0, "DEPOT", 0, 0), new Vertice(1, "NORMAL", 0, 0) };
        Arista[] aristas = { new Arista(0, 1, 15.5) };
        Ciudad ciudad = new Ciudad(vertices, aristas);
        ConfigLogisTEC config = new ConfigLogisTEC(ciudad, new Paquete[0], new Camion[0]);

        ValidadorPaquetes validador = new ValidadorPaquetes(config);
        Grafo grafo = validador.construirGrafo();

        assertEquals(2, grafo.getCantidadVertices(), "El grafo debe tener 2 vértices");
        assertEquals(1, grafo.getVecinos(0).size(), "El vértice 0 debe tener 1 vecino");
        assertEquals(1, grafo.getVecinos(1).size(), "El vértice 1 debe tener 1 vecino (es no dirigido)");
    }

    @Test
    public void testValidacionPaquetesConInalcanzables() {
        // --- 1. Preparar la topología ---
        Vertice[] vertices = {
            new Vertice(0, "DEPOT", 0, 0),
            new Vertice(1, "NORMAL", 0, 0),
            new Vertice(2, "NORMAL", 0, 0) // El nodo 2 estará aislado (sin calles)
        };
        
        // Solo hay camino del 0 al 1. El 2 está desconectado.
        Arista[] aristas = { new Arista(0, 1, 10.0) };
        Ciudad ciudad = new Ciudad(vertices, aristas);

        // --- 2. Preparar los paquetes ---
        Paquete pBueno = new Paquete("P01", 1, 5.0, 1);  // Va hacia el nodo 1 (Alcanzable)
        Paquete pMalo = new Paquete("P02", 2, 5.0, 1);   // Va hacia el nodo 2 (Inalcanzable)
        Paquete[] paquetes = { pBueno, pMalo };

        ConfigLogisTEC config = new ConfigLogisTEC(ciudad, paquetes, new Camion[0]);
        ValidadorPaquetes validador = new ValidadorPaquetes(config);

        // --- 3. Ejecutar Validación ---
        LinkedList<Paquete> rechazados = validador.validarPaquetes();

        // --- 4. Comprobar resultados ---
        assertEquals(1, rechazados.size(), "Debe haber exactamente 1 paquete rechazado");
        
        // Obtenemos el primer (y único) paquete de la lista enlazada personalizada
        Paquete rechazado = rechazados.getAt(0);
        assertEquals(2, rechazado.id(), "El paquete rechazado debe ser el ID 2 porque iba al nodo aislado");
    }
}