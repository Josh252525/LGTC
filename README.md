content = """# Proyecto LogísTEC (LGTC) 🚚

**Curso:** Algoritmos y Estructuras de Datos I (CE1103)  
**Estado del Proyecto:** Inicial / En Desarrollo (Provisional)

---

## 📌 Descripción del Proyecto
LogísTEC es un sistema de enrutamiento y logística que modela una ciudad como un **grafo no dirigido y ponderado**. El objetivo principal es gestionar la distribución de paquetes mediante una flota de camiones, calculando rutas óptimas y asignando cargas utilizando algoritmos clásicos de teoría de grafos y heurísticas de aproximación.

## 🛠️ Tecnologías Utilizadas
* **Lenguaje:** Java 17 LTS (o superior)
* **Gestor de Dependencias:** Maven
* **Estructuras de Datos:** Programadas desde cero (sin utilizar colecciones nativas de `java.util.*` en el núcleo algorítmico).
* **Manejo de JSON:** Gson (Google)
* **Interfaz Gráfica:** Swing / JavaFX

## 📦 Estructura del Proyecto (Simplificada)
* `Modelos.java` (Records): Estructuras base inmutables (`Ciudad`, `Vertice`, `Arista`, `Paquete`, `Camion`).
* `JsonParser.java`: Lógica de parseo del archivo de configuración inicial.
* `Main.java`: Punto de entrada de la aplicación.
* *Próximamente:* Implementación de `Grafo`, `ListaEnlazada`, y el núcleo de algoritmos (Dijkstra, Warshall, Prim, etc.).

## 🚀 Algoritmos y Lógica de Negocio
1. **Recorridos:** BFS y DFS.
2. **Caminos Mínimos:** Dijkstra y Floyd-Warshall.
3. **Alcanzabilidad:** Cierre Transitivo de Warshall para filtrado de paquetes.
4. **Árboles de Expansión Mínima (MST):** Algoritmos de Prim y Kruskal (con Union-Find).
5. **Asignación de Carga:** Heurística *Best-Fit* (prioridad ascendente, peso descendente).
6. **Ruteo de Camiones (TSP):** Comparación entre la heurística del Vecino Más Cercano (Nearest Neighbor) y la heurística basada en MST.

## 👥 Equipo de Trabajo
Desarrollado en equipo de 4 personas, divididos equitativamente mediante épicas y tareas.

---
*Nota: Este archivo README es provisional y se irá actualizando conforme se completen los hitos del proyecto.*
"""

with open('README.md', 'w', encoding='utf-8') as f:
    f.write(content)