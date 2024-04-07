import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import Clases.Aeropuerto;
import Clases.FlujoCapacidad;
import Clases.Paquete;
import Clases.Vuelo;

/**
 * Class representing the PSO Engine. This class implements all the necessary methods for initializing the swarm,
 * updating the velocity and position vectors, determining the fitness of particles and finding the best particle.
 */

//NOTA ...

public class PSOEngine {

    int numDimensions = 30; //Number of dimensions for problem
    int numParticles = 30; //Number of particles in swarm
    int maxIterations = 10000; //Max number of iterations
    double c1 = 1.496180; //Cognitive coefficient
    double c2 = 1.496180; //Social coefficient
    double w = 0.729844; //Inertia coefficient

    public PSOEngine (int numDimensions, int numParticles, int maxIterations, double c1, double c2, double w ) {
        this.numDimensions = numDimensions;
        this.numParticles = numParticles;
        this.maxIterations = maxIterations;
        this.c1 = c1;
        this.c2 = c2;
        this.w = w;
    }


    /**
     * Method to initialize the particles for PSO
     * @param particles The set of particles to initialize
     */
    public void initParticles(Particle[] particles) {
        //For each particle
        for (int i=0; i<particles.length;i++) {
            double[] positions = new double[numDimensions];
            double[] velocities = new double [numDimensions];
            //For each dimension of the particle assign a random x value [-5.12,5.12] and velocity=0
            //NOTA: pregunta - el rango de valores dependera del espacio de busqueda de la funcion objetivo
            //NOTA: esto cambiaria porque se mencion que dependia de la funcion fitness
            for (int j=0; j<numDimensions; j++) {
                positions[j] = ((Math.random()* ((5.12-(-5.12)))) - 5.12);
                velocities[j] = 0;
            }
            //Create the particle
            particles[i] = new Particle(positions, velocities);
            //Set particles personal best to initialized values
            particles[i].personalBest = particles[i].position.clone();
        }
    }

    /**
     * Method to update the velocities vector of a particle
     * @param particle The particle to update the velocity for
     */
    public void updateVelocity(Particle particle, double[] best, double[] r1, double[] r2) {
        //First we clone the velocities, positions, personal and neighbourhood best
        double[] velocities = particle.velocity.clone();
        double[] personalBest = particle.personalBest.clone();
        double[] positions = particle.position.clone();
        double[] bestNeigh = best.clone();

        double[] inertiaTerm = new double[numDimensions];
        double[] difference1 = new double[numDimensions];
        double[] difference2 = new double[numDimensions];

        double[] c1Timesr1 = new double[numDimensions];
        double[] c2Timesr2 = new double[numDimensions];

        double[] cognitiveTerm = new double[numDimensions];
        double[] socialTerm = new double[numDimensions];

        //Calculate inertia component
        for (int i=0; i<numDimensions; i++) {
            inertiaTerm[i] = w*velocities[i];
        }

        //Calculate the cognitive component

        //Calculate personal best - current position
        for (int i=0; i<numDimensions; i++) {
            difference1[i] = personalBest[i] - positions[i];
        }

        //Calculate c1*r1
        for (int i=0; i<numDimensions; i++) {
            c1Timesr1[i] = c1*r1[i];
        }

        //Calculate c1*r1*diff = cognitive term
        for (int i=0; i<numDimensions; i++) {
            cognitiveTerm[i] = c1Timesr1[i]*difference1[i];
        }

        //Calculate the social term

        //Calculate neighbourhood best - current position
        for (int i=0; i<numDimensions; i++) {
            difference2[i] = bestNeigh[i] - positions[i];
        }

        //Calculate c2*r2
        for (int i=0; i<numDimensions; i++) {
            c2Timesr2[i] = c2*r2[i];
        }
        //Calculate c2*r2*diff2 = social component
        for (int i=0; i<numDimensions; i++) {
            socialTerm[i] = c2Timesr2[i]*difference2[i];
        }

        //Update particles velocity at all dimensions
        for (int i=0; i<numDimensions; i++) {
            particle.velocity[i] = inertiaTerm[i]+cognitiveTerm[i]+socialTerm[i];
        }
    }

    /**
     * Method to update the positions vector of a particle
     * @param particle The particle to update the position for
     */

    public void updatePosition(Particle particle) {
        //Since new position is ALWAYS calculated after calculating new velocity, it is okay to just add old position to the current velocity (as velocity would have already been updated).
        for (int i=0; i<numDimensions; i++) {
            particle.position[i] = particle.position[i]+particle.velocity[i];
        }

    }

    /**
     * Method to find the best (fittest) particle from a given set of particles
     * @param particles The collection of particles to determine the best from
     * @return The best (fittest) particle from the collection of particles
     */

    //NOTA: esto cambiaria, en este caso se esta buscando el menor fitness
    public double[] findBest(Particle[] particles) {
        double[] best = null;
        double bestFitness = Double.MAX_VALUE;
        for(int i=0; i<numParticles; i++) {
            if (evaluateFitness(particles[i].personalBest)<= bestFitness) {
                bestFitness = evaluateFitness(particles[i].personalBest);
                best = particles[i].personalBest;
            }
        }
        return best;
    }

    /**
     * Method to calculate the fitness of a particle using the Rastrigin function
     * @param positions The position vector to evaluate the fitness for
     * @return The fitness of the particle
     */

    //NOTA: esto cambiaria
    public double evaluateFitness(double[] positions) {
        double fitness = 0;
        for (int i=0; i<numDimensions; i++) {
            fitness = fitness + (Math.pow(positions[i],2)-(10*Math.cos(2*Math.PI*positions[i])));
        }

        fitness = fitness + (10*numDimensions);
        return fitness;
    }

    public void asignar_capacidad_utlizada_inicial(Vuelo[] vuelos, int[] capacidad_vuelos_original){
        for(int i=0; i<vuelos.length; i++){
            vuelos[i].setCapacidad_utilizada(capacidad_vuelos_original[i]);
        }
    }

    public double evaluateFitness2(int[][] rutas,Paquete[] paquetes, Vuelo [] vuelos, Aeropuerto[] aeropuertos){
        int numPaquetes = paquetes.length;
        int numVuelos = vuelos.length;
        int numAeropuertos = aeropuertos.length;
        // rutas[i][j] = 1 si el paquete i va en el vuelo j;  0 en otro caso

        //VALIDAR LA SECUENCIA DE VUELOS; SI NO ES VALIDO, DEVUELVE EL "PEOR" FITNESS
        //CARGAR LA CAPACIDAD DE CADA VUELO

        int [] capacidad_vuelos_original = new int[numVuelos];
        for(int i=0; i<numVuelos; i++){
            capacidad_vuelos_original[i] = vuelos[i].getCapacidad_utilizada();
        }

        long [] porcentaje_tiempo = new long[numPaquetes];

        for(int i=0; i<numPaquetes; i++){
            List<Integer> indicesVuelos = new ArrayList<>();
            for(int j=0; j<numVuelos; j++){
                if(rutas[i][j] == 1){
                    indicesVuelos.add(j);
                    vuelos[j].aumentar_capacidad_utilizada(1);;
                }
            }
            if(indicesVuelos.size() == 0){
                asignar_capacidad_utlizada_inicial(vuelos, capacidad_vuelos_original);
                return Double.MAX_VALUE; //PEOR FITNESS
            }
            //1. Revisa si el primer vuelo viene de la ciudad del paquete
            String ciudad_origen = paquetes[i].getId_ciudad_almacen();
            String ciudad_origen_primer_vuelo = vuelos[indicesVuelos.get(0)].getPlan_vuelo().getId_ubicacion_origen();
            if(!ciudad_origen.equals(ciudad_origen_primer_vuelo)){
                asignar_capacidad_utlizada_inicial(vuelos, capacidad_vuelos_original);
                return Double.MAX_VALUE; //PEOR FITNESS
            }

            for(int j=0; j<indicesVuelos.size()-1; j++){
                // 2. Revisan las horas de llegada y salida del siguiente vuelo
                Date fecha_llegada = vuelos[indicesVuelos.get(j)].getFecha_llegada();
                Date fecha_salida_sig = vuelos[indicesVuelos.get(j+1)].getFecha_salida();
                if(fecha_llegada.after(fecha_salida_sig)){
                    asignar_capacidad_utlizada_inicial(vuelos, capacidad_vuelos_original);
                    return Double.MAX_VALUE; //PEOR FITNESS
                }

                // 3. Se revisan las ciudades de origen y destino
                String ciudad_destino = vuelos[indicesVuelos.get(j)].getPlan_vuelo().getId_ubicacion_destino();
                String ciudad_origen_sig = vuelos[indicesVuelos.get(j+1)].getPlan_vuelo().getId_ubicacion_origen();
                if(!ciudad_destino.equals(ciudad_origen_sig)){
                    asignar_capacidad_utlizada_inicial(vuelos, capacidad_vuelos_original);
                    return Double.MAX_VALUE; //PEOR FITNESS
                }
            }

            // 4. Se verifica que la ciudad destino del ultimo vuelo sea la ciudad destino del paquete
            String ciudad_destino = vuelos[indicesVuelos.get(indicesVuelos.size()-1)].getPlan_vuelo().getId_ubicacion_destino();
            String ciudad_destino_paquete = paquetes[i].getId_ciudad_destino();
            if(!ciudad_destino.equals(ciudad_destino_paquete)){
                asignar_capacidad_utlizada_inicial(vuelos, capacidad_vuelos_original);
                return Double.MAX_VALUE; //PEOR FITNESS
            }

            //Todo OK, entonces se calcula la diferencia entre la fecha de llegada del ultimo vuelo y la fecha de entrega del paquete
            Date fecha_llegada = vuelos[indicesVuelos.get(indicesVuelos.size()-1)].getFecha_llegada();
            long diferencia_fecha_maxima = paquetes[i].getFecha_maxima_entrega().getTime() - paquetes[i].getFecha_recepcion().getTime();
            long diferencia_fecha_entrega = fecha_llegada.getTime() - paquetes[i].getFecha_recepcion().getTime();
            porcentaje_tiempo[i] = (diferencia_fecha_entrega*100)/diferencia_fecha_maxima;
        }

        //CUANDO SE TENGAN LOS VUELOS CARGADOS, SE CALCULARA LAS CAPACIDADES DE LOS AEOROPUERTOS DURANTE EL TRANSCURSO

        List<FlujoCapacidad> flujo_capacidades = new ArrayList<>();
        for(int i=0; i<numVuelos; i++){
            flujo_capacidades.add(new FlujoCapacidad(-vuelos[i].getCapacidad_utilizada(), vuelos[i].getFecha_salida(), vuelos[i].getPlan_vuelo().getId_ubicacion_origen()));
            flujo_capacidades.add(new FlujoCapacidad(vuelos[i].getCapacidad_utilizada(), vuelos[i].getFecha_llegada(), vuelos[i].getPlan_vuelo().getId_ubicacion_destino()));
        }

        Collections.sort(flujo_capacidades, new Comparator<FlujoCapacidad>() {
            @Override
            public int compare(FlujoCapacidad o1, FlujoCapacidad o2) {
                return o1.getFecha().compareTo(o2.getFecha());
            }
        });

        int numFlujos = flujo_capacidades.size();
        
        for(int i=0; i<numAeropuertos; i++){
            int capacidad = aeropuertos[i].getCapacidad_utilizada();
            for(int j=0; j<numFlujos; j++){
                if(aeropuertos[i].getId().equals(flujo_capacidades.get(j).getId_ciudad())){
                    capacidad += flujo_capacidades.get(j).getCapacidad();
                }
                if(capacidad < 0 || capacidad > aeropuertos[i].getCapacidad_maxima()){
                    asignar_capacidad_utlizada_inicial(vuelos, capacidad_vuelos_original);
                    return Double.MAX_VALUE; //PEOR FITNESS
                }
            }
        }

        //Se calcula el promedio de porcentaje de tiempo
        long promedio_tiempo = 0;
        for(int i=0; i<numPaquetes; i++){
            promedio_tiempo += porcentaje_tiempo[i];
        }
        promedio_tiempo = promedio_tiempo/numPaquetes;

        return promedio_tiempo;
    }
}
