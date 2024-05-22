package pucp.e3c.redex_back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pucp.e3c.redex_back.model.PlanVuelo;
import pucp.e3c.redex_back.service.PlanVueloService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("back/plan_vuelo")
public class PlanVueloController {
    @Autowired
    PlanVueloService planVueloService;

    @PostMapping("/")
    public ResponseEntity<PlanVuelo> register(@RequestBody PlanVuelo planVuelo) {
        PlanVuelo registeredPlanVuelo = planVueloService.register(planVuelo);
        return new ResponseEntity<>(registeredPlanVuelo, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanVuelo> get(@PathVariable("id") int id) {
        PlanVuelo planVuelo = planVueloService.get(id);
        if (planVuelo != null) {
            return new ResponseEntity<>(planVuelo, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/")
    public ResponseEntity<PlanVuelo> put(@RequestBody PlanVuelo planVuelo) {
        PlanVuelo updatedPlanVuelo = planVueloService.update(planVuelo);
        if (updatedPlanVuelo != null) {
            return new ResponseEntity<>(updatedPlanVuelo, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") int id) {
        planVueloService.delete(id);
    }

    @GetMapping("/")
    public ResponseEntity<List<PlanVuelo>> getAll() {
        List<PlanVuelo> planVuelos = planVueloService.getAll();
        return new ResponseEntity<>(planVuelos, HttpStatus.OK);
    }
}
