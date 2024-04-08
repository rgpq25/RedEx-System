package pe.edu.pucp.packetsoft.repository.HerrramientasRepository;

import org.springframework.cache.annotation.CachePut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import io.lettuce.core.dynamic.annotation.Param;
import pe.edu.pucp.packetsoft.models.ActaDeConstitucion;
import pe.edu.pucp.packetsoft.models.HerramientaXProyecto;
import pe.edu.pucp.packetsoft.models.Tabla;

import java.util.List;

@Repository
@Transactional
public interface ActaDeConstitucionRepository extends JpaRepository<ActaDeConstitucion, Integer>{
    public List<ActaDeConstitucion> findAll();
    public ActaDeConstitucion findActaDeConstitucionById(int id);
    
    @Query("FROM ActaDeConstitucion a WHERE a.id = :idIngresado")
    public ActaDeConstitucion duplicadoPropio(int idIngresado);

    //private HerraientaXProyecto herramientaXProyecto;
    public ActaDeConstitucion findActaDeConstitucionByHerramientaXProyecto(HerramientaXProyecto herramientaXProyecto);
}
