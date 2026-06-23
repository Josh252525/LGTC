package datos;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import estructuras.LinkedList;
import planner.CamionPlanificado;
import planner.ResultadoLogistica;

public class ExportadorCSV {

    public static void generarReporte(String rutaArchivo, 
                                      ResultadoLogistica resultado, 
                                      LinkedList<LinkedList<Integer>> rutasMST, 
                                      LinkedList<LinkedList<Integer>> rutasNN,
                                      LinkedList<Long> tiemposMST,
                                      LinkedList<Long> tiemposNN) {
        
        try (PrintWriter escritor = new PrintWriter(new FileWriter(rutaArchivo))) {
            
            // =========================================================
            // BLOQUE 1: REPORTE DE LA FLOTA Y RUTAS
            // =========================================================
            escritor.println("--- REPORTE DE FLOTA ---");
            escritor.println("ID_Camion,Peso_Ocupado,Capacidad_Maxima,Paquetes_A_Bordo,Ruta_MST(Preorden),Tiempo_MST(ns),Ruta_NN(Vecino_Cercano),Tiempo_NN(ns)");

            int cantidadCamiones = resultado.flota().length;
            for (int i = 0; i < cantidadCamiones; i++) {
                CamionPlanificado camion = resultado.flota()[i];
                
                String idCamion = camion.getCamionBase().id();
                double pesoOcupadoCalculado = camion.getCamionBase().capacidad() - camion.getCapacidadRestante();
                String pesoOcupado = String.valueOf(pesoOcupadoCalculado);
                String capMaxima = String.valueOf(camion.getCamionBase().capacidad()); 
                
                String paquetes = obtenerListaPaquetes(camion.getPaquetesCargados());
                
                String linea = idCamion + "," +
                               pesoOcupado + "," +
                               capMaxima + "," +
                               paquetes + "," +
                               formatearRuta(rutasMST.getAt(i)) + "," +
                               tiemposMST.getAt(i) + "," +
                               formatearRuta(rutasNN.getAt(i)) + "," +
                               tiemposNN.getAt(i);
                
                escritor.println(linea);
            }
            
            escritor.println(); // Espacio en blanco para separar
            
            // =========================================================
            // BLOQUE 2: REPORTE DE PAQUETES RECHAZADOS
            // =========================================================
            escritor.println("--- REPORTE DE PAQUETES RECHAZADOS ---");
            escritor.println("ID_Paquete,Destino,Peso,Prioridad,Motivo_Rechazo");
            
            // 1. Rechazados por Warshall (Inalcanzables)
            LinkedList<Paquete> inalcanzables = resultado.inalcanzables();
            for (int i = 0; i < inalcanzables.size(); i++) {
                Paquete p = inalcanzables.getAt(i);
                String nombreDestino = JsonParser.diccionarioNombres[p.destino()];
                escritor.println(p.id() + "," + nombreDestino + "," + p.peso() + "," + p.prioridad() + ",Inalcanzable (Bloqueado / Sin calles)");
            }
            
            // 2. Rechazados por Best-Fit (Falta de espacio)
            LinkedList<Paquete> noAsignados = resultado.noAsignados();
            for (int i = 0; i < noAsignados.size(); i++) {
                Paquete p = noAsignados.getAt(i);
                String nombreDestino = JsonParser.diccionarioNombres[p.destino()];
                escritor.println(p.id() + "," + nombreDestino + "," + p.peso() + "," + p.prioridad() + ",Falta de espacio en la flota (Best-Fit)");
            }
            
            System.out.println("✅ Reporte exportado exitosamente en: " + rutaArchivo);
            
        } catch (IOException e) {
            System.err.println("❌ Error al intentar escribir el archivo CSV: " + e.getMessage());
        }
    }

    private static String formatearRuta(LinkedList<Integer> ruta) {
        if (ruta == null || ruta.size() == 0) return "Sin Ruta";
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ruta.size(); i++) {
            sb.append(datos.JsonParser.diccionarioNombres[ruta.getAt(i)]);
            if (i < ruta.size() - 1) {
                sb.append(" -> ");
            }
        }
        return "\"" + sb.toString() + "\""; 
    }
    
    private static String obtenerListaPaquetes(LinkedList<Paquete> paquetes) {
        if (paquetes == null || paquetes.size() == 0) return "Ninguno";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < paquetes.size(); i++) {
            sb.append(paquetes.getAt(i).id());
            if (i < paquetes.size() - 1) {
                sb.append(" ");
            }
        }
        return "\"" + sb.toString() + "\"";
    }
}