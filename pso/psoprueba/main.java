import Clases.Aeropuerto;
import Clases.Paquete;
import Clases.Vuelo;

public class main {
    public static void main(String[] args) {
        //System.out.println("Hello, World!");
        Funciones funciones = new Funciones();
        Aeropuerto [] aeropuertos = funciones.leerAeropuertos();
        //System.out.println(aeropuertos[0].getCapacidad_maxima());
        Paquete [] paquetes = funciones.leerPaquetes();
        Vuelo [] vuelos = funciones.leerVuelos();
        int numAeropuertos = aeropuertos.length;
        int numPaquetes = paquetes.length;
        int numVuelos = vuelos.length;
        //System.out.println(numAeropuertos + " " + numPaquetes + " " + numVuelos);
        
        int [][] rutas = new int[numPaquetes][numVuelos];

        PSOEngine pso = new PSOEngine();
        System.out.print("Rutas: ");
        
        pso.generarRutasAleatorias(rutas, numPaquetes, numVuelos);
        
        /*
        for (int i = 0; i < numPaquetes; i++) {
            for (int j = 0; j < numVuelos; j++) {
                System.out.print(rutas[i][j] + " ");
            }
            System.out.println();
        }
         */
        double resultadoFitness = pso.evaluateFitness2(rutas, paquetes, vuelos, aeropuertos);
        System.out.println(resultadoFitness);
        System.out.println("Fin :D");
    }
}