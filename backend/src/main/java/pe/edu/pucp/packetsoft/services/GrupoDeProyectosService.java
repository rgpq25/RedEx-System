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
@Service
public class GrupoDeProyectosService {
    private static final Logger logger = LoggerFactory.getLogger(ProyectoService.class);

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private GrupoDeProyectosRepository grupoDeProyectosRepository;

    @Autowired
    private PersonaRepository personaRepository;

    //create

    public GrupoDeProyectos register(GrupoDeProyectos grupoDeProyectos){
        try {
            return grupoDeProyectosRepository.save(grupoDeProyectos);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    //read

    public List<GrupoDeProyectos> getAll(){
        try {
            return grupoDeProyectosRepository.findAll();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public GrupoDeProyectos get(int id){
        try {
            return grupoDeProyectosRepository.findGrupoDeProyectosById(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public GrupoDeProyectos update(GrupoDeProyectos grupoDeProyectos){
        try {
            return grupoDeProyectosRepository.save(grupoDeProyectos);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    //delete

    public void delete(int id){
        try {
            grupoDeProyectosRepository.deleteById(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public List<GrupoDeProyectos> listarGruposDeProyectosPorPersona(int idPersona){
        try {
            Persona persona = new Persona();
            persona = personaRepository.findPersonaById(idPersona);
            return grupoDeProyectosRepository.findGrupoDeProyectosByPersona(persona);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
