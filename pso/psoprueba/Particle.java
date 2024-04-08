public class Particle {
    int[] position; //The position vector of this particle
	double fitness; //The fitness of this particle
	double[] velocity; //The velocity vector of this particle
	int[] personalBest; //Personal best of the particle

	public Particle(int[] position, double[] velocity) {
		this.position = position;
		this.velocity = velocity; 
	}
}
