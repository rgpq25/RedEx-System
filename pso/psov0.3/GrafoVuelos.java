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

import Clases.Vuelo;
import Clases.Aeropuerto;
import Clases.Ubicacion;

public class GrafoVuelos {
    Map<Ubicacion, List<Vuelo>> grafo;
    int rutaId;

    GrafoVuelos() {
        this.grafo = new HashMap<>();
        this.rutaId = 0;
    }

    void agregarVuelo(Vuelo vuelo) {
        if (!grafo.containsKey(vuelo.getPlan_vuelo().getCiudadOrigen())) {
            grafo.put(vuelo.getPlan_vuelo().getCiudadOrigen(), new ArrayList<>());
        }
        grafo.get(vuelo.getPlan_vuelo().getCiudadOrigen()).add(vuelo);
    }

    List<Ruta> buscarTodasLasRutas(Date fechaHoraInicio) {
        List<Ruta> rutasTotal = new ArrayList<>();
        for (Ubicacion origen : grafo.keySet()) {
            for (Ubicacion destino : grafo.keySet()) {
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

    private List<Ruta> buscarRutas(Ubicacion origen, Ubicacion destino, Date fechaHoraInicio) {
        List<Ruta> rutas = new ArrayList<>();
        buscarRutasDFS(origen, destino, fechaHoraInicio, new Ruta("Ruta-" + rutaId), new HashSet<>(), rutas);
        return rutas;
    }

    private void buscarRutasDFS(Ubicacion actual, Ubicacion destino, Date fechaHoraActual, Ruta rutaActual,
                                Set<Ubicacion> ciudadesVisitadas, List<Ruta> rutas) {
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
            if (esPosterior(fechaHoraActual, vuelo.getFecha_salida()) && !ciudadesVisitadas.contains(actual)) {
                rutaActual.agregarVuelo(vuelo);
                ciudadesVisitadas.add(actual);
                buscarRutasDFS(vuelo.getPlan_vuelo().getCiudadDestino(), destino, vuelo.getFecha_llegada(), rutaActual, ciudadesVisitadas, rutas);
                rutaActual.vuelos.remove(rutaActual.vuelos.size() - 1);
                ciudadesVisitadas.remove(actual);
            }
        }
    }

    private boolean esPosterior(Date fechaHoraActual, Date fechaHoraVuelo) {
        String formato = "yyyy-MM-dd HH:mm";
        //Date fechaActual = new Date();
        Date fechaActual = fechaHoraActual;
        //Date fechaVuelo = new Date();
        Date fechaVuelo = fechaHoraVuelo;
        //fechaActual = new SimpleDateFormat(formato).parse(fechaHoraActual);
        //fechaVuelo = new SimpleDateFormat(formato).parse(fechaHoraVuelo);
        return fechaVuelo.after(fechaActual);
    }
}
