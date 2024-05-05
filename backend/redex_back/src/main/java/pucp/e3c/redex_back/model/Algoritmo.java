package pucp.e3c.redex_back.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Algoritmo {
    public static void procesarPaquetes() {
        // Simmulated Annealing Parameters
        ArrayList<Paquete> paquetes = new ArrayList<>();
        double temperature = 1000;
        double coolingRate = 0.08;
        int neighbourCount = 10;
        int windowSize = 20;
        boolean stopWhenNoPackagesLeft = false;

        // Weight Parameters
        double badSolutionPenalization = 100;
        double flightPenalization = 200;
        double airportPenalization = 300;

        // funcion_fitness = (SUMA_TOTAL_PAQUETES) * 10 + (SUMA_TOTAL_VUELOS) * 4 +
        // (PROMEDIO_PONDERADO_TIEMPO_AEROPUERTO) * 4
        double sumaPaquetesWeight = 10;
        double sumaVuelosWeight = 4;
        double promedioPonderadoTiempoAeropuertoWeight = 4;

        String inputPath = "src\\main\\resources\\dataFija";

        ArrayList<Aeropuerto> aeropuertos = new ArrayList<Aeropuerto>();
        ArrayList<PlanVuelo> planVuelos = new ArrayList<PlanVuelo>();

        long startTime = System.nanoTime();
        HashMap<String, Ubicacion> ubicacionMap = Funciones.getUbicacionMap();
        aeropuertos = Funciones.leerAeropuertos(inputPath, ubicacionMap);

        String startPackagesDate = "2024-01-01 00:00:00";
        String endPackagesDate = "2024-01-04 23:59:59";
        paquetes = Funciones.generarPaquetes(
                20,
                aeropuertos,
                Funciones.parseDateString(startPackagesDate),
                Funciones.parseDateString(endPackagesDate));

        System.out.println("Se generaron " + paquetes.size() + " paquetes.");

        planVuelos = Funciones.leerPlanesVuelo(ubicacionMap, inputPath);

        Date minDate = Funciones.parseDateString(startPackagesDate);
        Date maxDate = Funciones.parseDateString(endPackagesDate);

        System.out.println("Fecha minima de recepcion de paquetes: " + Funciones.getFormattedDate(minDate));
        System.out.println("Fecha maxima de recepcion de paquetes: " + Funciones.getFormattedDate(maxDate));
        System.out
                .println("Tiempo de lectura de datos: " + (System.nanoTime() - startTime) / 1000000000 + " s");

        if (paquetes.size() == 0) {
            System.out.println("ERROR: No hay paquetes para procesar.");
            return;
        }
        if (planVuelos.size() == 0) {
            System.out.println("ERROR: No hay planes de vuelo para procesar.");
            return;
        }

        SAImplementation sa = new SAImplementation();

        sa.setData(
                aeropuertos,
                planVuelos,
                paquetes);

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
                promedioPonderadoTiempoAeropuertoWeight);

        sa.startAlgorithm();

    }
}
