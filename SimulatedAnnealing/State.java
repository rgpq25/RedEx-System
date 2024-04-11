import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Date;

import Clases.Paquete;
import Clases.Vuelo;

public class State {
    Paquete[] paquetes;

    public State(Paquete[] _paquetes){
        this.paquetes = _paquetes.clone();
    }

    public State(){

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

    //choose a random package and assign it to a random VALID flight 
    public State generateNeighbours(Vuelo[] listaVuelos, Paquete[] listaPaquetes){
        State neighbour = new State();
        Paquete[] nei_paquete = new Paquete[paquetes.length];
        for(int i = 0; i < paquetes.length; i++){
            nei_paquete[i] = new Paquete(paquetes[i]);
        }
        neighbour.paquetes = nei_paquete.clone();


        int randomPackageIndex = (int) (Math.random() * (paquetes.length));
        Paquete randomPackage = neighbour.paquetes[randomPackageIndex];
        Paquete[] tempArray = new Paquete[1];
        tempArray[0] = new Paquete(randomPackage);
        State _temp = new State(tempArray);
        if(_temp.verifyPlanErrors(randomPackage) == 0){
            _temp.printRutasTXT("tempSol.txt");
            System.out.println("Paquete " + randomPackage.getId() + " is already valid");
            return neighbour;
        }

        Vuelo[] currentPlan = new Vuelo[randomPackage.getListaVuelos().length];
        for(int i = 0; i < currentPlan.length; i++){
            currentPlan[i] = new Vuelo(randomPackage.getListaVuelos()[i]);
        }
        int chosenMove = (int) (Math.random() * 3);
        if(currentPlan.length == 0) chosenMove = 1;
        //if(currentPlan.length >= 10) chosenMove = 2;
        // 0 = swap 2 flights
        // 1 = add a flight
        // 2 = remove a flight


        if(chosenMove == 0){
            int index1 = (int) (Math.random() * currentPlan.length);
            int index2 = (int) (Math.random() * currentPlan.length);

            Vuelo temp = currentPlan[index1];
            currentPlan[index1] = currentPlan[index2];
            currentPlan[index2] = temp;

            
        } else if(chosenMove == 1){
            int randomIndex = (int) (Math.random() * listaVuelos.length);

            //evitamos agregar un repetido
            for(int i = 0; i < currentPlan.length; i++){
                if(currentPlan[i].getId() == listaVuelos[randomIndex].getId()){
                    randomIndex = (int) (Math.random() * listaVuelos.length);
                    i = 0;
                }
            }

            int indexNew = (int) (Math.random() * (currentPlan.length + 1));

            Vuelo[] newPlan = new Vuelo[currentPlan.length + 1];
            int conteo = 0;
            for(int i = 0; i < currentPlan.length + 1; i++){
                if(i == indexNew){
                    newPlan[i] = listaVuelos[randomIndex];
                    continue;
                }
                newPlan[i] = currentPlan[conteo];
                conteo++;
            }
            currentPlan = newPlan.clone();

        } else if(chosenMove == 2){
            //remove a flight
            int index = (int) (Math.random() * currentPlan.length);
            Vuelo[] newPlan = new Vuelo[currentPlan.length - 1];

            int counter = 0;
            for(int i = 0; i < currentPlan.length; i++){
                if(i != index){
                    newPlan[counter] = currentPlan[i];
                    counter++;
                }
            }
            currentPlan = new Vuelo[newPlan.length];
            currentPlan = newPlan.clone();
        }

        randomPackage.setLista_Vuelos(currentPlan);
        neighbour.paquetes[randomPackageIndex] = new Paquete(randomPackage);
        return neighbour;
    }


    public double verifyPlanErrors(Paquete _paquete){
        double errorCount = 0;
        Vuelo[] planVuelos = _paquete.getListaVuelos();

        if(planVuelos.length == 0){
            errorCount += 1000;
        }
        for(int i=0; i < planVuelos.length; i++){
            Vuelo currentVuelo = planVuelos[i];

            //1. Verificar que primer vuelo sea el correcto
            if(i == 0 && _paquete.getId_ciudad_origen() != currentVuelo.getPlan_vuelo().getId_ubicacion_origen()){
                errorCount += 10;
            }

            //2. Verificar que ultimo vuelo sea el correcto
            if(i == planVuelos.length - 1 && _paquete.getId_ciudad_destino() != currentVuelo.getPlan_vuelo().getId_ubicacion_destino()){
                errorCount += 10;
            }
        }

        return errorCount;
    }


    public double evaluate(){
        double errorCount = 0;
        for(int i = 0; i < paquetes.length; i++){
            errorCount += verifyPlanErrors(paquetes[i]);
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
