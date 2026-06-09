package planner;

import estructuras.Grafo;
import estructuras.LinkedList;
import estructuras.Conexion;
import algoritmos.Prim;

/*
Documentación:
Enrutador Logístico basado en la Heurística de Árbol de Expansión Mínima (MST).
Aproxima una solución óptima para el problema del Agente Viajero (TSP) generando
rutas de entrega eficientes y seguras.
*/
public class EnrutadorMST {

    /**
     * Genera la ruta de entrega óptima (aproximada) para un camión.
     * @param deposito        ID de la ciudad base de operaciones (origen/destino final).
     * @param destinosCamion  Lista con las ciudades asignadas a este camión por el Best-Fit.
     * @param matrizFloyd     Matriz de distancias mínimas calculada por Floyd-Warshall.
     * @return LinkedList con la secuencia exacta de IDs de ciudades en el orden de visita.
     */
    public LinkedList<Integer> generarRuta(int deposito, LinkedList<Integer> destinosCamion, double[][] matrizFloyd) {
        
        // --- 🛡️ CONTROL DE GUARDIAS BÁSICAS ---
        if (matrizFloyd == null || matrizFloyd.length == 0) {
            throw new IllegalArgumentException("Error: La matriz de distancias de Floyd-Warshall no puede ser nula ni vacía.");
        }
        if (destinosCamion == null) {
            throw new IllegalArgumentException("Error: La lista de destinos no puede ser nula.");
        }
        if (deposito < 0 || deposito >= matrizFloyd.length) {
            throw new IllegalArgumentException("Error: El nodo de depósito " + deposito + " está fuera de los límites del mapa.");
        }

        // --- 🛡️ CASO EXTREMO 1: El camión no tiene entregas ---
        if (destinosCamion.size() == 0) {
            LinkedList<Integer> rutaVacia = new LinkedList<>();
            rutaVacia.insert(deposito);
            return rutaVacia; // El camión se queda en la base
        }

        // --- 🛡️ FILTRADO DE DUPLICADOS Y LIMPIEZA DE DATOS ---
        LinkedList<Integer> ciudadesUnicas = new LinkedList<>();
        for (int i = 0; i < destinosCamion.size(); i++) {
            int ciudad = destinosCamion.getAt(i);
            
            // Validar límites de los IDs de las ciudades entregadas
            if (ciudad < 0 || ciudad >= matrizFloyd.length) {
                throw new IllegalArgumentException("Error: El destino con ID " + ciudad + " no existe en el mapa.");
            }
            
            // Ignorar si es el depósito o si ya está en la lista de nodos a visitar
            if (ciudad != deposito && !contieneElemento(ciudadesUnicas, ciudad)) {
                ciudadesUnicas.insert(ciudad);
            }
        }

        // --- 🛡️ CASO EXTREMO 2: Todas las entregas eran duplicados del depósito o base ---
        if (ciudadesUnicas.size() == 0) {
            LinkedList<Integer> rutaRetornoDirecto = new LinkedList<>();
            rutaRetornoDirecto.insert(deposito);
            return rutaRetornoDirecto;
        }

        // --- PASO 1: EXTRACCIÓN DEL SUBGRAFO INDUCIDO ---
        int cantidadNodosSubgrafo = ciudadesUnicas.size() + 1; // Ciudades + Depósito
        Grafo subgrafoInducido = new Grafo(cantidadNodosSubgrafo);
        
        // Mapeo inverso: guarda qué índice local (0 a N) corresponde a cuál ciudad del grafo principal
        int[] mapaGlobalACiudad = new int[cantidadNodosSubgrafo];
        mapaGlobalACiudad[0] = deposito; // El nodo 0 local SIEMPRE es el depósito
        
        for (int i = 0; i < ciudadesUnicas.size(); i++) {
            mapaGlobalACiudad[i + 1] = ciudadesUnicas.getAt(i);
        }

        // Construir la matriz de adyacencia densa del subgrafo usando Floyd-Warshall
        for (int i = 0; i < cantidadNodosSubgrafo; i++) {
            for (int j = i + 1; j < cantidadNodosSubgrafo; j++) {
                int uGlobal = mapaGlobalACiudad[i];
                int vGlobal = mapaGlobalACiudad[j];
                
                double distanciaFloyd = matrizFloyd[uGlobal][vGlobal];
                
                // 🛡️ CASO EXTREMO 3: Tratar de rutear hacia un nodo inalcanzable en el mapa
                if (distanciaFloyd == Double.POSITIVE_INFINITY) {
                    throw new IllegalStateException("Error Crítico: No existe ninguna ruta alcanzable entre la ciudad " 
                            + uGlobal + " y la ciudad " + vGlobal + ". Revisar filtro de Warshall.");
                }
                
                // Conexión bidireccional simétrica
                subgrafoInducido.agregarArista(i, j, distanciaFloyd);
                subgrafoInducido.agregarArista(j, i, distanciaFloyd);
            }
        }

        // --- PASO 2: CONSTRUCCIÓN DEL MST ---
        // Instanciamos el Prim robusto que modificamos anteriormente
        Prim solucionadorPrim = new Prim(subgrafoInducido);
        Grafo arbolMST = solucionadorPrim.obtenerGrafoMST();

        // --- PASO 3: RECORRIDO DFS EN PREORDEN CON ATAJOS ---
        LinkedList<Integer> ordenVisitaLocal = new LinkedList<>();
        boolean[] visitadosLocales = new boolean[cantidadNodosSubgrafo];
        
        // Comenzamos el preorden desde el depósito local (índice 0)
        ejecutarDFSPreorden(0, arbolMST, visitadosLocales, ordenVisitaLocal);

        // --- PASO 4: TRADUCCIÓN GLOBAL Y CIERRE DEL CIRCUITO ---
        LinkedList<Integer> rutaFinalCompleta = new LinkedList<>();
        for (int i = 0; i < ordenVisitaLocal.size(); i++) {
            int idLocal = ordenVisitaLocal.getAt(i);
            rutaFinalCompleta.insert(mapaGlobalACiudad[idLocal]); // Traducimos a ID real del mapa
        }
        
        // Regla logística obligatoria: Volver a la base al terminar las entregas
        rutaFinalCompleta.insert(deposito);

        return rutaFinalCompleta;
    }

    // Algoritmo de DFS en Preorden puro
    private void ejecutarDFSPreorden(int actual, Grafo arbolMST, boolean[] visitados, LinkedList<Integer> resultado) {
        visitados[actual] = true;
        resultado.insert(actual); // Operación de Preorden: Registrar inmediatamente al entrar

        LinkedList<Conexion> vecinos = arbolMST.getVecinos(actual);
        for (int i = 0; i < vecinos.size(); i++) {
            int destinoVecino = vecinos.getAt(i).destino;
            if (!visitados[destinoVecino]) {
                ejecutarDFSPreorden(destinoVecino, arbolMST, visitados, resultado);
            }
        }
    }

    // Método de soporte para evitar el uso de 'contains()' de java.util
    private boolean contieneElemento(LinkedList<Integer> lista, int elemento) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.getAt(i) == elemento) {
                return true;
            }
        }
        return false;
    }
}