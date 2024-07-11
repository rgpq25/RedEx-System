package pucp.e3c.redex_back.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pucp.e3c.redex_back.service.VueloService;

public class Solucion {
    public ArrayList<Paquete> paquetes;
    public ArrayList<PlanRutaNT> rutas;
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

    private double sumaPaquetesWeight;
    private double sumaVuelosWeight;
    private double promedioPonderadoTiempoAeropuertoWeight;
    private double mediaVuelosWight;

    public double STPaquetes;
    public double STVuelos;
    public double PPTAeropuerto;

    public double costoDePaquetesYRutasErroneas;

    public GrafoVuelos grafoVuelos;
    public ArrayList<Boolean> rutasValidas;
    HashMap<Integer, Vuelo> vuelos_hash;

    private static final Logger LOGGER = LoggerFactory.getLogger(Solucion.class);

    public Solucion(
            ArrayList<Paquete> paquetes,
            ArrayList<PlanRutaNT> rutas,
            ArrayList<Aeropuerto> aeropuertos,
            HashMap<Integer, Integer> ocupacionVuelos,
            double costo,
            double badSolutionPenalization,
            double flightPenalization,
            double airportPenalization,
            double sumaPaquetesWeight,
            double sumaVuelosWeight,
            double promedioPonderadoTiempoAeropuertoWeight,
            double mediaVuelosWight,
            HashMap<Integer, Vuelo> vuelos_hash,
            GrafoVuelos grafoVuelos) {
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
        this.sumaPaquetesWeight = sumaPaquetesWeight;
        this.sumaVuelosWeight = sumaVuelosWeight;
        this.promedioPonderadoTiempoAeropuertoWeight = promedioPonderadoTiempoAeropuertoWeight;
        this.mediaVuelosWight = mediaVuelosWight;

    }

    public boolean force_initialize(HashMap<String, ArrayList<PlanRutaNT>> todasLasRutas, VueloService vueloService,
            Date tiempoEnSimulacion, int TA) {
        // We do 5 attempts to try to initialize the solution
        ArrayList<PlanRutaNT> copiedRutas = new ArrayList<PlanRutaNT>();
        this.rutasValidas = new ArrayList<>();
        for (int i = 0; i < this.paquetes.size(); i++) {
            this.rutasValidas.add(false);
        }
        for (int idx = 0; idx < this.paquetes.size(); idx++) {
            if (this.rutas.size() == 0) {
                copiedRutas.add(new PlanRutaNT());
            } else {

                copiedRutas.add(this.rutas.get(idx));

            }
        }
        this.rutas.clear();

        for (int j = 0; j < 20; j++) {
            System.out.println("Intento " + (j + 1) + " de inicializacion");

            for (int i = 0; i < paquetes.size(); i++) {

                int contadorDeIntentos = 0;
                while (true) {
                    ArrayList<Paquete> tempPaquetesArray = new ArrayList<Paquete>();
                    tempPaquetesArray.add(paquetes.get(i));
                    ArrayList<PlanRutaNT> tempRoutesArray = grafoVuelos.generarRutasParaPaquetes(tempPaquetesArray,
                            vueloService, tiempoEnSimulacion, TA);
                    if (tempPaquetesArray == null) {
                        return false;
                    }
                    PlanRutaNT randomRoute = tempRoutesArray.get(0);

                    if (copiedRutas.get(i).getVuelos().size() > 0) {
                        this.rutas.add(copiedRutas.get(i));
                        break;
                    } else if (this.isCurrentRouteValid(paquetes.get(i), randomRoute) == true
                            && this.isRouteFlightsCapacityAvailable(randomRoute) == true) {
                        this.rutas.add(randomRoute);
                        this.ocupyRouteFlights(randomRoute);
                        break;
                    }

                    contadorDeIntentos++;
                    if (contadorDeIntentos >= 5) {
                        this.rutas.add(randomRoute);
                        this.ocupyRouteFlights(randomRoute);
                        break;
                    }
                }

            }
            for (int i = 0; i < this.paquetes.size(); i++) {
                double[] costAndConteo = getCostoPaquete(i);
                if (costAndConteo[1] > 0.0) {
                    this.rutasValidas.set(i, false);
                } else {
                    this.rutasValidas.set(i, true);

                }
            }
            return true;
            // } else {
            // this.rutas.clear();
            // t

        }

        throw new Error("Se excedio la capacidad maxima de los aeropuertos en inicializacion");

    }

    public void initialize(HashMap<String, ArrayList<PlanRutaNT>> todasLasRutas, VueloService vueloService, int TA) {
        // We do 5 attempts to try to initialize the solution
        for (int j = 0; j < 20; j++) {
            System.out.println("Intento " + (j + 1) + " de inicializacion");
            // ArrayList<PlanRutaNT> av_rutas =
            // grafoVuelos.generarRutasParaPaquetes(this.paquetes);

            for (int i = 0; i < paquetes.size(); i++) {
                ArrayList<Paquete> tempPaquetesArray = new ArrayList<Paquete>();
                tempPaquetesArray.add(paquetes.get(i));
                ArrayList<PlanRutaNT> tempRoutesArray = grafoVuelos.generarRutasParaPaquetes(tempPaquetesArray,
                        vueloService, null, TA);
                PlanRutaNT randomRoute = tempRoutesArray.get(0);

                // this.rutas.add(av_rutas.get(i));
                // this.ocupyRouteFlights(av_rutas.get(i));

                this.rutas.add(randomRoute);
                this.ocupyRouteFlights(randomRoute);

                // if(i % 100 == 0){
                // System.out.println("Se inicializaron " + (i+1) + " paquetes.");
                // }
            }
            if (this.isAirportCapacityAvailable() == true) {
                return;
            } else {
                this.rutas.clear();
                this.ocupacionVuelos.clear();
            }
        }

        throw new Error("Se excedio la capacidad maxima de los aeropuertos en inicializacion");

    }

    private int generatePseudoRandomIndex(int size, double prob,ArrayList<Integer> invalidIndexes ) {
        if (Math.random() < prob) {
            // Choose an index where rutasValidas is false
            if (invalidIndexes.size() > 0) {
                return invalidIndexes.get((int) (Math.random() * invalidIndexes.size()));
            } else {
                return (int) (Math.random() * this.paquetes.size());
            }
        } else {
            // Choose any random index
            return (int) (Math.random() * this.paquetes.size());
        }
    }

    /*private int generatePseudoRandomIndex(int size, double prob, ArrayList<Integer> invalidIndexes) {
        if (size == 0) {
            throw new IllegalArgumentException("Size cannot be zero");
        }
        
        if (ThreadLocalRandom.current().nextDouble() < prob) {
            // Choose an index from invalidIndexes
            if (!invalidIndexes.isEmpty()) {
                return invalidIndexes.get(ThreadLocalRandom.current().nextInt(invalidIndexes.size()));
            } else {
                return ThreadLocalRandom.current().nextInt(size);
            }
        } else {
            // Choose any random index
            return ThreadLocalRandom.current().nextInt(size);
        }
    }*/

    public Solucion generateNeighbour(int windowSize, VueloService vueloService, Date tiempoEnSimulacion, int TA, double probIndexInvParametro) {

        Solucion neighbour = new Solucion(
                new ArrayList<>(this.paquetes),
                new ArrayList<>(this.rutas),
                this.aeropuertos,
                new HashMap<Integer, Integer>(this.ocupacionVuelos),
                this.costo,
                this.badSolutionPenalization,
                this.flightPenalization,
                this.airportPenalization,
                this.sumaPaquetesWeight,
                this.sumaVuelosWeight,
                this.promedioPonderadoTiempoAeropuertoWeight,
                this.mediaVuelosWight,
                this.vuelos_hash,
                this.grafoVuelos);

        HashMap<Integer, Boolean> indexes = new HashMap<Integer, Boolean>();
        ArrayList<Paquete> randomPackages = new ArrayList<Paquete>();
        int[] randomPackageIndexes = new int[windowSize];

        ArrayList<Integer> invalidIndexes = new ArrayList<>();
        for (int j = 0; j < this.paquetes.size(); j++) {
            if (!this.rutasValidas.get(j)) {
                invalidIndexes.add(j);
            }
        }
        LOGGER.info("Generate Neighbour - Primer loop");
        // System.out.println("Primer Loop");
        for (int i = 0; i < windowSize; i++) {
            int randomIndex = generatePseudoRandomIndex(this.paquetes.size(), probIndexInvParametro, invalidIndexes);
            //LOGGER.info("Generate Neighbour - Primer loop Random index " + randomIndex);
            int maxAttempts = 0;
            boolean repetido = false;
            while (indexes.get(randomIndex) != null) {
                //LOGGER.info("Generate Neighbour - Primer loop Random index repetido " + randomIndex + " - " + indexes.get(randomIndex));
                randomIndex = generatePseudoRandomIndex(this.paquetes.size(), probIndexInvParametro, invalidIndexes);
                maxAttempts++;
                if(maxAttempts > 100){
                    repetido = true;
                    break;
                }
            }
            if(repetido){
                randomPackageIndexes[i] = -1;
                randomPackages.add(null);
            }
            else{
                randomPackageIndexes[i] = randomIndex;
                indexes.put(randomPackageIndexes[i], true);

                PlanRutaNT oldRoute = rutas.get(randomPackageIndexes[i]);
                neighbour.deocupyRouteFlights(oldRoute);
                randomPackages.add(neighbour.paquetes.get(randomPackageIndexes[i]));
            }

        }
        LOGGER.info("Generate Neighbour - Segundo loop");
        // System.out.println("Segundo Loop");
        // generate new routes for the selected packages
        for (int j = 0; j < windowSize; j++) {
            if(randomPackageIndexes[j] == -1){
                continue;
            }
            int conteo = 0;
            // System.out.print("| Intento " + (j + 1) + " de generacion de vecino |");
            while (true) {
                ArrayList<Paquete> tempPaquetesArray = new ArrayList<Paquete>();
                tempPaquetesArray.add(randomPackages.get(j));

                ArrayList<PlanRutaNT> tempRoutesArray = grafoVuelos.generarRutasParaPaquetes(tempPaquetesArray,
                        vueloService, tiempoEnSimulacion, TA);
                if (tempPaquetesArray == null) {
                    return null;
                }
                PlanRutaNT randomRoute = tempRoutesArray.get(0);

                // check if origin and destiny is different
                if (neighbour.isCurrentRouteValid(randomPackages.get(j), randomRoute) == true
                        && neighbour.isRouteFlightsCapacityAvailable(randomRoute) == true) {
                    neighbour.ocupyRouteFlights(randomRoute);
                    neighbour.rutas.set(randomPackageIndexes[j], randomRoute);
                    break;
                }
                conteo++;

                if (conteo >= 500) { // antes era 1000
                    return this;
                }
            }
        }
        LOGGER.info("Generate Neighbour - Fin Segundo loop");

        // tambien deberiamos medir si esto llega a repetirse X veces simplemente
        // devolver la solucion actual
        // if(neighbour.isAirportCapacityAvailable() == false){ //We generate again if
        // the airport capacity is exceeded
        // return generateNeighbour(todasLasRutas, windowSize);
        // } else {
        neighbour.rutasValidas = new ArrayList<>();
        for (int i = 0; i < neighbour.paquetes.size(); i++) {
            neighbour.rutasValidas.add(false);
        }
        for (int i = 0; i < neighbour.paquetes.size(); i++) {
            double[] costAndConteo = getCostoPaquete(i);
            if (costAndConteo[1] > 0.0) {
                neighbour.rutasValidas.set(i, false);
            } else {
                neighbour.rutasValidas.set(i, true);

            }
        }
        return neighbour;
        // }

    }

    public void printFlightOcupation(String filename) {

        File csvFile = new File(filename);
        PrintWriter out;

        try {
            out = new PrintWriter(csvFile);

            for (HashMap.Entry<Integer, Integer> entry : ocupacionVuelos.entrySet()) {
                Vuelo vuelo = vuelos_hash.get(entry.getKey());
                Date fechaSalida = vuelo.getFechaSalida();
                Date fechaLlegada = vuelo.getFechaLlegada();
                String origen = vuelo.getPlanVuelo().getCiudadOrigen().getId();
                String destino = vuelo.getPlanVuelo().getCiudadDestino().getId();
                out.println(
                        "Vuelo (" + entry.getKey() + ")  " +
                                origen + "-" + destino + " " +
                                Funciones.getFormattedDate(fechaSalida) + " - "
                                + Funciones.getFormattedDate(fechaLlegada) +
                                " -> " + entry.getValue() + " / " + vuelo.getPlanVuelo().getCapacidadMaxima());
            }

            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // iterate over ocupacionVuelos

    }

    public void printAirportHistoricOcupation(String filename) {
        this.estado = new EstadoAlmacen(this.paquetes, this.rutas, this.vuelos_hash, this.ocupacionVuelos,
                this.aeropuertos);
        estado.consulta_historicaTxt(filename);
    }

    public double getSTPaquetes() {
        double sum = 0;
        for (int i = 0; i < paquetes.size(); i++) {
            double tiempoRecepcion = paquetes.get(i).getEnvio().getFechaRecepcion().getTime();
            double tiempoLlegadaRuta = rutas.get(i).getVuelos().get(rutas.get(i).getVuelos().size() - 1)
                    .getFechaLlegada().getTime();
            double diferenciaFechaMaxima = paquetes.get(i).getEnvio().getFechaLimiteEntrega().getTime()
                    - tiempoRecepcion;
            double diferenciaFechaEntrega = tiempoLlegadaRuta - tiempoRecepcion;
            double porcentajeTiempo = (diferenciaFechaEntrega) / diferenciaFechaMaxima;
            sum += Math.max(0, porcentajeTiempo);
        }
        return sum;
    }

    public double getSTVuelos() {
        double sum = 0;
        for (HashMap.Entry<Integer, Integer> entry : ocupacionVuelos.entrySet()) {
            Vuelo vuelo = vuelos_hash.get(entry.getKey());
            int maxCapacity = vuelo.getPlanVuelo().getCapacidadMaxima();
            int usedCapacity = entry.getValue();

            if (usedCapacity >= maxCapacity) {
                sum += 100000;
            } else {
                sum += (double) usedCapacity / (double) maxCapacity;
            }
        }
        return sum;
    }

    public double getPPTAeropuerto() {
        this.estado = new EstadoAlmacen(this.paquetes, this.rutas, this.vuelos_hash, this.ocupacionVuelos,
                this.aeropuertos);
        return this.estado.calcularCostoTotalAlmacenamiento();
    }

    public double[] getCostoPaquete(int i) {
        double currentCost = 0;
        double conteoSinSentido = 0;

        Date horaSalidaRuta = this.rutas.get(i).getVuelos().get(0).getFechaSalida();
        Date horaLlegadaRuta = this.rutas.get(i).getVuelos().get(this.rutas.get(i).getVuelos().size() - 1)
                .getFechaLlegada();

        Date horaRecepcionPaquete = this.paquetes.get(i).getEnvio().getFechaRecepcion();
        Date horaMaximaEntregaPaquete = this.paquetes.get(i).getEnvio().getFechaLimiteEntrega();

        int cantVuelosDePaquete = this.rutas.get(i).getVuelos().size();

        currentCost += (double) (horaLlegadaRuta.getTime() - horaRecepcionPaquete.getTime())
                / (horaMaximaEntregaPaquete.getTime() - horaRecepcionPaquete.getTime());

        if (horaSalidaRuta.after(horaRecepcionPaquete) == false) { // inusable
            currentCost = 100000;
        }

        if (horaLlegadaRuta.after(horaMaximaEntregaPaquete) == true) { // para colapso es usable
            currentCost = 100;
        }

        if (horaSalidaRuta.after(horaRecepcionPaquete) == false
                || horaLlegadaRuta.after(horaMaximaEntregaPaquete) == true) {
            conteoSinSentido++;
        }

        return new double[] { currentCost, conteoSinSentido, cantVuelosDePaquete };
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
        cost = cost * this.sumaPaquetesWeight;
        double costoVuelos = this.getSTVuelos();
        double costoAeropuertos = this.getPPTAeropuerto();

        cost += costoVuelos * this.sumaVuelosWeight;
        cost += costoAeropuertos * this.promedioPonderadoTiempoAeropuertoWeight;
        cost += mediaVuelos * this.mediaVuelosWight;
        return cost;
    }

    public ArrayList<Paquete> getPaquetesSinSentido() {
        ArrayList<Paquete> paquetesSinSentido = new ArrayList<>();

        for (int i = 0; i < this.paquetes.size(); i++) {
            double[] costAndConteo = getCostoPaquete(i);
            double conteoSinSentido = costAndConteo[1];

            if (conteoSinSentido > 0.0) {
                paquetesSinSentido.add(this.paquetes.get(i));
            }
        }
        return paquetesSinSentido;
    }

    public void printCosts() {
        double cost = 0;
        double conteoSinSentido = 0;
        double mediaVuelos = 0;
        for (int i = 0; i < this.paquetes.size(); i++) {
            double[] costAndConteo = getCostoPaquete(i);
            cost += costAndConteo[0]; // costo de paquetes y asignacion de rutas
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
        System.out.println(
                " -> Costo total: " + ((cost * this.sumaPaquetesWeight) + (costoVuelos * this.sumaVuelosWeight)
                        + (costoAeropuertos * this.promedioPonderadoTiempoAeropuertoWeight)
                        + (mediaVuelos * this.mediaVuelosWight)));
    }

    public void printCostsInLog() {
        double cost = 0;
        double conteoSinSentido = 0;
        double mediaVuelos = 0;
        for (int i = 0; i < this.paquetes.size(); i++) {
            double[] costAndConteo = getCostoPaquete(i);
            cost += costAndConteo[0]; // costo de paquetes y asignacion de rutas
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
        System.out.println(
                " -> Costo total: " + ((cost * this.sumaPaquetesWeight) + (costoVuelos * this.sumaVuelosWeight)
                        + (costoAeropuertos * this.promedioPonderadoTiempoAeropuertoWeight)
                        + (mediaVuelos * this.mediaVuelosWight)));
        Funciones.printLineInLog(
                " -> Costo total: " + ((cost * this.sumaPaquetesWeight) + (costoVuelos * this.sumaVuelosWeight)
                        + (costoAeropuertos * this.promedioPonderadoTiempoAeropuertoWeight)
                        + (mediaVuelos * this.mediaVuelosWight)));
    }

    public boolean isCurrentRouteValid(int i) {
        String idRutaOrigen = this.rutas.get(i).getVuelos().get(0).getPlanVuelo().getCiudadOrigen().getId();
        String idRutaDestino = this.rutas.get(i).getVuelos().get(this.rutas.get(i).getVuelos().size() - 1)
                .getPlanVuelo().getCiudadDestino().getId();

        String idPaqueteOrigen = this.paquetes.get(i).getEnvio().getUbicacionOrigen().getId();
        String idPaqueteDestino = this.paquetes.get(i).getEnvio().getUbicacionDestino().getId();

        Date horaSalidaRuta = this.rutas.get(i).getVuelos().get(0).getFechaSalida();
        Date horaLlegadaRuta = this.rutas.get(i).getVuelos().get(this.rutas.get(i).getVuelos().size() - 1)
                .getFechaLlegada();

        Date horaRecepcionPaquete = this.paquetes.get(i).getEnvio().getFechaRecepcion();
        Date horaMaximaEntregaPaquete = this.paquetes.get(i).getEnvio().getFechaLimiteEntrega();

        if (!idRutaOrigen.equals(idPaqueteOrigen) ||
                !idRutaDestino.equals(idPaqueteDestino) ||
                !horaSalidaRuta.after(horaRecepcionPaquete) ||
                !horaLlegadaRuta.before(horaMaximaEntregaPaquete)) {
            return false;
        }

        return true;
    }

    public boolean isCurrentRouteValid(Paquete paquete, PlanRutaNT planRuta) {
        String idRutaOrigen = planRuta.getVuelos().get(0).getPlanVuelo().getCiudadOrigen().getId();
        String idRutaDestino = planRuta.getVuelos().get(planRuta.getVuelos().size() - 1)
                .getPlanVuelo().getCiudadDestino().getId();

        String idPaqueteOrigen = paquete.getEnvio().getUbicacionOrigen().getId();
        String idPaqueteDestino = paquete.getEnvio().getUbicacionDestino().getId();

        Date horaSalidaRuta = planRuta.getVuelos().get(0).getFechaSalida();
        Date horaLlegadaRuta = planRuta.getVuelos().get(planRuta.getVuelos().size() - 1)
                .getFechaLlegada();

        Date horaRecepcionPaquete = paquete.getEnvio().getFechaRecepcion();
        Date horaMaximaEntregaPaquete = paquete.getEnvio().getFechaLimiteEntrega();

        if (!idRutaOrigen.equals(idPaqueteOrigen) ||
                !idRutaDestino.equals(idPaqueteDestino) ||
                !horaSalidaRuta.after(horaRecepcionPaquete) ||
                !horaLlegadaRuta.before(horaMaximaEntregaPaquete)) {
            return false;
        }

        return true;
    }

    public boolean isRouteFlightsCapacityAvailable(PlanRutaNT ruta) {
        for (int i = 0; i < ruta.getVuelos().size(); i++) {
            int maxCapacity = ruta.getVuelos().get(i).getPlanVuelo().getCapacidadMaxima();

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

    public void ocupyRouteFlights(PlanRutaNT ruta) {
        for (int i = 0; i < ruta.getVuelos().size(); i++) {

            int idVuelo = ruta.getVuelos().get(i).getId();

            if (ocupacionVuelos.get(idVuelo) == null) {
                ocupacionVuelos.put(idVuelo, 1);
            } else {
                ocupacionVuelos.put(idVuelo, ocupacionVuelos.get(idVuelo) + 1);
            }

        }
    }

    public void deocupyRouteFlights(PlanRutaNT ruta) {
        for (int i = 0; i < ruta.getVuelos().size(); i++) {
            int idVuelo = ruta.getVuelos().get(i).getId();
            if (ocupacionVuelos.get(idVuelo) == 0) {
                System.out.println("Se intento desocupar un vuelo que no estaba ocupado");

                throw new Error("Se intento desocupar un vuelo que no estaba ocupado");
            }
            ocupacionVuelos.put(idVuelo, ocupacionVuelos.get(idVuelo) - 1);
        }
    }

    public boolean isAirportCapacityAvailable() {
        this.estado = new EstadoAlmacen(paquetes, rutas, vuelos_hash, ocupacionVuelos, aeropuertos);
        return estado.verificar_capacidad_maxima();
    }

    public void printLogCouldntFindRoute(Paquete paquete, String filename) {
        File csvFile = new File(filename);
        PrintWriter out;

        try {
            out = new PrintWriter(csvFile);
            out.println("No se pudo encontrar una ruta valida despues de 100 intentos");
            out.println("Paquete: " + paquete.getId());
            out.println("Origen: " + paquete.getEnvio().getUbicacionOrigen().getId());
            out.println("Destino: " + paquete.getEnvio().getUbicacionDestino().getId());
            out.println("Fecha Recepcion: " + Funciones.getFormattedDate(paquete.getEnvio().getFechaRecepcion()));
            out.println(
                    "Fecha Maxima Entrega: " + Funciones.getFormattedDate(paquete.getEnvio().getFechaLimiteEntrega()));
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}