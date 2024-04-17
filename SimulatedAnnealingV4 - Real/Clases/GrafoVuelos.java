package Clases;

import java.util.*;

public class GrafoVuelos {
    private Map<String, List<Vuelo>> grafo = new HashMap<>();
    private int rutaId = 0;

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
        buscarRutasDFS(origen, destino, fechaHoraInicio, new PlanRuta(rutaId++), new HashSet<>(), rutas);
        return rutas;
    }

    // Método DFS para encontrar rutas
    private void buscarRutasDFS(String actual, String destino, Date fechaHoraActual, PlanRuta rutaActual,
            Set<String> aeropuertosVisitados, List<PlanRuta> rutas) {
        if (actual.equals(destino)) {
            // Clonar la lista de vuelos para la nueva ruta
            ArrayList<Vuelo> vuelosClonados = new ArrayList<>(rutaActual.getVuelos());
            PlanRuta nuevaRuta = new PlanRuta(rutaId++);
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
