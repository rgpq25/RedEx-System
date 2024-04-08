package pe.edu.pucp.packetsoft.controllers;
import java.sql.SQLException;
import java.util.List;

import pe.edu.pucp.packetsoft.models.IGV;
import pe.edu.pucp.packetsoft.services.IGVService;

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
@RequestMapping("/igv")
@CrossOrigin
public class IGVController {
    
    @Autowired
    private IGVService igvService;

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/")
    List<IGV> getAll(){
        return igvService.getAll();
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/{id}")
    IGV get(@PathVariable int id){
        return igvService.get(id);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PostMapping(value = "/")
    IGV register(@RequestBody IGV igv) throws SQLException{
        if(igvService.duplicadoPropio(igv.getId())!=null){
            System.out.println("ERROR: Duplicado de igv");
            throw new SQLException();
        }
        return igvService.register(igv);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PutMapping(value="/")
    IGV update(@RequestBody IGV igv){
        return igvService.update(igv);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @DeleteMapping(value="/{id}")
    void delete(@PathVariable int id){
        igvService.delete(id);
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Duplicated data")
    public void conflict() {}

}
