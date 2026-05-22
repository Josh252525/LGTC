package estructuras;

//El union find ayuda a detectar ciclos. Lo usamos en Kruskal. 
//UnionFind ve nodos A B C. Pregunta: ¿Estos nodos ya pertenecen al mismo grupo?
//Kruskal ocupa evitar: A -> B -> C -> A

public class UnionFind {
	
	private int[] parent;
	private int[] rank;
	
	public UnionFind(int size) {
		parent = new int[size];
		rank = new int[size]; //Rank es una estimaciónd de la altura del árbol.
		//Para que una estimación? Para evitar árboles ENORMES porque find se vuelve lento
		
		//Por qué rayos hay un for en el constructor?
		//UnionFind ocupa asegurarse que, en un inicio, TODOS sus elementos estén en su propio grupo separado.
		
		for(int i = 0; i < size; i++) {
			parent[i] = i; //Cada elemento es su propio representante.
			rank[i] = 0; //Altura inicial del árbol que se construirá.
			
		}
	}
	
	//También se llama Path Compression. 
	//Dice "sube sube sube hasta encontrar al líder del grupo".
	public int find(int x) {
		if(parent[x] != x) {
			parent[x] = find(parent[x]);
		}
		
		return parent[x];
	}
	
	public void union(int a, int b) {
		
		int raizA = find(a);
		int raizB = find(b);
		//Buscamos a los representantes de ambos grupos, a y b.
		
		if(raizA == raizB) {
			return;
		}
		if(rank[raizA] < rank[raizB]) {
			parent[raizA] = raizB;
			
		}
		else if(rank[raizA] > rank[raizB]) {
			parent[raizB] = raizA;
		}
		
		else {
			parent[raizB] = raizA;
			rank[raizA]++;
		}
	}
	

}
