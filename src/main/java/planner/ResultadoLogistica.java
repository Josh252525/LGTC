package planner;

import estructuras.LinkedList;
import datos.Paquete;

/*
Documentación:
Objeto contenedor para el resultado de la logística.
- inalcanzables: Paquetes que no tienen ruta desde el depósito (Warshall).
- noAsignados: Paquetes alcanzables que no cupieron en los camiones (Best-Fit).
- flota: Estado final de los camiones usando la clase Wrapper (CamionPlanificado).
*/
public record ResultadoLogistica(
    LinkedList<Paquete> inalcanzables,
    LinkedList<Paquete> noAsignados,
    CamionPlanificado[] flota 
) {}