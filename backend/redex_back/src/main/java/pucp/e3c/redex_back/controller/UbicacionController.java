package pucp.e3c.redex_back.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pucp.e3c.redex_back.model.Ubicacion;
import pucp.e3c.redex_back.service.UbicacionService;

@RestController
@RequestMapping("back/ubicacion")
public class UbicacionController {

    @Autowired
    private UbicacionService ubicacionService;
    
    @PostMapping("/")
    public Ubicacion  register(@RequestBody Ubicacion ubicacion) {
        return ubicacionService.register(ubicacion);
    }

    @GetMapping("/")
    public List<Ubicacion> getAll() {
        return ubicacionService.getAll();
    }
    
    @GetMapping("/{id}")
    public Ubicacion get(@PathVariable("id") String id) {
        return ubicacionService.get(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        ubicacionService.delete(id);
    }
}
