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

import pe.edu.pucp.packetsoft.models.CategoriaHerramienta;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.CategoriaHerramientaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class CategoriaHerramientaService {
    @Autowired
    private CategoriaHerramientaRepository categoriaHerramientaRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(CategoriaHerramientaService.class);
    @PersistenceContext
    EntityManager entityManager;
    //CRUDS

    //create
    
    public CategoriaHerramienta register(CategoriaHerramienta categoriaHerramienta){
        try {
            return categoriaHerramientaRepository.save(categoriaHerramienta);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return categoriaHerramientaRepository.save(categoriaHerramienta);
    }

    //read
    
    public List<CategoriaHerramienta> getAll(){
        try {
            return categoriaHerramientaRepository.findAll();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return categoriaHerramientaRepository.findAll();
    }

    
    public CategoriaHerramienta get(int id){
        try {
            return categoriaHerramientaRepository.findCategoriaHerramientaById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return categoriaHerramientaRepository.findCategoriaHerramientaById(id);
    }

    //update
    
    public CategoriaHerramienta update(CategoriaHerramienta categoriaHerramienta){
        try {
            return categoriaHerramientaRepository.save(categoriaHerramienta);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return categoriaHerramientaRepository.save(categoriaHerramienta);
    }

    //delete
    
    public void delete(int id){
        try {
            categoriaHerramientaRepository.deleteById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        // categoriaHerramientaRepository.deleteById(id);
    }
    
}
