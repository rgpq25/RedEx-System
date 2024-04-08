package pe.edu.pucp.packetsoft.controllers;
import java.sql.SQLException;
import java.util.List;

import pe.edu.pucp.packetsoft.models.ActaDeConstitucion;
import pe.edu.pucp.packetsoft.models.GrupoDeProyectos;
import pe.edu.pucp.packetsoft.models.PersonasModel.Usuario;
import pe.edu.pucp.packetsoft.services.ActaDeConstitucionService;
import pe.edu.pucp.packetsoft.services.GrupoDeProyectosService;
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
@RequestMapping("/grupoDeProyectos")
@CrossOrigin
public class GrupoDeProyectosController {
    @Autowired
    private GrupoDeProyectosService grupoDeProyectosService;
    
    //Trae todos los grupoDeProyectos
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/")
    List<GrupoDeProyectos> getAll(){
        return grupoDeProyectosService.getAll();
    }

    //Trae a un grupoDeProyectos
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/{id}")
    GrupoDeProyectos get(@PathVariable int id){
        return grupoDeProyectosService.get(id);
    }

    //Registra un grupoDeProyectos
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PostMapping(value = "/")
    GrupoDeProyectos register(@RequestBody GrupoDeProyectos grupoDeProyectos) throws SQLException{
        GrupoDeProyectos resultado = grupoDeProyectosService.register(grupoDeProyectos);
        if(resultado == null){
            throw new SQLException();
        }
        return grupoDeProyectosService.register(grupoDeProyectos);
    }

    //Actualiza un grupoDeProyectos
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PutMapping(value = "/")
    GrupoDeProyectos update(@RequestBody GrupoDeProyectos grupoDeProyectos) throws SQLException{
        return grupoDeProyectosService.update(grupoDeProyectos);
    }

    //Elimina un grupoDeProyectos
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @DeleteMapping(value = "/{id}")
    void delete(@PathVariable int id){
        grupoDeProyectosService.delete(id);
    }

    //Trae todos los grupoDeProyectos
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/listarGrupoDeProyectosPorJefeDeProyecto/{idJefeDeProyecto}")
    List<GrupoDeProyectos> listarGrupoDeProyectosPorJefeDeProyecto(@PathVariable int idJefeDeProyecto){
        return grupoDeProyectosService.listarGruposDeProyectosPorPersona(idJefeDeProyecto);
    }
}
