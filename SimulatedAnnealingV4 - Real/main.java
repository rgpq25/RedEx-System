import Clases.Aeropuerto;
import Clases.Duracion;
import Clases.EstadoAlmacen;
import Clases.Funciones;
import Clases.GrafoVuelos;
import Clases.Paquete;
import Clases.PlanRuta;
import Clases.PlanVuelo;
import Clases.Ubicacion;
import Clases.Vuelo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class main {

    

    

    public static void main(String[] args) {

        // TODO: No procesar paquetes que aun no seran recibidos

        // Data Generation Parameters
        boolean generateNewData = false;
        int maxAirports = 30; // MAX AIRPORTS IS 30
        int packagesAmount = 1000;
        String startPackagesDate = "2024-01-01 00:00:00";
        String endPackagesDate = "2024-01-01 23:59:59";
        int flightsMultiplier = 1;

        // SImmulated Annealing Parameters
        double temperature = 100000;
        double coolingRate = 0.001;
        int neighbourCount = 1;
        int windowSize = 500; //best = 50 MUST BE LESS THE packagesAmount

        // Weight Parameters
        double badSolutionPenalization = 100;
        double flightPenalization = 200;
        double airportPenalization = 300;

        //funcion_fitness = (SUMA_TOTAL_PAQUETES) * 10 + (SUMA_TOTAL_VUELOS) * 4 + (PROMEDIO_PONDERADO_TIEMPO_AEROPUERTO) * 4
        double sumaPaquetesWeight = 10;
        double sumaVuelosWeight = 4;
        double promedioPonderadoTiempoAeropuertoWeight = 4;
        

        String inputPath = "inputGenerado";
        String generatedInputPath = "inputGenerado";

        HashMap<String, Ubicacion> ubicacionMap = Funciones.getUbicacionMap(maxAirports);
        ArrayList<Aeropuerto> aeropuertos = Funciones.leerAeropuertos(inputPath, ubicacionMap);

        aeropuertos = new ArrayList<Aeropuerto>(aeropuertos.subList(0, maxAirports));

        if (generateNewData == true) {
            ArrayList<Paquete> paquetes = Funciones.generarPaquetes(
                    packagesAmount,
                    aeropuertos,
                    Funciones.parseDateString(startPackagesDate),
                    Funciones.parseDateString(endPackagesDate),
                    generatedInputPath);

            ArrayList<PlanVuelo> planVuelos = Funciones.generarPlanesDeVuelo(aeropuertos, flightsMultiplier,
                    generatedInputPath);

            System.out.println("Se generaron " + paquetes.size() + " paquetes.");
            System.out.println("Se generaron " + planVuelos.size() + " planes de vuelo.");
            return;
        }

        SAImplementation sa = new SAImplementation(
            maxAirports,
            temperature,
            coolingRate,
            neighbourCount,
            windowSize,
            badSolutionPenalization, 
            flightPenalization, 
            airportPenalization, 
            sumaPaquetesWeight, 
            sumaVuelosWeight, 
            promedioPonderadoTiempoAeropuertoWeight
        );

        sa.startAlgorithm(inputPath);
    }
}