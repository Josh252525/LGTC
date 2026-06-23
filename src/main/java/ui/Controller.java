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

    // =========================================================================
    // COMPONENTES VISUALES COMPATIBLES CON TU FXML
    // =========================================================================
    @FXML private Button cargararchivosButton;
    @FXML private Button calcularrutasButton;
    @FXML private Label archivocargadolabel;
    @FXML private Pane mapaPane; 

    // =========================================================================
    // 🧠 VARIABLES DE ESTADO Y CORE LOGÍSTICO
    // =========================================================================
    private ConfigLogisTEC config;
    private Grafo ciudadGrafo;
    private double[][] matrizFloyd;
    private int depositoId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        archivocargadolabel.setText("📥 Esperando archivo JSON de configuración...");
    }

    @FXML
    private void cargarArchivos(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar Caso de Prueba JSON");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos JSON (*.json)", "*.json")
            );
            
            File archivoSeleccionado = fileChooser.showOpenDialog(new Stage());
            
            if (archivoSeleccionado != null) {
                // 1. Cargar datos usando tu Lector nativo
                JsonParser parser = new JsonParser();
                this.config = parser.parsearArchivo(archivoSeleccionado.getAbsolutePath());
                
                // 2. Construir la infraestructura del Grafo en memoria para Dijkstra
                this.ciudadGrafo = new Grafo(config.ciudad().vertices().length);
                for (datos.Arista arista : config.ciudad().aristas()) {
                    this.ciudadGrafo.agregarArista(arista.u(), arista.v(), arista.distancia());
                }
                
                // 3. Ubicar la base de operaciones (DEPOT)
                ValidadorPaquetes validadorInicial = new ValidadorPaquetes(config);
                this.depositoId = validadorInicial.encontrarDeposito();
                
                // 4. Calcular Súper Matriz de caminos mínimos usando tu firma ESTÁTICA real
                this.matrizFloyd = FloydWarshall.hacerFloydWarshall(ciudadGrafo);
                
                // 5. Renderizar infraestructura estática de la ciudad en el canvas de JavaFX
                mapaPane.getChildren().clear();
                DibujanteMapa.dibujarCiudad(mapaPane, config.ciudad(), 1.0);
                
                archivocargadolabel.setText("✅ Archivo cargado con éxito: " + archivoSeleccionado.getName());
            }
        } catch (Exception e) {
            archivocargadolabel.setText("❌ Error al cargar el archivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void calcularRutas(ActionEvent event) {
        if (config == null || ciudadGrafo == null || matrizFloyd == null) {
            archivocargadolabel.setText("❌ Primero debes cargar un archivo JSON válido.");
            return;
        }

        // =====================================================================
        // FILTRADO MANUAL DE PAQUETES ALCANZABLES (Soporte al Validador nativo)
        // =====================================================================
        ValidadorPaquetes validador = new ValidadorPaquetes(config);
        LinkedList<Paquete> inalcanzables = validador.validarPaquetes();
        
        LinkedList<Paquete> listaAlcanzables = new LinkedList<>();
        for (Paquete p : config.paquetes()) {
            if (inalcanzables.searchFor(p) == -1) {
                listaAlcanzables.insert(p);
            }
        }
        
        Paquete[] paquetesAlcanzables = new Paquete[listaAlcanzables.size()];
        for (int i = 0; i < listaAlcanzables.size(); i++) {
            paquetesAlcanzables[i] = listaAlcanzables.getAt(i);
        }

        // =====================================================================
        // 🚀 FASE 1: ROUTE-FIRST (Generar la Ruta Maestra Guía de la Ciudad)
        // =====================================================================
        LinkedList<Integer> destinosGlobales = new LinkedList<>();
        for (Paquete p : paquetesAlcanzables) {
            int dest = p.destino();
            if (destinosGlobales.searchFor(dest) == -1) {
                destinosGlobales.insert(dest);
            }
        }
        
        EnrutadorMST enrutadorMaestro = new EnrutadorMST();
        LinkedList<Integer> rutaMaestraGlobal = enrutadorMaestro.generarRuta(depositoId, destinosGlobales, matrizFloyd);

        // =====================================================================
        // 📦 FASE 2: CLUSTER-SECOND (El planner empaca respetando el orden)
        // =====================================================================
        LogisticaPlanner planner = new LogisticaPlanner();
        ResultadoLogistica resultado = planner.asignarCarga(paquetesAlcanzables, inalcanzables, config.camiones(), rutaMaestraGlobal);

        // =====================================================================
        // 🛣️ FASE 3: ENRUTAMIENTO INDIVIDUAL CON CRONÓMETRO DE CPU
        // =====================================================================
        LinkedList<LinkedList<Integer>> rutasFlotaMST = new LinkedList<>();
        LinkedList<LinkedList<Integer>> rutasFlotaNN = new LinkedList<>();
        
        // Listas nuevas para almacenar los tiempos por camión
        LinkedList<Long> tiemposMST = new LinkedList<>();
        LinkedList<Long> tiemposNN = new LinkedList<>();

        EnrutadorMST enrutadorMST = new EnrutadorMST();
        EnrutadorNearestNeighbor enrutadorNN = new EnrutadorNearestNeighbor();

        for (int i = 0; i < resultado.flota().length; i++) {
            CamionPlanificado camion = resultado.flota()[i];
            LinkedList<Integer> destinosCamion = extraerDestinos(camion.getPaquetesCargados());

            if (destinosCamion.size() > 0) {
                
                // ⏱️ Medición de la Estrategia MST
                long inicioMST = System.nanoTime();
                LinkedList<Integer> macroMST = enrutadorMST.generarRuta(depositoId, destinosCamion, matrizFloyd);
                LinkedList<Integer> microMST = expandirRutaConDijkstra(macroMST);
                long finMST = System.nanoTime();
                tiemposMST.insert(finMST - inicioMST);

                // ⏱️ Medición de la Estrategia Nearest Neighbor
                long inicioNN = System.nanoTime();
                LinkedList<Integer> macroNN = enrutadorNN.generarRuta(depositoId, destinosCamion, matrizFloyd);
                LinkedList<Integer> microNN = expandirRutaConDijkstra(macroNN);
                long finNN = System.nanoTime();
                tiemposNN.insert(finNN - inicioNN);

                rutasFlotaMST.insert(microMST);
                rutasFlotaNN.insert(microNN);
            } else {
                LinkedList<Integer> rutaVacia = new LinkedList<>();
                rutaVacia.insert(depositoId);
                rutasFlotaMST.insert(rutaVacia);
                rutasFlotaNN.insert(rutaVacia);
                
                // Si está vacío, tardó 0 nanosegundos
                tiemposMST.insert(0L);
                tiemposNN.insert(0L);
            }
        }

     // =====================================================================
        // 4. GUARDAR RESULTADOS EN DISCO Y DIBUJAR
        // =====================================================================
        // Le pasamos los tiempos al exportador para que los ponga en el CSV
        ExportadorCSV.generarReporte("reporte_logistica.csv", resultado, rutasFlotaMST, rutasFlotaNN, tiemposMST, tiemposNN);
        archivocargadolabel.setText("✅ Rutas calculadas y reporte_logistica.csv exportado.");

        DibujanteRutas.dibujarRutas(mapaPane, rutasFlotaMST, config.ciudad(), 1.0);
        DibujanteRutas.dibujarDestinos(mapaPane, destinosGlobales, config.ciudad(), 1.0);

        // =====================================================================
        // 5. DIBUJAR LAS RUTAS EN EL MAPA 
        // =====================================================================
        DibujanteRutas.dibujarRutas(mapaPane, rutasFlotaMST, config.ciudad(), 1.0);
        DibujanteRutas.dibujarDestinos(mapaPane, destinosGlobales, config.ciudad(), 1.0);

        // =====================================================================
        // 6. LANZAR PANEL ANALÍTICO DE ESTADÍSTICAS (¡AHORA CON TIEMPOS!)
        // =====================================================================
        GeneradorEstadisticas.mostrarPanel(resultado, rutasFlotaMST, rutasFlotaNN, tiemposMST, tiemposNN, matrizFloyd, ciudadGrafo);
    }

    private LinkedList<Integer> extraerDestinos(LinkedList<Paquete> paquetes) {
        LinkedList<Integer> destinos = new LinkedList<>();
        for (int i = 0; i < paquetes.size(); i++) {
            int destinoId = paquetes.getAt(i).destino();
            if (destinos.searchFor(destinoId) == -1) {
                destinos.insert(destinoId);
            }
        }
        return destinos;
    }
    
    private LinkedList<Integer> expandirRutaConDijkstra(LinkedList<Integer> macroRuta) {
        if (macroRuta == null || macroRuta.size() < 2) return macroRuta;
        
        LinkedList<Integer> rutaFisicaCompleta = new LinkedList<>();
        Dijkstra dijkstra = new Dijkstra(ciudadGrafo); 
        
        rutaFisicaCompleta.insert(macroRuta.getAt(0));

        for (int i = 0; i < macroRuta.size() - 1; i++) {
            int origen = macroRuta.getAt(i);
            int destino = macroRuta.getAt(i + 1);
           
            LinkedList<Integer> caminoIntermedio = dijkstra.calcular(origen, destino);
            
            // Insertamos omitiendo el primero para no duplicar la parada de empalme
            for (int j = 1; j < caminoIntermedio.size(); j++) {
                rutaFisicaCompleta.insert(caminoIntermedio.getAt(j));
            }
        }
        return rutaFisicaCompleta;
    }
}