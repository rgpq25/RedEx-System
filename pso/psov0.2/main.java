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
//import org.apache.commons.lang3.ArrayUtils;

public class main {

    public static void main(String[] args) {

        Aeropuerto lima = new Aeropuerto("LIM");
        Aeropuerto miami = new Aeropuerto("MIA");
        Aeropuerto roma = new Aeropuerto("ROM");
        Aeropuerto madrid = new Aeropuerto("MAD");

        //formato 'yyyy-mm-dd HH:MM'
        Vuelo vuelo1 = new Vuelo(lima, miami, "2024-04-15 08:00", "2024-04-15 14:00", "V001");
        Vuelo vuelo2 = new Vuelo(miami, roma, "2024-04-15 16:00", "2024-04-16 08:00", "V002");
        Vuelo vuelo3 = new Vuelo(lima, madrid, "2024-04-15 09:00", "2024-04-15 20:00", "V003");
        Vuelo vuelo4 = new Vuelo(madrid, roma, "2024-04-16 10:00", "2024-04-16 12:00", "V004");
        Vuelo vuelo5 = new Vuelo(miami, roma, "2024-04-16 10:00", "2024-04-16 22:00", "V005");
        Vuelo vuelo6 = new Vuelo(miami, lima, "2024-04-17 09:00", "2024-04-17 15:00", "V006");
        Vuelo vuelo7 = new Vuelo(roma, madrid, "2024-04-17 13:00", "2024-04-17 15:00", "V007");
        Vuelo vuelo8 = new Vuelo(lima, roma, "2024-04-16 23:00", "2024-04-17 09:00", "V008");
        Vuelo vuelo9 = new Vuelo(madrid, miami, "2024-04-17 16:00", "2024-04-18 02:00", "V009");
        Vuelo vuelo10 = new Vuelo(roma, miami, "2024-04-17 17:00", "2024-04-18 03:00", "V010");

        //Crear el grafo de vuelos y agregar los vuelos
        GrafoVuelos grafo_vuelos = new GrafoVuelos();
        grafo_vuelos.agregarVuelo(vuelo1);
        grafo_vuelos.agregarVuelo(vuelo2);
        grafo_vuelos.agregarVuelo(vuelo3);
        grafo_vuelos.agregarVuelo(vuelo4);
        grafo_vuelos.agregarVuelo(vuelo5);
        grafo_vuelos.agregarVuelo(vuelo6);
        grafo_vuelos.agregarVuelo(vuelo7);
        grafo_vuelos.agregarVuelo(vuelo8);
        grafo_vuelos.agregarVuelo(vuelo9);
        grafo_vuelos.agregarVuelo(vuelo10);

        //AQUI ES DONDE SE ARMA RUTAS
        List<Ruta> rutas = grafo_vuelos.buscarTodasLasRutas("2024-04-15 07:00");
        for (Ruta r : rutas) {
            System.out.println(r);
        }

        Paquete paquete1 = new Paquete(lima, roma, "2024-04-15 08:00");
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
        }
    }
}

