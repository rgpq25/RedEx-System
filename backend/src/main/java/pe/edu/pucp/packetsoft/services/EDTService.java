package pe.edu.pucp.packetsoft.services;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.CronogramaRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.EDTRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.HerramientaXProyectoRepository;
import pe.edu.pucp.packetsoft.repository.PersonasRepository.UsuarioRepository;
import pe.edu.pucp.packetsoft.models.Cronograma;
import pe.edu.pucp.packetsoft.models.EDT;
import pe.edu.pucp.packetsoft.models.HerramientaXProyecto;
import pe.edu.pucp.packetsoft.models.PersonasModel.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class EDTService {
    @Autowired
    private EDTRepository edtRepository;

    @Autowired
    private HerramientaXProyectoRepository herramientaXProyectoRepository;

    @Autowired
    private CronogramaRepository cronogramaRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(EDTService.class);
    @PersistenceContext
    EntityManager entityManager;

    
    public EDT register(EDT edt){
        try {
            return edtRepository.save(edt);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return edtRepository.save(edt);
    }

    
    public List<EDT> getAll(){
        try {
            return edtRepository.findAll();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return edtRepository.findAll();
    }

    
    public EDT get(int id){
        try {
            return edtRepository.findEDTById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return edtRepository.findEDTById(id);
    }

    
    public EDT update(EDT edt){
        try {
            return edtRepository.save(edt);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return edtRepository.save(edt);
    }

   
    public void delete(int id){
        try {
            edtRepository.deleteById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        // edtRepository.deleteById(id);
    }
    
    public EDT duplicadoPropio(int id){
        EDT edt = null;
        try {
            return edtRepository.duplicadoPropio(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public EDT seleccionarEDTPorHerramientaXProyecto(int idHerramientaXProyecto){
        try {
            HerramientaXProyecto herramientaXProyecto = new HerramientaXProyecto();
            herramientaXProyecto = herramientaXProyectoRepository.findHerramientaXProyectoById(idHerramientaXProyecto);
            return edtRepository.findEDTByHerramientaXProyecto(herramientaXProyecto);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public EDT seleccionarEDTPorCronograma(int idCronograma){
        try {
            Cronograma cronograma = new Cronograma();
            cronograma = cronogramaRepository.findCronogramaById(idCronograma);
            return edtRepository.findEDTByCronograma(cronograma);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }
    
}
