import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GrafoVuelos {
    Map<Aeropuerto, List<Vuelo>> grafo;
    int rutaId;

    GrafoVuelos() {
        this.grafo = new HashMap<>();
        this.rutaId = 0;
    }

    void agregarVuelo(Vuelo vuelo) {
        if (!grafo.containsKey(vuelo.origen)) {
            grafo.put(vuelo.origen, new ArrayList<>());
        }
        grafo.get(vuelo.origen).add(vuelo);
    }

    List<Ruta> buscarTodasLasRutas(String fechaHoraInicio) {
        List<Ruta> rutasTotal = new ArrayList<>();
        for (Aeropuerto origen : grafo.keySet()) {
            for (Aeropuerto destino : grafo.keySet()) {
                if (!origen.equals(destino)) {
                    List<Ruta> rutas = buscarRutas(origen, destino, fechaHoraInicio);
                    rutasTotal.addAll(rutas);

                    rutas = buscarRutas(destino, origen, fechaHoraInicio);
                    rutasTotal.addAll(rutas);
                }
            }
        }
        return rutasTotal;
    }

    private List<Ruta> buscarRutas(Aeropuerto origen, Aeropuerto destino, String fechaHoraInicio) {
        List<Ruta> rutas = new ArrayList<>();
        buscarRutasDFS(origen, destino, fechaHoraInicio, new Ruta("Ruta-" + rutaId), new HashSet<>(), rutas);
        return rutas;
    }

    private void buscarRutasDFS(Aeropuerto actual, Aeropuerto destino, String fechaHoraActual, Ruta rutaActual,
                                Set<Aeropuerto> ciudadesVisitadas, List<Ruta> rutas) {
        if (actual.equals(destino)) {
            Ruta nuevaRuta = new Ruta("Ruta-" + rutaId++);
            nuevaRuta.vuelos.addAll(rutaActual.vuelos);
            rutas.add(nuevaRuta);
            return;
        }

        if (!grafo.containsKey(actual)) {
            return;
        }

        for (Vuelo vuelo : grafo.get(actual)) {
            if (esPosterior(fechaHoraActual, vuelo.fechaPartida) && !ciudadesVisitadas.contains(actual)) {
                rutaActual.agregarVuelo(vuelo);
                ciudadesVisitadas.add(actual);
                buscarRutasDFS(vuelo.destino, destino, vuelo.fechaLlegada, rutaActual, ciudadesVisitadas, rutas);
                rutaActual.vuelos.remove(rutaActual.vuelos.size() - 1);
                ciudadesVisitadas.remove(actual);
            }
        }
    }

    private boolean esPosterior(String fechaHoraActual, String fechaHoraVuelo) {
        String formato = "yyyy-MM-dd HH:mm";
        try {
            Date fechaActual = new Date();
            Date fechaVuelo = new Date();
            fechaActual = new SimpleDateFormat(formato).parse(fechaHoraActual);
            fechaVuelo = new SimpleDateFormat(formato).parse(fechaHoraVuelo);
            return fechaVuelo.after(fechaActual);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}
