package ui;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.Font;

import datos.Ciudad;
import datos.Vertice;
import datos.Arista;

public class DibujanteMapa {

    /**
     * Dibuja la red estática de la ciudad (Nodos y Calles).
     * @param mapaPane El contenedor donde se va a dibujar.
     * @param ciudad El DTO con los vértices y aristas.
     * @param factorEscala Multiplicador para escalar las coordenadas a la pantalla.
     */
    public static void dibujarCiudad(Pane mapaPane, Ciudad ciudad, double factorEscala) {
        
        // 1. DIBUJAR LAS CALLES (Líneas de fondo)
        // Se dibujan primero para que queden por debajo de las ciudades
        for (Arista arista : ciudad.aristas()) {
            Vertice origen = buscarVerticePorId(ciudad.vertices(), arista.u());
            Vertice destino = buscarVerticePorId(ciudad.vertices(), arista.v());
            
            if (origen != null && destino != null) {
                Line calle = new Line(
                    origen.x() * factorEscala, origen.y() * factorEscala,
                    destino.x() * factorEscala, destino.y() * factorEscala
                );
                
                // Un gris oscuro para que no sature visualmente el modo oscuro
                calle.setStroke(Color.web("#555555")); 
                calle.setStrokeWidth(2.0);
                mapaPane.getChildren().add(calle);
            }
        }
        
        // 2. DIBUJAR LAS CIUDADES (Círculos) Y SUS ETIQUETAS
        for (Vertice v : ciudad.vertices()) {
            Circle ciudadGrafica = new Circle(v.x() * factorEscala, v.y() * factorEscala, 6.0);
            
            // Lógica para diferenciar el Depósito de las ciudades normales
            if (v.tipo().equalsIgnoreCase("DEPOT")) {
                ciudadGrafica.setFill(Color.RED);
                ciudadGrafica.setRadius(9.0); // El depósito es más grande
            } else {
                ciudadGrafica.setFill(Color.LIGHTGRAY);
            }
            
            // 3. TEXTO NEÓN (ID de la ciudad)
            Text etiquetaId = new Text(datos.JsonParser.diccionarioNombres[v.id()]);
            etiquetaId.setX((v.x() * factorEscala) + 8); // Desplazado un poco a la derecha
            etiquetaId.setY((v.y() * factorEscala) - 8); // Desplazado un poco hacia arriba
            etiquetaId.setFill(Color.BLACK);
            etiquetaId.setFont(Font.font("Arial", 12));
            etiquetaId.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            // Agregamos tanto el círculo como el texto al panel
            mapaPane.getChildren().addAll(ciudadGrafica, etiquetaId);
        }
    }

    // Método auxiliar iterativo para evitar el uso de java.util.HashMap
    private static Vertice buscarVerticePorId(Vertice[] vertices, int id) {
        for (Vertice v : vertices) {
            if (v.id() == id) { 
                return v; 
            }
        }
        return null;
    }
}