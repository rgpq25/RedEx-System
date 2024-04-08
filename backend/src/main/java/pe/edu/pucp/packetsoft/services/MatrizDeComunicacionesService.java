package pe.edu.pucp.packetsoft.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.HerramientaXProyectoRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.MatrizDeComunicacionesRepository;
import pe.edu.pucp.packetsoft.models.HerramientaXProyecto;
import pe.edu.pucp.packetsoft.models.MatrizDeComunicaciones;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MatrizDeComunicacionesService {
    
    @Autowired
    private MatrizDeComunicacionesRepository matrizDeComunicacionesRepository;

    @Autowired
    private HerramientaXProyectoRepository herramientaXProyectoRepository;

    // @Autowired
    // private EDTRepository edtRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(MatrizDeComunicacionesService.class);

    @PersistenceContext
    EntityManager entityManager;

    public MatrizDeComunicaciones register(MatrizDeComunicaciones matrizDeComunicaciones){
        try {
            return matrizDeComunicacionesRepository.save(matrizDeComunicaciones);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public List<MatrizDeComunicaciones> getAll(){
        try {
            return matrizDeComunicacionesRepository.findAll();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public MatrizDeComunicaciones get(int id){
        try {
            return matrizDeComunicacionesRepository.findMatrizDeComunicacionesById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public MatrizDeComunicaciones update(MatrizDeComunicaciones matrizDeComunicaciones){
        try {
            return matrizDeComunicacionesRepository.save(matrizDeComunicaciones);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public void delete(int id){
        try {
            matrizDeComunicacionesRepository.deleteById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public MatrizDeComunicaciones duplicadoPropio(int id){
        try {
            return matrizDeComunicacionesRepository.findMatrizDeComunicacionesById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public MatrizDeComunicaciones seleccionarMatrizDeComunicacionPorHerramientaXProyecto(int idHerramientaXProyecto){
        try{
            HerramientaXProyecto herramientaXProyecto = new HerramientaXProyecto();
            herramientaXProyecto = herramientaXProyectoRepository.findHerramientaXProyectoById(idHerramientaXProyecto);
            return matrizDeComunicacionesRepository.findMatrizDeComunicacionesByHerramientaXProyecto(herramientaXProyecto);
        } catch (Exception ex){
            LOGGER.error(ex.getMessage());
            return null;
        }
    }    
}
