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

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

import Clases.Vuelo;
import Clases.Aeropuerto;
import Clases.EstadoAlmacen;
import Clases.Funciones;
import Clases.MedianCalculator;
import Clases.Ubicacion;
import Clases.Paquete;
import Clases.PlanRuta;

public class PSO {

    //inicializar pso
    public PSO() {
    }

    /*public void checkFirstFlightTime(position, packages, rutas) {
        for (int i = 0; i < position.size(); i++) {
            String ciudadOrigen = packages.get(i).getCiudadOrigen().getId();
            String ciudadDestino = packages.get(i).getCiudadDestino().getId();
            String cadena = ciudadOrigen + "-" + ciudadDestino;
            ArrayList<PlanRuta> planRutas = rutas.get(cadena);
            if (planRutas == null) {
                //print error message
                System.out.println("No hay rutas para el paquete"+ packages.get(i).getId());
                return Double.MAX_VALUE;
            }
            
            double costo = calcularCosto(packages.get(i), planRutas.get(position.get(i)));
            costoPaquetes += costo;
            planRutasEscogidas.add(planRutas.get(position.get(i)));
        }

    }*/

    static class Particle {
        List<Integer> position;
        List<Double> velocity;
        List<Integer> bestPosition;
        double bestFitness;

        Particle(List<Paquete> packages,HashMap<Integer, ArrayList<PlanRuta>> rutas) {
            position = new ArrayList<>();
            velocity = new ArrayList<>();
            bestPosition = new ArrayList<>();
            //iterate for each package using a counter
            for (int i = 0; i < packages.size(); i++) {
                ArrayList<PlanRuta> planRutas = rutas.get(packages.get(i).getId());
                if (planRutas == null) {
                    position.add(0);
                    velocity.add(Math.random());
                }
                else{
                    position.add(new Random().nextInt(planRutas.size()));
                    velocity.add(Math.random());
                }
                
            }


            /*for (int i = 0; i < packages.size(); i++) {
                position.add(new Random().nextInt(numRoutes));
                velocity.add(Math.random());
            }*/
            bestPosition.addAll(position);
            bestFitness = Double.POSITIVE_INFINITY;
        }
    }

    static double calcularCosto(Paquete paquete, PlanRuta planRuta) {
        final double COSTO_MAXIMO = Double.POSITIVE_INFINITY;
        String formato = "yyyy-MM-dd HH:mm";

        //Pattern pattern = Pattern.compile("\\d+");
        //Matcher matcher = pattern.matcher(ruta.identificador);
        //int numeroRuta = matcher.find() ? Integer.parseInt(matcher.group()) : -1;

        /*if (numeroRuta >= rutas.size())
            return COSTO_MAXIMO;*/

        //NOTA: Se supone que cumple esta condicion, para eso se usa el GRAFO
        /*if (!planRuta.getVuelos().get(0).getPlan_vuelo().getCiudadOrigen().getId().equals(paquete.getCiudadOrigen().getId()) ||
                !planRuta.getVuelos().get(planRuta.getVuelos().size() - 1).getPlan_vuelo().getCiudadDestino().getId().equals(paquete.getCiudadDestino().getId()))
            return 1000;*/

        //long tiempoRecepcion = Paquete.FORMATO_FECHA.parse(paquete.fechaRecepcion).getTime();
        //long tiempoRecepcion = new SimpleDateFormat(formato).parse(paquete.getFecha_recepcion()).getTime();
        long tiempoRecepcion = paquete.getFecha_recepcion().getTime();
        //long tiempoPartidaRuta = new SimpleDateFormat(formato).parse(ruta.vuelos.get(0).fechaPartida).getTime();
        long tiempoPartidaRuta = planRuta.getVuelos().get(0).getFecha_salida().getTime();
        //long tiempoLlegadaRuta = new SimpleDateFormat(formato).parse(ruta.vuelos.get(ruta.vuelos.size() - 1).fechaLlegada).getTime();
        long tiempoLlegadaRuta = planRuta.getVuelos().get(planRuta.getVuelos().size() - 1).getFecha_llegada().getTime();

        long diferencia_fecha_maxima = paquete.getFecha_maxima_entrega().getTime() - paquete.getFecha_recepcion().getTime();
        long diferencia_fecha_entrega = tiempoLlegadaRuta - tiempoRecepcion;
        double porcentaje_tiempo = (diferencia_fecha_entrega)/diferencia_fecha_maxima;
        //Verifica que el primer vuelo no salga antes de recibir el paquete
        if (tiempoPartidaRuta < tiempoRecepcion)
            return 100000;
            //return Double.MAX_VALUE;
        if (porcentaje_tiempo > 1)
            return 100;
        return Math.max(0, porcentaje_tiempo);

    }

    public static void ocupyRouteFlights(PlanRuta ruta, HashMap<Integer, Integer> ocupacionVuelos) {
        for (int i = 0; i < ruta.getVuelos().size(); i++) {
            
            int idVuelo = ruta.getVuelos().get(i).getId();

            if(ocupacionVuelos.get(idVuelo) == null){
                ocupacionVuelos.put(idVuelo, 1);
            } else {
                ocupacionVuelos.put(idVuelo, ocupacionVuelos.get(idVuelo) + 1);
            }
            
        }
    }

    static double fitness(List<Integer> position, ArrayList<Paquete> packages, HashMap<Integer, ArrayList<PlanRuta>> rutas,ArrayList<Aeropuerto> aeropuertos,
    HashMap<Integer, Vuelo> vuelos_map ) {
        //ArrayList<Double> costosPaquetes = new ArrayList<>();
        double totalCost = 0;
        double costoPaquetes = 0;
        double costoVuelos = 0;
        double costoAeropuertos = 0;
        ArrayList<PlanRuta> planRutasEscogidas = new ArrayList<>(); 
        Funciones funciones = new Funciones();
        for (int i = 0; i < position.size(); i++) {

            ArrayList<PlanRuta> planRutas = rutas.get(packages.get(i).getId());
            if (planRutas == null) {
                //print error message
                System.out.println("No hay rutas para el paquete"+ packages.get(i).getId());
                return Double.MAX_VALUE;
            }
            
            double costo = calcularCosto(packages.get(i), planRutas.get(position.get(i)));
            costoPaquetes += costo;
            planRutasEscogidas.add(planRutas.get(position.get(i)));
        }
        
        HashMap<Integer, Integer> ocupacionVuelos = new HashMap<>();
        for (int i = 0; i < position.size(); i++) {
            PlanRuta planRuta = planRutasEscogidas.get(i);
            ocupyRouteFlights(planRuta, ocupacionVuelos);
        }
        //HashMap<Integer, Vuelo> vuelos_hash

        EstadoAlmacen estado= new EstadoAlmacen(packages, planRutasEscogidas, vuelos_map, ocupacionVuelos, aeropuertos);
        costoAeropuertos += estado.calcularCostoTotalAlmacenamiento();

        //TO DO costo aeropuertos, usar cuando se construya en el main con el grafo
        //iterate ocupacionVuelos
        for (Map.Entry<Integer, Integer> entry : ocupacionVuelos.entrySet()) {
            if(entry.getValue() > vuelos_map.get(entry.getKey()).getPlan_vuelo().getCapacidad_maxima()){
                costoVuelos+= 100000;
                //return Double.MAX_VALUE;
            }
            else{
                costoVuelos += (double)entry.getValue()/vuelos_map.get(entry.getKey()).getPlan_vuelo().getCapacidad_maxima();
            }
        }
        
        totalCost = costoPaquetes*10 + costoVuelos*4 + costoAeropuertos*4;
        return totalCost;
    }

    static int[] pso(ArrayList<Paquete> packages, HashMap<Integer, ArrayList<PlanRuta>> rutas,ArrayList<Aeropuerto> aeropuertos,HashMap<Integer, Vuelo> vuelos_map,
     int numParticles, int maxIterations, double w, double c1, double c2) {
        List<Particle> particles = new ArrayList<>();
        for (int i = 0; i < numParticles; i++) {
            particles.add(new Particle(packages, rutas));
        }

        List<Integer> globalBestPosition = particles.get(0).position;
        double globalBestFitness = Double.POSITIVE_INFINITY;

        for (int iter = 0; iter < maxIterations; iter++) {
            for (Particle particle : particles) {
                double fitnessVal = fitness(particle.position, packages, rutas,aeropuertos,vuelos_map);

                if (fitnessVal <= particle.bestFitness) {
                    particle.bestFitness = fitnessVal;
                    particle.bestPosition = new ArrayList<>(particle.position);
                }

                if (fitnessVal <= globalBestFitness) {
                    globalBestFitness = fitnessVal;
                    globalBestPosition = new ArrayList<>(particle.position);
                }

                for (int i = 0; i < particle.position.size(); i++) {
                    particle.velocity.set(i, w * particle.velocity.get(i) +
                            c1 * Math.random() * (particle.bestPosition.get(i) - particle.position.get(i)) +
                            c2 * Math.random() * (globalBestPosition.get(i) - particle.position.get(i)));

                    particle.position.set(i, particle.position.get(i) + particle.velocity.get(i).intValue());

                    /*String ciudadOrigen = packages.get(i).getCiudadOrigen().getId();
                    String ciudadDestino = packages.get(i).getCiudadDestino().getId();
                    String cadena = ciudadOrigen + "-" + ciudadDestino;*/
                    ArrayList<PlanRuta> planRutas = rutas.get(packages.get(i).getId());
                    
                    if(planRutas != null){
                        particle.position.set(i, Math.max(0, Math.min(particle.position.get(i), planRutas.size() - 1)));
                    }
                    else{
                        particle.position.set(i, 0);
                    }
                }
            }
            // System.out.println("Mejor posición: " + globalBestPosition + " con fitness " + globalBestFitness);
        }

        int[] result = new int[globalBestPosition.size()];
        for (int i = 0; i < globalBestPosition.size(); i++) {
            result[i] = globalBestPosition.get(i);
        }
        return result;
    }
}
