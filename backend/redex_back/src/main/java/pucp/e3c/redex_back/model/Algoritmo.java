package pucp.e3c.redex_back.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import jakarta.persistence.PersistenceException;
import pucp.e3c.redex_back.service.PaqueteService;
import pucp.e3c.redex_back.service.PlanRutaService;
import pucp.e3c.redex_back.service.PlanRutaXVueloService;
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
        long multiplicador = (long) simulacion.getMultiplicadorTiempo();

        return new Date(inicioSimulacion
                + (tiempoActual - inicioSistema) * multiplicador);
    }

    private Date agregarSAyTA(Date fechaEnSimulacion, int TA, int SA, double multiplicador) {
        // Supongamos que tienes una fecha, por ejemplo:

        // Crea un objeto Calendar y establece la fecha
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaEnSimulacion);

        // Añade 10 minutos a la fecha
        calendar.add(Calendar.MINUTE, (TA + SA) * (int) multiplicador);

        // Obtiene la nueva fecha con los minutos añadidos
        Date fechaActualizada = calendar.getTime();
        return fechaActualizada;
    }

    public ArrayList<PlanRutaNT> loopPrincipal(ArrayList<Aeropuerto> aeropuertos,
            ArrayList<PlanVuelo> planVuelos, ArrayList<Paquete> paquetes, VueloService vueloService,
            PlanRutaService planRutaService, PaqueteService paqueteService, PlanRutaXVueloService planRutaXVueloService,
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

        // recorrer los paquetes por cada 50
        HashMap<Integer, Integer> ocupacionVuelos = new HashMap<Integer, Integer>();
        GrafoVuelos grafoVuelos = new GrafoVuelos(planVuelos, paquetes);
        if (grafoVuelos.getVuelosHash() == null || grafoVuelos.getVuelosHash().size() <= 0) {
            System.out.println("ERROR: No se generaron vuelos.");
            messagingTemplate.convertAndSend("/algoritmo/estado", "Detenido, error en generar vuelos");
            return null;
        }
        int i = 0;
        boolean es_final = false;
        while (true) {
            Date tiempoEnSimulacion = calcularTiempoSimulacion(simulacion);
            Date fechaLimiteCalculo = agregarSAyTA(tiempoEnSimulacion, SA, TA, simulacion.getMultiplicadorTiempo());
            System.out.println("Planificacion iniciada");
            messagingTemplate.convertAndSend("/algoritmo/estado", "Planificacion iniciada");
            /*
             * ArrayList<Paquete> paquetesTemp = new ArrayList<>();
             * for (int j = i; j < i + tamanhoPaquetes; j++) {
             * if (j < paquetes.size()) {
             * paquetesTemp.add(paquetes.get(j));
             * }
             * }
             */
            Collections.sort(paquetes, Comparator.comparing(Paquete::getFechaRecepcion));

            // Filtra los paquetes que tengan una fecha de recepción anterior a fechaCorte
            List<Paquete> paquetesTemp = paquetes.stream()
                    .filter(p -> p.getFechaRecepcion().before(fechaLimiteCalculo))
                    .collect(Collectors.toList());
            ArrayList<Paquete> paquetesProcesar = new ArrayList<>(paquetesTemp);

            int tamanhoPaquetes = paquetesProcesar.size();
            System.out.println("Se van a procesar " + tamanhoPaquetes + " paquetes, hasta " + fechaLimiteCalculo);
            System.out.println("La simulacion inicio " + simulacion.fechaInicioSim);

            if (es_final) {
                messagingTemplate.convertAndSend("/algoritmo/estado", "No hay mas paquetes, terminando");

                break;
            }
            if (tamanhoPaquetes == paquetes.size()) {
                es_final = true;
            }

            RespuestaAlgoritmo respuestaAlgoritmo = procesarPaquetes(grafoVuelos, ocupacionVuelos, paquetesProcesar,
                    aeropuertos, planVuelos,
                    tamanhoPaquetes, i, vueloService, planRutaService, simulacion, messagingTemplate);

            for (int idx = 0; idx < respuestaAlgoritmo.getPlanesRutas().size(); idx++) {
                PlanRutaNT planRutaNT = respuestaAlgoritmo.getPlanesRutas().get(idx);

                planRutaNT.updateCodigo();
                PlanRuta planRuta = new PlanRuta();
                // Crear y guardar PlanRuta
                planRuta.setCodigo(planRutaNT.getCodigo());
                paquetes.get(i).setPlanRutaActual(planRuta);
                try {
                    paqueteService.update(paquetes.get(i));
                } catch (Exception e) {
                    // Manejo de errores si algo sale mal durante la operación de guardado
                    System.err.println("Error al guardar en la base de datos: " + e.getMessage());
                    messagingTemplate.convertAndSend("/algoritmo/estado",
                            "Error al guardar algun paquete: " + e.getMessage());
                }
                planRuta.setSimulacionActual(simulacion);

                try {
                    planRuta = planRutaService.register(planRuta);
                } catch (PersistenceException e) {
                    // Manejo de errores si algo sale mal durante la operación de guardado
                    System.err.println("Error al guardar en la base de datos: " + e.getMessage());
                    messagingTemplate.convertAndSend("/algoritmo/estado",
                            "Error al guardar algun plan ruta: " + e.getMessage());
                }

                // Asociar cada PlanRuta con sus vuelos
                for (Vuelo vuelo : planRutaNT.getVuelos()) {
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
            // System.out.println(respuestaAlgoritmo.toString());
            messagingTemplate.convertAndSend("/algoritmo/respuesta", respuestaAlgoritmo);
            System.out.println("Planificacion terminada hasta " + fechaLimiteCalculo);
            messagingTemplate.convertAndSend("/algoritmo/estado",
                    "Planificacion terminada hasta " + fechaLimiteCalculo);

            planRutas.addAll(respuestaAlgoritmo.getPlanesRutas());
        }
        return planRutas;

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
