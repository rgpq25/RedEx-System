package pe.edu.pucp.packetsoft.repository.ProyectosRepository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import pe.edu.pucp.packetsoft.models.Proyecto;
import pe.edu.pucp.packetsoft.models.Retrospectiva;

@Repository
@Transactional
public interface RetrospectivaRepository extends JpaRepository<Retrospectiva, Integer>{
    Retrospectiva findRetrospectivaById(int id);
    
    // @Query("FROM retrospectiva r WHERE r.id = :idIngresado")
    // public Retrospectiva duplicadoPropio(int idIngresado);

    //@Query(value="FROM Retrospectiva WHERE id_proyecto = :proyectoId")
    //List<Retrospectiva> findRestrospectivasByProyectoId(int proyectoId);
    List<Retrospectiva> findRestrospectivasByProyecto(Proyecto proyecto);
}
