package pucp.e3c.redex_back.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import jakarta.persistence.PersistenceException;
import pucp.e3c.redex_back.service.AeropuertoService;
import pucp.e3c.redex_back.service.PaqueteService;
import pucp.e3c.redex_back.service.PlanRutaService;
import pucp.e3c.redex_back.service.PlanRutaXVueloService;
import pucp.e3c.redex_back.service.SimulacionService;
import pucp.e3c.redex_back.service.TimeService;
import pucp.e3c.redex_back.service.VueloService;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

@Component
@EnableAsync
public class Algoritmo {

    private final SimpMessagingTemplate messagingTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(Algoritmo.class);

    RespuestaAlgoritmo ultimaRespuestaOperacionDiaDia;

    private boolean terminarPlanificacionDiaDia;

    private HashMap<Integer, List<Paquete>> paquetes_por_simulacion;

    private ArrayList<Paquete> paquetesProcesadosUltimaSimulacion = new ArrayList<>();

    private ArrayList<Paquete> paquetesSimulacion = new ArrayList<>();

    private ArrayList<PlanRutaNT> planRutasSimulacion = new ArrayList<>();

    // private ArrayList<Paquete> paquetesOpDiaDia = new ArrayList<>();

    // private ArrayList<PlanRutaNT> planRutasOpDiaDia = new ArrayList<>();

    private HashMap<Integer, Paquete> hashTodosPaquetesDiaDia = new HashMap<>();

    private HashMap<Integer, PlanRutaNT> hashPlanRutasNTDiaDia = new HashMap<>();

    private EstadoAlmacen estadoAlmacenOpDiaDia = new EstadoAlmacen();

    private int nConsultasDiaDia = 0;

    private boolean puedeRecibirPaquetesDiaDia = true;

    private boolean operacionDiaDiaActivo = false;

    @Autowired
    private TimeService timeService;

    public Algoritmo(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.ultimaRespuestaOperacionDiaDia = new RespuestaAlgoritmo();
        this.terminarPlanificacionDiaDia = false;
        this.paquetes_por_simulacion = new HashMap<>();
        this.puedeRecibirPaquetesDiaDia = true;
        this.operacionDiaDiaActivo = false;
    }

    public boolean isTerminarPlanificacionDiaDia() {
        return terminarPlanificacionDiaDia;
    }

    public void setTerminarPlanificacionDiaDia(boolean terminarPlanificacionDiaDia) {
        this.terminarPlanificacionDiaDia = terminarPlanificacionDiaDia;
    }

    private Date calcularTiempoSimulacionBack(Simulacion simulacion, long milisegundosDeVentaja) {
        long tiempoActual = new Date().getTime();
        long inicioSistema = simulacion.getFechaInicioSistema().getTime();
        long inicioSimulacion = simulacion.getFechaInicioSim().getTime();
        long milisegundosPausados = simulacion.getMilisegundosPausados();
        long multiplicador = (long) simulacion.getMultiplicadorTiempo();

        return new Date(inicioSimulacion
                + (tiempoActual - inicioSistema - milisegundosPausados + milisegundosDeVentaja) * multiplicador);
    }

    private Date agregarSAPyTA(Date fechaEnSimulacion, int TA, int SAP, double multiplicador) {
        // Supongamos que tienes una fecha, por ejemplo:

        // Crea un objeto Calendar y establece la fecha
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaEnSimulacion);

        // Añade 10 minutos a la fecha
        calendar.add(Calendar.SECOND, (TA + SAP) * (int) multiplicador);

        // Obtiene la nueva fecha con los minutos añadidos
        Date fechaActualizada = calendar.getTime();
        return fechaActualizada;
    }

    public void loopPrincipalDiaADia(
            ArrayList<Aeropuerto> aeropuertos,
            ArrayList<PlanVuelo> planVuelos,
            VueloService vueloService,
            PlanRutaService planRutaService,
            PaqueteService paqueteService,
            PlanRutaXVueloService planRutaXVueloService,
            AeropuertoService aeropuertoService,
            int SA, int TA) {

        // Inicia la operación dia a dia
        this.nConsultasDiaDia = 0;
        this.operacionDiaDiaActivo = true;
        this.puedeRecibirPaquetesDiaDia = true;  
        
        String tipoOperacion = "DIA A DIA";
        boolean replanificar = true;
        this.ultimaRespuestaOperacionDiaDia = new RespuestaAlgoritmo();
        this.estadoAlmacenOpDiaDia = new EstadoAlmacen();
        this.estadoAlmacenOpDiaDia.setAeropuertos(aeropuertos);
        this.ultimaRespuestaOperacionDiaDia.setIniciandoPrimeraPlanificacionDiaDia(true);

        this.hashTodosPaquetesDiaDia = new HashMap<>();
        this.hashPlanRutasNTDiaDia = new HashMap<>();

        // Enviar mensaje de inicio de loop principal
        messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado", "Iniciando loop principal");
        LOGGER.info(tipoOperacion + " Iniciando loop principal");

        // Verificar si hay planes de vuelo para procesar
        if (planVuelos.isEmpty()) {
            LOGGER.error(tipoOperacion + " ERROR: No hay planes de vuelo para procesar.");
            messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado", "Detenido, sin planes vuelo");
            return;
        }

        int i = 0;
        HashMap<Integer, Integer> ocupacionVuelos = new HashMap<>();
        boolean primeraIteracionConPaquetes = true;
        ArrayList<Paquete> paquetesDiaDia = new ArrayList<>();
        GrafoVuelos grafoVuelos = null;
        // HashMap<Integer, Paquete> hashTodosPaquetesDiaDia = new HashMap<>();
        // HashMap<Integer, PlanRutaNT> hashPlanRutasNTDiaDia = new HashMap<>();

        // Loop principal del día a día
        while (true) {
            if (this.terminarPlanificacionDiaDia || !this.ultimaRespuestaOperacionDiaDia.isCorrecta()){
                this.operacionDiaDiaActivo = false;
                this.terminarPlanificacionDiaDia = false;
                LOGGER.info(tipoOperacion + " Terminando loop principal");
                return;
            }                

            long start = System.currentTimeMillis();
            // Date now = new Date();
            Date now = timeService.now();

            // Obtener paquetes para operaciones del día a día
            paquetesDiaDia = paqueteService.findPaquetesOperacionesDiaDia();
            if (paquetesDiaDia != null) {
                LOGGER.info(tipoOperacion + " Se encontraron " + paquetesDiaDia.size()
                        + " paquetes correspondientes a Dia Dia.");
                paquetesDiaDia = actualizarPaquetesDiaDia(paquetesDiaDia, hashPlanRutasNTDiaDia, now, aeropuertoService,
                        paqueteService);
                LOGGER.info(tipoOperacion + " Paquetes actualizados");
            }

            // Agregar paquetes al hash map
            for (Paquete paquete : paquetesDiaDia) {
                hashTodosPaquetesDiaDia.put(paquete.getId(), paquete);
            }

            // Filtrar paquetes por entrega y fecha de recepción
            List<Paquete> paquetesPrimerFiltro = paquetesDiaDia.stream()
                    .filter(p -> !p.isEntregado())
                    .filter(p -> p.obtenerFechaRecepcion().before(now))
                    .collect(Collectors.toList());

            ArrayList<Paquete> paquetes = new ArrayList<>(paquetesPrimerFiltro);
            if (paquetes.isEmpty()) {
                LOGGER.info(tipoOperacion + " No hay paquetes para procesar.");
                messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado", "No hay paquetes para procesar.");
                this.ultimaRespuestaOperacionDiaDia.setIniciandoPrimeraPlanificacionDiaDia(false);

                long end = System.currentTimeMillis();
                long saMillis = SA * 1000 - (end - start);
                if (saMillis < 0)
                    continue;

                try {
                    Thread.sleep(saMillis);
                } catch (Exception e) {
                    System.out.println("Error en sleep");
                }
                continue;
            }
            // add 4 minutes to Date now
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.add(Calendar.MINUTE, 2);
            LOGGER.info(tipoOperacion + " Fecha agregada, inicio Grafo: " + calendar.getTime());

            // Crear o actualizar el grafo de vuelos
            if (primeraIteracionConPaquetes) {
                grafoVuelos = new GrafoVuelos(planVuelos, paquetes, vueloService, null, calendar.getTime());
                // grafoVuelos = new GrafoVuelos(planVuelos, paquetes, vueloService, null);
                if (grafoVuelos.getVuelosHash() == null || grafoVuelos.getVuelosHash().isEmpty()) {
                    LOGGER.error(tipoOperacion + " ERROR: No se generaron vuelos.");
                    messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado", "Detenido, error en generar vuelos");
                    this.operacionDiaDiaActivo = false;
                    return;
                }
                primeraIteracionConPaquetes = false;
            } else {
                grafoVuelos.agregarVuelosParaPaquetesDiaDia(planVuelos, paquetes, vueloService, timeService);
                // grafoVuelos.agregarVuelosParaPaquetes(planVuelos, paquetes, vueloService);
            }

            // Agregar PlanRutaNT para los paquetes que no tienen
            for (Paquete paquete : paquetes) {
                hashPlanRutasNTDiaDia.computeIfAbsent(paquete.getId(), k -> new PlanRutaNT());
            }

            LOGGER.info(tipoOperacion + " Planificacion iniciada");
            messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado", "Planificación iniciada");

            // Ordenar paquetes por fecha de recepción
            paquetes.sort(Comparator.comparing(Paquete::obtenerFechaRecepcion));

            LOGGER.info(tipoOperacion + " Filtro entrega");
            List<Paquete> paquetesProcesarFiltrados = paquetes.stream()
                    .filter(p -> (p.getFechaDeEntrega() == null)
                            || (replanificar && now.before(p.getFechaDeEntrega())))
                    .collect(Collectors.toList());
            LOGGER.info(
                    tipoOperacion + " Fin filtro entrega: " + paquetesProcesarFiltrados.size() + " paquetes restantes");
            // Filtrar paquetes que están volando
            LOGGER.info(tipoOperacion + " Filtrando vuelos");

            ArrayList<PlanRutaNT> temp_planesRutaActuales = new ArrayList<>();

            for (int j = 0; j < paquetesProcesarFiltrados.size(); j++) {
                temp_planesRutaActuales.add(hashPlanRutasNTDiaDia.get(paquetesProcesarFiltrados.get(j).getId()));
            }
            LOGGER.info(tipoOperacion + " Filtrando vuelos Array Temp Construido");
            ArrayList<Paquete> paquetesProcesar = filtrarPaquetesVolando(new ArrayList<>(paquetesProcesarFiltrados),
                    temp_planesRutaActuales, now, TA, 1);
            LOGGER.info(
                    tipoOperacion + " Fin de filtrado de vuelos:" + paquetesProcesar.size() + " paquetes restantes");

            if (paquetesProcesar.isEmpty()) {
                messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado", "No hay paquetes que planificar");
                LOGGER.info(tipoOperacion + " No hay paquetes que planificar");
                long end = System.currentTimeMillis();
                long saMillis = SA * 1000 - (end - start);
                if (saMillis < 0)
                    continue;

                try {
                    Thread.sleep(saMillis);
                } catch (Exception e) {
                    System.out.println("Error en sleep");
                }
                continue;
            }

            LOGGER.info(tipoOperacion + " Se van a PROCESAR " + paquetesProcesar.size() + " paquetes, hasta " + now);

            // Crear listas de rutas y IDs para los paquetes a procesar
            ArrayList<PlanRutaNT> planRutasPaquetesProcesar = new ArrayList<>();
            ArrayList<Integer> idsPaquetesProcesar = new ArrayList<>();
            for (Paquete paquete : paquetesProcesar) {
                PlanRutaNT planRutaNT = hashPlanRutasNTDiaDia.get(paquete.getId());
                planRutasPaquetesProcesar.add(planRutaNT);
                idsPaquetesProcesar.add(paquete.getId());
            }

            // Realizar la planificación
            RespuestaAlgoritmo respuestaAlgoritmo = procesarPaquetes(grafoVuelos, ocupacionVuelos, paquetesProcesar,
                    planRutasPaquetesProcesar, aeropuertos, planVuelos, paquetesProcesar.size(), i, vueloService,
                    planRutaService, null, null, messagingTemplate, tipoOperacion, now, TA);
            if (respuestaAlgoritmo == null) {
                LOGGER.error(tipoOperacion + ": Colpaso en fecha " + now);
                try {
                    PrintWriter writer = new PrintWriter("colapso.txt", "UTF-8");
                    writer.println(tipoOperacion + " Colpaso en fecha " + now);
                    writer.close();
                } catch (Exception e) {
                    System.out.println("Error en escritura de archivo");
                }
                respuestaAlgoritmo = new RespuestaAlgoritmo();
                respuestaAlgoritmo.setCorrecta(false);
                messagingTemplate.convertAndSend("/algoritmo/diaDiaRespuesta", respuestaAlgoritmo);
                messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado", "Colapso");
                this.ultimaRespuestaOperacionDiaDia = respuestaAlgoritmo;
                this.operacionDiaDiaActivo = false;
                return;
            }

            if (respuestaAlgoritmo.isCorrecta() == false) {
                LOGGER.error(tipoOperacion + ": Colpaso en fecha por paquetes " + now);
                // imprimir en un txt
                try {
                    PrintWriter writer = new PrintWriter("colapso.txt", "UTF-8");
                    writer.println(tipoOperacion + " Colpaso en fecha " + now);
                    writer.close();
                } catch (Exception e) {
                    System.out.println("Error en escritura de archivo");
                }
                // respuestaAlgoritmo = new RespuestaAlgoritmo();
                // respuestaAlgoritmo.setCorrecta(false);
                messagingTemplate.convertAndSend("/algoritmo/diaDiaRespuesta", respuestaAlgoritmo);
                messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado", "Colapso");
                this.ultimaRespuestaOperacionDiaDia = respuestaAlgoritmo;
                this.operacionDiaDiaActivo = false;
                return;
            }

            i++;

            // Actualizar el hash map de rutas
            ArrayList<PlanRutaNT> planRutasRespuestaAlgoritmo = respuestaAlgoritmo.getPlanesRutas();
            for (int j = 0; j < idsPaquetesProcesar.size(); j++) {
                hashPlanRutasNTDiaDia.put(idsPaquetesProcesar.get(j), planRutasRespuestaAlgoritmo.get(j));
            }

            LOGGER.info(tipoOperacion + " Planificacion finalizada");

            realizarGuardadoDiaDia(paquetesProcesar, planRutasRespuestaAlgoritmo, paqueteService, planRutaService,
                    vueloService, planRutaXVueloService, hashTodosPaquetesDiaDia);

            HashMap<Integer, Integer> nuevaOcupacion = new HashMap<>();
            // LLena la nuevaOcupacion recorriendo cada vuelo de cada planruta en planRutas
            List<PlanRutaNT> listPlanRutasNT = new ArrayList<>(hashPlanRutasNTDiaDia.values());
            if (listPlanRutasNT != null) {
                for (PlanRutaNT planRutaNT : listPlanRutasNT) {
                    if (planRutaNT.getVuelos() != null) {
                        for (Vuelo vuelo : planRutaNT.getVuelos()) {
                            if (nuevaOcupacion.get(vuelo.getId()) == null) {
                                nuevaOcupacion.put(vuelo.getId(), 1);
                            } else {
                                nuevaOcupacion.put(vuelo.getId(), nuevaOcupacion.get(vuelo.getId()) + 1);
                            }
                        }
                    }

                }
            }

            ocupacionVuelos = nuevaOcupacion;

            HashMap<Integer, Vuelo> hashVuelos = grafoVuelos.getVuelosHash();
            // Revision de colapso en caso de Ocupacion de Vuelos
            boolean colapso = false;
            boolean colapsoVuelos = false;
            boolean colapsoAlmacen = false;
            for (Integer idVuelo : hashVuelos.keySet()) {
                if (ocupacionVuelos.get(idVuelo) == null) {
                    // LOGGER.info(tipoOperacion + " Ocupacion de vuelos vacio.");
                    continue;
                }
                if (ocupacionVuelos.get(idVuelo) > hashVuelos.get(idVuelo).getPlanVuelo().getCapacidadMaxima()) {
                    // colapso = true;
                    colapsoVuelos = true;
                }
            }

            this.puedeRecibirPaquetesDiaDia = false;
            LOGGER.info(tipoOperacion + " Inicio Calculo Estado Almacen, NO PUEDE recibir paquetes");
            ArrayList<Paquete> currentPaquetes = new ArrayList<>();
            ArrayList<PlanRutaNT> currentPlanRutas = new ArrayList<>();
            for (Entry<Integer, Paquete> entry : hashTodosPaquetesDiaDia.entrySet()) {
                currentPaquetes.add(entry.getValue());
                currentPlanRutas.add(hashPlanRutasNTDiaDia.get(entry.getKey()));
            }

            ArrayList<Paquete> nuevosPaquetes = paqueteService.findPaqueteDiaDiaEntreFechas(now, timeService.now());

            /*
             * if (nuevosPaquetes != null) {
             * LOGGER.info(tipoOperacion + " Se encontraron " + nuevosPaquetes.size() +
             * " nuevos paquetes");
             * currentPaquetes.addAll(nuevosPaquetes);
             * currentPlanRutas.addAll(Collections.nCopies(nuevosPaquetes.size(), new
             * PlanRutaNT()));
             * } else {
             * LOGGER.info(tipoOperacion + " No se encontraron nuevos paquetes");
             * }
             */

            EstadoAlmacen estadoAlmacen = new EstadoAlmacen(currentPaquetes, currentPlanRutas,
                    grafoVuelos.getVuelosHash(),
                    ocupacionVuelos, aeropuertos);

            // verificacion de colapso en almacen
            //colapsoAlmacen = !(estadoAlmacen.verificar_capacidad_maxima_hasta(timeService.now()));
            colapsoAlmacen = !(estadoAlmacen.verificar_capacidad_maxima());

            colapso = colapsoVuelos || colapsoAlmacen;

            if (colapso) {
                LOGGER.info("Boolean colapsoVuelos " + colapsoVuelos);
                LOGGER.info("Boolean colapsoAlmacen " + colapsoAlmacen);

                LOGGER.error(tipoOperacion + ": Colpaso en fecha " + now);
                // imprimir en un txt
                try {
                    // PrintWriter writer = new PrintWriter("colapso.txt", "UTF-8");
                    messagingTemplate.convertAndSend("/algoritmo/estado",
                            "Colpaso en fecha " + now);
                    // writer.close();
                } catch (Exception e) {
                    System.out.println("Error en escritura de archivo");
                }

                //estadoAlmacen.consulta_historicaTxt("ocupacionAeropuertosDiaDiaPlani" + i + ".txt");

                // respuestaAlgoritmo = new RespuestaAlgoritmo();
                respuestaAlgoritmo.setCorrecta(false);
                messagingTemplate.convertAndSend("/algoritmo/diaDiaRespuesta", respuestaAlgoritmo);
                messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado", "Colapso");
                this.ultimaRespuestaOperacionDiaDia = respuestaAlgoritmo;
                this.operacionDiaDiaActivo = false;
                return;
            }

            // estadoAlmacen.consulta_historicaTxt("ocupacionAeropuertosDiaDiaPlani" + i +
            // ".txt");
            this.estadoAlmacenOpDiaDia = estadoAlmacen;
            // this.paquetesOpDiaDia = currentPaquetes;
            // this.planRutasOpDiaDia = currentPlanRutas;

            // Formar respuesta para el front
            respuestaAlgoritmo.setEstadoAlmacen(this.estadoAlmacenOpDiaDia);
            respuestaAlgoritmo.setSimulacion(null);
            respuestaAlgoritmo.setOcupacionVuelos(null);
            respuestaAlgoritmo.setPaquetes(null);

            messagingTemplate.convertAndSend("/algoritmo/diaDiaRespuesta", respuestaAlgoritmo);
            LOGGER.info(tipoOperacion + " Respuesta algoritmo enviada");

            this.ultimaRespuestaOperacionDiaDia = respuestaAlgoritmo;

            this.puedeRecibirPaquetesDiaDia = true;
            LOGGER.info(tipoOperacion + " Puede recibir paquetes");

            this.ultimaRespuestaOperacionDiaDia.setIniciandoPrimeraPlanificacionDiaDia(false);
            System.out.println("Planificacion terminada hasta " + now);
            messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado", "Planificacion terminada hasta " + now);

            long end = System.currentTimeMillis();
            LOGGER.info(tipoOperacion + "|| Planificacion terminada en " + (end - start) + " milisegundos");
            long saMillis = SA * 1000 - (end - start);
            if (saMillis < 0)
                continue;

            try {
                Thread.sleep(saMillis);
            } catch (Exception e) {
                System.out.println("Error en sleep");
            }
        }
    }

    public void realizarGuardadoDiaDia(ArrayList<Paquete> paquetes, ArrayList<PlanRutaNT> planRutaNTs,
            PaqueteService paqueteService,
            PlanRutaService planRutaService, VueloService vueloService, PlanRutaXVueloService planRutaXVueloService,
            HashMap<Integer, Paquete> hashTodosPaquetes) {
        for (int idx = 0; idx < planRutaNTs.size(); idx++) {
            PlanRutaNT planRutaNT = planRutaNTs.get(idx);

            // Crear y guardar PlanRuta
            planRutaNT.updateCodigo();
            PlanRuta planRuta = new PlanRuta();
            planRuta.setCodigo(planRutaNT.getCodigo());

            try {
                planRuta = planRutaService.register(planRuta);
            } catch (PersistenceException e) {
                // Manejo de errores si algo sale mal durante la operación de guardado
                System.err.println("Error al guardar en la base de datos: " + e.getMessage());
                messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado",
                        "Error al guardar algun plan ruta: " + e.getMessage());
            }

            // Actualizar paquete
            paquetes.get(idx).setFechaDeEntrega(planRutaNT.getFin());
            paquetes.get(idx).setPlanRutaActual(planRuta);
            hashTodosPaquetes.put(paquetes.get(idx).getId(), paquetes.get(idx));

            try {
                paqueteService.update(paquetes.get(idx));
            } catch (Exception e) {
                // Manejo de errores si algo sale mal durante la operación de guardado
                System.err.println("Error al guardar en la base de datos: " + e.getMessage());
                messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado",
                        "Error al guardar algun paquete: " + e.getMessage());
            }

            // Asociar cada PlanRuta con sus vuelos
            for (Vuelo vueloFromArray : planRutaNT.getVuelos()) {
                Vuelo vuelo = vueloService.register(vueloFromArray);
                PlanRutaXVuelo planRutaXVuelo = new PlanRutaXVuelo();
                planRutaXVuelo.setPlanRuta(planRuta);
                planRutaXVuelo.setVuelo(vuelo);
                planRutaXVuelo.setIndiceDeOrden(planRutaNT.getVuelos().indexOf(vueloFromArray));

                try {
                    planRutaXVueloService.register(planRutaXVuelo);
                } catch (PersistenceException e) {
                    // Manejo de errores si algo sale mal durante la operación de guardado
                    System.err.println("Error al guardar en la base de datos: " + e.getMessage());
                    messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado",
                            "Error al guardar algun plan ruta x vuelo: " + e.getMessage());
                }
            }

        }
    }

    private boolean inicializarLoop(ArrayList<Paquete> paquetes, Date fechaMinima, ArrayList<PlanRutaNT> planRutas,
            ArrayList<PlanVuelo> planVuelos, GrafoVuelos grafoVuelos, String tipoOperacion, Simulacion simulacion) {
        paquetes.removeIf(paquete -> paquete.getEnvio().getFechaRecepcion().before(fechaMinima));

        messagingTemplate.convertAndSend("/algoritmo/estado",
                new RespuestaAlgoritmoEstado("Iniciando loop principal", simulacion));
        LOGGER.info(tipoOperacion + "Iniciando loop principal de simulacion");

        for (int i = 0; i < paquetes.size(); i++) {
            PlanRutaNT planRuta = new PlanRutaNT();
            planRutas.add(planRuta);
        }

        if (paquetes.size() == 0) {
            // System.out.println("ERROR: No hay paquetes para procesar.");
            LOGGER.error(tipoOperacion + " ERROR: No hay paquetes para procesar.");
            messagingTemplate.convertAndSend("/algoritmo/estado",
                    new RespuestaAlgoritmoEstado("Detenido, sin paquetes", simulacion));
            return false;
        }
        if (planVuelos.size() == 0) {
            // System.out.println("ERROR: No hay planes de vuelo para procesar.");
            LOGGER.error(tipoOperacion + " ERROR: No hay planes de vuelo para procesar.");
            messagingTemplate.convertAndSend("/algoritmo/estado",
                    new RespuestaAlgoritmoEstado("Detenido, sin planes vuelo", simulacion));
            return false;
        }

        if (grafoVuelos.getVuelosHash() == null || grafoVuelos.getVuelosHash().size() <= 0) {
            // System.out.println("ERROR: No se generaron vuelos.");
            LOGGER.error(tipoOperacion + " ERROR: No se generaron vuelos.");
            messagingTemplate.convertAndSend("/algoritmo/estado",
                    new RespuestaAlgoritmoEstado("Detenido, error en generar vuelos", simulacion));
            return false;
        }
        return true;
    }

    private void enviarRespuestaVacia(Date tiempoEnSimulacion, Simulacion simulacion, String tipoOperacion) {
        messagingTemplate.convertAndSend("/algoritmo/estado",
                new RespuestaAlgoritmoEstado(
                        "No hay paquetes para la planificacion actual en " + tiempoEnSimulacion + ", esperando",
                        simulacion));
        LOGGER.info(tipoOperacion + " No hay paquetes para la planificacion actual en " + tiempoEnSimulacion
                + ", esperando");
        // System.out.println("No hay paquetes para la planificacion actual en " +
        // tiempoEnSimulacion + ", esperando");

        RespuestaAlgoritmo respuestaAlgoritmo = new RespuestaAlgoritmo();
        respuestaAlgoritmo.setSimulacion(simulacion);
        respuestaAlgoritmo.getVuelos().removeIf(vuelo -> vuelo.getCapacidadUtilizada() == 0);

        messagingTemplate.convertAndSend("/algoritmo/respuesta", respuestaAlgoritmo);

    }

    public ArrayList<PlanRutaNT> loopPrincipal(ArrayList<Aeropuerto> aeropuertos, ArrayList<PlanVuelo> planVuelos,
            ArrayList<Paquete> paquetes, VueloService vueloService, PlanRutaService planRutaService,
            PaqueteService paqueteService, AeropuertoService aeropuertoService,
            PlanRutaXVueloService planRutaXVueloService,
            SimulacionService simulacionService,
            Simulacion simulacion, int SAP, int TA) {
        Date fechaMinima = simulacion.getFechaInicioSim();
        boolean replanificar = false;
        long milisegundosDeVentaja = (SAP + TA) * 1000;
        System.out.println("\nSalto de consumo: " + (SAP + TA) * simulacion.multiplicadorTiempo + " segundos\n");
        String tipoOperacion = "SIMULACION SEMANAL";
        ArrayList<PlanRutaNT> planRutas = new ArrayList<>();
        this.paquetesSimulacion = paquetes;
        this.planRutasSimulacion = planRutas;
        HashMap<Integer, Integer> ocupacionVuelos = new HashMap<Integer, Integer>();
        GrafoVuelos grafoVuelos = new GrafoVuelos(planVuelos, paquetes, vueloService, simulacion);
        int i = 0;
        Date fechaSgteCalculo = simulacion.getFechaInicioSim();
        Date tiempoEnFront = simulacion.getFechaInicioSim();
        Date tiempoEnBack = new Date(simulacion.getFechaInicioSim().getTime()
                + milisegundosDeVentaja * (int) simulacion.getMultiplicadorTiempo());
        boolean primera_iter = true;
        // Inicializar simulacion
        boolean iniciar = inicializarLoop(paquetes, fechaMinima, planRutas, planVuelos, grafoVuelos, tipoOperacion,
                simulacion);

        while (iniciar) {
            simulacion = simulacionService.get(simulacion.getId());
            long startTime = System.currentTimeMillis();
            paquetes = actualizarPaquetes(paquetes, planRutas, tiempoEnFront, aeropuertoService);
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            System.out.println("Tiempo de actualizacion de paquetes: " + duration + " milisegundos");

            // Gestion de detencion
            if (simulacion.estado == 1) {
                // System.out.println("Simulacion terminada");
                LOGGER.info(tipoOperacion + " Simulacion terminada");
                messagingTemplate.convertAndSend("/algoritmo/estado",
                        new RespuestaAlgoritmoEstado("Simulacion terminada", simulacion));
                break;
            }

            // Gestion de pausa
            if (simulacion.getEstado() == 2) {
                LOGGER.info(tipoOperacion + " Simulacion pausada");
                // System.out.println("Simulacion pausada");

                if (primera_iter) {
                    simulacion.setFechaInicioSistema(new Date());
                    simulacion = simulacionService.update(simulacion);
                    primera_iter = false;

                }
                continue;
            }

            // Lapso de tiempo entre planificaciones
            if (tiempoEnBack.before(fechaSgteCalculo)) {
                tiempoEnBack = calcularTiempoSimulacionBack(simulacion, milisegundosDeVentaja);
                tiempoEnFront = calcularTiempoSimulacionBack(simulacion, 0);

                // System.out.println("Aun no es tiempo de planificar, la fecha en simulacion es
                // " + tiempoEnSimulacion);
                LOGGER.info(tipoOperacion + " Aun no es tiempo de planificar, la fecha en simulacion es "
                        + tiempoEnBack);
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    System.out.println("Error en sleep");
                }
                if (primera_iter) {
                    simulacion.setFechaInicioSistema(new Date());
                    simulacion = simulacionService.update(simulacion);
                    primera_iter = false;

                }
                continue;
            }
            // Calculo de la sgte planificacion
            fechaSgteCalculo = agregarSAPyTA(tiempoEnBack, TA, SAP, simulacion.getMultiplicadorTiempo());

            LOGGER.info(tipoOperacion + " Planificacion iniciada en " + tiempoEnBack);
            messagingTemplate.convertAndSend("/algoritmo/estado",
                    new RespuestaAlgoritmoEstado("Planificación iniciada", simulacion));

            // Filtrar paquetes a calcular
            ArrayList<Paquete> paquetesProcesar = filtrarPaquetesValidos(paquetes, tiempoEnBack, replanificar);

            int tamanhoPaquetes = paquetesProcesar.size();
            final Date finalTiempoEnSimulacion = tiempoEnBack;
            List<Paquete> paquetesRest = paquetes.stream()
                    .filter(p -> p.getFechaDeEntrega() == null || p.getFechaDeEntrega().after(finalTiempoEnSimulacion))
                    .collect(Collectors.toList());
            /*
             * this.respuesta_paquetes_simulacion = paquetes.stream()
             * .filter(p -> p.getFechaRecepcion().before(finalTiempoEnSimulacion)
             * && (p.getFechaDeEntrega() == null
             * || !p.getFechaDeEntrega().before(finalTiempoEnSimulacion)))
             * .collect(Collectors.toList());
             */
            List<Paquete> filteredPaquetes = paquetes.stream()
                    .filter(p -> p.obtenerFechaRecepcion().before(finalTiempoEnSimulacion)
                            && (p.getFechaDeEntrega() == null
                                    || !p.getFechaDeEntrega().before(finalTiempoEnSimulacion)))
                    .collect(Collectors.toList());
            if (primera_iter) {
                this.paquetes_por_simulacion.put(simulacion.getId(), paquetesProcesar);
            } else {
                this.paquetes_por_simulacion.put(simulacion.getId(), filteredPaquetes);
            }

            LOGGER.info(tipoOperacion + " Paquetes guardados en memoria: "
                    + this.paquetes_por_simulacion.get(simulacion.getId()).size());

            if (tamanhoPaquetes == 0) {
                if (primera_iter) {
                    simulacion.setFechaInicioSistema(new Date());
                    simulacion = simulacionService.update(simulacion);
                    primera_iter = false;

                }
                enviarRespuestaVacia(tiempoEnBack, simulacion, tipoOperacion);
                System.out.println("Proxima planificacion en tiempo de simulacion " + fechaSgteCalculo);
                tiempoEnBack = calcularTiempoSimulacionBack(simulacion, milisegundosDeVentaja);
                tiempoEnFront = calcularTiempoSimulacionBack(simulacion, 0);
                continue;
            }

            if (paquetesRest.size() == 0) {
                messagingTemplate.convertAndSend("/algoritmo/estado",
                        new RespuestaAlgoritmoEstado("No hay mas paquetes, terminando", simulacion));
                LOGGER.info(tipoOperacion + " No hay mas paquetes, terminando");
                simulacion.setEstado(1);
                simulacionService.update(simulacion);
                break;
            }

            // Filtrar paquetes que estan volando
            startTime = System.currentTimeMillis();
            ArrayList<PlanRutaNT> temp_planesRutaActuales = new ArrayList<>();

            for (int j = 0; j < paquetesProcesar.size(); j++) {
                temp_planesRutaActuales.add(planRutas.get(paquetes.indexOf(paquetesProcesar.get(j))));
            }
            paquetesProcesar = filtrarPaquetesVolando(paquetesProcesar, temp_planesRutaActuales, tiempoEnBack, TA,
                    simulacion.getMultiplicadorTiempo());
            endTime = System.currentTimeMillis();
            duration = endTime - startTime;
            System.out.println("Tiempo de filtrado de vuelos: " + duration + " milisegundos");

            // Recalcular el tamanho de paquetes
            tamanhoPaquetes = paquetesProcesar.size();

            // System.out.println("Se van a procesar " + tamanhoPaquetes + " paquetes, hasta
            // " + fechaLimiteCalculo);
            LOGGER.info(
                    tipoOperacion + " Se van a procesar " + tamanhoPaquetes + " paquetes, hasta " + tiempoEnBack);

            ArrayList<PlanRutaNT> planesRutaActuales = new ArrayList<>();

            for (int j = 0; j < paquetesProcesar.size(); j++) {
                planesRutaActuales.add(planRutas.get(paquetes.indexOf(paquetesProcesar.get(j))));
            }

            // Realizar planificacion
            startTime = System.currentTimeMillis();
            RespuestaAlgoritmo respuestaAlgoritmo = procesarPaquetes(grafoVuelos, ocupacionVuelos, paquetesProcesar,
                    planesRutaActuales, aeropuertos, planVuelos, tamanhoPaquetes, i, vueloService, planRutaService,
                    simulacionService, simulacion, messagingTemplate, tipoOperacion, tiempoEnBack,
                    TA * (int) simulacion.getMultiplicadorTiempo());
            endTime = System.currentTimeMillis();
            duration = endTime - startTime;
            System.out.println("Tiempo de ejecucion de algoritmo: " + duration + " milisegundos");

            if (respuestaAlgoritmo == null) {
                LOGGER.error(tipoOperacion + ": Colpaso en fecha " + tiempoEnBack);
                // imprimir en un txt
                try {
                    PrintWriter writer = new PrintWriter("colapso.txt", "UTF-8");
                    writer.println("Colpaso en fecha " + tiempoEnBack);
                    writer.close();
                } catch (Exception e) {
                    System.out.println("Error en escritura de archivo");
                }
                simulacion.setEstado(3);
                respuestaAlgoritmo = new RespuestaAlgoritmo();
                respuestaAlgoritmo.setSimulacion(simulacion);
                respuestaAlgoritmo.setCorrecta(false);
                return null;
            }
            if (respuestaAlgoritmo.isCorrecta() == false) {
                LOGGER.error(tipoOperacion + ": Colpaso en fecha por paquetes " + tiempoEnBack);
                // imprimir en un txt
                try {
                    PrintWriter writer = new PrintWriter("colapso.txt", "UTF-8");
                    writer.println("Colpaso en fecha " + tiempoEnBack);
                    writer.close();
                } catch (Exception e) {
                    System.out.println("Error en escritura de archivo");
                }
                simulacion.setEstado(3);
                respuestaAlgoritmo = new RespuestaAlgoritmo();
                respuestaAlgoritmo.setSimulacion(simulacion);
                respuestaAlgoritmo.setCorrecta(false);
                return null;
            }
            i++;

            // Guardar resultados
            startTime = System.currentTimeMillis();
            realizarGuardado(paquetes, planRutas, paquetesProcesar, respuestaAlgoritmo, simulacion, paqueteService,
                    planRutaService,
                    vueloService, planRutaXVueloService, "/algoritmo/estado");

            this.paquetesProcesadosUltimaSimulacion = new ArrayList<>(paquetesProcesar);
            // paquetesProcesar;
            HashMap<Integer, Integer> nuevaOcupacion = new HashMap<>();
            // LLena la nuevaOcupacion recorriendo cada vuelo de cada planruta en planRutas
            for (PlanRutaNT planRutaNT : planRutas) {
                for (Vuelo vuelo : planRutaNT.getVuelos()) {
                    if (nuevaOcupacion.get(vuelo.getId()) == null) {
                        nuevaOcupacion.put(vuelo.getId(), 1);
                    } else {
                        nuevaOcupacion.put(vuelo.getId(), nuevaOcupacion.get(vuelo.getId()) + 1);
                    }
                }
            }

            // ocupacionVuelos = respuestaAlgoritmo.getOcupacionVuelos();
            ocupacionVuelos = nuevaOcupacion;
            respuestaAlgoritmo.setPaquetes(new ArrayList<>(paquetesRest));
            respuestaAlgoritmo.setOcupacionVuelos(nuevaOcupacion);
            HashMap<Integer, Vuelo> hashVuelos = grafoVuelos.getVuelosHash();
            boolean colapso = false;
            boolean colapsoVuelos = false;
            boolean colapsoAlmacen = false;
            for (Integer idVuelo : hashVuelos.keySet()) {
                if (ocupacionVuelos.get(idVuelo) == null) {
                    // LOGGER.info(tipoOperacion + " Ocupacion de vuelos vacio.");
                    continue;
                }
                if (ocupacionVuelos.get(idVuelo) > hashVuelos.get(idVuelo).getPlanVuelo().getCapacidadMaxima()) {
                    // colapso = true;
                    colapsoVuelos = true;
                }
            }
            EstadoAlmacen estadoAlmacen = new EstadoAlmacen(paquetes, planRutas, grafoVuelos.getVuelosHash(),
                    ocupacionVuelos,
                    aeropuertos);
            // estadoAlmacen.consulta_historicaTxt("ocupacionAeropuertos" + i + ".txt");
            respuestaAlgoritmo.setEstadoAlmacen(estadoAlmacen);

            colapsoAlmacen = !(estadoAlmacen.verificar_capacidad_maxima_hasta(tiempoEnBack));

            // Solo en la primera iter, definir el inicio de la simulacion
            if (primera_iter) {
                simulacion.setFechaInicioSistema(new Date());
                simulacion = simulacionService.update(simulacion);
                primera_iter = false;

            }

            colapso = colapsoVuelos || colapsoAlmacen;

            if (colapso) {
                LOGGER.info("Boolean colapsoVuelos " + colapsoVuelos);
                LOGGER.info("Boolean colapsoAlmacen " + colapsoAlmacen);

                LOGGER.error(tipoOperacion + ": Colpaso en fecha " + tiempoEnBack);
                // imprimir en un txt
                try {
                    // PrintWriter writer = new PrintWriter("colapso.txt", "UTF-8");
                    messagingTemplate.convertAndSend("/algoritmo/estado",
                            "Colpaso en fecha " + tiempoEnBack);
                    // writer.close();
                } catch (Exception e) {
                    System.out.println("Error en escritura de archivo");
                }
                simulacion.setEstado(3);
                respuestaAlgoritmo = new RespuestaAlgoritmo();
                respuestaAlgoritmo.setSimulacion(simulacion);
                respuestaAlgoritmo.setCorrecta(false);
                return null;
            }
            endTime = System.currentTimeMillis();
            duration = endTime - startTime;
            System.out.println("Tiempo de guardado: " + duration + " milisegundos");

            while (true) {
                simulacion = simulacionService.get(simulacion.getId());
                LOGGER.info(tipoOperacion + " Simulacion pausada");
                if (simulacion.getEstado() != 2) {
                    break;
                }

            }
            // Formar respuesta a front
            enviarRespuesta(respuestaAlgoritmo, simulacion, tiempoEnFront, fechaSgteCalculo,
                    "/algoritmo/respuesta");
            this.paquetesSimulacion = new ArrayList<>(paquetes);
            this.planRutasSimulacion = new ArrayList<>();
            for (PlanRutaNT planRutaNT : planRutas) {
                PlanRutaNT clonedPlanRuta = new PlanRutaNT();
                clonedPlanRuta.setVuelos(new ArrayList<>(planRutaNT.getVuelos()));
                this.planRutasSimulacion.add(clonedPlanRuta);
            }
            LOGGER.info(tipoOperacion + " Respuesta algoritmo enviada de simulacion");

            // System.out.println("Proxima planificacion en tiempo de simulacion " +
            // fechaSgteCalculo);
            LOGGER.info(tipoOperacion + " Proxima planificacion en tiempo de simulacion " + fechaSgteCalculo);

            tiempoEnBack = calcularTiempoSimulacionBack(simulacion, milisegundosDeVentaja);
            tiempoEnFront = calcularTiempoSimulacionBack(simulacion, 0);

        }
        return planRutas;

    }

    public ArrayList<PlanRutaNT> loopPrincipalColapso(ArrayList<Aeropuerto> aeropuertos,
            ArrayList<PlanVuelo> planVuelos,
            ArrayList<Paquete> paquetes, VueloService vueloService, PlanRutaService planRutaService,
            PaqueteService paqueteService, AeropuertoService aeropuertoService,
            PlanRutaXVueloService planRutaXVueloService,
            SimulacionService simulacionService,
            Simulacion simulacion, int SA, int TA) throws InterruptedException {
        Date fechaMinima = simulacion.getFechaInicioSim();
        boolean replanificar = true;
        String tipoOperacion = "SIMULACION HASTA COLAPSO";
        ArrayList<PlanRutaNT> planRutas = new ArrayList<>();
        this.paquetesSimulacion = paquetes;
        this.planRutasSimulacion = planRutas;
        HashMap<Integer, Integer> ocupacionVuelos = new HashMap<Integer, Integer>();
        GrafoVuelos grafoVuelos = new GrafoVuelos(planVuelos, paquetes, vueloService, simulacion);
        int i = 0;
        Date fechaSgteCalculo = simulacion.getFechaInicioSim();
        Date tiempoEnSimulacion = simulacion.getFechaInicioSim();
        boolean primera_iter = true;
        // Inicializar simulacion
        boolean iniciar = inicializarLoop(paquetes, fechaMinima, planRutas, planVuelos, grafoVuelos, tipoOperacion,
                simulacion);

        while (iniciar) {
            simulacion = simulacionService.get(simulacion.getId());
            paquetes = actualizarPaquetes(paquetes, planRutas, tiempoEnSimulacion, aeropuertoService);

            // Gestion de pausa detencion
            if (simulacion.estado == 1) {
                // System.out.println("Simulacion terminada");
                LOGGER.info(tipoOperacion + " Simulacion terminada");
                messagingTemplate.convertAndSend("/algoritmo/estado",
                        new RespuestaAlgoritmoEstado("Simulacion terminada", simulacion));
                break;
            }

            // Gestion de pausa
            if (simulacion.getEstado() == 2) {
                LOGGER.info(tipoOperacion + " Simulacion pausada");
                // System.out.println("Simulacion pausada");

                if (primera_iter) {
                    simulacion.setFechaInicioSistema(new Date());
                    simulacion = simulacionService.update(simulacion);
                    primera_iter = false;

                }
                continue;
            }

            // Lapso de tiempo entre planificaciones
            if (tiempoEnSimulacion.before(fechaSgteCalculo)) {
                tiempoEnSimulacion = fechaSgteCalculo;
                // System.out.println("Aun no es tiempo de planificar, la fecha en simulacion es
                // " + tiempoEnSimulacion);
                LOGGER.info(tipoOperacion + " Aun no es tiempo de planificar, la fecha en simulacion es "
                        + tiempoEnSimulacion);
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    System.out.println("Error en sleep");
                }
                if (primera_iter) {
                    simulacion.setFechaInicioSistema(new Date());
                    simulacion = simulacionService.update(simulacion);
                    primera_iter = false;

                }
                continue;
            }

            // Calculo del limie de planificacion
            Date fechaLimiteCalculo = agregarSAPyTA(tiempoEnSimulacion, TA, SA, simulacion.getMultiplicadorTiempo());
            fechaSgteCalculo = agregarSAPyTA(tiempoEnSimulacion, 0, SA, simulacion.getMultiplicadorTiempo());

            LOGGER.info(tipoOperacion + " Planificacion iniciada en " + tiempoEnSimulacion);
            messagingTemplate.convertAndSend("/algoritmo/estado",
                    new RespuestaAlgoritmoEstado("Planificación iniciada", simulacion));

            // Filtrar paquetes a calcular
            ArrayList<Paquete> paquetesProcesar = filtrarPaquetesValidos(paquetes, tiempoEnSimulacion, replanificar);

            int tamanhoPaquetes = paquetesProcesar.size();
            final Date finalTiempoEnSimulacion = tiempoEnSimulacion;
            List<Paquete> paquetesRest = paquetes.stream()
                    .filter(p -> p.getFechaDeEntrega() == null || p.getFechaDeEntrega().after(finalTiempoEnSimulacion))
                    .collect(Collectors.toList());
            /*
             * this.respuesta_paquetes_simulacion = paquetes.stream()
             * .filter(p -> p.getFechaRecepcion().before(finalTiempoEnSimulacion)
             * && (p.getFechaDeEntrega() == null
             * || !p.getFechaDeEntrega().before(finalTiempoEnSimulacion)))
             * .collect(Collectors.toList());
             */
            List<Paquete> filteredPaquetes = paquetes.stream()
                    .filter(p -> p.obtenerFechaRecepcion().before(finalTiempoEnSimulacion)
                            && (p.getFechaDeEntrega() == null
                                    || !p.getFechaDeEntrega().before(finalTiempoEnSimulacion)))
                    .collect(Collectors.toList());
            if (primera_iter) {
                this.paquetes_por_simulacion.put(simulacion.getId(), paquetesProcesar);
            } else {
                this.paquetes_por_simulacion.put(simulacion.getId(), filteredPaquetes);
            }

            LOGGER.info(tipoOperacion + " Paquetes guardados en memoria: "
                    + this.paquetes_por_simulacion.get(simulacion.getId()).size());

            if (tamanhoPaquetes == 0) {
                if (primera_iter) {
                    simulacion.setFechaInicioSistema(new Date());
                    simulacion = simulacionService.update(simulacion);
                    primera_iter = false;

                }
                enviarRespuestaVacia(tiempoEnSimulacion, simulacion, tipoOperacion);
                System.out.println("Proxima planificacion en tiempo de simulacion " + fechaSgteCalculo);
                tiempoEnSimulacion = calcularTiempoSimulacionBack(simulacion, 60000);
                continue;
            }

            if (paquetesRest.size() == 0) {
                messagingTemplate.convertAndSend("/algoritmo/estado",
                        new RespuestaAlgoritmoEstado("No hay mas paquetes, terminando", simulacion));
                LOGGER.info(tipoOperacion + " No hay mas paquetes, terminando");
                simulacion.setEstado(1);
                simulacionService.update(simulacion);
                break;
            }
            ArrayList<PlanRutaNT> temp_planesRutaActuales = new ArrayList<>();

            for (int j = 0; j < paquetesProcesar.size(); j++) {
                temp_planesRutaActuales.add(planRutas.get(paquetes.indexOf(paquetesProcesar.get(j))));
            }
            // Filtrar paquetes que estan volando

            paquetesProcesar = filtrarPaquetesVolando(paquetesProcesar, temp_planesRutaActuales, tiempoEnSimulacion, TA,
                    simulacion.getMultiplicadorTiempo());

            // Recalcular el tamanho de paquetes
            tamanhoPaquetes = paquetesProcesar.size();

            // System.out.println("Se van a procesar " + tamanhoPaquetes + " paquetes, hasta
            // " + fechaLimiteCalculo);
            LOGGER.info(
                    tipoOperacion + " Se van a procesar " + tamanhoPaquetes + " paquetes, hasta " + fechaLimiteCalculo);

            ArrayList<PlanRutaNT> planesRutaActuales = new ArrayList<>();

            for (int j = 0; j < paquetesProcesar.size(); j++) {
                planesRutaActuales.add(planRutas.get(paquetes.indexOf(paquetesProcesar.get(j))));
            }

            // Realizar planificacion
            RespuestaAlgoritmo respuestaAlgoritmo = procesarPaquetesColapso(grafoVuelos, ocupacionVuelos,
                    paquetesProcesar,
                    planesRutaActuales, aeropuertos, planVuelos, tamanhoPaquetes, i, vueloService, planRutaService,
                    simulacionService, simulacion, messagingTemplate, tipoOperacion, tiempoEnSimulacion,
                    TA * (int) simulacion.getMultiplicadorTiempo());
            LOGGER.info(tipoOperacion + " Paquetes procesados");
            if (respuestaAlgoritmo == null) {
                LOGGER.error(tipoOperacion + ": Colpaso en fecha " + tiempoEnSimulacion);
                try {
                    PrintWriter writer = new PrintWriter("colapso.txt", "UTF-8");
                    writer.println("Colpaso en fecha " + tiempoEnSimulacion);
                    writer.close();
                } catch (Exception e) {
                    System.out.println("Error en escritura de archivo");
                }
                simulacion.setEstado(3);
                respuestaAlgoritmo = new RespuestaAlgoritmo();
                respuestaAlgoritmo.setSimulacion(simulacion);
                respuestaAlgoritmo.setCorrecta(false);
                return null;
            }

            if (respuestaAlgoritmo.isCorrecta() == false) {
                LOGGER.error(tipoOperacion + ": Colpaso en fecha por paquetes " + tiempoEnSimulacion);
                // imprimir en un txt
                try {
                    PrintWriter writer = new PrintWriter("colapso.txt", "UTF-8");
                    writer.println("Colpaso en fecha " + tiempoEnSimulacion);
                    writer.close();
                } catch (Exception e) {
                    System.out.println("Error en escritura de archivo");
                }
                simulacion.setEstado(3);
                respuestaAlgoritmo = new RespuestaAlgoritmo();
                respuestaAlgoritmo.setSimulacion(simulacion);
                respuestaAlgoritmo.setCorrecta(false);
                return null;
            }
            i++;

            // Guardar resultados
            realizarGuardado(paquetes, planRutas, paquetesProcesar, respuestaAlgoritmo, simulacion, paqueteService,
                    planRutaService,
                    vueloService, planRutaXVueloService, "/algoritmo/estado");
            this.paquetesProcesadosUltimaSimulacion = new ArrayList<>(paquetesProcesar);
            LOGGER.info(tipoOperacion + " Paquetes guardados");
            // paquetesProcesar;
            HashMap<Integer, Integer> nuevaOcupacion = new HashMap<>();
            // LLena la nuevaOcupacion recorriendo cada vuelo de cada planruta en planRutas
            for (PlanRutaNT planRutaNT : planRutas) {
                for (Vuelo vuelo : planRutaNT.getVuelos()) {
                    if (nuevaOcupacion.get(vuelo.getId()) == null) {
                        nuevaOcupacion.put(vuelo.getId(), 1);
                    } else {
                        nuevaOcupacion.put(vuelo.getId(), nuevaOcupacion.get(vuelo.getId()) + 1);
                    }
                }
            }

            // ocupacionVuelos = respuestaAlgoritmo.getOcupacionVuelos();
            ocupacionVuelos = nuevaOcupacion;
            respuestaAlgoritmo.setPaquetes(new ArrayList<>(paquetesRest));
            respuestaAlgoritmo.setOcupacionVuelos(nuevaOcupacion);

            LOGGER.info(tipoOperacion + " Verificacion vuelos");
            HashMap<Integer, Vuelo> hashVuelos = grafoVuelos.getVuelosHash();
            boolean colapso = false;
            boolean colapsoVuelos = false;
            boolean colapsoAlmacen = false;
            if (hashVuelos == null) {
                LOGGER.error(tipoOperacion + " ERROR: Hash de grafovuelos vacio.");
                messagingTemplate.convertAndSend("/algoritmo/estado",
                        new RespuestaAlgoritmoEstado("Detenido, error en generar vuelos", simulacion));
                return null;
            }
            for (Integer idVuelo : hashVuelos.keySet()) {
                if (ocupacionVuelos.get(idVuelo) == null) {
                    // LOGGER.info(tipoOperacion + " Ocupacion de vuelos vacio.");
                    continue;
                }
                if (ocupacionVuelos.get(idVuelo) > hashVuelos.get(idVuelo).getPlanVuelo().getCapacidadMaxima()) {
                    // colapso = true;
                    colapsoVuelos = true;
                }
            }
            LOGGER.info(tipoOperacion + " Estado almacen");
            EstadoAlmacen estadoAlmacen = new EstadoAlmacen(paquetes, planRutas, grafoVuelos.getVuelosHash(),
                    ocupacionVuelos,
                    aeropuertos);
            // estadoAlmacen.consulta_historicaTxt("ocupacionAeropuertos" + i + ".txt");

            // colapso = !(estadoAlmacen.verificar_capacidad_maxima());
            colapsoAlmacen = !(estadoAlmacen.verificar_capacidad_maxima_hasta(tiempoEnSimulacion));
            /*
             * if(!colapso){
             * colapso = !(estadoAlmacen.verificar_capacidad_maxima());
             * }
             */

            respuestaAlgoritmo.setEstadoAlmacen(estadoAlmacen);
            // Solo en la primera iter, definir el inicio de la simulacion
            if (primera_iter) {
                simulacion.setFechaInicioSistema(new Date());
                simulacion = simulacionService.update(simulacion);
                primera_iter = false;

            }

            colapso = colapsoVuelos || colapsoAlmacen;

            if (colapso) {
                LOGGER.info("Boolean colapsoVuelos " + colapsoVuelos);
                LOGGER.info("Boolean colapsoAlmacen " + colapsoAlmacen);
                estadoAlmacen.consulta_historicaTxt("ocupacionAeropuertos" + i + ".txt");
                LOGGER.error(tipoOperacion + ": Colpaso en fecha " + tiempoEnSimulacion);
                // imprimir en un txt
                try {
                    PrintWriter writer = new PrintWriter("colapso.txt", "UTF-8");
                    writer.println("Colpaso en fecha " + tiempoEnSimulacion);
                    writer.close();
                } catch (Exception e) {
                    System.out.println("Error en escritura de archivo");
                }
                simulacion.setEstado(3);
                respuestaAlgoritmo = new RespuestaAlgoritmo();
                respuestaAlgoritmo.setSimulacion(simulacion);
                respuestaAlgoritmo.setCorrecta(false);
                return null;
            }

            // Formar respuesta a front
            // enviarRespuesta(respuestaAlgoritmo, simulacion, fechaLimiteCalculo,
            // fechaSgteCalculo,
            // "/algoritmo/respuesta");
            // this.paquetesSimulacion = new ArrayList<>(paquetes);
            // this.planRutasSimulacion = new ArrayList<>();
            // for (PlanRutaNT planRutaNT : planRutas) {
            // PlanRutaNT clonedPlanRuta = new PlanRutaNT();
            // clonedPlanRuta.setVuelos(new ArrayList<>(planRutaNT.getVuelos()));
            // this.planRutasSimulacion.add(clonedPlanRuta);
            // }
            // LOGGER.info(tipoOperacion + " Respuesta algoritmo enviada de simulacion");

            // System.out.println("Proxima planificacion en tiempo de simulacion " +
            // fechaSgteCalculo);
            LOGGER.info(tipoOperacion + " Proxima planificacion en tiempo de simulacion " + fechaSgteCalculo);

            tiempoEnSimulacion = calcularTiempoSimulacionBack(simulacion, 60000);

        }
        return planRutas;
    }

    private void printRutasAlgoritmo(ArrayList<PlanRutaNT> planRutas, ArrayList<Paquete> paquetes, int i) {
        try {
            FileWriter fw = new FileWriter("planRutas" + i + " .txt", true);
            System.out.println("Guardando en el archivo " + "planRutas" + i + " .txt");
            BufferedWriter bw = new BufferedWriter(fw);
            int idx = 0;

            for (PlanRutaNT planRutaNT : planRutas) {
                try {
                    bw.write("Para el paquete: " + paquetes.get(idx).getId());
                    bw.newLine();
                    for (Vuelo vuelo : planRutaNT.getVuelos()) {
                        bw.write("(" + vuelo.getId() + ") -> ");

                    }
                    bw.newLine();
                    bw.write("------------------------");
                    bw.newLine();
                } catch (IOException e) {
                    System.out.println("Error al escribir en el archivo: " + e.getMessage());
                }
                idx++;
            }
            bw.close();
        } catch (IOException e) {
            System.out.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }

    private void printOcupacion(HashMap<Integer, Integer> hash, HashMap<Integer, Integer> ocupacionVuelos, int i) {
        try {
            FileWriter fw = new FileWriter("ocupacionVuelos" + i + " .txt", true);
            System.out.println("Guardando en el archivo " + "ocupacionVuelos" + i + " .txt");
            BufferedWriter bw = new BufferedWriter(fw);
            Set<Integer> clavesRecorridas = new HashSet<>();
            for (Map.Entry<Integer, Integer> entry : hash.entrySet()) {
                clavesRecorridas.add(entry.getKey());
                Integer key = entry.getKey();
                Integer value = entry.getValue();
                Integer otherValue = ocupacionVuelos.get(key);
                try {
                    bw.write("Clave: " + key + ", Valor en hash: " + value);
                    if (otherValue != null) {
                        bw.write(", Valor en ocupacionVuelos: " + otherValue);
                        ocupacionVuelos.remove(key); // Remove the key from ocupacionVuelos to avoid duplicate entries
                    }
                    bw.newLine();
                } catch (IOException e) {
                    System.out.println("Error al escribir en el archivo: " + e.getMessage());
                }
            }
            // Write the remaining entries in ocupacionVuelos
            for (Map.Entry<Integer, Integer> entry : ocupacionVuelos.entrySet()) {
                Integer key = entry.getKey();
                if (!clavesRecorridas.contains(key)) {
                    Integer value = entry.getValue();
                    try {
                        bw.write("Clave: " + key + ", Valor en ocupacionVuelos: " + value);
                        bw.newLine();
                    } catch (IOException e) {
                        System.out.println("Error al escribir en el archivo: " + e.getMessage());
                    }
                }
            }
            bw.close();
        } catch (IOException e) {
            System.out.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }

    private HashMap<Integer, Integer> printPaquetes(ArrayList<Paquete> paquetes, ArrayList<PlanRutaNT> planRutas, int i,
            VueloService vueloService) {
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        ArrayList<Paquete> paquetesOrdenados = new ArrayList<>(paquetes);
        Collections.sort(paquetesOrdenados, Comparator.comparing(Paquete::obtenerFechaRecepcion));

        try {
            FileWriter fw = new FileWriter("paquetes" + i + " .txt", true);
            System.out.println("Guardando en el archivo " + "paquetes" + i + " .txt");
            BufferedWriter bw = new BufferedWriter(fw);
            int iter = 0;
            for (Paquete paquete : paquetesOrdenados) {

                bw.write("Para el paquete " + paquete.getId() + "\n");

                if (paquete.planRutaActual == null) {
                    bw.write("No tiene rutas\n");
                    continue;
                }
                if (planRutas.get(iter) == null) {
                    bw.write("Paquete tiene plan ruta pero no objeto planRutaNT\n");
                }
                /*
                 * bw.write("Vuelos de planRutaNT\n");
                 * 
                 * for (Vuelo vuelo : planRutas.get(iter).getVuelos()) {
                 * if (hashMap.get(vuelo.getId()) == null) {
                 * hashMap.put(vuelo.getId(), 1);
                 * } else {
                 * hashMap.put(vuelo.getId(), hashMap.get(vuelo.getId()) + 1);
                 * }
                 * bw.write("(" + vuelo.getId() + ") -> ");
                 * }
                 * bw.write("\n");
                 */

                ArrayList<Vuelo> vuelos = vueloService.findVuelosByPaqueteId(paquete.getId());
                if (vuelos == null || vuelos.size() == 0) {
                    bw.write("El paquete tiene planRuta pero no vuelos\n");
                    continue;
                }
                // bw.write("Vuelos de api\n");

                for (Vuelo vuelo : vuelos) {
                    int ocupacion = hashMap.getOrDefault(vuelo.getId(), 0);
                    hashMap.put(vuelo.getId(), ocupacion + 1);
                    bw.write("(" + vuelo.getId() + ") -> ");
                }
                bw.write("\n");
                iter++;
                bw.write("------------------------\n");
            }
            bw.close();
            return hashMap;
        } catch (IOException e) {
            System.out.println("Error al escribir en el archivo: " + e.getMessage());
            return null;
        }

    }

    private ArrayList<Paquete> filtrarPaquetesValidos(ArrayList<Paquete> paquetes, Date tiempoEnSimulacion,
            boolean replanificar) {
        Collections.sort(paquetes, Comparator.comparing(Paquete::obtenerFechaRecepcion));

        final Date finalTiempoEnSimulacion = tiempoEnSimulacion;
        List<Paquete> paquetesTemp = paquetes.stream()
                .filter(p -> p.getFechaDeEntrega() == null
                        || (replanificar && finalTiempoEnSimulacion.before(p.getFechaDeEntrega())))
                .filter(p -> p.obtenerFechaRecepcion().before(tiempoEnSimulacion))
                .collect(Collectors.toList());
        ArrayList<Paquete> paquetesProcesar = new ArrayList<>(paquetesTemp);
        return paquetesProcesar;
    }

    private ArrayList<Paquete> filtrarPaquetesVolando(ArrayList<Paquete> paquetesProcesar,
            ArrayList<PlanRutaNT> planRutasProcesar,
            Date tiempoEnSimulacion, int TA, double multiplicador) {
        if (paquetesProcesar == null)
            return new ArrayList<>();
        ArrayList<Integer> indicesAEliminar = new ArrayList<>();
        Date fechaMaxima = agregarSAPyTA(tiempoEnSimulacion, TA, 0, multiplicador);
        Date fechaMaxima_entrega = agregarSAPyTA(tiempoEnSimulacion, TA, 0, multiplicador);
        for (int i = 0; i < paquetesProcesar.size(); i++) {
            // for (Paquete paquete : paquetesProcesar) {

            if (paquetesProcesar.get(i).planRutaActual == null) {
                continue;
            }
            ArrayList<Vuelo> vuelos = planRutasProcesar.get(i).getVuelos();
            if (vuelos == null || vuelos.size() == 0) {
                System.out.println("El paquete tiene planRuta pero no vuelo");
            }

            // Agregado, si el paquete ya esta por ser entregado no se replanificara
            if (paquetesProcesar.get(i).getFechaDeEntrega() != null
                    && paquetesProcesar.get(i).getFechaDeEntrega().before(fechaMaxima_entrega)) {
                indicesAEliminar.add(i);
                continue;
            }

            for (Vuelo vuelo : vuelos) {
                if (vuelo.getFechaLlegada().after(tiempoEnSimulacion)
                        && vuelo.getFechaSalida().before(fechaMaxima)) {
                    indicesAEliminar.add(i);
                    break;
                }
            }
        }
        Collections.sort(indicesAEliminar, Collections.reverseOrder());
        for (int index : indicesAEliminar) {
            paquetesProcesar.remove(index);
        }

        // System.out.println("Paquetes eliminados exitosamente.");

        return paquetesProcesar;

    }

    private void enviarRespuesta(RespuestaAlgoritmo respuestaAlgoritmo, Simulacion simulacion, Date tiempoEnsimulacion,
            Date fechaSgteCalculo, String canal) {
        respuestaAlgoritmo.setSimulacion(simulacion);

        respuestaAlgoritmo.getVuelos().removeIf(
                vuelo -> vuelo.getCapacidadUtilizada() == 0 || vuelo.getFechaLlegada().before(tiempoEnsimulacion));
        System.out.println("Se filtraron los vuelos");
        respuestaAlgoritmo.setOcupacionVuelos(null);
        respuestaAlgoritmo.setPaquetes(null);
        respuestaAlgoritmo.setPlanesRutas(null);
        messagingTemplate.convertAndSend(canal, respuestaAlgoritmo);
        System.out.println("Planificacion terminada en tiempo de simulacion hasta " + tiempoEnsimulacion);
        messagingTemplate.convertAndSend("/algoritmo/estado",
                new RespuestaAlgoritmoEstado("Planificacion terminada hasta " + tiempoEnsimulacion, simulacion));

    }

    private Date agregarHoras(Date tiempoEnsimulacion, int i) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(tiempoEnsimulacion);
        calendar.add(Calendar.HOUR_OF_DAY, i);
        return calendar.getTime();
    }

    private void realizarGuardado(ArrayList<Paquete> paquetesTotal, ArrayList<PlanRutaNT> planRutaNTs,
            ArrayList<Paquete> paquetesProcesar,
            RespuestaAlgoritmo respuestaAlgoritmo, Simulacion simulacion, PaqueteService paqueteService,
            PlanRutaService planRutaService, VueloService vueloService, PlanRutaXVueloService planRutaXVueloService,
            String canal) {

        for (int idx = 0; idx < respuestaAlgoritmo.getPlanesRutas().size(); idx++) {

            PlanRutaNT planRutaNT = respuestaAlgoritmo.getPlanesRutas().get(idx);
            int index = paquetesTotal.indexOf(paquetesProcesar.get(idx));
            // Verificar si tenia ruta

            if (planRutaNTs.get(index) != null) {
                // Eliminar antigua ruta del paquete
                // planRutaService.delete(paquetesTotal.get(index).getPlanRutaActual().getId());
            }
            planRutaNTs.set(index, planRutaNT);

            // Crear y guardar PlanRuta
            planRutaNT.updateCodigo();

            // LOGGER.info("GUARDADO || SIMULACION SEMANAL || PlanRutaNT: " +
            // planRutaNT.toString());

            PlanRuta planRuta = new PlanRuta();
            planRuta.setCodigo(planRutaNT.getCodigo());
            planRuta.setSimulacionActual(simulacion);
            try {
                planRuta = planRutaService.register(planRuta);
                // LOGGER.info("GUARDADO || SIMULACION SEMANAL || PlanRuta: " +
                // planRuta.toString());
            } catch (PersistenceException e) {
                // Manejo de errores si algo sale mal durante la operación de guardado
                System.err.println("Error al guardar en la base de datos: " + e.getMessage());
                messagingTemplate.convertAndSend(canal,
                        new RespuestaAlgoritmoEstado("Error al guardar algun plan ruta: " + e.getMessage(),
                                simulacion));
            }

            // Actualizar paquete
            Paquete paquete = paquetesProcesar.get(idx);
            /*
             * paquetesProcesar.get(idx).setFechaDeEntrega(planRutaNT.getFin());
             * paquetesProcesar.get(idx).setSimulacionActual(simulacion);
             * paquetesProcesar.get(idx).setPlanRutaActual(planRuta);
             */
            paquete.setFechaDeEntrega(planRutaNT.getFin());
            paquete.setSimulacionActual(simulacion);
            paquete.setPlanRutaActual(planRuta);
            // LOGGER.info("GUARDADO || SIMULACION SEMANAL || Paquete: " +
            // paquete.toString());

            try {
                Paquete paqueteBD = paqueteService.update(paquete);
                // LOGGER.info("GUARDADO || SIMULACION SEMANAL || (PAQUETE) " +
                // paqueteBD.toString() + " || (PlanRutaActual) " +
                // paqueteBD.getPlanRutaActual().toString() + " || (SimulacionActual) " +
                // paqueteBD.getSimulacionActual().getId() + " || (FechaDeEntrega) " +
                // paqueteBD.getFechaDeEntrega());
            } catch (Exception e) {
                // Manejo de errores si algo sale mal durante la operación de guardado
                System.err.println("Error al guardar en la base de datos: " + e.getMessage());
                messagingTemplate.convertAndSend(canal,
                        new RespuestaAlgoritmoEstado("Error al guardar algun paquete: " + e.getMessage(), simulacion));
            }

            // Asociar cada PlanRuta con sus vuelos
            for (Vuelo vuelo : planRutaNT.getVuelos()) {
                PlanRutaXVuelo planRutaXVuelo = new PlanRutaXVuelo();
                planRutaXVuelo.setPlanRuta(planRuta);
                planRutaXVuelo.setVuelo(vuelo);
                planRutaXVuelo.setIndiceDeOrden(planRutaNT.getVuelos().indexOf(vuelo));
                // System.out.println("SIMULACION PlanRutaXVuelo: " +
                // planRutaXVuelo.getPlanRuta().getCodigo() + " " +
                // planRutaXVuelo.getVuelo().getId() + " " + planRutaXVuelo.getIndiceDeOrden());

                try {
                    planRutaXVueloService.register(planRutaXVuelo);
                } catch (PersistenceException e) {
                    // Manejo de errores si algo sale mal durante la operación de guardado
                    System.err.println("Error al guardar en la base de datos: " + e.getMessage());
                    messagingTemplate.convertAndSend(canal,
                            new RespuestaAlgoritmoEstado("Error al guardar algun plan ruta x vuelo: " + e.getMessage(),
                                    simulacion));
                }
            }

        }
        LOGGER.info("GUARDADO || SIMULACION SEMANAL || Realizado");

    }

    private ArrayList<Paquete> actualizarPaquetesDiaDia(ArrayList<Paquete> paquetes,
            HashMap<Integer, PlanRutaNT> hashPlanRutasNT,
            Date fechaCorte, AeropuertoService aeropuertoService, PaqueteService paqueteService) {
        for (int i = 0; i < paquetes.size(); i++) {
            if (hashPlanRutasNT != null) {
                boolean primero = true;
                PlanRutaNT planRutaNT = hashPlanRutasNT.get(paquetes.get(i).getId());
                if (planRutaNT != null) {
                    for (Vuelo vuelo : planRutaNT.getVuelos()) {
                        if (vuelo.getFechaSalida().after(fechaCorte)) {

                            paquetes.get(i).setAeropuertoActual(
                                    aeropuertoService.findByUbicacion(vuelo.getPlanVuelo().getCiudadOrigen().getId()));
                            paquetes.get(i).setEnAeropuerto(true);
                            if (primero) {
                                paquetes.get(i).setEstado("En aeropuerto origen");
                            } else {
                                paquetes.get(i).setEstado("En espera");

                            }
                        } else if (vuelo.getFechaLlegada().after(fechaCorte)) {
                            paquetes.get(i).setEstado("En vuelo");
                            paquetes.get(i).setAeropuertoActual(null);
                            paquetes.get(i).setEnAeropuerto(false);
                        } else {
                            paquetes.get(i).setEstado("Entregado");
                            paquetes.get(i).setEntregado(true);
                            paquetes.get(i).setAeropuertoActual(null);
                            paquetes.get(i).setEnAeropuerto(false);
                            paqueteService.update(paquetes.get(i));
                        }

                        primero = false;
                    }
                }

            }

        }
        return paquetes;
    }

    private ArrayList<Paquete> actualizarPaquetes(ArrayList<Paquete> paquetes, ArrayList<PlanRutaNT> planRutaNTs,
            Date fechaEnSimulacion, AeropuertoService aeropuertoService) {
        ArrayList<Paquete> paquetesActualizar = (ArrayList<Paquete>) paquetes.stream()
                .filter(p -> p.getFechaDeEntrega() != null && p.getFechaDeEntrega().before(fechaEnSimulacion))
                .collect(Collectors.toList());
        for (int i = 0; i < paquetesActualizar.size(); i++) {
            if (planRutaNTs != null) {
                boolean primero = true;
                for (Vuelo vuelo : planRutaNTs.get(i).getVuelos()) {
                    if (vuelo.getFechaSalida().after(fechaEnSimulacion)) {

                        paquetes.get(i).setAeropuertoActual(
                                aeropuertoService.findByUbicacion(vuelo.getPlanVuelo().getCiudadOrigen().getId()));
                        paquetes.get(i).setEnAeropuerto(true);
                        if (primero) {
                            paquetes.get(i).setEstado("En aeropuerto origen");
                        } else {
                            paquetes.get(i).setEstado("En espera");

                        }
                    } else if (vuelo.getFechaLlegada().after(fechaEnSimulacion)) {
                        paquetes.get(i).setEstado("En vuelo");
                        paquetes.get(i).setAeropuertoActual(null);
                        paquetes.get(i).setEnAeropuerto(false);
                    } else {
                        paquetes.get(i).setEstado("Entregado");
                        paquetes.get(i).setEntregado(true);
                        paquetes.get(i).setAeropuertoActual(null);
                        paquetes.get(i).setEnAeropuerto(false);
                    }

                    primero = false;
                }
            }

        }
        return paquetes;
    }

    public Paquete finPaqueteByID(ArrayList<Paquete> paquetes, int idBuscado) {
        Paquete paqueteEncontrado = null;

        for (Paquete paquete : paquetes) {
            if (paquete.getId() == idBuscado) {
                paqueteEncontrado = paquete;
            }
        }

        return paqueteEncontrado;
    }

    public static RespuestaAlgoritmo procesarPaquetes(GrafoVuelos grafoVuelos,
            HashMap<Integer, Integer> ocupacionVuelos, ArrayList<Paquete> paquetes, ArrayList<PlanRutaNT> planRutaNTs,
            ArrayList<Aeropuerto> aeropuertos, ArrayList<PlanVuelo> planVuelos, int tamanhoPaquetes, int iteracion,
            VueloService vueloService, PlanRutaService planRutaService, SimulacionService simulacionService,
            Simulacion simulacion, SimpMessagingTemplate messagingTemplate, String tipoOperacion,
            Date tiempoEnSimulacion, int TA) {
        // Simmulated Annealing Parameters
        double temperature = 1500;
        double coolingRate = 0.08;
        int neighbourCount = 1;
        int windowSize = tamanhoPaquetes / 3;
        boolean stopWhenNoPackagesLeft = true;

        // Weight Parameters
        double badSolutionPenalization = 100;
        double flightPenalization = 200;
        double airportPenalization = 300;

        // funcion_fitness = (SUMA_TOTAL_PAQUETES) * 10 + (SUMA_TOTAL_VUELOS) * 4 +
        // (PROMEDIO_PONDERADO_TIEMPO_AEROPUERTO) * 4
        double sumaPaquetesWeight = 10;
        double sumaVuelosWeight = 6;
        double promedioPonderadoTiempoAeropuertoWeight = 4;
        double mediaVuelosWight = 4;

        SAImplementation sa = new SAImplementation();
        sa.setData(
                aeropuertos,
                planVuelos,
                paquetes,
                planRutaNTs,
                ocupacionVuelos,
                tiempoEnSimulacion);

        sa.setParameters(
                stopWhenNoPackagesLeft,
                temperature,
                coolingRate,
                neighbourCount,
                windowSize,
                badSolutionPenalization,
                flightPenalization,
                airportPenalization,
                sumaPaquetesWeight,
                sumaVuelosWeight,
                promedioPonderadoTiempoAeropuertoWeight,
                mediaVuelosWight);

        return sa.startAlgorithm(grafoVuelos, vueloService, simulacionService, planRutaService, simulacion, iteracion,
                messagingTemplate, tipoOperacion, TA);
    }

    public static RespuestaAlgoritmo procesarPaquetesColapso(GrafoVuelos grafoVuelos,
            HashMap<Integer, Integer> ocupacionVuelos, ArrayList<Paquete> paquetes, ArrayList<PlanRutaNT> planRutaNTs,
            ArrayList<Aeropuerto> aeropuertos, ArrayList<PlanVuelo> planVuelos, int tamanhoPaquetes, int iteracion,
            VueloService vueloService, PlanRutaService planRutaService, SimulacionService simulacionService,
            Simulacion simulacion, SimpMessagingTemplate messagingTemplate, String tipoOperacion,
            Date tiempoEnSimulacion, int TA) {
        // Simmulated Annealing Parameters
        double temperature = 1000;
        double coolingRate = 0.08;
        int neighbourCount = 1;
        int windowSize = tamanhoPaquetes / 3;
        boolean stopWhenNoPackagesLeft = true;

        // Weight Parameters
        double badSolutionPenalization = 100;
        double flightPenalization = 200;
        double airportPenalization = 300;

        // funcion_fitness = (SUMA_TOTAL_PAQUETES) * 10 + (SUMA_TOTAL_VUELOS) * 4 +
        // (PROMEDIO_PONDERADO_TIEMPO_AEROPUERTO) * 4
        double sumaPaquetesWeight = 10;
        double sumaVuelosWeight = 6;
        double promedioPonderadoTiempoAeropuertoWeight = 4;
        double mediaVuelosWight = 50;

        SAImplementation sa = new SAImplementation();
        sa.setData(
                aeropuertos,
                planVuelos,
                paquetes,
                planRutaNTs,
                ocupacionVuelos,
                tiempoEnSimulacion);

        sa.setParameters(
                stopWhenNoPackagesLeft,
                temperature,
                coolingRate,
                neighbourCount,
                windowSize,
                badSolutionPenalization,
                flightPenalization,
                airportPenalization,
                sumaPaquetesWeight,
                sumaVuelosWeight,
                promedioPonderadoTiempoAeropuertoWeight,
                mediaVuelosWight);

        return sa.startAlgorithm(grafoVuelos, vueloService, simulacionService, planRutaService, simulacion, iteracion,
                messagingTemplate, tipoOperacion, TA);
    }

    public RespuestaAlgoritmo getUltimaRespuestaOperacionDiaDia() {
        this.ultimaRespuestaOperacionDiaDia.setFechaActualDiaDia(timeService.now());
        return this.ultimaRespuestaOperacionDiaDia;
    }

    public HashMap<Integer, List<Paquete>> getPaquetes_por_simulacion() {
        return paquetes_por_simulacion;
    }

    public List<Paquete> obtener_paquetes_simulacion(Integer id_simulacion) {
        return paquetes_por_simulacion.get(id_simulacion);
    }

    public List<Envio> obtener_envios_simulacion(Integer id_simulacion) {
        List<Envio> envios = new ArrayList<>();
        List<Paquete> paquetes = paquetes_por_simulacion.get(id_simulacion);
        if (paquetes == null) {
            return envios;
        }

        Map<Integer, Envio> enviosMap = new HashMap<>();
        for (Paquete paquete : paquetes) {
            int envioId = paquete.getEnvio().getId();
            if (!enviosMap.containsKey(envioId)) {
                Envio envio = paquete.getEnvio();
                enviosMap.put(envioId, envio);
            }
        }
        envios.addAll(enviosMap.values());
        return envios;
    }

    public List<Envio> obtener_envios_dia_dia() {
        List<Envio> envios = new ArrayList<>();
        // List<Paquete> paquetes = this.paquetesOpDiaDia;
        Collection<Paquete> paquetesCollection = this.hashTodosPaquetesDiaDia.values();
        List<Paquete> paquetes = new ArrayList<>(paquetesCollection);
        if (paquetes.isEmpty()) {
            return envios;
        }
        // Date now = new Date();
        Date now = timeService.now();
        paquetes = paquetes.stream()
                .filter(p -> p.obtenerFechaRecepcion().before(now)
                        && (p.isEntregado() == false))
                .collect(Collectors.toList());

        Map<Integer, Envio> enviosMap = new HashMap<>();
        for (Paquete paquete : paquetes) {
            int envioId = paquete.getEnvio().getId();
            if (!enviosMap.containsKey(envioId)) {
                Envio envio = paquete.getEnvio();
                enviosMap.put(envioId, envio);
            }
        }
        envios.addAll(enviosMap.values());
        return envios;
    }

    public void setPaquetes_por_simulacion(HashMap<Integer, List<Paquete>> paquetes_por_simulacion) {
        this.paquetes_por_simulacion = paquetes_por_simulacion;
    }

    public List<Paquete> obtenerPaquetesActualesDiaDia(PaqueteService paqueteService) {
        // test time
        long start = System.currentTimeMillis();
        Collection<Paquete> paquetesCollection = this.hashTodosPaquetesDiaDia.values();
        List<Paquete> paquetes = new ArrayList<>(paquetesCollection);
        if (paquetes.isEmpty()) {
            return paquetes;
        }
        // Date now = new Date();
        Date now = timeService.now();
        paquetes = paquetes.stream()
                .filter(p -> p.obtenerFechaRecepcion().before(now)
                        && (p.isEntregado() == false))
                .collect(Collectors.toList());
        long end1 = System.currentTimeMillis();
        LOGGER.info("Algoritmo.obtenerPaquetesActualesDiaDia - Tiempo de filtrado: " + (end1 - start) + " ms");
        /*
         * List<Paquete> paquetesRespuesta = new ArrayList<>();
         * for (Paquete p : paquetes) {
         * Paquete paqueteRespuesta = paqueteService.getPaqueteNoSimulacion(p.getId(),
         * now);
         * paquetesRespuesta.add(paqueteRespuesta);
         * }
         * long end2 = System.currentTimeMillis();
         * LOGGER.info("Algoritmo.obtenerPaquetesActualesDiaDia - Tiempo de consulta: "
         * + (end2 - end1) + " ms");
         * LOGGER.info("Algoritmo.obtenerPaquetesActualesDiaDia - Paquetes actuales: " +
         * paquetesRespuesta.size());
         * // && (p.getFechaDeEntrega() == null || !p.getFechaDeEntrega().before(now)))
         */
        return paquetes;
    }

    public EstadoAlmacen obtenerEstadoAlmacenDiaDia() {
        return this.estadoAlmacenOpDiaDia;
    }

    /*
     * public void actualizarEstadoAlmacenDiaDia(EstadoAlmacen estadoAlmacenDiaDia)
     * {
     * this.ultimaRespuestaOperacionDiaDia.setEstadoAlmacen(estadoAlmacenDiaDia);
     * messagingTemplate.convertAndSend("/algoritmo/diaDiaRespuesta",
     * this.ultimaRespuestaOperacionDiaDia);
     * messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado",
     * "Estado Almacen actualizado");
     * }
     */

    public ArrayList<Paquete> obtenerPaquetesEnAeropuerto(String aeropuertoId, Simulacion simulacion) {
        ArrayList<Paquete> paquetesEnAeropuerto = new ArrayList<>();
        Date fechaCorte = calcularTiempoSimulacionBack(simulacion, 60000);

        for (int i = 0; i < paquetesSimulacion.size(); i++) {
            Paquete paquete = paquetesSimulacion.get(i);
            ArrayList<Vuelo> vuelos = new ArrayList<>();
            if (planRutasSimulacion.get(i) != null) {
                vuelos = planRutasSimulacion.get(i).getVuelos();
            }

            Collections.sort(vuelos, Comparator.comparing(Vuelo::getFechaSalida));

            boolean enAeropuerto = false;
            if (vuelos.isEmpty() || vuelos.size() == 0 || vuelos.get(0).getFechaSalida().after(fechaCorte)) {
                // No hay vuelos asignados, o aun no ha tomado vuelos, verificar la ciudad de
                // origen y la fecha de
                // recepción
                if (paquete.getEnvio().getUbicacionOrigen().getId().equals(aeropuertoId)
                        && !paquete.obtenerFechaRecepcion().after(fechaCorte)) {
                    enAeropuerto = true;
                }
            } else {
                for (Vuelo vuelo : vuelos) {
                    // Verificar si ya aterrizo el ultimo vuelo del arreglo
                    if (vuelos.indexOf(vuelo) == vuelos.size() - 1) {
                        break;
                    }
                    // El paquete está en el aeropuerto entre la llegada de un vuelo y la salida del
                    // siguiente
                    if (vuelo.getPlanVuelo().getCiudadDestino().getId().equals(aeropuertoId) &&
                            !vuelo.getFechaLlegada().after(fechaCorte) &&
                            (vuelos.get(vuelos.indexOf(vuelo) + 1).getFechaSalida().after(fechaCorte))) {
                        enAeropuerto = true;
                        break;
                    }
                }
            }

            if (enAeropuerto) {
                paquetesEnAeropuerto.add(paquete);
            }
        }
        return paquetesEnAeropuerto;
    }

    public ArrayList<Paquete> obtenerPaquetesEnAeropuertoDiaDia(String aeropuertoId, PaqueteService paqueteService) {
        ArrayList<Paquete> paquetesEnAeropuerto = new ArrayList<>();
        // Date fechaCorte = new Date();
        Date fechaCorte = timeService.now();
        List<Paquete> paquetes = new ArrayList<>();
        List<PlanRutaNT> planRutas = new ArrayList<>();
        /*
         * for (Entry<Integer, Paquete> entry : hashTodosPaquetesDiaDia.entrySet()){
         * if(entry.getValue().getEnvio().getFechaRecepcion().before(fechaCorte)){
         * paquetes.add(entry.getValue());
         * planRutas.add(hashPlanRutasNTDiaDia.get(entry.getKey()));
         * }
         * }
         */

        hashTodosPaquetesDiaDia.entrySet().stream()
                .filter(entry -> entry.getValue().getEnvio().getFechaRecepcion().before(fechaCorte))
                .forEach(entry -> {
                    paquetes.add(entry.getValue());
                    planRutas.add(hashPlanRutasNTDiaDia.get(entry.getKey()));
                });

        for (int i = 0; i < paquetes.size(); i++) {
            Paquete paquete = paquetes.get(i);
            ArrayList<Vuelo> vuelos = new ArrayList<>();
            if (planRutas.get(i) != null) {
                vuelos = planRutas.get(i).getVuelos();
            }

            Collections.sort(vuelos, Comparator.comparing(Vuelo::getFechaSalida));

            boolean enAeropuerto = false;
            if (vuelos.isEmpty() || vuelos.size() == 0 || vuelos.get(0).getFechaSalida().after(fechaCorte)) {
                // No hay vuelos asignados, o aun no ha tomado vuelos, verificar la ciudad de
                // origen y la fecha de
                // recepción
                if (paquete.getEnvio().getUbicacionOrigen().getId().equals(aeropuertoId)
                        && !paquete.obtenerFechaRecepcion().after(fechaCorte)) {
                    enAeropuerto = true;
                }
            } else {
                for (Vuelo vuelo : vuelos) {
                    // Verificar si ya aterrizo el ultimo vuelo del arreglo
                    if (vuelos.indexOf(vuelo) == vuelos.size() - 1) {
                        break;
                    }
                    // El paquete está en el aeropuerto entre la llegada de un vuelo y la salida del
                    // siguiente
                    if (vuelo.getPlanVuelo().getCiudadDestino().getId().equals(aeropuertoId) &&
                            !vuelo.getFechaLlegada().after(fechaCorte) &&
                            (vuelos.get(vuelos.indexOf(vuelo) + 1).getFechaSalida().after(fechaCorte))) {
                        enAeropuerto = true;
                        break;
                    }
                }
            }

            if (enAeropuerto) {
                // Paquete paqueteBD = paqueteService.findById(paquete.getId());
                Paquete paqueteBD = paqueteService.getPaqueteNoSimulacion(paquete.getId(), fechaCorte);
                paquetesEnAeropuerto.add(paqueteBD);
            }
        }
        return paquetesEnAeropuerto;
    }

    public void agregarPaquetesEstadoAlmacenDiaDia(String aeropuerto, Date fechaRecepcion, int cantidadPaquetes) {
        this.estadoAlmacenOpDiaDia.registrarCapacidadOperacionesDiaDia(aeropuerto, removeTime(fechaRecepcion),
                cantidadPaquetes);
    }

    public void agregarPaqueteEnAeropuertoDiaDia(Paquete paquete) {
        // this.paquetesOpDiaDia.add(paquete);
        // this.planRutasOpDiaDia.add(new PlanRutaNT());

        while (true) {
            if (this.isPuedeRecibirPaquetesDiaDia()) {
                // LOGGER.info("Envio: " + envio.getId() + " - Fecha de recepcion: " +
                // envio.getFechaRecepcion() + " Va a guardar paquetes");
                break;
            } else {
                LOGGER.info("DIA DIA Paquete: " + paquete.getId() + " NO PUEDE guardar paquetes AHORA");
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    System.out.println("Error en sleep");
                }
                continue;
            }
        }

        this.hashTodosPaquetesDiaDia.put(paquete.getId(), paquete);
        this.hashPlanRutasNTDiaDia.put(paquete.getId(), new PlanRutaNT());

        // String aeropuerto = paquete.getEnvio().getUbicacionOrigen().getId();
        // EstadoAlmacen estado =
        // this.ultimaRespuestaOperacionDiaDia.getEstadoAlmacen();

        // this.estadoAlmacenOpDiaDia.registrarCapacidadOperacionesDiaDia(aeropuerto,
        // removeTime(paquete.getEnvio().getFechaRecepcion()), 1);

        // this.estadoAlmacenOpDiaDia.sumaCalculada();
        // this.estadoAlmacenOpDiaDia.registrarCapacidad(aeropuerto,
        // paquete.getEnvio().getFechaRecepcion(), 1);

        // estado.registrarCapacidad(aeropuertoSalida,
        // removeTime(paquete.getEnvio().getFechaRecepcion()), 1);
        // this.ultimaRespuestaOperacionDiaDia.setEstadoAlmacen(estado);
        // messagingTemplate.convertAndSend("/algoritmo/diaDiaRespuesta",
        // this.ultimaRespuestaOperacionDiaDia);
        // messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado",
        // "Paquete " + paquete.getId() + " agregado al aeropuerto " +
        // aeropuertoSalida);
    }

    public void enviarEstadoAlmacenSocketDiaDiaPorEnvio(Integer id, Integer cantidadPaquetes, String aeropuerto) {
        this.ultimaRespuestaOperacionDiaDia.setEstadoAlmacen(this.estadoAlmacenOpDiaDia);
        if(this.estadoAlmacenOpDiaDia.getAeropuertos()!=null){
            boolean colapsoAlmacen = !(this.estadoAlmacenOpDiaDia.verificar_capacidad_maxima());
            if(colapsoAlmacen){
                System.out.println("Colapso de almacen");
                this.ultimaRespuestaOperacionDiaDia.setCorrecta(false);
            }
        }
        messagingTemplate.convertAndSend("/algoritmo/diaDiaRespuesta", this.ultimaRespuestaOperacionDiaDia);
        messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado",
                "Envio ID " + id + " con " + cantidadPaquetes + "paquete(s) agregado(s) al aeropuerto " + aeropuerto);
        // this.estadoAlmacenOpDiaDia.consulta_historicaTxt("ocupacionAeropuertosDiaDia"
        // + nConsultasDiaDia + ".txt");
        this.nConsultasDiaDia++;
    }

    public void enviarEstadoAlmacenSocketDiaDiaPorCarga(int cantidadEnvios, int cantidadPaquetes) {
        this.ultimaRespuestaOperacionDiaDia.setEstadoAlmacen(this.estadoAlmacenOpDiaDia);
        if(this.estadoAlmacenOpDiaDia.getAeropuertos()!=null){
            boolean colapsoAlmacen = !(this.estadoAlmacenOpDiaDia.verificar_capacidad_maxima());
            if(colapsoAlmacen){
                System.out.println("Colapso de almacen");
                this.ultimaRespuestaOperacionDiaDia.setCorrecta(false);
            }
        }
        
        messagingTemplate.convertAndSend("/algoritmo/diaDiaRespuesta", this.ultimaRespuestaOperacionDiaDia);
        messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado",
                cantidadEnvios + " envio(s), " + cantidadPaquetes + " paquete(s) agregado(s) ");
        // this.estadoAlmacenOpDiaDia.consulta_historicaTxt("ocupacionAeropuertosDiaDia"
        // + nConsultasDiaDia + ".txt");
        this.nConsultasDiaDia++;
    }

    private Date removeTime(Date date) {
        // Obtener una instancia de Calendar y establecer la fecha dada
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Ajustar las horas, minutos, segundos y milisegundos a cero
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Obtener la nueva fecha sin tiempo
        return calendar.getTime();
    }

    public boolean isPuedeRecibirPaquetesDiaDia() {
        return puedeRecibirPaquetesDiaDia;
    }

    public void setPuedeRecibirPaquetesDiaDia(boolean puedeRecibirPaquetesDiaDia) {
        this.puedeRecibirPaquetesDiaDia = puedeRecibirPaquetesDiaDia;
    }

    public ArrayList<Paquete> getPaquetesProcesadosUltimaSimulacion() {
        return paquetesProcesadosUltimaSimulacion;
    }

    public void setPaquetesProcesadosUltimaSimulacion(ArrayList<Paquete> paquetesProcesadosUltimaSimulacion) {
        this.paquetesProcesadosUltimaSimulacion = paquetesProcesadosUltimaSimulacion;
    }

    public boolean isOperacionDiaDiaActivo() {
        return operacionDiaDiaActivo;
    }

    public void setOperacionDiaDiaActivo(boolean operacionDiaDiaActivo) {
        this.operacionDiaDiaActivo = operacionDiaDiaActivo;
    }

    

}
