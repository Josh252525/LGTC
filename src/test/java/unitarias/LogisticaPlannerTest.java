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

        // El P03 (60kg) y P02 (30kg) se ajustan perfecto en C1 (100kg), dejando 10kg libres.
        assertEquals(10.0, resultado.flota()[0].getCapacidadRestante(), "C1 debió empacar P03 y P02, quedando con 10kg libres");
        
        // El P01 (40kg) se va a C2 (50kg), dejando 10kg libres.
        assertEquals(10.0, resultado.flota()[1].getCapacidadRestante(), "C2 debió empacar P01, quedando con 10kg libres");
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