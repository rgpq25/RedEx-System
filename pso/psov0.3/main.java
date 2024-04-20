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

import Clases.Aeropuerto;
import Clases.PlanVuelo;
import Clases.Ubicacion;
import Clases.Vuelo;
//import org.apache.commons.lang3.ArrayUtils;

public class main {

    public static void main(String[] args) {
        /* 
        Aeropuerto lima = new Aeropuerto("LIM");
        Aeropuerto miami = new Aeropuerto("MIA");
        Aeropuerto roma = new Aeropuerto("ROM");
        Aeropuerto madrid = new Aeropuerto("MAD");
        */
        Funciones funciones = new Funciones();
        ArrayList<Ubicacion> ubicaciones = funciones.generarUbicaciones(10);
        ArrayList<Aeropuerto> aeropuertos = funciones.generarAeropuertos(ubicaciones, 4);
        ArrayList<PlanVuelo> planVuelos = funciones.generarPlanesDeVuelo(aeropuertos, 1);
        ArrayList<Vuelo> vuelos = funciones.generarVuelos(aeropuertos, planVuelos, 4);
        GrafoVuelos grafo_vuelos = new GrafoVuelos();
        //Itera el array de vuelos
        
        for (Vuelo vuelo : vuelos) {
            //vuelo.print();
            grafo_vuelos.agregarVuelo(vuelo);
        }

        //AQUI ES DONDE SE ARMA RUTAS

        //turn string into Date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = null;
        try {
            date = sdf.parse("2024-04-20 07:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<Ruta> rutas = grafo_vuelos.buscarTodasLasRutas(date);
        /*for (Ruta r : rutas) {
            System.out.println(r);
        }*/

        //print rutas size
        System.out.println(rutas.size());

        /*Paquete paquete1 = new Paquete(lima, roma, "2024-04-15 08:00");
        Paquete paquete2 = new Paquete(miami, lima, "2024-04-16 04:30");
        Paquete paquete3 = new Paquete(madrid, miami, "2024-04-17 05:00");
        Paquete paquete4 = new Paquete(madrid, miami, "2024-04-17 07:00");

        List<Paquete> paquetes = new ArrayList<>();
        paquetes.add(paquete1);
        paquetes.add(paquete2);
        paquetes.add(paquete3);
        paquetes.add(paquete4);

        EspacioBusquedaPSO espacioBusqueda = new EspacioBusquedaPSO(paquetes, grafo_vuelos.buscarTodasLasRutas("2024-04-15 07:00"));
        System.out.println(espacioBusqueda);


        PSO pso = new PSO();
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
        } */

    }
}

