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

import pucp.e3c.redex_back.model.PaqueteXEstadoPaquete;
import pucp.e3c.redex_back.service.PaqueteXEstadoPaqueteService;

@RestController
@RequestMapping("back/paquetexestadopaquete")
public class PaqueteXEstadoPaqueteController {
    @Autowired
    private PaqueteXEstadoPaqueteService paqueteXEstadoPaqueteService;
    
    @PostMapping(value = "/")
    public PaqueteXEstadoPaquete  register(@RequestBody PaqueteXEstadoPaquete paqueteXEstadoPaquete) {
        return paqueteXEstadoPaqueteService.register(paqueteXEstadoPaquete);
    }

    @PutMapping(value = "/")
    public PaqueteXEstadoPaquete  update(@RequestBody PaqueteXEstadoPaquete paqueteXEstadoPaquete) {
        return paqueteXEstadoPaqueteService.update(paqueteXEstadoPaquete);
    }

    @GetMapping(value = "/")
    public List<PaqueteXEstadoPaquete> getAll() {
        return paqueteXEstadoPaqueteService.getAll();
    }

    @GetMapping(value = "/{id}")
    public PaqueteXEstadoPaquete get(@PathVariable("id") Integer id) {
        return paqueteXEstadoPaqueteService.get(id);
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable("id") Integer id) {
        paqueteXEstadoPaqueteService.delete(id);
    }

    @GetMapping(value = "/paquete/{id}")
    public List<PaqueteXEstadoPaquete> findByPaqueteId(@PathVariable("id") Integer id) {
        return paqueteXEstadoPaqueteService.findByPaqueteId(id);
    }

    @GetMapping(value = "/estadopaquete/{id}")
    public List<PaqueteXEstadoPaquete> findByEstadoPaqueteId(@PathVariable("id") Integer id) {
        return paqueteXEstadoPaqueteService.findByEstadoPaqueteId(id);
    }
    
}
