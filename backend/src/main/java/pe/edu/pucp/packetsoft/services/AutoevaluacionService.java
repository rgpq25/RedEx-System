package pe.edu.pucp.packetsoft.services;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import pe.edu.pucp.packetsoft.repository.ProyectosRepository.AutoevaluacionRepository;
import pe.edu.pucp.packetsoft.repository.ProyectosRepository.ProyectoRepository;
import pe.edu.pucp.packetsoft.models.Autoevaluacion;
import pe.edu.pucp.packetsoft.models.Proyecto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service 
public class AutoevaluacionService {
    @Autowired
    private AutoevaluacionRepository autoevaluacionRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(CronogramaService.class);
    @PersistenceContext
    EntityManager entityManager;

    public Autoevaluacion register(Autoevaluacion autoevaluacion){
        try {
            return autoevaluacionRepository.save(autoevaluacion);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }

    }

    public List<Autoevaluacion> getAll(){
        try {
            return autoevaluacionRepository.findAll();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }

    }

    public Autoevaluacion get(int id){
        try {
            return autoevaluacionRepository.findAutoevaluacionById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public Autoevaluacion update(Autoevaluacion autoevaluacion){
        try {
            return autoevaluacionRepository.save(autoevaluacion);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public void delete(int id){
        try {
            autoevaluacionRepository.deleteById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public Autoevaluacion seleccionarAutoevaluacionPorProyecto(int id){
        try {
            Proyecto proyecto = new Proyecto();
            proyecto = proyectoRepository.findProyectoById(id);
            return autoevaluacionRepository.findAutoevaluacionByProyecto(proyecto);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }
}
