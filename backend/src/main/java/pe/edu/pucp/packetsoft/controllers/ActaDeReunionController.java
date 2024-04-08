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

import pe.edu.pucp.packetsoft.models.ActaDeReunion;
import pe.edu.pucp.packetsoft.services.ActaDeReunionService;

@RestController
@RequestMapping("/actadereunion")
@CrossOrigin
public class ActaDeReunionController {
    @Autowired
    private ActaDeReunionService actaDeReunionService;
    
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/")
    List<ActaDeReunion> getAll(){
        return actaDeReunionService.getAll();
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/{id}")
    ActaDeReunion get(@PathVariable int id){
        return actaDeReunionService.get(id);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PostMapping(value = "/")
    ActaDeReunion register(@RequestBody ActaDeReunion act) throws SQLException{
        return actaDeReunionService.register(act);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PutMapping(value ="/update")
    public ActaDeReunion update(@RequestBody ActaDeReunion acta){
        return actaDeReunionService.update(acta);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @DeleteMapping(value = "/{id}")
    void delete(@PathVariable int id){
        actaDeReunionService.delete(id);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/porproyecto/{id}")
    List<ActaDeReunion> getPorProyecto(@PathVariable int id){
        return actaDeReunionService.getByProyecto(id);
    }
}
