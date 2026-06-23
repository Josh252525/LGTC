package unitarias;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import estructuras.LinkedList;
import datos.Ciudad;
import datos.Vertice;
import datos.Arista;
import ui.DibujanteRutas;

public class DibujanteRutasTest {

    @BeforeAll
    public static void initJFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
        }
    }

    @Test
    public void testDibujoYLimpiezaSeguraDeRutas() {
        Pane lienzo = new Pane();
        
        // --- PREPARACIÓN ---
        // 1. Simulamos que Dev B ya dibujó la ciudad base
        Circle nodoBase = new Circle(10, 10, 5);
        nodoBase.setId("nodoBaseIntocable");
        lienzo.getChildren().add(nodoBase);

        Vertice[] vertices = {
            new Vertice(0, "DEPOT", 10, 10),
            new Vertice(1, "NORMAL", 20, 20)
        };
        Ciudad ciudad = new Ciudad(vertices, new Arista[0]);

        // 2. Preparamos una ruta matemática para un camión (Del 0 al 1)
        LinkedList<Integer> rutaCamion1 = new LinkedList<>();
        rutaCamion1.insert(0);
        rutaCamion1.insert(1);
        
        LinkedList<LinkedList<Integer>> rutasTotales = new LinkedList<>();
        rutasTotales.insert(rutaCamion1);

        // --- ACCIÓN 1: DIBUJAR ---
        DibujanteRutas.dibujarRutas(lienzo, rutasTotales, ciudad, 1.0);

        // --- VALIDACIÓN 1 ---
        // El lienzo debería tener el nodo base de Dev B (1) + la ruta de Dev C (1) = 2 elementos
        assertEquals(2, lienzo.getChildren().size(), "El lienzo debe contener el mapa base y la ruta superpuesta");

        Polyline trazo = null;
        for (int i = 0; i < lienzo.getChildren().size(); i++) {
            if (lienzo.getChildren().get(i) instanceof Polyline) {
                trazo = (Polyline) lienzo.getChildren().get(i);
            }
        }
        
        assertNotNull(trazo, "Debe existir un objeto Polyline trazado");
        assertEquals("capaRutaLogistica", trazo.getId(), "La ruta debe estar correctamente etiquetada");

        // --- ACCIÓN 2: LIMPIAR RUTAS VIEJAS ---
        DibujanteRutas.limpiarCapaDeRutas(lienzo);

        // --- VALIDACIÓN 2 ---
        assertEquals(1, lienzo.getChildren().size(), "Al limpiar las rutas, debe quedar exactamente 1 elemento (el mapa base)");
        assertEquals("nodoBaseIntocable", lienzo.getChildren().get(0).getId(), "El nodo base no debió ser borrado");
    }
}