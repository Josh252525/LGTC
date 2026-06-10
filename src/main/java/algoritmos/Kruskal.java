package algoritmos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import estructuras.*;

public class Kruskal {

    public static class MSTEdge {
        public final int origen;
        public final int destino;
        public final double peso;

        public MSTEdge(int origen, int destino, double peso) {
            this.origen = origen;
            this.destino = destino;
            this.peso = peso;
        }
    }

    public List<MSTEdge> kruskal(Grafo grafo) {

        List<MSTEdge> todasLasAristas = new ArrayList<>();

        // Extraer todas las aristas del grafo
        for (int origen = 0; origen < grafo.getCantidadVertices(); origen++) {

            LinkedList<Conexion> vecinos = grafo.getVecinos(origen);

            for (int i = 0; i < vecinos.size(); i++) {

                Conexion conexion = vecinos.getAt(i);

                int destino = conexion.destino;

                if (origen < destino) {
                    todasLasAristas.add(
                            new MSTEdge(origen, destino, conexion.peso)
                    );
                }
            }
    }

        // Ordenar aristas por peso ascendente
        todasLasAristas.sort(
                Comparator.comparingDouble(e -> e.peso)
        );

        UnionFind uf = new UnionFind(
                grafo.getCantidadVertices()
        );

        List<MSTEdge> mst = new ArrayList<>();

        // Kruskal
        for (MSTEdge arista : todasLasAristas) {

            int raizOrigen = uf.find(arista.origen);
            int raizDestino = uf.find(arista.destino);

            // Si están en componentes diferentes, no forma ciclo
            if (raizOrigen != raizDestino) {

                mst.add(arista);

                uf.union(arista.origen, arista.destino);

                // Un MST tiene V - 1 aristas
                if (mst.size() == grafo.getCantidadVertices() - 1) {
                    break;
                }
            }
        }

        return mst;
    }
}