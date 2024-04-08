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
import pe.edu.pucp.packetsoft.models.Herramienta;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.CategoriaHerramientaRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.HerramientaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class HerramientaService {
    @Autowired
    private HerramientaRepository herramientaRepository;
    private static final Logger logger = LoggerFactory.getLogger(HerramientaService.class);
    @Autowired
    private CategoriaHerramientaRepository categoriaHerramientaRepository;

    @PersistenceContext
    EntityManager entityManager;
    //CRUDS

    //create
    
    public Herramienta register(Herramienta herramienta){
        try {
            return herramientaRepository.save(herramienta);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        //return herramientaRepository.save(herramienta);
    }

    //read
    
    public List<Herramienta> getAll(){
        try {
            return herramientaRepository.findAll();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        //return herramientaRepository.findAll();
    }

    
    public Herramienta get(int id){
        try {
            return herramientaRepository.findHerramientaById(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        //return herramientaRepository.findHerramientaById(id);
    }

    //update
    
    public Herramienta update(Herramienta herramienta){
        try {
            return herramientaRepository.save(herramienta);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        //return herramientaRepository.save(herramienta);
    }

    //delete
    
    public void delete(int id){
        try {
            herramientaRepository.deleteById(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        //herramientaRepository.deleteById(id);
    }
    
    public List<Herramienta> listarHerramientasPorCategoria(int idCategoria){
        try {
            CategoriaHerramienta categoriaHerramienta = new CategoriaHerramienta();
            categoriaHerramienta = categoriaHerramientaRepository.findCategoriaHerramientaById(idCategoria);

            return herramientaRepository.findHerramientaByCategoriaHerramienta(categoriaHerramienta);
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public int EncontrarIdxHerramienta(String nombre){
        Herramienta herramienta = new Herramienta();

        try {
            herramienta= herramientaRepository.EncontrarIdxHerramienta(nombre);
            return herramienta.getId();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return -1;
        }
    }
}
