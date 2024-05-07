package pucp.e3c.redex_back.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;

import pucp.e3c.redex_back.model.PlanRuta;
import pucp.e3c.redex_back.service.PlanRutaService;

@RestController
@RequestMapping("back/plan_ruta")
public class PlanRutaController {
    @Autowired
    PlanRutaService planRutaService;

    @PostMapping("/")
    public ResponseEntity<PlanRuta> register(@RequestBody PlanRuta planRuta) {
        PlanRuta registeredPlanRuta = planRutaService.register(planRuta);
        return new ResponseEntity<>(registeredPlanRuta, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanRuta> get(@PathVariable("id") int id) {
        PlanRuta planRuta = planRutaService.get(id);
        if (planRuta != null) {
            return new ResponseEntity<>(planRuta, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/")
    public ResponseEntity<PlanRuta> put(@RequestBody PlanRuta planRuta) {
        PlanRuta updatedPlanRuta = planRutaService.update(planRuta);
        if (updatedPlanRuta != null) {
            return new ResponseEntity<>(updatedPlanRuta, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") int id) {
        planRutaService.delete(id);
    }

    @GetMapping("/")
    public ResponseEntity<List<PlanRuta>> getAll() {
        List<PlanRuta> planRutas = planRutaService.getAll();
        return new ResponseEntity<>(planRutas, HttpStatus.OK);
    }
}
