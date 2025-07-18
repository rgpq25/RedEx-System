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
import Clases.GrafoVuelos;
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
    public double flightPenalization;
    public double airportPenalization;

    public double badSolutionCost;
    public double flightCost;
    public double airportCost;

    public double STPaquetes;
    public double STVuelos;
    public double PPTAeropuerto;


    public double costoDePaquetesYRutasErroneas;

    public GrafoVuelos grafoVuelos;


    HashMap<Integer, Vuelo> vuelos_hash;

    public Solucion(
            ArrayList<Paquete> paquetes,
            ArrayList<PlanRuta> rutas,
            ArrayList<Aeropuerto> aeropuertos,
            HashMap<Integer, Integer> ocupacionVuelos,
            double costo,
            double badSolutionPenalization,
            double flightPenalization,
            double airportPenalization,
            HashMap<Integer, Vuelo> vuelos_hash,
            GrafoVuelos grafoVuelos
    ) {
        this.paquetes = paquetes;
        this.rutas = rutas;
        this.aeropuertos = aeropuertos;

        this.ocupacionVuelos = ocupacionVuelos;

        this.costo = costo;
        this.badSolutionPenalization = badSolutionPenalization;
        this.flightPenalization = flightPenalization;
        this.airportPenalization = airportPenalization;

        this.vuelos_hash = vuelos_hash;

        this.costoDePaquetesYRutasErroneas = 0;

        this.grafoVuelos = grafoVuelos;
    }

    public void force_initialize(HashMap<String, ArrayList<PlanRuta>> todasLasRutas) {
        //We do 5 attempts to try to initialize the solution
        for(int j = 0; j < 20; j++){
            System.out.println("Intento " + (j + 1) + " de inicializacion");
            //ArrayList<PlanRuta> av_rutas = grafoVuelos.generarRutasParaPaquetes(this.paquetes);

            for (int i = 0; i < paquetes.size(); i++) {
                
                int contadorDeIntentos = 0;
                while(true){
                    ArrayList<Paquete> tempPaquetesArray = new ArrayList<Paquete>();
                    tempPaquetesArray.add(paquetes.get(i));
                    ArrayList<PlanRuta> tempRoutesArray = grafoVuelos.generarRutasParaPaquetes(tempPaquetesArray);
                    PlanRuta randomRoute = tempRoutesArray.get(0);

                    if(this.isCurrentRouteValid(paquetes.get(i), randomRoute) == true && this.isRouteFlightsCapacityAvailable(randomRoute) == true){
                        this.rutas.add(randomRoute);
                        this.ocupyRouteFlights(randomRoute);
                        break;
                    }

                    contadorDeIntentos++;
                    if(contadorDeIntentos >= 5){
                        this.rutas.add(randomRoute);
                        this.ocupyRouteFlights(randomRoute);
                        break;
                    }
                }
                
            }
            // if(this.isAirportCapacityAvailable() == true){
                return;
            // } else {
            //     this.rutas.clear();
            //     this.ocupacionVuelos.clear();
            // }
        }

        throw new Error("Se excedio la capacidad maxima de los aeropuertos en inicializacion");
        
    }

    public void initialize(HashMap<String, ArrayList<PlanRuta>> todasLasRutas) {
        //We do 5 attempts to try to initialize the solution
        for(int j = 0; j < 20; j++){
            System.out.println("Intento " + (j + 1) + " de inicializacion");
            //ArrayList<PlanRuta> av_rutas = grafoVuelos.generarRutasParaPaquetes(this.paquetes);

            for (int i = 0; i < paquetes.size(); i++) {
                ArrayList<Paquete> tempPaquetesArray = new ArrayList<Paquete>();
                tempPaquetesArray.add(paquetes.get(i));
                ArrayList<PlanRuta> tempRoutesArray = grafoVuelos.generarRutasParaPaquetes(tempPaquetesArray);
                PlanRuta randomRoute = tempRoutesArray.get(0);

                // this.rutas.add(av_rutas.get(i));
                // this.ocupyRouteFlights(av_rutas.get(i));

                this.rutas.add(randomRoute);
                this.ocupyRouteFlights(randomRoute);

                // if(i % 100 == 0){
                //     System.out.println("Se inicializaron " + (i+1) + " paquetes.");
                // }
            }
            if(this.isAirportCapacityAvailable() == true){
                return;
            } else {
                this.rutas.clear();
                this.ocupacionVuelos.clear();
            }
        }

        throw new Error("Se excedio la capacidad maxima de los aeropuertos en inicializacion");
        
    }



    public Solucion generateNeighbour(int windowSize) {

        Solucion neighbour = new Solucion(
            new ArrayList<>(this.paquetes),
            new ArrayList<>(this.rutas),
            this.aeropuertos,
            new HashMap<Integer, Integer>(this.ocupacionVuelos),
            this.costo,
            this.badSolutionPenalization,
            this.flightPenalization,
            this.airportPenalization,
            this.vuelos_hash,
            this.grafoVuelos
        );


        HashMap<Integer, Boolean> indexes = new HashMap<Integer, Boolean>();
        ArrayList<Paquete> randomPackages = new ArrayList<Paquete>();
        int[] randomPackageIndexes = new int[windowSize];
        for (int i = 0; i < windowSize; i++) {
            int randomIndex = (int) (Math.random() * this.paquetes.size());
            while(indexes.get(randomIndex) != null){
                randomIndex = (int) (Math.random() * this.paquetes.size());
            }
            randomPackageIndexes[i] = randomIndex;
            indexes.put(randomPackageIndexes[i], true);

            PlanRuta oldRoute = rutas.get(randomPackageIndexes[i]);
            neighbour.deocupyRouteFlights(oldRoute);
            randomPackages.add(neighbour.paquetes.get(randomPackageIndexes[i]));
        }


        //generate new routes for the selected packages
        for (int j = 0; j < windowSize; j++) {
            int conteo=0;
            while (true) {
                ArrayList<Paquete> tempPaquetesArray = new ArrayList<Paquete>();
                tempPaquetesArray.add(randomPackages.get(j));

                ArrayList<PlanRuta> tempRoutesArray = grafoVuelos.generarRutasParaPaquetes(tempPaquetesArray);
                PlanRuta randomRoute = tempRoutesArray.get(0);

                //check if origin and destiny is different
                if(
                    neighbour.isCurrentRouteValid(randomPackages.get(j), randomRoute) == true  
                    && neighbour.isRouteFlightsCapacityAvailable(randomRoute) == true
                ){
                    neighbour.ocupyRouteFlights(randomRoute);
                    neighbour.rutas.set(randomPackageIndexes[j], randomRoute);
                    break;
                }

                if(conteo>=50){ //antes era 1000
                    return this;
                }
            }
        }

        //tambien deberiamos medir si esto llega a repetirse X veces simplemente devolver la solucion actual
        // if(neighbour.isAirportCapacityAvailable() == false){    //We generate again if the airport capacity is exceeded
        //     return generateNeighbour(todasLasRutas, windowSize);
        // } else {
            return neighbour;
        // }
        
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

    public void printAirportHistoricOcupation(String filename){
        this.estado = new EstadoAlmacen(this.paquetes, this.rutas, this.vuelos_hash, this.ocupacionVuelos, this.aeropuertos);
        estado.consulta_historicaTxt(filename);
    }

    public double getSTPaquetes(){
        double sum = 0;
        for(int i = 0; i < paquetes.size(); i++){
            double tiempoRecepcion = paquetes.get(i).getFecha_recepcion().getTime();
            double tiempoLlegadaRuta = rutas.get(i).getVuelos().get(rutas.get(i).getVuelos().size() - 1).getFecha_llegada().getTime();
            double diferenciaFechaMaxima = paquetes.get(i).getFecha_maxima_entrega().getTime() - tiempoRecepcion;
            double diferenciaFechaEntrega = tiempoLlegadaRuta - tiempoRecepcion;
            double porcentajeTiempo = (diferenciaFechaEntrega)/diferenciaFechaMaxima;
            sum += Math.max(0, porcentajeTiempo);
        }
        return sum;
    }

    public double getSTVuelos(){
        double sum = 0;
        for (HashMap.Entry<Integer, Integer> entry : ocupacionVuelos.entrySet()) {
            Vuelo vuelo = vuelos_hash.get(entry.getKey());
            int maxCapacity = vuelo.getPlan_vuelo().getCapacidad_maxima();
            int usedCapacity = entry.getValue();
            
            if(usedCapacity >= maxCapacity){
                sum += 100000;
            } else {
                sum += (double)usedCapacity / (double)maxCapacity;
            }
        }
        return sum;
    }

    public double getPPTAeropuerto(){
        this.estado = new EstadoAlmacen(this.paquetes, this.rutas, this.vuelos_hash, this.ocupacionVuelos, this.aeropuertos);
        return this.estado.calcularCostoTotalAlmacenamiento();
    }

    public double[] getCostoPaquete(int i){
        double currentCost = 0;
        double conteoSinSentido = 0;

        Date horaSalidaRuta = this.rutas.get(i).getVuelos().get(0).getFecha_salida();
        Date horaLlegadaRuta = this.rutas.get(i).getVuelos().get(this.rutas.get(i).getVuelos().size() - 1).getFecha_llegada();

        Date horaRecepcionPaquete = this.paquetes.get(i).getFecha_recepcion();
        Date horaMaximaEntregaPaquete = this.paquetes.get(i).getFecha_maxima_entrega();

        int cantVuelosDePaquete = this.rutas.get(i).getVuelos().size();


        currentCost += (double)(horaLlegadaRuta.getTime() - horaRecepcionPaquete.getTime()) / (horaMaximaEntregaPaquete.getTime() - horaRecepcionPaquete.getTime());

        if (horaSalidaRuta.after(horaRecepcionPaquete) == false) { //inusable
            currentCost = 100000;
        }

        if (horaLlegadaRuta.after(horaMaximaEntregaPaquete) == true) { //para colapso es usable
            currentCost = 100;
        }

        if(horaSalidaRuta.after(horaRecepcionPaquete) == false || horaLlegadaRuta.after(horaMaximaEntregaPaquete) == true){
            conteoSinSentido++;
        }

        return new double[]{currentCost, conteoSinSentido, cantVuelosDePaquete};
    }


    public double getSolutionCost() {
        double cost = 0;
        double conteoSinSentido = 0;
        double mediaVuelos = 0;

        for (int i = 0; i < this.paquetes.size(); i++) {
            double[] costAndConteo = getCostoPaquete(i);
            cost += costAndConteo[0];
            conteoSinSentido += costAndConteo[1];
            mediaVuelos += costAndConteo[2];
        }
        mediaVuelos = mediaVuelos / this.paquetes.size();

        this.costoDePaquetesYRutasErroneas = conteoSinSentido;
        cost = cost * 10;

        double costoVuelos = this.getSTVuelos();
        double costoAeropuertos = this.getPPTAeropuerto();

        cost += costoVuelos * 4;
        cost += costoAeropuertos * 4;
        cost += mediaVuelos * 6;

        return cost;
    }

    public void printCosts(){
        double cost = 0;
        double conteoSinSentido = 0;
        double mediaVuelos = 0;
        for (int i = 0; i < this.paquetes.size(); i++) {
            double[] costAndConteo = getCostoPaquete(i);
            cost += costAndConteo[0];   //costo de paquetes y asignacion de rutas
            conteoSinSentido += costAndConteo[1];
            mediaVuelos += costAndConteo[2];
        }
        mediaVuelos = mediaVuelos / this.paquetes.size();

        double costoVuelos = this.getSTVuelos();
        double costoAeropuertos = this.getPPTAeropuerto();
        System.out.println(" -> Costo de paquetes y su asignacion de rutas: " + (cost));
        System.out.println(" -> Costo de cantidad de vuelos por ruta: " + (mediaVuelos));
        System.out.println(" -> Costo de vuelos: " + (costoVuelos));
        System.out.println(" -> Costo de aeropuertos: " + (costoAeropuertos));
        System.out.println(" -> Costo total: " + ((cost * 10) + (costoVuelos * 4) + (costoAeropuertos * 4) + (mediaVuelos * 6)));
    }

    public void printCostsInLog(){
        double cost = 0;
        double conteoSinSentido = 0;
        double mediaVuelos = 0;
        for (int i = 0; i < this.paquetes.size(); i++) {
            double[] costAndConteo = getCostoPaquete(i);
            cost += costAndConteo[0];   //costo de paquetes y asignacion de rutas
            conteoSinSentido += costAndConteo[1];
            mediaVuelos += costAndConteo[2];
        }
        mediaVuelos = mediaVuelos / this.paquetes.size();

        double costoVuelos = this.getSTVuelos();
        double costoAeropuertos = this.getPPTAeropuerto();
        System.out.println(" -> Costo de paquetes y su asignacion de rutas: " + (cost));
        Funciones.printLineInLog(" -> Costo de paquetes y su asignacion de rutas: " + (cost));
        System.out.println(" -> Costo de cantidad de vuelos por ruta: " + (mediaVuelos));
        Funciones.printLineInLog(" -> Costo de cantidad de vuelos por ruta: " + (mediaVuelos));
        System.out.println(" -> Costo de vuelos: " + (costoVuelos));
        Funciones.printLineInLog(" -> Costo de vuelos: " + (costoVuelos));
        System.out.println(" -> Costo de aeropuertos: " + (costoAeropuertos));
        Funciones.printLineInLog(" -> Costo de aeropuertos: " + (costoAeropuertos));
        System.out.println(" -> Costo total: " + ((cost * 10) + (costoVuelos * 4) + (costoAeropuertos * 4) + (mediaVuelos * 6)));
        Funciones.printLineInLog(" -> Costo total: " + ((cost * 10) + (costoVuelos * 4) + (costoAeropuertos * 4) + (mediaVuelos * 6)));
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
                usedCapacity = 0;
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
                ocupacionVuelos.put(idVuelo, 1);
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
        this.estado = new EstadoAlmacen( paquetes, rutas, vuelos_hash, ocupacionVuelos, aeropuertos);
        return estado.verificar_capacidad_maxima();
    }


    

    
    public void printLogCouldntFindRoute(Paquete paquete, String filename){
        File csvFile = new File(filename);
        PrintWriter out;

        try {
            out = new PrintWriter(csvFile);
            out.println("No se pudo encontrar una ruta valida despues de 100 intentos");
            out.println("Paquete: " + paquete.getId());
            out.println("Origen: " + paquete.getCiudadOrigen().getId());
            out.println("Destino: " + paquete.getCiudadDestino().getId());
            out.println("Fecha Recepcion: " + Funciones.getFormattedDate(paquete.getFecha_recepcion()));
            out.println("Fecha Maxima Entrega: " + Funciones.getFormattedDate(paquete.getFecha_maxima_entrega()));
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}