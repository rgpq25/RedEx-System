import Clases.Aeropuerto;
import Clases.GrafoVuelos;
import Clases.Paquete;
import Clases.PlanRuta;
import Clases.Vuelo;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
                if (ruta.getVuelos().get(0).getPlan_vuelo().getId_ubicacion_origen().equals(randomPackage.getId_ciudad_origen()) &&
                    ruta.getVuelos().get(ruta.getVuelos().size() - 1).getPlan_vuelo().getId_ubicacion_destino().equals(randomPackage.getId_ciudad_destino())) {
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

    public static void main(String[] args) {
        Funciones funciones = new Funciones();

        String inputPath = "input";
        //Aeropuerto[] aeropuertos = funciones.leerAeropuertos(inputPath);
        Paquete[] paquetes = funciones.leerPaquetes(inputPath);
        Vuelo[] vuelos = funciones.leerVuelos(inputPath);

        funciones.ordenarPaquetes(paquetes);
        funciones.ordenarVuelos(vuelos);
        GrafoVuelos grafoVuelos = new GrafoVuelos();
        for (Vuelo vuelo : vuelos) {
            grafoVuelos.agregarVuelo(vuelo);
        }
        Date date = Date.from(LocalDateTime.of(2023, 1, 2, 0, 0).toInstant(ZoneOffset.UTC));

        List<PlanRuta> todasLasRutas = grafoVuelos.buscarTodasLasRutas(date);
        for (PlanRuta ruta : todasLasRutas) {
            System.out.println(ruta.toString());
        }

        


        double temperature = 10000;
        double coolingRate = 0.003;
        int neighbourCount = 10;    //TODO: Verificar que creacion de neighboors sea correcta en la memoria, que cada posicion del arreglo sea diferente con DEBUG

        //current.printRutasTXT("initial.txt");
        Solucion current = new Solucion(new ArrayList<Paquete>(Arrays.asList(paquetes)), new ArrayList<PlanRuta>(), 0);
        current.initialize(todasLasRutas);
        
        while (temperature > 1) {
            // Pick a random neighboring solution
            ArrayList<Solucion> neighbours = new ArrayList<Solucion>();
            for(int i = 0; i < neighbourCount; i++){
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
        
        System.out.println("Final cost: " + current.costo);
        current.printTxt();
    }
}