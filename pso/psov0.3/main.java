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
//import org.apache.commons.lang3.ArrayUtils;

public class main {

    public static void main(String[] args) {

        Funciones funciones = new Funciones();
        String inputPath = "input";
        Aeropuerto[] aeropuertos = funciones.leerAeropuertos(inputPath);
        HashMap<String, Ubicacion> ubicacionMap = new HashMap<>();
        for (Aeropuerto aeropuerto : aeropuertos) {
        ubicacionMap.put(aeropuerto.getUbicacion().getId(),
        aeropuerto.getUbicacion());
        }
        //Paquete[] paquetes = funciones.leerPaquetes(inputPath, ubicacionMap);
        Vuelo[] vuelos = funciones.leerVuelos(inputPath, ubicacionMap);

        /* 
        ArrayList<Ubicacion> ubicaciones = funciones.generarUbicaciones(10);
        ArrayList<Aeropuerto> aeropuertos = funciones.generarAeropuertos(ubicaciones, 4);
        ArrayList<PlanVuelo> planVuelos = funciones.generarPlanesDeVuelo(aeropuertos, 1);
        ArrayList<Vuelo> vuelos = funciones.generarVuelos(aeropuertos, planVuelos, 4);*/
        GrafoVuelos grafo_vuelos = new GrafoVuelos();
        //Itera el array de vuelos y los agrega al grafo
        for (Vuelo vuelo : vuelos) {
            vuelo.print();
            grafo_vuelos.agregarVuelo(vuelo);
        }

        //AQUI ES DONDE SE ARMA RUTAS

        //turn string into Date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = null;
        try {
            date = sdf.parse("2023-01-03 10:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<Ruta> rutas = grafo_vuelos.buscarTodasLasRutas(date);

        //print rutas size
        System.out.println(rutas.size());

        Paquete[] paquetesLeidos = funciones.leerPaquetes(inputPath, ubicacionMap);

        //print paquetes
        for (Paquete paquete : paquetesLeidos) {
            paquete.print();
        }

        List<Paquete> paquetes = Arrays.asList(paquetesLeidos);

        EspacioBusquedaPSO espacioBusqueda = new EspacioBusquedaPSO(paquetes, grafo_vuelos.buscarTodasLasRutas(date));
        System.out.println(espacioBusqueda);


        //PSO pso = new PSO();
        int[] bestComb = PSO.pso(paquetes, rutas, 100, 1000, 0.7, 1.4, 1.4);
        List<Integer> bestCombList = new ArrayList<>();
        for (int i = 0; i < bestComb.length; i++) {
            bestCombList.add(bestComb[i]);
        }
        //double bestFit = PSO.fitness(Arrays.asList(ArrayUtils.toObject(bestComb)), paquetes, rutas);
        double bestFit = PSO.fitness(bestCombList, paquetes, rutas);

        for (int i = 0; i < bestComb.length; i++) {
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
        } /**/

    }
}

