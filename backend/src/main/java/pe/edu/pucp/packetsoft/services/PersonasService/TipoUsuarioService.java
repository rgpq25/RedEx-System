package pe.edu.pucp.packetsoft.services.PersonasService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import pe.edu.pucp.packetsoft.controllers.PersonasController.TipoUsuarioController;
import pe.edu.pucp.packetsoft.models.PersonasModel.TipoUsuario;
import pe.edu.pucp.packetsoft.repository.PersonasRepository.TipoUsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class TipoUsuarioService {
    @Autowired 
    private TipoUsuarioRepository tipoUsuarioRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(TipoUsuarioService.class);
    public TipoUsuarioService(TipoUsuarioRepository tipoUsuarioRepository) {
        this.tipoUsuarioRepository = tipoUsuarioRepository;
    }

    //CRUDS
    //create
    public TipoUsuario register(TipoUsuario tipoUser){
        try {
            if(tipoUser.getNombre() == null || tipoUser.getNombre().isEmpty()){
                throw new RuntimeException("El tipo usuario no puede estar vacío");
            }
            TipoUsuario found = null;
            found = tipoUsuarioRepository.findTipoUsuarioByNombre(tipoUser.getNombre());

            if(found != null){ // se encuentra duplicado
                return null;
            }else{
                return tipoUsuarioRepository.save(tipoUser);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }
    
    //read
    public List<TipoUsuario> getAll(){
        try {
            return tipoUsuarioRepository.findAll();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }
    public TipoUsuario get(int id){
        try {
            return tipoUsuarioRepository.findTipoUsuarioById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return tipoUsuarioRepository.findTipoUsuarioById(id);
    }

    //delete
    public void delete(int id){
        try {
            tipoUsuarioRepository.deleteById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        // tipoUsuarioRepository.deleteById(id);
    }

    //update
    public TipoUsuario update(TipoUsuario tipoUser){
        try {
            return tipoUsuarioRepository.save(tipoUser);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return tipoUsuarioRepository.save(tipoUser);
    }
}
