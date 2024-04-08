package pe.edu.pucp.packetsoft.controllers.PersonasController;

import java.sql.SQLException;
import java.util.List;

import pe.edu.pucp.packetsoft.models.PersonasModel.Usuario;
import pe.edu.pucp.packetsoft.services.PersonasService.UsuarioService;

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
@RequestMapping("/usuario")
@CrossOrigin
public class UsuarioController {

    @Autowired
    private UsuarioService userService;

    //Trae todos los usuarios
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/")
    List<Usuario> getAll(){
        return userService.getAll();
    }

    //Trae a un usuario
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/{id}")
    Usuario get(@PathVariable int id){
        return userService.get(id);
    }

    //Registra un usuario
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PostMapping(value = "/")
    Usuario register(@RequestBody Usuario user) throws SQLException{
        
        if(userService.duplicadoPropio(user.getUsuario(), -1)!=null){
            throw new SQLException();
        }
        user.setPassword(generarHash(user.getPassword()));
        return userService.register(user);
    }

    //Actualiza un usuario
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PutMapping(value = "/")
    Usuario update(@RequestBody Usuario user) throws SQLException{
        if(userService.duplicadoPropio(user.getUsuario(), user.getId())==null)
            throw new SQLException();
        
        return userService.update(user);
    }

    //Actualizar el password de un usuario
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PutMapping(value = "/newpassword={id_usuario}")
    boolean updatePassword(@PathVariable int id_usuario, @RequestBody List<String> data){
        return userService.updatePassword(id_usuario, data.get(0), data.get(1), data.get(2));
    }
    
    //Eliminar un usuario con id dado
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @DeleteMapping(value = "/{id}")
    void delete(@PathVariable int id){
        userService.delete(id);
    }

    private String generarHash(String password){
        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        return argon2.hash(1, 1024*1, 1, password);
    }

    //Excepcion para data duplicada
    @ExceptionHandler(SQLException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Duplicated data")
    public void conflict() {}

}