package pe.edu.pucp.packetsoft.services.PersonasService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.edu.pucp.packetsoft.models.PersonasModel.Solicitud;
import pe.edu.pucp.packetsoft.repository.PersonasRepository.SolicitudRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SolicitudService {
    @Autowired
    private SolicitudRepository solicitudRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudService.class);

    //CRUDS
    //create
    public Solicitud register(Solicitud solicitud){
        try {
           return solicitudRepository.save(solicitud);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    //read
    public List<Solicitud> getAll(){
        try {
            return solicitudRepository.findAll();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return solicitudRepository.findAll();
    }

    public Solicitud get(int id){
        try {
            return solicitudRepository.findSolicitudById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return solicitudRepository.findSolicitudById(id);
    }

    //delete
    public void delete(int id){
        try {
            solicitudRepository.deleteById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //update
    public Solicitud update(Solicitud solicitud){
        try {
            return solicitudRepository.save(solicitud);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }
    
}
