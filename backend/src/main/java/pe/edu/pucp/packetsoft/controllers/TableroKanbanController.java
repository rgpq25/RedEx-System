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
import pe.edu.pucp.packetsoft.models.TableroKanban;
import pe.edu.pucp.packetsoft.services.TableroKanbanService;

@RestController
@RequestMapping("/tableroKanban")
@CrossOrigin
public class TableroKanbanController {

    @Autowired
    private TableroKanbanService tableroKanbanService;

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/")
    List<TableroKanban> getAll(){
        return tableroKanbanService.getAll();
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/{id}")
    TableroKanban get(@PathVariable int id){
        return tableroKanbanService.get(id);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PostMapping(value = "/")
    TableroKanban register(@RequestBody TableroKanban tableroKanban) throws SQLException{
        if(tableroKanbanService.duplicadoPropio(tableroKanban.getId())!=null){
            System.out.println("ERROR: Id de tablero kanban duplicado");
            throw new SQLException();
        }
        return tableroKanbanService.register(tableroKanban);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PutMapping(value = "/")
    TableroKanban update(@RequestBody TableroKanban tableroKanban){
        return tableroKanbanService.update(tableroKanban);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @DeleteMapping(value = "/{id}")
    void delete(@PathVariable int id){
        tableroKanbanService.delete(id);
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Duplicated data")
    public void conflict() {}

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/seleccionarTableroKanbanPorHerramientaXProyecto/{idHerramientaXProyecto}")
    TableroKanban seleccionarTableroKanbanPorHerramientaXProyecto(@PathVariable int idHerramientaXProyecto){
        return tableroKanbanService.seleccionarTableroKanbanPorHerramientaXProyecto(idHerramientaXProyecto);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/seleccionarTableroKanbanPorPersona/{idPersona}")
    TableroKanban seleccionarTableroKanbanPorPersona(@PathVariable int idPersona){
        return tableroKanbanService.seleccionarTableroKanbanPorPersona(idPersona);
    }
    
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/seleccionarTableroKanbanPorProductBacklog/{idHerramientaXProyecto}")
    TableroKanban seleccionarTableroKanbanPorProductBacklog(@PathVariable int idProductBacklog){
        return tableroKanbanService.seleccionarTableroKanbanPorProductBacklog(idProductBacklog);
    }
}
