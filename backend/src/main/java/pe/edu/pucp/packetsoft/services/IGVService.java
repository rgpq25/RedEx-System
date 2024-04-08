package pe.edu.pucp.packetsoft.services;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.IGVRepository;
import pe.edu.pucp.packetsoft.models.IGV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.hibernate.annotations.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class IGVService {
    
    @Autowired
    private IGVRepository igvRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(IGVService.class);

    @PersistenceContext
    EntityManager entityManager;

    @CacheEvict(value = "igv", allEntries = true,beforeInvocation = true)
    @CachePut(value = "igv", key = "#result.id", condition = "#result != null")
    public IGV register(IGV igv){
        try {
            return igvRepository.save(igv);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    @Cacheable(value = "igv")
    public List<IGV> getAll(){
        try {
            return igvRepository.findAll();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    @Cacheable(value = "igv", key = "#id")
    public IGV get(int id){
        try {
            return igvRepository.findIGVById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    @CacheEvict(value = "igv", allEntries = true)
    @CachePut(value = "igv", key = "#igv.id", condition = "#igv != null")
    public IGV update(IGV igv){
        try {
            return igvRepository.save(igv);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    @CacheEvict(value = "igv", allEntries = true, beforeInvocation = true)
    public void delete(int id){
        try {
            igvRepository.deleteById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public IGV duplicadoPropio(int id){
        try {
            return igvRepository.findIGVById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }
}
