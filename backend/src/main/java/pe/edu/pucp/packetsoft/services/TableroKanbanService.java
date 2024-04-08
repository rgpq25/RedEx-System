package pe.edu.pucp.packetsoft.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.HerramientaXProyectoRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.ProductBacklogRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.TableroKanbanRepository;
import pe.edu.pucp.packetsoft.repository.PersonasRepository.PersonaRepository;
import pe.edu.pucp.packetsoft.models.HerramientaXProyecto;
import pe.edu.pucp.packetsoft.models.ProductBacklog;
import pe.edu.pucp.packetsoft.models.TableroKanban;
import pe.edu.pucp.packetsoft.models.PersonasModel.Persona;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TableroKanbanService {
    
    @Autowired
    private TableroKanbanRepository tableroKanbanRepository;

    @Autowired
    private HerramientaXProyectoRepository herramientaXProyectoRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private ProductBacklogRepository productBacklogRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(TableroKanbanService.class);

    @PersistenceContext
    EntityManager entityManager;

    public TableroKanban register(TableroKanban tableroKanban){
        try {
            return tableroKanbanRepository.save(tableroKanban);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public List<TableroKanban> getAll(){
        try {
            return tableroKanbanRepository.findAll();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public TableroKanban get(int id){
        try {
            return tableroKanbanRepository.findTableroKanbanById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public TableroKanban update(TableroKanban tableroKanban){
        try {
            return tableroKanbanRepository.save(tableroKanban);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public void delete(int id){
        try {
            tableroKanbanRepository.deleteById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public TableroKanban duplicadoPropio(int id){
        try {
            return tableroKanbanRepository.findTableroKanbanById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public TableroKanban seleccionarTableroKanbanPorHerramientaXProyecto(int idHerramientaXProyecto){
        try{
            HerramientaXProyecto herramientaXProyecto = new HerramientaXProyecto();
            herramientaXProyecto = herramientaXProyectoRepository.findHerramientaXProyectoById(idHerramientaXProyecto);
            return tableroKanbanRepository.findTableroKanbanByHerramientaXProyecto(herramientaXProyecto);
        } catch (Exception ex){
            LOGGER.error(ex.getMessage());
            return null;
        }
    }

    public TableroKanban seleccionarTableroKanbanPorPersona(int idPersona){
        try{
            Persona persona = new Persona();
            persona = personaRepository.findPersonaById(idPersona);
            return tableroKanbanRepository.findTableroKanbanByPersona(persona);
        } catch (Exception ex){
            LOGGER.error(ex.getMessage());
            return null;
        }
    }

    public TableroKanban seleccionarTableroKanbanPorProductBacklog(int idProductBacklog){
        try{
            ProductBacklog productBacklog = new ProductBacklog();
            productBacklog = productBacklogRepository.findProductBacklogById(idProductBacklog);
            return tableroKanbanRepository.findTableroKanbanByProductBacklog(productBacklog);
        } catch (Exception ex){
            LOGGER.error(ex.getMessage());
            return null;
        }
    }
}
