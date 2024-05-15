package pucp.e3c.redex_back.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pucp.e3c.redex_back.model.Aeropuerto;
import pucp.e3c.redex_back.model.Paquete;
import pucp.e3c.redex_back.service.AeropuertoService;
import pucp.e3c.redex_back.service.SimulacionService;

@RestController
@RequestMapping("back/aeropuerto")
public class AeropuertoController {
    
    @Autowired
    private AeropuertoService aeropuertoService;

    @Autowired
    private SimulacionService simulacionService;


    
    @PostMapping(value = "/")
    public Aeropuerto  register(@RequestBody Aeropuerto aeropuerto) {
        return aeropuertoService.register(aeropuerto);
    }

    @PutMapping(value = "/")
    public Aeropuerto  update(@RequestBody Aeropuerto aeropuerto) {
        return aeropuertoService.update(aeropuerto);
    }

    @GetMapping(value = "/")
    public List<Aeropuerto> getAll() {
        return aeropuertoService.getAll();
    }

    @GetMapping(value = "/{id}")
    public Aeropuerto get(@PathVariable("id") Integer id) {
        return aeropuertoService.get(id);
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable("id") Integer id) {
        aeropuertoService.delete(id);
    }

    @GetMapping(value = "/ubicacion/{idUbicacion}")
    public Aeropuerto findByUbicacion(@PathVariable("idUbicacion") String idUbicacion) {
        return aeropuertoService.findByUbicacion(idUbicacion);
    }
    
    /*@GetMapping(value = "/{idAeropuerto}/simulation/{idSimulacion}")
    public List<Paquete> getPaquetesFromSimulation(@PathVariable("idAeropuerto") Integer id, @PathVariable("idSimulacion") Integer idSimulacion){
        return aeropuertoService.getPaquetesFromSimulation(id);
    }*/
    
}
