import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

            // Create a FileWriter to write to the file
            FileWriter fileWriter = new FileWriter(filePath);
            
            // Create a BufferedWriter to improve performance
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            
            // Redirect standard output to the file
            System.setOut(new PrintStream(new FileOutputStream(filePath)));
            
            //Data Generation Parameters
            boolean generateNewData = false;
            int maxAirports = 30; // MAX AIRPORTS IS 30
            int packagesAmount = 1000;
            int flightsMultiplier = 1;

            Funciones funciones = new Funciones();
            String inputPath = "input";
            //HashMap<String, Ubicacion> ubicacionMap = new HashMap<>();
            HashMap<String, Ubicacion> ubicacionMap = funciones.getUbicacionMap(maxAirports);
            ArrayList<Aeropuerto> aeropuertos = funciones.leerAeropuertos(inputPath,ubicacionMap);
            aeropuertos = new ArrayList<Aeropuerto>(aeropuertos.subList(0, maxAirports));
            ArrayList<Paquete> paquetes = Funciones.leerPaquetes(inputPath, ubicacionMap);
            ArrayList<PlanVuelo> planVuelos = Funciones.leerPlanVuelos(inputPath, ubicacionMap);

            //print each package
            /*for (Paquete paquete : paquetes) {
                paquete.print();
            }*/

            //print each planvuelo
            /*for (PlanVuelo planVuelo : planVuelos) {
                planVuelo.print();
            }*/

            //print end message
            System.out.println("Data loaded successfully");

            GrafoVuelos grafoVuelos = new GrafoVuelos(planVuelos, paquetes);
            //grafoVuelos.imprimirVuelos();

            long startTime = System.nanoTime();
            HashMap<String, ArrayList<PlanRuta>> rutas = grafoVuelos.buscarTodasLasRutas();
            long endTime = System.nanoTime();
            long duration = endTime - startTime;

            System.out.println("Tiempo de ejecución de la ordenación: " + (float) (duration / 1000000000) + " segundos");
            //funciones.imprimirTodasLasRutas(rutas);

            //PARTE DE PSO
            //PSO pso = new PSO();
            long startTime2 = System.nanoTime();
            int[] bestComb = PSO.pso(paquetes, rutas,aeropuertos, 100, 1000, 0.7, 1.4, 1.4);
            long endTime2 = System.nanoTime();
            long duration2 = endTime2 - startTime2;
            System.out.println("Tiempo de ejecución del algoritmo PSO: " + (float) (duration2 / 1000000000) + " segundos");

            List<Integer> bestCombList = new ArrayList<>();
            for (int i = 0; i < bestComb.length; i++) {
                bestCombList.add(bestComb[i]);
            }
            //double bestFit = PSO.fitness(Arrays.asList(ArrayUtils.toObject(bestComb)), paquetes, rutas);
            double bestFit = PSO.fitness(bestCombList, paquetes, rutas, aeropuertos);
            //print bestFit
            System.out.println("Best fitness: " + bestFit);
            //IMPRIME LA MEJOR SOLUCION
            for (int i = 0; i < bestComb.length; i++) {
                String ciudadOrigen = paquetes.get(i).getCiudadOrigen().getId();
                //get ciudad destino paquete id
                String ciudadDestino = paquetes.get(i).getCiudadDestino().getId();
                String cadena = ciudadOrigen + "-" + ciudadDestino;
                ArrayList<PlanRuta> planRutas = rutas.get(cadena);
                if(planRutas == null){
                    System.out.println("No hay rutas para el paquete " + i);
                }
                else{
                    System.out.println("La mejor ruta para el paquete " + i + " fue:");
                    System.out.println(planRutas.get(bestComb[i]));
                }
            }
            System.setOut(originalStdOut);
            bufferedWriter.close();
            System.out.println("Fin");
        }catch (IOException e) {
            // Handle IO Exception
            e.printStackTrace();
        }

        
    }
}

