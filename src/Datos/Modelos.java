package Datos;

import java.util.List;

// Modelo Vértice: Propiedades id, tipo, x, y
public record Vertice(int id, String tipo, int x, int y) {} 

// Modelo Arista: Propiedades u, v y distancia
public record Arista(int u, int v, double distancia) {} 

// Modelo Ciudad: Debe contener una lista de vertices y una lista de aristas
public record Ciudad(List<Vertice> vertices, List<Arista> aristas) {}

// Modelo Paquete: Propiedades id, destino, peso y prioridad
public record Paquete(int id, int destino, double peso, int prioridad) {} 

// Modelo Camión: Propiedades id y capacidad
public record Camion(String id, double capacidad) {} 

// Contenedor principal que agrupa todo el JSON
public record ConfigLogisTEC(Ciudad ciudad, List<Paquete> paquetes, List<Camion> camiones) {}
