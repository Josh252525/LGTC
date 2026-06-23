package ui;

import estructuras.LinkedList;
import estructuras.Grafo;
import planner.CamionPlanificado;
import planner.ResultadoLogistica;
import datos.JsonParser;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class GeneradorEstadisticas {

    public static void mostrarPanel(ResultadoLogistica resultado, 
                                    LinkedList<LinkedList<Integer>> rutasMST, 
                                    LinkedList<LinkedList<Integer>> rutasNN, 
                                    LinkedList<Long> tiemposMST,
                                    LinkedList<Long> tiemposNN,
                                    double[][] matrizFloyd,
                                    Grafo grafoCiudad) {
        
        StringBuilder sb = new StringBuilder();
        
        sb.append("=========================================================\n");
        sb.append("      REPORTE LOGÍSTICO Y BENCHMARKS DETALLADO           \n");
        sb.append("=========================================================\n\n");

        // =========================================================
        // 1. ANÁLISIS DE LA FLOTA Y CARGA (BEST-FIT)
        // =========================================================
        sb.append("📦 ANÁLISIS DE EMPAQUETADO (BEST-FIT) \n");
        sb.append("---------------------------------------------------------\n");
        
        double cargaTransportadaGlobal = 0.0;
        double capacidadTotalGlobal = 0.0;
        
        for (int i = 0; i < resultado.flota().length; i++) {
            CamionPlanificado camion = resultado.flota()[i];
            double capacidadTotal = camion.getCamionBase().capacidad();
            double ocupado = capacidadTotal - camion.getCapacidadRestante();
            
            cargaTransportadaGlobal += ocupado;
            capacidadTotalGlobal += capacidadTotal;
            
            double eficiencia = (capacidadTotal > 0) ? (ocupado / capacidadTotal) * 100.0 : 0.0;
            
            sb.append(String.format("Camión: %s | Carga: %.2f / %.2f kg (Eficiencia: %.1f %%)\n", 
                    camion.getCamionBase().id(), ocupado, capacidadTotal, eficiencia));
            
            if (camion.getPaquetesCargados().size() > 0) {
                sb.append("   -> Paquetes a bordo: ");
                for (int j = 0; j < camion.getPaquetesCargados().size(); j++) {
                    sb.append(camion.getPaquetesCargados().getAt(j).id()).append(" ");
                }
                sb.append("\n");
            } else {
                sb.append("   -> [Camión Vacío]\n");
            }
        }
        
        if (capacidadTotalGlobal > 0) {
            double eGlobal = (cargaTransportadaGlobal / capacidadTotalGlobal) * 100.0;
            sb.append(String.format("\n📊 Carga Total Transportada: %.2f de %.2f kg (Eficiencia Global: %.1f %%)\n\n", 
                    cargaTransportadaGlobal, capacidadTotalGlobal, eGlobal));
        }

        // =========================================================
        // 2. ANÁLISIS DE ENRUTAMIENTO POR CAMIÓN
        // =========================================================
        sb.append("🛣️ BENCHMARKS DE ENRUTAMIENTO POR CAMIÓN \n");
        sb.append("---------------------------------------------------------\n");

        double distanciaGlobalMST = 0.0;
        double distanciaGlobalNN = 0.0;

        for (int i = 0; i < resultado.flota().length; i++) {
            CamionPlanificado camion = resultado.flota()[i];
            sb.append("🚚 ").append(camion.getCamionBase().id()).append(":\n");

            if (camion.getPaquetesCargados().size() > 0) {
                LinkedList<Integer> rutaMST = rutasMST.getAt(i);
                LinkedList<Integer> rutaNN = rutasNN.getAt(i);
                long tMST = tiemposMST.getAt(i);
                long tNN = tiemposNN.getAt(i);

                double distMST = calcularDistancia(rutaMST, matrizFloyd);
                double distNN = calcularDistancia(rutaNN, matrizFloyd);

                distanciaGlobalMST += distMST;
                distanciaGlobalNN += distNN;

                sb.append("   [Estrategia MST (Prim + DFS)]\n");
                sb.append("      - Distancia: ").append(String.format("%.2f km\n", distMST));
                sb.append("      - Tiempo CPU: ").append(tMST).append(" ns\n");
                sb.append("      - Ruta:      ").append(formatearRuta(rutaMST)).append("\n\n");

                sb.append("   [Estrategia NN (Vecino Cercano)]\n");
                sb.append("      - Distancia: ").append(String.format("%.2f km\n", distNN));
                sb.append("      - Tiempo CPU: ").append(tNN).append(" ns\n");
                sb.append("      - Ruta:      ").append(formatearRuta(rutaNN)).append("\n");
            } else {
                sb.append("   -> Sin ruta asignada (Camión vacío).\n");
            }
            sb.append("\n");
        }

        // =========================================================
        // 3. RESUMEN DE PAQUETES RECHAZADOS (CON IDs)
        // =========================================================
        sb.append("⚠️ PAQUETES RECHAZADOS \n");
        sb.append("---------------------------------------------------------\n");
        
        sb.append("Por falta de espacio (Best-Fit): ").append(resultado.noAsignados().size()).append(" paquete(s).\n");
        if(resultado.noAsignados().size() > 0) {
            sb.append("   -> IDs: ");
            for(int i = 0; i < resultado.noAsignados().size(); i++) {
                sb.append(resultado.noAsignados().getAt(i).id()).append(" ");
            }
            sb.append("\n");
        }
        
        sb.append("Por rutas inalcanzables (Warshall): ").append(resultado.inalcanzables().size()).append(" paquete(s).\n");
        if(resultado.inalcanzables().size() > 0) {
            sb.append("   -> IDs: ");
            for(int i = 0; i < resultado.inalcanzables().size(); i++) {
                sb.append(resultado.inalcanzables().getAt(i).id()).append(" ");
            }
            sb.append("\n");
        }

        // =========================================================
        // 4. VEREDICTO FINAL
        // =========================================================
        sb.append("\n🏆 VEREDICTO FINAL DEL SISTEMA \n");
        sb.append("---------------------------------------------------------\n");
        sb.append(String.format("Distancia Total de la Flota (MST): %.2f km\n", distanciaGlobalMST));
        sb.append(String.format("Distancia Total de la Flota (NN):  %.2f km\n", distanciaGlobalNN));
        
        if (distanciaGlobalMST < distanciaGlobalNN) {
            sb.append(">> El algoritmo MST ofreció rutas globales más eficientes.\n");
        } else if (distanciaGlobalNN < distanciaGlobalMST) {
            sb.append(">> El algoritmo Vecino Cercano (NN) ofreció rutas globales más eficientes.\n");
        } else {
            sb.append(">> Empate técnico. Ambas estrategias generaron rutas de la misma longitud total.\n");
        }

        // =========================================================
        // CONFIGURACIÓN DE LA INTERFAZ VISUAL (JavaFX Alert)
        // =========================================================
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Dashboard Analítico LogísTEC");
        alert.setHeaderText("Resultados de la Simulación");

        TextArea textArea = new TextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        // Estilo de terminal (Letras verdes sobre fondo oscuro para darle un look técnico y profesional)
        textArea.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 14px; -fx-control-inner-background: #1e1e1e; -fx-text-fill: #00ff00;");

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);

        alert.getDialogPane().setContent(expContent);
        alert.getDialogPane().setPrefWidth(900); // Ventana más ancha para que quepan las rutas
        alert.getDialogPane().setPrefHeight(650);

        alert.showAndWait();
    }

    // =========================================================
    // MÉTODOS AUXILIARES
    // =========================================================
    private static double calcularDistancia(LinkedList<Integer> ruta, double[][] matrizFloyd) {
        double total = 0.0;
        if (ruta == null || ruta.size() < 2) return total;

        for (int i = 0; i < ruta.size() - 1; i++) {
            int origen = ruta.getAt(i);
            int destino = ruta.getAt(i + 1);
            if (matrizFloyd[origen][destino] != Double.POSITIVE_INFINITY) {
                total += matrizFloyd[origen][destino];
            }
        }
        return total;
    }

    private static String formatearRuta(LinkedList<Integer> ruta) {
        if (ruta == null || ruta.size() == 0) return "Sin Ruta";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ruta.size(); i++) {
            sb.append(JsonParser.diccionarioNombres[ruta.getAt(i)]);
            if (i < ruta.size() - 1) {
                sb.append(" -> ");
            }
        }
        return sb.toString();
    }
}