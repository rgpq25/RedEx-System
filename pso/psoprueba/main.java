import Clases.Aeropuerto;
import Clases.Paquete;
import Clases.Vuelo;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class main {
    public static void main(String[] args) {
        //System.out.println("Hello, World!");
        Funciones funciones = new Funciones();
        Aeropuerto [] aeropuertos = funciones.leerAeropuertos();
        //System.out.println(aeropuertos[0].getCapacidad_maxima());
        Paquete [] paquetes = funciones.leerPaquetes();
        Vuelo [] vuelos = funciones.leerVuelos();
        int numAeropuertos = aeropuertos.length;
        int numPaquetes = paquetes.length;
        int numVuelos = vuelos.length;

        //podria ordenar los paquetes y vuelos de acuerdo a las fechas
        //ver el tema de reposicionamiento con las velocidades de nuevo
        
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Define a date and time format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Format the current date and time and print it
        String formattedDateTime = currentDateTime.format(formatter);
        System.out.println("Current Date and Time: " + formattedDateTime);

        PSOimplementation p = new PSOimplementation(paquetes,vuelos,aeropuertos);

        currentDateTime = LocalDateTime.now();
        formattedDateTime = currentDateTime.format(formatter);
        System.out.println("Current Date and Time: " + formattedDateTime);
    }
}