package pucp.e3c.redex_back.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import pucp.e3c.redex_back.service.PlanRutaService;
import pucp.e3c.redex_back.service.VueloService;

public class SAImplementation {
        private ArrayList<Aeropuerto> aeropuertos;
        private ArrayList<PlanVuelo> planVuelos;
        private ArrayList<Paquete> paquetes;
        private HashMap<Integer, Integer> ocupacionInicial;
        private GrafoVuelos grafoVuelos;

        // Simmulated Annealing Parameters
        private boolean stopWhenNoPackagesLeft;
        private double temperature;
        private double coolingRate;
        private int neighbourCount;
        private int windowSize; // best = 50 MUST BE LESS THE packagesAmount

        // Weight Parameters
        private double badSolutionPenalization;
        private double flightPenalization;
        private double airportPenalization;

        // funcion_fitness = (SUMA_TOTAL_PAQUETES) * 10 + (SUMA_TOTAL_VUELOS) * 4 +
        // (PROMEDIO_PONDERADO_TIEMPO_AEROPUERTO) * 4
        private double sumaPaquetesWeight;
        private double sumaVuelosWeight;
        private double promedioPonderadoTiempoAeropuertoWeight;

        public SAImplementation() {
        }

        public void setData(
                        ArrayList<Aeropuerto> aeropuertos,
                        ArrayList<PlanVuelo> planVuelos,
                        ArrayList<Paquete> paquetes,
                        HashMap<Integer, Integer> ocupacionInicial) {
                this.aeropuertos = aeropuertos;
                this.planVuelos = planVuelos;
                this.paquetes = paquetes;
                this.ocupacionInicial = ocupacionInicial;
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
                        double promedioPonderadoTiempoAeropuertoWeight) {
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

        public RespuestaAlgoritmo startAlgorithm(GrafoVuelos grafoVuelos, VueloService vueloService,
                        PlanRutaService planRutaService, Simulacion simulacion, int iteracion,
                        SimpMessagingTemplate messagingTemplate) {

                try {
                        HashMap<Integer, Vuelo> vuelos_map = grafoVuelos.getVuelosHash();

                        long startTime = System.nanoTime();
                        HashMap<String, ArrayList<PlanRutaNT>> todasLasRutas = new HashMap<String, ArrayList<PlanRutaNT>>();
                        long endTime = System.nanoTime();
                        long duration = endTime - startTime;
                        System.out.println("Tiempo de ejecución de la ordenación: " + (float) (duration / 1000000000)
                                        + " segundos");

                        Solucion current = new Solucion(
                                        paquetes,
                                        new ArrayList<PlanRutaNT>(),
                                        aeropuertos,
                                        ocupacionInicial,
                                        0,
                                        badSolutionPenalization,
                                        flightPenalization,
                                        airportPenalization,
                                        vuelos_map,
                                        grafoVuelos);

                        long startTimeInitialization = System.nanoTime();
                        current.force_initialize(todasLasRutas, vueloService);
                        // Funciones.printRutasTXT(current.paquetes, current.rutas, "initial.txt");
                        System.out.println("Finished solution initialization in "
                                        + (System.nanoTime() - startTimeInitialization) / 1000000000 + " s");
                        startTime = System.nanoTime();

                        // print current.paquetes.size
                        // System.out.println("\nPaquetes size: " + current.paquetes.size());
                        // print windowSize
                        // System.out.println("Window size: " + windowSize +"\n");

                        while (temperature > 1) {
                                // System.out.println("\nIteracion en loop\n");
                                ArrayList<Solucion> neighbours = new ArrayList<Solucion>();
                                for (int i = 0; i < neighbourCount; i++) {
                                        neighbours.add(
                                                        current.generateNeighbour(windowSize, vueloService));
                                        // System.out.println("Vecino generado");
                                }
                                // System.out.println("\nNeighbours iterados\n");
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
                                // System.out.println("\nBest neighbour\n");
                                double costDifference = bestNeighbourCost - current.getSolutionCost();

                                if (costDifference < 0 ||
                                                Math.exp(-costDifference / temperature) > Math.random()) {
                                        current = neighbours.get(bestNeighbourIndex);
                                }

                                if (current.costoDePaquetesYRutasErroneas == 0 && stopWhenNoPackagesLeft == true) {
                                        break;
                                }

                                temperature *= 1 - coolingRate;

                                System.out.println(
                                                "Current cost: " + current.getSolutionCost() +
                                                                " | Packages left: "
                                                                + current.costoDePaquetesYRutasErroneas +
                                                                " | Temperature: " + temperature);

                        }

                        endTime = System.nanoTime();
                        duration = endTime - startTime;

                        System.out.println("=====================================");

                        System.out.println("Tiempo de ejecución de algoritmo: " + (float) (duration /
                                        1000000000) + " segundos");
                        Funciones.printLineInLog("Tiempo de ejecucion de algoritmo: " + (float) (duration /
                                        1000000000) + " segundos");

                        System.out.println(
                                        "Final cost: " + current.getSolutionCost() +
                                                        " | Packages left: " + current.costoDePaquetesYRutasErroneas +
                                                        " | Temperature: " + temperature);
                        Funciones.printLineInLog(
                                        "Final cost: " + current.getSolutionCost() +
                                                        " | Packages left: " + current.costoDePaquetesYRutasErroneas +
                                                        " | Temperature: " + temperature);

                        // current.printCosts();
                        current.printCostsInLog();
                        Funciones.printLineInLog("");
                        Funciones.printLineInLog("");

                        Funciones.printRutasTXT(current.paquetes, current.rutas, "rutasFinal.txt");
                        // current.printFlightOcupation("ocupacionVuelos.txt");
                        // current.printAirportHistoricOcupation("ocupacionAeropuertos.txt");

                        // Guardar vuelos
                        for (int id : current.ocupacionVuelos.keySet()) {

                                Vuelo vuelo = current.vuelos_hash.get(id);
                                if (simulacion != null) {
                                        vuelo.setSimulacionActual(simulacion);
                                }
                                vuelo.setCapacidadUtilizada(current.ocupacionVuelos.get(id));
                                try {
                                        // vueloService.update(vuelo);
                                } catch (Exception e) {
                                        System.err.println("Error al guardar en la base de datos: " + e.getMessage());
                                        messagingTemplate.convertAndSend("/algoritmo/estado",
                                                        "Error al guardar algun vuelo: " + e.getMessage());
                                }
                        }
                        RespuestaAlgoritmo respuestaAlgoritmo = new RespuestaAlgoritmo(
                                        new ArrayList<>(current.vuelos_hash.values()),
                                        current.estado, current.rutas, simulacion);
                        return respuestaAlgoritmo;
                } catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("Error al ejecutar el algoritmo p: " + e.getMessage());
                        messagingTemplate.convertAndSend("/algoritmo/estado",
                                        "Error al ejecutar el algoritmo: " + e.getMessage());
                        return null;
                }

        }
}
