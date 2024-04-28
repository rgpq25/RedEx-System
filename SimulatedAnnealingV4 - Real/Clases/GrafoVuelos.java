package Clases;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class GrafoVuelos {
    private HashMap<Ubicacion, ArrayList<Vuelo>> grafo = new HashMap<>();
    private int rutaId = 0;
    private Date fecha_inicio;
    private HashMap<Integer, Vuelo> vuelos_hash = new HashMap<>();

    public HashMap<Integer, Vuelo> getVuelosHash() {
        return vuelos_hash;
    }

    public GrafoVuelos(ArrayList<PlanVuelo> planV, Date inicio, Date fin) {
        fecha_inicio = inicio;
        ArrayList<Vuelo> vuelos = generarVuelos(planV, inicio, fin);
        for (Vuelo vuelo : vuelos) {
            agregarVuelo(vuelo);
        }
    }

    public Date hora_local_a_fecha_generica(Date fechaGMT0, String hora_local, int diferencia_horaria) {
        Date fechaBase = removeTime(fechaGMT0);
        Calendar cal = Calendar.getInstance();
        int horaGenerica = Integer.parseInt(hora_local.split(":")[0]);
        cal.setTime(fechaBase);
        if (horaGenerica - diferencia_horaria < 0) {

            cal.add(Calendar.DAY_OF_MONTH, -1);
            horaGenerica = 24 + horaGenerica + diferencia_horaria;
        } else if (horaGenerica - diferencia_horaria >= 24) {
            cal.add(Calendar.DAY_OF_MONTH, +1);
            horaGenerica = horaGenerica + diferencia_horaria - 24;
        } else {
            horaGenerica = horaGenerica - diferencia_horaria;
        }

        cal.set(Calendar.HOUR_OF_DAY, horaGenerica);
        cal.set(Calendar.MINUTE, Integer.parseInt(hora_local.split(":")[1]));

        return cal.getTime();
    }

    private Date removeTime(Date date) {
        // Obtener una instancia de Calendar y establecer la fecha dada
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Ajustar las horas, minutos, segundos y milisegundos a cero
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Obtener la nueva fecha sin tiempo
        return calendar.getTime();
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
            vuelos_hash.putIfAbsent(vuelo.getId(), vuelo);
            agregarVuelo(vuelo);
        }
        System.out.println("Vuelos generados: " + vuelos.size());

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
                calLlegada.setTime(fechaLlegada);
                // Ajusta la fecha de llegada si es necesario.
                if (!fechaLlegada.after(fechaPartida)) {
                    calLlegada.add(Calendar.HOUR_OF_DAY, 24);
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
                //
                Vuelo vuelo = new Vuelo(plan, fechaPartida, fechaLlegada);
                // System.out.println("Plan Vuelo: " + plan.toString() + ", Vuelo generado " +
                // vuelo.toString());

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

    public HashMap<String, ArrayList<PlanRuta>> buscarTodasLasRutas() throws InterruptedException, ExecutionException {
        ContadorID.reiniciar();
        System.out.println("Buscando rutas");
        HashMap<String, ArrayList<PlanRuta>> rutasTotal = new HashMap<>();
        AtomicInteger totalRutas = new AtomicInteger(0); // Usar AtomicInteger para manejar la suma en un entorno
                                                         // multihilo
        AtomicInteger total = new AtomicInteger(0);
        int nThreads = Runtime.getRuntime().availableProcessors(); // Número de hilos basado en los procesadores
                                                                   // disponibles
        ExecutorService executor = Executors.newFixedThreadPool(nThreads); // Crear un pool de hilos

        List<Future<HashMap<String, ArrayList<PlanRuta>>>> futures = new ArrayList<>();

        for (Ubicacion origen : grafo.keySet()) {
            for (Ubicacion destino : grafo.keySet()) {
                if (!origen.equals(destino)) {
                    // Envío de la tarea de búsqueda de rutas para ejecución en paralelo
                    Future<HashMap<String, ArrayList<PlanRuta>>> future = executor.submit(() -> {
                        ArrayList<PlanRuta> rutas = buscarRutas(origen, destino, fecha_inicio);
                        total.getAndAdd(1);
                        // System.out.print(total.get() + " ");
                        HashMap<String, ArrayList<PlanRuta>> result = new HashMap<>();
                        String claveOrigenDestino = origen.getId() + "-" + destino.getId();
                        result.put(claveOrigenDestino, rutas);
                        return result;
                    });
                    futures.add(future);
                }
            }
        }

        // Recuperar y combinar los resultados de todas las tareas futuras
        for (Future<HashMap<String, ArrayList<PlanRuta>>> future : futures) {
            HashMap<String, ArrayList<PlanRuta>> resultadoParcial = future.get();
            resultadoParcial.forEach((clave, listaRutas) -> {
                rutasTotal.putIfAbsent(clave, new ArrayList<>());
                rutasTotal.get(clave).addAll(listaRutas);
                totalRutas.addAndGet(listaRutas.size()); // Segura en entornos multihilo
            });
        }

        executor.shutdown(); // Cerrar el ExecutorService
        executor.awaitTermination(1, TimeUnit.HOURS); // Esperar a que terminen todas las tareas

        System.out.println("Se encontraron " + rutasTotal.size() + " pares de origen-destino con rutas.");
        System.out.println("Se encontraron " + totalRutas.get() + " rutas en total."); // Usar get() para obtener el
                                                                                       // valor actual
        return rutasTotal;
    }

    // Busca rutas de un origen a un destino comenzando en una fecha y hora
    // específicas
    public ArrayList<PlanRuta> buscarRutas(Ubicacion origen, Ubicacion destino, Date fechaHoraInicio) {
        boolean continental = origen.getContinente().equals(destino.getContinente());
        int tamanho_max = 0;
        if (continental) {
            tamanho_max = 2;
        } else {
            tamanho_max = 3;
        }
        ArrayList<PlanRuta> rutas = new ArrayList<>();
        // long startTime = System.nanoTime();
        buscarRutasDFS(origen, destino, fechaHoraInicio, new PlanRuta(), new HashSet<>(), new HashSet<>(), rutas,
                continental,
                tamanho_max);
        if (rutas.size() == 0) {
            System.out.println("No se encontraron rutas para " + origen.getId() + "-" + destino.getId());
        }
        // long endTime = System.nanoTime();
        // long duration = endTime - startTime;
        // System.out
        // .println("Encontrados " + rutas.size() + " rutas para " + origen.getId() +
        // "-"
        // + destino.getId()

        // + " en " + (float) (duration / 1000000) + " milisegundos");
        return rutas;
    }

    // Método DFS para encontrar rutas
    private void buscarRutasDFS(Ubicacion actual, Ubicacion destino, Date fechaHoraActual, PlanRuta rutaActual,
            Set<String> aeropuertosVisitados, Set<String> continentesVisitados, ArrayList<PlanRuta> rutas,
            boolean continental, int tamanho_max) {
        if (actual.getId().equals(destino.getId())) {
            // Clonar la lista de vuelos para la nueva ruta
            ArrayList<Vuelo> vuelosClonados = new ArrayList<>(rutaActual.getVuelos());
            PlanRuta nuevaRuta = new PlanRuta();
            nuevaRuta.setVuelos(vuelosClonados);
            rutas.add(nuevaRuta);
            return;
        }

        if (aeropuertosVisitados.size() > tamanho_max) {
            return;
        }

        // Asegurar que los vuelos están ordenados por fecha de salida
        ArrayList<Vuelo> vuelosOrdenados = new ArrayList<>(grafo.get(actual));
        Collections.sort(vuelosOrdenados, Comparator.comparing(Vuelo::getFecha_salida));

        for (Vuelo vuelo : vuelosOrdenados) {

            if ((!vuelo.getPlan_vuelo().getCiudadDestino().getContinente().equals(actual.getContinente()) &&
                    continental)) {
                continue;
            }

            if (fechaHoraActual.before(vuelo.getFecha_salida()) &&
                    !aeropuertosVisitados.contains(vuelo.getPlan_vuelo().getCiudadDestino().getId())
                    && !continentesVisitados.contains(vuelo.getPlan_vuelo().getCiudadDestino().getContinente())) {
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
                    rutaActual.getVuelos().add(vuelo);
                    aeropuertosVisitados.add(actual.getId());
                    continentesVisitados.add(actual.getContinente());
                    buscarRutasDFS(vuelo.getPlan_vuelo().getCiudadDestino(), destino, vuelo.getFecha_llegada(),
                            rutaActual, aeropuertosVisitados, continentesVisitados, rutas, continental,
                            tamanho_max);
                    // Backtracking
                    rutaActual.getVuelos().remove(rutaActual.getVuelos().size() - 1);
                    continentesVisitados.remove(actual.getContinente());
                    aeropuertosVisitados.remove(actual.getId());
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
