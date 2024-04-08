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
import pe.edu.pucp.packetsoft.models.CatalogoDeInteresados;

import pe.edu.pucp.packetsoft.services.CatalogoDeInteresadosService;


@RestController
@RequestMapping("/catalogoDeInteresados")
@CrossOrigin
public class CatalogoDeInteresadosController {
    @Autowired
    private CatalogoDeInteresadosService catalogoDeInteresadosService;

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/")
    List<CatalogoDeInteresados> getAll(){
        return catalogoDeInteresadosService.getAll();
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/{id}")
    CatalogoDeInteresados get(@PathVariable int id){
        return catalogoDeInteresadosService.get(id);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PostMapping(value = "/")
    CatalogoDeInteresados register(@RequestBody CatalogoDeInteresados catalogoDeInteresados) throws SQLException{
        if(catalogoDeInteresadosService.duplicadoPropio(catalogoDeInteresados.getId())!=null){
            System.out.println("ERROR: Id de catalogo de interesados duplicado");
            throw new SQLException();
        }
        return catalogoDeInteresadosService.register(catalogoDeInteresados);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PutMapping(value = "/")
    CatalogoDeInteresados update(@RequestBody CatalogoDeInteresados catalogoDeInteresados){
        return catalogoDeInteresadosService.update(catalogoDeInteresados);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @DeleteMapping(value = "/{id}")
    void delete(@PathVariable int id){
        catalogoDeInteresadosService.delete(id);
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Duplicated data")
    public void conflict() {}

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/seleccionarCatalogoDeInteresadosPorHerramientaXProyecto/{idHerramientaXProyecto}")
    CatalogoDeInteresados seleccionarCatalogoDeInteresadosPorHerramientaXProyecto(@PathVariable int idHerramientaXProyecto){
        return catalogoDeInteresadosService.seleccionarCatalogoDeInteresadosByHerramientaXProyecto(idHerramientaXProyecto);
    }

    
    
}
