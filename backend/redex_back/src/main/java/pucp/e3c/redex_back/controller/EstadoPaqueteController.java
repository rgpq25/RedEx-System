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

import pucp.e3c.redex_back.model.EstadoPaquete;
import pucp.e3c.redex_back.service.EstadoPaqueteService;

@RestController
@RequestMapping("back/estadopaquete")
public class EstadoPaqueteController {
    @Autowired
    private EstadoPaqueteService estadoPaqueteService;

    @PostMapping(value = "/")
    public EstadoPaquete register(@RequestBody EstadoPaquete estadoPaquete) {
        return estadoPaqueteService.register(estadoPaquete);
    }

    @PutMapping(value = "/")
    public EstadoPaquete update(@RequestBody EstadoPaquete estadoPaquete) {
        return estadoPaqueteService.update(estadoPaquete);
    }

    @GetMapping(value = "/")
    public List<EstadoPaquete> getAll() {
        return estadoPaqueteService.getAll();
    }

    @GetMapping(value = "/{id}")
    public EstadoPaquete get(@PathVariable("id") Integer id) {
        return estadoPaqueteService.get(id);
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable("id") Integer id) {
        estadoPaqueteService.delete(id);
    }
    
}
