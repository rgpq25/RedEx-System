package pe.edu.pucp.packetsoft.repository.ProyectosRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.pucp.packetsoft.models.Actividad;
import java.util.List;

@Repository
@Transactional
public interface ActividadRepository extends JpaRepository<Actividad, Integer>{
    Actividad findActividadById(int id);

    @Query(value = "SELECT * FROM actividad WHERE id_proyecto = :proyectoId ORDER BY fecha_creacion DESC LIMIT 20", nativeQuery = true)
    List<Actividad> findTop20ByProyectoIdOrderByFecha_creacionDesc(int proyectoId);
    
}
