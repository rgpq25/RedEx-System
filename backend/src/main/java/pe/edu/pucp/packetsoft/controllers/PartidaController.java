package pe.edu.pucp.packetsoft.controllers;
import java.sql.SQLException;
import java.util.List;

import pe.edu.pucp.packetsoft.models.Partida;
import pe.edu.pucp.packetsoft.services.PartidaService;

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
@RequestMapping("/partida")
@CrossOrigin
public class PartidaController {
    @Autowired
    private PartidaService partidaService;

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/")
    List<Partida> getAll(){
        return partidaService.getAll();
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/{id}")
    Partida get(@PathVariable int id){
        return partidaService.get(id);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PostMapping(value = "/")
    Partida register(@RequestBody Partida partida) throws SQLException{
        if(partidaService.duplicadoPropio(partida.getId())!=null){
            System.out.println("ERROR: Duplicado de partida");
            throw new SQLException();
        }
        return partidaService.register(partida);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PutMapping(value="/")
    Partida update(@RequestBody Partida partida){
        return partidaService.update(partida);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @DeleteMapping(value="/{id}")
    void delete(@PathVariable int id){
        partidaService.delete(id);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value="/listarPartidasPorIdPresupuesto/{idPresupuesto}")
    List<Partida> listarPartidasPorIdPresupuesto(@PathVariable int idPresupuesto){
        return partidaService.listarPartidasPorIdPresupuesto(idPresupuesto);
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Duplicated data")
    public void conflict() {}

}
