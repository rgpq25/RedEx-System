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
public interface ProyectoRepository extends JpaRepository<Proyecto, Integer>{
    
    public List<Proyecto> findAll();
    public Proyecto findProyectoById(int id);

    @Query("SELECT p FROM Proyecto p WHERE p.nombre LIKE CONCAT('%', :nombre, '%') AND p.jefeDeProyecto.id = :jefeProyectoId ORDER BY p.fecha_modificacion DESC")
    public List<Proyecto> findProyectosByNombreAndJefeDeProyecto(String nombre, int jefeProyectoId);

    public List<Proyecto> findProyectosByJefeDeProyecto(Persona jefeDeProyecto);

    @Query("SELECT p FROM Proyecto p WHERE p.jefeDeProyecto.id = ?1 ORDER BY p.fecha_modificacion DESC")
    public List<Proyecto> findProyectobyJefeProyectoOrderDate(int jefeProyectoId);
    
    public List<Proyecto> findProyectosByGrupoDeProyectos(GrupoDeProyectos grupoDeProyectos);
    
}