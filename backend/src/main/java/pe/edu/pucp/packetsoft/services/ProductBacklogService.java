package pe.edu.pucp.packetsoft.services;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


import pe.edu.pucp.packetsoft.models.EDT;
import pe.edu.pucp.packetsoft.models.HerramientaXProyecto;
import pe.edu.pucp.packetsoft.models.ProductBacklog;
import pe.edu.pucp.packetsoft.models.PersonasModel.Usuario;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.HerramientaXProyectoRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.ProductBacklogRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class ProductBacklogService {
    @Autowired
    private ProductBacklogRepository productBacklogRepository;

    @Autowired
    private HerramientaXProyectoRepository herramientaXProyectoRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductBacklogService.class);
    @PersistenceContext
    EntityManager entityManager;

    
    public ProductBacklog register(ProductBacklog productBacklog){
        try {
            return productBacklogRepository.save(productBacklog);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return productBacklogRepository.save(productBacklog);
    }

    
    public List<ProductBacklog> getAll(){
        try {
            return productBacklogRepository.findAll();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return productBacklogRepository.findAll();
    }

    
    public ProductBacklog get(int id){
        try {
            return productBacklogRepository.findProductBacklogById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return productBacklogRepository.findProductBacklogById(id);
    }

    
    public ProductBacklog update(ProductBacklog productBacklog){
        try {
            return productBacklogRepository.save(productBacklog);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return productBacklogRepository.save(productBacklog);
    }

    public ProductBacklog duplicadoPropio(int idIngresado){
        ProductBacklog productBacklog = null;
        try {
            return productBacklogRepository.duplicadoPropio(idIngresado);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return productBacklogRepository.duplicadoPropio(idIngresado);
    }

    
    public void delete(int id){
        try {
            productBacklogRepository.deleteById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        // productBacklogRepository.deleteById(id);
    }

    public ProductBacklog seleccionarProductBacklogPorHerramientaXProyecto(int idHerramientaXProyecto){
        try {
            HerramientaXProyecto herramientaXProyecto = new HerramientaXProyecto();
            herramientaXProyecto = herramientaXProyectoRepository.findHerramientaXProyectoById(idHerramientaXProyecto);
            return productBacklogRepository.findProudctBacklogByHerramientaXProyecto(herramientaXProyecto);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

}
