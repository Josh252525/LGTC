package datos;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import estructuras.LinkedList;
import planner.CamionPlanificado;
import planner.ResultadoLogistica;

public class ExportadorCSV {

    /**
     * Escribe los resultados de la asignación y las rutas en un archivo CSV.
     */
    public static void generarReporte(String rutaArchivo, ResultadoLogistica resultado, 
                                      LinkedList<LinkedList<Integer>> rutasMST, 
                                      LinkedList<LinkedList<Integer>> rutasNN) {
        
        try (PrintWriter escritor = new PrintWriter(new FileWriter(rutaArchivo))) {
            
            // 1. Escribimos la cabecera
            escritor.println("ID_Camion,Peso_Ocupado,Capacidad_Maxima,Ruta_MST(Preorden),Ruta_NN(Vecino_Cercano)");

            int cantidadCamiones = resultado.flota().length;
            for (int i = 0; i < cantidadCamiones; i++) {
                CamionPlanificado camion = resultado.flota()[i];
                
                // Usamos size() > 0 basándonos en tu LinkedList
                if (camion.getPaquetesCargados().size() > 0) {
                    
                    LinkedList<Integer> rutaMST = rutasMST.getAt(i);
                    LinkedList<Integer> rutaNN = rutasNN.getAt(i);

                    // ====================================================================
                    // CORRECCIONES APLICADAS BASADAS EN TUS CLASES:
                    // ====================================================================
                    
                    // 1. Usamos getCamionBase() en lugar de getCamionOriginal()
                    String idCamion = String.valueOf(camion.getCamionBase().id()); 
                    
                    // 2. Calculamos el peso ocupado: (Capacidad Total - Capacidad Restante)
                    double pesoOcupadoCalculado = camion.getCamionBase().capacidad() - camion.getCapacidadRestante();
                    String pesoOcupado = String.valueOf(pesoOcupadoCalculado);
                    
                    // 3. Obtenemos la capacidad máxima del record base
                    String capMaxima = String.valueOf(camion.getCamionBase().capacidad()); 
                    
                    String linea = idCamion + "," +
                                   pesoOcupado + "," +
                                   capMaxima + "," +
                                   formatearRuta(rutaMST) + "," +
                                   formatearRuta(rutaNN);
                    
                    escritor.println(linea);
                }
            }
            
            System.out.println("✅ Reporte exportado exitosamente en: " + rutaArchivo);
            
        } catch (IOException e) {
            System.err.println("❌ Error al intentar escribir el archivo CSV: " + e.getMessage());
        }
    }

    /**
     * Convierte la lista enlazada a un texto legible, ej: "0 -> 4 -> 2 -> 0"
     */
    private static String formatearRuta(LinkedList<Integer> ruta) {
        // CORRECCIÓN: Usamos size() == 0 en lugar de isEmpty()
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
}