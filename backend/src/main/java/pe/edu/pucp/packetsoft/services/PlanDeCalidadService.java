package pe.edu.pucp.packetsoft.services;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.HerramientaXProyectoRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.PlanDeCalidadRepository;
import pe.edu.pucp.packetsoft.repository.PersonasRepository.UsuarioRepository;
import pe.edu.pucp.packetsoft.models.HerramientaXProyecto;
import pe.edu.pucp.packetsoft.models.PlanDeCalidad;
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
public class PlanDeCalidadService {
    @Autowired
    private PlanDeCalidadRepository planDeCalidadRepository;

    @Autowired
    private HerramientaXProyectoRepository herramientaXProyectoRepository;

    private static final Logger logger = LoggerFactory.getLogger(PlanDeCalidadService.class);
    @PersistenceContext
    EntityManager entityManager;


    
    public PlanDeCalidad register(PlanDeCalidad planDeCalidad){
        try {
            return planDeCalidadRepository.save(planDeCalidad);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        //return planDeCalidadRepository.save(planDeCalidad);
    }

    
    public List<PlanDeCalidad> getAll(){
        try {
            return planDeCalidadRepository.findAll();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        //return planDeCalidadRepository.findAll();
    }

    
    public PlanDeCalidad get(int id){
        try {
            return planDeCalidadRepository.findPlanDeCalidadById(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        //return planDeCalidadRepository.findPlanDeCalidadById(id);
    }

    
    public PlanDeCalidad update(PlanDeCalidad planDeCalidad){
        try {
            return planDeCalidadRepository.save(planDeCalidad);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        //return planDeCalidadRepository.save(planDeCalidad);
    }

    
    public void delete(int id){
        try {
            planDeCalidadRepository.deleteById(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        //planDeCalidadRepository.deleteById(id);
    }

    public PlanDeCalidad duplicadoPropio(int id){
        try {
            return planDeCalidadRepository.duplicadoPropio(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

        public PlanDeCalidad seleccionarPlanDeCalidadPorHerramientaXProyecto(int idHerramientaXProyecto){
            try {
                HerramientaXProyecto herramientaXProyecto = new HerramientaXProyecto();
                herramientaXProyecto = herramientaXProyectoRepository.findHerramientaXProyectoById(idHerramientaXProyecto);
                return planDeCalidadRepository.findPlanDeCalidadByHerramientaXProyecto(herramientaXProyecto);
            } catch (Exception e) {
                logger.error(e.getMessage());
                return null;
        }
    }    

}
