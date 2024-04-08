package pe.edu.pucp.packetsoft.services;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import pe.edu.pucp.packetsoft.models.ActaDeConstitucion;
import pe.edu.pucp.packetsoft.models.HerramientaXProyecto;
import pe.edu.pucp.packetsoft.models.PersonasModel.Usuario;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.ActaDeConstitucionRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.HerramientaXProyectoRepository;

import org.hibernate.annotations.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class ActaDeConstitucionService {
    @Autowired 
    private ActaDeConstitucionRepository actaDeConstitucionRepository;

    @Autowired
    private HerramientaXProyectoRepository herramientaXProyectoRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ActaDeConstitucionService.class);
    @PersistenceContext
    EntityManager entityManager;

    
    public ActaDeConstitucion register(ActaDeConstitucion actaDeConstitucion){
        try {
            return actaDeConstitucionRepository.save(actaDeConstitucion);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return actaDeConstitucionRepository.save(actaDeConstitucion);
    }

    
    public List<ActaDeConstitucion> getAll(){
        try {
            return actaDeConstitucionRepository.findAll();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return actaDeConstitucionRepository.findAll();
    }

    
    public ActaDeConstitucion get(int id){
        try {
            return actaDeConstitucionRepository.findActaDeConstitucionById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return actaDeConstitucionRepository.findActaDeConstitucionById(id);
    }

    
    public ActaDeConstitucion update(ActaDeConstitucion actaDeConstitucion){
        try {
            return actaDeConstitucionRepository.save(actaDeConstitucion);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return actaDeConstitucionRepository.save(actaDeConstitucion);
    }

    
    public void delete(int id){
        try {
            actaDeConstitucionRepository.deleteById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        // actaDeConstitucionRepository.deleteById(id);
    }

    public ActaDeConstitucion duplicadoPropio(int id){
        try {
            return actaDeConstitucionRepository.duplicadoPropio(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public ActaDeConstitucion seleccionarActaDeConstitucionPorHerramientaXProyecto(int idHerramientaXProyecto){
        try {
            HerramientaXProyecto herramientaXProyecto = new HerramientaXProyecto();
            herramientaXProyecto = herramientaXProyectoRepository.findHerramientaXProyectoById(idHerramientaXProyecto);
            return actaDeConstitucionRepository.findActaDeConstitucionByHerramientaXProyecto(herramientaXProyecto);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }
}
