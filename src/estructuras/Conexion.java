package estructuras;

//Representa la conexión del nodo con su peso.
//EJ: (4,10). Estoy conectado al 4 con peso 10.

public class Conexion {

		int destino;
		double peso;
		
		public Conexion(int destino, double peso) {
			this.destino = destino;
			this.peso = peso;
		}
}
