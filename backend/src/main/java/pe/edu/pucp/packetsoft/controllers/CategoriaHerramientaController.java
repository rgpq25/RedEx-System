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
import pe.edu.pucp.packetsoft.models.CategoriaHerramienta;
import pe.edu.pucp.packetsoft.services.CategoriaHerramientaService;

@RestController
@RequestMapping("/categoriaHerramienta")
@CrossOrigin

public class CategoriaHerramientaController {
    
    @Autowired
    private CategoriaHerramientaService categoriaHerramientaService;

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/")
    List<CategoriaHerramienta> getAll(){
        return categoriaHerramientaService.getAll();
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/{id}")
    CategoriaHerramienta get(@PathVariable int id){
        return categoriaHerramientaService.get(id);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PostMapping(value = "/")
    CategoriaHerramienta register(@RequestBody CategoriaHerramienta categoriaHerramienta) throws SQLException{
        CategoriaHerramienta resultado = categoriaHerramientaService.register(categoriaHerramienta);
        if(resultado == null){
            throw new SQLException();
        }
        return resultado;
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PutMapping(value = "/")
    CategoriaHerramienta update(@RequestBody CategoriaHerramienta categoriaHerramienta) throws SQLException{
        return categoriaHerramientaService.update(categoriaHerramienta);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @DeleteMapping(value = "/{id}")
    void delete(@PathVariable int id){
        categoriaHerramientaService.delete(id);
    }
}
