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
import Clases.EstadoAlmacen;
import Clases.PlanRuta;

//import org.apache.commons.lang3.ArrayUtils;



public class main {
    
    public static HashMap<String, Ubicacion> getUbicacionMap(int maxAirports){
        HashMap<String, Ubicacion> ubicacionMap = new HashMap<String, Ubicacion>();
        if(maxAirports >= 1) ubicacionMap.put("SKBO",new Ubicacion("SKBO", "America del Sur", "Colombia", "Bogota", "bogo", "GMT-5"));
        if(maxAirports >= 2) ubicacionMap.put("SEQM",new Ubicacion("SEQM", "America del Sur", "Ecuador", "Quito", "quit", "GMT-5"));
        if(maxAirports >= 3) ubicacionMap.put("SVMI",new Ubicacion("SVMI", "America del Sur", "Venezuela", "Caracas", "cara", "GMT-4"));
        if(maxAirports >= 4) ubicacionMap.put("SBBR",new Ubicacion("SBBR", "America del Sur", "Brasil", "Brasilia", "bras", "GMT-3"));
        if(maxAirports >= 5) ubicacionMap.put("SPIM",new Ubicacion("SPIM", "America del Sur", "Perú", "Lima", "lima", "GMT-5"));
        if(maxAirports >= 6) ubicacionMap.put("SLLP",new Ubicacion("SLLP", "America del Sur", "Bolivia", "La Paz", "lapa", "GMT-4"));
        if(maxAirports >= 7) ubicacionMap.put("SCEL",new Ubicacion("SCEL", "America del Sur", "Chile", "Santiago de Chile", "sant", "GMT-3"));
        if(maxAirports >= 8) ubicacionMap.put("SABE",new Ubicacion("SABE", "America del Sur", "Argentina", "Buenos Aires", "buen", "GMT-3"));
        if(maxAirports >= 9) ubicacionMap.put("SGAS",new Ubicacion("SGAS", "America del Sur", "Paraguay", "Asunción", "asun", "GMT-4"));
        if(maxAirports >= 10) ubicacionMap.put("SUAA",new Ubicacion("SUAA", "America del Sur", "Uruguay", "Montevideo", "mont", "GMT-3"));
        if(maxAirports >= 11) ubicacionMap.put("LATI",new Ubicacion("LATI", "Europa", "Albania", "Tirana", "tira", "GMT+2"));
        if(maxAirports >= 12) ubicacionMap.put("EDDI",new Ubicacion("EDDI", "Europa", "Alemania", "Berlin", "berl", "GMT+2"));
        if(maxAirports >= 13) ubicacionMap.put("LOWW",new Ubicacion("LOWW", "Europa", "Austria", "Viena", "vien", "GMT+2"));
        if(maxAirports >= 14) ubicacionMap.put("EBCI",new Ubicacion("EBCI", "Europa", "Belgica", "Bruselas", "brus", "GMT+2"));
        if(maxAirports >= 15) ubicacionMap.put("UMMS",new Ubicacion("UMMS", "Europa", "Bielorrusia", "Minsk", "mins", "GMT+3"));
        if(maxAirports >= 16) ubicacionMap.put("LBSF",new Ubicacion("LBSF", "Europa", "Bulgaria", "Sofia", "sofi", "GMT+3"));
        if(maxAirports >= 17) ubicacionMap.put("LKPR",new Ubicacion("LKPR", "Europa", "Checa", "Praga", "prag", "GMT+2"));
        if(maxAirports >= 18) ubicacionMap.put("LDZA",new Ubicacion("LDZA", "Europa", "Croacia", "Zagreb", "zagr", "GMT+2"));
        if(maxAirports >= 19) ubicacionMap.put("EKCH",new Ubicacion("EKCH", "Europa", "Dinamarca", "Copenhague", "cope", "GMT+2"));
        if(maxAirports >= 20) ubicacionMap.put("EHAM",new Ubicacion("EHAM", "Europa", "Holanda", "Amsterdam", "amst", "GMT+2"));
        if(maxAirports >= 21) ubicacionMap.put("VIDP",new Ubicacion("VIDP", "Asia", "India", "Delhi", "delh", "GMT+5"));
        if(maxAirports >= 22) ubicacionMap.put("RKSI",new Ubicacion("RKSI", "Asia", "Corea del Sur", "Seul", "seul", "GMT+9"));
        if(maxAirports >= 23) ubicacionMap.put("VTBS",new Ubicacion("VTBS", "Asia", "Tailandia", "Bangkok", "bang", "GMT+7"));
        if(maxAirports >= 24) ubicacionMap.put("OMDB",new Ubicacion("OMDB", "Asia", "Emiratos A.U", "Dubai", "emir", "GMT+4"));
        if(maxAirports >= 25) ubicacionMap.put("ZBAA",new Ubicacion("ZBAA", "Asia", "China", "Beijing", "beij", "GMT+8"));
        if(maxAirports >= 26) ubicacionMap.put("RJTT",new Ubicacion("RJTT", "Asia", "Japon", "Tokyo", "toky", "GMT+9"));
        if(maxAirports >= 27) ubicacionMap.put("WMKK",new Ubicacion("WMKK", "Asia", "Malasia", "Kuala Lumpur", "kual", "GMT+8"));
        if(maxAirports >= 28) ubicacionMap.put("WSSS",new Ubicacion("WSSS", "Asia", "Singapur", "Singapore", "sing", "GMT+8"));
        if(maxAirports >= 29) ubicacionMap.put("WIII",new Ubicacion("WIII", "Asia", "Indonesia", "Jakarta", "jaka", "GMT+7"));
        if(maxAirports >= 30) ubicacionMap.put("RPLL",new Ubicacion("RPLL", "Asia", "Filipinas", "Manila", "mani", "GMT+8"));

        return ubicacionMap;
    }

    public static void main(String[] args) {

        boolean generateNewData = false;
        int maxAirports = 10;     //MAX AIRPORTS IS 30
        int packagesAmount = 50;
        int flightsMultiplier = 1; //gives off around 800 flights if = 1

        String inputPath = "inputGenerado";
        String generatedInputPath = "inputGenerado";

        Funciones funciones = new Funciones();

        /* 
        Aeropuerto[] aeropuertos = funciones.leerAeropuertos(inputPath);
        HashMap<String, Ubicacion> ubicacionMap = new HashMap<>();
        for (Aeropuerto aeropuerto : aeropuertos) {
            ubicacionMap.put(aeropuerto.getUbicacion().getId(), aeropuerto.getUbicacion());
        }*/
        
        //Paquete[] paquetes = funciones.leerPaquetes(inputPath, ubicacionMap);

        HashMap<String, Ubicacion> ubicacionMap = getUbicacionMap(maxAirports);
        ArrayList<Aeropuerto> aeropuertos = Funciones.leerAeropuertos(inputPath, ubicacionMap);


        //truncate aeropuertos to get only the first maxAirports elements
        aeropuertos = new ArrayList<Aeropuerto>(aeropuertos.subList(0, maxAirports));

        /*Vuelo[] vuelos = funciones.leerVuelos(inputPath, ubicacionMap);
                */
        if(generateNewData == true){
            ArrayList<Paquete> paquetes = Funciones.generarPaquetes(
                packagesAmount, 
                aeropuertos, 
                Funciones.parseDateString("2024-01-01 00:00:00"), 
                Funciones.parseDateString("2024-01-02 23:59:59"), 
                generatedInputPath
            );

            ArrayList<PlanVuelo> planVuelos = Funciones.generarPlanesDeVuelo(aeropuertos, flightsMultiplier, generatedInputPath);

            System.out.println("Se generaron " + paquetes.size() + " paquetes.");
            System.out.println("Se generaron " + planVuelos.size() + " planes de vuelo.");
            return;
        }

        ArrayList<Paquete> paquetes = Funciones.leerPaquetes(inputPath, ubicacionMap);
        ArrayList<PlanVuelo> planVuelos = Funciones.leerPlanVuelos(inputPath, ubicacionMap);

        /* 
        ArrayList<Ubicacion> ubicaciones = funciones.generarUbicaciones(10);
        ArrayList<Aeropuerto> aeropuertos = funciones.generarAeropuertos(ubicaciones, 4);
        ArrayList<PlanVuelo> planVuelos = funciones.generarPlanesDeVuelo(aeropuertos, 1);
        ArrayList<Vuelo> vuelos = funciones.generarVuelos(aeropuertos, planVuelos, 4);*/


        GrafoVuelos grafo_vuelos = new GrafoVuelos(planVuelos, paquetes);
        List<PlanRuta> todasLasRutas = grafo_vuelos.buscarTodasLasRutas();
        System.out.println("Se consiguieron " + todasLasRutas.size() + " rutas posibles.");

    
        //AQUI ES DONDE SE ARMA RUTAS

        /* 
        //turn string into Date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = null;
        try {
            date = sdf.parse("2023-01-03 10:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        */


        /* 
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
*/

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

