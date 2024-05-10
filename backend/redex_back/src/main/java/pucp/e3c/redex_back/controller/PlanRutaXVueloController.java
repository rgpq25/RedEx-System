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

import pucp.e3c.redex_back.model.PlanRutaXVuelo;
import pucp.e3c.redex_back.service.PlanRutaXVueloService;

@RestController
@RequestMapping("back/plan_ruta_vuelo")
public class PlanRutaXVueloController {
    @Autowired
    PlanRutaXVueloService planRutaXVueloService;

    @PostMapping("/")
    public ResponseEntity<PlanRutaXVuelo> register(@RequestBody PlanRutaXVuelo planRutaXVuelo) {
        PlanRutaXVuelo registeredPlanVuelo = planRutaXVueloService.register(planRutaXVuelo);
        return new ResponseEntity<>(registeredPlanVuelo, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanRutaXVuelo> get(@PathVariable("id") int id) {
        PlanRutaXVuelo planRutaXVuelo = planRutaXVueloService.get(id);
        if (planRutaXVuelo != null) {
            return new ResponseEntity<>(planRutaXVuelo, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/")
    public ResponseEntity<PlanRutaXVuelo> put(@RequestBody PlanRutaXVuelo planVuelo) {
        PlanRutaXVuelo updatedPlanRutaXVuelo = planRutaXVueloService.update(planVuelo);
        if (updatedPlanRutaXVuelo != null) {
            return new ResponseEntity<>(updatedPlanRutaXVuelo, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") int id) {
        planRutaXVueloService.delete(id);
    }

    @GetMapping("/")
    public ResponseEntity<List<PlanRutaXVuelo>> getAll() {
        List<PlanRutaXVuelo> planRutaXVuelos = planRutaXVueloService.getAll();
        return new ResponseEntity<>(planRutaXVuelos, HttpStatus.OK);
    }

    @GetMapping("/plan_ruta/{id}")
    public ResponseEntity<List<PlanRutaXVuelo>> getByPlanRuta(@PathVariable("id") int id) {
        List<PlanRutaXVuelo> planRutaXVuelos = planRutaXVueloService.findByPlanRuta(id);
        return new ResponseEntity<>(planRutaXVuelos, HttpStatus.OK);
    }

}
