package unitarias;

import datos.Camion;
import datos.Paquete;
import estructuras.LinkedList;
import planner.LogisticaPlanner;
import planner.ResultadoLogistica;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LogisticaPlannerTest {

    private LogisticaPlanner planner;

    @BeforeEach
    public void setUp() {
        planner = new LogisticaPlanner();
    }

    @Test
    public void testOrdenamientoGeograficoYBestFit() {
        // 1. Preparamos los camiones
        Camion[] camiones = {
            new Camion("C1", 100.0),
            new Camion("C2", 50.0)
        };

        // 2. Preparamos los paquetes (ID, Destino, Peso, Prioridad)
        Paquete[] paquetes = {
            new Paquete("P01", 3, 40.0, 2), // Va al destino 3
            new Paquete("P02", 2, 30.0, 1), // Va al destino 2
            new Paquete("P03", 2, 60.0, 1)  // Va al destino 2, es más pesado
        };

        // 3. Simulamos la Ruta Maestra (El camión pasará primero por el Destino 2 y luego por el 3)
        LinkedList<Integer> rutaMaestra = new LinkedList<>();
        rutaMaestra.insert(2);
        rutaMaestra.insert(3);

        LinkedList<Paquete> inalcanzables = new LinkedList<>();

        // 4. Ejecutamos
        ResultadoLogistica resultado = planner.asignarCarga(paquetes, inalcanzables, camiones, rutaMaestra);

        // 5. Verificaciones
        assertNotNull(resultado, "El resultado no debería ser nulo");
        assertEquals(0, resultado.noAsignados().size(), "Todos los paquetes debieron ser asignados");

        // El paquete P03 (Destino 2, Prioridad 1, 60kg) debió procesarse primero e ir al Camion C1 (único donde cabe)
        assertEquals(40.0, resultado.flota()[0].getCapacidadRestante(), "C1 debió quedar con 40kg libres (100 - 60)");
        
        // El P02 (Destino 2, Prioridad 1, 30kg) debió procesarse segundo e ir al C2 (Ajuste más apretado: 50 - 30 = 20 libres vs C1: 40 - 30 = 10 libres)
        // Ojo: En Best Fit, busca el menor espacio sobrante. En C1 sobra 10, en C2 sobra 20. Así que va a C1.
        // Verifiquemos cómo quedó C1: 100 - 60(P03) - 30(P02) = 10 libres.
        // P01 (Destino 3, Prioridad 2, 40kg) no cabe en C1 (sobran 10). Va a C2 (sobran 10).
        assertEquals(10.0, resultado.flota()[0].getCapacidadRestante(), "C1 debió empacar P03 y P02");
        assertEquals(10.0, resultado.flota()[1].getCapacidadRestante(), "C2 debió empacar P01");
    }

    @Test
    public void testPaqueteRechazadoPorSobrepeso() {
        Camion[] camiones = { new Camion("C1", 50.0) };
        Paquete[] paquetes = {
            new Paquete("P_NORMAL", 1, 20.0, 1),
            new Paquete("P_GIGANTE", 1, 100.0, 1) // 100kg en camión de 50kg
        };

        LinkedList<Integer> rutaMaestra = new LinkedList<>();
        rutaMaestra.insert(1);

        ResultadoLogistica resultado = planner.asignarCarga(paquetes, new LinkedList<>(), camiones, rutaMaestra);

        // El paquete de 20kg debió entrar, el de 100kg debió irse a no asignados
        assertEquals(1, resultado.noAsignados().size(), "Un paquete debió ser rechazado por sobrepeso");
        assertEquals("P_GIGANTE", resultado.noAsignados().getAt(0).id(), "El paquete rechazado debe ser el ID P_GIGANTE");
        assertEquals(30.0, resultado.flota()[0].getCapacidadRestante(), "El camión debió quedar con 30kg libres");
    }
}