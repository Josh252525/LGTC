package ui;

import estructuras.LinkedList;
import estructuras.Grafo;
import algoritmos.Prim;
import algoritmos.Kruskal;
import planner.CamionPlanificado;
import planner.ResultadoLogistica;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class GeneradorEstadisticas {

    /**
     * Calcula las métricas de carga, ruteo y realiza la comparación empírica de tiempos Prim vs Kruskal.
     */
    public static void mostrarPanel(ResultadoLogistica resultado, 
                                    LinkedList<LinkedList<Integer>> rutasMST, 
                                    LinkedList<LinkedList<Integer>> rutasNN, 
                                    double[][] matrizFloyd,
                                    Grafo grafoCiudad) { // <-- NUEVO: Recibimos el grafo puro para el benchmark
        
        // 1. Calcular Carga Transportada
        double cargaTransportada = 0.0;
        double capacidadTotal = 0.0;
        
        for (int i = 0; i < resultado.flota().length; i++) {
            CamionPlanificado camion = resultado.flota()[i];
            capacidadTotal += camion.getCamionBase().capacidad();
            cargaTransportada += (camion.getCamionBase().capacidad() - camion.getCapacidadRestante());
        }

        // 2. Calcular Carga Rechazada
        double cargaRechazada = 0.0;
        for (int i = 0; i < resultado.inalcanzables().size(); i++) {
            cargaRechazada += resultado.inalcanzables().getAt(i).peso();
        }
        for (int i = 0; i < resultado.noAsignados().size(); i++) {
            cargaRechazada += resultado.noAsignados().getAt(i).peso();
        }

        // 3. Calcular Distancias Totales
        double distMST = 0.0;
        for (int i = 0; i < rutasMST.size(); i++) {
            distMST += calcularDistancia(rutasMST.getAt(i), matrizFloyd);
        }

        double distNN = 0.0;
        for (int i = 0; i < rutasNN.size(); i++) {
            distNN += calcularDistancia(rutasNN.getAt(i), matrizFloyd);
        }

        // 4. Estrategia de Enrutamiento Ganadora (MST vs NN)
        String ganadorEnrutamiento;
        double ahorroRuta = Math.abs(distNN - distMST);
        double pctAhorroRuta = 0.0;

        if (distMST < distNN) {
            ganadorEnrutamiento = "Estrategia MST (Árbol de Expansión Mínima)";
            if (distNN > 0) pctAhorroRuta = (ahorroRuta / distNN) * 100;
        } else if (distNN < distMST) {
            ganadorEnrutamiento = "Estrategia NN (Vecino Cercano)";
            if (distMST > 0) pctAhorroRuta = (ahorroRuta / distMST) * 100;
        } else {
            ganadorEnrutamiento = "Empate de Heurísticas Visuales";
        }

        // ===================================================================
        // 📊 FASE NUEVA: COMPARACIÓN EMPÍRICA (BENCHMARK PRIM VS KRUSKAL)
        // ===================================================================
        Prim algoritmoPrim = new Prim(grafoCiudad);
        Kruskal algoritmoKruskal = new Kruskal(grafoCiudad);

        // --- Cronometrar Prim ---
        long inicioPrim = System.nanoTime();
        double costoPrim = algoritmoPrim.calcMST();
        long finPrim = System.nanoTime();
        long tiempoPrimNs = finPrim - inicioPrim;

        // --- Cronometrar Kruskal ---
        long inicioKruskal = System.nanoTime();
        double costoKruskal = algoritmoKruskal.calcMST();
        long finKruskal = System.nanoTime();
        long tiempoKruskalNs = finKruskal - inicioKruskal;

        // Analizar rendimiento de ejecución (Eficiencia de tiempo)
        String algoritmoMasRapido;
        double factorVelocidad = 0.0;
        if (tiempoPrimNs < tiempoKruskalNs) {
            algoritmoMasRapido = "Prim (Cola de Prioridad / Heap)";
            factorVelocidad = (double) tiempoKruskalNs / tiempoPrimNs;
        } else if (tiempoKruskalNs < tiempoPrimNs) {
            algoritmoMasRapido = "Kruskal (Union-Find + Bubble Sort)";
            factorVelocidad = (double) tiempoPrimNs / tiempoKruskalNs;
        } else {
            algoritmoMasRapido = "Empate de ejecución de CPU";
        }

        // ===================================================================
        // ✍️ CONSTRUCCIÓN DEL REPORTE TEXTUAL TANGIBLE
        // ===================================================================
        StringBuilder sb = new StringBuilder();
        sb.append("📦 METRICAS DE OPERACIÓN (CARGA):\n");
        sb.append(String.format("  - Carga Total Transportada: %.2f kg (Eficiencia de Flota: %.1f %%)\n", 
                cargaTransportada, (cargaTransportada / (capacidadTotal > 0 ? capacidadTotal : 1)) * 100));
        sb.append(String.format("  - Carga Dejada en Almacén: %.2f kg\n\n", cargaRechazada));

        sb.append("COMPARATIVA DE EFECTIVIDAD DE RUTAS:\n");
        sb.append(String.format("  - Distancia Total Recorrida con MST: %.2f km\n", distMST));
        sb.append(String.format("  - Distancia Total Recorrida con NN : %.2f km\n", distNN));
        sb.append("  - Heurística Más Efectiva: ").append(ganadorEnrutamiento).append("\n");
        if (ahorroRuta > 0) {
            sb.append(String.format("  - Reducción de trayecto: %.2f km (%.2f %% más óptimo)\n\n", ahorroRuta, pctAhorroRuta));
        } else {
            sb.append("  - Reducción de trayecto: 0.00 km (Rutas equivalentes en mapas simples)\n\n");
        }

        sb.append("BENCHMARK DEL MST:\n");
        sb.append(String.format("  - Tiempo de ejecución de Prim   : %d ns (Resultado MST: %.2f)\n", tiempoPrimNs, costoPrim));
        sb.append(String.format("  - Tiempo de ejecución de Kruskal: %d ns (Resultado MST: %.2f)\n", tiempoKruskalNs, costoKruskal));
        sb.append("  - Algoritmo de CPU más veloz   : ").append(algoritmoMasRapido).append("\n");
        if (factorVelocidad > 0) {
            sb.append(String.format("  - Factor de rendimiento empírico: El ganador fue %.2fx más rápido.\n", factorVelocidad));
        }
        sb.append("  - Validación de Exactitud      : ")
          .append(Math.abs(costoPrim - costoKruskal) < 0.001 ? "PASSED (Ambos construyen el mismo MST óptimo)" : "FAILED")
          .append("\n");

        // 6. Respaldo directo en consola del IDE
        System.out.println("\n=======================================================");
        System.out.println("RENDIMIENTO LOGÍSTEC");
        System.out.println("=======================================================");
        System.out.println(sb.toString());
        System.out.println("=======================================================\n");

        // 7. Lanzar Pop-up Gráfico interactivo para la defensa
        Alert popup = new Alert(AlertType.INFORMATION);
        popup.setTitle("Dashboard de Análisis Empírico - LogísTEC");
        popup.setHeaderText("Resultados Tangibles de Eficiencia y Tiempos de Ejecución");
        popup.setContentText(sb.toString());
        popup.showAndWait();
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