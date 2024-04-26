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
import Clases.MedianCalculator;
import Clases.Ubicacion;
import Clases.Paquete;
import Clases.PlanRuta;

public class PSO {

    //inicializar pso
    public PSO() {
    }

    static class Particle {
        List<Integer> position;
        List<Double> velocity;
        List<Integer> bestPosition;
        double bestFitness;

        Particle(List<Paquete> packages, HashMap<String, ArrayList<PlanRuta>> rutas) {
            position = new ArrayList<>();
            velocity = new ArrayList<>();
            bestPosition = new ArrayList<>();
            //iterate for each package using a counter
            for (int i = 0; i < packages.size(); i++) {
                //get ciudad origen paquete id
                String ciudadOrigen = packages.get(i).getCiudadOrigen().getId();
                //get ciudad destino paquete id
                String ciudadDestino = packages.get(i).getCiudadDestino().getId();
                String cadena = ciudadOrigen + "-" + ciudadDestino;
                ArrayList<PlanRuta> planRutas = rutas.get(cadena);
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
        double porcentaje_tiempo = (diferencia_fecha_entrega*100)/diferencia_fecha_maxima;
        return Math.max(0, porcentaje_tiempo);

        //Verifica que el primer vuelo no salga antes de recibir el paquete
        /*if (tiempoPartidaRuta < tiempoRecepcion)
            return 1000000;

        //Esto verifica el tiempo maximo de entrega FALTA MODIFICAR ESTO
        if (tiempoLlegadaRuta - tiempoRecepcion > 48 * 3600 * 1000)
            return 100;

        //Por ahora devuelve el total de horas de viaje FALTA MODIFICAR ESTO    
        double horasParaEntrega = (tiempoLlegadaRuta - tiempoRecepcion) / (3600.0 * 1000);
        return Math.max(0, horasParaEntrega);*/
        //return 0;
    }

    static double fitness(List<Integer> position, List<Paquete> packages, HashMap<String, ArrayList<PlanRuta>> rutas) {
        ArrayList<Double> costos = new ArrayList<>();
        double totalCost = 0;
        for (int i = 0; i < position.size(); i++) {
            String ciudadOrigen = packages.get(i).getCiudadOrigen().getId();
            String ciudadDestino = packages.get(i).getCiudadDestino().getId();
            String cadena = ciudadOrigen + "-" + ciudadDestino;
            ArrayList<PlanRuta> planRutas = rutas.get(cadena);
            if (planRutas == null) {
                return 1000000;
            }
            //double costo = calcularCosto(packages.get(i), planRutas.get(position.get(i)));
            //totalCost += costo;
            costos.add(calcularCosto(packages.get(i), planRutas.get(position.get(i))));
        }
        double median = MedianCalculator.calculateMedian(costos);
        totalCost += median;
        return totalCost;
    }

    static int[] pso(List<Paquete> packages, HashMap<String, ArrayList<PlanRuta>> rutas, int numParticles, int maxIterations, double w, double c1, double c2) {
        List<Particle> particles = new ArrayList<>();
        for (int i = 0; i < numParticles; i++) {
            particles.add(new Particle(packages, rutas));
        }

        List<Integer> globalBestPosition = particles.get(0).position;
        double globalBestFitness = Double.POSITIVE_INFINITY;

        for (int iter = 0; iter < maxIterations; iter++) {
            for (Particle particle : particles) {
                double fitnessVal = fitness(particle.position, packages, rutas);

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

                    String ciudadOrigen = packages.get(i).getCiudadOrigen().getId();
                    String ciudadDestino = packages.get(i).getCiudadDestino().getId();
                    String cadena = ciudadOrigen + "-" + ciudadDestino;
                    ArrayList<PlanRuta> planRutas = rutas.get(cadena);
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
