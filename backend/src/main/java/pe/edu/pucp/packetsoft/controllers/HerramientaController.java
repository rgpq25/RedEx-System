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
import pe.edu.pucp.packetsoft.models.Herramienta;
import pe.edu.pucp.packetsoft.services.HerramientaService;

@RestController
@RequestMapping("/herramienta")
@CrossOrigin
public class HerramientaController {
    @Autowired
    private HerramientaService herramientaRepositoryService;

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/")
    List<Herramienta> getAll(){
        return herramientaRepositoryService.getAll();
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/{id}")
    Herramienta get(@PathVariable int id){
        return herramientaRepositoryService.get(id);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PostMapping(value = "/")
    Herramienta register(@RequestBody Herramienta herramienta) throws SQLException{
        Herramienta resultado = herramientaRepositoryService.register(herramienta);
        if(resultado == null){
            throw new SQLException();
        }
        return resultado;
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PutMapping(value = "/")
    Herramienta update(@RequestBody Herramienta herramienta) throws SQLException{
        return herramientaRepositoryService.update(herramienta);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @DeleteMapping(value = "/{id}")
    void delete(@PathVariable int id){
        herramientaRepositoryService.delete(id);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/listarHerramientasPorCategoria/{idCategoria}")
    List<Herramienta> listarHerramientasPorCategoria(@PathVariable int idCategoria){
        return herramientaRepositoryService.listarHerramientasPorCategoria(idCategoria);
    }
    
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/EncontrarIdxHerramienta/{nombre}")
    int EncontrarIdxHerramienta(@PathVariable String nombre){
        return herramientaRepositoryService.EncontrarIdxHerramienta(nombre);
    }

}
