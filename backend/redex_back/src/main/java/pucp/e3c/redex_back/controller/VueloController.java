package pucp.e3c.redex_back.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;

import pucp.e3c.redex_back.model.Paquete;
import pucp.e3c.redex_back.model.PlanRuta;
import pucp.e3c.redex_back.model.Vuelo;
import pucp.e3c.redex_back.service.PaqueteService;
import pucp.e3c.redex_back.service.PlanRutaXVueloService;
import pucp.e3c.redex_back.service.VueloService;

@RestController
@RequestMapping("back/vuelo")
public class VueloController {
    @Autowired
    VueloService vueloService;

    @Autowired
    PlanRutaXVueloService planRutaXVueloService;

    @Autowired
    PaqueteService paqueteService;

    @PostMapping("/")
    public ResponseEntity<Vuelo> register(@RequestBody Vuelo vuelo) {
        Vuelo registeredVuelo = vueloService.register(vuelo);
        return new ResponseEntity<>(registeredVuelo, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vuelo> get(@PathVariable("id") int id) {
        Vuelo vuelo = vueloService.get(id);
        if (vuelo != null) {
            return new ResponseEntity<>(vuelo, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/")
    public ResponseEntity<Vuelo> put(@RequestBody Vuelo vuelo) {
        Vuelo updatedVuelo = vueloService.update(vuelo);
        if (updatedVuelo != null) {
            return new ResponseEntity<>(updatedVuelo, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") int id) {
        vueloService.delete(id);
    }

    @GetMapping("/")
    public ResponseEntity<List<Vuelo>> getAll() {
        List<Vuelo> vuelos = vueloService.getAll();
        return new ResponseEntity<>(vuelos, HttpStatus.OK);
    }

    @GetMapping("/{id}/paquetes")
    public List<Paquete> getPaquetes(@PathVariable("id") int id) {
        Vuelo vuelo = vueloService.get(id);
        if (vuelo != null) {
            List<PlanRuta> planesRuta = planRutaXVueloService.findPlanesRutaByVuelo(vuelo.getId());
            List<Paquete> paquetes = new ArrayList<Paquete>();
            for (PlanRuta planRuta : planesRuta) {
                Paquete paquete = paqueteService.findByPlanRutaId(planRuta.getId());
                if (paquete != null) {
                    paquetes.add(paquete);
                }
            }
            return paquetes;
        } else {
            return null;
        }
    }
}
