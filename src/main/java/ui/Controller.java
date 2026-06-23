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

import datos.JsonParser;                 
import datos.ConfigLogisTEC;             
import datos.Paquete;                   
import datos.ValidadorPaquetes;       
import datos.ExportadorCSV;             
import estructuras.LinkedList;      
import estructuras.Grafo;               
import algoritmos.FloydWarshall;          
import algoritmos.Dijkstra;
import planner.LogisticaPlanner;         
import planner.CamionPlanificado;        
import planner.ResultadoLogistica;        
import planner.EnrutadorMST;              
import planner.EnrutadorNearestNeighbor;  


/**
 * Clase Controladora de la Ventana Principal (Patrón MVC).
 * Actúa como el puente definitivo de comunicación entre la UI y los algoritmos del backend.
 */
public class Controller implements Initializable {

    // ====================
    // COMPONENTES VISUALES 
    // ====================
    @FXML private Button cargararchivosButton;
    @FXML private Button calcularrutasButton;
    @FXML private Label archivocargadolabel;
    @FXML private Pane mapaPane; 

    // =========================================================================
    // 🧠 VARIABLES DE ESTADO Y CONFIGURACIONES GEOMÉTRICAS
    // =========================================================================
    private FileChooser fileChooser = new FileChooser();
    private ConfigLogisTEC configuracionGlobal; 
    private Grafo ciudadGrafo;                  
    private int nodoDeposito;                   
    private double factorEscala = 1.0; // Multiplicador para expandir coordenadas al tamaño del Pane

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    	
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Archivos de Configuración LogisTEC (*.json)", "*.json")
        );
    }

    // =======================
    // BOTÓN "CARGAR ARCHIVOS"
    // =======================
    @FXML
    void cargarArchivos(ActionEvent event) {
        // Abre el cuadro de diálogo nativo del Sistema Operativo
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            // Actualizamos el indicador de texto de la UI con el nombre del archivo
            archivocargadolabel.setText(file.getName());

            JsonParser parser = new JsonParser();
            configuracionGlobal = parser.parsearArchivo(file.getAbsolutePath());

            if (configuracionGlobal != null) {
                // Se extrae el grafo de calles y se localiza el ID del Depósito
            	
                ValidadorPaquetes validador = new ValidadorPaquetes(configuracionGlobal);
                ciudadGrafo = validador.construirGrafo();
                nodoDeposito = validador.encontrarDeposito();

                // Se limpian trazos anteriores por completo
                mapaPane.getChildren().clear();
                
                // Dibuja la ciudad en gris
                DibujanteMapa.dibujarCiudad(mapaPane, configuracionGlobal.ciudad(), factorEscala);
                
                System.out.println("UI: Infraestructura estática de la ciudad renderizada con éxito.");
            }
        }
    }

    // ======================
    // BOTÓN "CALCULAR RUTAS"
    // ======================
    @FXML
    void calcularRutas(ActionEvent event) {
    	
        // Verificación de datos de configuración nulos
        if (configuracionGlobal == null || ciudadGrafo == null) {
            System.err.println("Error de usuario: Debe cargar un archivo de configuración válido primero.");
            return;
        }

        // Distancias óptimas globales y filtrado de paquetes
        double[][] matrizFloyd = FloydWarshall.hacerFloydWarshall(ciudadGrafo);
        
        ValidadorPaquetes validador = new ValidadorPaquetes(configuracionGlobal);
        LinkedList<Paquete> inalcanzables = validador.validarPaquetes();
        Paquete[] paquetesAlcanzables = filtrarPaquetesAlcanzables(configuracionGlobal.paquetes(), inalcanzables);

        // Planificación de carga en camiones 
        LogisticaPlanner planner = new LogisticaPlanner();
        ResultadoLogistica resultado = planner.asignarCarga(paquetesAlcanzables, inalcanzables, configuracionGlobal.camiones());

        // Enrutamiento comparativo simultáneo (MST vs Nearest Neighbor)
        
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
            
            
            // --- MST ---
            
            LinkedList<Integer> macroRutaMST = enrutadorMST.generarRuta(nodoDeposito, destinos, matrizFloyd);
            // Dijkstra
            LinkedList<Integer> microRutaMST = expandirRutaConDijkstra(macroRutaMST);
            
            // IMPORTANTE: Un solo insert por camión
            rutasFlotaMST.insert(microRutaMST);
            
            // --- Nearest Neighbor ---
            
            LinkedList<Integer> macroRutaNN = enrutadorNN.generarRuta(nodoDeposito, destinos, matrizFloyd);
            // Dijkstra al Vecino Más Cercano
            LinkedList<Integer> microRutaNN = expandirRutaConDijkstra(macroRutaNN);
            // IMPORTANTE: Un solo insert por camión
            rutasFlotaNN.insert(microRutaNN);
        }

        // Renderizado visual
        DibujanteRutas.dibujarRutas(mapaPane, rutasFlotaMST, configuracionGlobal.ciudad(), factorEscala);
        
        DibujanteRutas.dibujarDestinos(mapaPane, destinosGlobales, configuracionGlobal.ciudad(), factorEscala);
        
        // Reporte en CSV
        ExportadorCSV.generarReporte("reporte_logistica.csv", resultado, rutasFlotaMST, rutasFlotaNN);
        
        GeneradorEstadisticas.mostrarPanel(resultado, rutasFlotaMST, rutasFlotaNN, matrizFloyd, ciudadGrafo);

        System.out.println("Mapa actualizado y reporte .csv exportado.");
    }
    
    // ==================
    // MÉTODOS AUXILIARES
    // ==================
    
    /**
     * Filtra la lista removiendo los paquetes reportados como inalcanzables por Warshall.
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
     * Toma una Macro-Ruta y usa Dijkstra para rellenar las calles
     * intermedias y formar la Micro-Ruta real que recorrerá el camión.
     */
    private LinkedList<Integer> expandirRutaConDijkstra(LinkedList<Integer> macroRuta) {
        if (macroRuta == null || macroRuta.size() < 2) return macroRuta;
        
        LinkedList<Integer> rutaFisicaCompleta = new LinkedList<>();
        Dijkstra dijkstra = new Dijkstra(ciudadGrafo); 
        
        rutaFisicaCompleta.insert(macroRuta.getAt(0));

        // Se recorren los vértices de 2 en 2
        for (int i = 0; i < macroRuta.size() - 1; i++) {
            int origen = macroRuta.getAt(i);
            int destino = macroRuta.getAt(i + 1);
           
            LinkedList<Integer> caminoIntermedio = dijkstra.calcular(origen, destino);
            
            // Insertamos las calles intermedias en nuestra ruta final ignorando el origen 
            for (int j = 1; j < caminoIntermedio.size(); j++) {
                rutaFisicaCompleta.insert(caminoIntermedio.getAt(j));
            }
        }
        
        return rutaFisicaCompleta;
    }
    
    
    
    
}