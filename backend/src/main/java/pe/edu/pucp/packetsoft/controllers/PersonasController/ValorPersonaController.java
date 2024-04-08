package pe.edu.pucp.packetsoft.controllers.PersonasController;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.edu.pucp.packetsoft.models.PersonasModel.ValorPersona;
import pe.edu.pucp.packetsoft.services.PersonasService.ValorPersonaService;

@RestController
@RequestMapping("/valorPersona")
@CrossOrigin
public class ValorPersonaController {
    @Autowired
    private ValorPersonaService valorPersonaService;

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/")
    List<ValorPersona> getAll(){
        return valorPersonaService.getAll();
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/{id}")
    ValorPersona get(@PathVariable int id){
        return valorPersonaService.get(id);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PostMapping(value = "/")
    ValorPersona register(@RequestBody ValorPersona reg) throws SQLException{
            
        ValorPersona resultado = valorPersonaService.register(reg);
        if(resultado == null){
            throw new SQLException();
        }
        return resultado;  
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PutMapping(value = "/")
    ValorPersona update(@RequestBody ValorPersona data)throws SQLException{
        return valorPersonaService.update(data);
    }
    
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @DeleteMapping(value = "/{id}")
    void delete(@PathVariable int id){
        valorPersonaService.delete(id);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/listarValorPersonaPorValor/{idValor}")
    List<ValorPersona> listarValorPersonaPorValor(@PathVariable int idValor){
        return valorPersonaService.listarValorPersonaPorValor(idValor);
    }
}
