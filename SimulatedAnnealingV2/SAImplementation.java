import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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

    public List<Vuelo> getPossibleFlights(String origin, List<Vuelo> flights) {
        List<Vuelo> possibleFlights = new ArrayList<>();
        for (Vuelo flight : flights) {
            if (flight.getPlan_vuelo().getId_ubicacion_origen().equals(origin)) {
                possibleFlights.add(flight);
            }
        }
        return possibleFlights;
    }

    public List<Vuelo> generateRandomRoute(Paquete pkg, List<Vuelo> flights) {
        List<Vuelo> route = new ArrayList<>();
        String currentLocation = pkg.getId_ciudad_origen();
        while (!currentLocation.equals(pkg.getId_ciudad_destino())) {
            List<Vuelo> possibleFlights = getPossibleFlights(currentLocation, flights);
            if (possibleFlights.isEmpty()) {
                // No valid flights available from the current location
                return route;
            }
            Vuelo chosenFlight = possibleFlights.get(ThreadLocalRandom.current().nextInt(possibleFlights.size()));
            route.add(chosenFlight);
            currentLocation = chosenFlight.getPlan_vuelo().getId_ubicacion_destino();
        }
        return route;
    }

    public SAImplementation(Paquete paquete, Vuelo[] vuelos, Aeropuerto[] aeropuertos) {

        List<Vuelo> currentRoute = new ArrayList<Vuelo>();
        currentRoute.add(vuelos[ThreadLocalRandom.current().nextInt(vuelos.length)]);

        double temperature = 10000;
        double coolingRate = 0.003;
        int neighbourCount = 100;



        //current.printRutasTXT("initial.txt");

        
        while (temperature > 1) {
            // Pick a random neighboring solution
            List<Vuelo> newRoute = new ArrayList<>(currentRoute);
            int indexToChange = ThreadLocalRandom.current().nextInt(newRoute.size());
            newRoute.set(indexToChange, vuelos[ThreadLocalRandom.current().nextInt(vuelos.length)]);

            // Calculate the cost difference between the new and current routes
            double currentCost = calculateTotalCost(currentRoute);
            double newCost = calculateTotalCost(newRoute);
            double costDifference = newCost - currentCost;

            // Decide if we should accept the new solution
            if (costDifference < 0 || Math.exp(-costDifference / temperature) > Math.random()) {
                currentRoute = newRoute;
            }

            // Cool down the system
            temperature *= 1 - coolingRate;
        }

        current.printRutasTXT("result.txt");
	}
}
