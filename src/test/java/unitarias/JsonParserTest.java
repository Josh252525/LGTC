package unitarias;

import datos.JsonParser;
import datos.ConfigLogisTEC;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class JsonParserTest {

    private JsonParser parser;

    @BeforeEach
    public void setUp() {
        parser = new JsonParser();
    }

    @Test
    public void testArchivoNoEncontrado() {
        // Le pasamos una ruta que sabemos que no existe
        ConfigLogisTEC config = parser.parsearArchivo("ruta/inventada/que/no/existe.json");
        
        // Tu código tiene un try-catch que retorna null en caso de error, validamos que funcione
        assertNull(config, "El parser debe retornar null y no crashear si el archivo no existe");
    }

    @Test
    public void testJsonCorruptoInvalido(@TempDir Path tempDir) throws Exception {
        // Creamos un archivo temporal con un JSON mal escrito
        Path archivoMalo = tempDir.resolve("corrupto.json");
        Files.writeString(archivoMalo, "{ \"ciudad\": [ esto no es json válido... ");

        ConfigLogisTEC config = parser.parsearArchivo(archivoMalo.toString());
        
        // Gson debería lanzar un JsonSyntaxException, tu catch lo atrapa y retorna null
        assertNull(config, "El parser debe retornar null si el formato del JSON es inválido");
    }

    @Test
    public void testParseoExitosoBasico(@TempDir Path tempDir) throws Exception {
        // Simulamos un JSON perfectamente estructurado
        String jsonValido = """
        {
            "ciudad": {
                "vertices": [ {"id": 0, "tipo": "DEPOT"} ],
                "aristas": []
            },
            "paquetes": [],
            "camiones": []
        }
        """;
        Path archivoBueno = tempDir.resolve("bueno.json");
        Files.writeString(archivoBueno, jsonValido);

        ConfigLogisTEC config = parser.parsearArchivo(archivoBueno.toString());
        
        assertNotNull(config, "El config no debe ser null con un JSON válido");
        assertNotNull(config.ciudad(), "La ciudad debió ser parseada");
        assertEquals(1, config.ciudad().vertices().length, "Debe haber 1 vértice parseado");
        assertEquals("DEPOT", config.ciudad().vertices()[0].tipo(), "El tipo del vértice debe ser DEPOT");
    }
}