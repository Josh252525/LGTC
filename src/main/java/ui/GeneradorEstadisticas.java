package ui;

import estructuras.LinkedList;
import planner.CamionPlanificado;
import planner.ResultadoLogistica;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class GeneradorEstadisticas {

    /**
     * Calcula las métricas finales y muestra un panel gráfico y un reporte en consola.
     */
    public static void mostrarPanel(ResultadoLogistica resultado, 
                                    LinkedList<LinkedList<Integer>> rutasMST, 
                                    LinkedList<LinkedList<Integer>> rutasNN, 
                                    double[][] matrizFloyd) {
        
        // 1. Calcular Carga Transportada
        double cargaTransportada = 0.0;
        double capacidadTotal = 0.0;
        
        for (int i = 0; i < resultado.flota().length; i++) {
            CamionPlanificado camion = resultado.flota()[i];
            capacidadTotal += camion.getCamionBase().capacidad();
            // Peso ocupado = Total - Restante
            cargaTransportada += (camion.getCamionBase().capacidad() - camion.getCapacidadRestante());
        }

        // 2. Calcular Carga Rechazada (Inalcanzables + Sin Espacio)
        double cargaRechazada = 0.0;
        for (int i = 0; i < resultado.inalcanzables().size(); i++) {
            cargaRechazada += resultado.inalcanzables().getAt(i).peso();
        }
        for (int i = 0; i < resultado.noAsignados().size(); i++) {
            cargaRechazada += resultado.noAsignados().getAt(i).peso();
        }

        // 3. Calcular Distancias Totales (Calle por calle)
        double distMST = 0.0;
        for (int i = 0; i < rutasMST.size(); i++) {
            distMST += calcularDistancia(rutasMST.getAt(i), matrizFloyd);
        }

        double distNN = 0.0;
        for (int i = 0; i < rutasNN.size(); i++) {
            distNN += calcularDistancia(rutasNN.getAt(i), matrizFloyd);
        }

        // 4. Análisis de Ahorro y Comparativa
        String ganador;
        double ahorroAbsoluto = 0.0;
        double porcentajeAhorro = 0.0;

        if (distMST < distNN) {
            ganador = "MST (Árbol de Expansión Mínima)";
            ahorroAbsoluto = distNN - distMST;
            if (distNN > 0) porcentajeAhorro = (ahorroAbsoluto / distNN) * 100;
        } else if (distNN < distMST) {
            ganador = "Nearest Neighbor (Vecino Cercano)";
            ahorroAbsoluto = distMST - distNN;
            if (distMST > 0) porcentajeAhorro = (ahorroAbsoluto / distMST) * 100;
        } else {
            ganador = "Empate (Ambas heurísticas son igual de eficientes)";
        }

        // 5. Construir Texto del Panel
        StringBuilder sb = new StringBuilder();
        sb.append("📦 MÉTRICAS DE CARGA:\n");
        sb.append(String.format("- Carga Transportada: %.2f kg de %.2f kg en flota.\n", cargaTransportada, capacidadTotal));
        sb.append(String.format("- Carga Rechazada/Pendiente: %.2f kg.\n\n", cargaRechazada));

        sb.append("🛣️ MÉTRICAS DE ENRUTAMIENTO:\n");
        sb.append(String.format("- Distancia Total MST: %.2f km.\n", distMST));
        sb.append(String.format("- Distancia Total NN: %.2f km.\n\n", distNN));

        sb.append("🏆 ANÁLISIS DE RENDIMIENTO:\n");
        sb.append("- Estrategia Ganadora: ").append(ganador).append("\n");
        
        if (ahorroAbsoluto > 0) {
            sb.append(String.format("- Distancia ahorrada: %.2f km.\n", ahorroAbsoluto));
            sb.append(String.format("- Eficiencia superior por: %.2f %%\n", porcentajeAhorro));
        } else {
            // Se ejecuta si hay un empate exacto
            sb.append("- Distancia ahorrada: 0.00 km (Rutas idénticas).\n");
            sb.append("- Eficiencia superior por: 0.00 %\n");
        }

        // 6. Imprimir en Consola (Respaldo)
        System.out.println("\n============== 📊 DASHBOARD LOGÍSTEC ==============");
        System.out.println(sb.toString());
        System.out.println("===================================================\n");

        // 7. Mostrar Alerta Gráfica (Pop-up en JavaFX)
        Alert popup = new Alert(AlertType.INFORMATION);
        popup.setTitle("Dashboard de Resultados");
        popup.setHeaderText("Resumen de Operación Logística");
        popup.setContentText(sb.toString());
        popup.showAndWait(); // Pausa la UI hasta que el usuario le de "Aceptar"
    }

    /**
     * Suma los pesos de las aristas exactas por las que pasó el camión.
     */
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