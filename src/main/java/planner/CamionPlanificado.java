package planner;

import datos.Camion;
import datos.Paquete;
import estructuras.LinkedList;

/*
Clase Wrapper para gestionar el estado dinámico de carga.
Mantiene el record original intacto pero permite modificar la capacidad 
restante y almacenar los paquetes asignados por el Best-Fit.
*/
public class CamionPlanificado {
    
    private final Camion camionBase; // Guardamos una referencia al record original
    private double capacidadRestante;
    private final LinkedList<Paquete> paquetesCargados;

    public CamionPlanificado(Camion camion) {
        this.camionBase = camion;
        this.capacidadRestante = camion.capacidad(); // Inicializa con la capacidad total del record
        this.paquetesCargados = new LinkedList<>();
    }

    /**
     * Intenta cargar un paquete en este camión bajo la regla del Best-Fit.
     */
    public boolean intentarCargar(Paquete p) {
        if (p.peso() <= this.capacidadRestante) {
            this.paquetesCargados.insert(p);
            this.capacidadRestante -= p.peso();
            return true;
        }
        return false;
    }

    // Getters para el algoritmo de Best-Fit
    public double getCapacidadRestante() {
        return capacidadRestante;
    }

    public Camion getCamionBase() {
        return camionBase;
    }

    public LinkedList<Paquete> getPaquetesCargados() {
        return paquetesCargados;
    }
}