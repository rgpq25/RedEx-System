package pe.edu.pucp.packetsoft.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.HerramientaXProyectoRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.MatrizDeResponsabilidadesRepository;
import pe.edu.pucp.packetsoft.models.HerramientaXProyecto;
import pe.edu.pucp.packetsoft.models.MatrizDeResponsabilidades;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MatrizDeResponsabilidadesService {
    @Autowired
    private MatrizDeResponsabilidadesRepository matrizDeResponsabilidadesRepository;

    @Autowired
    private HerramientaXProyectoRepository herramientaXProyectoRepository;

    // @Autowired
    // private EDTRepository edtRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(MatrizDeResponsabilidadesService.class);

    @PersistenceContext
    EntityManager entityManager;

    public MatrizDeResponsabilidades register(MatrizDeResponsabilidades matrizDeResponsabilidades){
        try {
            return matrizDeResponsabilidadesRepository.save(matrizDeResponsabilidades);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public List<MatrizDeResponsabilidades> getAll(){
        try {
            return matrizDeResponsabilidadesRepository.findAll();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public MatrizDeResponsabilidades get(int id){
        try {
            return matrizDeResponsabilidadesRepository.findMatrizDeResponsabilidadesById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public MatrizDeResponsabilidades update(MatrizDeResponsabilidades matrizDeResponsabilidades){
        try {
            return matrizDeResponsabilidadesRepository.save(matrizDeResponsabilidades);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public void delete(int id){
        try {
            matrizDeResponsabilidadesRepository.deleteById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public MatrizDeResponsabilidades duplicadoPropio(int id){
        try {
            return matrizDeResponsabilidadesRepository.findMatrizDeResponsabilidadesById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public MatrizDeResponsabilidades seleccionarMatrizDeResponsabilidadPorHerramientaXProyecto(int idHerramientaXProyecto){
        try{
            HerramientaXProyecto herramientaXProyecto = new HerramientaXProyecto();
            herramientaXProyecto = herramientaXProyectoRepository.findHerramientaXProyectoById(idHerramientaXProyecto);
            return matrizDeResponsabilidadesRepository.findMatrizDeResponsabilidadesByHerramientaXProyecto(herramientaXProyecto);
        } catch (Exception ex){
            LOGGER.error(ex.getMessage());
            return null;
        }
    }
}
