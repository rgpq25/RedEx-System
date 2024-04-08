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
import pe.edu.pucp.packetsoft.models.MatrizDeResponsabilidades;
import pe.edu.pucp.packetsoft.models.Presupuesto;
import pe.edu.pucp.packetsoft.services.MatrizDeResponsabilidadesService;

@RestController
@RequestMapping("/matrizDeResponsabilidades")
@CrossOrigin

public class MatrizDeResponsabilidadesController {
    
    @Autowired
    private MatrizDeResponsabilidadesService matrizDeResponsabilidadesService;

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe") 
    @GetMapping(value = "/")
    List<MatrizDeResponsabilidades> getAll(){
        return matrizDeResponsabilidadesService.getAll();
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe") 
    @GetMapping(value = "/{id}")
    MatrizDeResponsabilidades get(@PathVariable int id){
        return matrizDeResponsabilidadesService.get(id);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe") 
    @PostMapping(value = "/")
    MatrizDeResponsabilidades register(@RequestBody MatrizDeResponsabilidades matrizDeResponsabilidades) throws SQLException{
        if(matrizDeResponsabilidadesService.duplicadoPropio(matrizDeResponsabilidades.getId())!=null){
            System.out.println("ERROR: Id de presupuesto duplicado");
            throw new SQLException();
        }
        return matrizDeResponsabilidadesService.register(matrizDeResponsabilidades);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe") 
    @PutMapping(value = "/")
    MatrizDeResponsabilidades update(@RequestBody MatrizDeResponsabilidades matrizDeResponsabilidades){
        return matrizDeResponsabilidadesService.update(matrizDeResponsabilidades);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @DeleteMapping(value = "/{id}")
    void delete(@PathVariable int id){
        matrizDeResponsabilidadesService.delete(id);
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Duplicated data")
    public void conflict() {}

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe") 
    @GetMapping(value = "/seleccionarMatrizDeResponsabilidadPorHerramientaXProyecto/{idHerramientaXProyecto}")
    MatrizDeResponsabilidades seleccionarMatrizDeResponsabilidadPorHerramientaXProyecto(@PathVariable int idHerramientaXProyecto){
        return matrizDeResponsabilidadesService.seleccionarMatrizDeResponsabilidadPorHerramientaXProyecto(idHerramientaXProyecto);
    } 

}
