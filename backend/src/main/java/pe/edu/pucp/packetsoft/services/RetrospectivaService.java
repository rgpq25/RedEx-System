package pe.edu.pucp.packetsoft.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.edu.pucp.packetsoft.models.Proyecto;
import pe.edu.pucp.packetsoft.models.Retrospectiva;
import pe.edu.pucp.packetsoft.repository.ProyectosRepository.ProyectoRepository;
import pe.edu.pucp.packetsoft.repository.ProyectosRepository.RetrospectivaRepository;

@Service
public class RetrospectivaService {
    @Autowired
    private RetrospectivaRepository retrospectivaRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(RetrospectivaService.class);

    public Retrospectiva register(Retrospectiva retrospectiva){
        try {
            return retrospectivaRepository.save(retrospectiva);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public List <Retrospectiva> getAll(){
        try {
            return retrospectivaRepository.findAll();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public Retrospectiva get(int id){
        try {
            return retrospectivaRepository.findRetrospectivaById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public void delete(int id){
        try {
            retrospectivaRepository.deleteById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public Retrospectiva update(Retrospectiva retrospectiva){
        try {
            return retrospectivaRepository.save(retrospectiva);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public List <Retrospectiva> getByProyecto(int id_proyecto){
        try {
            Proyecto proyecto = new Proyecto();
            proyecto = proyectoRepository.findProyectoById(id_proyecto);
            return retrospectivaRepository.findRestrospectivasByProyecto(proyecto);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }    
}
