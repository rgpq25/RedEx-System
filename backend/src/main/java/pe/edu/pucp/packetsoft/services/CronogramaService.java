package pe.edu.pucp.packetsoft.services;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.CronogramaRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.HerramientaXProyectoRepository;
import pe.edu.pucp.packetsoft.repository.PersonasRepository.UsuarioRepository;
import pe.edu.pucp.packetsoft.models.Cronograma;
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
public class CronogramaService {
    @Autowired 
    private CronogramaRepository cronogramaRepository;

    @Autowired
    private HerramientaXProyectoRepository herramientaXProyectoRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(CronogramaService.class);
    @PersistenceContext
    EntityManager entityManager;

    
    public Cronograma register(Cronograma cronograma){
        try {
            return cronogramaRepository.save(cronograma);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return cronogramaRepository.save(cronograma);
    }

    
    public List<Cronograma> getAll(){
        try {
            return cronogramaRepository.findAll();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return cronogramaRepository.findAll();
    }

   
    public Cronograma get(int id){
        try {
            return cronogramaRepository.findCronogramaById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return cronogramaRepository.findCronogramaById(id);
    }

    
    public Cronograma update(Cronograma cronograma){
        try {
            return cronogramaRepository.save(cronograma);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return cronogramaRepository.save(cronograma);
    }

    
    public void delete(int id){
        try {
            cronogramaRepository.deleteById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        // cronogramaRepository.deleteById(id);
    }

    public Cronograma duplicadoPropio(int id){
        Cronograma cronograma = null;
        try {
            return cronogramaRepository.duplicadoPropio(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public Cronograma seleccionarCronogramaPorHerramientaXProyecto(int idHerramientaXProyecto){
        try {
            HerramientaXProyecto herramientaXProyecto = new HerramientaXProyecto();
            herramientaXProyecto = herramientaXProyectoRepository.findHerramientaXProyectoById(idHerramientaXProyecto);
            return cronogramaRepository.findCronogramaByHerramientaXProyecto(herramientaXProyecto);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }
    
}
