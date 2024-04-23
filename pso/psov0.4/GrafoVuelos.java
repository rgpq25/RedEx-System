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
import java.util.*;
import Clases.Vuelo;
import Clases.Aeropuerto;
import Clases.Ubicacion;
import Clases.PlanVuelo;
import Clases.PlanRuta;
import Clases.Paquete;

public class GrafoVuelos {
    private Map<String, List<Vuelo>> grafo= new HashMap<>();
    private int rutaId = 0;
    private Date fecha_inicio;

    public GrafoVuelos(ArrayList<PlanVuelo> planV, Date inicio, Date fin) {
        fecha_inicio = inicio;
        ArrayList<Vuelo> vuelos = generarVuelos(planV, inicio, fin);
        for (Vuelo vuelo : vuelos) {
            agregarVuelo(vuelo);
        }
    }


    public void imprimirRutas() {
        ArrayList<PlanRuta> rutas = buscarTodasLasRutas();
        int id = 1;
        for (PlanRuta ruta : rutas) {
            System.out.println("Ruta " + id++);
            for (Vuelo vuelo : ruta.getVuelos()) {
                System.out.println(
                        "  Vuelo " + vuelo.getId() + " desde " + vuelo.getPlan_vuelo().getCiudadOrigen().getId() + " a "
                                + vuelo.getPlan_vuelo().getCiudadDestino().getId());
            }
        }
    }

    public Date hora_local_a_fecha_generica(Date fechaGMT0, String hora_local, int diferencia_horaria) {
        Calendar cal = Calendar.getInstance();
        int horaGenerica = Integer.parseInt(hora_local.split(":")[0]);
        cal.setTime(fechaGMT0);

        if (horaGenerica - diferencia_horaria < 0) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
            horaGenerica = 24 + horaGenerica + diferencia_horaria;
        } else if (horaGenerica - diferencia_horaria >= 24) {
            cal.add(Calendar.DAY_OF_MONTH, +1);
            horaGenerica = horaGenerica + diferencia_horaria - 24;
        } else {
            horaGenerica = horaGenerica + diferencia_horaria;
        }

        cal.set(Calendar.HOUR_OF_DAY, horaGenerica);
        cal.set(Calendar.MINUTE, Integer.parseInt(hora_local.split(":")[1]));
        return cal.getTime();
    }

    public GrafoVuelos(ArrayList<PlanVuelo> planV, ArrayList<Paquete> paquetes) {
        // Encuentra el paquete con la fecha de recepción más temprana
        Optional<Paquete> minRecepcionPaquete = paquetes.stream()
                .min(Comparator.comparing(p -> p.getFecha_recepcion()));

        // Encuentra el paquete con la fecha de entrega máxima más tardía
        Optional<Paquete> maxEntregaPaquete = paquetes.stream()
                .max(Comparator.comparing(p -> p.getFecha_maxima_entrega()));

        Date inicio = minRecepcionPaquete.map(p -> p.getFecha_recepcion()).orElse(new Date());
        Date fin = maxEntregaPaquete.map(p -> p.getFecha_maxima_entrega()).orElse(new Date());

        fecha_inicio = inicio;

        ArrayList<Vuelo> vuelos = generarVuelos(planV, inicio, fin);
        for (Vuelo vuelo : vuelos) {
            agregarVuelo(vuelo);
        }
        // System.out.println("Termino la generacion de vuelos");
    }

    public GrafoVuelos() {
    }

    private ArrayList<Vuelo> generarVuelos(ArrayList<PlanVuelo> planesVuelo, Date inicio, Date fin) {
        ArrayList<Vuelo> vuelos = new ArrayList<>();

        // Inicializa el calendario para la fecha de inicio.
        Calendar cal = Calendar.getInstance();
        cal.setTime(inicio);

        Calendar finCal = Calendar.getInstance();
        finCal.setTime(fin);

        while (!cal.after(finCal)) {
            for (PlanVuelo plan : planesVuelo) {
                // Usa la función hora_local_a_fecha_generica para configurar la hora de partida
                // en GMT+0.
                Date fechaPartida = hora_local_a_fecha_generica(cal.getTime(), plan.getHora_ciudad_origen(),
                        Funciones.stringGmt2Int(plan.getCiudadOrigen().getZonaHoraria()));

                // Clona 'cal' para configurar la hora de llegada.
                Calendar calLlegada = (Calendar) cal.clone();
                Date fechaLlegada = hora_local_a_fecha_generica(calLlegada.getTime(), plan.getHora_ciudad_destino(),
                        Funciones.stringGmt2Int(plan.getCiudadDestino().getZonaHoraria()));

                // Ajusta la fecha de llegada si es necesario.
                if (!fechaLlegada.after(fechaPartida)) {
                    calLlegada.add(Calendar.DAY_OF_MONTH, 1);
                    fechaLlegada = calLlegada.getTime();
                }
                /*
                 * if (!plan.es_continental()) {
                 * long duracion = (fechaLlegada.getTime() - fechaPartida.getTime()) / (1000 *
                 * 60 * 60); // Duración en
                 * // horas
                 * if (duracion < 12 || duracion > 24) {
                 * System.out.println("Caso no contemplado: " + plan.toString());
                 * continue;
                 * }
                 * }
                 * if (plan.es_continental()) {
                 * long duracion = (fechaLlegada.getTime() - fechaPartida.getTime()) / (1000 *
                 * 60 * 60); // Duración en
                 * // horas
                 * if (duracion < 0 || duracion > 12) {
                 * System.out.println("Caso no contemplado: " + plan.toString());
                 * continue;
                 * }
                 * }
                 */
                // Añade el vuelo a la lista con la fecha y hora en GMT+0.
                vuelos.add(new Vuelo(plan, fechaPartida, fechaLlegada));
            }
            // Avanza al día siguiente.
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        return vuelos;
    }

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
    public ArrayList<PlanRuta> buscarTodasLasRutas() {
        ArrayList<PlanRuta> rutasTotal = new ArrayList<>();
        for (String origen : grafo.keySet()) {
            for (String destino : grafo.keySet()) {
                if (!origen.equals(destino)) {
                    System.out.println("Buscando");
                    ArrayList<PlanRuta> rutas = buscarRutas(origen, destino, fecha_inicio);

                    rutasTotal.addAll(rutas);
                    System.out.println("Se agrego ruta desde " + origen + " a " + destino + " con " + rutas.size()
                            + " rutas encontradas");
                }
            }
        }
        return rutasTotal;
    }

 // Busca rutas de un origen a un destino comenzando en una fecha y hora
    // específicas
    public ArrayList<PlanRuta> buscarRutas(String origen, String destino, Date fechaHoraInicio) {
        ArrayList<PlanRuta> rutas = new ArrayList<>();
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

        if (!grafo.containsKey(actual) || aeropuertosVisitados.size() >= 5) {
            return; /*Aca esta el cambio*/ 
        }


        for (Vuelo vuelo : grafo.get(actual)) {
            if (fechaHoraActual.before(vuelo.getFecha_salida())
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
