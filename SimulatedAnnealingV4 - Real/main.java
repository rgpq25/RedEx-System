import Clases.Aeropuerto;
import Clases.GrafoVuelos;
import Clases.Paquete;
import Clases.PlanRuta;
import Clases.Ubicacion;
import Clases.Vuelo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class main {

    public static class Solucion {
        public ArrayList<Paquete> paquetes;
        public ArrayList<PlanRuta> rutas;
        public double costo;
        //TODO: El costo real deberia considerar cuantos almacenes esta ocupando. Si ocupa un aeropuerto / vuelo concurrido el costo deberia ser mayor.

        public Solucion(ArrayList<Paquete> paquetes, ArrayList<PlanRuta> rutas, double costo) {
            this.paquetes = paquetes;
            this.rutas = rutas;
            this.costo = costo;
        }

        public void initialize(List<PlanRuta> todasLasRutas){
            double costo = 0;

            for (int i = 0; i < paquetes.size(); i++) {
                int randomRouteIndex = (int) (Math.random() * todasLasRutas.size());
                PlanRuta randomRoute = todasLasRutas.get(randomRouteIndex);
                this.rutas.add(randomRoute);
                costo += randomRoute.getVuelos().size();
            }

            this.costo = costo;
        }

        public Solucion generateNeighbour(List<PlanRuta> todasLasRutas){

            Solucion neighbour = new Solucion(new ArrayList<>(this.paquetes), new ArrayList<>(this.rutas), costo);
            int randomPackageIndex = (int) (Math.random() * this.paquetes.size());
            Paquete randomPackage = neighbour.paquetes.get(randomPackageIndex);
            ArrayList<PlanRuta> availableRoutes = new ArrayList<>();
            
            for (PlanRuta ruta : todasLasRutas) {
                if (ruta.getVuelos().get(0).getPlan_vuelo().getCiudadOrigen().getId().equals(randomPackage.getCiudadOrigen().getId()) &&
                    ruta.getVuelos().get(ruta.getVuelos().size() - 1).getPlan_vuelo().getCiudadDestino().getId().equals(randomPackage.getCiudadDestino().getId()) &&
                    ruta.getVuelos().get(0).getFecha_salida().after(randomPackage.getFecha_recepcion()) &&
                    ruta.getVuelos().get(ruta.getVuelos().size() -1).getFecha_llegada().before(randomPackage.getFecha_maxima_entrega())) {
                        availableRoutes.add(ruta);
                }
            }

            if (availableRoutes.size() == 0) {
                System.out.println("No available routes for package (" + randomPackage.getId() + ")");
                return neighbour;
            }

            int randomRouteIndex = (int) (Math.random() * availableRoutes.size());
            PlanRuta randomRoute = availableRoutes.get(randomRouteIndex);
            neighbour.rutas.set(randomPackageIndex, randomRoute);
        
            double newCost = 0;
            for(int i = 0; i < neighbour.paquetes.size(); i++){
                newCost += neighbour.rutas.get(i).getVuelos().size();
            }
            neighbour.costo = newCost;

            return neighbour;
        }

        public void printTxt(){
            for (int i = 0; i < paquetes.size(); i++) {
                System.out.println("Paquete " + paquetes.get(i).getId() + " -> " + rutas.get(i).toString());
            }
        }
    }


    public static void printRutasTXT(ArrayList<Paquete> paquetes, ArrayList<PlanRuta> rutas, String filename){
        File csvFile = new File(filename);
        PrintWriter out;

        try {
            out = new PrintWriter(csvFile);

            for(int i=0; i<paquetes.size(); i++){

                String origen_paquete = paquetes.get(i).getCiudadOrigen().getId();
                String destino_paquete = paquetes.get(i).getCiudadDestino().getId();
                Date fecha_recepcion = paquetes.get(i).getFecha_recepcion();
                out.println("Paquete " + i + " - " + origen_paquete + "-" + destino_paquete + "  |  " + fecha_recepcion);
                for(int j=0; j<rutas.get(i).length(); j++){
                        String id_origen = rutas.get(i).getVuelos().get(j).getPlan_vuelo().getCiudadOrigen().getId();
                        String id_destino = rutas.get(i).getVuelos().get(j).getPlan_vuelo().getCiudadDestino().getId();
                        Date fecha_salida = rutas.get(i).getVuelos().get(j).getFecha_salida();
                        Date fecha_llegada = rutas.get(i).getVuelos().get(j).getFecha_llegada();
                    
                        out.println("         " + id_origen + "-" + id_destino + " " + fecha_salida + "-" + fecha_llegada);
                }
                out.println();
            }

            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Funciones funciones = new Funciones();

        String inputPath = "input";
        Aeropuerto[] aeropuertos = funciones.leerAeropuertos(inputPath);

        HashMap<String, Ubicacion> ubicacionMap = new HashMap<>();
        for(Aeropuerto aeropuerto : aeropuertos){
            ubicacionMap.put(aeropuerto.getUbicacion().getId(), aeropuerto.getUbicacion());
        }

        Paquete[] paquetes = funciones.leerPaquetes(inputPath, ubicacionMap);
        Vuelo[] vuelos = funciones.leerVuelos(inputPath, ubicacionMap);

        // for(Aeropuerto aeropuerto: aeropuertos){
        //     aeropuerto.print();
        // }
        for(Paquete paquete: paquetes){
            paquete.print();
        }
        // for(Vuelo vuelo: vuelos){
        //     vuelo.print();
        // }

        // funciones.ordenarPaquetes(paquetes);
        // funciones.ordenarVuelos(vuelos);
        // GrafoVuelos grafoVuelos = new GrafoVuelos();
        // for (Vuelo vuelo : vuelos) {
        //     grafoVuelos.agregarVuelo(vuelo);
        // }
        // Date date = Date.from(LocalDateTime.of(2023, 1, 2, 0, 0).toInstant(ZoneOffset.UTC));

        // List<PlanRuta> todasLasRutas = grafoVuelos.buscarTodasLasRutas(date);

        
        // String fechaActual = "2023-01-02 00:00:00";

        // double temperature = 10000;
        // double coolingRate = 0.003;
        // int neighbourCount = 10;  


        // Solucion current = new Solucion(new ArrayList<Paquete>(Arrays.asList(paquetes)), new ArrayList<PlanRuta>(), 0);
        // current.initialize(todasLasRutas);
        // printRutasTXT( current.paquetes, current.rutas, "initial.txt");  
        
        // while (temperature > 1) {
        //     // Pick a random neighboring solution
        //     ArrayList<Solucion> neighbours = new ArrayList<Solucion>();
        //     for(int i = 0; i < neighbourCount; i++){
        //         neighbours.add(current.generateNeighbour(todasLasRutas));
        //     }

        //     int bestNeighbourIndex = 0;
        //     double bestNeighbourCost = Double.MAX_VALUE;
        //     for (int i = 0; i < neighbours.size(); i++) {
        //         Solucion neighbour = neighbours.get(i);
        //         double neighbourCost = neighbour.costo;
        //         if (neighbourCost < bestNeighbourCost) {
        //             bestNeighbourCost = neighbourCost;
        //             bestNeighbourIndex = i;
        //         }
        //     }

        //     // Calculate the cost difference between the new and current routes
        //     double currentCost = current.costo;
        //     double newCost = neighbours.get(bestNeighbourIndex).costo;
        //     double costDifference = newCost - currentCost;


        //     // Decide if we should accept the new solution
        //     if (costDifference < 0 || Math.exp(-costDifference / temperature) > Math.random()) {
        //         current = neighbours.get(bestNeighbourIndex);
        //     }

        //     // Cool down the system
        //     temperature *= 1 - coolingRate;
        // }
        
        // System.out.println("Final cost: " + current.costo);
        // printRutasTXT( current.paquetes, current.rutas, "rutasFinal.txt");  
    }
}