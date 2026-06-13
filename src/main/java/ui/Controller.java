package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

// =========================================================================
// 🤝 CONEXIONES CON LOS MÓDULOS DE NUESTRO BACKEND (PROHIBIDO USA JAVA.UTIL)
// =========================================================================
import datos.JsonParser;                  // Para leer e interpretar el JSON
import datos.ConfigLogisTEC;               // Contenedor global de configuraciones
import datos.Paquete;                     // DTO de paquetes
import datos.ValidadorPaquetes;           // Para aislar paquetes inalcanzables y armar el grafo
import datos.ExportadorCSV;               // Para guardar el reporte en Excel
import estructuras.LinkedList;            // Nuestra lista enlazada personalizada
import estructuras.Grafo;                 // Estructura oficial de la red vial
import algoritmos.FloydWarshall;           // Generador de la matriz de distancias optimizadas
import algoritmos.Dijkstra;
import planner.LogisticaPlanner;          // Orquestador del Best-Fit
import planner.CamionPlanificado;         // Envoltura mutable del estado del camión
import planner.ResultadoLogistica;        // Reporte de salida de la distribución
import planner.EnrutadorMST;              // Tu enrutador basado en Prim + DFS Preorden
import planner.EnrutadorNearestNeighbor;   // El enrutador codicioso de tu compañero

/**
 * Clase Controladora de la Ventana Principal (Patrón MVC).
 * Actúa como el puente definitivo de comunicación entre la UI y los algoritmos del backend.
 */
public class Controller implements Initializable {

    // =========================================================================
    // 📺 COMPONENTES VISUALES INYECTADOS DESDE EL ARCHIVO FXML
    // =========================================================================
    @FXML private Button cargararchivosButton;
    @FXML private Button calcularrutasButton;
    @FXML private Label archivocargadolabel;
    @FXML private Pane mapaPane; // El lienzo de 600x600 donde ocurre la magia visual

    // =========================================================================
    // 🧠 VARIABLES DE ESTADO Y CONFIGURACIONES GEOMÉTRICAS
    // =========================================================================
    private FileChooser fileChooser = new FileChooser();
    private ConfigLogisTEC configuracionGlobal; 
    private Grafo ciudadGrafo;                  
    private int nodoDeposito;                   
    private double factorEscala = 15.0; // Multiplicador para expandir coordenadas al tamaño del Pane

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configuramos el selector de archivos para que solo muestre filtros JSON válidos
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Archivos de Configuración LogisTEC (*.json)", "*.json")
        );
    }

    // =========================================================================
    // 🛎️ ACCIÓN: BOTÓN "CARGAR ARCHIVOS"
    // =========================================================================
    @FXML
    void cargarArchivos(ActionEvent event) {
        // Abre el cuadro de diálogo nativo del Sistema Operativo
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            // Actualizamos el indicador de texto de la UI con el nombre del archivo
            archivocargadolabel.setText(file.getName());

            // 1. Convertimos el texto JSON estructurado a objetos DTO de Java
            JsonParser parser = new JsonParser();
            configuracionGlobal = parser.parsearArchivo(file.getAbsolutePath());

            if (configuracionGlobal != null) {
                // 2. Extraemos el grafo de calles y localizamos el ID del Depósito
                ValidadorPaquetes validador = new ValidadorPaquetes(configuracionGlobal);
                ciudadGrafo = validador.construirGrafo();
                nodoDeposito = validador.encontrarDeposito();

                // 3. Limpiamos trazos anteriores por completo
                mapaPane.getChildren().clear();
                
                // 4. Invocamos al Cartógrafo para dibujar la infraestructura estática
                DibujanteMapa.dibujarCiudad(mapaPane, configuracionGlobal.ciudad(), factorEscala);
                
                System.out.println("UI: Infraestructura estática de la ciudad renderizada con éxito.");
            }
        }
    }

    // =========================================================================
    // 🛎️ ACCIÓN: BOTÓN "CALCULAR RUTAS"
    // =========================================================================
    @FXML
    void calcularRutas(ActionEvent event) {
        // Verificación de seguridad defensiva
        if (configuracionGlobal == null || ciudadGrafo == null) {
            System.err.println("Error de usuario: Debe cargar un archivo de configuración válido primero.");
            return;
        }

        // FASE 1: Distancias óptimas globales y filtrado de paquetes
        double[][] matrizFloyd = FloydWarshall.hacerFloydWarshall(ciudadGrafo);
        
        ValidadorPaquetes validador = new ValidadorPaquetes(configuracionGlobal);
        LinkedList<Paquete> inalcanzables = validador.validarPaquetes();
        Paquete[] paquetesAlcanzables = filtrarPaquetesAlcanzables(configuracionGlobal.paquetes(), inalcanzables);

        // FASE 2: Planificación de carga en camiones (Algoritmo Greedy Best-Fit)
        LogisticaPlanner planner = new LogisticaPlanner();
        ResultadoLogistica resultado = planner.asignarCarga(paquetesAlcanzables, inalcanzables, configuracionGlobal.camiones());

        // FASE 3: Enrutamiento comparativo simultáneo (MST vs Nearest Neighbor)
        EnrutadorMST enrutadorMST = new EnrutadorMST();
        EnrutadorNearestNeighbor enrutadorNN = new EnrutadorNearestNeighbor();

        LinkedList<LinkedList<Integer>> rutasFlotaMST = new LinkedList<>();
        LinkedList<LinkedList<Integer>> rutasFlotaNN = new LinkedList<>();
        
        LinkedList<Integer> destinosGlobales = new LinkedList<>();

        for (CamionPlanificado camionInfo : resultado.flota()) {
            LinkedList<Integer> destinos = extraerDestinosUnicos(camionInfo.getPaquetesCargados());
            
            for(int i = 0; i < destinos.size(); i++){
                destinosGlobales.insert(destinos.getAt(i));
            }
            
            
            // --- Heurística A (MST) ---
            LinkedList<Integer> macroRutaMST = enrutadorMST.generarRuta(nodoDeposito, destinos, matrizFloyd);
            // Dijkstra convierte los saltos imaginarios en calles reales
            LinkedList<Integer> microRutaMST = expandirRutaConDijkstra(macroRutaMST);
            // IMPORTANTE: Un solo insert por camión
            rutasFlotaMST.insert(microRutaMST);
            
            // --- Heurística B (Nearest Neighbor) ---
            LinkedList<Integer> macroRutaNN = enrutadorNN.generarRuta(nodoDeposito, destinos, matrizFloyd);
            // También le aplicamos Dijkstra al Vecino Más Cercano
            LinkedList<Integer> microRutaNN = expandirRutaConDijkstra(macroRutaNN);
            // IMPORTANTE: Un solo insert por camión
            rutasFlotaNN.insert(microRutaNN);
        }

        // FASE 4: Renderizado visual
        DibujanteRutas.dibujarRutas(mapaPane, rutasFlotaMST, configuracionGlobal.ciudad(), factorEscala);
        
        DibujanteRutas.dibujarDestinos(mapaPane, destinosGlobales, configuracionGlobal.ciudad(), factorEscala);
        
        // FASE 5: Persistencia del Reporte en Disco
        ExportadorCSV.generarReporte("reporte_logistica.csv", resultado, rutasFlotaMST, rutasFlotaNN);
        
        GeneradorEstadisticas.mostrarPanel(resultado, rutasFlotaMST, rutasFlotaNN, matrizFloyd);

        System.out.println("UI: Simulación logística completada. Mapa actualizado y reporte .csv exportado.");
    }
    
    // =========================================================================
    // 🧰 MÉTODOS DE SOPORTE PRIVADOS (TRANSFORMADORES DE MODELOS DE DATOS)
    // =========================================================================
    
    /**
     * Filtra la lista cruda removiendo los paquetes dictaminados como inalcanzables por Warshall.
     */
    private Paquete[] filtrarPaquetesAlcanzables(Paquete[] todos, LinkedList<Paquete> inalcanzables) {
        int alcanzablesCount = todos.length - inalcanzables.size();
        Paquete[] alcanzables = new Paquete[alcanzablesCount];
        int idx = 0;
        
        for (Paquete p : todos) {
            boolean esInalcanzable = false;
            for (int i = 0; i < inalcanzables.size(); i++) {
                if (inalcanzables.getAt(i).id() == p.id()) { 
                    esInalcanzable = true; 
                    break; 
                }
            }
            if (!esInalcanzable) { 
                alcanzables[idx++] = p; 
            }
        }
        return alcanzables;
    }

    /**
     * Recorre la lista de paquetes asignados a un camión para extraer sus códigos de destino únicos.
     */
    private LinkedList<Integer> extraerDestinosUnicos(LinkedList<Paquete> paquetes) {
        LinkedList<Integer> destinos = new LinkedList<>();
        for (int i = 0; i < paquetes.size(); i++) {
            int destinoId = paquetes.getAt(i).destino();
            
            // Evitamos insertar duplicados en la lista de destinos de paradas viales
            boolean yaExiste = false;
            for (int j = 0; j < destinos.size(); j++) {
                if (destinos.getAt(j) == destinoId) {
                    yaExiste = true;
                    break;
                }
            }
            if (!yaExiste) {
                destinos.insert(destinoId);
            }
        }
        return destinos;
    }
    
    /**
     * Toma una "Macro-Ruta" (solo paradas) y usa Dijkstra para rellenar las calles
     * intermedias y formar la "Micro-Ruta" real que recorrerá el camión.
     */
    private LinkedList<Integer> expandirRutaConDijkstra(LinkedList<Integer> macroRuta) {
        if (macroRuta == null || macroRuta.size() < 2) return macroRuta;
        
        LinkedList<Integer> rutaFisicaCompleta = new LinkedList<>();
        Dijkstra dijkstra = new Dijkstra(ciudadGrafo); // Instanciamos la clase de tu compañero
        
        // Empezamos insertando el punto de salida
        rutaFisicaCompleta.insert(macroRuta.getAt(0));

        // Recorremos las paradas de 2 en 2 (Ej: de 0 a 4, luego de 4 a 0)
        for (int i = 0; i < macroRuta.size() - 1; i++) {
            int origen = macroRuta.getAt(i);
            int destino = macroRuta.getAt(i + 1);
            
            // ¡AQUÍ ES DONDE SE UTILIZA DIJKSTRA!
            // Le pedimos a Dijkstra que nos calcule el camino calle por calle
            LinkedList<Integer> caminoIntermedio = dijkstra.calcular(origen, destino);
            
            // Insertamos las calles intermedias en nuestra ruta final (ignorando el origen 
            // para no duplicarlo, ya que el destino del paso anterior es el origen del actual)
            for (int j = 1; j < caminoIntermedio.size(); j++) {
                rutaFisicaCompleta.insert(caminoIntermedio.getAt(j));
            }
        }
        
        return rutaFisicaCompleta;
    }
    
    
    
    
}