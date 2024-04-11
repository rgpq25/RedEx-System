import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import Clases.Aeropuerto;
import Clases.Paquete;
import Clases.PlanRuta;
import Clases.Vuelo;

public class SAImplementation {


    static class State {
        Paquete[] paquetes;

        public State(Paquete[] _paquetes){
            this.paquetes = _paquetes.clone();
        }

        public State(){

        }

        //choose a random package and assign it to a random VALID flight 
        public State generateNeighbours(Vuelo[] listaVuelos, Paquete[] listaPaquetes){
            // State[] neighbours = new State[paquetes.length];
            State neighbour = new State(paquetes);
            Paquete[] nei_paquete = new Paquete[paquetes.length];
            for(int i = 0; i < paquetes.length; i++){
                nei_paquete[i] = paquetes[i];
                nei_paquete[i].setLista_Vuelos(getRandomizedPlan(listaVuelos.clone()));
            }
            neighbour.paquetes = nei_paquete.clone();
            return neighbour;


            // int randomPackageIndex = (int) (Math.random() * (paquetes.length));
            // Paquete randomPackage = neighbour.paquetes[randomPackageIndex];
            // Vuelo[] currentPlan = randomPackage.getListaVuelos();
            // int chosenMove = (int) (Math.random() * 3);

            // if(currentPlan.length == 0) chosenMove = 1;
            // if(currentPlan.length >= 10) chosenMove = 2;
            // // 0 = swap 2 flights
            // // 1 = add a flight
            // // 2 = remove a flight

            // if(chosenMove == 0){
            //     int index1 = (int) (Math.random() * currentPlan.length);
            //     int index2 = (int) (Math.random() * currentPlan.length);

            //     Vuelo temp = currentPlan[index1];
            //     currentPlan[index1] = currentPlan[index2];
            //     currentPlan[index2] = temp;

                
            // } else if(chosenMove == 1){
            //     int randomIndex = (int) (Math.random() * listaVuelos.length);
            //     Vuelo[] newPlan = new Vuelo[currentPlan.length + 1];
            //     for(int i = 0; i < currentPlan.length; i++){
            //         newPlan[i] = currentPlan[i];
            //     }
            //     newPlan[currentPlan.length] = listaVuelos[randomIndex];
            //     currentPlan = newPlan.clone();

            // } else if(chosenMove == 2){
            //     //remove a flight
            //     int index = (int) (Math.random() * currentPlan.length);
            //     Vuelo[] newPlan = new Vuelo[currentPlan.length - 1];

            //     int counter = 0;
            //     for(int i = 0; i < currentPlan.length; i++){
            //         if(i != index){
            //             newPlan[counter] = currentPlan[i];
            //             counter++;
            //         }
            //     }
            //     currentPlan = new Vuelo[newPlan.length];
            //     currentPlan = newPlan.clone();
            // }

            // randomPackage.setLista_Vuelos(currentPlan);
            // neighbour.paquetes[randomPackageIndex] = randomPackage;
            // return neighbour;
        }

        public Vuelo[] getRandomizedPlan(Vuelo[] listaVuelos){
            

            int maxFlightCount = 10;
            int flightCount = (int) (Math.random() * (maxFlightCount + 1));
            Vuelo[] randomizedPlan = new Vuelo[flightCount];

            int[] usedIndexes = new int[flightCount];
            for(int i = 0; i < flightCount; i++){
                int randomIndex = (int) (Math.random() * (listaVuelos.length));

                for(int j = 0; j < usedIndexes.length; j++){
                    if(usedIndexes[j] == randomIndex){
                        randomIndex = (int) (Math.random() * (listaVuelos.length));
                        j = 0;
                    }
                }

                randomizedPlan[i] = listaVuelos[randomIndex];
                usedIndexes[i] = randomIndex;
            }

            return randomizedPlan;
        }

        public double verifyPlanErrors(Paquete paquete, Vuelo[] planVuelos){
            double errorCount = 0;

            if(planVuelos.length == 0){
                errorCount += 1000;
            }
            for(int i=0; i < planVuelos.length; i++){
                Vuelo currentVuelo = planVuelos[i];

                //1. Verificar que primer vuelo sea el correcto
                if(i == 0 && paquete.getId_ciudad_origen() != currentVuelo.getPlan_vuelo().getId_ubicacion_origen()){
                    errorCount += 10;
                }

                //2. Verificar que ultimo vuelo sea el correcto
                if(i == planVuelos.length - 1 && paquete.getId_ciudad_destino() != currentVuelo.getPlan_vuelo().getId_ubicacion_destino()){
                    errorCount += 10;
                }

                //3. Verificar que la fecha de maxima entrega del paquete no se exceda
                // if(i == planVuelos.length - 1 && currentVuelo.getFecha_llegada().compareTo(paquete.getFecha_maxima_entrega()) > 0){
                //     errorCount += 10;
                // }

                //4. Verificar que la capacidad del vuelo no se exceda
                // if(currentVuelo.getCapacidad_utilizada() >= currentVuelo.getPlan_vuelo().getCapacidad_maxima()){
                //     errorCount += 10;
                // }

                //5. Verificar que la fecha de llegada del vuelo anterior sea menor a la fecha de salida del vuelo actual
                // if(i > 0){
                //     Vuelo previousVuelo = planVuelos[i - 1];
                //     if(previousVuelo.getFecha_llegada().compareTo(currentVuelo.getFecha_salida()) > 0){
                //         errorCount += 10;
                //     }
                // }

                //6. Verificar que la fecha de llegada del vuelo actual sea menor a la fecha de salida del vuelo siguiente
                // if(i < planVuelos.length - 1){
                //     Vuelo nextVuelo = planVuelos[i + 1];
                //     // if(currentVuelo.getFecha_llegada().compareTo(nextVuelo.getFecha_salida()) > 0){
                //     //     errorCount += 10;
                //     // }

                //     //7. Verificar que el vuelo actual tiene de destino la ciudad de origen del vuelo siguiente
                //     if(currentVuelo.getPlan_vuelo().getId_ubicacion_destino() != nextVuelo.getPlan_vuelo().getId_ubicacion_origen()){
                //         errorCount += 100;
                //     }
                // }

                //TODO: Verificar capacidades correctamente
            }

            return errorCount;
        }


        public double evaluate(){
            double errorCount = 0;
            for(int i = 0; i < paquetes.length; i++){
                errorCount = verifyPlanErrors(paquetes[i], paquetes[i].getListaVuelos());
            }
            return errorCount;
        }

        public void printSelectedRoutes(){
            for(int i = 0; i < paquetes.length; i++){
                System.out.println("Paquete " + paquetes[i].getId() + " con " + paquetes[i].getListaVuelos().length + " vuelos seleccionados: ");
                for(int j = 0; j < paquetes[i].getListaVuelos().length; j++){
                    System.out.println("Vuelo " + paquetes[i].getListaVuelos()[j].getId());
                }
            }
        }

        public void printRutasTXT(String filename){
            File csvFile = new File(filename);
            PrintWriter out;
    
            try {
                out = new PrintWriter(csvFile);
    
                for(int i=0; i<paquetes.length; i++){
                    String origen_paquete = paquetes[i].getId_ciudad_origen();
                    String destino_paquete = paquetes[i].getId_ciudad_destino();
                    Date fecha_recepcion = paquetes[i].getFecha_recepcion();
                    out.println("Paquete " + i + " - " + origen_paquete + "-" + destino_paquete + "  |  " + fecha_recepcion);

                    Vuelo[] vuelos = paquetes[i].getListaVuelos();
                    for(int j=0; j<vuelos.length; j++){
                            String id_origen = vuelos[j].getPlan_vuelo().getId_ubicacion_origen();
                            String id_destino = vuelos[j].getPlan_vuelo().getId_ubicacion_destino();
                            Date fecha_salida = vuelos[j].getFecha_salida();
                            Date fecha_llegada = vuelos[j].getFecha_llegada();
                        
                            out.println("         " + id_origen + "-" + id_destino + " " + fecha_salida + "-" + fecha_llegada);
                    }
                    out.println();
                }
    
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }



    public double acceptNewState(double deltaCosto, double temperatura){
        if(deltaCosto < 0){
            return 1.0;
        } else {
            return (1 / (1 + Math.exp(deltaCosto / temperatura)));
        }
    }

    public SAImplementation(Paquete[] paquetes, Vuelo[] vuelos, Aeropuerto[] aeropuertos) {
		int numPaquetes = paquetes.length;

        double temp = 1000000000;
        double coolingRate = 0.01;

        int neighbourCount = 1000;
        //conseguir solucion randomizada inicial
        State current = new State(paquetes.clone());
        for(int i = 0; i < numPaquetes; i++){
            current.paquetes[i].setLista_Vuelos(current.getRandomizedPlan(vuelos));
        }

        current.printRutasTXT("initial.txt");

        //asumir que best = current (randomizada)
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
