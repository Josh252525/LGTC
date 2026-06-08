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
    public void testOrdenamientoYBestFitNormal() {
        // 1. Preparamos camiones (Capacidades: 100 y 50)
        Camion[] camiones = {
            new Camion("C1", 100.0),
            new Camion("C2", 50.0)
        };

        // 2. Preparamos paquetes
        // Record: id, destino, peso, prioridad (1 es mayor prioridad)
        Paquete[] paquetes = {
            new Paquete(1, 2, 40.0, 2),  // Prioridad 2, Peso 40
            new Paquete(2, 3, 30.0, 1),  // Prioridad 1, Peso 30 (Debe ir primero)
            new Paquete(3, 4, 60.0, 1)   // Prioridad 1, Peso 60 (Debe ir MÁS primero por ser pesado)
        };

        LinkedList<Paquete> inalcanzables = new LinkedList<>();

        // 3. Ejecutamos el planner
        ResultadoLogistica resultado = planner.asignarCarga(paquetes, inalcanzables, camiones);

        // 4. Validamos que no haya rechazados
        assertEquals(0, resultado.noAsignados().size(), "Todos los paquetes debieron caber");

        // 5. Validamos la lógica del Best Fit
        // Paquete 3 (60kg) entra en C1 (100kg -> sobran 40kg)
        // Paquete 2 (30kg) entra en C2 (50kg -> sobran 20kg) O C1 (sobran 10kg). Best-Fit elige C1 porque deja MENOS espacio sobrante.
        // Paquete 1 (40kg) entra en C2 (50kg -> sobran 10kg).
        
        // Verificamos el camión 1 (C1)
        assertEquals(10.0, resultado.flota()[0].getCapacidadRestante());
        assertEquals(2, resultado.flota()[0].getPaquetesCargados().size());

        // Verificamos el camión 2 (C2)
        assertEquals(10.0, resultado.flota()[1].getCapacidadRestante());
        assertEquals(1, resultado.flota()[1].getPaquetesCargados().size());
    }

    @Test
    public void testPaqueteDemasiadoPesado() {
        Camion[] camiones = { new Camion("C1", 50.0) };
        Paquete[] paquetes = {
            new Paquete(1, 2, 20.0, 1),
            new Paquete(2, 3, 100.0, 1) // Paquete de 100kg en camión de 50kg
        };

        ResultadoLogistica resultado = planner.asignarCarga(paquetes, new LinkedList<>(), camiones);

        // El paquete de 20kg debió entrar, el de 100kg debió irse a no asignados
        assertEquals(1, resultado.noAsignados().size(), "Un paquete debió ser rechazado por sobrepeso");
        assertEquals(2, resultado.noAsignados().getAt(0).id(), "El paquete rechazado debe ser el ID 2");
        assertEquals(30.0, resultado.flota()[0].getCapacidadRestante());
    }

    @Test
    public void testEmpatePerfecto() {
        // Si un camión tiene exactamente el tamaño del paquete, el sobrante es 0 (ajuste perfecto)
        Camion[] camiones = {
            new Camion("C1", 100.0),
            new Camion("C2", 40.0)
        };
        Paquete[] paquetes = { new Paquete(1, 2, 40.0, 1) };

        ResultadoLogistica resultado = planner.asignarCarga(paquetes, new LinkedList<>(), camiones);

        // El Best-Fit debe preferir C2 (sobra 0) sobre C1 (sobra 60)
        assertEquals(0.0, resultado.flota()[1].getCapacidadRestante(), "El camión 2 debe estar lleno");
        assertEquals(100.0, resultado.flota()[0].getCapacidadRestante(), "El camión 1 debe estar intacto");
    }
}