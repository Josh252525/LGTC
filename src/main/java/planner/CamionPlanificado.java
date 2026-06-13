package planner;

import datos.Camion;
import datos.Paquete;
import estructuras.LinkedList;

/**
 * Clase contenedora (Wrapper) para gestionar el estado dinámico de carga de un camión.
 * <p>
 * Mantiene el registro (record) original inmutable intacto, pero permite modificar la 
 * capacidad restante en tiempo de ejecución y almacenar los paquetes que le han sido 
 * asignados por el algoritmo de empacado.
 * </p>
 */
public class CamionPlanificado {
    
    private final Camion camionBase; 
    private double capacidadRestante;
    private final LinkedList<Paquete> paquetesCargados;

    /**
     * Inicializa un camión planificado con su capacidad máxima disponible.
     *
     * @param camion El objeto base (record) con los datos estáticos del camión.
     */
    public CamionPlanificado(Camion camion) {
        this.camionBase = camion;
        this.capacidadRestante = camion.capacidad(); 
        this.paquetesCargados = new LinkedList<>();
    }

    /**
     * Intenta cargar un paquete en este camión verificando si existe espacio suficiente.
     * Operación fundamental para implementar heurísticas de Bin Packing como el Best-Fit.
     *
     * @param p El paquete que se desea cargar.
     * @return true si el paquete fue cargado exitosamente, false si excede la capacidad restante.
     */
    public boolean intentarCargar(Paquete p) {
        if (p.peso() <= this.capacidadRestante) {
            this.paquetesCargados.insert(p);
            this.capacidadRestante -= p.peso();
            return true;
        }
        return false;
    }

    /**
     * Obtiene el espacio libre actual del camión.
     *
     * @return La capacidad restante en kilogramos.
     */
    public double getCapacidadRestante() {
        return capacidadRestante;
    }

    /**
     * Obtiene la información original e inmutable del camión.
     *
     * @return El record estático del camión.
     */
    public Camion getCamionBase() {
        return camionBase;
    }

    /**
     * Obtiene la lista de paquetes que han sido exitosamente asignados a este camión.
     *
     * @return Una LinkedList que contiene los paquetes cargados.
     */
    public LinkedList<Paquete> getPaquetesCargados() {
        return paquetesCargados;
    }
}