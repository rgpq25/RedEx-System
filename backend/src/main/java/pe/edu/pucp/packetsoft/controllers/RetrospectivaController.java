package pe.edu.pucp.packetsoft.controllers;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.edu.pucp.packetsoft.models.Retrospectiva;
import pe.edu.pucp.packetsoft.services.RetrospectivaService;

@RestController
@RequestMapping("/retrospectiva")
@CrossOrigin
public class RetrospectivaController {
    @Autowired
    private RetrospectivaService retrospectivaService;
    
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/")
    List<Retrospectiva> getAll(){
        return retrospectivaService.getAll();
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/{id}")
    Retrospectiva get(@PathVariable int id){
        return retrospectivaService.get(id);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PostMapping(value = "/")
    Retrospectiva register(@RequestBody Retrospectiva retrospectiva) throws SQLException{
        return retrospectivaService.register(retrospectiva);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PutMapping(value ="/update")
    public Retrospectiva update(@RequestBody Retrospectiva retrospectiva){
        return retrospectivaService.update(retrospectiva);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @DeleteMapping(value = "/{id}")
    void delete(@PathVariable int id){
        retrospectivaService.delete(id);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/porproyecto/{id_proyecto}")
    List<Retrospectiva> getPorProyecto(@PathVariable int id_proyecto){
        return retrospectivaService.getByProyecto(id_proyecto);
    }    
}
