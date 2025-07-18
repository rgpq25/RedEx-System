package pucp.e3c.redex_back.controller;

import java.util.ArrayList;
import java.util.Date;
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

import pucp.e3c.redex_back.model.Paquete;
import pucp.e3c.redex_back.service.PaqueteService;

@RestController
@RequestMapping("back/paquete")
public class PaqueteController {
    @Autowired
    private PaqueteService paqueteService;

    @PostMapping(value = "/")
    public Paquete register(@RequestBody Paquete paquete) {
        return paqueteService.register(paquete);
    }

    @PutMapping(value = "/")
    public Paquete update(@RequestBody Paquete paquete) {
        return paqueteService.update(paquete);
    }

    @GetMapping(value = "/")
    public List<Paquete> getAll() {
        return paqueteService.getAll();
    }

    @GetMapping(value = "/{id}")
    public Paquete get(@PathVariable("id") Integer id) {
        return paqueteService.get(id);
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable("id") Integer id) {
        paqueteService.delete(id);
    }

    @GetMapping(value = "/aeropuerto/{id}")
    public List<Paquete> findByAeropuertoActualId(@PathVariable("id") Integer id) {
        return paqueteService.findByAeropuertoActualId(id);
    }

    @GetMapping(value = "/envio/{id}")
    public List<Paquete> findByEnvioId(@PathVariable("id") Integer id) {
        return paqueteService.findByEnvioId(id);
    }

    @GetMapping(value = "/simulacion/{id}")
    public List<Paquete> findBySimulacionId(@PathVariable("id") Integer id) {
        return paqueteService.findBySimulacionId(id);
    }

    /*
     * @GetMapping(value = "/simulacion/{idSimulacion}/{idUbicacionOrigen}")
     * public ArrayList<Paquete>
     * findPaquetesWithoutPlanRuta(@PathVariable("idUbicacionOrigen") String
     * idUbicacionOrigen,@PathVariable("idSimulacion") Integer idSimulacion){
     * Date fechaCorte = new Date();
     * return
     * paqueteService.findPaquetesWithoutPlanRutaSimulacion(idUbicacionOrigen,
     * idSimulacion,fechaCorte);
     * }
     */

    @GetMapping(value = "/getPaqueteNoSimulacion/{id}")
    public Paquete getPaqueteNoSimulacion(@PathVariable("id") Integer id) {
        return paqueteService.getPaqueteNoSimulacion(id);
    }
}
