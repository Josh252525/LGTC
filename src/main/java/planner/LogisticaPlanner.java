package planner;

import estructuras.*;
import datos.*;

public class LogisticaPlanner {

    // Recibe los alcanzables, los inalcanzables (de Warshall) y el JSON de camiones puros
    public ResultadoLogistica asignarCarga(Paquete[] paquetesAlcanzables, LinkedList<Paquete> inalcanzables, Camion[] camionesJson) {
        
        // 1. Mapeamos los records inmutables a nuestras clases mutables de simulación
        CamionPlanificado[] flota = new CamionPlanificado[camionesJson.length];
        for (int i = 0; i < camionesJson.length; i++) {
            flota[i] = new CamionPlanificado(camionesJson[i]);
        }
        
        // 2. Ordenamos paquetes (Prioridad ascendente, Peso descendente)
        ordenarPaquetes(paquetesAlcanzables);
        
        LinkedList<Paquete> noAsignados = new LinkedList<>();
        
        // 3. Lógica pura de Best-Fit
        for (Paquete p : paquetesAlcanzables) {
            int mejorCamionIndex = -1;
            double mejorEspacioSobrante = Double.MAX_VALUE;

            for (int i = 0; i < flota.length; i++) {
                double espacioActual = flota[i].getCapacidadRestante();
                
                // ¿Cabe el paquete?
                if (espacioActual >= p.peso()) {
                    double espacioSobrante = espacioActual - p.peso();
                    
                    // Condición de Best-Fit: encontrar el ajuste más apretado
                    if (espacioSobrante < mejorEspacioSobrante) {
                        mejorEspacioSobrante = espacioSobrante;
                        mejorCamionIndex = i;
                    }
                }
            }

            // 4. Asignamos usando el método seguro de la envoltura
            if (mejorCamionIndex != -1) {
                flota[mejorCamionIndex].intentarCargar(p);
            } else {
                noAsignados.insert(p); // Ningún camión tuvo espacio suficiente
            }
        }

        // Devolvemos el reporte completo
        return new ResultadoLogistica(inalcanzables, noAsignados, flota);
    }
    
    // Insertion Sort robusto adaptado a las reglas del negocio de LogísTEC
    private void ordenarPaquetes(Paquete[] paquetes) {
        for (int i = 1; i < paquetes.length; i++) {
            Paquete actual = paquetes[i];
            int j = i - 1;
            
            // Regla 1: Prioridad ascendente (1 es más prioritario que 2, por ende 2 es 'mayor' en número).
            // Regla 2: A igual prioridad, peso descendente (el más pesado va primero).
            while (j >= 0 && (
                   paquetes[j].prioridad() > actual.prioridad() || 
                   (paquetes[j].prioridad() == actual.prioridad() && paquetes[j].peso() < actual.peso())
                   )) {
                
                paquetes[j + 1] = paquetes[j];
                j--;
            }
            paquetes[j + 1] = actual;
        }
    }
}