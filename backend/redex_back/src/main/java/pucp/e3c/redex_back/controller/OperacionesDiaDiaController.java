package pucp.e3c.redex_back.controller;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pucp.e3c.redex_back.model.Aeropuerto;
import pucp.e3c.redex_back.model.Algoritmo;
import pucp.e3c.redex_back.model.Paquete;
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
@RequestMapping("back/operacionesDiaDia")
public class OperacionesDiaDiaController {

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
    private Algoritmo algoritmo;

    @GetMapping("/diaDiaRespuesta")
    public RespuestaAlgoritmo resendLastMessage() {
        return algoritmo.getUltimaRespuestaOperacionDiaDia();
    }

    /*public RespuestaAlgoritmo obtenerUltimaRespuesta(){

    }*/

    /*@GetMapping("/runAlgorithm")
    public String correrSimulacion() {
        ArrayList<Aeropuerto> aeropuertos = (ArrayList<Aeropuerto>) aeropuertoService.getAll();
        ArrayList<PlanVuelo> planVuelos = (ArrayList<PlanVuelo>) planVueloService.getAll();
        Algoritmo algoritmo = new Algoritmo(messagingTemplate);
        CompletableFuture.runAsync(() -> {
            algoritmo.loopPrincipalDiaADia(aeropuertos, planVuelos,
                    vueloService, planRutaService, paqueteService, planRutaXVueloService, simulacionService,
                    300, 240);
        });

        return "Simulacion iniciada";

    }*/
    
    /*@GetMapping("/ejecutarUnaPlanificacion")
    public RespuestaAlgoritmo ejecutarUnaPlanificacion(){
        ArrayList<Aeropuerto> aeropuertos = (ArrayList<Aeropuerto>) aeropuertoService.getAll();
        ArrayList<PlanVuelo> planVuelos = (ArrayList<PlanVuelo>) planVueloService.getAll();
        Algoritmo algoritmo = new Algoritmo(messagingTemplate);
        return algoritmo.unaPlanificacionDiaDia(aeropuertos, planVuelos, vueloService, planRutaService, paqueteService, planRutaXVueloService);        
    }*/
}
