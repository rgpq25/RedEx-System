package pe.edu.pucp.packetsoft.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import pe.edu.pucp.packetsoft.models.Actividad;
import pe.edu.pucp.packetsoft.repository.ProyectosRepository.ActividadRepository;


@Service
public class ActividadService {
    @Autowired
    private ActividadRepository actividadRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(ActividadService.class);

    public Actividad register(Actividad actividad){
        try {
            return actividadRepository.save(actividad);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public List<Actividad> getAll(){
        try {
            return actividadRepository.findAll();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public Actividad get(int id){
        try {
            return actividadRepository.findActividadById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public void delete(int id){
        try {
            actividadRepository.deleteById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public Actividad update(Actividad actividad){
        try {
            return actividadRepository.save(actividad);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public List<Actividad> listarPorIdProyectoTop20(int idproyecto) {
        try {
            return actividadRepository.findTop20ByProyectoIdOrderByFecha_creacionDesc(idproyecto);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }
}
