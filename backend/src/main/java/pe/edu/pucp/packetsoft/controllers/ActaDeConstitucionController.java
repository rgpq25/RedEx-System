package pe.edu.pucp.packetsoft.controllers;

import java.sql.SQLException;
import java.util.List;

import pe.edu.pucp.packetsoft.models.ActaDeConstitucion;
import pe.edu.pucp.packetsoft.models.PersonasModel.Usuario;
import pe.edu.pucp.packetsoft.services.ActaDeConstitucionService;
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
@RequestMapping("/actaDeConstitucion")
@CrossOrigin
public class ActaDeConstitucionController {
    @Autowired
    private ActaDeConstitucionService actaDeConstitucionService;

    //Trae todos los actaDeConstitucion
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/")
    List<ActaDeConstitucion> getAll(){
        /*List<Usuario> usuarios =new ArrayList<>();
        usuarios.add(new Usuario(null, null, null, 0));
        return usuarios;*/
        return actaDeConstitucionService.getAll();
    }

    //Trae a un actaDeConstitucion
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/{id}")
    ActaDeConstitucion get(@PathVariable int id){
        return actaDeConstitucionService.get(id);
    }

    //Registra un actaDeConstitucion
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PostMapping(value = "/")
    ActaDeConstitucion register(@RequestBody ActaDeConstitucion actaDeConstitucion) throws SQLException{
         
        if(actaDeConstitucionService.duplicadoPropio(actaDeConstitucion.getId())!=null){
            throw new SQLException();
        }
     
        return actaDeConstitucionService.register(actaDeConstitucion);
    }

    //Actualiza un actaDeConstitucion
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PutMapping(value = "/")
    ActaDeConstitucion update(@RequestBody ActaDeConstitucion actaDeConstitucion) throws SQLException{
        return actaDeConstitucionService.update(actaDeConstitucion);
    }
    
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @DeleteMapping(value = "/{id}")
    void delete(@PathVariable int id){
        actaDeConstitucionService.delete(id);
    }

    //Excepcion para data duplicada
    @ExceptionHandler(SQLException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Duplicated data")
    public void conflict() {}

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/seleccionarActaDeConstitucionPorHerramientaXProyecto/{idHerramientaXProyecto}")
    ActaDeConstitucion seleccionarActaDeConstitucionPorHerramientaXProyecto(@PathVariable int idHerramientaXProyecto){
        return actaDeConstitucionService.seleccionarActaDeConstitucionPorHerramientaXProyecto(idHerramientaXProyecto);
    }
}
