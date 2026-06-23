package ui;

import estructuras.LinkedList;
import estructuras.Grafo;
import algoritmos.Prim;
import algoritmos.Kruskal;
import planner.CamionPlanificado;
import planner.ResultadoLogistica;
import datos.Paquete;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class GeneradorEstadisticas {

    public static void mostrarPanel(ResultadoLogistica resultado, 
                                    LinkedList<LinkedList<Integer>> rutasMST, 
                                    LinkedList<LinkedList<Integer>> rutasNN, 
                                    double[][] matrizFloyd,
                                    Grafo grafoCiudad) {
        
        StringBuilder sb = new StringBuilder();
        
        sb.append("==============================\n");
        sb.append("REPORTE LOGÍSTICO Y BENCHMARKS   \n");
        sb.append("==============================\n\n");

        // --- 1. ANÁLISIS DE LA FLOTA Y CARGA (BEST-FIT) ---
        sb.append("Análisis Best-Fit \n");
        sb.append("-------------------------------------------------------\n");
        
        double cargaTransportadaGlobal = 0.0;
        double capacidadTotalGlobal = 0.0;
        
        for (int i = 0; i < resultado.flota().length; i++) {
            CamionPlanificado camion = resultado.flota()[i];
            double capacidadTotal = camion.getCamionBase().capacidad();
            double restante = camion.getCapacidadRestante();
            double cargaUsada = capacidadTotal - restante;
            double eficiencia = (cargaUsada / (capacidadTotal > 0 ? capacidadTotal : 1)) * 100;
            
            cargaTransportadaGlobal += cargaUsada;
            capacidadTotalGlobal += capacidadTotal;
            
            sb.append(String.format("Camión: %s | Carga: %.2f / %.2f kg (Eficiencia: %.1f %%)\n", 
                camion.getCamionBase().id(), cargaUsada, capacidadTotal, eficiencia));
            
            LinkedList<Paquete> asignados = camion.getPaquetesCargados();
            if (asignados.size() > 0) {
                sb.append("   -> IDs de Paquetes: ");
                for (int j = 0; j < asignados.size(); j++) {
                    sb.append(asignados.getAt(j).id()).append(" ");
                }
                sb.append("\n");
            } else {
                sb.append("   -> Paquetes: Ninguno (Vehículo Vacío)\n");
            }
        }
        
        double eficienciaGlobal = (capacidadTotalGlobal > 0) ? (cargaTransportadaGlobal / capacidadTotalGlobal) * 100 : 0.0;
        sb.append(String.format("\n Carga Total Transportada: %.2f kg de %.2f kg (Eficiencia Global: %.1f %%)\n\n", 
            cargaTransportadaGlobal, capacidadTotalGlobal, eficienciaGlobal));

        // --- 2. INCIDENCIAS DE PAQUETERÍA (RECHAZOS) ---
        sb.append("Rechazo de paquetes \n");
        sb.append("-------------------------------------------------------\n");
        
        sb.append("Rechazados por Best-Fit: ");
        if (resultado.noAsignados().size() == 0) {
            sb.append("0 paquetes.\n");
        } else {
            sb.append(resultado.noAsignados().size()).append(" paquete(s). IDs: ");
            for (int i = 0; i < resultado.noAsignados().size(); i++) {
                sb.append(resultado.noAsignados().getAt(i).id()).append(" ");
            }
            sb.append("\n");
        }
        
        sb.append("Destino inalcanzable detectado por Warshall): ");
        if (resultado.inalcanzables().size() == 0) {
            sb.append("0 paquetes.\n\n");
        } else {
            sb.append(resultado.inalcanzables().size()).append(" paquete(s). IDs: ");
            for (int i = 0; i < resultado.inalcanzables().size(); i++) {
                sb.append(resultado.inalcanzables().getAt(i).id()).append(" ");
            }
            sb.append("\n\n");
        }

        // --- 3. COMPARATIVA DE ENRUTAMIENTO (MST VS NEAREST NEIGHBOR) ---
        sb.append("Efectividad de Enrutamiento\n");
        sb.append("-------------------------------------------------------\n");
        
        double distMSTGlobal = 0.0;
        double distNNGlobal = 0.0;
        
        for (int i = 0; i < rutasMST.size(); i++) {
            double dMST = calcularDistancia(rutasMST.getAt(i), matrizFloyd);
            double dNN = calcularDistancia(rutasNN.getAt(i), matrizFloyd);
            distMSTGlobal += dMST;
            distNNGlobal += dNN;
        }
        
        sb.append(String.format("Distancia Total de la Flota usando MST: %.2f km\n", distMSTGlobal));
        sb.append(String.format("Distancia Total de la Flota usando NN:  %.2f km\n", distNNGlobal));
        
        double ahorroRuta = Math.abs(distNNGlobal - distMSTGlobal);
        if (distMSTGlobal < distNNGlobal) {
            double pct = (distNNGlobal > 0) ? (ahorroRuta / distNNGlobal) * 100 : 0.0;
            sb.append(String.format("Árbol de Expansión Mínima (MST).\n   -> Ahorró %.2f km a la empresa (%.2f %% más óptimo).\n\n", ahorroRuta, pct));
        } else if (distNNGlobal < distMSTGlobal) {
            double pct = (distMSTGlobal > 0) ? (ahorroRuta / distMSTGlobal) * 100 : 0.0;
            sb.append(String.format("Nearest Neighbor.\n   -> Ahorró %.2f km a la empresa (%.2f %% más óptimo).\n\n", ahorroRuta, pct));
        } else {
            sb.append("Empate: Ambas heurísticas generaron rutas de igual distancia.\n\n");
        }

        // --- 4. BENCHMARK EMPÍRICO PRIM VS KRUSKAL ---
        sb.append("Benchmarks de Prim vs Kruskal \n");
        sb.append("-------------------------------------------------------\n");
        
        Prim algoritmoPrim = new Prim(grafoCiudad);
        Kruskal algoritmoKruskal = new Kruskal(grafoCiudad);

        long inicioPrim = System.nanoTime();
        double costoPrim = algoritmoPrim.calcMST();
        long finPrim = System.nanoTime();
        long tiempoPrimNs = finPrim - inicioPrim;

        long inicioKruskal = System.nanoTime();
        double costoKruskal = algoritmoKruskal.calcMST();
        long finKruskal = System.nanoTime();
        long tiempoKruskalNs = finKruskal - inicioKruskal;
        
        sb.append(String.format("Algoritmo Prim:\n   - Tiempo: %d ns\n   - Costo Árbol: %.2f\n", tiempoPrimNs, costoPrim));
        sb.append(String.format("Algoritmo Kruskal:\n   - Tiempo: %d ns\n   - Costo Árbol: %.2f\n", tiempoKruskalNs, costoKruskal));
        
        if (tiempoPrimNs < tiempoKruskalNs) {
            double factor = (double) tiempoKruskalNs / tiempoPrimNs;
            sb.append(String.format("Rendimiento: Prim fue %.2fx veces más rápido.\n", factor));
        } else if (tiempoKruskalNs < tiempoPrimNs) {
            double factor = (double) tiempoPrimNs / tiempoKruskalNs;
            sb.append(String.format("Rendimiento: Kruskal fue %.2fx veces más rápido.\n", factor));
        } else {
            sb.append("Rendimiento: Empate exacto en CPU.\n");
        }
        
        sb.append("Validación Matemática: ");
        sb.append(Math.abs(costoPrim - costoKruskal) < 0.001 ? "PASSED (Idénticos)\n" : "FAILED (Diferentes)\n");

        // --- RENDERIZADO VISUAL DEL PANEL ---
        // Imprimir respaldo en consola de IDE
        System.out.println(sb.toString());

        // Mostrar Ventana Pop-up 
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Dashboard Analítico - LogísTEC");
        alert.setHeaderText("Resultados de la Operación Logística y Auditoría");
        
        // Se utiliza TextArea para tener Scroll
        TextArea textArea = new TextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 14px; -fx-control-inner-background: #1e1e1e; -fx-text-fill: #00ff00;");

        // Expansión dinámica del cuadro de texto
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);

        // Se asigna la caja con scroll al panel de diálogo
        alert.getDialogPane().setContent(expContent);
        alert.getDialogPane().setPrefWidth(700); // Ancho de la ventana
        alert.getDialogPane().setPrefHeight(600); // Alto de la ventana

        alert.showAndWait();
    }

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
}