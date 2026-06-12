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

// --- 🤝 CON QUIÉN SE COMUNICA EL CONTROLADOR ---
import datos.JsonParser;          // Cocina: Para leer el archivo JSON
import datos.ConfigLogisTEC;       // Datos: El contenedor de toda la info del JSON
import datos.Paquete;             // Datos: Modelo de un paquete
import estructuras.LinkedList;    // Estructuras: Tu TDA personalizado sin java.util
import estructuras.Grafo;         // Estructuras: Tu representación de la ciudad
import datos.ValidadorPaquetes;   // Filtro: Para limpiar paquetes y armar el grafo base
import algoritmos.FloydWarshall;   // Algoritmos: Para calcular distancias cortas reales
import planner.LogisticaPlanner;  // Logística: Tu Best-Fit para cargar camiones
import planner.CamionPlanificado; // Logística: Envoltura mutables de los camiones
import planner.ResultadoLogistica;// Logística: El reporte final del Best-Fit
import planner.EnrutadorMST;      // Algoritmos: Tu heurística de ruteo con Prim y DFS

public class Controller implements Initializable {

    // ==========================================
    // 📺 CONEXIONES CON EL ARCHIVO FXML (VISTA)
    // ==========================================
    @FXML private Button cargararchivosButton;
    @FXML private Button calcularrutasButton;
    @FXML private Label archivocargadolabel;
    @FXML private Pane mapaPane; // El lienzo de 600x600 donde pintaremos

    // ==========================================
    // 🧠 VARIABLES DE ESTADO (MEMORIA DEL CONTROLADOR)
    // ==========================================
    private FileChooser fileChooser = new FileChooser();
    private ConfigLogisTEC configuracionGlobal; // Guarda los camiones y paquetes crudos
    private Grafo ciudadGrafo;                  // El grafo físico de la ciudad
    private int nodoDeposito;                   // El ID de la ciudad base (DEPOT)
    private double factorEscala = 15.0;         // Factor para expandir el dibujo

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Se ejecuta automáticamente al abrir la ventana.
        // Aquí configuramos el buscador de archivos para que solo muestre archivos .json
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Archivos JSON (*.json)", "*.json")
        );
    }

    // ==========================================
    // 🛎️ ACCIÓN 1: CARGAR EL ARCHIVO JSON
    // ==========================================
    @FXML
    void cargarArchivos(ActionEvent event) {
        // Habla con el sistema operativo para abrir la ventana de selección de archivos
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            // Modifica un texto en la pantalla para mostrar el nombre del archivo cargado
            archivocargadolabel.setText(file.getName());

            // 1. Habla con JsonParser para convertir el texto plano en objetos Java (Records)
            JsonParser parser = new JsonParser();
            configuracionGlobal = parser.parsearArchivo(file.getAbsolutePath());

            if (configuracionGlobal != null) {
                // 2. Habla con el Validador de Paquetes para inicializar el Grafo de la ciudad
                ValidadorPaquetes validador = new ValidadorPaquetes(configuracionGlobal);
                ciudadGrafo = validador.construirGrafo();
                nodoDeposito = validador.encontrarDeposito();

                // 3. Limpia el lienzo visual por si había un mapa viejo dibujado
                mapaPane.getChildren().clear();
                
                // 4. [Aquí llamará a Dev B] para que pinte los círculos y calles estáticas
                DibujanteMapa.dibujarCiudad(mapaPane, configuracionGlobal.ciudad(), factorEscala);
                
                System.out.println("Backend: Grafo y depósito inicializados correctamente.");
            }
        }
    }

    // ==========================================
    // 🛎️ ACCIÓN 2: CALCULAR LA LOGÍSTICA Y RUTAS
    // ==========================================
    @FXML
    void calcularRutas(ActionEvent event) {
        // Guardia defensiva: Si no han cargado un archivo, no hacemos nada
        if (configuracionGlobal == null || ciudadGrafo == null) {
            System.err.println("Error: No se ha cargado ninguna configuración de ciudad.");
            return;
        }

        // PASO 1: Habla con FloydWarshall para obtener la matriz de distancias optimizadas
        double[][] matrizFloyd = FloydWarshall.hacerFloydWarshall(ciudadGrafo);
        
        // PASO 2: Habla con ValidadorPaquetes para ejecutar Warshall y aislar los paquetes inalcanzables
        ValidadorPaquetes validador = new ValidadorPaquetes(configuracionGlobal);
        LinkedList<Paquete> inalcanzables = validador.validarPaquetes();
        
        // Filtramos en memoria los paquetes que sí son alcanzables
        Paquete[] paquetesAlcanzables = filtrarPaquetesAlcanzables(configuracionGlobal.paquetes(), inalcanzables);

        // PASO 3: Habla con LogisticaPlanner para ejecutar el Best-Fit (Empacar camiones)
        LogisticaPlanner planner = new LogisticaPlanner();
        ResultadoLogistica resultado = planner.asignarCarga(paquetesAlcanzables, inalcanzables, configuracionGlobal.camiones());

        // PASO 4: Habla con EnrutadorMST para trazar la ruta de cada camión cargado
        EnrutadorMST enrutador = new EnrutadorMST();
        LinkedList<LinkedList<Integer>> rutasDeTodaLaFlota = new LinkedList<>();

        // Navegamos por los camiones que devolvió el Best-Fit
        for (CamionPlanificado camionInfo : resultado.flota()) {
            // Extraemos los destinos únicos de los paquetes que este camión aceptó llevar
            LinkedList<Integer> destinosCamion = extraerDestinosUnicos(camionInfo.getPaquetesCargados());
            
            // Le pedimos al enrutador heurístico que calcule la secuencia óptima (MST + DFS Preorden)
            LinkedList<Integer> rutaCalculada = enrutador.generarRuta(nodoDeposito, destinosCamion, matrizFloyd);
            
            // Guardamos la ruta de este camión en nuestra lista global de rutas
            rutasDeTodaLaFlota.insert(rutaCalculada);
        }

        // PASO 5: Habla contigo mismo en tu rol de Dev C (DibujanteRutas)
        // Le pasas el lienzo visual y la lista de rutas calculadas matemáticas para que las pinte
        DibujanteRutas.dibujarRutas(mapaPane, rutasDeTodaLaFlota, configuracionGlobal.ciudad(), factorEscala);
    }
    
    // ==========================================
    // 🧰 MÉTODOS AUXILIARES DE SOPORTE (TRANSFORMADORES DE DATA)
    // ==========================================
    private Paquete[] filtrarPaquetesAlcanzables(Paquete[] todos, LinkedList<Paquete> inalcanzables) {
        int alcanzablesCount = todos.length - inalcanzables.size();
        Paquete[] alcanzables = new Paquete[alcanzablesCount];
        int idx = 0;
        for (Paquete p : todos) {
            boolean esMalo = false;
            for (int i = 0; i < inalcanzables.size(); i++) {
                if (inalcanzables.getAt(i).id() == p.id()) { esMalo = true; break; }
            }
            if (!esMalo) { alcanzables[idx++] = p; }
        }
        return alcanzables;
    }	

    private LinkedList<Integer> extraerDestinosUnicos(LinkedList<Paquete> paquetes) {
        LinkedList<Integer> destinos = new LinkedList<>();
        for (int i = 0; i < paquetes.size(); i++) {
            destinos.insert(paquetes.getAt(i).destino());
        }
        return destinos;
    }
}