package datos;

import com.google.gson.Gson;
import java.io.FileReader;
import java.io.Reader;

public class JsonParser {

    /**
     * Lee el archivo JSON de configuración y lo convierte en un objeto manipulable en Java.
     * @param rutaArchivo La ruta donde se encuentra el archivo (ej. "data/grafo_30_vertices.json")
     * @return Objeto ConfigLogisTEC con todos los datos mapeados, o null si ocurre un error.
     */
    public ConfigLogisTEC parsearArchivo(String rutaArchivo) {
        Gson gson = new Gson();
        
        try (Reader reader = new FileReader(rutaArchivo)) {
            // Gson lee el texto y lo inyecta automáticamente en los Records
            ConfigLogisTEC configuracion = gson.fromJson(reader, ConfigLogisTEC.class);
            
            System.out.println("¡JSON de LogísTEC parseado con éxito!");
            return configuracion;
            
        } catch (Exception e) {
            System.err.println("Error crítico al leer el archivo JSON: " + e.getMessage());
            return null;
        }
    }
}
