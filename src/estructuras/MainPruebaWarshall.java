package estructuras;

import java.util.List;

import Datos.*;

public class MainPruebaWarshall {

    public static void main(String[] args) {

        List<Vertice> vertices = List.of(
                new Datos.Vertice(0, "DEPOT", 0, 0),
                new Vertice(1, "INTERSECCION", 0, 0),
                new Vertice(2, "INTERSECCION", 0, 0),
                new Vertice(3, "INTERSECCION", 0, 0)
        );

        List<Arista> aristas = List.of(
                new Arista(0, 1, 10),
                new Arista(1, 2, 10)
        );

        Ciudad ciudad =
                new Ciudad(vertices, aristas);

        List<Paquete> paquetes = List.of(
                new Paquete(1, 2, 5, 1),
                new Paquete(2, 3, 4, 2)
        );

        ConfigLogisTEC config =
                new ConfigLogisTEC(
                        ciudad,
                        paquetes,
                        List.of()
                );

        ValidadorPaquetes validador =
                new ValidadorPaquetes(config);

        List<Paquete> rechazados =
                validador.validarPaquetes();

        for(Paquete paquete : rechazados) {
            System.out.println(
                    "Paquete rechazado: "
                    + paquete.id()
            );
        }
    }
}