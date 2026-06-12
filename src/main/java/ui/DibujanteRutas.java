package ui;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Polyline;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import estructuras.LinkedList;
import datos.Vertice;
import datos.Ciudad;

public class DibujanteRutas {

    // Paleta de colores profesionales para identificar a cada camión de forma independiente
    private static final Color[] COLORES_FLOTA = {
        Color.web("#007BFF", 0.7), // Camión 0: Azul traslúcido
        Color.web("#28A745", 0.7), // Camión 1: Verde traslúcido
        Color.web("#FD7E14", 0.7), // Camión 2: Naranja traslúcido
        Color.web("#6F42C1", 0.7), // Camión 3: Morado traslúcido
        Color.web("#DC3545", 0.7)  // Camión 4: Rojo traslúcido
    };

    /**
     * @param mapaPane       El contenedor central del FXML.
     * @param rutasFlota     Lista con las rutas de cada camión (LinkedList de LinkedList de enteros).
     * @param ciudad         DTO de la ciudad para saber dónde vive físicamente cada ID en coordenadas X,Y.
     * @param factorEscala   Multiplicador geométrico para escalar el mapa a la pantalla.
     */
    public static void dibujarRutas(Pane mapaPane, LinkedList<LinkedList<Integer>> rutasFlota, Ciudad ciudad, double factorEscala) {
        
        // 1. Habla con su propio método interno para borrar los trazos de camiones anteriores
        // ¡Así evitamos pintar rutas encima de rutas viejas!
        limpiarCapaDeRutas(mapaPane);

        Vertice[] catalogoCiudades = ciudad.vertices();

        // 2. Iteramos sobre la lista de rutas (una por cada camión de la empresa)
        for (int i = 0; i < rutasFlota.size(); i++) {
            LinkedList<Integer> rutaDeEsteCamion = rutasFlota.getAt(i);

            // Si el camión no se usó o no tiene paradas, lo ignoramos para ahorrar procesamiento
            if (rutaDeEsteCamion == null || rutaDeEsteCamion.size() <= 1) {
                continue;
            }

            // 3. Instancia un objeto gráfico Polyline (una cadena de líneas conectadas)
            Polyline trazoCamion = new Polyline();
            
            // 🛡️ EL PASAPORTE CLAVE: Le ponemos un ID único al objeto gráfico
            // Esto permite que el limpiador lo identifique después sin tocar los círculos de la ciudad
            trazoCamion.setId("capaRutaLogistica"); 

            // 4. Configuración estética del camino del camión
            Color colorAsignado = COLORES_FLOTA[i % COLORES_FLOTA.length];
            trazoCamion.setStroke(colorAsignado);
            
         // EFECTO NEÓN
            DropShadow resplandor = new DropShadow();
            resplandor.setColor(colorAsignado);
            resplandor.setRadius(15);   // Qué tan difuminada es la luz
            resplandor.setSpread(0.4);  // Qué tan fuerte es la luz
            trazoCamion.setEffect(resplandor);
            trazoCamion.setStrokeWidth(5.0);               // Línea gruesa para que resalte
            trazoCamion.setStrokeLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND); // Curvas suaves en las esquinas

            // 5. Traducimos los IDs matemáticos a puntos en el espacio bidimensional de JavaFX
            for (int j = 0; j < rutaDeEsteCamion.size(); j++) {
                int idCiudadActual = rutaDeEsteCamion.getAt(j);
                
                // Busca el vértice en el catálogo para extraer sus variables de coordenadas x, y
                Vertice ciudadFisica = buscarCiudadPorId(catalogoCiudades, idCiudadActual);
                
                if (ciudadFisica != null) {
                    // Multiplicamos por la escala. Si X=10 y Escala=15, se dibujará en el píxel 150.
                    double pixelX = (double) ciudadFisica.x() * factorEscala;
                    double pixelY = (double) ciudadFisica.y() * factorEscala;
                    
                    // Inyecta el punto en el Polyline de JavaFX
                    trazoCamion.getPoints().addAll(pixelX, pixelY);
                }
            }

            // 6. Inserta el Polyline terminado dentro del Pane de la ventana para que el ojo humano lo vea
            mapaPane.getChildren().add(trazoCamion);
        }
    }

    /**
     * Borra los caminos de colores de la pantalla de forma segura.
     */
    public static void limpiarCapaDeRutas(Pane mapaPane) {
        // Condición lambda: "Elimina del Pane cualquier nodo gráfico cuyo ID sea exactamente 'capaRutaLogistica'"
        // Esto protege los círculos del Dev B, ya que ellos no tienen este ID.
        mapaPane.getChildren().removeIf(grafico -> "capaRutaLogistica".equals(grafico.getId()));
    }

    // Método de soporte iterativo para encontrar coordenadas sin usar HashMap
    private static Vertice buscarCiudadPorId(Vertice[] vertices, int id) {
        for (Vertice v : vertices) {
            if (v.id() == id) {
                return v;
            }
        }
        return null;
    }
}