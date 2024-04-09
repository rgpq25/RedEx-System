import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;

import Clases.Aeropuerto;
import Clases.FlujoCapacidad;
import Clases.Paquete;
import Clases.Vuelo;

/**
 * Class representing the PSO Engine. This class implements all the necessary methods for initializing the swarm,
 * updating the velocity and position vectors, determining the fitness of particles and finding the best particle.
 */


public class PSOEngine {

    int numDimensions; //Number of dimensions for problem
    int numParticles; //Number of particles in swarm
    int maxIterations; //Max number of iterations
    double c1; //Cognitive coefficient
    double c2; //Social coefficient
    double w; //Inertia coefficient

    public PSOEngine (int numDimensions, int numParticles, int maxIterations, double c1, double c2, double w ) {
        this.numDimensions = numDimensions;
        this.numParticles = numParticles;
        this.maxIterations = maxIterations;
        this.c1 = c1;
        this.c2 = c2;
        this.w = w;
    }

    public PSOEngine() {
    }


    public void initParticles(Particle[] particles, int numPaquetes, int numVuelos) {
        Random rand = new Random();
        //For each particle
        for (int i=0; i<particles.length;i++) {
            int[] positions = new int[numDimensions];
            double[] velocities = new double [numDimensions];
            /* 
            int contador = 0;
            for(int j=0; j<numPaquetes; j++){
                for(int k=0; k<numVuelos; k++){
                    //rutas[i][j] = positions[contador];
                    positions[contador] = rand.nextInt(2);
                    velocities[contador] = 0;
                    contador++;
                }
            }
            */
            
            for (int j=0; j<numDimensions; j++) {
                //Se asigna o 0 o 1
                positions[j] = rand.nextInt(2);
                velocities[j] = 0;
            }
            
            //Create the particle
            particles[i] = new Particle(positions, velocities);
            //Set particles personal best to initialized values
            particles[i].personalBest = particles[i].position.clone();
        }
    }

    /**
     * Method to update the velocity vector of a particle
     * @param particle The particle to update the velocity for
     * @param best The best particle in the swarm
     * @param r1 Random vector 1
     * @param r2 Random vector 2
     */
    public void updateVelocity(Particle particle, int[] best, double[] r1, double[] r2) {
        //First we clone the velocities, positions, personal and neighbourhood best
        double[] velocities = particle.velocity.clone();
        int[] personalBest = particle.personalBest.clone();
        int[] positions = particle.position.clone();
        int[] bestNeigh = best.clone();

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
        //The new position is ALWAYS calculated after calculating new velocity
        for (int i=0; i<numDimensions; i++) {
            particle.position[i] += particle.velocity[i] >= 0.5 ? 1 : 0;
        }

    }

    /**
     * Method to find the best (fittest) particle from a given set of particles
     * @param particles The collection of particles to determine the best from
     * @return The best (fittest) particle from the collection of particles
     */

    
    public int[] findBest(Particle[] particles, Paquete[] paquetes, Vuelo[] vuelos, Aeropuerto[] aeropuertos) {
        int[] best = null;
        double bestFitness = Double.MAX_VALUE;
        for(int i=0; i<numParticles; i++) {
            if (evaluateFitness2(particles[i].personalBest,paquetes,vuelos,aeropuertos)<= bestFitness) {
                bestFitness = evaluateFitness2(particles[i].personalBest,paquetes,vuelos,aeropuertos);
                best = particles[i].personalBest;
            }
        }
        return best;
    }

    /**
     * Ejemplo de Fitness
     */
    /* 
    public double evaluateFitness(int[] positions) {
        double fitness = 0;
        for (int i=0; i<numDimensions; i++) {
            fitness  = fitness + positions[i];
            //fitness = fitness + (Math.pow(positions[i],2)-(10*Math.cos(2*Math.PI*positions[i])));
        }

        //fitness = fitness + (10*numDimensions);
        return fitness;
    }
    */

    /*
    public void generarRutasAleatorias(int [][] rutas, int numPaquetes, int numVuelos){
        Random rand = new Random();
        for(int i=0; i<numPaquetes; i++){
            for(int j=0; j<numVuelos; j++){
                //int valorRand = rand.nextInt(2);
                rutas[i][j] = rand.nextInt(2);
                //System.out.println(valorRand);
            }
        }
    }
     */

    public void asignar_capacidad_utlizada_inicial(Vuelo[] vuelos, int[] capacidad_vuelos_original){
        for(int i=0; i<vuelos.length; i++){
            vuelos[i].setCapacidad_utilizada(capacidad_vuelos_original[i]);
        }
    }

    public int[][] generarMatrizRutasApartirDePosiciones(int [] positions,int numPaquetes ,int numVuelos){
        int [][] rutas = new int[numPaquetes][numVuelos];
        int contador = 0;
        for(int i=0; i<numPaquetes; i++){
            for(int j=0; j<numVuelos; j++){
                rutas[i][j] = positions[contador];
                contador++;
            }
        }
        return rutas;
    }

    public double evaluateFitness2(int[] positions,Paquete[] paquetes, Vuelo [] vuelos, Aeropuerto[] aeropuertos){
        double fitness = 0;
        int numPaquetes = paquetes.length;
        int numVuelos = vuelos.length;
        int numAeropuertos = aeropuertos.length;
        int [][] rutas = generarMatrizRutasApartirDePosiciones(positions, numPaquetes, numVuelos);
        // rutas[i][j] = 1 si el paquete i va en el vuelo j;  0 en otro caso


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
                //System.out.println("Fallo 0");
                asignar_capacidad_utlizada_inicial(vuelos, capacidad_vuelos_original);
                return Double.MAX_VALUE; //PEOR FITNESS
            }
            //1. Revisa si el primer vuelo viene de la ciudad del paquete
            String ciudad_origen = paquetes[i].getId_ciudad_almacen();
            String ciudad_origen_primer_vuelo = vuelos[indicesVuelos.get(0)].getPlan_vuelo().getId_ubicacion_origen();
            if(!ciudad_origen.equals(ciudad_origen_primer_vuelo)){
                //System.out.println("Fallo 1");
                //asignar_capacidad_utlizada_inicial(vuelos, capacidad_vuelos_original);

                //Penalizacion de Fitness, no es el peor porque afectaria la exploracion del PSO
                fitness = fitness + 1000;
                
            }

            //2. Revisa si el primer vuelo sale despues de la fecha de recepcion del paquete
            //NOTA: SE TENDRIA QUE VERIFICAR QUE LA FECHA DE SALIDA DEL PRIMER VUELO SEA DESPUES DE AHORA
            Date fecha_salida = vuelos[indicesVuelos.get(0)].getFecha_salida();
            if(fecha_salida.before(paquetes[i].getFecha_recepcion())){
                //System.out.println("Fallo 2");
                //asignar_capacidad_utlizada_inicial(vuelos, capacidad_vuelos_original);

                //Penalizacion de Fitness, no es el peor porque afectaria la exploracion del PSO
                fitness = fitness + 1000;
                
            }

            for(int j=0; j<indicesVuelos.size()-1; j++){
                // 3. Revisan las horas de llegada y salida del siguiente vuelo
                Date fecha_llegada = vuelos[indicesVuelos.get(j)].getFecha_llegada();
                Date fecha_salida_sig = vuelos[indicesVuelos.get(j+1)].getFecha_salida();
                if(fecha_llegada.after(fecha_salida_sig)){
                    //System.out.println("Fallo 3");
                    //asignar_capacidad_utlizada_inicial(vuelos, capacidad_vuelos_original);

                    //Penalizacion de Fitness, no es el peor porque afectaria la exploracion del PSO
                    fitness = fitness + 1000;
                }

                // 4. Se revisan las ciudades de origen y destino
                String ciudad_destino = vuelos[indicesVuelos.get(j)].getPlan_vuelo().getId_ubicacion_destino();
                String ciudad_origen_sig = vuelos[indicesVuelos.get(j+1)].getPlan_vuelo().getId_ubicacion_origen();
                if(!ciudad_destino.equals(ciudad_origen_sig)){
                    //System.out.println("Fallo 4");

                    //Penalizacion de Fitness, no es el peor porque afectaria la exploracion del PSO
                    fitness = fitness + 1000;
                }
            }

            // 5. Se verifica que la ciudad destino del ultimo vuelo sea la ciudad destino del paquete
            String ciudad_destino = vuelos[indicesVuelos.get(indicesVuelos.size()-1)].getPlan_vuelo().getId_ubicacion_destino();
            String ciudad_destino_paquete = paquetes[i].getId_ciudad_destino();
            if(!ciudad_destino.equals(ciudad_destino_paquete)){
                //System.out.println("Fallo 5");
                //asignar_capacidad_utlizada_inicial(vuelos, capacidad_vuelos_original);

                //Penalizacion de Fitness, no es el peor porque afectaria la exploracion del PSO
                fitness = fitness + 1000;
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
                //se asume que el Id aeropuerto es el codigo de la ciudad
                if(aeropuertos[i].getId().equals(flujo_capacidades.get(j).getId_ciudad())){
                    capacidad += flujo_capacidades.get(j).getCapacidad();
                }
                if(capacidad < 0 || capacidad > aeropuertos[i].getCapacidad_maxima()){
                    //System.out.println("Fallo 6");
                    //asignar_capacidad_utlizada_inicial(vuelos, capacidad_vuelos_original);

                    //Penalizacion de Fitness, no es el peor porque afectaria la exploracion del PSO
                    fitness = fitness + 1000;
                }
            }
        }

        //Se calcula el promedio de porcentaje de tiempo
        long promedio_tiempo = 0;
        for(int i=0; i<numPaquetes; i++){
            promedio_tiempo += porcentaje_tiempo[i];
        }
        promedio_tiempo = promedio_tiempo/numPaquetes;
        asignar_capacidad_utlizada_inicial(vuelos, capacidad_vuelos_original);
        fitness = fitness + promedio_tiempo;
        return fitness;
    }
}
