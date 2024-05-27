import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import Clases.Aeropuerto;
import Clases.Funciones;
import Clases.GrafoVuelos;
import Clases.Paquete;
import Clases.PlanRuta;
import Clases.PlanVuelo;
import Clases.Ubicacion;
import Clases.Vuelo;

public class SAImplementation {
    private ArrayList<Aeropuerto> aeropuertos;
    private ArrayList<PlanVuelo> planVuelos;
    private ArrayList<Paquete> paquetes;

    // Simmulated Annealing Parameters
    private boolean stopWhenNoPackagesLeft;
    private double temperature;
    private double coolingRate;
    private int neighbourCount;
    private int windowSize; //best = 50 MUST BE LESS THE packagesAmount

    // Weight Parameters
    private double badSolutionPenalization;
    private double flightPenalization;
    private double airportPenalization;

    //funcion_fitness = (SUMA_TOTAL_PAQUETES) * 10 + (SUMA_TOTAL_VUELOS) * 4 + (PROMEDIO_PONDERADO_TIEMPO_AEROPUERTO) * 4
    private double sumaPaquetesWeight;
    private double sumaVuelosWeight;
    private double promedioPonderadoTiempoAeropuertoWeight;

    public SAImplementation() {}

    public void setData(
        ArrayList<Aeropuerto> aeropuertos, 
        ArrayList<PlanVuelo> planVuelos, 
        ArrayList<Paquete> paquetes
    ){
        this.aeropuertos = aeropuertos;
        this.planVuelos = planVuelos;
        this.paquetes = paquetes;
    }

    public void setParameters(
        boolean stopWhenNoPackagesLeft,
        double temperature,
        double coolingRate,
        int neighbourCount,
        int windowSize,
        double badSolutionPenalization, 
        double flightPenalization, 
        double airportPenalization, 
        double sumaPaquetesWeight, 
        double sumaVuelosWeight, 
        double promedioPonderadoTiempoAeropuertoWeight
    ) {
        this.stopWhenNoPackagesLeft = stopWhenNoPackagesLeft;
        this.temperature = temperature;
        this.coolingRate = coolingRate;
        this.neighbourCount = neighbourCount;
        this.windowSize = windowSize;
        this.badSolutionPenalization = badSolutionPenalization;
        this.flightPenalization = flightPenalization;
        this.airportPenalization = airportPenalization;
        this.sumaPaquetesWeight = sumaPaquetesWeight;
        this.sumaVuelosWeight = sumaVuelosWeight;
        this.promedioPonderadoTiempoAeropuertoWeight = promedioPonderadoTiempoAeropuertoWeight;
    }

    public void startAlgorithm(String inputPath){
        GrafoVuelos grafoVuelos = new GrafoVuelos(planVuelos, paquetes);
        HashMap<Integer, Vuelo> vuelos_map = grafoVuelos.getVuelosHash();

        long startTime = System.nanoTime();
        HashMap<String, ArrayList<PlanRuta>> todasLasRutas = new HashMap<String, ArrayList<PlanRuta>>();
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        System.out.println("Tiempo de ejecución de la ordenación: " + (float) (duration / 1000000000) + " segundos");


        Solucion current = new Solucion(
            paquetes,
            new ArrayList<PlanRuta>(),
            aeropuertos,
            new HashMap<Integer, Integer>(),
            0,
            badSolutionPenalization,
            flightPenalization,
            airportPenalization,
            vuelos_map,
            grafoVuelos
        );

        long startTimeInitialization = System.nanoTime();
        current.force_initialize(todasLasRutas);
        Funciones.printRutasTXT(current.paquetes, current.rutas, "initial.txt");
        long segundosInitiliazation = (System.nanoTime() - startTimeInitialization) / 1000000000;
        System.out.println("Finished solution initialization in " + segundosInitiliazation+ " s");

        
        startTime = System.nanoTime();


        while (temperature > 1) {
            ArrayList<Solucion> neighbours = new ArrayList<Solucion>();
            for (int i = 0; i < neighbourCount; i++) {
                neighbours.add(
                    current.generateNeighbour(windowSize)
                );
            }

            int bestNeighbourIndex = 0;
            double bestNeighbourCost = Double.MAX_VALUE;
            for (int i = 0; i < neighbours.size(); i++) {
                Solucion neighbour = neighbours.get(i);
                double neighbourCost = neighbour.getSolutionCost();
                if (neighbourCost < bestNeighbourCost) {
                    bestNeighbourCost = neighbourCost;
                    bestNeighbourIndex = i;
                }
            }
    
            double costDifference = bestNeighbourCost - current.getSolutionCost();        

            if (
                costDifference < 0 || 
                Math.exp(-costDifference / temperature) > Math.random()
            ) {
                current = neighbours.get(bestNeighbourIndex);
            }


            if (current.costoDePaquetesYRutasErroneas == 0 && stopWhenNoPackagesLeft == true) {
                break;
            }
            
            
            temperature *= 1 - coolingRate;
            
            System.out.println(
                "Current cost: " + current.getSolutionCost() + 
                " | Packages left: " + current.costoDePaquetesYRutasErroneas +
                " | Temperature: " + temperature
            );
        }


        endTime = System.nanoTime();
        duration = endTime - startTime;

        System.out.println("=====================================");
        System.out.println("Nombre de archivo de entrada -> " + inputPath);
        Funciones.printLineInLog("Nombre de archivo de entrada -> " + inputPath);

        System.out.println("Tiempo de ejecución de algoritmo: " + (float) (duration /
        1000000000) + " segundos");
        Funciones.printLineInLog("Tiempo de ejecucion de algoritmo: " + (float) (duration /
        1000000000) + " segundos");
        Funciones.printLineInLog("Parametros: Temperatura " + temperature + " | Cooling Rate " + coolingRate + " | Neighbour Count " + neighbourCount + " | Window Size " + windowSize);

        System.out.println(
            "Final cost: " + current.getSolutionCost() + 
            " | Packages left: " + current.costoDePaquetesYRutasErroneas +
            " | Temperature: " + temperature
        );
        Funciones.printLineInLog(
            "Final cost: " + current.getSolutionCost() + 
            " | Packages left: " + current.costoDePaquetesYRutasErroneas +
            " | Temperature: " + temperature
        );

        //current.printCosts();
        current.printCostsInLog();
        Funciones.printLineInLog("Finished solution initialization in " + segundosInitiliazation+ " s");
        Funciones.printLineInLog("");
        Funciones.printLineInLog("");

        Funciones.printRutasTXT(current.paquetes, current.rutas, "rutasFinal.txt");
        current.printFlightOcupation("ocupacionVuelos.txt");
        current.printAirportHistoricOcupation("ocupacionAeropuertos.txt");
    }
}
