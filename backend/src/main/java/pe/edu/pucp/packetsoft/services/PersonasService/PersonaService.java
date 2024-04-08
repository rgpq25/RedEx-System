package pe.edu.pucp.packetsoft.services.PersonasService;

import pe.edu.pucp.packetsoft.models.PersonasModel.Persona;
import pe.edu.pucp.packetsoft.repository.PersonasRepository.PersonaRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class PersonaService {
    @Autowired
    private PersonaRepository personaRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonaService.class);
    //CRUDS
    //create
    public Persona register(Persona persona){
        try {
            if(persona.getNombres() == null || persona.getNombres().isEmpty()){
                throw new RuntimeException("La persona no puede estar vacía");
            }
            Persona foundPersona = null;
            foundPersona = personaRepository.findPersonaByCorreo(persona.getCorreo());

            if(foundPersona != null){ // se encuentra duplicado
                return null;
            }else{
                return personaRepository.save(persona);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }
    
    //read
    public List<Persona> getAll(){
        try {
            return personaRepository.findAll();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return personaRepository.findAll();
    }

    
    public Persona get(int id){
        try {
            return personaRepository.findPersonaById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return personaRepository.findPersonaById(id);
    }

    //delete
    public void delete(int id){
        try {
            personaRepository.deleteById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        // personaRepository.deleteById(id);
    }

    //update
    public Persona update(Persona persona){
        try {
            return personaRepository.save(persona);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return personaRepository.save(persona);
    }

    public Persona encontrarPersonaPorCorreo(String correo){
        try {
            return personaRepository.findPersonaByCorreo(correo);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public List<Persona> listarPersonasPorCorreoParcial(String correo){
        try{
            return personaRepository.encontrarPersonasPorCorreoParcial(correo);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }

}
