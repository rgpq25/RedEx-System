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

import pucp.e3c.redex_back.model.Simulacion;
import pucp.e3c.redex_back.service.SimulacionService;

@RestController
@RequestMapping("back/simulacion")
public class SimulacionController {
    @Autowired
    SimulacionService simulacionService;

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
}
