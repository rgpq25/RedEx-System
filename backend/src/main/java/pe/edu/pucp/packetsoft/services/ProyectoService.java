package pe.edu.pucp.packetsoft.services;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.lettuce.core.dynamic.annotation.Param;
import pe.edu.pucp.packetsoft.models.GrupoDeProyectos;
import pe.edu.pucp.packetsoft.models.Proyecto;
import pe.edu.pucp.packetsoft.models.PersonasModel.Persona;
import pe.edu.pucp.packetsoft.models.PersonasModel.Usuario;
import pe.edu.pucp.packetsoft.repository.PersonasRepository.PersonaRepository;
import pe.edu.pucp.packetsoft.repository.PersonasRepository.UsuarioRepository;
import pe.edu.pucp.packetsoft.repository.ProyectosRepository.GrupoDeProyectosRepository;
import pe.edu.pucp.packetsoft.repository.ProyectosRepository.ProyectoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service//Esta anotación marca la clase UsuarioService como un componente de servicio de Spring. Los componentes de servicio en Spring se utilizan para encapsular la lógica de negocio y proporcionar métodos para interactuar con los datos o realizar operaciones específicas.
public class ProyectoService {
    private static final Logger logger = LoggerFactory.getLogger(ProyectoService.class);
    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private GrupoDeProyectosRepository grupoDeProyectosRepository;
    
    @PersistenceContext
    EntityManager entityManager;
    //CRUDS

    //create

    public Proyecto register(Proyecto proyecto){
        try {
            return proyectoRepository.save(proyecto);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        //return proyectoRepository.save(proyecto);
    }

    //read

    public List<Proyecto> getAll(){
        try {
            return proyectoRepository.findAll();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        //return proyectoRepository.findAll();
    }


    public Proyecto get(int id){
        try {
            return proyectoRepository.findProyectoById(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        //return proyectoRepository.findProyectoById(id);
    }

    public List<Proyecto> listarProyectosPorNombreYJefeDeProyecto(String nombre,int idJefe){
        try {
            return proyectoRepository.findProyectosByNombreAndJefeDeProyecto(nombre,idJefe);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    //update

    public Proyecto update(Proyecto proyecto){
        try {
            return proyectoRepository.save(proyecto);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        //return proyectoRepository.save(proyecto);
    }

    //delete

    public void delete(int id){
        try {
            proyectoRepository.deleteById(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        //proyectoRepository.deleteById(id);
    }

    public List<Proyecto> listarProyectosPorJefeDeProyecto(int idJefeDeProyecto){
        try {
            Persona jefeDeProyecto = new Persona();
            jefeDeProyecto = personaRepository.findPersonaById(idJefeDeProyecto);

            return proyectoRepository.findProyectosByJefeDeProyecto(jefeDeProyecto);

        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        //return proyectoRepository.listarProyectosPorJefeDeProyecto(idJefeDeProyecto);
    }
    
    public List<Proyecto> listarProyectosPorJefeDeProyectoOrdenado(int jefeProyectoId){
        try {
            return proyectoRepository.findProyectobyJefeProyectoOrderDate(jefeProyectoId); 
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public List<Proyecto> listarProyectosPorGrupoDeProyectos(int idGrupoDeProyectos){
        try {
            GrupoDeProyectos grupoDeProyectos = new GrupoDeProyectos();
            grupoDeProyectos = grupoDeProyectosRepository.findGrupoDeProyectosById(idGrupoDeProyectos);

            return proyectoRepository.findProyectosByGrupoDeProyectos(grupoDeProyectos);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    
}