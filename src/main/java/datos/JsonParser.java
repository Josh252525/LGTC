package datos;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.FileReader;
import java.io.Reader;

/**
 * Lee el archivo JSON y actúa como un TRADUCTOR.
 * Convierte los IDs de texto (Ej: "Oficina_BN") a IDs numéricos (Ej: 1)
 * para que los algoritmos matemáticos no exploten.
 */
public class JsonParser {

    // Diccionario global para traducir de vuelta los números a nombres al final
    public static String[] diccionarioNombres;

    public ConfigLogisTEC parsearArchivo(String rutaArchivo) {
        
        try (Reader reader = new FileReader(rutaArchivo)) {
            
            // 1. Interceptamos el JSON antes de que Gson intente mapearlo a los DTOs
            JsonObject root = com.google.gson.JsonParser.parseReader(reader).getAsJsonObject();

            // 2. Extraemos los vértices para armar nuestro diccionario
            JsonArray vertices = root.getAsJsonObject("ciudad").getAsJsonArray("vertices");
            int numVertices = vertices.size();
            diccionarioNombres = new String[numVertices];

            for (int i = 0; i < numVertices; i++) {
                JsonObject v = vertices.get(i).getAsJsonObject();
                String stringId = v.get("id").getAsString();
                
                // Guardamos el nombre real en el diccionario (Índice = ID numérico)
                diccionarioNombres[i] = stringId; 
                
                // Reemplazamos la palabra por el número entero en el JSON en memoria
                v.addProperty("id", i);
            }

            // 3. Traducimos las calles (Aristas u, v)
            JsonArray aristas = root.getAsJsonObject("ciudad").getAsJsonArray("aristas");
            for (int i = 0; i < aristas.size(); i++) {
                JsonObject a = aristas.get(i).getAsJsonObject();
                String u = a.get("u").getAsString();
                String v = a.get("v").getAsString();
                
                // Cambiamos texto por número
                a.addProperty("u", buscarIdEntero(u));
                a.addProperty("v", buscarIdEntero(v));
            }

            // 4. Traducimos los destinos de los paquetes
            JsonArray paquetes = root.getAsJsonArray("paquetes");
            for (int i = 0; i < paquetes.size(); i++) {
                JsonObject p = paquetes.get(i).getAsJsonObject();
                String destino = p.get("destino").getAsString();
                
                // Cambiamos texto por número
                p.addProperty("destino", buscarIdEntero(destino));
            }

            // 5. Ahora que el JSON tiene puros números, hacemos el mapeo normal y seguro
            Gson gson = new Gson();
            ConfigLogisTEC configuracion = gson.fromJson(root, ConfigLogisTEC.class);
            
            System.out.println("✅ JSON traducido a formato numérico y parseado con éxito.");
            return configuracion;
            
        } catch (Exception e) {
            System.err.println("Error crítico al leer/traducir el archivo JSON: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Busca la palabra en el diccionario y retorna su ID numérico asociado.
     */
    private int buscarIdEntero(String nombreString) {
        for (int i = 0; i < diccionarioNombres.length; i++) {
            if (diccionarioNombres[i].equals(nombreString)) {
                return i;
            }
        }
        return -1; // Por si el profesor pone un destino que no existe en el mapa
    }
}