package pe.edu.pucp.packetsoft.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.CatalogoDeInteresadosRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.HerramientaXProyectoRepository;

import pe.edu.pucp.packetsoft.models.CatalogoDeInteresados;
import pe.edu.pucp.packetsoft.models.HerramientaXProyecto;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CatalogoDeInteresadosService {

    @Autowired
    private CatalogoDeInteresadosRepository catalogoDeInteresadosRepository;

    @Autowired
    private HerramientaXProyectoRepository herramientaXProyectoRepository;

    // @Autowired
    // private EDTRepository edtRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(CatalogoDeInteresadosService.class);

    @PersistenceContext
    EntityManager entityManager;

    public CatalogoDeInteresados register(CatalogoDeInteresados catalogoDeInteresados){
        try {
            return catalogoDeInteresadosRepository.save(catalogoDeInteresados);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public List<CatalogoDeInteresados> getAll(){
        try {
            return catalogoDeInteresadosRepository.findAll();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public CatalogoDeInteresados get(int id){
        try {
            return catalogoDeInteresadosRepository.findCatalogoDeInteresadosById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public CatalogoDeInteresados update(CatalogoDeInteresados catalogoDeInteresados){
        try {
            return catalogoDeInteresadosRepository.save(catalogoDeInteresados);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public void delete(int id){
        try {
            catalogoDeInteresadosRepository.deleteById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public CatalogoDeInteresados duplicadoPropio(int idIngresado){
        try {
            return catalogoDeInteresadosRepository.duplicadoPropio(idIngresado);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public CatalogoDeInteresados seleccionarCatalogoDeInteresadosByHerramientaXProyecto(int idHerramientaXProyecto){
        try{
            HerramientaXProyecto herramientaXProyecto = new HerramientaXProyecto();
            herramientaXProyecto = herramientaXProyectoRepository.findHerramientaXProyectoById(idHerramientaXProyecto);
            return catalogoDeInteresadosRepository.findCatalogoDeInteresadosByHerramientaXProyecto(herramientaXProyecto);
        } catch (Exception ex){
            LOGGER.error(ex.getMessage());
            return null;
        }
    }
    
}
