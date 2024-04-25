package Clases;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class GrafoVuelos {
    private HashMap<Ubicacion, ArrayList<Vuelo>> grafo = new HashMap<>();
    private int rutaId = 0;
    private Date fecha_inicio;

    public GrafoVuelos(ArrayList<PlanVuelo> planV, Date inicio, Date fin) {
        fecha_inicio = inicio;
        ArrayList<Vuelo> vuelos = generarVuelos(planV, inicio, fin);
        for (Vuelo vuelo : vuelos) {
            agregarVuelo(vuelo);
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

    // Agrega un vuelo al grafo
    public void agregarVuelo(Vuelo vuelo) {
        if (!grafo.containsKey(vuelo.getPlan_vuelo().getCiudadOrigen())) {
            grafo.put(vuelo.getPlan_vuelo().getCiudadOrigen(), new ArrayList<>());
        }
        grafo.get(vuelo.getPlan_vuelo().getCiudadOrigen()).add(vuelo);
    }

    // Imprime todos los vuelos en el grafo
    public void imprimirVuelos() {
        for (HashMap.Entry<Ubicacion, ArrayList<Vuelo>> entrada : grafo.entrySet()) {
            System.out.println("Desde el aeropuerto: " + entrada.getKey());
            for (Vuelo vuelo : entrada.getValue()) {
                System.out.println(
                        "  Vuelo a: " + vuelo.getPlan_vuelo().getCiudadDestino().getId() + ", ID: " + vuelo.getId());
            }
        }
    }

    // Busca todas las rutas desde todos los aeropuertos hacia todos los otros
    // aeropuertos

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd - HH:mm:ss");

    // Ajuste de la función para retornar la estructura compleja
    public HashMap<String, HashMap<Duracion, ArrayList<PlanRuta>>> buscarTodasLasRutas() {
        ContadorID.reiniciar();
        System.out.println("Buscando rutas");
        HashMap<String, HashMap<Duracion, ArrayList<PlanRuta>>> rutasTotal = new HashMap<>();
        int total_rutas = 0;
        for (Ubicacion origen : grafo.keySet()) {
            for (Ubicacion destino : grafo.keySet()) {
                if (!origen.equals(destino)) {
                    ArrayList<PlanRuta> rutas = buscarRutas(origen, destino, fecha_inicio);
                    total_rutas += rutas.size();
                    String claveOrigenDestino = origen + "-" + destino;

                    for (PlanRuta ruta : rutas) {
                        Date inicioRuta = ruta.getInicio();
                        Date finRuta = ruta.getFin();

                        // Formatear las fechas de inicio y fin
                        Duracion fechas = new Duracion(inicioRuta, finRuta);

                        // Asegurarse de que los HashMaps estén inicializados
                        rutasTotal.putIfAbsent(claveOrigenDestino, new HashMap<>());
                        rutasTotal.get(claveOrigenDestino).putIfAbsent(fechas, new ArrayList<>());

                        // Agregar la ruta al ArrayList correspondiente
                        rutasTotal.get(claveOrigenDestino).get(fechas).add(ruta);
                    }
                }
            }
        }
        System.out.println("Se encontraron " + rutasTotal.size() + " pares de origen-destino con rutas.");
        System.out.println("Se encontraron " + total_rutas + " rutas en total.");
        return rutasTotal;
    }

    // Busca rutas de un origen a un destino comenzando en una fecha y hora
    // específicas
    public ArrayList<PlanRuta> buscarRutas(Ubicacion origen, Ubicacion destino, Date fechaHoraInicio) {
        boolean continental = origen.getContinente().equals(destino.getContinente());
        int tamanho_max = 0;
        if (continental) {
            tamanho_max = 3;
        } else {
            tamanho_max = 5;
        }
        ArrayList<PlanRuta> rutas = new ArrayList<>();
        buscarRutasDFS(origen, destino, fechaHoraInicio, new PlanRuta(), new HashSet<>(), rutas, continental,
                tamanho_max);
        return rutas;
    }

    // Método DFS para encontrar rutas
    private void buscarRutasDFS(Ubicacion actual, Ubicacion destino, Date fechaHoraActual, PlanRuta rutaActual,
            Set<Ubicacion> aeropuertosVisitados, ArrayList<PlanRuta> rutas, boolean continental, int tamanho_max) {
        if (actual.equals(destino)) {
            // Clonar la lista de vuelos para la nueva ruta
            ArrayList<Vuelo> vuelosClonados = new ArrayList<>(rutaActual.getVuelos());
            PlanRuta nuevaRuta = new PlanRuta();
            nuevaRuta.setVuelos(vuelosClonados);
            rutas.add(nuevaRuta);
            return;
        }

        if (aeropuertosVisitados.size() >= tamanho_max) {
            return;
        }

        // Asegurar que los vuelos están ordenados por fecha de salida
        ArrayList<Vuelo> vuelosOrdenados = new ArrayList<>(grafo.get(actual));
        Collections.sort(vuelosOrdenados, Comparator.comparing(Vuelo::getFecha_salida));

        for (Vuelo vuelo : vuelosOrdenados) {
            /*
             * if (continental
             * && !vuelo.getPlan_vuelo().getCiudadDestino().getContinente().equals(actual.
             * getContinente())) {
             * continue;
             * }
             */
            if (fechaHoraActual.before(vuelo.getFecha_salida())) {
                // Establecer fechaInicio si es la primera adición de vuelo a la ruta
                Date fechaInicio = rutaActual.getInicio();
                if (fechaInicio == null) {
                    fechaInicio = vuelo.getFecha_salida();
                }
                // Calcular la diferencia de tiempo desde el inicio hasta la fecha de salida del
                // vuelo actual
                long duracionRuta = vuelo.getFecha_llegada().getTime() - fechaInicio.getTime();
                long duracionRutaHoras = (duracionRuta + 3599999) / 3600000;
                long limite;
                if (continental) {
                    limite = 30;
                } else {
                    limite = 60;
                }
                if (duracionRutaHoras <= limite) {
                    if (!aeropuertosVisitados.contains(vuelo.getPlan_vuelo().getCiudadDestino())) {
                        rutaActual.getVuelos().add(vuelo);
                        aeropuertosVisitados.add(actual);
                        buscarRutasDFS(vuelo.getPlan_vuelo().getCiudadDestino(), destino, vuelo.getFecha_llegada(),
                                rutaActual, aeropuertosVisitados, rutas, continental, tamanho_max);
                        // Backtracking
                        rutaActual.getVuelos().remove(rutaActual.getVuelos().size() - 1);
                        aeropuertosVisitados.remove(actual);
                    }
                } else {
                    // Si se excede la duración máxima, terminar la búsqueda desde este punto
                    break;
                }
            }
        }
        // Restablecer fechaInicio a null si se eliminan todos los vuelos en el
        // backtracking

    }

}
