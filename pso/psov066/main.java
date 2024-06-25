import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Clases.Aeropuerto;
import Clases.PlanVuelo;
import Clases.RutasFileReader;
import Clases.Ubicacion;
import Clases.Vuelo;
import Clases.Paquete;
import Clases.Funciones;
import Clases.GrafoVuelos;
import Clases.PlanRuta;

import java.io.*;

//import org.apache.commons.lang3.ArrayUtils;

public class main {

    public static void main(String[] args) {
        String filePath = "output.txt";
        try {
            PrintStream originalStdOut = System.out;
            System.out.println("Inicio");

            // Data Generation Parameters
            boolean generateNewData = false;
            int maxAirports = 30; // MAX AIRPORTS IS 30
            int packagesAmount = 1000;
            int flightsMultiplier = 2;

            Funciones funciones = new Funciones();
            String inputPath = "inputGenerado";
            String generatedInputPath = "inputGenerado";
            String rutasPath = "rutasHash";
            String profePath = "inputProfe";
            // HashMap<String, Ubicacion> ubicacionMap = new HashMap<>();
            HashMap<String, Ubicacion> ubicacionMap = funciones.getUbicacionMap(maxAirports);
            ArrayList<Aeropuerto> aeropuertos = funciones.leerAeropuertos(inputPath, ubicacionMap);
            aeropuertos = new ArrayList<Aeropuerto>(aeropuertos.subList(0, maxAirports));

            // Create a FileWriter to write to the file
            FileWriter fileWriterOutput = new FileWriter(filePath);

            // Create a BufferedWriter to improve performance
            BufferedWriter bufferedWriterOutput = new BufferedWriter(fileWriterOutput);

            // Redirect standard output to the file
            System.setOut(new PrintStream(new FileOutputStream(filePath)));

            ArrayList<Paquete> paquetes = Funciones.leerPaquetes(inputPath, ubicacionMap);
            // ArrayList<PlanVuelo> planVuelos = Funciones.leerPlanVuelos(inputPath,
            // ubicacionMap);
            ArrayList<PlanVuelo> planVuelos = Funciones.leerRawPlanesVuelo(ubicacionMap, profePath);


            // print end message
            System.out.println("Data loaded successfully");
            long startTime0 = System.nanoTime();
            GrafoVuelos grafoVuelos = new GrafoVuelos(planVuelos, paquetes);
            HashMap<Integer, Vuelo> vuelos_map = grafoVuelos.getVuelosHash();
            long endTime0 = System.nanoTime();
            long duration0 = endTime0 - startTime0;
            System.out.println("Tiempo de ejecución (grafoVuelos): " + (float) (duration0 / 1000000000) + " segundos");
            // grafoVuelos.imprimirVuelos();

            HashMap<String, ArrayList<PlanRuta>> rutas2 = RutasFileReader.readRutasFiles(rutasPath, vuelos_map);
            // iterate hashmap
            for (Map.Entry<String, ArrayList<PlanRuta>> entry : rutas2.entrySet()) {
                String key = entry.getKey();
                ArrayList<PlanRuta> value = entry.getValue();

                for (PlanRuta planRuta : value) {
                    // check if planRuta is null
                    if (planRuta == null || planRuta.getVuelos() == null) {
                        System.out.println("Key: " + key);
                        System.out.println("PlanRuta is null");
                    }
                }
            }

            long startTime3 = System.nanoTime();
            HashMap<Integer, ArrayList<PlanRuta>> rutas_validas = funciones.getRutasValidasPorPaquete(paquetes, rutas2);
            long endTime3 = System.nanoTime();
            long duration3 = endTime3 - startTime3;
            System.out.println(
                    "Tiempo de (getRutasValidasPorPaquete): " + (float) (duration3 / 1000000000) + " segundos");

            // PARTE DE PSO
            // PSO pso = new PSO();
            long startTime2 = System.nanoTime();
            int[] bestComb = PSO.pso(paquetes, rutas_validas, aeropuertos, vuelos_map, 100, 1000, 0.7, 1.4, 1.4);
            long endTime2 = System.nanoTime();
            long duration2 = endTime2 - startTime2;
            System.out.println(
                    "Tiempo de ejecución del algoritmo PSO: " + (float) (duration2 / 1000000000) + " segundos");

            List<Integer> bestCombList = new ArrayList<>();
            for (int i = 0; i < bestComb.length; i++) {
                bestCombList.add(bestComb[i]);
            }
            // double bestFit = PSO.fitness(Arrays.asList(ArrayUtils.toObject(bestComb)),
            // paquetes, rutas);
            boolean impresionCostos = true;
            double bestFit = PSO.fitness(bestCombList, paquetes, rutas_validas, aeropuertos, vuelos_map,
                    impresionCostos);
            // print bestFit
            System.out.println("Best fitness: " + bestFit);
            // IMPRIME LA MEJOR SOLUCION
            for (int i = 0; i < bestComb.length; i++) {
                ArrayList<PlanRuta> planRutas = rutas_validas.get(paquetes.get(i).getId());
                // print paquete
                paquetes.get(i).print();
                if (planRutas == null) {
                    System.out.println("No hay rutas para el paquete " + i);
                } else {
                    System.out.println("La mejor ruta para el paquete " + i + " fue:");
                    System.out.println(planRutas.get(bestComb[i]));
                    // verifica la hora salida del primer vuelo
                    long tiempoRecepcion = paquetes.get(i).getFecha_recepcion().getTime();
                    long tiempoPartidaRuta = planRutas.get(bestComb[i]).getVuelos().get(0).getFecha_salida().getTime();
                    long tiempoLlegadaRuta = planRutas.get(bestComb[i]).getVuelos()
                            .get(planRutas.get(bestComb[i]).getVuelos().size() - 1).getFecha_llegada().getTime();
                    if (tiempoRecepcion > tiempoPartidaRuta) {
                        System.out.println("El paquete " + i + " se recibió después de la salida del primer vuelo");
                    }
                    if (tiempoLlegadaRuta > paquetes.get(i).getFecha_maxima_entrega().getTime()) {
                        System.out.println("El paquete " + i + " llega después de la fecha máxima de entrega");
                    }
                }
                System.out.println();
            }
            System.setOut(originalStdOut);
            bufferedWriterOutput.close();
            System.out.println("Fin");
        } catch (IOException e) {
            // Handle IO Exception
            e.printStackTrace();
        } /*
           * catch (Exception e) {
           * // Handle IO Exception
           * e.printStackTrace();
           * }
           */

    }
}
