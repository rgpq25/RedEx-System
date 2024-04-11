import Clases.Aeropuerto;
import Clases.Paquete;
import Clases.Vuelo;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class main {
    public static void main(String[] args) {
        Funciones funciones = new Funciones();

        String inputPath = "input";
        Aeropuerto [] aeropuertos = funciones.leerAeropuertos(inputPath);
        Paquete [] paquetes = funciones.leerPaquetes(inputPath);
        Vuelo [] vuelos = funciones.leerVuelos(inputPath);

        funciones.ordenarPaquetes(paquetes);
        funciones.ordenarVuelos(vuelos);


        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        System.out.println("Inicio: " + formattedDateTime);

        
        
        SAImplementation sa = new SAImplementation(paquetes,vuelos,aeropuertos);


        currentDateTime = LocalDateTime.now();
        formattedDateTime = currentDateTime.format(formatter);
        System.out.println("Fin: " + formattedDateTime);
        
    }
}