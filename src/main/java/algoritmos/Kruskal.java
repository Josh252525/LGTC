package algoritmos;

import estructuras.*;


public class Kruskal {

    private Grafo grafo;

    public Kruskal(Grafo grafo) {
        this.grafo = grafo;
    }

    //Calcula el costo total del MST usando Kruskal
    public double calcMST() {

        int vertices = grafo.getCantidadVertices();

        if (vertices == 0) {
            return 0.0;
        }

        // Union-Find para detectar ciclos malvados
        UnionFind uf = new UnionFind(vertices);

        // Lista de aristas
        LinkedList<MSTEdge> edges = new LinkedList<>();

        // Extraer aristas del grafo
        for (int u = 0; u < vertices; u++) {

            LinkedList<Conexion> vecinos = grafo.getVecinos(u);

            for (int i = 0; i < vecinos.size(); i++) {

                Conexion c = vecinos.getAt(i);

                int v = c.destino;

                // evitar duplicados (grafo no dirigido)
                if (u < v) {
                    edges.insert(new MSTEdge(u, v, c.peso));
                }
            }
        }

        // Ordenar aristas con el bubble sort
        sortEdges(edges);

        // Construir MST
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

    //Construye el MST como grafo

    public Grafo obtenerGrafoMST() {

        int vertices = grafo.getCantidadVertices();

        if (vertices == 0) {
            return new Grafo(0);
        }

        Grafo mst = new Grafo(vertices);

        UnionFind uf = new UnionFind(vertices);

        LinkedList<MSTEdge> edges = new LinkedList<>();

        // extraer aristas
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

        for (int i = 0; i < edges.size(); i++) {

            MSTEdge e = edges.getAt(i);

            int ru = uf.find(e.origen);
            int rv = uf.find(e.destino);

            if (ru != rv) {
                uf.union(e.origen, e.destino);

                mst.agregarArista(e.origen, e.destino, e.peso);
                mst.agregarArista(e.destino, e.origen, e.peso);
            }
        }

        return mst;
    }

    //sortear la vara
    private void sortEdges(LinkedList<MSTEdge> list) {

        int n = list.size();

        for (int i = 0; i < n; i++) {

            for (int j = 0; j < n - 1; j++) {

                MSTEdge a = list.getAt(j);
                MSTEdge b = list.getAt(j + 1);

                if (a.peso > b.peso) {

                    // swap (ineficiente pero válido para estructuras propias)
                    list.deleteAt(j);
                    list.insertAt(j, b);

                    list.deleteAt(j + 1);
                    list.insertAt(j + 1, a);
                }
            }
        }
    }

    //clase interna de arista
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
