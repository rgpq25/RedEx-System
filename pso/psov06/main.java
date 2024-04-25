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

//import org.apache.commons.lang3.ArrayUtils;

public class main {

    public static void main(String[] args) {

        //Data Generation Parameters
        boolean generateNewData = false;
        int maxAirports = 15; // MAX AIRPORTS IS 30
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

        /*for (Aeropuerto aeropuerto : aeropuertos) {
        ubicacionMap.put(aeropuerto.getUbicacion().getId(),
        aeropuerto.getUbicacion());
        }*/

        //Paquete[] paquetes = funciones.leerPaquetes(inputPath, ubicacionMap);
        //Vuelo[] vuelos = funciones.leerVuelos(inputPath, ubicacionMap);

        /* 
        ArrayList<Ubicacion> ubicaciones = funciones.generarUbicaciones(10);
        ArrayList<Aeropuerto> aeropuertos = funciones.generarAeropuertos(ubicaciones, 4);
        ArrayList<PlanVuelo> planVuelos = funciones.generarPlanesDeVuelo(aeropuertos, 1);
        ArrayList<Vuelo> vuelos = funciones.generarVuelos(aeropuertos, planVuelos, 4);*/
        //GrafoVuelos grafo_vuelos = new GrafoVuelos();
        //Itera el array de vuelos y los agrega al grafo
        /*for (Vuelo vuelo : vuelos) {
            vuelo.print();
            grafo_vuelos.agregarVuelo(vuelo);
        }*/

        //AQUI ES DONDE SE ARMA RUTAS

        //turn string into Date
        /* 
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = null;
        try {
            date = sdf.parse("2023-01-03 10:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }*/

        //List<Ruta> rutas = grafo_vuelos.buscarTodasLasRutas(date);

        //print rutas size
        //System.out.println(rutas.size());

        //Paquete[] paquetesLeidos = funciones.leerPaquetes(inputPath, ubicacionMap);

        //print paquetes
        /*for (Paquete paquete : paquetesLeidos) {
            paquete.print();
        }*/

        //List<Paquete> paquetes = Arrays.asList(paquetesLeidos);

        //EspacioBusquedaPSO espacioBusqueda = new EspacioBusquedaPSO(paquetes, todasLasRutas);
        //System.out.println(espacioBusqueda);


        //PSO pso = new PSO();
        int[] bestComb = PSO.pso(paquetes, rutas, 100, 1000, 0.7, 1.4, 1.4);

        List<Integer> bestCombList = new ArrayList<>();
        for (int i = 0; i < bestComb.length; i++) {
            bestCombList.add(bestComb[i]);
        }
        //double bestFit = PSO.fitness(Arrays.asList(ArrayUtils.toObject(bestComb)), paquetes, rutas);
        double bestFit = PSO.fitness(bestCombList, paquetes, rutas);

        /*for (int i = 0; i < bestComb.length; i++) {
            int paqueteIdx = i + 1;
            for (Ruta ruta : rutas) {
                Matcher matcher = Pattern.compile("\\d+").matcher(ruta.identificador);
                if (matcher.find()) {
                    int numeroRuta = Integer.parseInt(matcher.group());
                    if (numeroRuta == bestComb[i]) {
                        System.out.println("La mejor ruta para el paquete " + paqueteIdx + " con fitness " + bestFit + " fue:");
                        System.out.println(ruta);
                        break;
                    }
                }
            }
        } */
        for (int i = 0; i < bestComb.length; i++) {
            String ciudadOrigen = paquetes.get(i).getCiudadOrigen().getId();
            //get ciudad destino paquete id
            String ciudadDestino = paquetes.get(i).getCiudadDestino().getId();
            String cadena = ciudadOrigen + "-" + ciudadDestino;
            ArrayList<PlanRuta> planRutas = rutas.get(cadena);
            System.out.println("La mejor ruta para el paquete " + i + " fue:");
            System.out.println(planRutas.get(bestComb[i]));
        }
    }
}

