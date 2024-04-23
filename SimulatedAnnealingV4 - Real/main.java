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

        public void initialize(List<PlanRuta> todasLasRutas) {
            double costo = 0;

            for (int i = 0; i < paquetes.size(); i++) {
                int randomRouteIndex = (int) (Math.random() * todasLasRutas.size());
                PlanRuta randomRoute = todasLasRutas.get(randomRouteIndex);

                // ArrayList<CapacidadPorTiempo> huecos = getRouteHoles(randomRoute);
                // for (CapacidadPorTiempo hueco : huecos) {
                // //agregarlos a su aeropuerto correspondiente
                // }

                this.rutas.add(randomRoute);
                costo += randomRoute.getVuelos().size();
            }

            this.costo = costo;
        }

        

        public Solucion generateNeighbour(List<PlanRuta> todasLasRutas) {

            Solucion neighbour = new Solucion(new ArrayList<>(this.paquetes), new ArrayList<>(this.rutas),
                    new ArrayList<>(this.aeropuertos), costo);
            int randomPackageIndex = (int) (Math.random() * this.paquetes.size());
            Paquete randomPackage = neighbour.paquetes.get(randomPackageIndex);
            ArrayList<PlanRuta> availableRoutes = new ArrayList<>();

            for (PlanRuta ruta : todasLasRutas) {
                if (ruta.getVuelos().get(0).getPlan_vuelo().getCiudadOrigen().getId()
                        .equals(randomPackage.getCiudadOrigen().getId()) &&
                        ruta.getVuelos().get(ruta.getVuelos().size() - 1).getPlan_vuelo().getCiudadDestino().getId()
                                .equals(randomPackage.getCiudadDestino().getId())) {
                    availableRoutes.add(ruta);
                }
            }

            if (availableRoutes.size() == 0) {
                System.out.println("No available routes for package (" + randomPackage.getId() + ")");
                return neighbour;
            }

            // for (PlanRuta availableRoute : availableRoutes) {
            // boolean capacidadDisponible = true;
            // for (Vuelo vuelo : availableRoute.getVuelos()) {
            // if
            // (!vuelo.getPlan_vuelo().getAeropuerto().tieneCapacidadDisponible(vuelo.getFecha_salida()))
            // {
            // capacidadDisponible = false;
            // break;
            // }
            // }
            // if (capacidadDisponible) {
            // neighbour.rutas.set(randomPackageIndex, availableRoute);
            // break;
            // }
            // }


            int randomRouteIndex = (int) (Math.random() * availableRoutes.size());
            PlanRuta randomRoute = new PlanRuta(availableRoutes.get(randomRouteIndex));

            // //restamos fecha de paquete - baseline para darnos la diferencia
            long diff = Funciones.getDifferenceInDays(
                randomRoute.getVuelos().get(0).getFecha_salida(), 
                randomPackage.getFecha_recepcion()
            );

            //System.out.println("Diferencia de dias: " + diff);


            //TODO: Confirmar que esto no afecta el plan ruta original con una impresion del plan ruta escogido originalmente
            for (Vuelo vuelo : randomRoute.getVuelos()) {
                Date newFechaSalida = new Date(vuelo.getFecha_salida().getTime() + TimeUnit.DAYS.toMillis(diff));
                Date newFechaLlegada = new Date(vuelo.getFecha_llegada().getTime() + TimeUnit.DAYS.toMillis(diff));
                vuelo.setFecha_salida(newFechaSalida);
                vuelo.setFecha_llegada(newFechaLlegada);
            }



            neighbour.rutas.set(randomPackageIndex, randomRoute);

            double newCost = 0;
            for (int i = 0; i < neighbour.paquetes.size(); i++) {
                newCost += neighbour.rutas.get(i).getVuelos().size();
            }
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
                out.println(
                        "Paquete " + i + " - " + origen_paquete + "-" + destino_paquete + "  |  " + Funciones.getFormattedDate(fecha_recepcion));
                for (int j = 0; j < rutas.get(i).length(); j++) {
                    String id_origen = rutas.get(i).getVuelos().get(j).getPlan_vuelo().getCiudadOrigen().getId();
                    String id_destino = rutas.get(i).getVuelos().get(j).getPlan_vuelo().getCiudadDestino().getId();
                    Date fecha_salida = rutas.get(i).getVuelos().get(j).getFecha_salida();
                    Date fecha_llegada = rutas.get(i).getVuelos().get(j).getFecha_llegada();

                    out.println("         " + id_origen + "-" + id_destino + " " + Funciones.getFormattedDate(fecha_salida) + " - " + Funciones.getFormattedDate(fecha_llegada));
                }
                out.println();
            }

            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String inputPath = "inputReal";
        String generatedInputPath = "inputGenerado";

        HashMap<String, Ubicacion> ubicacionMap = new HashMap<String, Ubicacion>();
        ubicacionMap.put("SKBO",new Ubicacion("SKBO", "America del Sur", "Colombia", "Bogota", "bogo", "GMT-5"));
        ubicacionMap.put("SEQM",new Ubicacion("SEQM", "America del Sur", "Ecuador", "Quito", "quit", "GMT-5"));
        ubicacionMap.put("SVMI",new Ubicacion("SVMI", "America del Sur", "Venezuela", "Caracas", "cara", "GMT-4"));
        ubicacionMap.put("SBBR",new Ubicacion("SBBR", "America del Sur", "Brasil", "Brasilia", "bras", "GMT-3"));
        ubicacionMap.put("SPIM",new Ubicacion("SPIM", "America del Sur", "Perú", "Lima", "lima", "GMT-5"));
        ubicacionMap.put("SLLP",new Ubicacion("SLLP", "America del Sur", "Bolivia", "La Paz", "lapa", "GMT-4"));
        ubicacionMap.put("SCEL",new Ubicacion("SCEL", "America del Sur", "Chile", "Santiago de Chile", "sant", "GMT-3"));
        ubicacionMap.put("SABE",new Ubicacion("SABE", "America del Sur", "Argentina", "Buenos Aires", "buen", "GMT-3"));
        ubicacionMap.put("SGAS",new Ubicacion("SGAS", "America del Sur", "Paraguay", "Asunción", "asun", "GMT-4"));
        ubicacionMap.put("SUAA",new Ubicacion("SUAA", "America del Sur", "Uruguay", "Montevideo", "mont", "GMT-3"));
        ubicacionMap.put("LATI",new Ubicacion("LATI", "Europa", "Albania", "Tirana", "tira", "GMT+2"));
        ubicacionMap.put("EDDI",new Ubicacion("EDDI", "Europa", "Alemania", "Berlin", "berl", "GMT+2"));
        ubicacionMap.put("LOWW",new Ubicacion("LOWW", "Europa", "Austria", "Viena", "vien", "GMT+2"));
        ubicacionMap.put("EBCI",new Ubicacion("EBCI", "Europa", "Belgica", "Bruselas", "brus", "GMT+2"));
        ubicacionMap.put("UMMS",new Ubicacion("UMMS", "Europa", "Bielorrusia", "Minsk", "mins", "GMT+3"));
        ubicacionMap.put("LBSF",new Ubicacion("LBSF", "Europa", "Bulgaria", "Sofia", "sofi", "GMT+3"));
        ubicacionMap.put("LKPR",new Ubicacion("LKPR", "Europa", "Checa", "Praga", "prag", "GMT+2"));
        ubicacionMap.put("LDZA",new Ubicacion("LDZA", "Europa", "Croacia", "Zagreb", "zagr", "GMT+2"));
        ubicacionMap.put("EKCH",new Ubicacion("EKCH", "Europa", "Dinamarca", "Copenhague", "cope", "GMT+2"));
        ubicacionMap.put("EHAM",new Ubicacion("EHAM", "Europa", "Holanda", "Amsterdam", "amst", "GMT+2"));
        ubicacionMap.put("VIDP",new Ubicacion("VIDP", "Asia", "India", "Delhi", "delh", "GMT+5"));
        ubicacionMap.put("RKSI",new Ubicacion("RKSI", "Asia", "Corea del Sur", "Seul", "seul", "GMT+9"));
        ubicacionMap.put("VTBS",new Ubicacion("VTBS", "Asia", "Tailandia", "Bangkok", "bang", "GMT+7"));
        ubicacionMap.put("OMDB",new Ubicacion("OMDB", "Asia", "Emiratos A.U", "Dubai", "emir", "GMT+4"));
        ubicacionMap.put("ZBAA",new Ubicacion("ZBAA", "Asia", "China", "Beijing", "beij", "GMT+8"));
        ubicacionMap.put("RJTT",new Ubicacion("RJTT", "Asia", "Japon", "Tokyo", "toky", "GMT+9"));
        ubicacionMap.put("WMKK",new Ubicacion("WMKK", "Asia", "Malasia", "Kuala Lumpur", "kual", "GMT+8"));
        ubicacionMap.put("WSSS",new Ubicacion("WSSS", "Asia", "Singapur", "Singapore", "sing", "GMT+8"));
        ubicacionMap.put("WIII",new Ubicacion("WIII", "Asia", "Indonesia", "Jakarta", "jaka", "GMT+7"));
        ubicacionMap.put("RPLL",new Ubicacion("RPLL", "Asia", "Filipinas", "Manila", "mani", "GMT+8"));

        ArrayList<Aeropuerto> aeropuertos = Funciones.leerAeropuertos(inputPath, ubicacionMap);
        //ArrayList<Paquete> paquetes = Funciones.leerPaquetes(inputPath, ubicacionMap);
        //ArrayList<PlanVuelo> planVuelos = Funciones.leerPlanVuelos(inputPath, ubicacionMap);

        //ArrayList<Ubicacion> ubicaciones = Funciones.generarUbicaciones(10);
        //ArrayList<Aeropuerto> aeropuertos = Funciones.generarAeropuertos(ubicaciones,
        // 4);
        ArrayList<Paquete> paquetes = Funciones.generarPaquetes(100, aeropuertos, 3, generatedInputPath);
        //ArrayList<PlanVuelo> planVuelos = Funciones.generarPlanesDeVuelo(aeropuertos, 1, generatedInputPath);
        ArrayList<PlanVuelo> planVuelos = new ArrayList<PlanVuelo>();
        // ArrayList<PlanRuta> rutasPorPaquete =
        // funciones.asignarVuelosAPaquetes(paquetes, vuelos);
        // Boolean funca = funciones.verificar_capacidad_aeropuertos(paquetes,
        // rutasPorPaquete, aeropuertos);

        if(true){
            return;
        }
    
        GrafoVuelos grafoVuelos = new GrafoVuelos(planVuelos, paquetes);

        List<PlanRuta> todasLasRutas = grafoVuelos.buscarTodasLasRutas();

        for(PlanRuta ruta : todasLasRutas){
            System.out.println(ruta.toString());
        }

        


        double temperature = 10000;
        double coolingRate = 0.003;
        int neighbourCount = 100;

        Solucion current = new Solucion(paquetes, new ArrayList<PlanRuta>(), aeropuertos, 0);
        current.initialize(todasLasRutas);
        printRutasTXT(current.paquetes, current.rutas, "initial.txt");

        while (temperature > 1) {
            // Pick a random neighboring solution
            ArrayList<Solucion> neighbours = new ArrayList<Solucion>();
            for (int i = 0; i < neighbourCount; i++) {
            neighbours.add(current.generateNeighbour(todasLasRutas));
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

            // Cool down the system
            temperature *= 1 - coolingRate;
        }

        // EstadoAlmacen estado = Funciones.obtenerEstadoAlmacen(current.paquetes, current.rutas, current.aeropuertos);
        // estado.consulta_historica();
        // System.out.println("VERIFICANDO CAPACIDAD");
        // HashMap<Aeropuerto, Integer> curr =
        // estado.verificar_capacidad_en(Date.from(LocalDateTime.of(2023, 1, 3, 15,0).toInstant(ZoneOffset.UTC)));
        // for (Aeropuerto aeropuerto : curr.keySet()) {
        //     System.out.println("Aeropuerto: " + aeropuerto.getId() + " | Capacidad: " +
        //     curr.get(aeropuerto));
        // }
        System.out.println("Final cost: " + current.costo);
        printRutasTXT(current.paquetes, current.rutas, "rutasFinal.txt");

    }
}