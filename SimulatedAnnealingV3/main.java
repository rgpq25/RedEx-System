import Clases.Aeropuerto;
import Clases.GrafoVuelos;
import Clases.Paquete;
import Clases.PlanRuta;
import Clases.Vuelo;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class main {
    public static void main(String[] args) {
        Funciones funciones = new Funciones();

        String inputPath = "input";
        Aeropuerto[] aeropuertos = funciones.leerAeropuertos(inputPath);
        Paquete[] paquetes = funciones.leerPaquetes(inputPath);
        Vuelo[] vuelos = funciones.leerVuelos(inputPath);

        funciones.ordenarPaquetes(paquetes);
        funciones.ordenarVuelos(vuelos);
        GrafoVuelos grafoVuelos = new GrafoVuelos();
        for (Vuelo vuelo : vuelos) {
            grafoVuelos.agregarVuelo(vuelo);
        }
        Date date = Date.from(LocalDateTime.of(2023, 1, 2, 0, 0).toInstant(ZoneOffset.UTC));

        List<PlanRuta> todasLasRutas = grafoVuelos.buscarTodasLasRutas(date);
        for (PlanRuta ruta : todasLasRutas) {
            System.out.println(ruta.toString());
        }
    }
}