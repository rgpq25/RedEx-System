package pe.edu.pucp.packetsoft.controllers;
import java.sql.SQLException;
import java.util.List;

import pe.edu.pucp.packetsoft.models.Presupuesto;
import pe.edu.pucp.packetsoft.services.PresupuestoService;

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

@RestController
@RequestMapping("/presupuesto")
@CrossOrigin

public class PresupuestoController {
    
    @Autowired
    private PresupuestoService presupuestoService;

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/")
    List<Presupuesto> getAll(){
        return presupuestoService.getAll();
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/{id}")
    Presupuesto get(@PathVariable int id){
        return presupuestoService.get(id);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PostMapping(value = "/")
    Presupuesto register(@RequestBody Presupuesto presupuesto) throws SQLException{
        if(presupuestoService.duplicadoPropio(presupuesto.getId())!=null){
            System.out.println("ERROR: Id de presupuesto duplicado");
            throw new SQLException();
        }
        return presupuestoService.register(presupuesto);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PutMapping(value = "/")
    Presupuesto update(@RequestBody Presupuesto presupuesto){
        return presupuestoService.update(presupuesto);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @DeleteMapping(value = "/{id}")
    void delete(@PathVariable int id){
        presupuestoService.delete(id);
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Duplicated data")
    public void conflict() {}

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/seleccionarPresupuestoPorHerramientaXProyecto/{idHerramientaXProyecto}")
    Presupuesto seleccionarPresupuestoPorHerramientaXProyecto(@PathVariable int idHerramientaXProyecto){
        return presupuestoService.seleccionarPresupuestoPorHerramientaXProyecto(idHerramientaXProyecto);
    }    
}
