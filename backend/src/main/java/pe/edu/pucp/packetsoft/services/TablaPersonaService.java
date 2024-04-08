package pe.edu.pucp.packetsoft.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import pe.edu.pucp.packetsoft.models.TablaPersona;
import pe.edu.pucp.packetsoft.repository.AtributoValor.TablaPersonaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class TablaPersonaService {
    private static final Logger logger = LoggerFactory.getLogger(TablaPersonaService.class);
    @Autowired 
    private TablaPersonaRepository tPersonaRepository;

    @PersistenceContext
    EntityManager entityManager;

    //CRUDS

    //create
    
    public TablaPersona register(TablaPersona tPersona){
        try {
            return tPersonaRepository.save(tPersona);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        // TablaPersona resultado = tPersonaRepository.save(tPersona);
        // if(resultado == null){
        //     return null;
        // }
        // return resultado;
    }

    //read
    
    public List<TablaPersona> getAll(){
        try {
            return tPersonaRepository.findAll();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        //return tPersonaRepository.findAll();
    }

   
    public TablaPersona get(int id){
        try {
            return tPersonaRepository.findRegById(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        //return tPersonaRepository.findRegById(id);
    }

    //update
    
    public TablaPersona update(TablaPersona tabla){
        try {
            return tPersonaRepository.save(tabla);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        //return tPersonaRepository.save(tabla);
    }

    //delete
    
    public void delete(int id){
        try {
            tPersonaRepository.deleteById(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        //tPersonaRepository.deleteById(id);
    }  
}
