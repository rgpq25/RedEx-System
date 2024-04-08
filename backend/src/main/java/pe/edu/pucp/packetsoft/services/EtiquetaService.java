package pe.edu.pucp.packetsoft.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
//import org.springframework.web.bind.annotation.PathVariable;

import pe.edu.pucp.packetsoft.models.Etiqueta;
import pe.edu.pucp.packetsoft.repository.AtributoValor.EtiquetaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EtiquetaService {
    private static final Logger logger = LoggerFactory.getLogger(EtiquetaService.class);
    @Autowired 
    private EtiquetaRepository etiquetaRepository;

    @PersistenceContext
    EntityManager entityManager;

    //CRUDS

    //create
    
    public Etiqueta register(Etiqueta etiqueta){
        try {
            return etiquetaRepository.save(etiqueta);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        // Etiqueta resultado = etiquetaRepository.save(etiqueta);
        // if(resultado == null){
        //     return null;
        // }
        // return resultado;
    }

    //read
    
    public List<Etiqueta> getAll(){
        try {
            return etiquetaRepository.findAll();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        //return etiquetaRepository.findAll();
    }

   
    public Etiqueta get(int id){
        try {
            return etiquetaRepository.findTagById(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        //return etiquetaRepository.findTagById(id);
    }

    //update
    
    public Etiqueta update(Etiqueta tabla){
        try {
            return etiquetaRepository.save(tabla);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        //return etiquetaRepository.save(tabla);
    }

    //delete
    
    public void delete(int id){
        try {
            etiquetaRepository.deleteById(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        //etiquetaRepository.deleteById(id);
    }
}
