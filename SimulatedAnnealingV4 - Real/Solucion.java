import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import Clases.Aeropuerto;
import Clases.Duracion;
import Clases.EstadoAlmacen;
import Clases.Funciones;
import Clases.Paquete;
import Clases.PlanRuta;
import Clases.Vuelo;

public class Solucion {
    public ArrayList<Paquete> paquetes;
    public ArrayList<PlanRuta> rutas;
    public ArrayList<Aeropuerto> aeropuertos;

    public HashMap<Integer, Integer> ocupacionVuelos;
    public EstadoAlmacen estado;

    public double costo;
    public double badSolutionPenalization;
    HashMap<Integer, Vuelo> vuelos_hash;

    // TODO: El costo real deberia considerar cuantos almacenes esta ocupando. Si
    // TODO: ocupa un aeropuerto / vuelo concurrido el costo deberia ser mayor.

    public Solucion(
            ArrayList<Paquete> paquetes,
            ArrayList<PlanRuta> rutas,
            ArrayList<Aeropuerto> aeropuertos,
            HashMap<Integer, Integer> ocupacionVuelos,
            double costo,
            double badSolutionPenalization,
            HashMap<Integer, Vuelo> vuelos_hash
    ) {
        this.paquetes = paquetes;
        this.rutas = rutas;
        this.aeropuertos = aeropuertos;

        this.ocupacionVuelos = ocupacionVuelos;

        this.costo = costo;
        this.badSolutionPenalization = badSolutionPenalization;

        this.vuelos_hash = vuelos_hash;
    }

    public void printFlightOcupation(String filename){

        File csvFile = new File(filename);
        PrintWriter out;


        try {
            out = new PrintWriter(csvFile);

            for (HashMap.Entry<Integer, Integer> entry : ocupacionVuelos.entrySet()) {
                Vuelo vuelo = vuelos_hash.get(entry.getKey());
                Date fechaSalida = vuelo.getFecha_salida();
                Date fechaLlegada = vuelo.getFecha_llegada();
                String origen = vuelo.getPlan_vuelo().getCiudadOrigen().getId();   
                String destino = vuelo.getPlan_vuelo().getCiudadDestino().getId();
                out.println(
                    "Vuelo (" + entry.getKey() + ")  " + 
                    origen + "-" + destino + " " + 
                    Funciones.getFormattedDate(fechaSalida) + " - " + Funciones.getFormattedDate(fechaLlegada) + 
                    " -> " + entry.getValue() + " / " + vuelo.getPlan_vuelo().getCapacidad_maxima());
            }

            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //iterate over ocupacionVuelos
        
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
                cost += this.badSolutionPenalization;
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

    public boolean isCurrentRouteValid(Paquete paquete, PlanRuta planRuta) {
        String idRutaOrigen = planRuta.getVuelos().get(0).getPlan_vuelo().getCiudadOrigen().getId();
        String idRutaDestino = planRuta.getVuelos().get(planRuta.getVuelos().size() - 1)
                .getPlan_vuelo().getCiudadDestino().getId();

        String idPaqueteOrigen = paquete.getCiudadOrigen().getId();
        String idPaqueteDestino = paquete.getCiudadDestino().getId();

        Date horaSalidaRuta = planRuta.getVuelos().get(0).getFecha_salida();
        Date horaLlegadaRuta = planRuta.getVuelos().get(planRuta.getVuelos().size() - 1)
                .getFecha_llegada();

        Date horaRecepcionPaquete = paquete.getFecha_recepcion();
        Date horaMaximaEntregaPaquete = paquete.getFecha_maxima_entrega();

        if (!idRutaOrigen.equals(idPaqueteOrigen) ||
                !idRutaDestino.equals(idPaqueteDestino) ||
                !horaSalidaRuta.after(horaRecepcionPaquete) ||
                !horaLlegadaRuta.before(horaMaximaEntregaPaquete)) {
            return false;
        }

        return true;
    }

    public boolean isRouteFlightsCapacityAvailable(PlanRuta ruta) {
        for (int i = 0; i < ruta.getVuelos().size(); i++) {
            int maxCapacity = ruta.getVuelos().get(i).getPlan_vuelo().getCapacidad_maxima();

            int usedCapacity = 0;
            if (ocupacionVuelos.get(ruta.getVuelos().get(i).getId()) == null) {
                ocupacionVuelos.put(ruta.getVuelos().get(i).getId(), 0);
            } else {
                usedCapacity = ocupacionVuelos.get(ruta.getVuelos().get(i).getId());
            }

            if (usedCapacity > maxCapacity) {
                System.out.println("En la ruta de id " + ruta.getId()
                        + " se excedio la capacidad maxima, buscar error =================================");
                return false;
            }
            if (usedCapacity == maxCapacity) {
                return false;
            }
        }

        return true;
    }

    public void ocupyRouteFlights(PlanRuta ruta) {
        for (int i = 0; i < ruta.getVuelos().size(); i++) {
            
            int idVuelo = ruta.getVuelos().get(i).getId();

            if(ocupacionVuelos.get(idVuelo) == null){
                ocupacionVuelos.put(idVuelo, 0);
            } else {
                ocupacionVuelos.put(idVuelo, ocupacionVuelos.get(idVuelo) + 1);
            }
            
        }
    }

    public void deocupyRouteFlights(PlanRuta ruta) {
        for (int i = 0; i < ruta.getVuelos().size(); i++) {
            int idVuelo = ruta.getVuelos().get(i).getId();
            if(ocupacionVuelos.get(idVuelo) == 0){
                System.out.println("Se intento desocupar un vuelo que no estaba ocupado");
                throw new Error("Se intento desocupar un vuelo que no estaba ocupado");
            }
            ocupacionVuelos.put(idVuelo, ocupacionVuelos.get(idVuelo) - 1);
        }
    }

    public boolean isAirportCapacityAvailable(){
        // if(true)return true;
        this.estado = new EstadoAlmacen( paquetes, rutas,
        vuelos_hash, ocupacionVuelos, aeropuertos);
        if(estado.verificar_capacidad_maxima() == true){
            System.out.println("Se excedio la capacidad maxima de los aeropuertos");
            throw new Error("Se excedio la capacidad maxima de los aeropuertos");
        }
        return false;
    }

    public void initialize(HashMap<String, ArrayList<PlanRuta>> todasLasRutas) {

        for (int i = 0; i < paquetes.size(); i++) {
            String origenPaquete = paquetes.get(i).getCiudadOrigen().getId();
            String destinoPaquete = paquetes.get(i).getCiudadDestino().getId();
            String keyString = origenPaquete + "-" + destinoPaquete;

            int randomRouteIndex = (int) (Math.random() * todasLasRutas.get(keyString).size());

            PlanRuta randomRoute = todasLasRutas.get(keyString).get(randomRouteIndex);

            this.rutas.add(randomRoute);
            this.ocupyRouteFlights(randomRoute);
        }

        this.costo = getSolutionCost();
    }

    public Solucion generateNeighbour(HashMap<String, ArrayList<PlanRuta>> todasLasRutas, int windowSize,
            boolean randomizeNeighboors) {

        Solucion neighbour = new Solucion(
                new ArrayList<>(this.paquetes),
                new ArrayList<>(this.rutas), // me asegurara de no alterar los vuelos de otras rutas? deberia hacer una
                                             // copia de cada vuelo para asegurarlo
                new ArrayList<>(this.aeropuertos),
                new HashMap<Integer, Integer>(this.ocupacionVuelos),
                this.costo,
                this.badSolutionPenalization,
                this.vuelos_hash
                
                );


        HashMap<Integer, Boolean> indexes = new HashMap<Integer, Boolean>();
        int[] randomPackageIndexes = new int[windowSize];
        for (int i = 0; i < windowSize; i++) {
            int randomIndex = (int) (Math.random() * this.paquetes.size());
            while(indexes.get(randomIndex) != null){
                randomIndex = (int) (Math.random() * this.paquetes.size());
            }
            randomPackageIndexes[i] = (int) (Math.random() * this.paquetes.size());
            indexes.put(randomPackageIndexes[i], true);
        }

        ArrayList<Paquete> randomPackages = new ArrayList<Paquete>();
        ArrayList<ArrayList<PlanRuta>> availableRoutesPerPackage = new ArrayList<ArrayList<PlanRuta>>();

        for (int i = 0; i < windowSize; i++) {
            PlanRuta oldRoute = rutas.get(randomPackageIndexes[i]);
            neighbour.deocupyRouteFlights(oldRoute);

            randomPackages.add(neighbour.paquetes.get(randomPackageIndexes[i]));
            availableRoutesPerPackage.add(new ArrayList<PlanRuta>());
        }

        double newCost = this.costo;

        for (int j = 0; j < windowSize; j++) {
            String origenPaquete = randomPackages.get(j).getCiudadOrigen().getId();
            String destinoPaquete = randomPackages.get(j).getCiudadDestino().getId();
            String keyString = origenPaquete + "-" + destinoPaquete;

            if (randomizeNeighboors) {
                while (true) {
                    int randomRouteIndex = (int) (Math.random() * todasLasRutas.get(keyString).size());
                    PlanRuta randomRoute = todasLasRutas.get(keyString).get(randomRouteIndex);
                    //check if origin and destiny is different
                    if(
                        neighbour.isCurrentRouteValid(randomPackages.get(j), randomRoute) == true  
                        && neighbour.isRouteFlightsCapacityAvailable(randomRoute) == true
                        //&& neighbour.isAirportCapacityAvailable() == true
                    ){
                        neighbour.ocupyRouteFlights(randomRoute);
                        if (neighbour.isCurrentRouteValid(randomPackageIndexes[j]) == false) { // su solucion no era
                                                                                               // valida
                            newCost -= neighbour.badSolutionPenalization;
                        }
                        neighbour.rutas.set(randomPackageIndexes[j], randomRoute);
                        break;
                    }
                }
            } else {
                // TODO: A esta opcion no se le ha agregado la ocupacion de vuelos y almacenes
                for (PlanRuta ruta : todasLasRutas.get(keyString)) {
                    if (isCurrentRouteValid(randomPackages.get(j), ruta) == true) {
                        availableRoutesPerPackage.get(j).add(ruta);
                    }
                }

                if (availableRoutesPerPackage.get(j).size() == 0
                        && isCurrentRouteValid(randomPackageIndexes[j]) == true) {
                    newCost += neighbour.badSolutionPenalization;
                } else if (availableRoutesPerPackage.get(j).size() > 0
                        && isCurrentRouteValid(randomPackageIndexes[j]) == false) {
                    newCost -= neighbour.badSolutionPenalization;
                }

                int randomRouteIndex = (int) (Math.random() * availableRoutesPerPackage.get(j).size());
                PlanRuta randomRoute = new PlanRuta(availableRoutesPerPackage.get(j).get(randomRouteIndex));
                neighbour.rutas.set(randomPackageIndexes[j], randomRoute);
            }
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