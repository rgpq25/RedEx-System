package pe.edu.pucp.packetsoft.services.PersonasService;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
//import org.springframework.web.bind.annotation.PathVariable;

import pe.edu.pucp.packetsoft.models.Valor;
import pe.edu.pucp.packetsoft.models.PersonasModel.ValorPersona;
import pe.edu.pucp.packetsoft.repository.AtributoValor.ValorRepository;
import pe.edu.pucp.packetsoft.repository.PersonasRepository.ValorPersonaRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ValorPersonaService {
    @Autowired 
    private ValorPersonaRepository valorPersonaRepository;

    @Autowired
    private ValorRepository valorRepository;

    private static final Logger logger = LoggerFactory.getLogger(ValorPersonaService.class);
    @PersistenceContext
    EntityManager entityManager;

    //CRUDS

    //create
    
    public ValorPersona register(ValorPersona data){
        try {
            return valorPersonaRepository.save(data);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        // ValorPersona resultado = valorPersonaRepository.save(data);
        // if(resultado == null){
        //     return null;
        // }
        // return resultado;
    }

    //read
   
    public List<ValorPersona> getAll(){
        try {
            return valorPersonaRepository.findAll();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        //return valorPersonaRepository.findAll();
    }

    
    public ValorPersona get(int id){
        try {
            return valorPersonaRepository.findValorPersonaById(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        //return valorPersonaRepository.findValorPersonaById(id);
    }

    //update
    
    public ValorPersona update(ValorPersona data){
        try {
            return valorPersonaRepository.save(data);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        //return valorPersonaRepository.save(data);
    }

    //delete
    
    public void delete(int id){
        try {
            valorPersonaRepository.deleteById(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        //valorPersonaRepository.deleteById(id);
    }

    public List<ValorPersona> listarValorPersonaPorValor(int idValor){
        try {
            Valor valor = new Valor();
            valor = valorRepository.findValorById(idValor);
            return valorPersonaRepository.findValorPersonaByValor(valor);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        //return valorPersonaRepository.getValorPersonaByValor(idValor);
    }
}
