import Clases.Aeropuerto;
import Clases.Paquete;
import Clases.Vuelo;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class main {
    public static void main(String[] args) {
        Funciones funciones = new Funciones();
        //Lectura de datos de entrada
        Aeropuerto [] aeropuertos = funciones.leerAeropuertos();
        Paquete [] paquetes = funciones.leerPaquetes();
        Vuelo [] vuelos = funciones.leerVuelos();

        funciones.ordernarPaquetes(paquetes);
        funciones.ordernarVuelos(vuelos);


        LocalDateTime currentDateTime = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Format the current date and time and print it
        String formattedDateTime = currentDateTime.format(formatter);
        System.out.println("Inicio: " + formattedDateTime);

        PSOimplementation p = new PSOimplementation(paquetes,vuelos,aeropuertos);

        currentDateTime = LocalDateTime.now();
        formattedDateTime = currentDateTime.format(formatter);
        System.out.println("Fin: " + formattedDateTime);
        
    }
}