package algoritmos;

import estructuras.*;

/**
 * Implementación del algoritmo de Kruskal para calcular el Árbol de Expansión Mínima (MST) 
 * de un grafo ponderado no dirigido.
 * <p>
 * Este algoritmo opera bajo un enfoque Greedy (Ávido), ordenando inicialmente todas las aristas
 * de menor a mayor peso y evaluándolas una por una mediante la estructura Union-Find para
 * garantizar que no se generen ciclos cerrados.
 * </p>
 */
public class Kruskal {

    private Grafo grafo;

    /**
     * Constructor de la clase Kruskal.
     * * @param grafo El grafo no dirigido sobre el cual se calculará el MST.
     */
    public Kruskal(Grafo grafo) {
        this.grafo = grafo;
    }

    /**
     * Construye el Árbol de Expansión Mínima y retorna la sumatoria de sus pesos.
     * * @return El costo total (distancia en km) de todas las aristas que conforman el MST.
     * Si el grafo está vacío, retorna 0.0.
     */
    public double calcMST() {

        int vertices = grafo.getCantidadVertices();

        if (vertices == 0) {
            return 0.0;
        }

        UnionFind uf = new UnionFind(vertices);
        LinkedList<MSTEdge> edges = new LinkedList<>();

        for (int u = 0; u < vertices; u++) {

            LinkedList<Conexion> vecinos = grafo.getVecinos(u);

            for (int i = 0; i < vecinos.size(); i++) {

                Conexion c = vecinos.getAt(i);
                int v = c.destino;

                if (u < v) {
                    edges.insert(new MSTEdge(u, v, c.peso));
                }
            }
        }

        sortEdges(edges);

        double costoTotal = 0.0;

        for (int i = 0; i < edges.size(); i++) {

            MSTEdge e = edges.getAt(i);

            int ru = uf.find(e.origen);
            int rv = uf.find(e.destino);

            if (ru != rv) {
                uf.union(e.origen, e.destino);
                costoTotal += e.peso;
            }
        }

        return costoTotal;
    }

    // Método privado interno (no requiere javadoc formal)
    private void sortEdges(LinkedList<MSTEdge> list) {
        int n = list.size();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n - 1; j++) {
                MSTEdge a = list.getAt(j);
                MSTEdge b = list.getAt(j + 1);

                if (a.peso > b.peso) {
                    list.deleteAt(j);
                    list.insertAt(j, b);
                    list.deleteAt(j + 1);
                    list.insertAt(j + 1, a);
                }
            }
        }
    }
    
    public static class MSTEdge {
        public int origen;
        public int destino;
        public double peso;

        public MSTEdge(int origen, int destino, double peso) {
            this.origen = origen;
            this.destino = destino;
            this.peso = peso;
        }
    }
}