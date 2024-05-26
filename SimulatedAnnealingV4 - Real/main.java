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

        
        // General Parameters
        boolean useGeneratedData = false;

        // Raw Data parameters
        String minPackagesDate = "2024-01-01 00:00:00";
        String maxPackagesDate = "2024-01-04 23:59:59";

        // Data Generation Parameters
        int maxAirports = 30; // MAX AIRPORTS IS 30
        int packagesAmount = 1500;
        String startPackagesDate = "2024-01-01 00:00:00";
        String endPackagesDate = "2024-01-04 23:59:59";
        int flightsMultiplier = 1;

        // Simmulated Annealing Parameters

        
        double temperature = 10000;
        double coolingRate = 0.08;
        int neighbourCount = 10;
        int windowSize = 250; 
        boolean stopWhenNoPackagesLeft = false;


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

        
        ArrayList<Aeropuerto> aeropuertos = new ArrayList<Aeropuerto>();
        ArrayList<PlanVuelo> planVuelos = new ArrayList<PlanVuelo>();
        ArrayList<Paquete> paquetes = new ArrayList<Paquete>();

        HashMap<Integer, Integer> map = new HashMap<>();

        // Manually assign values to the keys
        map.put(0, 250);
        map.put(1, 500);
        map.put(2, 1000);
        map.put(3, 2000);
        map.put(4, 3000);
        map.put(5, 6250);
        // Continue this for all keys from 0 to 29
        // Example values here, you can change these as needed
        map.put(6, 12500);
        map.put(7, 25000);
        map.put(8, 50000);
        map.put(9, 100000);
 
        

        for(int i = 0; i<10; i++){
            //String pathParaExpNum = "outputExpNum/paquetes500_j" + (i+1) + ".csv";
            String pathParaExpNum = "benchmark/paquetes" + map.get(i) + ".csv";
        

            long startTime = System.nanoTime();
            if (useGeneratedData == true) {
                HashMap<String, Ubicacion> ubicacionMap = Funciones.getUbicacionMap(maxAirports);
                aeropuertos = Funciones.leerAeropuertos(inputPath, ubicacionMap);
                aeropuertos = new ArrayList<Aeropuerto>(aeropuertos.subList(0, maxAirports));

                // planVuelos = Funciones.generarPlanesDeVuelo(
                //     aeropuertos, 
                //     flightsMultiplier,
                //     generatedInputPath + "/vuelos.csv"
                // );

                // paquetes = Funciones.generarPaquetes(
                //     packagesAmount,
                //     aeropuertos,
                //     Funciones.parseDateString(startPackagesDate),
                //     Funciones.parseDateString(endPackagesDate),
                //     generatedInputPath + "paquetes.csv"
                // );

                paquetes = Funciones.generarPaquetes(
                    map.get(i),
                    aeropuertos,
                    Funciones.parseDateString(startPackagesDate),
                    Funciones.parseDateString(endPackagesDate),
                    pathParaExpNum
                    // "/CAMBIAR AQUI"
                );

                System.out.println("Se generaron " + paquetes.size() + " paquetes.");
                System.out.println("Se generaron " + planVuelos.size() + " planes de vuelo.");
                System.out.println("Tiempo de generacion de datos: " + (System.nanoTime() - startTime) / 1000000000 + " s");

                continue;
            } else {

                HashMap<String, Ubicacion> ubicacionMap = Funciones.getUbicacionMap(30);
                aeropuertos = Funciones.leerAeropuertos(inputPath, ubicacionMap);

                planVuelos = Funciones.leerRawPlanesVuelo(ubicacionMap, "rawData");
                /*paquetes = Funciones.leerRawEnvios(
                    ubicacionMap, 
                    "rawData/envios", 
                    Funciones.parseDateString(minPackagesDate), 
                    Funciones.parseDateString(maxPackagesDate)
                );*/
                paquetes = Funciones.leerPaquetes(pathParaExpNum, ubicacionMap);
                
                Date minDate = Funciones.parseDateString("2026-01-01 00:00:00");
                Date maxDate = Funciones.parseDateString("2021-01-01 00:00:00");

                for(Paquete paquete : paquetes){
                    if(paquete.getFecha_recepcion() != null){
                        if(paquete.getFecha_recepcion().before(minDate)){
                            minDate = paquete.getFecha_recepcion();
                        }
                        if(paquete.getFecha_recepcion().after(maxDate)){
                            maxDate = paquete.getFecha_recepcion();
                        }
                    }
                }

                System.out.println("Se leyeron " + paquetes.size() + " paquetes.");
                System.out.println("Fecha minima de recepcion de paquetes: " + Funciones.getFormattedDate(minDate));
                System.out.println("Fecha maxima de recepcion de paquetes: " + Funciones.getFormattedDate(maxDate));
                System.out.println("Tiempo de lectura de datos: " + (System.nanoTime() - startTime) / 1000000000 + " s");
            }   

            if(paquetes.size() == 0){
                System.out.println("ERROR: No hay paquetes para procesar.");
                return;
            }
            if(planVuelos.size() == 0){
                System.out.println("ERROR: No hay planes de vuelo para procesar.");
                return;
            }


            SAImplementation sa = new SAImplementation();

            sa.setData(
                aeropuertos,
                planVuelos,
                paquetes
            );
            
            sa.setParameters(
                stopWhenNoPackagesLeft,
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

            sa.startAlgorithm(pathParaExpNum);

        }
    }
}