package pucp.e3c.redex_back;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
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
    private Funciones funciones;

    @Autowired
    private EnvioService envioService;

    @Autowired
    ResourceLoader resourceLoader;

    private static final Logger LOGGER = LoggerFactory.getLogger(DataInitializer.class);

    public DataInitializer(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    private void inicializaPaquetesDiaDia(ArrayList<Aeropuerto> aeropuertos, HashMap<String, Ubicacion> ubicacionMap,
            ArrayList<PlanVuelo> planVuelos) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));

        ZonedDateTime startDate = now.minusHours(3);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String startPackagesDate = startDate.format(formatter);
        String endPackagesDate = now.format(formatter);

        ArrayList<Paquete> paquetes = Funciones.generarPaquetes(
                500,
                aeropuertos,
                Funciones.parseDateString(startPackagesDate),
                Funciones.parseDateString(endPackagesDate));

        System.out.println("Operaciones Dia a Dia: Se generaron " + paquetes.size() + " paquetes.");

        Date minDate = Funciones.parseDateString(startPackagesDate);
        Date maxDate = Funciones.parseDateString(endPackagesDate);

        System.out.println(
                "Operaciones Dia a Dia: Fecha minima de recepcion de paquetes: " + Funciones.getFormattedDate(minDate));
        System.out.println(
                "Operaciones Dia a Dia: Fecha maxima de recepcion de paquetes: " + Funciones.getFormattedDate(maxDate));
        ;

        for (Paquete paquete : paquetes) {
            envioService.register(paquete.getEnvio());
            paqueteService.register(paquete);
        }
    }

    private void incializacionFijaPaquetesDiaDia(ArrayList<Aeropuerto> aeropuertos,
            HashMap<String, Ubicacion> ubicacionMap,
            ArrayList<PlanVuelo> planVuelos, int nEnvios) throws IOException {

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));

        ZonedDateTime startDate = now.minusHours(1);

        Resource resource = resourceLoader.getResource("classpath:static/envios_semanal_V4_P1.txt");
        InputStream input1 = resource.getInputStream();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input1))) {
            List<String> lines = reader.lines().collect(Collectors.toList());

            ArrayList<RegistrarEnvio> registrarEnvios = new ArrayList<>();
            for (String line : lines) {
                RegistrarEnvio registrarEnvio = new RegistrarEnvio();
                registrarEnvio.setCodigo(line);
                registrarEnvio.setSimulacion(null);
                registrarEnvios.add(registrarEnvio);
                // System.out.println("LINE: " + line);
            }

            HashMap<String, Aeropuerto> aeropuertoMap = new HashMap<>();
            for (Aeropuerto aeropuerto : aeropuertos) {
                aeropuertoMap.put(aeropuerto.getUbicacion().getId(), aeropuerto);
            }

            // LOGGER.info("Fecha inicio " + Date.from(startDate.toInstant()));
            // LOGGER.info("Fecha fin " + Date.from(now.toInstant()));
            ArrayList<Envio> envios = envioService.registerAllByStringEspInicioFijo(registrarEnvios, aeropuertoMap,
                    Date.from(startDate.toInstant()), Date.from(now.toInstant()), nEnvios);

            // Filtrar envios
            LOGGER.info("Cantidad de envios ANTES: " + envios.size());
            Date fechaMinima = Date.from(startDate.toInstant());
            envios.removeIf(envio -> envio.getFechaRecepcion().before(fechaMinima));
            LOGGER.info("Cantidad de envios DESPUES: " + envios.size());
            int totalPaquetes = envios.stream()
                    .mapToInt(envio -> envio.getCantidadPaquetes())
                    .sum();
            LOGGER.info("DATA INIT || Se generaron " + totalPaquetes + " paquetes.");
            // System.out.println("Se generaron " + totalPaquetes + " paquetes.");
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            LOGGER.error("Error al leer el archivo: " + e.getMessage());
        }
    }

    private void eliminarRegistrosBaseDeDatos() {
        // 1. Planruta x vuelo
        planRutaXVueloService.deleteAll();
        // 2. vuelo
        vueloService.deleteAll();
        // 3. paquete
        paqueteService.deleteAll();
        // 4. envio
        envioService.deleteAll();
        // 5. plan ruta
        planRutaService.deleteAll();
        // 6. plan vuelo
        planVueloService.deleteAll();
        // 7. aeropuerto
        aeropuertoService.deleteAll();
        // 8. ubicacion
        ubicacionService.deleteAll();
        // 9. simulacion
        simulacionService.deleteAll();
    }

    @PostConstruct
    public void initData() throws IOException {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        eliminarRegistrosBaseDeDatos();
        System.out.println("Inicializando planes de vuelo y aeropuertos");
        String inputPath = "src\\main\\resources\\dataFija";

        ArrayList<Aeropuerto> aeropuertos = new ArrayList<Aeropuerto>();
        ArrayList<PlanVuelo> planVuelos = new ArrayList<PlanVuelo>();
        aeropuertos = funciones.leerAeropuertosCompletos();

        HashMap<String, Ubicacion> ubicacionMap = new HashMap<String, Ubicacion>();
        for (Aeropuerto aeropuerto : aeropuertos) {
            Ubicacion _ubicacion = ubicacionService.register(aeropuerto.getUbicacion());
            ubicacionMap.put(_ubicacion.getId(), _ubicacion);
        }
        planVuelos = funciones.leerPlanesVuelo(ubicacionMap, inputPath);

        for (Aeropuerto aeropuerto : aeropuertos) {
            aeropuerto = aeropuertoService.register(aeropuerto);
        }

        for (PlanVuelo planVuelo : planVuelos) {
            planVuelo = planVueloService.register(planVuelo);
        }

        boolean inicializar_paquetes_operaciones_dia_dia = false;
        if (inicializar_paquetes_operaciones_dia_dia) {
            // inicializaPaquetesDiaDia(aeropuertos, ubicacionMap, planVuelos);
            incializacionFijaPaquetesDiaDia(aeropuertos, ubicacionMap, planVuelos, 60); // 100,250,500
        }

        boolean inicializar_paquetes_operaciones_simulacion = false;
        if (inicializar_paquetes_operaciones_simulacion) {
            funciones.inicializaPaquetesSimulacion(aeropuertos, simulacionService, envioService);
        }

        // INICIALIZA LOOP PRINCIPAL DIA A DIA
        ArrayList<Aeropuerto> aeropuertosLoop = (ArrayList<Aeropuerto>) aeropuertoService.getAll();
        ArrayList<PlanVuelo> planVuelosLoop = (ArrayList<PlanVuelo>) planVueloService.getAll();

        // Ejecuta de forma asíncrona el método loopPrincipalDiaADia del objeto
        // algoritmo con los parámetros dados.
        System.out.println("Inicializando loop principal dia a dia");
        CompletableFuture.runAsync(() -> {
            algoritmo.loopPrincipalDiaADia(aeropuertosLoop, planVuelosLoop,
                    vueloService, planRutaService, paqueteService, planRutaXVueloService, aeropuertoService,
                    //60, 40);
                    240, 180);
        });

    }

}
