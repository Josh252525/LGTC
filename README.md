# LogísTEC (Sistema de Optimización Logística Urbana - Código: LGTC)

LogísTEC es una plataforma de software de grado empresarial desarrollada para la resolución del problema de optimización de rutas de distribución física de mercancías y el empaquetado tridimensional de carga dentro de redes urbanas complejas. El núcleo del sistema modela la infraestructura de transporte mediante abstracciones matemáticas de grafos ponderados y no dirigidos, aplicando algoritmos de optimización combinatoria y heurísticas avanzadas de enrutamiento y empaquetado.

Este sistema ha sido desarrollado bajo los lineamientos académicos e ingenieriles del Tecnológico de Costa Rica para el curso de Algoritmos y Estructuras de Datos I (CE1103).

---

# 1. Especificaciones de Arquitectura y Estructuras de Datos Core

Para cumplir con las restricciones de rendimiento de memoria, exclusión de recolectores automáticos ineficientes y control estricto sobre las complejidades temporales y espaciales (Big-O), se prohibió la importación del paquete funcional `java.util.*` en los módulos de procesamiento lógico.

## 1.1 Tipos Abstractos de Datos (TAD) Propios

El sistema cuenta con un paquete aislado de estructuras lineales y jerárquicas genéricas implementadas desde cero:

* **LinkedList<T> (Lista Enlazada Simple Genérica):** Estructura base para el almacenamiento dinámico con punteros explícitos a nodos adyacentes, optimizada con métodos de búsqueda binaria y secuencial integrada (`searchFor`) para la supresión indexada de elementos en O(1) tras localización.
* **Grafo por Listas de Adyacencia:** Representación dispersa de la matriz urbana. Minimiza la complejidad espacial a O(V + A), permitiendo consultas aceleradas de vecinos mediante el filtrado inmediato de aristas interconectadas.
* **Colas de Prioridad (Binary Heaps):** Utilizadas para la optimización interna del ordenamiento dinámico de fronteras de exploración en los algoritmos de expansión mínima.

## 1.2 Motores Algorítmicos Integrados

* **Clausura Transitiva (Algoritmo de Warshall):** Ejecutado en la fase de preprocesamiento logístico para calcular la matriz de alcanzabilidad booleana, aislando las solicitudes dirigidas a subgrafos desconectados.
* **Caminos Mínimos Globales (Algoritmo de Floyd-Warshall):** Genera la matriz de distancias geodésicas inter-nodales completas con una complejidad temporal estricta de O(V³), sirviendo como caché de consulta O(1) para las heurísticas de ruteo.
* **Ruteo de Trayectorias (Algoritmo de Dijkstra):** Utilizado para expandir las macro-secuencias lógicas en micro-rutas físicas reales calle por calle, evitando colisiones lógicas en aristas inexistentes.

---

# 2. Requisitos de Infraestructura y Entorno

El sistema operativo y los componentes de hardware deben cumplir con la siguiente matriz de compatibilidad para garantizar la estabilidad de los hilos de renderizado y el procesamiento de matrices tridimensionales.

## 2.1 Requisitos de Software y Compatibilidad de Sistema Operativo

### Sistemas Operativos Soportados

* **GNU/Linux:** Distribuciones basadas en Debian/RHEL (Ubuntu 20.04 LTS o superior verificado).
* **Microsoft Windows:** Windows 10 y Windows 11 arquitecturas x64 de forma nativa.
* **macOS:** Versión 12 (Monterey) o superior con soporte para arquitecturas Intel y Apple Silicon.

### Dependencias

* **Java Development Kit (JDK):** Versión 17 LTS.
* **Apache Maven:** Versión 3.8.1 o superior.
* **JavaFX:** Versión 17.
* **Google GSON:** Versión 2.10.1.

## 2.2 Requisitos Mínimos y Recomendados de Hardware

### Procesador (CPU)

* Mínimo: Dual-Core 2.4 GHz x64.
* Recomendado: Quad-Core 3.0 GHz o superior.

### Memoria RAM

* Mínimo: 4 GB.
* Recomendado: 8 GB.

### Almacenamiento

* 200 MB de espacio libre para binarios, registros y reportes `.csv`.

---

# 3. Guía de Instalación y Configuración del Sistema

Siga la siguiente secuencia de comandos dentro de una terminal Unix o PowerShell.

## 3.1 Clonación del Repositorio

```bash
git clone https://github.com/tu-usuario/LGTC.git
cd LGTC
```

## 3.2 Compilación y Verificación

```bash
mvn clean compile
```

## 3.3 Ejecución en Entornos CI sin Monitor

Debido a que el proyecto cuenta con pruebas automáticas que validan componentes JavaFX, en entornos de integración continua puede requerirse una pantalla virtual mediante `xvfb`.

```bash
xvfb-run mvn -B package --file pom.xml
```

---

# 4. Manual de Operación y Usuario

El sistema opera bajo una arquitectura dirigida por eventos que transforma datos tabulares estructurados en decisiones de ruteo geográfico.

```text
[Archivo JSON]
        │
        ▼
[Parser e Inyección]
        │
        ▼
[Filtro Warshall]
        │
        ▼
[Ruta Maestra MST]
        │
        ▼
[Best-Fit Flota]
        │
        ▼
[Enrutamiento Final]
        │
        ▼
[Dashboard / CSV]
```

## 4.1 Formato del Dataset de Entrada

El sistema requiere un archivo `.json` con la siguiente estructura:

```json
{
  "ciudad": {
    "vertices": [
      { "id": 0, "tipo": "DEPOT", "x": 100, "y": 150 },
      { "id": 1, "tipo": "CLIENTE", "x": 300, "y": 450 }
    ],
    "aristas": [
      { "u": 0, "v": 1, "distancia": 12.5 }
    ]
  },
  "paquetes": [
    { "id": "P01", "destino": 1, "peso": 25.0, "prioridad": 1 }
  ],
  "camiones": [
    { "id": "C01", "capacidad": 150.0 }
  ]
}
```

## 4.2 Ejecución e Ingesta de Datos

Inicie la aplicación mediante:

```bash
mvn javafx:run
```

Posteriormente:

1. Presione el botón de carga.
2. Seleccione un archivo JSON válido.
3. El sistema procesará el archivo utilizando el componente `JsonParser`.
4. Se renderizarán los nodos urbanos en pantalla:

   * DEPOT: rojo.
   * Clientes: gris.

## 4.3 Procesamiento Logístico (Route-First, Cluster-Second)

Al iniciar el cálculo automático se ejecutan las siguientes etapas:

### Evaluación de Viabilidad (Warshall)

Se determina qué destinos son alcanzables dentro del grafo urbano. Los paquetes con destinos inaccesibles son descartados.

### Construcción de la Ruta Maestra

Se calcula un Árbol de Expansión Mínima (MST) que conecta todos los puntos de entrega partiendo desde el depósito central.

### Asignación de Carga (Best-Fit)

Los paquetes alcanzables se ordenan considerando:

* Posición geográfica.
* Prioridad.
* Peso.

Posteriormente se asignan a los camiones minimizando el espacio desperdiciado.

## 4.4 Evaluación de Heurísticas

El sistema compara dos estrategias para resolver el problema del viajante (TSP):

### Estrategia MST (Prim + DFS Preorden)

* Genera recorridos libres de ciclos.
* Mantiene límites de aproximación controlados.

### Estrategia Nearest Neighbor

* Algoritmo codicioso (Greedy).
* Selecciona el siguiente nodo más cercano en cada paso.

Las rutas obtenidas son refinadas utilizando Dijkstra para producir recorridos reales sobre la red vial.

---

# 5. Salidas del Sistema y Reportabilidad

## 5.1 Reporte CSV

Al finalizar el procesamiento se genera automáticamente:

```text
reporte_logistica.csv
```

El archivo contiene:

### Métricas de la Flota

* ID del camión.
* Peso total transportado.
* Capacidad máxima.
* Lista de paquetes asignados.
* Ruta calculada mediante MST.
* Tiempo de CPU del algoritmo MST.
* Ruta calculada mediante Nearest Neighbor.
* Tiempo de CPU del algoritmo Nearest Neighbor.

### Métricas de Paquetes Excluidos

* ID del paquete.
* Destino.
* Peso.
* Prioridad.
* Motivo de exclusión:

  * Falta de espacio.
  * Destino inalcanzable.

## 5.2 Dashboard Analítico

De manera simultánea a la generación del CSV, el componente `GeneradorEstadisticas` despliega un panel interactivo con:

* Porcentaje de utilización de cada camión.
* Eficiencia de empaquetado.
* Comparación de algoritmos.
* Determinación del algoritmo más eficiente según:

  * Tiempo de CPU.
  * Distancia total recorrida.

---

# 6. Aseguramiento de Calidad y Pruebas Unitarias

El proyecto incluye pruebas automatizadas mediante:

* JUnit 5
* Maven Surefire Plugin

Para ejecutar toda la batería de pruebas:

```bash
mvn surefire:test
```

Se recomienda ejecutar estas pruebas antes de cada entrega o integración para garantizar la estabilidad funcional del sistema.

## Authors

- [@JalemOG](https://www.github.com/JalemOG)

- [@Josue252525](https://www.github.com/Josue252525)

- [@wEstebanOS](https://www.github.com/wEstebanOS)


