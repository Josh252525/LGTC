package unitarias;

import datos.Camion;
import datos.ExportadorCSV;
import datos.JsonParser;
import datos.Paquete;
import estructuras.LinkedList;
import planner.CamionPlanificado;
import planner.ResultadoLogistica;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class ExportadorCSVTest {

    private final String RUTA_ARCHIVO_PRUEBA = "test_reporte_logistica.csv";

    @BeforeEach
    public void setUp() {
        // Simulamos el diccionario del parser para que no de NullPointerException al formatear rutas
        JsonParser.diccionarioNombres = new String[]{"DEPOT", "Tienda_A", "Tienda_B"};
    }

    @AfterEach
    public void tearDown() {
        // Limpiamos el archivo temporal después de la prueba
        File archivo = new File(RUTA_ARCHIVO_PRUEBA);
        if (archivo.exists()) {
            archivo.delete();
        }
    }

    @Test
    public void testGeneracionArchivoCSV() {
        // 1. Preparamos datos ficticios
        CamionPlanificado c1 = new CamionPlanificado(new Camion("C01", 100.0));
        c1.intentarCargar(new Paquete("P01", 1, 50.0, 1));
        
        CamionPlanificado[] flota = { c1 };
        
        LinkedList<Paquete> inalcanzables = new LinkedList<>();
        inalcanzables.insert(new Paquete("P_MALO", 2, 10.0, 1));
        
        LinkedList<Paquete> noAsignados = new LinkedList<>();
        
        ResultadoLogistica resultado = new ResultadoLogistica(inalcanzables, noAsignados, flota);

        // Rutas y tiempos ficticios
        LinkedList<LinkedList<Integer>> rutasMST = new LinkedList<>();
        LinkedList<Integer> rutaM = new LinkedList<>();
        rutaM.insert(0); rutaM.insert(1); rutaM.insert(0);
        rutasMST.insert(rutaM);

        LinkedList<LinkedList<Integer>> rutasNN = new LinkedList<>();
        rutasNN.insert(rutaM);

        LinkedList<Long> tiempos = new LinkedList<>();
        tiempos.insert(1500L);

        // 2. Ejecutamos la exportación
        ExportadorCSV.generarReporte(RUTA_ARCHIVO_PRUEBA, resultado, rutasMST, rutasNN, tiempos, tiempos);

        // 3. Verificamos que el archivo se haya creado correctamente
        File archivoGenerado = new File(RUTA_ARCHIVO_PRUEBA);
        assertTrue(archivoGenerado.exists(), "El archivo CSV debió ser creado en el disco");
        assertTrue(archivoGenerado.length() > 0, "El archivo CSV no debería estar vacío");
    }
}