package pucp.e3c.redex_back;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import pucp.e3c.redex_back.model.Aeropuerto;
import pucp.e3c.redex_back.model.Algoritmo;
import pucp.e3c.redex_back.model.Envio;
import pucp.e3c.redex_back.model.Funciones;
import pucp.e3c.redex_back.model.Paquete;
import pucp.e3c.redex_back.model.PlanVuelo;
import pucp.e3c.redex_back.model.RegistrarEnvio;
import pucp.e3c.redex_back.model.Simulacion;
import pucp.e3c.redex_back.model.Ubicacion;
import pucp.e3c.redex_back.service.AeropuertoService;
import pucp.e3c.redex_back.service.EnvioService;
import pucp.e3c.redex_back.service.PaqueteService;
import pucp.e3c.redex_back.service.PlanRutaService;
import pucp.e3c.redex_back.service.PlanRutaXVueloService;
import pucp.e3c.redex_back.service.PlanVueloService;
import pucp.e3c.redex_back.service.SimulacionService;
import pucp.e3c.redex_back.service.UbicacionService;
import pucp.e3c.redex_back.service.VueloService;

@Component
public class DataInitializer {
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    private Algoritmo algoritmo;

    @Autowired
    private PlanVueloService planVueloService;

    @Autowired
    private UbicacionService ubicacionService;

    @Autowired
    private AeropuertoService aeropuertoService;

    @Autowired
    private PaqueteService paqueteService;

    @Autowired
    private SimulacionService simulacionService;

    @Autowired
    private VueloService vueloService;

    @Autowired
    private PlanRutaService planRutaService;

    @Autowired
    private PlanRutaXVueloService planRutaXVueloService;

    @Autowired
    private EnvioService envioService;

    public DataInitializer(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @PostConstruct
    public void initData() {
        System.out.println("Inicializando planes de vuelo y aeropuertos");
        String inputPath = "src\\main\\resources\\dataFija";

        ArrayList<Aeropuerto> aeropuertos = new ArrayList<Aeropuerto>();
        ArrayList<PlanVuelo> planVuelos = new ArrayList<PlanVuelo>();
        ArrayList<Paquete> paquetes = new ArrayList<>();
        HashMap<String, Ubicacion> ubicacionMap = Funciones.getUbicacionMap();
        for (Ubicacion ubicacion : ubicacionMap.values()) {
            ubicacionService.register(ubicacion);
        }
        aeropuertos = Funciones.leerAeropuertos(inputPath, ubicacionMap);
        planVuelos = Funciones.leerPlanesVuelo(ubicacionMap, inputPath);

        /*
         * LocalDate today = LocalDate.of(2024, 1, 3);
         * 
         * // Sumar 3 días a la fecha de hoy
         * LocalDate startDate = today.plusDays(1);
         * LocalDate endDate = today.plusDays(3);
         * 
         * // Formatear las fechas como strings
         * DateTimeFormatter formatter =
         * DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
         * 
         * String startPackagesDate = startDate.atStartOfDay().format(formatter);
         * String endPackagesDate = endDate.atTime(20, 59, 59).format(formatter);
         *
         * 
         * paquetes = Funciones.generarPaquetes(
         * 1000,
         * aeropuertos,
         * Funciones.parseDateString(startPackagesDate),
         * Funciones.parseDateString(endPackagesDate));
         *
         * 
         * System.out.println("Se generaron " + paquetes.size() + " paquetes.");
         * 
         * Date minDate = Funciones.parseDateString(startPackagesDate);
         * Date maxDate = Funciones.parseDateString(endPackagesDate);
         * 
         * System.out.println("Fecha minima de recepcion de paquetes: " +
         * Funciones.getFormattedDate(minDate));
         * System.out.println("Fecha maxima de recepcion de paquetes: " +
         * Funciones.getFormattedDate(maxDate));
         */
        LocalDate hoy = LocalDate.of(2024, 1, 3);
        LocalDateTime fecha = hoy.atStartOfDay();
        Date fechaDate = java.sql.Timestamp.valueOf(fecha);
        Simulacion simulacion = new Simulacion();
        simulacion.fillData();
        simulacion.setFechaInicioSim(fechaDate);
        simulacion.setMultiplicadorTiempo(100.0);
        simulacion = simulacionService.register(simulacion);
        System.out.println(simulacion.toString());

        try (BufferedReader reader = new BufferedReader(
                new FileReader("src\\main\\resources\\dataFija\\pack_enviado_SKBO.txt"))) {
            String line;
            int cantidadPaquetes = 0;
            while ((line = reader.readLine()) != null) {
                RegistrarEnvio registrarEnvio = new RegistrarEnvio();
                registrarEnvio.setCodigo(line);
                registrarEnvio.setSimulacion(simulacion);
                Envio envio = envioService.registerByString(registrarEnvio);
                cantidadPaquetes += envio.getCantidadPaquetes();
            }
            System.out.println("Se generaron " + cantidadPaquetes + " paquetes.");
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }

        for (Aeropuerto aeropuerto : aeropuertos) {
            aeropuertoService.register(aeropuerto);
        }

        for (PlanVuelo planVuelo : planVuelos) {
            planVueloService.register(planVuelo);
        }
        /*
         * for (Paquete paquete : paquetes) {
         * envioService.register(paquete.getEnvio());
         * paquete.setSimulacionActual(simulacion);
         * paqueteService.register(paquete);
         * }
         */

        // INICIALIZA LOOP PRINCIPAL DIA A DIA
        ArrayList<Aeropuerto> aeropuertosLoop = (ArrayList<Aeropuerto>) aeropuertoService.getAll();
        ArrayList<PlanVuelo> planVuelosLoop = (ArrayList<PlanVuelo>) planVueloService.getAll();
        // Algoritmo algoritmo = new Algoritmo(messagingTemplate);
        CompletableFuture.runAsync(() -> {
            algoritmo.loopPrincipalDiaADia(aeropuertosLoop, planVuelosLoop,
                    vueloService, planRutaService, paqueteService, planRutaXVueloService, simulacionService,
                    120, 60);
        });

    }
}
