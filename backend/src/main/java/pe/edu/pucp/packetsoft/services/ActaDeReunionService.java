package pe.edu.pucp.packetsoft.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.edu.pucp.packetsoft.models.ActaDeReunion;
import pe.edu.pucp.packetsoft.models.Proyecto;
import pe.edu.pucp.packetsoft.repository.ProyectosRepository.ActaDeReunionRepository;
import pe.edu.pucp.packetsoft.repository.ProyectosRepository.ProyectoRepository;

@Service
public class ActaDeReunionService {
    @Autowired
    private ActaDeReunionRepository actaDeReunionRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ActaDeReunionService.class);

    public ActaDeReunion register(ActaDeReunion actaDeReunion){
        try {
            return actaDeReunionRepository.save(actaDeReunion);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public List <ActaDeReunion> getAll(){
        try {
            return actaDeReunionRepository.findAll();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public ActaDeReunion get(int id){
        try {
            return actaDeReunionRepository.findActaDeReunionById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public void delete(int id){
        try {
            actaDeReunionRepository.deleteById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public ActaDeReunion update(ActaDeReunion actaDeReunion){
        try {
            return actaDeReunionRepository.save(actaDeReunion);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public List <ActaDeReunion> getByProyecto(int id){
        try {
            Proyecto proyecto = new Proyecto();
            proyecto = proyectoRepository.findProyectoById(id);
            return actaDeReunionRepository.findActaDeReunionByProyecto(proyecto);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

}