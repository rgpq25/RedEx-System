package pe.edu.pucp.packetsoft.services;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.PartidaRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.PresupuestoRepository;
import pe.edu.pucp.packetsoft.models.Partida;
import pe.edu.pucp.packetsoft.models.Presupuesto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PartidaService {
    
    @Autowired
    private PartidaRepository partidaRepository;

    @Autowired
    private PresupuestoRepository presupuestoRepository;    


    private static final Logger LOGGER = LoggerFactory.getLogger(IGVService.class);

    @PersistenceContext
    EntityManager entityManager;
    
    @CacheEvict(value = "partida", allEntries = true,beforeInvocation = true)
    @CachePut(value = "partida", key = "#result.id", condition = "#result != null")
    public Partida register(Partida partida){
        try {
            return partidaRepository.save(partida);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    @Cacheable(value = "partida")
    public List<Partida> getAll(){
        try {
            return partidaRepository.findAll();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    @Cacheable(value = "partida", key = "#id")
    public Partida get(int id){
        try {
            return partidaRepository.findPartidaById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    @CacheEvict(value = "partida", allEntries = true)
    @CachePut(value = "partida", key = "#partida.id", condition = "#partida != null")
    public Partida update(Partida partida){
        try {
            return partidaRepository.save(partida);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    @CacheEvict(value = "partida", allEntries = true, beforeInvocation = true)
    public void delete(int id){
        try {
            partidaRepository.deleteById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public Partida duplicadoPropio(int id){
        try {
            return partidaRepository.findPartidaById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public List<Partida> listarPartidasPorIdPresupuesto(int idPresupuesto){
        try {
            return partidaRepository.findPartidaByPresupuestoId(idPresupuesto);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

}
