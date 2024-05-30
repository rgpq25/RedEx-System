package pucp.e3c.redex_back.controller;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
        System.out.println("Se encontraron " + envios.size() + " envios");
        System.out.println("La fecha minima es " + fechaMinima);
        envios.removeIf(envio -> envio.getFechaRecepcion().before(fechaMinima));
        System.out.println("Tras quedaron " + envios.size() + " envios");

        for (Envio envio : envios) {
            int antiguoEnvioId = envio.getId();
            envio.setId(0);
            envio.setSimulacionActual(simulacion);
            envio = envioService.register(envio);
            ArrayList<Paquete> paquetes = new ArrayList<>(paqueteService.findByEnvioId(antiguoEnvioId));
            for (Paquete paquete : paquetes) {
                paquete.setId(0);
                paquete.setEnvio(envio);
                paquete.setSimulacionActual(simulacion);
                paquete = paqueteService.register(paquete);
            }
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
    public String correrSimulacion(@PathVariable("id") int id) {
        ArrayList<Aeropuerto> aeropuertos = (ArrayList<Aeropuerto>) aeropuertoService.getAll();
        ArrayList<Paquete> paquetes = (ArrayList<Paquete>) paqueteService.findBySimulacionId(id);
        ArrayList<PlanVuelo> planVuelos = (ArrayList<PlanVuelo>) planVueloService.getAll();
        // Algoritmo algoritmo = new Algoritmo(messagingTemplate);
        Simulacion simulacion = simulacionService.get(id);
        CompletableFuture.runAsync(() -> {
            algoritmo.loopPrincipal(aeropuertos, planVuelos, paquetes,
                    vueloService, planRutaService, paqueteService, aeropuertoService, planRutaXVueloService,
                    simulacionService, simulacion,
                    30, 10);
        });

        return "Simulacion iniciada";

    }

}
