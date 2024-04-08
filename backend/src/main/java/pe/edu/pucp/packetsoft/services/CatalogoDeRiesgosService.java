package pe.edu.pucp.packetsoft.services;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.CatalogoDeRiesgosRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.CronogramaRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.EDTRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.HerramientaXProyectoRepository;
import pe.edu.pucp.packetsoft.repository.PersonasRepository.UsuarioRepository;
import pe.edu.pucp.packetsoft.models.CatalogoDeRiesgos;
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
public class CatalogoDeRiesgosService {
    @Autowired
    private CatalogoDeRiesgosRepository catalogoDeRiesgosRepository;

    @Autowired
    private HerramientaXProyectoRepository herramientaXProyectoRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(CatalogoDeRiesgosService.class);

    @PersistenceContext
    EntityManager entityManager;

    
    public CatalogoDeRiesgos register(CatalogoDeRiesgos catalogoDeRiesgos){
        try {
            return catalogoDeRiesgosRepository.save(catalogoDeRiesgos);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    
    public List<CatalogoDeRiesgos> getAll(){
        try {
            return catalogoDeRiesgosRepository.findAll();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

   
    public CatalogoDeRiesgos get(int id){
        try {
            return catalogoDeRiesgosRepository.findCatalogoDeRiesgosById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    
    public CatalogoDeRiesgos update(CatalogoDeRiesgos catalogoDeRiesgos){
        try {
            return catalogoDeRiesgosRepository.save(catalogoDeRiesgos);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    
    public void delete(int id){
        try {
            catalogoDeRiesgosRepository.deleteById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public CatalogoDeRiesgos duplicadoPropio(int id){
        try {
            return catalogoDeRiesgosRepository.duplicadoPropio(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public CatalogoDeRiesgos seleccionarCatalogoDeRiesgosPorHerramientaXProyecto(int  idHerramientaXProyecto){
        try {
            HerramientaXProyecto herramientaXProyecto = new HerramientaXProyecto();
            herramientaXProyecto = herramientaXProyectoRepository.findHerramientaXProyectoById(idHerramientaXProyecto);
            return catalogoDeRiesgosRepository.findCatalogoDeRiesgosByHerramientaXProyecto(herramientaXProyecto);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

}
