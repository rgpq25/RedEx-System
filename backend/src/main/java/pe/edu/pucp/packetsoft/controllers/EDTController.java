package pe.edu.pucp.packetsoft.controllers;
import java.sql.SQLException;
import java.util.List;

import pe.edu.pucp.packetsoft.models.EDT;
import pe.edu.pucp.packetsoft.services.EDTService;
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
@RequestMapping("/edt")
@CrossOrigin
public class EDTController {
    @Autowired
    private EDTService edtService;

    //Trae todos los edt
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/")
    List<EDT> getAll(){
        return edtService.getAll();
    }

    //Trae a un edt
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/{id}")
    EDT get(@PathVariable int id){
        return edtService.get(id);
    }

    //Registra un edt
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PostMapping(value = "/")
    EDT register(@RequestBody EDT edt) throws SQLException{
        
        if(edtService.duplicadoPropio(edt.getId())!=null){
            throw new SQLException();
        }
        return edtService.register(edt);
    }

    //Actualiza un edt
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PutMapping(value = "/")
    EDT update(@RequestBody EDT edt){
        return edtService.update(edt);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @DeleteMapping(value = "/{id}")
    void delete(@PathVariable int id){
        edtService.delete(id);
    }

    //Excepcion para data duplicada
    @ExceptionHandler(SQLException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Duplicated data")
    public void conflict() {}

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/seleccionarEDTPorHerramientaXProyecto/{idHerramientaXProyecto}")
    EDT seleccionarEDTPorHerramientaXProyecto(@PathVariable int idHerramientaXProyecto){
        return edtService.seleccionarEDTPorHerramientaXProyecto(idHerramientaXProyecto);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/seleccionarEDTPorCronograma/{idCronograma}")
    EDT seleccionarEDTPorCronograma(@PathVariable int idCronograma){
        return edtService.seleccionarEDTPorCronograma(idCronograma);
    }
}
