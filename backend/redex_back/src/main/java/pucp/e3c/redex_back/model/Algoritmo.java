package pucp.e3c.redex_back.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import java.lang.Thread;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import jakarta.persistence.PersistenceException;
import pucp.e3c.redex_back.service.PaqueteService;
import pucp.e3c.redex_back.service.PlanRutaService;
import pucp.e3c.redex_back.service.PlanRutaXVueloService;
import pucp.e3c.redex_back.service.SimulacionService;
import pucp.e3c.redex_back.service.VueloService;

@EnableAsync
public class Algoritmo {

    private final SimpMessagingTemplate messagingTemplate;

    public Algoritmo(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    private Date calcularTiempoSimulacion(Simulacion simulacion) {
        long tiempoActual = new Date().getTime();
        long inicioSistema = simulacion.getFechaInicioSistema().getTime();
        long inicioSimulacion = simulacion.getFechaInicioSim().getTime();
        long milisegundosPausados = simulacion.getMilisegundosPausados();
        long multiplicador = (long) simulacion.getMultiplicadorTiempo();
        return new Date(inicioSimulacion
                + (tiempoActual - inicioSistema - milisegundosPausados) * multiplicador);
    }

    private Date agregarSAyTA(Date fechaEnSimulacion, int TA, int SA, double multiplicador) {
        // Supongamos que tienes una fecha, por ejemplo:

        // Crea un objeto Calendar y establece la fecha
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaEnSimulacion);

        // Añade 10 minutos a la fecha
        calendar.add(Calendar.SECOND, (TA + SA) * (int) multiplicador);

        // Obtiene la nueva fecha con los minutos añadidos
        Date fechaActualizada = calendar.getTime();
        return fechaActualizada;
    }
 /*
    public ArrayList<PlanRutaNT> loopPrincipalDiaADia(ArrayList<Aeropuerto> aeropuertos, ArrayList<PlanVuelo> planVuelos,
             VueloService vueloService, PlanRutaService planRutaService,
            PaqueteService paqueteService, PlanRutaXVueloService planRutaXVueloService,
            SimulacionService simulacionService,
            Simulacion simulacion, int SA, int TA) {
        messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado", "Iniciando loop principal");

        ArrayList<PlanRutaNT> planRutas = new ArrayList<>();

        if (planVuelos.size() == 0) {
            System.out.println("ERROR: No hay planes de vuelo para procesar.");
            messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado", "Detenido, sin planes vuelo");
            return null;
        }

        HashMap<Integer, Integer> ocupacionVuelos = new HashMap<Integer, Integer>();
        GrafoVuelos grafoVuelos = new GrafoVuelos(planVuelos, paquetes);
        if (grafoVuelos.getVuelosHash() == null || grafoVuelos.getVuelosHash().size() <= 0) {
            System.out.println("ERROR: No se generaron vuelos.");
            messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado", "Detenido, error en generar vuelos");
            return null;
        }
        int i = 0;

        Date fechaSgteCalculo = simulacion.getFechaInicioSim();
        Date tiempoEnSimulacion = simulacion.getFechaInicioSim();
        boolean primera_iter = true;
        while (true) {
            ArrayList<Paquete> paquetes = paqueteService.findPaquetesSinSimulacionYNoEntregados();
            System.out.println("Planificacion iniciada");
            messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado", "Planificacion iniciada");

            // Filtrar paquetes a calcular
            Collections.sort(paquetes, Comparator.comparing(Paquete::getFechaRecepcion));

            final Date finalTiempoEnSimulacion = tiempoEnSimulacion;
            List<Paquete> paquetesTemp = paquetes.stream()
                    .filter(p -> p.getFechaDeEntrega() == null || finalTiempoEnSimulacion.before(p.getFechaDeEntrega()))
                    .filter(p -> p.getFechaRecepcion().before(fechaLimiteCalculo))
                    .collect(Collectors.toList());
            ArrayList<Paquete> paquetesProcesar = new ArrayList<>(paquetesTemp);

            int tamanhoPaquetes = paquetesProcesar.size();

            if (tamanhoPaquetes == 0) {
                messagingTemplate.convertAndSend("/algoritmo/estado", "No hay mas paquetes, terminando");
                System.out.println("No hay mas paquetes, terminando");
                simulacion.setEstado(1);
                break;
            }
            System.out.println("LLegue aqui");


            // Recalcular el tamanho de paquetes
            // tamanhoPaquetes = paquetesProcesar.size();

            System.out.println("Se van a procesar " + tamanhoPaquetes + " paquetes, hasta " + fechaLimiteCalculo);

            // Realizar planificacion
            RespuestaAlgoritmo respuestaAlgoritmo = procesarPaquetes(grafoVuelos, ocupacionVuelos, paquetesProcesar,
                    aeropuertos, planVuelos,
                    tamanhoPaquetes, i, vueloService, planRutaService, simulacion, messagingTemplate);

            for (int idx = 0; idx < respuestaAlgoritmo.getPlanesRutas().size(); idx++) {
                PlanRutaNT planRutaNT = respuestaAlgoritmo.getPlanesRutas().get(idx);

                // Crear y guardar PlanRuta
                planRutaNT.updateCodigo();
                PlanRuta planRuta = new PlanRuta();
                planRuta.setCodigo(planRutaNT.getCodigo());
                planRuta.setSimulacionActual(simulacion);
                try {
                    planRuta = planRutaService.register(planRuta);
                } catch (PersistenceException e) {
                    // Manejo de errores si algo sale mal durante la operación de guardado
                    System.err.println("Error al guardar en la base de datos: " + e.getMessage());
                    messagingTemplate.convertAndSend("/algoritmo/estado",
                            "Error al guardar algun plan ruta: " + e.getMessage());
                }

                // Actualizar paquete
                paquetes.get(i).setFechaDeEntrega(planRutaNT.getFin());
                paquetes.get(i).setSimulacionActual(simulacion);
                paquetes.get(i).setPlanRutaActual(planRuta);

                try {
                    paqueteService.update(paquetes.get(i));
                } catch (Exception e) {
                    // Manejo de errores si algo sale mal durante la operación de guardado
                    System.err.println("Error al guardar en la base de datos: " + e.getMessage());
                    messagingTemplate.convertAndSend("/algoritmo/estado",
                            "Error al guardar algun paquete: " + e.getMessage());
                }

                // Asociar cada PlanRuta con sus vuelos
                for (Vuelo vuelo : planRutaNT.getVuelos()) {
                    vuelo = vueloService.register(vuelo);
                    PlanRutaXVuelo planRutaXVuelo = new PlanRutaXVuelo();
                    planRutaXVuelo.setPlanRuta(planRuta);
                    planRutaXVuelo.setVuelo(vuelo);
                    planRutaXVuelo.setIndiceDeOrden(planRutaNT.getVuelos().indexOf(vuelo));

                    try {
                        planRutaXVueloService.register(planRutaXVuelo);
                    } catch (PersistenceException e) {
                        // Manejo de errores si algo sale mal durante la operación de guardado
                        System.err.println("Error al guardar en la base de datos: " + e.getMessage());
                        messagingTemplate.convertAndSend("/algoritmo/estado",
                                "Error al guardar algun plan ruta x vuelo: " + e.getMessage());
                    }
                }

            }
            // Solo en la primera iter, definir el inicio de la simulacion
            if (primera_iter) {
                simulacion.setFechaInicioSistema(new Date());
                primera_iter = false;

            }

            // Formar respuesta a front
            respuestaAlgoritmo.setSimulacion(simulacion);
            messagingTemplate.convertAndSend("/algoritmo/respuesta", respuestaAlgoritmo);
            System.out.println("Planificacion terminada en tiempo de simulacion hasta " + fechaLimiteCalculo);
            messagingTemplate.convertAndSend("/algoritmo/estado",
                    "Planificacion terminada hasta " + fechaLimiteCalculo);

            System.out.println("Proxima planificacion en tiempo de simulacion " + fechaSgteCalculo);
            planRutas.addAll(respuestaAlgoritmo.getPlanesRutas());

            tiempoEnSimulacion = calcularTiempoSimulacion(simulacion);

            try {
                Thread.sleep(10000);
            } catch (Exception e) {
                System.out.println("Error en sleep");
            }
        }
        return planRutas;

    }

*/
    public ArrayList<PlanRutaNT> loopPrincipal(ArrayList<Aeropuerto> aeropuertos, ArrayList<PlanVuelo> planVuelos,
            ArrayList<Paquete> paquetes, VueloService vueloService, PlanRutaService planRutaService,
            PaqueteService paqueteService, PlanRutaXVueloService planRutaXVueloService,
            SimulacionService simulacionService,
            Simulacion simulacion, int SA, int TA) {
        messagingTemplate.convertAndSend("/algoritmo/estado", "Iniciando loop principal");

        ArrayList<PlanRutaNT> planRutas = new ArrayList<>();

        if (paquetes.size() == 0) {
            System.out.println("ERROR: No hay paquetes para procesar.");
            messagingTemplate.convertAndSend("/algoritmo/estado", "Detenido, sin paquetes");
            return null;
        }
        if (planVuelos.size() == 0) {
            System.out.println("ERROR: No hay planes de vuelo para procesar.");
            messagingTemplate.convertAndSend("/algoritmo/estado", "Detenido, sin planes vuelo");
            return null;
        }

        HashMap<Integer, Integer> ocupacionVuelos = new HashMap<Integer, Integer>();
        GrafoVuelos grafoVuelos = new GrafoVuelos(planVuelos, paquetes);
        if (grafoVuelos.getVuelosHash() == null || grafoVuelos.getVuelosHash().size() <= 0) {
            System.out.println("ERROR: No se generaron vuelos.");
            messagingTemplate.convertAndSend("/algoritmo/estado", "Detenido, error en generar vuelos");
            return null;
        }
        int i = 0;

        Date fechaSgteCalculo = simulacion.getFechaInicioSim();
        Date tiempoEnSimulacion = simulacion.getFechaInicioSim();
        boolean primera_iter = true;
        while (true) {
            simulacion = simulacionService.get(simulacion.getId());
            if (simulacion.estado == 1) {
                System.out.println("Simulacion terminada");
                messagingTemplate.convertAndSend("/algoritmo/estado", "Simulacion terminada");
                break;
            }

            // Gestion de pausa
            if (simulacion.getEstado() == 2) {
                System.out.println("Simulacion pausada");
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    System.out.println("Error en sleep");
                }
                continue;
            }

            // Lapso de tiempo entre planificaciones
            if (tiempoEnSimulacion.before(fechaSgteCalculo)) {
                tiempoEnSimulacion = calcularTiempoSimulacion(simulacion);
                System.out.println("Aun no es tiempo de planificar, la fecha en simulacion es " + tiempoEnSimulacion);
                try {
                    Thread.sleep(10000);
                } catch (Exception e) {
                    System.out.println("Error en sleep");
                }
                continue;
            }

            // Calculo del limie de planificacion
            Date fechaLimiteCalculo = agregarSAyTA(tiempoEnSimulacion,TA, SA, simulacion.getMultiplicadorTiempo());
            fechaSgteCalculo = agregarSAyTA(tiempoEnSimulacion,0,SA, simulacion.getMultiplicadorTiempo());
            System.out.println("Planificacion iniciada");
            messagingTemplate.convertAndSend("/algoritmo/estado", "Planificacion iniciada");

            // Filtrar paquetes a calcular
            Collections.sort(paquetes, Comparator.comparing(Paquete::getFechaRecepcion));

            final Date finalTiempoEnSimulacion = tiempoEnSimulacion;
            List<Paquete> paquetesTemp = paquetes.stream()
                    .filter(p -> p.getFechaDeEntrega() == null || finalTiempoEnSimulacion.before(p.getFechaDeEntrega()))
                    .filter(p -> p.getFechaRecepcion().before(fechaLimiteCalculo))
                    .collect(Collectors.toList());
            ArrayList<Paquete> paquetesProcesar = new ArrayList<>(paquetesTemp);

            int tamanhoPaquetes = paquetesProcesar.size();

            if (tamanhoPaquetes == 0) {
                messagingTemplate.convertAndSend("/algoritmo/estado", "No hay mas paquetes, terminando");
                System.out.println("No hay mas paquetes, terminando");
                simulacion.setEstado(1);
                break;
            }
            System.out.println("LLegue aqui");
            // Filtrar paquetes que estan volando
            /*
             * for (Paquete paquete : paquetesProcesar) {
             * ArrayList<Vuelo> vuelos =
             * vueloService.findVuelosByPaqueteId(paquete.getId());
             * for (Vuelo vuelo : vuelos) {
             * if (vuelo.getFechaLlegada().after(tiempoEnSimulacion)
             * && vuelo.getFechaSalida().before(tiempoEnSimulacion)) {
             * paquetesProcesar.remove(paquete);
             * break;
             * }
             * }
             * }
             */

            // Recalcular el tamanho de paquetes
            // tamanhoPaquetes = paquetesProcesar.size();

            System.out.println("Se van a procesar " + tamanhoPaquetes + " paquetes, hasta " + fechaLimiteCalculo);

            // Realizar planificacion
            RespuestaAlgoritmo respuestaAlgoritmo = procesarPaquetes(grafoVuelos, ocupacionVuelos, paquetesProcesar,
                    aeropuertos, planVuelos,
                    tamanhoPaquetes, i, vueloService, planRutaService, simulacion, messagingTemplate);

            for (int idx = 0; idx < respuestaAlgoritmo.getPlanesRutas().size(); idx++) {
                PlanRutaNT planRutaNT = respuestaAlgoritmo.getPlanesRutas().get(idx);

                // Crear y guardar PlanRuta
                planRutaNT.updateCodigo();
                PlanRuta planRuta = new PlanRuta();
                planRuta.setCodigo(planRutaNT.getCodigo());
                planRuta.setSimulacionActual(simulacion);
                try {
                    planRuta = planRutaService.register(planRuta);
                } catch (PersistenceException e) {
                    // Manejo de errores si algo sale mal durante la operación de guardado
                    System.err.println("Error al guardar en la base de datos: " + e.getMessage());
                    messagingTemplate.convertAndSend("/algoritmo/estado",
                            "Error al guardar algun plan ruta: " + e.getMessage());
                }

                // Actualizar paquete
                paquetes.get(i).setFechaDeEntrega(planRutaNT.getFin());
                paquetes.get(i).setSimulacionActual(simulacion);
                paquetes.get(i).setPlanRutaActual(planRuta);

                try {
                    paqueteService.update(paquetes.get(i));
                } catch (Exception e) {
                    // Manejo de errores si algo sale mal durante la operación de guardado
                    System.err.println("Error al guardar en la base de datos: " + e.getMessage());
                    messagingTemplate.convertAndSend("/algoritmo/estado",
                            "Error al guardar algun paquete: " + e.getMessage());
                }

                // Asociar cada PlanRuta con sus vuelos
                for (Vuelo vuelo : planRutaNT.getVuelos()) {
                    vuelo = vueloService.register(vuelo);
                    PlanRutaXVuelo planRutaXVuelo = new PlanRutaXVuelo();
                    planRutaXVuelo.setPlanRuta(planRuta);
                    planRutaXVuelo.setVuelo(vuelo);
                    planRutaXVuelo.setIndiceDeOrden(planRutaNT.getVuelos().indexOf(vuelo));

                    try {
                        planRutaXVueloService.register(planRutaXVuelo);
                    } catch (PersistenceException e) {
                        // Manejo de errores si algo sale mal durante la operación de guardado
                        System.err.println("Error al guardar en la base de datos: " + e.getMessage());
                        messagingTemplate.convertAndSend("/algoritmo/estado",
                                "Error al guardar algun plan ruta x vuelo: " + e.getMessage());
                    }
                }

            }
            // Solo en la primera iter, definir el inicio de la simulacion
            if (primera_iter) {
                simulacion.setFechaInicioSistema(new Date());
                primera_iter = false;

            }

            // Formar respuesta a front
            respuestaAlgoritmo.setSimulacion(simulacion);
            messagingTemplate.convertAndSend("/algoritmo/respuesta", respuestaAlgoritmo);
            System.out.println("Planificacion terminada en tiempo de simulacion hasta " + fechaLimiteCalculo);
            messagingTemplate.convertAndSend("/algoritmo/estado",
                    "Planificacion terminada hasta " + fechaLimiteCalculo);

            System.out.println("Proxima planificacion en tiempo de simulacion " + fechaSgteCalculo);
            planRutas.addAll(respuestaAlgoritmo.getPlanesRutas());

            tiempoEnSimulacion = calcularTiempoSimulacion(simulacion);

        }
        return planRutas;

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
            HashMap<Integer, Integer> ocupacionVuelos, ArrayList<Paquete> paquetes,
            ArrayList<Aeropuerto> aeropuertos, ArrayList<PlanVuelo> planVuelos, int tamanhoPaquetes, int iteracion,
            VueloService vueloService, PlanRutaService planRutaService, Simulacion simulacion,
            SimpMessagingTemplate messagingTemplate) {
        // Simmulated Annealing Parameters
        double temperature = 1000;
        double coolingRate = 0.08;
        int neighbourCount = 10;
        int windowSize = tamanhoPaquetes / 5;
        boolean stopWhenNoPackagesLeft = true;

        // Weight Parameters
        double badSolutionPenalization = 100;
        double flightPenalization = 200;
        double airportPenalization = 300;

        // funcion_fitness = (SUMA_TOTAL_PAQUETES) * 10 + (SUMA_TOTAL_VUELOS) * 4 +
        // (PROMEDIO_PONDERADO_TIEMPO_AEROPUERTO) * 4
        double sumaPaquetesWeight = 10;
        double sumaVuelosWeight = 4;
        double promedioPonderadoTiempoAeropuertoWeight = 4;

        SAImplementation sa = new SAImplementation();

        sa.setData(
                aeropuertos,
                planVuelos,
                paquetes, ocupacionVuelos);

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
                promedioPonderadoTiempoAeropuertoWeight);

        return sa.startAlgorithm(grafoVuelos, vueloService, planRutaService, simulacion, iteracion, messagingTemplate);
    }
}
