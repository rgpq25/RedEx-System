package pe.edu.pucp.packetsoft.repository.ProyectosRepository;

import org.springframework.cache.annotation.CachePut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.query.Procedure; 
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.lettuce.core.dynamic.annotation.Param;
import pe.edu.pucp.packetsoft.models.PersonasModel.Persona;
import pe.edu.pucp.packetsoft.models.GrupoDeProyectos;
import pe.edu.pucp.packetsoft.models.Proyecto;

import java.util.List;

@Repository
@Transactional
public interface GrupoDeProyectosRepository extends JpaRepository<GrupoDeProyectos,Integer>{
    
    public List<GrupoDeProyectos> findAll();
    public GrupoDeProyectos findGrupoDeProyectosById(int id);
    public List<GrupoDeProyectos> findGrupoDeProyectosByPersona(Persona persona);
}
