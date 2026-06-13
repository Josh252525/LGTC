package planner;

import estructuras.*;
import datos.*;

/**
 * Motor central de la planificación logística para el empacado de camiones.
 * Implementa la heurística de asignación Best-Fit Decreasing (Mejor Ajuste Descendente)
 * para resolver el problema de Bin Packing y maximizar el uso del espacio en la flota.
 */
public class LogisticaPlanner {

    /**
     * Distribuye los paquetes disponibles entre los camiones de la empresa intentando
     * dejar el menor espacio sobrante posible en cada vehículo.
     *
     * @param paquetesAlcanzables Arreglo de paquetes cuyas ciudades de destino son válidas.
     * @param inalcanzables       Lista de paquetes previamente descartados por no tener ruta.
     * @param camionesJson        Arreglo con la información estática de los camiones disponibles.
     * @return Un objeto ResultadoLogistica que segrega la carga asignada, rechazada e inalcanzable.
     */
    public ResultadoLogistica asignarCarga(Paquete[] paquetesAlcanzables, LinkedList<Paquete> inalcanzables, Camion[] camionesJson) {
        
        CamionPlanificado[] flota = new CamionPlanificado[camionesJson.length];
        for (int i = 0; i < camionesJson.length; i++) {
            flota[i] = new CamionPlanificado(camionesJson[i]);
        }
        
        ordenarPaquetes(paquetesAlcanzables);
        
        LinkedList<Paquete> noAsignados = new LinkedList<>();
        
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
    
    // Insertion Sort robusto adaptado a las reglas del negocio (Prioridad y Peso)
    private void ordenarPaquetes(Paquete[] paquetes) {
        for (int i = 1; i < paquetes.length; i++) {
            Paquete actual = paquetes[i];
            int j = i - 1;
            
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