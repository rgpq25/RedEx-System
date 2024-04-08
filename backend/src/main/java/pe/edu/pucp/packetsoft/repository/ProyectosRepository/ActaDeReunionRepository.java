package pe.edu.pucp.packetsoft.repository.ProyectosRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.lettuce.core.dynamic.annotation.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import pe.edu.pucp.packetsoft.models.ActaDeReunion;
import pe.edu.pucp.packetsoft.models.Proyecto;

@Repository
@Transactional
public interface ActaDeReunionRepository extends JpaRepository<ActaDeReunion, Integer>{
    ActaDeReunion findActaDeReunionById(int id);
    //List<ActaDeReunion> findActaDeReunionByProyectoId(int proyectoId);
    List<ActaDeReunion> findActaDeReunionByProyecto(Proyecto proyecto);
}