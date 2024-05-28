package pucp.e3c.redex_back.model;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import pucp.e3c.redex_back.service.VueloService;

public class GrafoVuelos {
    private HashMap<Ubicacion, TreeMap<Date, Vuelo>> grafo = new HashMap<>();
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
                .min(Comparator.comparing(p -> p.getEnvio().getFechaRecepcion()));

        // Encuentra el paquete con la fecha de entrega máxima más tardía
        Optional<Paquete> maxEntregaPaquete = paquetes.stream()
                .max(Comparator.comparing(p -> p.getEnvio().getFechaLimiteEntrega()));

        Date inicio = minRecepcionPaquete.map(p -> p.getEnvio().getFechaRecepcion()).orElse(new Date());
        Date fin = maxEntregaPaquete.map(p -> p.getEnvio().getFechaLimiteEntrega()).orElse(new Date());

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
                Date fechaPartida = hora_local_a_fecha_generica(cal.getTime(), plan.getHoraCiudadOrigen(),
                        Funciones.stringGmt2Int(plan.getCiudadOrigen().getZonaHoraria()));

                // Clona 'cal' para configurar la hora de llegada.
                Calendar calLlegada = (Calendar) cal.clone();
                Date fechaLlegada = hora_local_a_fecha_generica(calLlegada.getTime(), plan.getHoraCiudadDestino(),
                        Funciones.stringGmt2Int(plan.getCiudadDestino().getZonaHoraria()));
                calLlegada.setTime(fechaLlegada);
                // Ajusta la fecha de llegada si es necesario.
                if (!fechaLlegada.after(fechaPartida)) {
                    calLlegada.add(Calendar.HOUR_OF_DAY, 24);
                    fechaLlegada = calLlegada.getTime();
                }
                // Vuelo vuelo = new Vuelo(plan, fechaPartida, fechaLlegada);
                Vuelo vuelo = new Vuelo();
                vuelo.fillData2(plan, fechaPartida, fechaLlegada);
                vuelos.add(vuelo);
            }
            // Avanza al día siguiente.
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        return vuelos;
    }

    // Agrega un vuelo al grafo
    public void agregarVuelo(Vuelo vuelo) {
        Ubicacion ciudadOrigen = vuelo.getPlanVuelo().getCiudadOrigen();
        if (!this.grafo.containsKey(ciudadOrigen)) {
            TreeMap<Date, Vuelo> nuevoMap = new TreeMap<>();
            grafo.put(ciudadOrigen, nuevoMap);
        }
        grafo.get(ciudadOrigen).put(vuelo.getFechaSalida(), vuelo);
    }

    // Imprime todos los vuelos en el grafo
    public void imprimirVuelos() {
        for (HashMap.Entry<Ubicacion, TreeMap<Date, Vuelo>> entrada : grafo.entrySet()) {
            System.out.println("Desde el aeropuerto: " + entrada.getKey());
            for (HashMap.Entry<Date, Vuelo> vueloEntry : entrada.getValue().entrySet()) {
                Vuelo vuelo = vueloEntry.getValue();
                System.out.println(
                        "  Vuelo a: " + vuelo.getPlanVuelo().getCiudadDestino().getId() + ", ID: " + vuelo.getId());
            }
        }
    }

    public HashMap<String, ArrayList<PlanRutaNT>> buscarTodasLasRutas()
            throws InterruptedException, ExecutionException {
        ContadorID.reiniciar();
        System.out.println("Buscando rutas");
        HashMap<String, ArrayList<PlanRutaNT>> rutasTotal = new HashMap<>();
        AtomicInteger totalRutas = new AtomicInteger(0); // Usar AtomicInteger para manejar la suma en un entorno
                                                         // multihilo
        AtomicInteger total = new AtomicInteger(0);
        int nThreads = Runtime.getRuntime().availableProcessors(); // Número de hilos basado en los procesadores
                                                                   // disponibles
        ExecutorService executor = Executors.newFixedThreadPool(nThreads); // Crear un pool de hilos

        List<Future<HashMap<String, ArrayList<PlanRutaNT>>>> futures = new ArrayList<>();

        for (Ubicacion origen : grafo.keySet()) {
            for (Ubicacion destino : grafo.keySet()) {
                if (!origen.equals(destino)) {
                    // Envío de la tarea de búsqueda de rutas para ejecución en paralelo
                    Future<HashMap<String, ArrayList<PlanRutaNT>>> future = executor.submit(() -> {
                        ArrayList<PlanRutaNT> rutas = buscarRutas(origen, destino, fecha_inicio);
                        total.getAndAdd(1);
                        // System.out.print(total.get() + " ");
                        HashMap<String, ArrayList<PlanRutaNT>> result = new HashMap<>();
                        String claveOrigenDestino = origen.getId() + "-" + destino.getId();
                        result.put(claveOrigenDestino, rutas);
                        return result;
                    });
                    futures.add(future);
                }
            }
        }

        // Recuperar y combinar los resultados de todas las tareas futuras
        for (Future<HashMap<String, ArrayList<PlanRutaNT>>> future : futures) {
            HashMap<String, ArrayList<PlanRutaNT>> resultadoParcial = future.get();
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
    public ArrayList<PlanRutaNT> buscarRutas(Ubicacion origen, Ubicacion destino, Date fechaHoraInicio) {
        boolean continental = origen.getContinente().equals(destino.getContinente());
        int tamanho_max = 0;
        if (continental) {
            tamanho_max = 2;
        } else {
            tamanho_max = 3;
        }
        ArrayList<PlanRutaNT> rutas = new ArrayList<>();
        // long startTime = System.nanoTime();
        buscarRutasDFS(origen, destino, fechaHoraInicio, new PlanRutaNT(), new HashSet<>(), new HashSet<>(), rutas,
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
    private void buscarRutasDFS(Ubicacion actual, Ubicacion destino, Date fechaHoraActual, PlanRutaNT rutaActual,
            Set<String> aeropuertosVisitados, Set<String> continentesVisitados, ArrayList<PlanRutaNT> rutas,
            boolean continental, int tamanho_max) {
        if (actual.getId().equals(destino.getId())) {
            // Clonar la lista de vuelos para la nueva ruta
            ArrayList<Vuelo> vuelosClonados = new ArrayList<>(rutaActual.getVuelos());
            PlanRutaNT nuevaRuta = new PlanRutaNT();
            nuevaRuta.setVuelos(vuelosClonados);
            rutas.add(nuevaRuta);
            return;
        }

        if (aeropuertosVisitados.size() > tamanho_max) {
            return;
        }

        // Asegurar que los vuelos están ordenados por fecha de salida
        TreeMap<Date, Vuelo> vuelosOrdenados = new TreeMap<Date, Vuelo>(grafo.get(actual));

        for (Date fecha : vuelosOrdenados.keySet()) {
            Vuelo vuelo = vuelosOrdenados.get(fecha);
            /*
             * if ((!vuelo.getPlanVuelo().getCiudadDestino().getContinente().equals(actual.
             * getContinente()) &&
             * continental)) {
             * continue;
             * }
             */
            if (fechaHoraActual.before(vuelo.getFechaSalida()) &&
                    !aeropuertosVisitados.contains(vuelo.getPlanVuelo().getCiudadDestino().getId())
                    && !continentesVisitados.contains(vuelo.getPlanVuelo().getCiudadDestino().getContinente())) {
                // Establecer fechaInicio si es la primera adición de vuelo a la ruta
                Date fechaInicio = rutaActual.getInicio();
                if (fechaInicio == null) {
                    fechaInicio = vuelo.getFechaSalida();
                }
                // Calcular la diferencia de tiempo desde el inicio hasta la fecha de salida del
                // vuelo actual
                long duracionRuta = vuelo.getFechaLlegada().getTime() - fechaInicio.getTime();
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
                    buscarRutasDFS(vuelo.getPlanVuelo().getCiudadDestino(), destino, vuelo.getFechaLlegada(),
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

    public ArrayList<Vuelo> obtenerVuelosEntreFechas(Ubicacion ciudadOrigen, Date fechaInicio, Date fechaFin) {
        if (grafo.containsKey(ciudadOrigen)) {
            TreeMap<Date, Vuelo> vuelos = grafo.get(ciudadOrigen);
            // Creamos una lista a partir de la colección de vuelos en el rango dado
            ArrayList<Vuelo> listaVuelos = new ArrayList<>(vuelos.subMap(fechaInicio, true, fechaFin, true).values());
            return listaVuelos;
        }
        return new ArrayList<>(); // Devolver una lista vacía en lugar de null si la ciudad no está en el grafo
    }

    private PlanRutaNT buscarRutaAleatoriaDFS(Ubicacion actual, Ubicacion destino, Date fechaHoraActual,
            PlanRutaNT rutaActual, Set<String> aeropuertosVisitados, boolean continental, Paquete paquete) {

        if (actual.getId().equals(destino.getId())) {
            PlanRutaNT nuevaRuta = new PlanRutaNT();
            nuevaRuta.setVuelos(rutaActual.getVuelos());
            return nuevaRuta;
        }

        ArrayList<Vuelo> vuelosPosibles = obtenerVuelosEntreFechas(actual, paquete.getEnvio().getFechaRecepcion(),
                paquete.getEnvio().getFechaLimiteEntrega());
        Collections.shuffle(vuelosPosibles);

        for (Vuelo vuelo : vuelosPosibles) {
            if (fechaHoraActual.before(vuelo.getFechaSalida()) &&
                    !aeropuertosVisitados.contains(vuelo.getPlanVuelo().getCiudadDestino().getId())) {
                Date fechaInicio = rutaActual.getInicio();
                if (fechaInicio == null) {
                    fechaInicio = vuelo.getFechaSalida();
                }
                // Calcular la diferencia de tiempo desde el inicio hasta la fecha de salida del
                // vuelo actual
                long duracionRuta = vuelo.getFechaLlegada().getTime() - fechaInicio.getTime();
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
                    PlanRutaNT result = buscarRutaAleatoriaDFS(vuelo.getPlanVuelo().getCiudadDestino(), destino,
                            vuelo.getFechaLlegada(), rutaActual, aeropuertosVisitados, continental, paquete);
                    if (result != null) {
                        return result; // Ruta encontrada, retornarla.
                    }
                    // Backtracking
                    rutaActual.getVuelos().remove(rutaActual.getVuelos().size() - 1);
                    aeropuertosVisitados.remove(actual.getId());
                }

            }
        }

        return null; // No se encontró ruta, retornar null.
    }

    public ArrayList<PlanRutaNT> generarRutasParaPaquetes(ArrayList<Paquete> paquetes, VueloService vueloService) {
        ArrayList<PlanRutaNT> rutas = new ArrayList<>();
        for (Paquete paquete : paquetes) {
            Set<String> aeropuertosVisitados = new HashSet<>();
            PlanRutaNT rutaTomada = obtenerRutaHastaAeropuertoActual(paquete, vueloService);
            PlanRutaNT rutaEncontrada = buscarRutaAleatoriaDFS(paquete.getAeropuertoActual().getUbicacion(),
                    paquete.getEnvio().getUbicacionDestino(),
                    paquete.getEnvio().getFechaRecepcion(), rutaTomada,
                    aeropuertosVisitados,
                    paquete.getEnvio().getUbicacionOrigen().getId()
                            .equals(paquete.getEnvio().getUbicacionDestino().getId()),
                    paquete);
            if (rutaEncontrada == null) {
                throw new IllegalStateException("No se pudo encontrar una ruta para el paquete "
                        + paquete.toString());

            }
            rutas.add(rutaEncontrada);
        }
        return rutas;
    }

    public PlanRutaNT obtenerRutaHastaAeropuertoActual(Paquete paquete, VueloService vueloService) {
        Ubicacion ubicacionActual = paquete.getAeropuertoActual().getUbicacion();

        PlanRutaNT rutaHastaActual = new PlanRutaNT();
        ArrayList<Vuelo> vuelosTomados = new ArrayList<>();
        ArrayList<Vuelo> vuelosPaquete = vueloService.findVuelosByPaqueteId(paquete.getId());
        if (vuelosPaquete == null) {
            return rutaHastaActual;
        }
        // Iterar sobre los vuelos en el plan de ruta completo
        for (Vuelo vuelo : vuelosPaquete) {
            vuelosTomados.add(vuelo); // Agregar vuelo a la lista de vuelos tomados
            // Verificar si el destino del vuelo es el aeropuerto actual
            if (vuelo.getPlanVuelo().getCiudadDestino().getId().equals(ubicacionActual.getId())) {
                break; // Si se alcanza el aeropuerto actual, detener la iteración
            }
        }

        rutaHastaActual.setVuelos(vuelosTomados);
        return rutaHastaActual;
    }
}
