package pe.edu.pucp.packetsoft.controllers;
import java.sql.SQLException;
import java.util.List;

import pe.edu.pucp.packetsoft.models.EDT;
import pe.edu.pucp.packetsoft.models.ProductBacklog;
import pe.edu.pucp.packetsoft.services.EDTService;
import pe.edu.pucp.packetsoft.services.ProductBacklogService;

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
@RequestMapping("/productBacklog")
@CrossOrigin
public class ProductBacklogController {
    @Autowired
    private ProductBacklogService productBacklogService;

    //Trae todos los productBacklog
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/")
    List<ProductBacklog> getAll(){

        return productBacklogService.getAll();
    }

    //Trae a un productBacklog
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/{id}")
    ProductBacklog get(@PathVariable int id){
        return productBacklogService.get(id);
    }

    //Registra un productBacklog
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PostMapping(value = "/")
    ProductBacklog register(@RequestBody ProductBacklog productBacklog) throws SQLException{
        
        if(productBacklogService.duplicadoPropio(productBacklog.getId())!=null){
            throw new SQLException();
        }
        return productBacklogService.register(productBacklog);
    }

    //Actualiza un productBacklog
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PutMapping(value = "/")
    ProductBacklog update(@RequestBody ProductBacklog productBacklog){
        return productBacklogService.update(productBacklog);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @DeleteMapping(value = "/{id}")
    void delete(@PathVariable int id){
        productBacklogService.delete(id);
    }

    //Excepcion para data duplicada
    @ExceptionHandler(SQLException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Duplicated data")
    public void conflict() {}

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/seleccionarProductBacklogPorHerramientaXProyecto/{idHerramientaXProyecto}")
    ProductBacklog seleccionarProductBacklogPorHerramientaXProyecto(@PathVariable int idHerramientaXProyecto){
        return productBacklogService.seleccionarProductBacklogPorHerramientaXProyecto(idHerramientaXProyecto);
    }
}
