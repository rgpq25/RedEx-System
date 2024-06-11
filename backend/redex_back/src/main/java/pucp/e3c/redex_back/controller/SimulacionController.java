package pucp.e3c.redex_back.controller;

import java.util.Date;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pucp.e3c.redex_back.model.Aeropuerto;
import pucp.e3c.redex_back.model.Algoritmo;
import pucp.e3c.redex_back.model.Envio;
import pucp.e3c.redex_back.model.Paquete;
import pucp.e3c.redex_back.model.PlanRutaNT;
import pucp.e3c.redex_back.model.PlanVuelo;
import pucp.e3c.redex_back.model.RegistrarEnvio;
import pucp.e3c.redex_back.model.RespuestaAlgoritmo;
import pucp.e3c.redex_back.model.Simulacion;
import pucp.e3c.redex_back.service.AeropuertoService;
import pucp.e3c.redex_back.service.EnvioService;
import pucp.e3c.redex_back.service.PaqueteService;
import pucp.e3c.redex_back.service.PlanRutaService;
import pucp.e3c.redex_back.service.PlanRutaXVueloService;
import pucp.e3c.redex_back.service.PlanVueloService;
import pucp.e3c.redex_back.service.SimulacionService;
import pucp.e3c.redex_back.service.VueloService;

@RestController
@CrossOrigin(origins = "https://inf226-981-3c.inf.pucp.edu.pe")
@RequestMapping("back/simulacion")
public class SimulacionController {
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    private Algoritmo algoritmo;

    @Autowired
    SimulacionService simulacionService;

    @Autowired
    private PlanVueloService planVueloService;

    @Autowired
    private AeropuertoService aeropuertoService;

    @Autowired
    private PaqueteService paqueteService;

    @Autowired
    private VueloService vueloService;

    @Autowired
    private PlanRutaService planRutaService;

    @Autowired
    private PlanRutaXVueloService planRutaXVueloService;

    @Autowired
    private EnvioService envioService;

    @Autowired
    ResourceLoader resourceLoader;

    private static final Logger LOGGER = LoggerFactory.getLogger(SimulacionController.class);

    public SimulacionController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/")
    public ResponseEntity<Simulacion> register(@RequestBody Simulacion simulacion) {
        Simulacion registeredSimulacion = simulacionService.register(simulacion);
        System.out.println(registeredSimulacion.toString());
        return new ResponseEntity<>(registeredSimulacion, HttpStatus.CREATED);
    }

    @PostMapping("/inicializarSimulacion")
    public ResponseEntity<Simulacion> inicializarSimulacion(@RequestBody Simulacion simulacion) {
        // Registrar una nueva simulacion
        simulacion.setId(0);
        simulacion.setMilisegundosPausados(0);
        simulacion = simulacionService.register(simulacion);

        ArrayList<Envio> envios = envioService.findBySimulacionActualID(1);
        Date fechaMinima = simulacion.getFechaInicioSim();
        envios.removeIf(envio -> envio.getFechaRecepcion().before(fechaMinima));

        for (Envio envio : envios) {
            Envio nuevoEnvio = new Envio();
            nuevoEnvio.fillData(envio.getUbicacionOrigen(), envio.getUbicacionDestino(), envio.getFechaRecepcion(),
                    envio.getFechaLimiteEntrega());
            nuevoEnvio.setSimulacionActual(simulacion);
            nuevoEnvio = envioService.register(nuevoEnvio);
            ArrayList<Paquete> paquetes = new ArrayList<>(paqueteService.findByEnvioId(envio.getId()));
            for (Paquete paquete : paquetes) {
                Paquete nuevoPaquete = new Paquete();
                nuevoPaquete.fillData(paquete.getAeropuertoActual(), paquete.getEnvio().getUbicacionOrigen(),
                        paquete.getEnvio().getUbicacionDestino(), paquete.getEnvio().getFechaRecepcion());
                nuevoPaquete.setEnvio(nuevoEnvio);
                nuevoPaquete.setSimulacionActual(simulacion);
                nuevoPaquete = paqueteService.register(nuevoPaquete);
            }
        }

        return new ResponseEntity<>(simulacion, HttpStatus.CREATED);
    }

    @PostMapping("/inicializarSimulacionCargaVariable/{carga}")
    public ResponseEntity<Simulacion> inicializarSimulacionCargaVariable(@RequestBody Simulacion simulacion,
            @PathVariable("carga") String carga) throws IOException, ParseException {
        if (!carga.equals("chica") && !carga.equals("media") && !carga.equals("grande")) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        System.out.println(simulacion.getFechaInicioSim() + ", " + simulacion.getFechaFinSim());

        ArrayList<Aeropuerto> aeropuertos = (ArrayList<Aeropuerto>) aeropuertoService.getAll();
        // Registrar una nueva simulacion
        // LOGGER.info("inicializarSimulacionCargaVariable - Registrando simulacion -
        // Simulacion ID" + simulacion.getId());
        // simulacion.setId(0);
        simulacion.setMilisegundosPausados(0);
        simulacion = simulacionService.register(simulacion);
        LOGGER.info("inicializarSimulacionCargaVariable - Registrando simulacion - Simulacion ID" + simulacion.getId());

        Resource resource = resourceLoader.getResource("classpath:static/envios_semanal_V3.txt");
        InputStream input1 = resource.getInputStream();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input1))) {
            List<String> lines = reader.lines().collect(Collectors.toList());
            int cantidadEnvios = lines.size();
            // extrae la fecha de la primera linea
            String primeraFechaStr = lines.get(0).split("-")[2];
            // convertir el string a fecha
            SimpleDateFormat formato = new SimpleDateFormat("yyyyMMdd");
            Date primeraFecha = formato.parse(primeraFechaStr);

            ArrayList<RegistrarEnvio> registrarEnvios = new ArrayList<>();
            for (String line : lines) {
                RegistrarEnvio registrarEnvio = new RegistrarEnvio();
                registrarEnvio.setCodigo(line);
                registrarEnvio.setSimulacion(simulacion);
                registrarEnvios.add(registrarEnvio);
                // System.out.println("LINE: " + line);
            }

            HashMap<String, Aeropuerto> aeropuertoMap = new HashMap<>();
            for (Aeropuerto aeropuerto : aeropuertos) {
                aeropuertoMap.put(aeropuerto.getUbicacion().getId(), aeropuerto);
            }
            // int cantidadEnvios = (int)
            // Files.lines(Paths.get("src\\main\\resources\\dataFija\\envios_semanal_V2.txt")).count();
            int nuevaCantidad = (carga.equals("chica")) ? cantidadEnvios / 10
                    : (carga.equals("media")) ? cantidadEnvios / 2 : cantidadEnvios;

            ArrayList<Envio> envios = envioService.registerAllByStringBloqueFecha(registrarEnvios, aeropuertoMap,
                    simulacion.getFechaInicioSim(), primeraFecha, nuevaCantidad);

            // Filtrar envios
            Date fechaMinima = simulacion.getFechaInicioSim();
            envios.removeIf(envio -> envio.getFechaRecepcion().before(fechaMinima));
            int totalPaquetes = envios.stream()
                    .mapToInt(envio -> envio.getCantidadPaquetes())
                    .sum();
            System.out.println("Se generaron " + totalPaquetes + " paquetes.");
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }

        return new ResponseEntity<>(simulacion, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Simulacion> get(@PathVariable("id") int id) {
        Simulacion Simulacion = simulacionService.get(id);
        if (Simulacion != null) {
            return new ResponseEntity<>(Simulacion, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/")
    public ResponseEntity<Simulacion> put(@RequestBody Simulacion simulacion) {
        Simulacion updatedSimulacion = simulacionService.update(simulacion);
        if (updatedSimulacion != null) {
            return new ResponseEntity<>(updatedSimulacion, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/detener")
    public ResponseEntity<Simulacion> detenerSimulacion(@RequestBody Simulacion simulacion) {
        simulacion = simulacionService.get(simulacion.getId());
        if (simulacion.getEstado() == 0) {
            simulacion.setEstado(1);
            Simulacion updatedSimulacion = simulacionService.update(simulacion);
            return new ResponseEntity<>(updatedSimulacion, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping("/reiniciar")
    public ResponseEntity<Simulacion> reiniciarSimulacion(@RequestBody Simulacion simulacion) {
        simulacion = simulacionService.get(simulacion.getId());
        if (simulacion.getEstado() == 1) {
            simulacion.setEstado(0);
            Simulacion updatedSimulacion = simulacionService.update(simulacion);
            return new ResponseEntity<>(updatedSimulacion, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping("/pausar")
    public ResponseEntity<Simulacion> pausarSimulacion(@RequestBody Simulacion simulacion) {
        simulacion = simulacionService.get(simulacion.getId());
        if (simulacion.getEstado() == 0) {
            simulacion.setEstado(2);
            Simulacion updatedSimulacion = simulacionService.update(simulacion);
            return new ResponseEntity<>(updatedSimulacion, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping("/reanudar")
    public ResponseEntity<Simulacion> reanudarSimulacion(@RequestBody Simulacion simulacion) {
        simulacion = simulacionService.get(simulacion.getId());
        simulacion.setMilisegundosPausados(simulacion.getMilisegundosPausados());
        if (simulacion.getEstado() == 2) {
            simulacion.setEstado(0);
            Simulacion updatedSimulacion = simulacionService.update(simulacion);
            return new ResponseEntity<>(updatedSimulacion, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") int id) {
        simulacionService.delete(id);
    }

    @GetMapping("/")
    public ResponseEntity<List<Simulacion>> getAll() {
        List<Simulacion> simulacions = simulacionService.getAll();
        return new ResponseEntity<>(simulacions, HttpStatus.OK);
    }

    @GetMapping("/runAlgorithm/{id}")
    public Simulacion correrSimulacion(@PathVariable("id") int id) {
        Simulacion simulacion = simulacionService.get(id);
        ArrayList<Aeropuerto> aeropuertos = (ArrayList<Aeropuerto>) aeropuertoService.getAll();
        ArrayList<Paquete> paquetes = (ArrayList<Paquete>) paqueteService.findBySimulacionId(id).stream()
                .filter(paquete -> paquete.getFechaRecepcion().after(simulacion.getFechaInicioSim()))
                .collect(Collectors.toList());
        ;
        ArrayList<PlanVuelo> planVuelos = (ArrayList<PlanVuelo>) planVueloService.getAll();
        // Algoritmo algoritmo = new Algoritmo(messagingTemplate);

        CompletableFuture.runAsync(() -> {
            algoritmo.loopPrincipal(aeropuertos, planVuelos, paquetes,
                    vueloService, planRutaService, paqueteService, aeropuertoService, planRutaXVueloService,
                    simulacionService, simulacion,
                    30, 10);
        });

        return simulacion;

    }

    @GetMapping("/obtenerFechaSimulada/{id}")
    public Date obtenerFechaSimulada(@PathVariable("id") int id) {
        Simulacion simulacion = simulacionService.get(id);
        long tiempoActual = new Date().getTime();
        long inicioSistema = simulacion.getFechaInicioSistema().getTime();
        long inicioSimulacion = simulacion.getFechaInicioSim().getTime();
        long milisegundosPausados = simulacion.getMilisegundosPausados();
        long multiplicador = (long) simulacion.getMultiplicadorTiempo();

        return new Date(inicioSimulacion
                + (tiempoActual - inicioSistema - milisegundosPausados) * multiplicador);
    }

    @GetMapping("/obtenerPaquetesSimulacionEnCurso/{id}")
    public ResponseEntity<List<Paquete>> obtenerPaquetesSimulacionEnCurso(@PathVariable("id") int id) {
        List<Paquete> paquetes = algoritmo.obtener_paquetes_simulacion(id);
        return new ResponseEntity<>(paquetes, HttpStatus.OK);
    }

}
