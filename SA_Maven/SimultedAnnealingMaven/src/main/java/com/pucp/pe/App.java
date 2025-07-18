package com.pucp.pe;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import com.pucp.pe.Clases.Aeropuerto;
import com.pucp.pe.Clases.Duracion;
import com.pucp.pe.Clases.EstadoAlmacen;
import com.pucp.pe.Clases.EventManager;
import com.pucp.pe.Clases.Funciones;
import com.pucp.pe.Clases.GrafoVuelos;
import com.pucp.pe.Clases.Paquete;
import com.pucp.pe.Clases.PlanRuta;
import com.pucp.pe.Clases.PlanVuelo;
import com.pucp.pe.Clases.Solucion;
import com.pucp.pe.Clases.Ubicacion;
import com.pucp.pe.Clases.Vuelo;

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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class App {

    public static void printRutasTXT(ArrayList<Paquete> paquetes, ArrayList<PlanRuta> rutas, String filename) {
        File csvFile = new File(filename);
        PrintWriter out;

        try {
            out = new PrintWriter(csvFile);

            for (int i = 0; i < paquetes.size(); i++) {

                String origen_paquete = paquetes.get(i).getCiudadOrigen().getId();
                String destino_paquete = paquetes.get(i).getCiudadDestino().getId();
                Date fecha_recepcion = paquetes.get(i).getFecha_recepcion();
                Date fecha_maxima = paquetes.get(i).getFecha_maxima_entrega();
                out.println(
                        "Paquete " + i + " - " + origen_paquete + "-" + destino_paquete + "  |  "
                                + Funciones.getFormattedDate(fecha_recepcion) + " => "
                                + Funciones.getFormattedDate(fecha_maxima));
                for (int j = 0; j < rutas.get(i).length(); j++) {
                    String id_origen = rutas.get(i).getVuelos().get(j).getPlan_vuelo().getCiudadOrigen().getId();
                    String id_destino = rutas.get(i).getVuelos().get(j).getPlan_vuelo().getCiudadDestino().getId();
                    Date fecha_salida = rutas.get(i).getVuelos().get(j).getFecha_salida();
                    Date fecha_llegada = rutas.get(i).getVuelos().get(j).getFecha_llegada();

                    out.println(
                            "         " + id_origen + "-" + id_destino + " " + Funciones.getFormattedDate(fecha_salida)
                                    + " - " + Funciones.getFormattedDate(fecha_llegada));
                }
                out.println();
            }

            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static HashMap<String, Ubicacion> getUbicacionMap(int maxAirports) {
        HashMap<String, Ubicacion> ubicacionMap = new HashMap<String, Ubicacion>();
        if (maxAirports >= 1)
            ubicacionMap.put("SKBO", new Ubicacion("SKBO", "America del Sur", "Colombia", "Bogota", "bogo", "GMT-5"));
        if (maxAirports >= 2)
            ubicacionMap.put("SEQM", new Ubicacion("SEQM", "America del Sur", "Ecuador", "Quito", "quit", "GMT-5"));
        if (maxAirports >= 3)
            ubicacionMap.put("SVMI", new Ubicacion("SVMI", "America del Sur", "Venezuela", "Caracas", "cara", "GMT-4"));
        if (maxAirports >= 4)
            ubicacionMap.put("SBBR", new Ubicacion("SBBR", "America del Sur", "Brasil", "Brasilia", "bras", "GMT-3"));
        if (maxAirports >= 5)
            ubicacionMap.put("SPIM", new Ubicacion("SPIM", "America del Sur", "Perú", "Lima", "lima", "GMT-5"));
        if (maxAirports >= 6)
            ubicacionMap.put("SLLP", new Ubicacion("SLLP", "America del Sur", "Bolivia", "La Paz", "lapa", "GMT-4"));
        if (maxAirports >= 7)
            ubicacionMap.put("SCEL",
                    new Ubicacion("SCEL", "America del Sur", "Chile", "Santiago de Chile", "sant", "GMT-3"));
        if (maxAirports >= 8)
            ubicacionMap.put("SABE",
                    new Ubicacion("SABE", "America del Sur", "Argentina", "Buenos Aires", "buen", "GMT-3"));
        if (maxAirports >= 9)
            ubicacionMap.put("SGAS", new Ubicacion("SGAS", "America del Sur", "Paraguay", "Asunción", "asun", "GMT-4"));
        if (maxAirports >= 10)
            ubicacionMap.put("SUAA",
                    new Ubicacion("SUAA", "America del Sur", "Uruguay", "Montevideo", "mont", "GMT-3"));
        if (maxAirports >= 11)
            ubicacionMap.put("LATI", new Ubicacion("LATI", "Europa", "Albania", "Tirana", "tira", "GMT+2"));
        if (maxAirports >= 12)
            ubicacionMap.put("EDDI", new Ubicacion("EDDI", "Europa", "Alemania", "Berlin", "berl", "GMT+2"));
        if (maxAirports >= 13)
            ubicacionMap.put("LOWW", new Ubicacion("LOWW", "Europa", "Austria", "Viena", "vien", "GMT+2"));
        if (maxAirports >= 14)
            ubicacionMap.put("EBCI", new Ubicacion("EBCI", "Europa", "Belgica", "Bruselas", "brus", "GMT+2"));
        if (maxAirports >= 15)
            ubicacionMap.put("UMMS", new Ubicacion("UMMS", "Europa", "Bielorrusia", "Minsk", "mins", "GMT+3"));
        if (maxAirports >= 16)
            ubicacionMap.put("LBSF", new Ubicacion("LBSF", "Europa", "Bulgaria", "Sofia", "sofi", "GMT+3"));
        if (maxAirports >= 17)
            ubicacionMap.put("LKPR", new Ubicacion("LKPR", "Europa", "Checa", "Praga", "prag", "GMT+2"));
        if (maxAirports >= 18)
            ubicacionMap.put("LDZA", new Ubicacion("LDZA", "Europa", "Croacia", "Zagreb", "zagr", "GMT+2"));
        if (maxAirports >= 19)
            ubicacionMap.put("EKCH", new Ubicacion("EKCH", "Europa", "Dinamarca", "Copenhague", "cope", "GMT+2"));
        if (maxAirports >= 20)
            ubicacionMap.put("EHAM", new Ubicacion("EHAM", "Europa", "Holanda", "Amsterdam", "amst", "GMT+2"));
        if (maxAirports >= 21)
            ubicacionMap.put("VIDP", new Ubicacion("VIDP", "Asia", "India", "Delhi", "delh", "GMT+5"));
        if (maxAirports >= 22)
            ubicacionMap.put("RKSI", new Ubicacion("RKSI", "Asia", "Corea del Sur", "Seul", "seul", "GMT+9"));
        if (maxAirports >= 23)
            ubicacionMap.put("VTBS", new Ubicacion("VTBS", "Asia", "Tailandia", "Bangkok", "bang", "GMT+7"));
        if (maxAirports >= 24)
            ubicacionMap.put("OMDB", new Ubicacion("OMDB", "Asia", "Emiratos A.U", "Dubai", "emir", "GMT+4"));
        if (maxAirports >= 25)
            ubicacionMap.put("ZBAA", new Ubicacion("ZBAA", "Asia", "China", "Beijing", "beij", "GMT+8"));
        if (maxAirports >= 26)
            ubicacionMap.put("RJTT", new Ubicacion("RJTT", "Asia", "Japon", "Tokyo", "toky", "GMT+9"));
        if (maxAirports >= 27)
            ubicacionMap.put("WMKK", new Ubicacion("WMKK", "Asia", "Malasia", "Kuala Lumpur", "kual", "GMT+8"));
        if (maxAirports >= 28)
            ubicacionMap.put("WSSS", new Ubicacion("WSSS", "Asia", "Singapur", "Singapore", "sing", "GMT+8"));
        if (maxAirports >= 29)
            ubicacionMap.put("WIII", new Ubicacion("WIII", "Asia", "Indonesia", "Jakarta", "jaka", "GMT+7"));
        if (maxAirports >= 30)
            ubicacionMap.put("RPLL", new Ubicacion("RPLL", "Asia", "Filipinas", "Manila", "mani", "GMT+8"));

        return ubicacionMap;
    }

    public static void main(String[] args) {

        // TODO: No procesar paquetes que aun no seran recibidos

        // Data Generation Parameters
        boolean generateNewData = false;
        int maxAirports = 30; // MAX AIRPORTS IS 30
        int packagesAmount = 6000;
        int flightsMultiplier = 2;

        // SImmulated Annealing Parameters
        double temperature = 100000;
        double coolingRate = 0.001;
        int neighbourCount = 1;
        int windowSize = 200; // best = 50 MUST BE LESS THE packagesAmount

        // Weight Parameters
        double badSolutionPenalization = 100;
        double flightPenalization = 200;
        double airportPenalization = 300;

        EventManager manager = new EventManager();
        manager.addEvent(Range.closed(1, 10), "Evento1");
        manager.addEvent(Range.closed(5, 15), "Evento2");
        manager.addEvent(Range.closed(11, 20), "Evento3");

        System.out.println("Eventos completamente contenidos en el rango [3, 12]: "
                + manager.getEventsInRange(Range.closed(3, 12)));
        System.out.println("Eventos completamente contenidos en el rango [0, 16]: "
                + manager.getEventsInRange(Range.closed(0, 16)));

        System.out.println("Eventos en el rango [3, 12]: " + manager.getEventsInRange(Range.closed(3, 12)));
        // funcion_fitness = (SUMA_TOTAL_PAQUETES) * 10 + (SUMA_TOTAL_VUELOS) * 4 +
        // (PROMEDIO_PONDERADO_TIEMPO_AEROPUERTO) * 4
        double sumaPaquetesWeight = 10;
        double sumaVuelosWeight = 4;
        double promedioPonderadoTiempoAeropuertoWeight = 4;

        String inputPath = "src\\main\\java\\com\\pucp\\pe\\resources\\inputGenerado";
        String generatedInputPath = "src\\main\\java\\com\\pucp\\pe\\resources\\inputGenerado";

        HashMap<String, Ubicacion> ubicacionMap = getUbicacionMap(maxAirports);
        ArrayList<Aeropuerto> aeropuertos = Funciones.leerAeropuertos(inputPath, ubicacionMap);

        aeropuertos = new ArrayList<Aeropuerto>(aeropuertos.subList(0, maxAirports));

        if (generateNewData == true) {
            ArrayList<Paquete> paquetes = Funciones.generarPaquetes(
                    packagesAmount,
                    aeropuertos,
                    Funciones.parseDateString("2024-01-01 00:00:00"),
                    Funciones.parseDateString("2024-01-03 23:59:59"),
                    generatedInputPath);

            ArrayList<PlanVuelo> planVuelos = Funciones.generarPlanesDeVuelo(aeropuertos, flightsMultiplier,
                    generatedInputPath);

            System.out.println("Se generaron " + paquetes.size() + " paquetes.");
            System.out.println("Se generaron " + planVuelos.size() + " planes de vuelo.");
            return;
        }

        ArrayList<Paquete> paquetes = Funciones.leerPaquetes(inputPath, ubicacionMap);
        ArrayList<PlanVuelo> planVuelos = Funciones.leerPlanVuelos(inputPath, ubicacionMap);

        GrafoVuelos grafoVuelos = new GrafoVuelos(planVuelos, paquetes);
        HashMap<Integer, Vuelo> vuelos_map = grafoVuelos.getVuelosHash();

        long startTime = System.nanoTime();
        HashMap<String, ArrayList<PlanRuta>> todasLasRutas = new HashMap<String, ArrayList<PlanRuta>>();

        try {
            todasLasRutas = grafoVuelos.buscarTodasLasRutas();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println("Checking if ORIGIN-DESTINATION has atleast 1 route available");
        for (Map.Entry<String, ArrayList<PlanRuta>> entry : todasLasRutas.entrySet()) {
            String key = entry.getKey();
            ArrayList<PlanRuta> value = entry.getValue();
            if (value.size() == 0) {
                System.out.println("No route available for " + key);
            }
        }

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
                vuelos_map);
        current.initialize(todasLasRutas);
        printRutasTXT(current.paquetes, current.rutas, "initial.txt");
        System.out.println("Finished solution initialization");

        startTime = System.nanoTime();
        while (temperature > 1) {
            ArrayList<Solucion> neighbours = new ArrayList<Solucion>();
            for (int i = 0; i < neighbourCount; i++) {
                neighbours.add(
                        current.generateNeighbour(
                                todasLasRutas,
                                windowSize));
            }

            int bestNeighbourIndex = 0;
            double bestNeighbourCost = Double.MAX_VALUE;
            for (int i = 0; i < neighbours.size(); i++) {
                Solucion neighbour = neighbours.get(i);
                double neighbourCost = neighbour.getSolutionCost();
                // System.out.println("Current cost: " + neighbourCost);
                if (neighbourCost < bestNeighbourCost) {
                    bestNeighbourCost = neighbourCost;
                    bestNeighbourIndex = i;
                }
            }

            double currentCost = current.getSolutionCost();
            double newCost = bestNeighbourCost;
            double costDifference = newCost - currentCost;

            if (costDifference < 0 ||
                    Math.exp(-costDifference / temperature) > Math.random()) {
                current = neighbours.get(bestNeighbourIndex);
            }

            int cnt = (int) (current.getSolutionCost() / badSolutionPenalization);
            if (current.getSolutionCost() < badSolutionPenalization) {
                break;
            }

            // Cool down the system
            temperature *= 1 - coolingRate;

            System.out.println(
                    "Current cost: " + current.getSolutionCost() +
                            " | Packages left: " + cnt +
                            " | Temperature: " + temperature);
        }

        endTime = System.nanoTime();
        duration = endTime - startTime;

        System.out.println("Tiempo de ejecución de algoritmo: " + (float) (duration /
                1000000000) + " segundos");

        int cnt2 = (int) (current.costo / badSolutionPenalization);
        System.out.println(
                "Final cost: " + current.costo + " | Packages left: " + cnt2 +
                        " | Temperature: " + temperature);
        printRutasTXT(current.paquetes, current.rutas, "rutasFinal.txt");
        current.printFlightOcupation("ocupacionVuelos.txt");

        // EstadoAlmacen estado = new EstadoAlmacen(
        // current.paquetes,
        // current.rutas,
        // current.vuelos_hash,
        // current.ocupacionVuelos,
        // current.aeropuertos
        // );

        // System.out.println("VERIFICANDO CAPACIDAD");

        // estado.consulta_historica();

    }
}