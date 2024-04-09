import Clases.Aeropuerto;
import Clases.Paquete;
import Clases.Vuelo;
/**
 * Class implementing the PSO algorithm.
 */

public class PSOimplementation {

	public final int numDimensions; //Number of dimensions for problem
	public final int numParticles = 100; //Number of particles in swarm
	public final int maxIterations = 100; //Max number of iterations
	public final double c1 = 1.4; //Cognitive coefficient
	public final double c2 = 1.4; //Social coefficient
	public final double w = 0.7; //Inertia coefficient
	public  double[] r1; //Random vector 1
	public  double[] r2;  //Random vector 2
	public int[] best;
	Particle[] particles; //Array to hold all particles
	
	public PSOimplementation(Paquete[] paquetes, Vuelo [] vuelos, Aeropuerto[] aeropuertos) {
		//PSO algorithm
		int numPaquetes = paquetes.length;
        int numVuelos = vuelos.length;
        int numAeropuertos = aeropuertos.length;

		this.numDimensions = numPaquetes*numVuelos;

		particles = new Particle[numParticles];
		PSOEngine PSO = new PSOEngine(numDimensions, numParticles, maxIterations, c1, c2, w);

		//Initialize particles
		PSO.initParticles(particles,paquetes.length, vuelos.length);

		//PSO loop
		int numIter = 0;
		while (numIter<maxIterations) {
			// Evaluate fitness of each particle
			for (int i=0; i<numParticles; i++) {
				particles[i].fitness = PSO.evaluateFitness2(particles[i].position, paquetes, vuelos, aeropuertos);

				//update personal best position 
				if (particles[i].fitness <= PSO.evaluateFitness2(particles[i].personalBest, paquetes, vuelos, aeropuertos)) {
					particles[i].personalBest = particles[i].position.clone();
				}
			}
			//Find best particle in set
			best = PSO.findBest(particles,paquetes, vuelos, aeropuertos);

			//Initialize the random vectors for updates
			r1 = new double[numDimensions];
			r2 = new double[numDimensions];
			for (int i=0; i<numDimensions; i++) {
				r1[i] = Math.random();
				r2[i] = Math.random();
			}

			//Update the velocity and position vectors
			for (int i=0; i<numParticles;i++) {
				PSO.updateVelocity(particles[i], best, r1, r2);
				PSO.updatePosition(particles[i]);
			}
			numIter++;
		}	
		System.out.println(PSO.evaluateFitness2(best, paquetes, vuelos, aeropuertos));
	}



	public void print (int[] a) {
		System.out.print("< ");
		for (int i=0; i<a.length; i++) {
			System.out.print(a[i]  + " ");
		}
		System.out.println(" > ");

	}
	/* 
	public static void main(String[] args) {
		PSOimplementation p = new PSOimplementation(); 
	}
	*/
}