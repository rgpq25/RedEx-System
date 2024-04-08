package pe.edu.pucp.packetsoft.controllers;

import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import pe.edu.pucp.packetsoft.models.HerramientaXProyecto;
import pe.edu.pucp.packetsoft.services.HerramientaXProyectoService;


@RestController
@RequestMapping("/herramientaXProyecto")
@CrossOrigin
public class HerramientaXProyectoController {
    @Autowired
    private HerramientaXProyectoService herramientaXProyectoService;

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/")
    List<HerramientaXProyecto> getAll(){
        return herramientaXProyectoService.getAll();
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/{id}")
    HerramientaXProyecto get(@PathVariable int id){
        return herramientaXProyectoService.get(id);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PostMapping(value = "/")
    HerramientaXProyecto register(@RequestBody HerramientaXProyecto herramientaXProyecto) throws SQLException{
        HerramientaXProyecto resultado = herramientaXProyectoService.register(herramientaXProyecto);
        if(resultado == null){
            throw new SQLException();
        }
        return resultado;
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PutMapping(value = "/")
    HerramientaXProyecto update(@RequestBody HerramientaXProyecto herramientaXProyecto) throws SQLException{
        return herramientaXProyectoService.update(herramientaXProyecto);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @DeleteMapping(value = "/{id}")
    void delete(@PathVariable int id){
        herramientaXProyectoService.delete(id);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/listarHerramientasXProyectoPorProyecto/{idProyecto}")
    List<HerramientaXProyecto> listarHerramientasXProyecto(@PathVariable int idProyecto){
        return herramientaXProyectoService.listarHerramientasXProyectoPorProyecto(idProyecto);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/listarHerramientasXProyectoPorHerramienta/{idHerramienta}")
    List<HerramientaXProyecto> listarHerramientasXProyectoPorHerramienta(@PathVariable int idHerramienta){
        return herramientaXProyectoService.listarHerramientasXProyectoPorHerramienta(idHerramienta);
    }
}
