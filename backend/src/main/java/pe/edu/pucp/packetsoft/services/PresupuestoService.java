package pe.edu.pucp.packetsoft.services;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.PresupuestoRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.HerramientaXProyectoRepository;
import pe.edu.pucp.packetsoft.models.Presupuesto;
import pe.edu.pucp.packetsoft.models.HerramientaXProyecto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PresupuestoService {
    @Autowired
    private PresupuestoRepository presupuestoRepository;

    @Autowired
    private HerramientaXProyectoRepository herramientaXProyectoRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(PresupuestoService.class);

    @PersistenceContext
    EntityManager entityManager;

    @CacheEvict(value = "presupuesto", allEntries = true,beforeInvocation = true)
    @CachePut(value = "presupuesto", key = "#result.id", condition = "#result != null")
    public Presupuesto register(Presupuesto presupuesto){
        try {
            return presupuestoRepository.save(presupuesto);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    @Cacheable(value = "presupuesto")
    public List<Presupuesto> getAll(){
        try {
            return presupuestoRepository.findAll();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    @Cacheable(value = "presupuesto", key = "#id")
    public Presupuesto get(int id){
        try {
            return presupuestoRepository.findPresupuestoById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    @CacheEvict(value = "presupuesto", allEntries = true,beforeInvocation = true)
    @CachePut(value = "presupuesto", key = "#result.id", condition = "#result != null")
    public Presupuesto update(Presupuesto presupuesto){
        try {
            return presupuestoRepository.save(presupuesto);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    @CacheEvict(value = "presupuesto", allEntries = true,beforeInvocation = true)
    public void delete(int id){
        try {
            presupuestoRepository.deleteById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public Presupuesto duplicadoPropio(int id){
        try {
            return presupuestoRepository.findPresupuestoById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }
    //comentario para que no se malogre
    public Presupuesto seleccionarPresupuestoPorHerramientaXProyecto(int idHerramientaXProyecto){
        try{
            HerramientaXProyecto herramientaXProyecto = new HerramientaXProyecto();
            herramientaXProyecto = herramientaXProyectoRepository.findHerramientaXProyectoById(idHerramientaXProyecto);
            return presupuestoRepository.findPresupuestoByHerramientaXProyecto(herramientaXProyecto);
        } catch (Exception ex){
            LOGGER.error(ex.getMessage());
            return null;
        }
    }

}
