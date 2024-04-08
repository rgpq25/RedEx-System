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
import pe.edu.pucp.packetsoft.models.MatrizDeComunicaciones;
import pe.edu.pucp.packetsoft.services.MatrizDeComunicacionesService;

@RestController
@RequestMapping("/matrizDeComunicaciones")
@CrossOrigin
public class MatrizDeComunicacionesController {

    @Autowired
    private MatrizDeComunicacionesService matrizDeComunicacionesService;

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/")
    List<MatrizDeComunicaciones> getAll(){
        return matrizDeComunicacionesService.getAll();
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/{id}")
    MatrizDeComunicaciones get(@PathVariable int id){
        return matrizDeComunicacionesService.get(id);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PostMapping(value = "/")
    MatrizDeComunicaciones register(@RequestBody MatrizDeComunicaciones matrizDeComunicaciones) throws SQLException{
        if(matrizDeComunicacionesService.duplicadoPropio(matrizDeComunicaciones.getId())!=null){
            System.out.println("ERROR: Id de matriz de comunicaciones duplicado");
            throw new SQLException();
        }
        return matrizDeComunicacionesService.register(matrizDeComunicaciones);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PutMapping(value = "/")
    MatrizDeComunicaciones update(@RequestBody MatrizDeComunicaciones matrizDeComunicaciones){
        return matrizDeComunicacionesService.update(matrizDeComunicaciones);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @DeleteMapping(value = "/{id}")
    void delete(@PathVariable int id){
        matrizDeComunicacionesService.delete(id);
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Duplicated data")
    public void conflict() {}

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/seleccionarMatrizDeComunicacionPorHerramientaXProyecto/{idHerramientaXProyecto}")
    MatrizDeComunicaciones seleccionarMatrizDeComunicacionPorHerramientaXProyecto(@PathVariable int idHerramientaXProyecto){
        return matrizDeComunicacionesService.seleccionarMatrizDeComunicacionPorHerramientaXProyecto(idHerramientaXProyecto);
    } 
    
}
