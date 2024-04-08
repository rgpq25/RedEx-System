package pe.edu.pucp.packetsoft.services;


import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import pe.edu.pucp.packetsoft.models.Herramienta;
import pe.edu.pucp.packetsoft.models.HerramientaXProyecto;
import pe.edu.pucp.packetsoft.models.Plantilla;
import pe.edu.pucp.packetsoft.models.Proyecto;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.HerramientaRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.HerramientaXProyectoRepository;
import pe.edu.pucp.packetsoft.repository.ProyectosRepository.ProyectoRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class HerramientaXProyectoService {
    
    @Autowired
    private HerramientaXProyectoRepository herramientaXProyectoRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private HerramientaRepository herramientaRepository;

    @Autowired
    private PlantillaService plantillaService;

    private static final Logger logger = LoggerFactory.getLogger(HerramientaXProyectoService.class);
    @PersistenceContext
    EntityManager entityManager;

    
    public HerramientaXProyecto register(HerramientaXProyecto herramientaXProyecto){
        try {
            HerramientaXProyecto herramientaXProyecto2 = herramientaXProyectoRepository.save(herramientaXProyecto);

            List<Plantilla> plantilla = null;
            plantilla = plantillaService.findPlantillaByHerramienta(herramientaXProyecto2.getHerramienta().getId());

            if(plantilla != null){
                plantillaService.duplicarPlantilla(plantilla.get(0).getId(), herramientaXProyecto2.getId());
            }
            
            return herramientaXProyecto2;
        } catch (Exception e) {
            logger.error(e.getMessage() + "\nError con la herramientaXProyecto " + herramientaXProyecto.getId());
            return null;
        }
        //return herramientaXProyectoRepository.save(herramientaXProyecto);
    }

    //read
    
    public List<HerramientaXProyecto> getAll(){
        try {
            return herramientaXProyectoRepository.findAll();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        //return herramientaXProyectoRepository.findAll();
    }

    
    public HerramientaXProyecto get(int id){
        try {
            return herramientaXProyectoRepository.findHerramientaXProyectoById(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        //return herramientaXProyectoRepository.findHerramientaXProyectoById(id);
    }

    //update
    
    public HerramientaXProyecto update(HerramientaXProyecto herramientaXProyecto){
        try {
            return herramientaXProyectoRepository.save(herramientaXProyecto);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        //return herramientaXProyectoRepository.save(herramientaXProyecto);
    }

    //delete
    
    public void delete(int id){
        try {
            herramientaXProyectoRepository.deleteById(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        //herramientaXProyectoRepository.deleteById(id);
    }
    
    public List<HerramientaXProyecto> listarHerramientasXProyectoPorProyecto(int idProyecto){
        try {
            Proyecto proyecto = new Proyecto();
            proyecto = proyectoRepository.findProyectoById(idProyecto);
            return herramientaXProyectoRepository.findHerramientaXProyectoByProyecto(proyecto);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public List<HerramientaXProyecto> listarHerramientasXProyectoPorHerramienta(int idHerramienta){
        try {
            Herramienta herramienta = new Herramienta();
            herramienta = herramientaRepository.findHerramientaById(idHerramienta);
            return herramientaXProyectoRepository.findHerramientaXProyectoByHerramienta(herramienta);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
 

   


}
