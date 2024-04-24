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

import Clases.Vuelo;
import Clases.Aeropuerto;
import Clases.Ubicacion;
import Clases.Paquete;

public class PSO {

    //inicializar pso
    public PSO() {
    }

    static class Particle {
        List<Integer> position;
        List<Double> velocity;
        List<Integer> bestPosition;
        double bestFitness;

        Particle(int numPackages, int numRoutes) {
            position = new ArrayList<>();
            velocity = new ArrayList<>();
            bestPosition = new ArrayList<>();
            for (int i = 0; i < numPackages; i++) {
                position.add(new Random().nextInt(numRoutes));
                velocity.add(Math.random());
            }
            bestPosition.addAll(position);
            bestFitness = Double.POSITIVE_INFINITY;
        }
    }

    static double calcularCosto(Paquete paquete, Ruta ruta, List<Ruta> rutas) {
        final double COSTO_MAXIMO = Double.POSITIVE_INFINITY;
        String formato = "yyyy-MM-dd HH:mm";

        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(ruta.identificador);
        int numeroRuta = matcher.find() ? Integer.parseInt(matcher.group()) : -1;

        if (numeroRuta >= rutas.size())
            return COSTO_MAXIMO;

        if (!ruta.vuelos.get(0).getPlan_vuelo().getCiudadOrigen().getId().equals(paquete.getCiudadOrigen().getId()) ||
                !ruta.vuelos.get(ruta.vuelos.size() - 1).getPlan_vuelo().getCiudadDestino().getId().equals(paquete.getCiudadDestino().getId()))
            return 1000;

        //long tiempoRecepcion = Paquete.FORMATO_FECHA.parse(paquete.fechaRecepcion).getTime();
        //long tiempoRecepcion = new SimpleDateFormat(formato).parse(paquete.getFecha_recepcion()).getTime();
        long tiempoRecepcion = paquete.getFecha_recepcion().getTime();
        //long tiempoPartidaRuta = new SimpleDateFormat(formato).parse(ruta.vuelos.get(0).fechaPartida).getTime();
        long tiempoPartidaRuta = ruta.vuelos.get(0).getFecha_salida().getTime();
        //long tiempoLlegadaRuta = new SimpleDateFormat(formato).parse(ruta.vuelos.get(ruta.vuelos.size() - 1).fechaLlegada).getTime();
        long tiempoLlegadaRuta = ruta.vuelos.get(ruta.vuelos.size() - 1).getFecha_llegada().getTime();

        if (tiempoPartidaRuta < tiempoRecepcion)
            return 1000;

        if (tiempoLlegadaRuta - tiempoRecepcion > 48 * 3600 * 1000)
            return 100;

        double horasParaEntrega = (tiempoLlegadaRuta - tiempoRecepcion) / (3600.0 * 1000);
        return Math.max(0, horasParaEntrega);
        //return 0;
    }

    static double fitness(List<Integer> position, List<Paquete> packages, List<Ruta> rutas) {
        double totalCost = 0;
        for (int i = 0; i < position.size(); i++) {
            double costo = calcularCosto(packages.get(i), rutas.get(position.get(i)), rutas);
            totalCost += costo;
        }
        return totalCost;
    }

    static int[] pso(List<Paquete> packages, List<Ruta> rutas, int numParticles, int maxIterations, double w, double c1, double c2) {
        List<Particle> particles = new ArrayList<>();
        for (int i = 0; i < numParticles; i++) {
            particles.add(new Particle(packages.size(), rutas.size()));
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
                    particle.position.set(i, Math.max(0, Math.min(particle.position.get(i), rutas.size() - 1)));
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
