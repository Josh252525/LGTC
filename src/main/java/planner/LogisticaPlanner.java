package planner;

import estructuras.*;
import datos.*;

public class LogisticaPlanner {

    // 🚀 NUEVA FIRMA: Ahora recibe la rutaMaestra
    public ResultadoLogistica asignarCarga(Paquete[] paquetesAlcanzables, LinkedList<Paquete> inalcanzables, Camion[] camionesJson, LinkedList<Integer> rutaMaestra) {
        
        CamionPlanificado[] flota = new CamionPlanificado[camionesJson.length];
        for (int i = 0; i < camionesJson.length; i++) {
            flota[i] = new CamionPlanificado(camionesJson[i]);
        }
        
        // 1. Ordenamos paquetes respetando la geografía de la ruta
        ordenarPaquetesPorRuta(paquetesAlcanzables, rutaMaestra);
        
        LinkedList<Paquete> noAsignados = new LinkedList<>();
        
        // 2. Lógica pura de Best-Fit
        for (Paquete p : paquetesAlcanzables) {
            int mejorCamionIndex = -1;
            double mejorEspacioSobrante = Double.MAX_VALUE;

            for (int i = 0; i < flota.length; i++) {
                double espacioActual = flota[i].getCapacidadRestante();
                
                if (espacioActual >= p.peso()) {
                    double espacioSobrante = espacioActual - p.peso();
                    if (espacioSobrante < mejorEspacioSobrante) {
                        mejorEspacioSobrante = espacioSobrante;
                        mejorCamionIndex = i;
                    }
                }
            }

            if (mejorCamionIndex != -1) {
                flota[mejorCamionIndex].intentarCargar(p);
            } else {
                noAsignados.insert(p); 
            }
        }

        return new ResultadoLogistica(inalcanzables, noAsignados, flota);
    }
    
    // =========================================================================
    // NUEVO SISTEMA DE ORDENAMIENTO: Geografía -> Prioridad -> Peso
    // =========================================================================
    private void ordenarPaquetesPorRuta(Paquete[] paquetes, LinkedList<Integer> rutaMaestra) {
        for (int i = 1; i < paquetes.length; i++) {
            Paquete actual = paquetes[i];
            int j = i - 1;
            
            while (j >= 0 && debeMoverse(paquetes[j], actual, rutaMaestra)) {
                paquetes[j + 1] = paquetes[j];
                j--;
            }
            paquetes[j + 1] = actual;
        }
    }

    private boolean debeMoverse(Paquete anterior, Paquete actual, LinkedList<Integer> rutaMaestra) {
        int posAnterior = obtenerPosicionEnRuta(anterior.destino(), rutaMaestra);
        int posActual = obtenerPosicionEnRuta(actual.destino(), rutaMaestra);

        // 1. CRITERIO GEOGRÁFICO: El que esté más cerca en la ruta se sube primero
        if (posAnterior > posActual) return true;
        if (posAnterior < posActual) return false;

        // 2. Si van exactamente a la misma ciudad, respetamos la prioridad
        if (anterior.prioridad() > actual.prioridad()) return true;
        if (anterior.prioridad() < actual.prioridad()) return false;

        // 3. Si tienen misma ciudad y prioridad, priorizamos el más pesado (Best-Fit clásico)
        if (anterior.peso() < actual.peso()) return true;

        return false;
    }

    private int obtenerPosicionEnRuta(int destino, LinkedList<Integer> rutaMaestra) {
        for (int i = 0; i < rutaMaestra.size(); i++) {
            if (rutaMaestra.getAt(i) == destino) {
                return i;
            }
        }
        return 9999; // Si por alguna razón no está en la ruta, se va al final de la lista
    }
}