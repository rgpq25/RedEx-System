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
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import jakarta.persistence.PersistenceException;
import pucp.e3c.redex_back.service.PaqueteService;
import pucp.e3c.redex_back.service.PlanRutaService;
import pucp.e3c.redex_back.service.PlanRutaXVueloService;
import pucp.e3c.redex_back.service.SimulacionService;
import pucp.e3c.redex_back.service.VueloService;

@Component
@EnableAsync
public class Algoritmo {

    private final SimpMessagingTemplate messagingTemplate;

    RespuestaAlgoritmo ultimaRespuestaOperacionDiaDia;

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

    public RespuestaAlgoritmo unaPlanificacionDiaDia(ArrayList<Aeropuerto> aeropuertos,
        ArrayList<PlanVuelo> planVuelos,
        VueloService vueloService, PlanRutaService planRutaService,
        PaqueteService paqueteService, PlanRutaXVueloService planRutaXVueloService){
        //messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado", "Iniciando loop principal");
        if (planVuelos.size() == 0) {
            System.out.println("ERROR: No hay planes de vuelo para procesar.");
            //messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado", "Detenido, sin planes vuelo");
            return null;
        }
        HashMap<Integer, Integer> ocupacionVuelos = new HashMap<Integer, Integer>();
        long start = System.currentTimeMillis();
        ArrayList<Paquete> paquetes = paqueteService.findPaquetesSinSimulacionYNoEntregados();
        if (paquetes.size() == 0) {
            System.out.println("No hay paquetes para procesar.");
            //messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado", "Detenido, sin paquetes");
            return null;
        }
 
        GrafoVuelos grafoVuelos = new GrafoVuelos(planVuelos, paquetes);
        if (grafoVuelos.getVuelosHash() == null || grafoVuelos.getVuelosHash().size() <= 0) {
            System.out.println("ERROR: No se generaron vuelos.");
            //messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado", "Detenido, error en generar vuelos");
            return null;
        }

        // Calculo del limie de planificacion
        System.out.println("Planificacion iniciada");
        //messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado", "Planificacion iniciada");

            // Filtrar paquetes a calcular
        Collections.sort(paquetes, Comparator.comparing(Paquete::getFechaRecepcion));
        List<Paquete> paquetesTemp = paquetes.stream()
                //.filter(p -> p.getFechaDeEntrega() == null)
                .collect(Collectors.toList());
        ArrayList<Paquete> paquetesProcesar = new ArrayList<>(paquetesTemp);

        int tamanhoPaquetes = paquetesProcesar.size();

        if (tamanhoPaquetes == 0) {
            //messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado", "No hay paquetes que planificar");
            System.out.println("No hay paquetes que planificar");
            return null;
        }
            // Filtrar paquetes que estan volando

        Date now = new Date();
            /*
             * for (Paquete paquete : paquetesProcesar) {
             * ArrayList<Vuelo> vuelos =
             * vueloService.findVuelosByPaqueteId(paquete.getId());
             * for (Vuelo vuelo : vuelos) {
             * if (vuelo.getFechaLlegada().after(now)
             * && vuelo.getFechaSalida().before(now)) {
             * paquetesProcesar.remove(paquete);
             * break;
             * }
             * }
             * }
             */

            // Recalcular el tamanho de paquetes
        tamanhoPaquetes = paquetesProcesar.size();

        System.out.println("Se van a procesar " + tamanhoPaquetes + " paquetes, hasta " + now);

            // Realizar planificacion
        RespuestaAlgoritmo respuestaAlgoritmo = procesarPaquetes(grafoVuelos, ocupacionVuelos, paquetesProcesar,
                aeropuertos, planVuelos,
                tamanhoPaquetes, 0, vueloService, planRutaService, null, messagingTemplate);
        return respuestaAlgoritmo;
    }

    public ArrayList<PlanRutaNT> loopPrincipalDiaADia(ArrayList<Aeropuerto> aeropuertos,
            ArrayList<PlanVuelo> planVuelos,
            VueloService vueloService, PlanRutaService planRutaService,
            PaqueteService paqueteService, PlanRutaXVueloService planRutaXVueloService,
            SimulacionService simulacionService, int SA, int TA) {
        // SA y TA en segundos

        messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado", "Iniciando loop principal");

        ArrayList<PlanRutaNT> planRutas = new ArrayList<>();

        if (planVuelos.size() == 0) {
            System.out.println("ERROR: No hay planes de vuelo para procesar.");
            messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado", "Detenido, sin planes vuelo");
            return null;
        }
        int i = 0;
        HashMap<Integer, Integer> ocupacionVuelos = new HashMap<Integer, Integer>();

        while (true) {
            long start = System.currentTimeMillis();
            ArrayList<Paquete> paquetes = paqueteService.findPaquetesSinSimulacionYNoEntregados();
            if (paquetes.size() == 0) {
                System.out.println("No hay paquetes para procesar.");
                messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado", "Detenido, sin paquetes");
                long end = System.currentTimeMillis();
                long sa_millis = SA * 1000 - (end - start);
                if(sa_millis < 0) continue;
                try {
                    Thread.sleep(sa_millis);
                } catch (Exception e) {
                    System.out.println("Error en sleep");
                }
                continue;
            }

            
            GrafoVuelos grafoVuelos = new GrafoVuelos(planVuelos, paquetes);
            if (grafoVuelos.getVuelosHash() == null || grafoVuelos.getVuelosHash().size() <= 0) {
                System.out.println("ERROR: No se generaron vuelos.");
                messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado", "Detenido, error en generar vuelos");
                return null;
            }

            // Calculo del limie de planificacion
            System.out.println("Planificacion iniciada");
            messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado", "Planificacion iniciada");

            // Filtrar paquetes a calcular
            Collections.sort(paquetes, Comparator.comparing(Paquete::getFechaRecepcion));

            List<Paquete> paquetesTemp = paquetes.stream()
                    .filter(p -> p.getFechaDeEntrega() == null)
                    .collect(Collectors.toList());
            ArrayList<Paquete> paquetesProcesar = new ArrayList<>(paquetesTemp);

            int tamanhoPaquetes = paquetesProcesar.size();

            if (tamanhoPaquetes == 0) {
                messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado", "No hay paquetes que planificar");
                System.out.println("No hay paquetes que planificar");
                break;
            }
            // Filtrar paquetes que estan volando

            Date now = new Date();
            /*
             * for (Paquete paquete : paquetesProcesar) {
             * ArrayList<Vuelo> vuelos =
             * vueloService.findVuelosByPaqueteId(paquete.getId());
             * for (Vuelo vuelo : vuelos) {
             * if (vuelo.getFechaLlegada().after(now)
             * && vuelo.getFechaSalida().before(now)) {
             * paquetesProcesar.remove(paquete);
             * break;
             * }
             * }
             * }
             */

            // Recalcular el tamanho de paquetes
            tamanhoPaquetes = paquetesProcesar.size();

            System.out.println("Se van a procesar " + tamanhoPaquetes + " paquetes, hasta " + now);

            // Realizar planificacion
            RespuestaAlgoritmo respuestaAlgoritmo = procesarPaquetes(grafoVuelos, ocupacionVuelos, paquetesProcesar,
                    aeropuertos, planVuelos,
                    tamanhoPaquetes, i, vueloService, planRutaService, null, messagingTemplate);
            i++;
            for (int idx = 0; idx < respuestaAlgoritmo.getPlanesRutas().size(); idx++) {
                PlanRutaNT planRutaNT = respuestaAlgoritmo.getPlanesRutas().get(idx);

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
                paquetesProcesar.get(idx).setFechaDeEntrega(planRutaNT.getFin());
                paquetesProcesar.get(idx).setPlanRutaActual(planRuta);

                try {
                    paqueteService.update(paquetesProcesar.get(idx));
                } catch (Exception e) {
                    // Manejo de errores si algo sale mal durante la operación de guardado
                    System.err.println("Error al guardar en la base de datos: " + e.getMessage());
                    messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado",
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
                        messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado",
                                "Error al guardar algun plan ruta x vuelo: " + e.getMessage());
                    }
                }

            }

            // Formar respuesta a front
            respuestaAlgoritmo.setSimulacion(null);
            messagingTemplate.convertAndSend("/algoritmo/diaDiaRespuesta", respuestaAlgoritmo);
            this.ultimaRespuestaOperacionDiaDia = respuestaAlgoritmo;
            System.out.println("Planificacion terminada hasta " + now);
            messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado",
                    "Planificacion terminada hasta " + now);

            planRutas.addAll(respuestaAlgoritmo.getPlanesRutas());

            long end = System.currentTimeMillis();
            long sa_millis = SA * 1000 - (end - start);
            if(sa_millis < 0) continue;
            try {
                Thread.sleep(sa_millis);
            } catch (Exception e) {
                System.out.println("Error en sleep");
            }

        }
        return planRutas;

    }

    


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
            Date fechaLimiteCalculo = agregarSAyTA(tiempoEnSimulacion, TA, SA, simulacion.getMultiplicadorTiempo());
            fechaSgteCalculo = agregarSAyTA(tiempoEnSimulacion, 0, SA, simulacion.getMultiplicadorTiempo());
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
                messagingTemplate.convertAndSend("/algoritmo/estado",
                        "No hay paquetes para la planificacion actual, esperando");
                System.out.println("No hay paquetes para la planificacion actual, esperando");
                RespuestaAlgoritmo respuestaAlgoritmo = new RespuestaAlgoritmo();
                respuestaAlgoritmo.setSimulacion(simulacion);
                respuestaAlgoritmo.getVuelos().removeIf(vuelo -> vuelo.getCapacidadUtilizada() == 0);
                messagingTemplate.convertAndSend("/algoritmo/respuesta", respuestaAlgoritmo);
                tiempoEnSimulacion = calcularTiempoSimulacion(simulacion);
                continue;
            }

            List<Paquete> paquetesRest = paquetes.stream()
                    .filter(p -> p.getFechaDeEntrega() == null || p.getFechaRecepcion().before(finalTiempoEnSimulacion))
                    .collect(Collectors.toList());

            if (paquetesRest.size() == 0) {
                messagingTemplate.convertAndSend("/algoritmo/estado", "No hay mas paquetes, terminando");
                System.out.println("No hay mas paquetes, terminando");
                simulacion.setEstado(1);
                simulacionService.update(simulacion);
                break;
            }

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
            tamanhoPaquetes = paquetesProcesar.size();

            System.out.println("Se van a procesar " + tamanhoPaquetes + " paquetes, hasta " + fechaLimiteCalculo);

            // Realizar planificacion
            RespuestaAlgoritmo respuestaAlgoritmo = procesarPaquetes(grafoVuelos, ocupacionVuelos, paquetesProcesar,
                    aeropuertos, planVuelos,
                    tamanhoPaquetes, i, vueloService, planRutaService, simulacion, messagingTemplate);
            i++;
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
                paquetesProcesar.get(idx).setFechaDeEntrega(planRutaNT.getFin());
                paquetesProcesar.get(idx).setSimulacionActual(simulacion);
                paquetesProcesar.get(idx).setPlanRutaActual(planRuta);

                try {
                    paqueteService.update(paquetesProcesar.get(idx));
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
        int neighbourCount = 5;
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

    public RespuestaAlgoritmo getUltimaRespuestaOperacionDiaDia() {
        return ultimaRespuestaOperacionDiaDia;
    }

    
}
