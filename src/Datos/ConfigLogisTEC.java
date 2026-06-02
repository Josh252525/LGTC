package Datos;

import java.util.List;

//Contenedor principal que agrupa todo el JSON
public record ConfigLogisTEC(Ciudad ciudad, List<Paquete> paquetes, List<Camion> camiones) {}
