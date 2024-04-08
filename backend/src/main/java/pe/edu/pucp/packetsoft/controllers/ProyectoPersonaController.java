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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import pe.edu.pucp.packetsoft.models.Proyecto;
import pe.edu.pucp.packetsoft.models.ProyectoPersona;
import pe.edu.pucp.packetsoft.models.PersonasModel.Persona;
import pe.edu.pucp.packetsoft.services.EmailService;
import pe.edu.pucp.packetsoft.services.ProyectoPersonaService;
import pe.edu.pucp.packetsoft.services.ProyectoService;
import pe.edu.pucp.packetsoft.services.PersonasService.PersonaService;

@RestController
@RequestMapping("/proyectoPersona")
@CrossOrigin
public class ProyectoPersonaController {
    @Autowired
    private ProyectoPersonaService proyectoPersonaService;

     @Autowired
    private EmailService emailService;

    @Autowired
    private PersonaService personaService;

    @Autowired
    private ProyectoService proyectoService;

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/")
    List<ProyectoPersona> getAll(){
        return proyectoPersonaService.getAll();
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/{id}")
    ProyectoPersona get(@PathVariable int id){
        return proyectoPersonaService.get(id);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PostMapping(value = "/")
    ProyectoPersona register(@RequestBody ProyectoPersona proyectoPersona) throws SQLException{
        ProyectoPersona resultado = proyectoPersonaService.register(proyectoPersona);
        if(resultado == null){
            throw new SQLException();
        }
        //quiero invocar al servicio de obtener persona por id 
        //y obtener el correo de la persona
        //y enviar el correo de confirmación
        Proyecto proyecto= proyectoService.get(proyectoPersona.getProyecto().getId());
        Persona persona =personaService.get(proyectoPersona.getPersona().getId());
        if(persona != null){
            enviarCorreoConfirmacion(persona.getCorreo(), proyecto.getNombre());
        }
        
        return resultado;
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PutMapping(value = "/")
    ProyectoPersona update(@RequestBody ProyectoPersona proyectoPersona) throws SQLException{
        return proyectoPersonaService.update(proyectoPersona);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @DeleteMapping(value = "/{id}")
    void delete(@PathVariable int id){
        proyectoPersonaService.delete(id);
    }

    void enviarCorreoConfirmacion(String correoDestino, String nombreProyecto) {
        // Define el contenido del correo de confirmación
        String asunto = "Confirmación de agregación a proyecto";
        String mensaje = "¡Hola!\nHas sido agregado al proyecto '" + nombreProyecto + "'.";

        // Envía el correo
        emailService.sendEmailTool( asunto,correoDestino, mensaje);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/buscarPorProyecto/{idProyecto}")
    List<ProyectoPersona> buscarProyectoPersonaPorProyecto(@PathVariable int idProyecto){
        return proyectoPersonaService.findProyectoPersonaByProyecto(idProyecto);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/buscarPorPersona/{idPersona}")
    List<ProyectoPersona> buscarProyectoPersonaPorPersona(@PathVariable int idPersona){
        return proyectoPersonaService.findProyectoPersonaByPersona(idPersona);
    }

    //encontrarParticipantesProyectoPersonaByProyecto
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/buscarParticipantesPorProyecto/{idProyecto}")
    List<ProyectoPersona> buscarParticipantesProyectoPersonaPorProyecto(@PathVariable int idProyecto){
        return proyectoPersonaService.encontrarParticipantesProyectoPersonaByProyecto(idProyecto);
    }

}
