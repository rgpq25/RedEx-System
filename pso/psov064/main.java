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
import Clases.Ubicacion;
import Clases.Vuelo;
import Clases.Paquete;
import Clases.Funciones;
import Clases.PlanRuta;

import java.io.*;

//import org.apache.commons.lang3.ArrayUtils;

public class main {

    public static void main(String[] args) {
        String filePath = "output.txt";
        try{
            PrintStream originalStdOut = System.out;
            System.out.println("Inicio");

            
            
            //Data Generation Parameters
            boolean generateNewData = false;
            int maxAirports = 30; // MAX AIRPORTS IS 30
            int packagesAmount = 1000;
            int flightsMultiplier = 2;

            Funciones funciones = new Funciones();
            String inputPath = "inputGenerado";
            String generatedInputPath = "inputGenerado";
            //HashMap<String, Ubicacion> ubicacionMap = new HashMap<>();
            HashMap<String, Ubicacion> ubicacionMap = funciones.getUbicacionMap(maxAirports);
            ArrayList<Aeropuerto> aeropuertos = funciones.leerAeropuertos(inputPath,ubicacionMap);
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

            // Create a FileWriter to write to the file
            FileWriter fileWriterOutput = new FileWriter(filePath);
            
            // Create a BufferedWriter to improve performance
            BufferedWriter bufferedWriterOutput = new BufferedWriter(fileWriterOutput);
            
            // Redirect standard output to the file
            System.setOut(new PrintStream(new FileOutputStream(filePath)));

            
            ArrayList<Paquete> paquetes = Funciones.leerPaquetes(inputPath, ubicacionMap);
            ArrayList<PlanVuelo> planVuelos = Funciones.leerPlanVuelos(inputPath, ubicacionMap);
            

            //print each package
            /*for (Paquete paquete : paquetes) {
                paquete.print();
            }

            //print each planvuelo
            for (PlanVuelo planVuelo : planVuelos) {
                planVuelo.print();
            }*/

            //print end message
            System.out.println("Data loaded successfully");
            long startTime0 = System.nanoTime();
            GrafoVuelos grafoVuelos = new GrafoVuelos(planVuelos, paquetes);
            long endTime0 = System.nanoTime();
            long duration0 = endTime0 - startTime0;
            System.out.println("Tiempo de ejecución (grafoVuelos): " + (float) (duration0 / 1000000000) + " segundos");
            //grafoVuelos.imprimirVuelos();

            long startTime = System.nanoTime();
            //HashMap<String, ArrayList<PlanRuta>> rutas = grafoVuelos.buscarTodasLasRutas();
            HashMap<String, ArrayList<PlanRuta>> rutas = new HashMap<String, ArrayList<PlanRuta>>();
            try {
                rutas = grafoVuelos.buscarTodasLasRutas();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            System.out.println("Checking if ORIGIN-DESTINATION has atleast 1 route available");
            for (Map.Entry<String, ArrayList<PlanRuta>> entry : rutas.entrySet()) {
                String key = entry.getKey();
                ArrayList<PlanRuta> value = entry.getValue();
                if (value.size() == 0) {
                    System.out.println("No route available for " + key);
                }
            }

            long endTime = System.nanoTime();
            long duration = endTime - startTime;

            System.out.println("Tiempo de ejecución de la ordenación (buscarTodasLasRutas): " + (float) (duration / 1000000000) + " segundos");
            //funciones.imprimirTodasLasRutas(rutas); 
            
            HashMap<Integer, Vuelo> vuelos_map = grafoVuelos.getVuelosHash();

            //tengo ArrayList<Paquete> paquetes, tengo HashMap<String, ArrayList<PlanRuta>> rutas
            //quiero HashMap<Integer, ArrayList<PlanRuta>>
            long startTime3 = System.nanoTime();
            HashMap<Integer, ArrayList<PlanRuta>> rutas_validas = funciones.getRutasValidasPorPaquete(paquetes, rutas);
            long endTime3 = System.nanoTime();
            long duration3 = endTime3 - startTime3;
            System.out.println("Tiempo de (getRutasValidasPorPaquete): " + (float) (duration3 / 1000000000) + " segundos");

            //PARTE DE PSO
            //PSO pso = new PSO();
            long startTime2 = System.nanoTime();
            int[] bestComb = PSO.pso(paquetes, rutas_validas,aeropuertos,vuelos_map, 100, 1000, 0.7, 1.4, 1.4);
            long endTime2 = System.nanoTime();
            long duration2 = endTime2 - startTime2;
            System.out.println("Tiempo de ejecución del algoritmo PSO: " + (float) (duration2 / 1000000000) + " segundos");

            List<Integer> bestCombList = new ArrayList<>();
            for (int i = 0; i < bestComb.length; i++) {
                bestCombList.add(bestComb[i]);
            }
            //double bestFit = PSO.fitness(Arrays.asList(ArrayUtils.toObject(bestComb)), paquetes, rutas);
            double bestFit = PSO.fitness(bestCombList, paquetes, rutas_validas, aeropuertos,vuelos_map);
            //print bestFit
            System.out.println("Best fitness: " + bestFit);
            //IMPRIME LA MEJOR SOLUCION
            for (int i = 0; i < bestComb.length; i++) {
                ArrayList<PlanRuta> planRutas = rutas_validas.get(paquetes.get(i).getId());
                //print paquete
                paquetes.get(i).print();
                if(planRutas == null){
                    System.out.println("No hay rutas para el paquete " + i);
                }
                else{
                    System.out.println("La mejor ruta para el paquete " + i + " fue:");
                    System.out.println(planRutas.get(bestComb[i]));
                    //verifica la hora salida del primer vuelo
                    long tiempoRecepcion = paquetes.get(i).getFecha_recepcion().getTime();
                    long tiempoPartidaRuta = planRutas.get(bestComb[i]).getVuelos().get(0).getFecha_salida().getTime();
                    long tiempoLlegadaRuta = planRutas.get(bestComb[i]).getVuelos().get(planRutas.get(bestComb[i]).getVuelos().size() - 1).getFecha_llegada().getTime();
                    if(tiempoRecepcion > tiempoPartidaRuta){
                        System.out.println("El paquete " + i + " se recibió después de la salida del primer vuelo");
                    }
                    if(tiempoLlegadaRuta > paquetes.get(i).getFecha_maxima_entrega().getTime()){
                        System.out.println("El paquete " + i + " llega después de la fecha máxima de entrega");
                    }
                }
                System.out.println();
            }
            System.setOut(originalStdOut);
            bufferedWriterOutput.close();
            System.out.println("Fin");
        }catch (IOException e) {
            // Handle IO Exception
            e.printStackTrace();
        }

        
    }
}

