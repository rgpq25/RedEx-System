package Clases;

import java.util.*;

public class GrafoVuelos {
    private Map<String, List<Vuelo>> grafo = new HashMap<>();
    private int rutaId = 0;

    public GrafoVuelos(ArrayList<PlanVuelo> planV, Date inicio, Date fin) {
        ArrayList<Vuelo> vuelos = generarVuelos(planV, inicio, fin);
        for (Vuelo vuelo : vuelos) {
            agregarVuelo(vuelo);
        }
    }

    public ArrayList<Vuelo> generarVuelos(ArrayList<PlanVuelo> planesVuelo,
            Date inicio, Date fin) {
        ArrayList<Vuelo> vuelos = new ArrayList<>();

        // Mueve la instancia de Calendar aquí para inicializarla una vez.
        Calendar cal = Calendar.getInstance();
        cal.setTime(inicio);

        Calendar finCal = Calendar.getInstance();
        finCal.setTime(fin);

        while (!cal.after(finCal)) {
            for (PlanVuelo plan : planesVuelo) {

                // Configura la hora de partida según el plan.
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(plan.getHora_ciudad_origen().split(":")[0]));
                cal.set(Calendar.MINUTE, Integer.parseInt(plan.getHora_ciudad_origen().split(":")[1]));
                Date fechaPartida = cal.getTime();

                // Clona 'cal' para configurar la hora de llegada.
                Calendar calLlegada = (Calendar) cal.clone();
                calLlegada.set(Calendar.HOUR_OF_DAY, Integer.parseInt(plan.getHora_ciudad_destino().split(":")[0]));
                calLlegada.set(Calendar.MINUTE, Integer.parseInt(plan.getHora_ciudad_destino().split(":")[1]));
                Date fechaLlegada = calLlegada.getTime();

                // Comprobar si la fecha de llegada es anterior a la de partida y ajustarla si
                // es necesario.
                if (!fechaLlegada.after(fechaPartida)) {
                    calLlegada.add(Calendar.DAY_OF_MONTH, 1); // Añadir un día a la fecha de llegada.
                    fechaLlegada = calLlegada.getTime();
                }

                // Agrega el vuelo a la lista.
                vuelos.add(new Vuelo(plan, fechaPartida, fechaLlegada));
            }

            // Avanza al día siguiente.
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        return vuelos;
    }

    // Agrega un vuelo al grafo
    public void agregarVuelo(Vuelo vuelo) {
        if (!grafo.containsKey(vuelo.getPlan_vuelo().getCiudadOrigen().getId())) {
            grafo.put(vuelo.getPlan_vuelo().getCiudadOrigen().getId(), new ArrayList<>());
        }
        grafo.get(vuelo.getPlan_vuelo().getCiudadOrigen().getId()).add(vuelo);
    }

    // Imprime todos los vuelos en el grafo
    public void imprimirVuelos() {
        for (Map.Entry<String, List<Vuelo>> entrada : grafo.entrySet()) {
            System.out.println("Desde el aeropuerto: " + entrada.getKey());
            for (Vuelo vuelo : entrada.getValue()) {
                System.out.println(
                        "  Vuelo a: " + vuelo.getPlan_vuelo().getCiudadDestino().getId() + ", ID: " + vuelo.getId());
            }
        }
    }

    // Busca todas las rutas desde todos los aeropuertos hacia todos los otros
    // aeropuertos
    public List<PlanRuta> buscarTodasLasRutas(Date fechaHoraInicio) {
        List<PlanRuta> rutasTotal = new ArrayList<>();
        for (String origen : grafo.keySet()) {
            for (String destino : grafo.keySet()) {
                if (!origen.equals(destino)) {
                    List<PlanRuta> rutas = buscarRutas(origen, destino, fechaHoraInicio);

                    rutasTotal.addAll(rutas);
                }
            }
        }
        return rutasTotal;
    }

    // Busca rutas de un origen a un destino comenzando en una fecha y hora
    // específicas
    public List<PlanRuta> buscarRutas(String origen, String destino, Date fechaHoraInicio) {
        List<PlanRuta> rutas = new ArrayList<>();
        buscarRutasDFS(origen, destino, fechaHoraInicio, new PlanRuta(), new HashSet<>(), rutas);
        return rutas;
    }

    // Método DFS para encontrar rutas
    private void buscarRutasDFS(String actual, String destino, Date fechaHoraActual, PlanRuta rutaActual,
            Set<String> aeropuertosVisitados, List<PlanRuta> rutas) {
        if (actual.equals(destino)) {
            // Clonar la lista de vuelos para la nueva ruta
            ArrayList<Vuelo> vuelosClonados = new ArrayList<>(rutaActual.getVuelos());
            PlanRuta nuevaRuta = new PlanRuta();
            nuevaRuta.setVuelos(vuelosClonados);
            rutas.add(nuevaRuta);
            return;
        }

        if (!grafo.containsKey(actual)) {
            return;
        }

        for (Vuelo vuelo : grafo.get(actual)) {
            if (fechaHoraActual.before(vuelo.getFecha_llegada())
                    && !aeropuertosVisitados.contains(vuelo.getPlan_vuelo().getCiudadDestino().getId())) {
                rutaActual.getVuelos().add(vuelo);
                aeropuertosVisitados.add(actual);
                buscarRutasDFS(vuelo.getPlan_vuelo().getCiudadDestino().getId(), destino, vuelo.getFecha_llegada(),
                        rutaActual, aeropuertosVisitados, rutas);
                // Remover el último vuelo agregado para seguir con el backtracking
                rutaActual.getVuelos().remove(rutaActual.getVuelos().size() - 1);
                aeropuertosVisitados.remove(actual);
            }
        }
    }
}
