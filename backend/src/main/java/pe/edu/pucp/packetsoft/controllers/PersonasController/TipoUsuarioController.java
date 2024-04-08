package pe.edu.pucp.packetsoft.controllers.PersonasController;

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

import pe.edu.pucp.packetsoft.models.PersonasModel.TipoUsuario;
import pe.edu.pucp.packetsoft.services.PersonasService.TipoUsuarioService;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/tipousuario")
@CrossOrigin
public class TipoUsuarioController {
    @Autowired
    private TipoUsuarioService tipoUsuarioService;

    //Trae todas los tipos de usuario
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping("/")
    List<TipoUsuario> getAll(){
        return tipoUsuarioService.getAll();
    }

    //Trae a un tipo de usuario
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/{id}")
    TipoUsuario get(@PathVariable int id){
        return tipoUsuarioService.get(id);
    }

    //Registra un tipo de usuario
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PostMapping(value = "/")
    TipoUsuario register(@RequestBody TipoUsuario tipoUser) throws SQLException{
        return tipoUsuarioService.register(tipoUser);
    }

    //Actualiza un tipo de usuario
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PutMapping(value ="/update")
    public TipoUsuario update(@RequestBody TipoUsuario tipoUser){
        return tipoUsuarioService.update(tipoUser);
    }

    //Borra a un tipo de usuario
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @DeleteMapping(value = "/{id}")
    void delete(@PathVariable int id){
        tipoUsuarioService.delete(id);
    }
    
}