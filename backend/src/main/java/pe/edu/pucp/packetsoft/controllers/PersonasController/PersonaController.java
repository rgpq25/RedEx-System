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

import pe.edu.pucp.packetsoft.models.PersonasModel.Persona;
import pe.edu.pucp.packetsoft.services.PersonasService.PersonaService;

@RestController
@RequestMapping("/persona")
@CrossOrigin
public class PersonaController {

    @Autowired
    private PersonaService personaService;

    //Trae todas las personas
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/")
    List<Persona> getAll(){
        return personaService.getAll();
    }
    //Trae a una persona
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/{id}")
    Persona get(@PathVariable int id){
        return personaService.get(id);
    }

    //Registra una persona
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PostMapping(value = "/")
    Persona register(@RequestBody Persona per) throws SQLException{
        String patron = "[^a-zA-ZñÑáéíóúÁÉÍÓÚ\\s]";
        String nombre = per.getNombres();
        String primer_apellido= per.getPrimerApellido();
        String segundo_apellido= per.getSegundoApellido();
        
        String nombre_validado= nombre.replaceAll(patron, "");// Reemplazar caracteres no deseados por una cadena vacía
        String primer_apellido_validado= primer_apellido.replaceAll(patron, "");// Reemplazar caracteres no deseados por una cadena vacía
        String segundo_apellido_validado= segundo_apellido.replaceAll(patron, "");// Reemplazar caracteres no deseados por una cadena vacía
        per.setNombres(nombre_validado);
        per.setPrimerApellido(primer_apellido_validado);
        per.setSegundoApellido(segundo_apellido_validado);
    
        return personaService.register(per);
    }

    //Actualiza una persona
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PutMapping(value ="/update")
    public Persona update(@RequestBody Persona persona){
        String patron = "[^a-zA-ZñÑáéíóúÁÉÍÓÚ\\s]";
        String nombre = persona.getNombres();
        String primer_apellido= persona.getPrimerApellido();
        String segundo_apellido= persona.getSegundoApellido();
        
        String nombre_validado= nombre.replaceAll(patron, "");// Reemplazar caracteres no deseados por una cadena vacía
        String primer_apellido_validado= primer_apellido.replaceAll(patron, "");// Reemplazar caracteres no deseados por una cadena vacía
        String segundo_apellido_validado= segundo_apellido.replaceAll(patron, "");// Reemplazar caracteres no deseados por una cadena vacía
        persona.setNombres(nombre_validado);
        persona.setPrimerApellido(primer_apellido_validado);
        persona.setSegundoApellido(segundo_apellido_validado);
        return personaService.update(persona);
    }

    //Borra a una persona
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @DeleteMapping(value = "/{id}")
    void delete(@PathVariable int id){
        personaService.delete(id);
    }
    
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value="/encontrarPersonaPorCorreo/{correo}")
    Persona encontrarPersonaPorCorreo(@PathVariable String correo){
        return personaService.encontrarPersonaPorCorreo(correo);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value="/listarPersonasPorCorreoParcial/{correo}")
    List<Persona> listarPersonasPorCorreoParcial(@PathVariable String correo){
        return personaService.listarPersonasPorCorreoParcial(correo);
    }
}
