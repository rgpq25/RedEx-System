package pe.edu.pucp.packetsoft.services;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.lettuce.core.dynamic.annotation.Param;
import pe.edu.pucp.packetsoft.models.Proyecto;
import pe.edu.pucp.packetsoft.models.ProyectoPersona;
import pe.edu.pucp.packetsoft.models.PersonasModel.Persona;
import pe.edu.pucp.packetsoft.repository.PersonasRepository.PersonaRepository;
import pe.edu.pucp.packetsoft.repository.ProyectosRepository.ProyectoPersonaRepository;
import pe.edu.pucp.packetsoft.repository.ProyectosRepository.ProyectoRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class ProyectoPersonaService {
    @Autowired
    private ProyectoPersonaRepository proyectoPersonaRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private PersonaRepository personaRepository;

    private static final Logger logger = LoggerFactory.getLogger(ProyectoPersonaService.class);
    @PersistenceContext
    EntityManager entityManager;

    
    public ProyectoPersona register(ProyectoPersona proyectoPersona){
        try {
            return proyectoPersonaRepository.save(proyectoPersona);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        //return proyectoPersonaRepository.save(proyectoPersona);
    }

    
    public List<ProyectoPersona> getAll(){
        try {
            return proyectoPersonaRepository.findAll();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        //return proyectoPersonaRepository.findAll();
    }

    
    public ProyectoPersona get(int id){
        try {
            return proyectoPersonaRepository.findProyectoPersonaById(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        //return proyectoPersonaRepository.findProyectoPersonaById(id);
    }

    
    public ProyectoPersona update(ProyectoPersona proyectoPersona){
        try {
            return proyectoPersonaRepository.save(proyectoPersona);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        //return proyectoPersonaRepository.save(proyectoPersona);
    }

    
    public void delete(int id){
        try {
            proyectoPersonaRepository.deleteById(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        //proyectoPersonaRepository.deleteById(id);
    }

    public List<ProyectoPersona> findProyectoPersonaByProyecto(int idProyecto){
        try {
            Proyecto proyecto = new Proyecto();
            proyecto = proyectoRepository.findProyectoById(idProyecto);
            return proyectoPersonaRepository.findProyectoPersonaByProyecto(proyecto);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public List<ProyectoPersona> encontrarParticipantesProyectoPersonaByProyecto(int idProyecto){
        try {
            Proyecto proyecto = new Proyecto();
            proyecto = proyectoRepository.findProyectoById(idProyecto);
            List<ProyectoPersona> listaProyectoPersona = proyectoPersonaRepository.findProyectoPersonaByProyecto(proyecto);
            if(listaProyectoPersona == null ) return null;
            List<ProyectoPersona> listaRes = new ArrayList<>(); 
            for (ProyectoPersona proyectoPersona : listaProyectoPersona) {
                if(proyectoPersona.getRolProyecto().equals("Participante")){
                    listaRes.add(proyectoPersona);
                }
            }
            return listaRes;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public List<ProyectoPersona> findProyectoPersonaByPersona(int idPersona){
        try {
            Persona persona = new Persona();
            persona = personaRepository.findPersonaById(idPersona);
            return proyectoPersonaRepository.findProyectoPersonaByPersona(persona);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
    
}
