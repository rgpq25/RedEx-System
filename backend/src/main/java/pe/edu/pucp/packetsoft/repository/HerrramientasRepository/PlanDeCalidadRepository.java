package pe.edu.pucp.packetsoft.repository.HerrramientasRepository;


import org.hibernate.metamodel.model.convert.spi.JpaAttributeConverter;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.lettuce.core.dynamic.annotation.Param;
import pe.edu.pucp.packetsoft.models.HerramientaXProyecto;
import pe.edu.pucp.packetsoft.models.PlanDeCalidad;

import java.util.List;

@Repository
@Transactional
public interface PlanDeCalidadRepository extends JpaRepository<PlanDeCalidad, Integer>{
    public List<PlanDeCalidad> findAll();
    public PlanDeCalidad findPlanDeCalidadById(int id);

    @Query("FROM PlanDeCalidad p WHERE p.id = :idIngresado")
    public PlanDeCalidad duplicadoPropio(int idIngresado);

    //private HerramientaXProyecto herramientaXProyecto;
    public PlanDeCalidad findPlanDeCalidadByHerramientaXProyecto(HerramientaXProyecto herramientaXProyecto);

}
