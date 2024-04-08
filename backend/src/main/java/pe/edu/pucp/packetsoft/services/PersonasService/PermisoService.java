package pe.edu.pucp.packetsoft.services.PersonasService;

import pe.edu.pucp.packetsoft.models.PersonasModel.Permiso;
import pe.edu.pucp.packetsoft.models.PersonasModel.Persona;
import pe.edu.pucp.packetsoft.models.PersonasModel.TipoUsuario;
import pe.edu.pucp.packetsoft.repository.PersonasRepository.PermisoRepository;
import pe.edu.pucp.packetsoft.repository.PersonasRepository.PersonaRepository;
import pe.edu.pucp.packetsoft.repository.PersonasRepository.TipoUsuarioRepository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class PermisoService {
    @Autowired 
    PermisoRepository permisoRepository;

    @Autowired
    PersonaRepository personaRepository;

    @Autowired
    TipoUsuarioRepository tipoUsuarioRepository;

    @PersistenceContext
    EntityManager entityManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(PermisoService.class);
    //CRUDS
    //create
    public Permiso register(Permiso permiso){ // no verifico que exista repetido pq existen muchas combinaciones de permisos
        try {
            if(permiso == null){ // vacio
                return null;
            }else{
                return permisoRepository.save(permiso);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    //read
    public List<Permiso> getAll(){
        try {
            return permisoRepository.findAll();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return permisoRepository.findAll();
    }
    
    public Permiso get(int id){
        try {
            return permisoRepository.findPermisoById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return permisoRepository.findPermisoById(id);
    }

    //delete
    public void delete(int id){
        try {
            permisoRepository.deleteById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        // permisoRepository.deleteById(id);
    }

    //update
    public Permiso update(Permiso permiso){
        try {
            return permisoRepository.save(permiso);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return permisoRepository.save(permiso);
    }
    

}
