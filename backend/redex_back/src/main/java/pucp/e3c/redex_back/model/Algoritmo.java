package pucp.e3c.redex_back.model;

import java.util.ArrayList;
import java.util.Calendar;
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
import java.lang.Thread;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import jakarta.persistence.PersistenceException;
import pucp.e3c.redex_back.service.AeropuertoService;
import pucp.e3c.redex_back.service.PaqueteService;
import pucp.e3c.redex_back.service.PlanRutaService;
import pucp.e3c.redex_back.service.PlanRutaXVueloService;
import pucp.e3c.redex_back.service.SimulacionService;
import pucp.e3c.redex_back.service.VueloService;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

@Component
@EnableAsync
public class Algoritmo {

    private final SimpMessagingTemplate messagingTemplate;

    RespuestaAlgoritmo ultimaRespuestaOperacionDiaDia;

    public Algoritmo(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.ultimaRespuestaOperacionDiaDia = new RespuestaAlgoritmo();
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

    public void loopPrincipalDiaADia(ArrayList<Aeropuerto> aeropuertos,
            ArrayList<PlanVuelo> planVuelos,
            VueloService vueloService, PlanRutaService planRutaService,
            PaqueteService paqueteService, PlanRutaXVueloService planRutaXVueloService,
            AeropuertoService aeropuertoService, int SA, int TA) {
        this.ultimaRespuestaOperacionDiaDia.setIniciandoPrimeraPlanificacionDiaDia(true);
        // SA y TA en segundos\

        messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado", "Iniciando loop principal");


        if (planVuelos.size() == 0) {
            System.out.println("ERROR: No hay planes de vuelo para procesar.");
            messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado", "Detenido, sin planes vuelo");
            return;
        }
        int i = 0;
        HashMap<Integer, Integer> ocupacionVuelos = new HashMap<Integer, Integer>();

        boolean primera_iteracion_con_paquetes = true;
        ArrayList<Paquete> paquetesDiaDia = null;
        GrafoVuelos grafoVuelos = null;
        //ArrayList<PlanRutaNT> planRutaNTs = new ArrayList<>();
        //ArrayList<PlanRutaNT> planRutaNTs = null;
        HashMap<Integer, PlanRutaNT> hashPlanRutasNT = new HashMap<>();

        while (true) {
            long start = System.currentTimeMillis();
            Date now = new Date();
            //TO DO ACTUALIZAR PAQUETES

            //paquetesDiaDia = paqueteService.findPaquetesSinSimulacionYNoEntregados();
            paquetesDiaDia = paqueteService.findPaquetesOperacionesDiaDia();
            if(paquetesDiaDia != null){
                paquetesDiaDia = actualizarPaquetesDiaDia(paquetesDiaDia, hashPlanRutasNT,now,aeropuertoService,paqueteService);
            }
            List<Paquete> paquetesPrimerFiltro = paquetesDiaDia.stream()
            .filter(p -> p.isEntregado() == false)
            .filter(p -> p.getFechaRecepcion().before(now))
            .collect(Collectors.toList());
            ArrayList<Paquete> paquetes = new ArrayList<>(paquetesPrimerFiltro);
            if (paquetes.size() == 0) {
                System.out.println("No hay paquetes para procesar.");
                messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado", "Detenido, sin paquetes");
                this.ultimaRespuestaOperacionDiaDia.setIniciandoPrimeraPlanificacionDiaDia(false);
                long end = System.currentTimeMillis();
                long sa_millis = SA * 1000 - (end - start);
                if (sa_millis < 0)
                    continue;
                try {
                    Thread.sleep(sa_millis);
                } catch (Exception e) {
                    System.out.println("Error en sleep");
                }
                continue;
            }

            if (primera_iteracion_con_paquetes) {
                grafoVuelos = new GrafoVuelos(planVuelos, paquetes, vueloService);
                if (grafoVuelos.getVuelosHash() == null || grafoVuelos.getVuelosHash().size() <= 0) {
                    System.out.println("ERROR: No se generaron vuelos.");
                    messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado", "Detenido, error en generar vuelos");
                    return;
                }
                primera_iteracion_con_paquetes = false;
            } else {
                grafoVuelos.agregarVuelosParaPaquetes(planVuelos,paquetes,vueloService);
            }

            //Agrega planRutasNT para los paquetes que no tienen
            for(Paquete paquete : paquetes){
                if(hashPlanRutasNT.get(paquete.getId()) == null){
                    PlanRutaNT planRutaNT = new PlanRutaNT();
                    hashPlanRutasNT.put(paquete.getId(), planRutaNT);
                }
            }

            System.out.println("Planificacion iniciada");
            messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado", "Planificacion iniciada");

            // Ordeno por fecha de recepcion
            Collections.sort(paquetes, Comparator.comparing(Paquete::getFechaRecepcion));
            
            // Filtrar paquetes que estan volando
            System.out.println("Filtrando vuelos");
            ArrayList<Paquete> paquetesProcesar = filtrarPaquetesVolando(paquetes, vueloService, now);
            System.out.println("Fin de filtrado de vuelos");
            int tamanhoPaquetes = paquetesProcesar.size();

            if (tamanhoPaquetes == 0) {
                messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado", "No hay paquetes que planificar");
                System.out.println("No hay paquetes que planificar");
                continue;
            }
            // Recalcular el tamanho de paquetes
            System.out.println("Se van a procesar " + tamanhoPaquetes + " paquetes, hasta " + now);

            //Crea el array rutas para los paquetes a procesar
            ArrayList<PlanRutaNT> planRutasPaquetesProcesar = new ArrayList<>();
            ArrayList<Integer> idsPaquetesProcesar = new ArrayList<>();
            for(Paquete paquete: paquetesProcesar){
                PlanRutaNT planRutaNT = hashPlanRutasNT.get(paquete.getId());
                planRutasPaquetesProcesar.add(planRutaNT);
                idsPaquetesProcesar.add(paquete.getId());
            }

            // Realizar planificacion
            RespuestaAlgoritmo respuestaAlgoritmo = procesarPaquetes(grafoVuelos, ocupacionVuelos, paquetesProcesar,
            planRutasPaquetesProcesar,
                    aeropuertos, planVuelos,
                    tamanhoPaquetes, i, vueloService, planRutaService, null, messagingTemplate);        
            //respuestaAlgoritmo.filtrarVuelosSinPaquetes();
            ocupacionVuelos = respuestaAlgoritmo.getOcupacionVuelos();


            //Actualiza el hasmap rutas
            ArrayList<PlanRutaNT> planRutasRespuestaAlgoritmo = respuestaAlgoritmo.getPlanesRutas();

            int contador_index_respuesta = 0;
            for(Integer idPaquete : idsPaquetesProcesar){
                hashPlanRutasNT.put(idPaquete, planRutasRespuestaAlgoritmo.get(contador_index_respuesta));
                contador_index_respuesta++;
            }

            i++;
            System.out.println("");
            System.out.println("Planificacion finalizada");
            System.out.println("");

            realizarGuardadoDiaDia(paquetesProcesar, planRutasRespuestaAlgoritmo, paqueteService, planRutaService, vueloService, planRutaXVueloService);


            // Formar respuesta a front
            respuestaAlgoritmo.setSimulacion(null);
            respuestaAlgoritmo.setOcupacionVuelos(null);
            messagingTemplate.convertAndSend("/algoritmo/diaDiaRespuesta", respuestaAlgoritmo);
            this.ultimaRespuestaOperacionDiaDia = respuestaAlgoritmo;
            this.ultimaRespuestaOperacionDiaDia.setIniciandoPrimeraPlanificacionDiaDia(false);
            System.out.println("Planificacion terminada hasta " + now);
            messagingTemplate.convertAndSend("/algoritmo/diaDiaEstado",
                    "Planificacion terminada hasta " + now);

            long end = System.currentTimeMillis();
            long sa_millis = SA * 1000 - (end - start);
            if (sa_millis < 0)
                continue;
            try {
                Thread.sleep(sa_millis);
            } catch (Exception e) {
                System.out.println("Error en sleep");
            }

        }
    }

    public void realizarGuardadoDiaDia(ArrayList<Paquete> paquetes, ArrayList<PlanRutaNT> planRutaNTs,  PaqueteService paqueteService,
    PlanRutaService planRutaService, VueloService vueloService, PlanRutaXVueloService planRutaXVueloService){
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

            try {
                paqueteService.update(paquetes.get(idx));
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
    }

    public ArrayList<PlanRutaNT> loopPrincipal(ArrayList<Aeropuerto> aeropuertos, ArrayList<PlanVuelo> planVuelos,
            ArrayList<Paquete> paquetes, VueloService vueloService, PlanRutaService planRutaService,
            PaqueteService paqueteService, AeropuertoService aeropuertoService,
            PlanRutaXVueloService planRutaXVueloService,
            SimulacionService simulacionService,
            Simulacion simulacion, int SA, int TA) {
        messagingTemplate.convertAndSend("/algoritmo/estado", "Iniciando loop principal");

        Date fechaMinima = simulacion.getFechaInicioSim();
        paquetes.removeIf(paquete -> paquete.getEnvio().getFechaRecepcion().before(fechaMinima));

        ArrayList<PlanRutaNT> planRutas = new ArrayList<>();

        for (int i = 0; i < paquetes.size(); i++) {
            PlanRutaNT planRuta = new PlanRutaNT();
            planRutas.add(planRuta);
        }

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
        GrafoVuelos grafoVuelos = new GrafoVuelos(planVuelos, paquetes, vueloService);
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

            paquetes = actualizarPaquetes(paquetes, planRutas, tiempoEnSimulacion, aeropuertoService);
            // Gestion de parado forzado
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
                if (primera_iter) {
                    simulacion.setFechaInicioSistema(new Date());
                    simulacion = simulacionService.update(simulacion);
                    primera_iter = false;

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
                if (primera_iter) {
                    simulacion.setFechaInicioSistema(new Date());
                    simulacion = simulacionService.update(simulacion);
                    primera_iter = false;

                }
                continue;
            }

            // Calculo del limie de planificacion
            Date fechaLimiteCalculo = agregarSAyTA(tiempoEnSimulacion, TA, SA, simulacion.getMultiplicadorTiempo());
            fechaSgteCalculo = agregarSAyTA(tiempoEnSimulacion, 0, SA, simulacion.getMultiplicadorTiempo());

            System.out.println("Planificacion iniciada");
            messagingTemplate.convertAndSend("/algoritmo/estado", "Planificacion iniciada");

            // Filtrar paquetes a calcular

            ArrayList<Paquete> paquetesProcesar = filtrarPaquetesValidos(paquetes, tiempoEnSimulacion,
                    fechaLimiteCalculo);
            int tamanhoPaquetes = paquetesProcesar.size();
            final Date finalTiempoEnSimulacion = tiempoEnSimulacion;
            List<Paquete> paquetesRest = paquetes.stream()
                    .filter(p -> p.getFechaDeEntrega() == null || p.getFechaRecepcion().before(finalTiempoEnSimulacion))
                    .collect(Collectors.toList());

            if (tamanhoPaquetes == 0) {
                if (primera_iter) {
                    simulacion.setFechaInicioSistema(new Date());
                    simulacion = simulacionService.update(simulacion);
                    primera_iter = false;

                }
                messagingTemplate.convertAndSend("/algoritmo/estado",
                        "No hay paquetes para la planificacion actual en " + tiempoEnSimulacion + ", esperando");
                System.out.println(
                        "No hay paquetes para la planificacion actual en " + tiempoEnSimulacion + ", esperando");
                RespuestaAlgoritmo respuestaAlgoritmo = new RespuestaAlgoritmo();
                respuestaAlgoritmo.setSimulacion(simulacion);
                respuestaAlgoritmo.getVuelos().removeIf(vuelo -> vuelo.getCapacidadUtilizada() == 0);
                respuestaAlgoritmo.setPaquetes(new ArrayList<>(paquetesRest));

                messagingTemplate.convertAndSend("/algoritmo/respuesta", respuestaAlgoritmo);

                tiempoEnSimulacion = calcularTiempoSimulacion(simulacion);

                continue;
            }

            if (paquetesRest.size() == 0) {
                messagingTemplate.convertAndSend("/algoritmo/estado", "No hay mas paquetes, terminando");
                System.out.println("No hay mas paquetes, terminando");
                simulacion.setEstado(1);
                simulacionService.update(simulacion);
                break;
            }

            // Filtrar paquetes que estan volando
            paquetesProcesar = filtrarPaquetesVolando(paquetesProcesar, vueloService, tiempoEnSimulacion);

            // Recalcular el tamanho de paquetes
            tamanhoPaquetes = paquetesProcesar.size();

            System.out.println("Se van a procesar " + tamanhoPaquetes + " paquetes, hasta " + fechaLimiteCalculo);

            ArrayList<PlanRutaNT> planesRutaActuales = new ArrayList<>();

            for (int j = 0; j < paquetesProcesar.size(); j++) {
                planesRutaActuales.add(planRutas.get(j));
            }
            // Realizar planificacion
            RespuestaAlgoritmo respuestaAlgoritmo = procesarPaquetes(grafoVuelos, ocupacionVuelos, paquetesProcesar,
                    planesRutaActuales,
                    aeropuertos, planVuelos,
                    tamanhoPaquetes, i, vueloService, planRutaService, simulacion, messagingTemplate);
            i++;
            ocupacionVuelos = respuestaAlgoritmo.getOcupacionVuelos();
            // Imprimir ocupacionVuelos ordenado por sus claves

            // printRutasAlgoritmo(respuestaAlgoritmo.getPlanesRutas(), paquetesProcesar,
            // i);
            // Guardar resultados
            realizarGuardado(paquetes, planRutas, paquetesProcesar, respuestaAlgoritmo, simulacion, paqueteService,
                    planRutaService,
                    vueloService, planRutaXVueloService, "/algoritmo/estado");

            respuestaAlgoritmo.setPaquetes(new ArrayList<>(paquetesRest));
            // HashMap<Integer, Integer> hash = printPaquetes(paquetes, planRutas, i,
            // vueloService);
            // printOcupacion(hash, ocupacionVuelos, i);
            // Solo en la primera iter, definir el inicio de la simulacion
            if (primera_iter) {
                simulacion.setFechaInicioSistema(new Date());
                simulacion = simulacionService.update(simulacion);
                primera_iter = false;

            }

            // Formar respuesta a front
            enviarRespuesta(respuestaAlgoritmo, simulacion, fechaLimiteCalculo, fechaSgteCalculo,
                    "/algoritmo/respuesta");

            System.out.println("Proxima planificacion en tiempo de simulacion " + fechaSgteCalculo);

            tiempoEnSimulacion = calcularTiempoSimulacion(simulacion);

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
        Collections.sort(paquetesOrdenados, Comparator.comparing(Paquete::getFechaRecepcion));

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
            Date fechaLimiteCalculo) {
        Collections.sort(paquetes, Comparator.comparing(Paquete::getFechaRecepcion));

        final Date finalTiempoEnSimulacion = tiempoEnSimulacion;
        List<Paquete> paquetesTemp = paquetes.stream()
                .filter(p -> p.getFechaDeEntrega() == null || finalTiempoEnSimulacion.before(p.getFechaDeEntrega()))
                .filter(p -> p.getFechaRecepcion().before(fechaLimiteCalculo))
                .collect(Collectors.toList());
        ArrayList<Paquete> paquetesProcesar = new ArrayList<>(paquetesTemp);
        return paquetesProcesar;
    }

    private ArrayList<Paquete> filtrarPaquetesVolando(ArrayList<Paquete> paquetesProcesar, VueloService vueloService,
            Date tiempoEnSimulacion) {
        ArrayList<Integer> indicesAEliminar = new ArrayList<>();
        for (int i = 0; i < paquetesProcesar.size(); i++) {
            // for (Paquete paquete : paquetesProcesar) {

            if (paquetesProcesar.get(i).planRutaActual == null) {
                continue;
            }
            ArrayList<Vuelo> vuelos = vueloService.findVuelosByPaqueteId(paquetesProcesar.get(i).getId());
            if (vuelos == null) {
                System.out.println("El paquete tiene planRuta pero no vuelo");
            }
            for (Vuelo vuelo : vuelos) {
                if (vuelo.getFechaLlegada().after(tiempoEnSimulacion)
                        && vuelo.getFechaSalida().before(tiempoEnSimulacion)) {
                    // System.out.println("Eliminando paquete " + paquetesProcesar.get(i));
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

    private void enviarRespuesta(RespuestaAlgoritmo respuestaAlgoritmo, Simulacion simulacion, Date fechaLimiteCalculo,
            Date fechaSgteCalculo, String canal) {
        respuestaAlgoritmo.setSimulacion(simulacion);

        respuestaAlgoritmo.getVuelos().removeIf(vuelo -> vuelo.getCapacidadUtilizada() == 0);
        messagingTemplate.convertAndSend(canal, respuestaAlgoritmo);
        System.out.println("Planificacion terminada en tiempo de simulacion hasta " + fechaLimiteCalculo);
        messagingTemplate.convertAndSend("/algoritmo/estado",
                "Planificacion terminada hasta " + fechaLimiteCalculo);

        System.out.println("Proxima planificacion en tiempo de simulacion " + fechaSgteCalculo);
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
            PlanRuta planRuta = new PlanRuta();
            planRuta.setCodigo(planRutaNT.getCodigo());
            planRuta.setSimulacionActual(simulacion);
            try {
                planRuta = planRutaService.register(planRuta);
            } catch (PersistenceException e) {
                // Manejo de errores si algo sale mal durante la operación de guardado
                System.err.println("Error al guardar en la base de datos: " + e.getMessage());
                messagingTemplate.convertAndSend(canal,
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
                messagingTemplate.convertAndSend(canal,
                        "Error al guardar algun paquete: " + e.getMessage());
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
                    messagingTemplate.convertAndSend(canal,
                            "Error al guardar algun plan ruta x vuelo: " + e.getMessage());
                }
            }

        }
    }

    private ArrayList<Paquete> actualizarPaquetesDiaDia(ArrayList<Paquete> paquetes, HashMap<Integer, PlanRutaNT> hashPlanRutasNT,
            Date fechaCorte, AeropuertoService aeropuertoService, PaqueteService paqueteService) {
        for (int i = 0; i < paquetes.size(); i++) {
            if (hashPlanRutasNT != null) {
                boolean primero = true;
                PlanRutaNT planRutaNT = hashPlanRutasNT.get(paquetes.get(i).getId());
                if(planRutaNT != null){
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
        for (int i = 0; i < paquetes.size(); i++) {
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
                paquetes,
                planRutaNTs,
                ocupacionVuelos);

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
