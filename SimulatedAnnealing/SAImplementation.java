import java.lang.reflect.Array;

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

        //choose a random package and assign it to a random VALID flight 
        public State[] generateNeighbours(Vuelo[] listaVuelos, Paquete[] listaPaquetes){
            State[] neighbours = new State[paquetes.length];

            for (int i = 0; i < paquetes.length; i++) {
                Paquete paquete = paquetes[i];
                PlanRuta planRuta = paquete.getPlan_rutas();

                for (int j = 0; j < listaVuelos.length; j++) {
                    
                    for(int k = 0; k <planRuta.length(); k++){
                        Vuelo vuelo = planRuta.getVuelos()[k];

                        if (k == 0 && !vuelo.getPlan_vuelo().getId_ubicacion_origen().equals(paquete.getId_ciudad_origen())) {
                            continue;
                        }
                        if(k == listaVuelos.length - 1 && !vuelo.getPlan_vuelo().getId_ubicacion_destino().equals(paquete.getId_ciudad_destino())){
                            continue;
                        }


                        if (cumpleSecuenciaLogica(vuelo, paquete)) {
                            Paquete[] newPaquetes = paquetes.clone();
                            newPaquetes[i] = paquete;
                            neighbours[i] = new State(newPaquetes);
                        }
                        
                    }
                    Vuelo vuelo = listaVuelos[j];
                    if (vuelo.getPlan_vuelo().getId_ubicacion_origen().equals(paquete.getId_ciudad_origen()) &&
                            vuelo.getPlan_vuelo().getId_ubicacion_destino().equals(paquete.getId_ciudad_destino())) {
                        if (cumpleSecuenciaLogica(vuelo, paquete)) {
                            Paquete[] newPaquetes = paquetes.clone();
                            newPaquetes[i] = paquete;
                            neighbours[i] = new State(newPaquetes);
                        }
                    }
                }
            }

            for (Vuelo vuelo : listaVuelos) {
                for (Paquete paquete : listaPaquetes) {
                    // Verificar restricciones de origen y destino
                    if (vuelo.getPlan_vuelo().getId_ubicacion_origen().equals(paquete.getId_ciudad_origen()) &&
                            vuelo.id_ubicacion_destino.equals(paquete.id_ciudad_destino)) {
                        // Verificar restricciones de secuencia lógica de vuelos
                        if (cumpleSecuenciaLogica(vuelo, paquete)) {
                            // Clonar la asignación actual
                            Map<PlanVuelo, List<Paquete>> nuevaAsignacion = new HashMap<>(asignacionPaquetes);
                            List<Paquete> paquetesEnVuelo = nuevaAsignacion.getOrDefault(vuelo, new ArrayList<>());

                            // Movemos el paquete al vuelo actual
                            paquetesEnVuelo.add(paquete);
                            nuevaAsignacion.put(vuelo, paquetesEnVuelo);

                            // Creamos un nuevo estado con esta asignación y lo agregamos a los vecinos
                            vecinos.add(new Estado(nuevaAsignacion));
                        }
                    }
                }
            }

            return neighbours;
        }



    }

    public SAImplementation(double temp, double coolingRate, Paquete[] paquetes, Vuelo[] vuelos, Aeropuerto[] aeropuertos) {
		int numPaquetes = paquetes.length;
        int numVuelos = vuelos.length;
        int numAeropuertos = aeropuertos.length;

        //conseguir solucion randomizada inicial

        //asumir que best = current (randomizada)
        while(temp > 1){
            //crear neighbour copiando original


            //cambio ligero en solucion


            temp *= 1 - coolingRate;
        }

		// System.out.println(PSO.evaluateFitness2(best, paquetes, vuelos, aeropuertos));
		// PSO.printPosiciones(best, numPaquetes, numVuelos);
		// PSO.printRutasTXT(best, numPaquetes, numVuelos, paquetes, vuelos);
	}
}
