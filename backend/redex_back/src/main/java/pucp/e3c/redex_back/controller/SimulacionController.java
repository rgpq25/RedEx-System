package pucp.e3c.redex_back.controller;

import java.util.ArrayList;
import java.util.List;

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
import pucp.e3c.redex_back.model.Paquete;
import pucp.e3c.redex_back.model.PlanRutaNT;
import pucp.e3c.redex_back.model.PlanVuelo;
import pucp.e3c.redex_back.model.RespuestaAlgoritmo;
import pucp.e3c.redex_back.model.Simulacion;
import pucp.e3c.redex_back.service.AeropuertoService;
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

    public SimulacionController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/")
    public ResponseEntity<Simulacion> register(@RequestBody Simulacion simulacion) {
        Simulacion registeredSimulacion = simulacionService.register(simulacion);
        return new ResponseEntity<>(registeredSimulacion, HttpStatus.CREATED);
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
    public void correrSimulacion(@PathVariable("id") int id) {
        ArrayList<Aeropuerto> aeropuertos = (ArrayList<Aeropuerto>) aeropuertoService.getAll();
        ArrayList<Paquete> paquetes = (ArrayList<Paquete>) paqueteService.findBySimulacionId(id);
        ArrayList<PlanVuelo> planVuelos = (ArrayList<PlanVuelo>) planVueloService.getAll();
        Algoritmo algoritmo = new Algoritmo(messagingTemplate);
        Simulacion simulacion = simulacionService.get(id);
        algoritmo.loopPrincipal(aeropuertos, planVuelos, paquetes,
                vueloService, planRutaService, paqueteService, planRutaXVueloService, simulacion);

    }

}
