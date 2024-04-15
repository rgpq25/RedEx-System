import Clases.Aeropuerto;
import Clases.Paquete;
import Clases.Vuelo;

public class SAImplementation {

    public double acceptNewState(double deltaCosto, double temperatura){
        if(deltaCosto < 0){
            return 1.0;
        } else {
            return (1 / (1 + Math.exp(deltaCosto / temperatura)));
        }
    }

    public SAImplementation(Paquete[] paquetes, Vuelo[] vuelos, Aeropuerto[] aeropuertos) {
		int numPaquetes = paquetes.length;

        double temp = 100000;
        double coolingRate = 0.001;
        int neighbourCount = 100;

        //conseguir solucion randomizada inicial
        State current = new State(paquetes.clone());
        for(int i = 0; i < numPaquetes; i++){
            current.paquetes[i].setLista_Vuelos(current.getRandomizedPlan(vuelos));
        }

        current.printRutasTXT("initial.txt");

        
        while(temp > 1){
            //crear neighbour copiando original
            State[] neighbours = new State[neighbourCount];
            for(int i = 0; i < neighbourCount; i++){
                neighbours[i] = current.generateNeighbours(vuelos, paquetes);
            }
            int randomNeighbourIndex = (int) (Math.random() * neighbourCount);
            State nuevoEstado = neighbours[randomNeighbourIndex];

            double deltaCosto = nuevoEstado.evaluate()- current.evaluate();
            if(acceptNewState(deltaCosto, temp) > Math.random()){
                current = new State(nuevoEstado.paquetes.clone());
            }

            double currentCost = current.evaluate();
            System.out.println("Current error count: " + currentCost);

            if(currentCost == 0){
                System.out.println("Found solution: " + currentCost);
                break;
            }

            temp *= 1 - coolingRate;
        }

        current.printRutasTXT("result.txt");
	}
}
