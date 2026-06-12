package unitarias;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import planner.EnrutadorNearestNeighbor;
import estructuras.LinkedList;

public class EnrutadorNearestNeighborTest {

    @Test
    public void testRutaNormal() {

        double[][] floyd = {
            {0,10,5,6},
            {10,0,3,2},
            {5,3,0,1},
            {6,2,1,0}
        };

        LinkedList<Integer> destinos = new LinkedList<>();
        destinos.insert(1);
        destinos.insert(2);
        destinos.insert(3);

        EnrutadorNearestNeighbor nn = new EnrutadorNearestNeighbor();

        LinkedList<Integer> ruta =
                nn.generarRuta(0, destinos, floyd);

        assertEquals(5, ruta.size());

        assertEquals(0, ruta.getAt(0));
        assertEquals(2, ruta.getAt(1));
        assertEquals(3, ruta.getAt(2));
        assertEquals(1, ruta.getAt(3));
        assertEquals(0, ruta.getAt(4));
    }

    @Test
    public void testSinEntregas() {

        double[][] floyd = {
            {0}
        };

        LinkedList<Integer> destinos = new LinkedList<>();

        EnrutadorNearestNeighbor nn =
                new EnrutadorNearestNeighbor();

        LinkedList<Integer> ruta =
                nn.generarRuta(0, destinos, floyd);

        assertEquals(1, ruta.size());
        assertEquals(0, ruta.getAt(0));
    }

    @Test
    public void testUnDestino() {

        double[][] floyd = {
            {0,4,7,2},
            {4,0,3,5},
            {7,3,0,1},
            {2,5,1,0}
        };

        LinkedList<Integer> destinos =
                new LinkedList<>();

        destinos.insert(3);

        EnrutadorNearestNeighbor nn =
                new EnrutadorNearestNeighbor();

        LinkedList<Integer> ruta =
                nn.generarRuta(0, destinos, floyd);

        assertEquals(3, ruta.size());

        assertEquals(0, ruta.getAt(0));
        assertEquals(3, ruta.getAt(1));
        assertEquals(0, ruta.getAt(2));
    }

    @Test
    public void testDestinosDuplicados() {

        double[][] floyd = {
            {0,1,2},
            {1,0,1},
            {2,1,0}
        };

        LinkedList<Integer> destinos =
                new LinkedList<>();

        destinos.insert(2);
        destinos.insert(2);
        destinos.insert(2);

        EnrutadorNearestNeighbor nn =
                new EnrutadorNearestNeighbor();

        LinkedList<Integer> ruta =
                nn.generarRuta(0, destinos, floyd);

        assertNotNull(ruta);

        assertEquals(5, ruta.size());

        assertEquals(0, ruta.getAt(0));
        assertEquals(2, ruta.getAt(1));
        assertEquals(2, ruta.getAt(2));
        assertEquals(2, ruta.getAt(3));
        assertEquals(0, ruta.getAt(4));
    }

    @Test
    public void testMatrizNula() {

        EnrutadorNearestNeighbor nn =
                new EnrutadorNearestNeighbor();

        LinkedList<Integer> destinos =
                new LinkedList<>();

        assertThrows(
            IllegalArgumentException.class,
            () -> nn.generarRuta(0, destinos, null)
        );
    }

    @Test
    public void testMatrizVacia() {

        EnrutadorNearestNeighbor nn =
                new EnrutadorNearestNeighbor();

        LinkedList<Integer> destinos =
                new LinkedList<>();

        double[][] floyd = new double[0][0];

        assertThrows(
            IllegalArgumentException.class,
            () -> nn.generarRuta(0, destinos, floyd)
        );
    }
}