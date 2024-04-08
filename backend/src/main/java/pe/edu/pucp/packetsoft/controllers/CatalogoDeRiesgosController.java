package pe.edu.pucp.packetsoft.controllers;
import java.sql.SQLException;
import java.util.List;

import pe.edu.pucp.packetsoft.models.CatalogoDeRiesgos;

import pe.edu.pucp.packetsoft.services.CatalogoDeRiesgosService;

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
@RequestMapping("/catalogoDeRiesgos")
@CrossOrigin
public class CatalogoDeRiesgosController {
    @Autowired
    private CatalogoDeRiesgosService catalogoDeRiesgosService;
    
    //Trae todos los catalogoDeRiesgos
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/")
    List<CatalogoDeRiesgos> getAll(){
        return catalogoDeRiesgosService.getAll();
    }

    //Trae a un catalogoDeRiesgos
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/{id}")
    CatalogoDeRiesgos get(@PathVariable int id){
        return catalogoDeRiesgosService.get(id);
    }

    //Registra un catalogoDeRiesgos
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PostMapping(value = "/")
    CatalogoDeRiesgos register(@RequestBody CatalogoDeRiesgos catalogoDeRiesgos) throws SQLException{
        
        if(catalogoDeRiesgosService.duplicadoPropio(catalogoDeRiesgos.getId())!=null){
            throw new SQLException();
        }
        return catalogoDeRiesgosService.register(catalogoDeRiesgos);
    }

    //Actualiza un catalogoDeRiesgos
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PutMapping(value = "/")
    CatalogoDeRiesgos update(@RequestBody CatalogoDeRiesgos catalogoDeRiesgos){
        return catalogoDeRiesgosService.update(catalogoDeRiesgos);
    }

    @DeleteMapping(value = "/{id}")
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    void delete(@PathVariable int id){
        catalogoDeRiesgosService.delete(id);
    }

    //Excepcion para data duplicada
    @ExceptionHandler(SQLException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Duplicated data")
    public void conflict() {}

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/seleccionarCatalogoDeRiesgosPorHerramientaXProyecto/{idHerramientaXProyecto}")
    CatalogoDeRiesgos seleccionarCatalogoDeRiesgosPorHerramientaXProyecto(@PathVariable int idHerramientaXProyecto){
        return catalogoDeRiesgosService.seleccionarCatalogoDeRiesgosPorHerramientaXProyecto(idHerramientaXProyecto);
    }
}
