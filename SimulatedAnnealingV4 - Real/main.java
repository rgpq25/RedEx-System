import Clases.Aeropuerto;
import Clases.EstadoAlmacen;
import Clases.Funciones;
import Clases.GrafoVuelos;
import Clases.Paquete;
import Clases.PlanRuta;
import Clases.PlanVuelo;
import Clases.Ubicacion;
import Clases.Vuelo;

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
import java.util.concurrent.TimeUnit;

public class main {

    public static class Solucion {
        public ArrayList<Paquete> paquetes;
        public ArrayList<PlanRuta> rutas;
        public ArrayList<Aeropuerto> aeropuertos;
        public double costo;

        // TODO: El costo real deberia considerar cuantos almacenes esta ocupando. Si
        // TODO: ocupa un aeropuerto / vuelo concurrido el costo deberia ser mayor.

        public Solucion(ArrayList<Paquete> paquetes, ArrayList<PlanRuta> rutas, ArrayList<Aeropuerto> aeropuertos,
                double costo) {
            this.paquetes = paquetes;
            this.rutas = rutas;
            this.aeropuertos = aeropuertos;
            this.costo = costo;
        }

        public double getSolutionCost() {
            double cost = 0;

            for (int i = 0; i < this.paquetes.size(); i++) {
                String idRutaOrigen = this.rutas.get(i).getVuelos().get(0).getPlan_vuelo().getCiudadOrigen().getId();
                String idRutaDestino = this.rutas.get(i).getVuelos().get(this.rutas.get(i).getVuelos().size() - 1)
                        .getPlan_vuelo().getCiudadDestino().getId();

                String idPaqueteOrigen = this.paquetes.get(i).getCiudadOrigen().getId();
                String idPaqueteDestino = this.paquetes.get(i).getCiudadDestino().getId();

                Date horaSalidaRuta = this.rutas.get(i).getVuelos().get(0).getFecha_salida();
                Date horaLlegadaRuta = this.rutas.get(i).getVuelos().get(this.rutas.get(i).getVuelos().size() - 1)
                        .getFecha_llegada();

                Date horaRecepcionPaquete = this.paquetes.get(i).getFecha_recepcion();
                Date horaMaximaEntregaPaquete = this.paquetes.get(i).getFecha_maxima_entrega();

                if (!idRutaOrigen.equals(idPaqueteOrigen) ||
                        !idRutaDestino.equals(idPaqueteDestino) ||
                        !horaSalidaRuta.after(horaRecepcionPaquete) ||
                        !horaLlegadaRuta.before(horaMaximaEntregaPaquete)) {
                    cost += 10000;
                }
            }

            return cost;
        }

        public boolean isCurrentRouteValid(int i) {
            String idRutaOrigen = this.rutas.get(i).getVuelos().get(0).getPlan_vuelo().getCiudadOrigen().getId();
            String idRutaDestino = this.rutas.get(i).getVuelos().get(this.rutas.get(i).getVuelos().size() - 1)
                    .getPlan_vuelo().getCiudadDestino().getId();

            String idPaqueteOrigen = this.paquetes.get(i).getCiudadOrigen().getId();
            String idPaqueteDestino = this.paquetes.get(i).getCiudadDestino().getId();

            Date horaSalidaRuta = this.rutas.get(i).getVuelos().get(0).getFecha_salida();
            Date horaLlegadaRuta = this.rutas.get(i).getVuelos().get(this.rutas.get(i).getVuelos().size() - 1)
                    .getFecha_llegada();

            Date horaRecepcionPaquete = this.paquetes.get(i).getFecha_recepcion();
            Date horaMaximaEntregaPaquete = this.paquetes.get(i).getFecha_maxima_entrega();

            if (!idRutaOrigen.equals(idPaqueteOrigen) ||
                    !idRutaDestino.equals(idPaqueteDestino) ||
                    !horaSalidaRuta.after(horaRecepcionPaquete) ||
                    !horaLlegadaRuta.before(horaMaximaEntregaPaquete)) {
                return false;
            }

            return true;
        }

        public void initialize(HashMap<String, ArrayList<PlanRuta>> todasLasRutas) {

            for (int i = 0; i < paquetes.size(); i++) {
                String origenPaquete = paquetes.get(i).getCiudadOrigen().getId();
                String destinoPaquete = paquetes.get(i).getCiudadDestino().getId();
                String keyString = origenPaquete + "-" + destinoPaquete;
                int randomRouteIndex = (int) (Math.random() * todasLasRutas.get(keyString).size());
                PlanRuta randomRoute = todasLasRutas.get(keyString).get(randomRouteIndex);

                this.rutas.add(randomRoute);
            }

            this.costo = getSolutionCost();
        }

        public Solucion generateNeighbour(HashMap<String, ArrayList<PlanRuta>> todasLasRutas, int windowSize) {

            Solucion neighbour = new Solucion(new ArrayList<>(this.paquetes), new ArrayList<>(this.rutas),
                    new ArrayList<>(this.aeropuertos), costo);
            int randomPackageIndex = (int) (Math.random() * this.paquetes.size());

            // int[] randomPackageIndexes = new int[windowSize];
            // for (int i = 0; i < windowSize; i++) {
            //     randomPackageIndexes[i] = (int) (Math.random() * this.paquetes.size());
            //     //TODO: Asegurar que sena unicos, que no repitan
            // }

            // ArrayList<Paquete> randomPackages = new ArrayList<Paquete>();
            // for (int i = 0; i < windowSize; i++) {
            //     randomPackages.add(neighbour.paquetes.get(randomPackageIndexes[i]));
            // }

            Paquete randomPackage = neighbour.paquetes.get(randomPackageIndex);
            ArrayList<PlanRuta> availableRoutes = new ArrayList<>();


            String origenPaquete = randomPackage.getCiudadOrigen().getId();
            String destinoPaquete = randomPackage.getCiudadDestino().getId();
            String keyString = origenPaquete + "-" + destinoPaquete;

            for (PlanRuta ruta : todasLasRutas.get(keyString)) {
                String idRutaOrigen = ruta.getVuelos().get(0).getPlan_vuelo().getCiudadOrigen().getId();
                String idRutaDestino = ruta.getVuelos().get(ruta.getVuelos().size() - 1).getPlan_vuelo()
                        .getCiudadDestino().getId();

                String idPaqueteOrigen = randomPackage.getCiudadOrigen().getId();
                String idPaqueteDestino = randomPackage.getCiudadDestino().getId();

                Date horaSalidaRuta = ruta.getVuelos().get(0).getFecha_salida();
                Date horaLlegadaRuta = ruta.getVuelos().get(ruta.getVuelos().size() - 1).getFecha_llegada();

                Date horaRecepcionPaquete = randomPackage.getFecha_recepcion();
                Date horaMaximaEntregaPaquete = randomPackage.getFecha_maxima_entrega();

                if (idRutaOrigen.equals(idPaqueteOrigen) &&
                        idRutaDestino.equals(idPaqueteDestino) &&
                        horaSalidaRuta.after(horaRecepcionPaquete) &&
                        horaLlegadaRuta.before(horaMaximaEntregaPaquete)) {
                    availableRoutes.add(ruta);
                }
            }

            double newCost = this.costo;
            if (availableRoutes.size() == 0 && isCurrentRouteValid(randomPackageIndex) == true) {
                newCost += 10000;
            } else if (availableRoutes.size() > 0 && isCurrentRouteValid(randomPackageIndex) == false) {
                newCost -= 10000;
            }

            // TODO: Alternativa, escojer un random index en un loop y verificar si es
            // valido, si no repetir (sera mas rapido facil)
            int randomRouteIndex = (int) (Math.random() * availableRoutes.size());
            PlanRuta randomRoute = new PlanRuta(availableRoutes.get(randomRouteIndex));

            neighbour.rutas.set(randomPackageIndex, randomRoute);
            neighbour.costo = newCost;

            return neighbour;
        }

        public void printTxt() {
            for (int i = 0; i < paquetes.size(); i++) {
                System.out.println("Paquete " + paquetes.get(i).getId() + " -> " + rutas.get(i).toString());
            }
        }
    }

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


        boolean generateNewData = false;
        int maxAirports = 10; // MAX AIRPORTS IS 30
        int packagesAmount = 1000;
        int flightsMultiplier = 1; // gives off around 800 flights if = 1



        int windowSize = 1;
        double temperature = 100000;
        double coolingRate = 0.001;
        int neighbourCount = 1;




        String inputPath = "inputGenerado";
        String generatedInputPath = "inputGenerado";

        HashMap<String, Ubicacion> ubicacionMap = getUbicacionMap(maxAirports);
        ArrayList<Aeropuerto> aeropuertos = Funciones.leerAeropuertos(inputPath, ubicacionMap);

        // truncate aeropuertos to get only the first maxAirports elements
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
        long startTime = System.nanoTime();
        HashMap<String, ArrayList<PlanRuta>> todasLasRutas = grafoVuelos.buscarTodasLasRutas();

        long endTime = System.nanoTime();
        long duration = endTime - startTime;

        System.out.println("Tiempo de ejecución de la ordenación: " + (float) (duration / 1000000000) + " segundos");


       
        Solucion current = new Solucion(paquetes, new ArrayList<PlanRuta>(), aeropuertos, 0);
        current.initialize(todasLasRutas);
        printRutasTXT(current.paquetes, current.rutas, "initial.txt");

        while (temperature > 1) {
            // Pick a random neighboring solution
            ArrayList<Solucion> neighbours = new ArrayList<Solucion>();
            for (int i = 0; i < neighbourCount; i++) {
                neighbours.add(current.generateNeighbour(todasLasRutas, windowSize));
            }

            int bestNeighbourIndex = 0;
            double bestNeighbourCost = Double.MAX_VALUE;
            for (int i = 0; i < neighbours.size(); i++) {
                Solucion neighbour = neighbours.get(i);
                double neighbourCost = neighbour.costo;
                if (neighbourCost < bestNeighbourCost) {
                    bestNeighbourCost = neighbourCost;
                    bestNeighbourIndex = i;
                }
            }

            // Calculate the cost difference between the new and current routes
            double currentCost = current.costo;
            double newCost = neighbours.get(bestNeighbourIndex).costo;
            double costDifference = newCost - currentCost;

            // Decide if we should accept the new solution
            if (costDifference < 0 || Math.exp(-costDifference / temperature) > Math.random()) {
                current = neighbours.get(bestNeighbourIndex);
            }


            int cnt = (int) (current.costo / 10000);
            if (current.costo < 10000) {
                System.out.println("Final cost: " + current.costo + " | Packages left: " + cnt + " | Temperature: " + temperature);
                printRutasTXT(current.paquetes, current.rutas, "rutasFinal.txt");
                return;
            }

            // Cool down the system
            temperature *= 1 - coolingRate;
            
            System.out.println("Current cost: " + current.costo + " | Packages left: " + cnt + " | Temperature: " + temperature);
        }

        // EstadoAlmacen estado = Funciones.obtenerEstadoAlmacen(current.paquetes,
        // current.rutas, current.aeropuertos);
        // estado.consulta_historica();
        // System.out.println("VERIFICANDO CAPACIDAD");
        // HashMap<Aeropuerto, Integer> curr =
        // estado.verificar_capacidad_en(Date.from(LocalDateTime.of(2023, 1, 3,
        // 15,0).toInstant(ZoneOffset.UTC)));
        // for (Aeropuerto aeropuerto : curr.keySet()) {
        // System.out.println("Aeropuerto: " + aeropuerto.getId() + " | Capacidad: " +
        // curr.get(aeropuerto));
        // }
        
        int cnt2 = (int) (current.costo / 10000);
        System.out.println("Final cost: " + current.costo + " | Packages left: " + cnt2 + " | Temperature: " + temperature);
        printRutasTXT(current.paquetes, current.rutas, "rutasFinal.txt");

    }
}