package estructuras;

/**
 * Implementación de la estructura de Conjuntos Disjuntos (Disjoint-Set / Union-Find).
 * Optimiza la verificación y fusión de agrupaciones de nodos en tiempo casi constante,
 * previniendo la formación de ciclos perjudiciales al construir Árboles de Expansión Mínima.
 */
public class UnionFind {
    
    public int[] parent;
    private int[] rank;
    
    /**
     * Inicializa el arreglo de conjuntos independientes. Al arrancar, cada nodo
     * es su propio representante de grupo.
     *
     * @param size La cantidad total de nodos a administrar.
     */

    public UnionFind(int size) {
        parent = new int[size];
        rank = new int[size]; 
        
        for(int i = 0; i < size; i++) {
            parent[i] = i; 
            rank[i] = 0; 
        }
    }
    
    /**
     * Localiza al "Líder" o representante del conjunto al que pertenece el nodo.
     * Utiliza la técnica de Compresión de Caminos (Path Compression) para aplanar
     * el árbol recursivamente y acelerar consultas futuras.
     *
     * @param x El nodo a consultar.
     * @return El ID del representante supremo de su conjunto.
     */
    public int find(int x) {
        if(parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        
        return parent[x];
    }
    
    /**
     * Fusiona los conjuntos de dos nodos distintos en un único grupo.
     * Utiliza la heurística de Unión por Rango (Union by Rank) anexando el árbol
     * más pequeño debajo de la raíz del árbol más profundo.
     *
     * @param a Primer nodo.
     * @param b Segundo nodo.
     */
    public void union(int a, int b) {
        
        int raizA = find(a);
        int raizB = find(b);
        
        if (raizA != raizB) {
            if (rank[raizA] < rank[raizB]) {
                parent[raizA] = raizB;
            } else if (rank[raizA] > rank[raizB]) {
                parent[raizB] = raizA;
            } else {
                parent[raizB] = raizA;
                rank[raizA]++;
            }
        }
    }
}