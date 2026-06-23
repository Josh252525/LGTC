package unitarias;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import datos.Ciudad;
import datos.Vertice;
import datos.Arista;
import ui.DibujanteMapa;

public class DibujanteMapaTest {

    // Enciende el motor interno de JavaFX antes de empezar
    @BeforeAll
    public static void initJFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // El Toolkit ya estaba inicializado, ignoramos el error
        }
    }

    @Test
    public void testDibujoCorrectoDeCiudad() {
        // 1. Preparar un lienzo vacío
        Pane lienzo = new Pane();
        
        // 2. Crear una mini-ciudad de prueba (2 ciudades, 1 calle)
        Vertice[] vertices = {
            new Vertice(0, "DEPOT", 10, 10),
            new Vertice(1, "NORMAL", 20, 20)
        };
        Arista[] aristas = { new Arista(0, 1, 15.0) };
        Ciudad ciudad = new Ciudad(vertices, aristas);

        // 3. Ejecutar el dibujante con un factor de escala de 2.0
        DibujanteMapa.dibujarCiudad(lienzo, ciudad, 2.0);

        // 4. Validar que se dibujaron exactamente 5 elementos
        // (1 Linea de calle + 2 Círculos de nodos + 2 Textos de IDs)
        assertEquals(5, lienzo.getChildren().size(), "El lienzo debe tener exactamente 5 elementos geométricos");

        // 5. Validar que la geometría y la escala se aplicaron bien (usamos for clásico sin java.util)
        int contadorCirculos = 0;
        int contadorLineas = 0;

        for (int i = 0; i < lienzo.getChildren().size(); i++) {
            Node figura = lienzo.getChildren().get(i);
            
            if (figura instanceof Circle) {
                contadorCirculos++;
                Circle c = (Circle) figura;
                // Si es el depósito (coordenada original 10x10 * escala 2.0 = 20)
                if (c.getCenterX() == 20.0) {
                    assertEquals(9.0, c.getRadius(), "El depósito debe tener un radio mayor (9.0)");
                }
            } else if (figura instanceof Line) {
                contadorLineas++;
            }
        }

        assertEquals(2, contadorCirculos, "Debe haber 2 ciudades dibujadas");
        assertEquals(1, contadorLineas, "Debe haber 1 calle dibujada");
    }
}